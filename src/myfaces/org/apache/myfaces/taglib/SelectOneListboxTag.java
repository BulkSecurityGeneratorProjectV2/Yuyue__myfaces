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
package net.sourceforge.myfaces.taglib;

import net.sourceforge.myfaces.renderkit.attr.ListboxRendererAttributes;
import net.sourceforge.myfaces.renderkit.html.HTML;
import net.sourceforge.myfaces.renderkit.html.ListboxRenderer;


/**
 * see "select_one_listbox" tag in myfaces_html.tld
 * @author Thomas Spiegl (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class SelectOneListboxTag
    extends MyFacesTag
    implements ListboxRendererAttributes
{
    public String getComponentType()
    {
        return "SelectOne";
    }

    public String getRendererType()
    {
        return ListboxRenderer.TYPE;
    }

    // UIComponent attributes --> already implemented in MyFacesTag

    // UISelectOne attributes

    public void setValue(Object v)
    {
        super.setValue(v);
    }

    public void setSelectOneClass(String value)
    {
        setRendererAttributeString(SELECT_ONE_CLASS_ATTR, value);
    }

    // HTML universal attributes --> already implemented in MyFacesTag

    // HTML event handler attributes --> already implemented in MyFacesTag

    // HTML select attributes

    public void setDatafld(String value)
    {
        setRendererAttributeString(HTML.DATAFLD_ATTR, value);
    }

    public void setDatasrc(String value)
    {
        setRendererAttributeString(HTML.DATASRC_ATTR, value);
    }

    public void setDataformatas(String value)
    {
        setRendererAttributeString(HTML.DATAFORMATAS_ATTR, value);
    }

    public void setDisabled(String value)
    {
        setRendererAttributeBoolean(HTML.DISABLED_ATTR, value);
    }

    public void setOnblur(String value)
    {
        setRendererAttributeString(HTML.ONBLUR_ATTR, value);
    }

    public void setOnchange(String value)
    {
        setRendererAttributeString(HTML.ONCHANGE_ATTR, value);
    }

    public void setOnfocus(String value)
    {
        setRendererAttributeString(HTML.ONFOCUS_ATTR, value);
    }

    public void setTabindex(String value)
    {
        setRendererAttributeString(HTML.TABINDEX_ATTR, value);
    }

    // Listbox Renderer attributes

    // user role attributes --> already implemented in MyFacesTag
}
