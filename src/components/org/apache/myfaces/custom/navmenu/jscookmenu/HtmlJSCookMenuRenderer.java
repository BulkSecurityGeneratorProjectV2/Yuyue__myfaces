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
package net.sourceforge.myfaces.custom.navmenu.jscookmenu;

import net.sourceforge.myfaces.custom.navmenu.NavigationMenuItem;
import net.sourceforge.myfaces.custom.navmenu.NavigationMenuUtils;
import net.sourceforge.myfaces.el.SimpleActionMethodBinding;
import net.sourceforge.myfaces.renderkit.RendererUtils;
import net.sourceforge.myfaces.renderkit.html.HtmlRenderer;
import net.sourceforge.myfaces.renderkit.html.util.DummyFormUtils;
import net.sourceforge.myfaces.renderkit.html.util.DummyFormResponseWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.webapp.UIComponentTag;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Spiegl (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          $Log$
 *          Revision 1.5  2004/07/01 21:53:09  mwessendorf
 *          ASF switch
 *
 *          Revision 1.4  2004/06/25 10:58:43  royalts
 *          fixed bug 979038
 *
 *          Revision 1.3  2004/06/23 14:17:31  royalts
 *          no message
 *
 *          Revision 1.2  2004/06/23 13:50:18  royalts
 *          no message
 *
 *          Revision 1.1  2004/06/23 13:44:31  royalts
 *          no message
 *
 */
public class HtmlJSCookMenuRenderer
    extends HtmlRenderer
{
    //private static final Log log = LogFactory.getLog(HtmlJSCookMenuRenderer.class);

    private static final String JSCOOK_ACTION_PARAM = "jscook_action";

    public void decode(FacesContext context, UIComponent component)
    {
        RendererUtils.checkParamValidity(context, component, HtmlCommandJSCookMenu.class);

        Map parameter = context.getExternalContext().getRequestParameterMap();
        String actionParam = (String)parameter.get(JSCOOK_ACTION_PARAM);
        if (actionParam != null)
        {
            MethodBinding mb;
            if (UIComponentTag.isValueReference(actionParam))
            {
                mb = context.getApplication().createMethodBinding(actionParam, null);
            }
            else
            {
                mb = new SimpleActionMethodBinding(actionParam);
            }
            ((HtmlCommandJSCookMenu)component).setAction(mb);

            component.queueEvent(new ActionEvent(component));
        }
    }

    public boolean getRendersChildren()
    {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component) throws IOException
    {
        RendererUtils.checkParamValidity(context, component, HtmlCommandJSCookMenu.class);

        List list = NavigationMenuUtils.getNavigationMenuItemList(component);
        if (list.size() > 0)
        {
            DummyFormResponseWriter dummyFormResponseWriter = DummyFormUtils.getDummyFormResponseWriter(context);
            dummyFormResponseWriter.addDummyFormParameter(JSCOOK_ACTION_PARAM);
            dummyFormResponseWriter.setWriteDummyForm(true);

            ResponseWriter writer = context.getResponseWriter();

            writer.write("\n<script language=\"JavaScript\"><!--\n" +
                         "var myMenu =\n[");
            encodeNavigationMenuItems(writer, list);
            writer.write("];\n" +
                         "--></script>\n");
        }
    }

    private void encodeNavigationMenuItems(ResponseWriter writer, List items)
        throws IOException
    {
        for (int i = 0, size = items.size(); i < size; i++)
        {
            if (i > 0)
                writer.write(",\n");
            NavigationMenuItem item = (NavigationMenuItem)items.get(i);

            if (item.isSplit())
            {
                writer.write("_cmSplit,");
            }

            String icon = item.getIcon() != null ?
                "'<img src=\"" + item.getIcon() + "\"/>'" : "''";
            String action = item.getAction() != null ?
                "'" + item.getAction() + "'" : "null";
            writer.write("[" + icon + ", '" + item.getLabel() + "', " + action +", '#', null");

            List children = item.getChildren();
            if (children.size() > 0)
            {
                writer.write(",");
                encodeNavigationMenuItems(writer, children);
            }
            writer.write("]");
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException
    {
        RendererUtils.checkParamValidity(context, component, HtmlCommandJSCookMenu.class);
        HtmlCommandJSCookMenu menu = (HtmlCommandJSCookMenu)component;
        ResponseWriter writer = context.getResponseWriter();

        String menuId = component.getId() + "_menu";
        writer.write("<div id=" + menuId + "></div>\n" +
                     "<script language=\"JavaScript\"><!--\n" +
                     "\tcmDraw ('" + menuId + "', myMenu, '" + menu.getLayout() + "', cm" + menu.getTheme() + ", '" + menu.getTheme() + "');\n" +
                     "--></script>\n");
    }

}
