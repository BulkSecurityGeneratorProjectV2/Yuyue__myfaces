/**
 * MyFaces - the free JSF implementation
 * Copyright (C) 2003  The MyFaces Team (http://myfaces.sourceforge.net)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.myfaces.renderkit.html;

import net.sourceforge.myfaces.component.CommonComponentProperties;
import net.sourceforge.myfaces.component.UIComponentUtils;
import net.sourceforge.myfaces.renderkit.attr.CommonRendererAttributes;
import net.sourceforge.myfaces.renderkit.attr.SecretRendererAttributes;
import net.sourceforge.myfaces.renderkit.attr.UserRoleAttributes;
import net.sourceforge.myfaces.renderkit.html.util.HTMLEncoder;
import net.sourceforge.myfaces.renderkit.html.util.HTMLUtil;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

/**
 * see Spec.1.0 EA - JSF.7.6.4 Renderer Types for UIInput Components
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class SecretRenderer
    extends HTMLRenderer
    implements CommonComponentProperties,
               CommonRendererAttributes,
               SecretRendererAttributes,
               UserRoleAttributes
{
    public static final String TYPE = "Secret";

    public String getRendererType()
    {
        return TYPE;
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
        throws IOException
    {
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.write("<input type=\"password\"");
        writer.write(" name=\"");
        writer.write(uiComponent.getClientId(facesContext));
        writer.write("\"");
        if (UIComponentUtils.getBooleanAttribute(uiComponent,
                                                 REDISPLAY_ATTR,
                                                 false))
        {
            String currentValue = getStringValue(facesContext, (UIInput)uiComponent);
            if (currentValue != null)
            {
                writer.write(" value=\"");
                writer.write(HTMLEncoder.encode(currentValue, false, false));
                writer.write("\"");
            }
        }

        HTMLUtil.renderCssClass(writer, uiComponent, INPUT_CLASS_ATTR);
        HTMLUtil.renderHTMLAttributes(writer, uiComponent, HTML.UNIVERSAL_ATTRIBUTES);
        HTMLUtil.renderHTMLAttributes(writer, uiComponent, HTML.EVENT_HANDLER_ATTRIBUTES);
        HTMLUtil.renderHTMLAttributes(writer, uiComponent, HTML.INPUT_ATTRIBUTES);
        HTMLUtil.renderHTMLAttribute(writer, uiComponent, MAXLENGTH_ATTR, "maxlength");
        HTMLUtil.renderDisabledOnUserRole(facesContext, uiComponent);

        writer.write(">");
    }

}
