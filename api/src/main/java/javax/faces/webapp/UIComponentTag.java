/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.faces.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ExternalContext;
import javax.faces.el.ValueBinding;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Base class for all JSP tags that represent a JSF UIComponent.
 * <p>
 * <i>Disclaimer</i>: The official definition for the behaviour of
 * this class is the JSF specification but for legal reasons the
 * specification cannot be replicated here. Any javadoc present on this
 * class therefore describes the current implementation rather than the
 * officially required behaviour, though it is believed that this class
 * does comply with the specification.
 * 
 * see Javadoc of <a href="http://java.sun.com/j2ee/javaserverfaces/1.2/docs/api/index.html">JSF Specification</a> for more.
 * 
 * @author Manfred Geiler (latest modification by $Author$)
 * @author Bruno Aranda
 * @version $Revision$ $Date$
 *
 * @deprecated replaced by {@link UIComponentELTag}
 */
public abstract class UIComponentTag extends UIComponentClassicTagBase
{

    //tag attributes
    private String _binding = null;
    private String _rendered = null;

    private Boolean _suppressed = null;
    private ResponseWriter _writer = null;

    public UIComponentTag()
    {

    }

    public void release()
    {
        super.release();

        _binding = null;
        _rendered = null;
    }

    /** Setter for common JSF xml attribute "binding". */
    public void setBinding(String binding)
            throws JspException
    {
        if (!isValueReference(binding))
        {
            throw new IllegalArgumentException("not a valid binding: " + binding);
        }
        _binding = binding;
    }


    /** Setter for common JSF xml attribute "rendered". */
    public void setRendered(String rendered)
    {
        _rendered = rendered;
    }


    /**
     * Return the nearest JSF tag that encloses this tag.
     * @deprecated
     */
    public static UIComponentTag getParentUIComponentTag(PageContext pageContext)
    {
        UIComponentClassicTagBase parentTag = getParentUIComponentClassicTagBase(pageContext);

        if (parentTag instanceof UIComponentTag)
        {
            return (UIComponentTag) parentTag;
        }
        else
        {
            return new UIComponentTagWrapper(parentTag);
        }
    }

    /**
     * Return true if the specified string contains an EL expression.
     * <p>
     * UIComponent properties are often required to be value-binding
     * expressions; this method allows code to check whether that is
     * the case or not.
     */
    public static boolean isValueReference(String value)
    {
        if (value == null) throw new NullPointerException("value");

        int start = value.indexOf("#{");
        if (start < 0) return false;

        int end = value.lastIndexOf('}');
        return (end >=0 && start < end);
    }

    /**
     * Create a UIComponent. Abstract method getComponentType is invoked to
     * determine the actual type name for the component to be created.
     *
     * If this tag has a "binding" attribute, then that is immediately
     * evaluated to store the created component in the specified property.
     */
    protected UIComponent createComponent(FacesContext context, String id) 
    {
        String componentType = getComponentType();
        if (componentType == null)
        {
            throw new NullPointerException("componentType");
        }

        if (_binding != null)
        {
            Application application = context.getApplication();
            ValueBinding componentBinding = application.createValueBinding(_binding);
            UIComponent component = application.createComponent(componentBinding,
                                                                context,
                                                                componentType);
            component.setId(id);
            component.setValueBinding("binding", componentBinding);

            return component;
        }
        else
        {
            UIComponent component = context.getApplication().createComponent(componentType);
            component.setId(id);

            return component;
        }
    }


    private boolean isFacet()
    {
        return getParent() != null && getParent() instanceof FacetTag;
    }

    /**
     * Determine whether this component renders itself. A component
     * is "suppressed" when it is either not rendered, or when it is
     * rendered by its parent component at a time of the parent's choosing.
     */
    protected boolean isSuppressed()
    {
        if (_suppressed == null)
        {
            // we haven't called this method before, so determine the suppressed
            // value and cache it for later calls to this method.

            if (isFacet())
            {
                // facets are always rendered by their parents --> suppressed
                _suppressed = Boolean.TRUE;
                return true;
            }

            UIComponent component = getComponentInstance();

            // Does any parent render its children?
            // (We must determine this first, before calling any isRendered method
            //  because rendered properties might reference a data var of a nesting UIData,
            //  which is not set at this time, and would cause a VariableResolver error!)
            UIComponent parent = component.getParent();
            while (parent != null)
            {
                if (parent.getRendersChildren())
                {
                    //Yes, parent found, that renders children --> suppressed
                    _suppressed = Boolean.TRUE;
                    return true;
                }
                parent = parent.getParent();
            }

            // does component or any parent has a false rendered attribute?
            while (component != null)
            {
                if (!component.isRendered())
                {
                    //Yes, component or any parent must not be rendered --> suppressed
                    _suppressed = Boolean.TRUE;
                    return true;
                }
                component = component.getParent();
            }

            // else --> not suppressed
            _suppressed = Boolean.FALSE;
        }
        return _suppressed.booleanValue();
    }

    protected void setProperties(UIComponent component)
    {
        if (getRendererType() != null)
        {
            getComponentInstance().setRendererType(getRendererType());
        }

        if (_rendered != null)
        {
            if (isValueReference(_rendered))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(_rendered);
                component.setValueBinding("rendered", vb);
            } else
            {
                boolean b = Boolean.valueOf(_rendered).booleanValue();
                component.setRendered(b);
            }
        }
    }

    /**
     * Class used to create an UIComponentTag from a UIComponentClassicTagBase
     */
    private static class UIComponentTagWrapper extends UIComponentTag
    {
        public UIComponentTagWrapper(UIComponentClassicTagBase classicTag)
        {
            setId(classicTag.getId());
            setJspId(classicTag.getJspId());
            setParent(classicTag.getParent());
        }

        public String getComponentType()
        {
            return null;
        }

        public String getRendererType()
        {
            return null;
        }

        protected UIComponent createComponent(FacesContext context, String newId)
        {
            return null;
        }


    }

    @Override
    protected boolean hasBinding()
    {
        return _binding != null;
    }

}
