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
package net.sourceforge.myfaces.custom.tree.taglib;

import net.sourceforge.myfaces.custom.tree.model.DefaultTreeModel;
import net.sourceforge.myfaces.custom.tree.model.TreeModel;
import net.sourceforge.myfaces.custom.tree.model.TreePath;
import net.sourceforge.myfaces.custom.tree.HtmlTree;
import net.sourceforge.myfaces.taglib.UIComponentTagBase;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.jsp.JspException;


/**
 * HtmlTree tag.
 * 
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 * @version $Revision$ $Date$
 *          $Log$
 *          Revision 1.5  2004/07/01 21:53:06  mwessendorf
 *          ASF switch
 *
 *          Revision 1.4  2004/05/10 01:24:51  o_rossmueller
 *          added iconClass attribute
 *
 *          Revision 1.3  2004/05/05 00:18:57  o_rossmueller
 *          various fixes/modifications in model event handling and tree update
 *
 *          Revision 1.2  2004/04/22 21:59:17  o_rossmueller
 *          added expandRoot attribute
 *
 *          Revision 1.1  2004/04/22 10:20:25  manolito
 *          tree component
 *
 */
public class TreeTag
        extends UIComponentTagBase
{

    private String value;
    private String iconLine;
    private String iconNoline;
    private String iconChild;
    private String iconChildFirst;
    private String iconChildMiddle;
    private String iconChildLast;
    private String iconNodeOpen;
    private String iconNodeOpenFirst;
    private String iconNodeOpenMiddle;
    private String iconNodeOpenLast;
    private String iconNodeClose;
    private String iconNodeCloseFirst;
    private String iconNodeCloseMiddle;
    private String iconNodeCloseLast;
    private String styleClass;
    private String nodeClass;
    private String selectedNodeClass;
    private String iconClass;
    private boolean expandRoot;


    public String getComponentType()
    {
        return "net.sourceforge.myfaces.HtmlTree";
    }


    public String getRendererType()
    {
        return "net.sourceforge.myfaces.HtmlTree";
    }


    public String getValue()
    {
        return value;
    }


    public void setValue(String newValue)
    {
        value = newValue;
    }


    public String getIconLine()
    {
        return iconLine;
    }


    public void setIconLine(String iconLine)
    {
        this.iconLine = iconLine;
    }


    public String getIconNoline()
    {
        return iconNoline;
    }


    public void setIconNoline(String iconNoline)
    {
        this.iconNoline = iconNoline;
    }


    public String getIconChild()
    {
        return iconChild;
    }


    public void setIconChild(String iconChild)
    {
        this.iconChild = iconChild;
    }


    public String getIconChildFirst()
    {
        return iconChildFirst;
    }


    public void setIconChildFirst(String iconChildFirst)
    {
        this.iconChildFirst = iconChildFirst;
    }


    public String getIconChildMiddle()
    {
        return iconChildMiddle;
    }


    public void setIconChildMiddle(String iconChildMiddle)
    {
        this.iconChildMiddle = iconChildMiddle;
    }


    public String getIconChildLast()
    {
        return iconChildLast;
    }


    public void setIconChildLast(String iconChildLast)
    {
        this.iconChildLast = iconChildLast;
    }


    public String getIconNodeOpen()
    {
        return iconNodeOpen;
    }


    public void setIconNodeOpen(String iconNodeOpen)
    {
        this.iconNodeOpen = iconNodeOpen;
    }


    public String getIconNodeOpenFirst()
    {
        return iconNodeOpenFirst;
    }


    public void setIconNodeOpenFirst(String iconNodeOpenFirst)
    {
        this.iconNodeOpenFirst = iconNodeOpenFirst;
    }


    public String getIconNodeOpenMiddle()
    {
        return iconNodeOpenMiddle;
    }


    public void setIconNodeOpenMiddle(String iconNodeOpenMiddle)
    {
        this.iconNodeOpenMiddle = iconNodeOpenMiddle;
    }


    public String getIconNodeOpenLast()
    {
        return iconNodeOpenLast;
    }


    public void setIconNodeOpenLast(String iconNodeOpenLast)
    {
        this.iconNodeOpenLast = iconNodeOpenLast;
    }


    public String getIconNodeClose()
    {
        return iconNodeClose;
    }


    public void setIconNodeClose(String iconNodeClose)
    {
        this.iconNodeClose = iconNodeClose;
    }


    public String getIconNodeCloseFirst()
    {
        return iconNodeCloseFirst;
    }


    public void setIconNodeCloseFirst(String iconNodeCloseFirst)
    {
        this.iconNodeCloseFirst = iconNodeCloseFirst;
    }


    public String getIconNodeCloseMiddle()
    {
        return iconNodeCloseMiddle;
    }


    public void setIconNodeCloseMiddle(String iconNodeCloseMiddle)
    {
        this.iconNodeCloseMiddle = iconNodeCloseMiddle;
    }


    public String getIconNodeCloseLast()
    {
        return iconNodeCloseLast;
    }


    public void setIconNodeCloseLast(String iconNodeCloseLast)
    {
        this.iconNodeCloseLast = iconNodeCloseLast;
    }


    public String getStyleClass()
    {
        return styleClass;
    }


    public void setStyleClass(String styleClass)
    {
        this.styleClass = styleClass;
    }


    public String getNodeClass()
    {
        return nodeClass;
    }


    public void setNodeClass(String nodeClass)
    {
        this.nodeClass = nodeClass;
    }


    public String getSelectedNodeClass()
    {
        return selectedNodeClass;
    }


    public void setSelectedNodeClass(String selectedNodeClass)
    {
        this.selectedNodeClass = selectedNodeClass;
    }


    public String getIconClass()
    {
        return iconClass;
    }


    public void setIconClass(String iconClass)
    {
        this.iconClass = iconClass;
    }


    public boolean isExpandRoot()
    {
        return expandRoot;
    }


    public void setExpandRoot(boolean expandRoot)
    {
        this.expandRoot = expandRoot;
    }


    /**
     * Obtain tree model or create a default model.
     */
    public int doStartTag() throws JspException
    {
        FacesContext context = FacesContext.getCurrentInstance();

        if (value != null)
        {
            ValueBinding valueBinding = context.getApplication().createValueBinding(value);
            TreeModel treeModel = (TreeModel)(valueBinding.getValue(context));

            if (treeModel == null)
            {
                // create default model
                treeModel = new DefaultTreeModel();
                valueBinding.setValue(context, treeModel);
            }
        }
        int answer = super.doStartTag();
        HtmlTree tree = (HtmlTree) getComponentInstance();

        if (getCreated() && expandRoot)
        {
            // component was created, so expand the root node
            TreeModel model = tree.getModel(context);

            if (model != null) {
                tree.expandPath(new TreePath(new Object[]{model.getRoot()}), context);
            }
        }

        tree.addToModelListeners();
        return answer;
    }


    /**
     * Applies attributes to the tree component
     */
    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        FacesContext context = FacesContext.getCurrentInstance();

        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding binding = context.getApplication().createValueBinding(value);
                component.setValueBinding("model", binding);
            }
        }
        else
        {
            ValueBinding binding = component.getValueBinding("model");
            if (binding == null)
            {
                binding = context.getApplication().createValueBinding("#{sessionScope.tree}");
            }
            component.setValueBinding("model", binding);
        }

        setStringProperty(component, "iconLine", iconLine);
        setStringProperty(component, "iconNoline", iconNoline);
        setStringProperty(component, "iconChild", iconChild);
        setStringProperty(component, "iconChildFirst", iconChildFirst);
        setStringProperty(component, "iconChildMiddle", iconChildMiddle);
        setStringProperty(component, "iconChildLast", iconChildLast);
        setStringProperty(component, "iconNodeOpen", iconNodeOpen);
        setStringProperty(component, "iconNodeOpenFirst", iconNodeOpenFirst);
        setStringProperty(component, "iconNodeOpenMiddle", iconNodeOpenMiddle);
        setStringProperty(component, "iconNodeOpenLast", iconNodeOpenLast);
        setStringProperty(component, "iconNodeClose", iconNodeClose);
        setStringProperty(component, "iconNodeCloseFirst", iconNodeCloseFirst);
        setStringProperty(component, "iconNodeCloseMiddle", iconNodeCloseMiddle);
        setStringProperty(component, "iconNodeCloseLast", iconNodeCloseLast);
        setStringProperty(component, "styleClass", styleClass);
        setStringProperty(component, "nodeClass", nodeClass);
        setStringProperty(component, "selectedNodeClass", selectedNodeClass);
        setStringProperty(component, "iconClass", iconClass);
    }
}
