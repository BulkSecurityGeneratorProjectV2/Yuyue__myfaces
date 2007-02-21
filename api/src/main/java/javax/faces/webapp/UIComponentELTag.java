/*
 * Copyright 2006 The Apache Software Foundation.
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

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.tagext.Tag;

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
 * @author Bruno Aranda (latest modification by $Author$)
 * @author Manfred Geiler
 * @version $Revision$ $Date$
 *
 * @since 1.2
 */
public abstract class UIComponentELTag extends UIComponentClassicTagBase
        implements Tag
{

    private ValueExpression _binding = null;
    private ValueExpression _rendered = null;

    public UIComponentELTag()
    {

    }

    public void release()
    {
        super.release();
        _binding = null;
        _rendered = null;
    }


    protected void setProperties(UIComponent component)
    {
        if (getRendererType() != null)
        {
            component.setRendererType(getRendererType());
        }

        if (_rendered != null)
        {
            if (_rendered.isLiteralText())
            {
                boolean b = Boolean.valueOf(_rendered.getExpressionString()).booleanValue();
                component.setRendered(b);
            }
            else
            {
                component.setValueExpression("rendered", _rendered);
            }
        }
    }

    protected UIComponent createComponent(FacesContext context, String newId) 
    {
        UIComponent component;
        Application application = context.getApplication();

        if (_binding != null)
        {
            component = application.createComponent(_binding, context, getComponentType());
            component.setValueExpression("binding", _binding);
        }
        else
        {
            component = application.createComponent(getComponentType());
        }

        component.setId(newId);
        setProperties(component);

        return component;
    }
    
    public void setBinding(ValueExpression binding)
    {
        _binding = binding;
    }
    
    protected boolean hasBinding()
    {
        return _binding != null;
    }
    
    public void setRendered(ValueExpression rendered)
    {
        _rendered = rendered;
    }
}
