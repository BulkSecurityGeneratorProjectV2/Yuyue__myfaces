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
package net.sourceforge.myfaces.custom.navmenu;

import net.sourceforge.myfaces.component.UserRoleAware;
import net.sourceforge.myfaces.taglib.core.SelectItemTagBase;

import javax.faces.component.UIComponent;

/**
 * @author Thomas Spiegl (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          $Log$
 *          Revision 1.2  2004/07/01 21:53:07  mwessendorf
 *          ASF switch
 *
 *          Revision 1.1  2004/06/23 13:44:31  royalts
 *          no message
 *
 */
public class HtmlNavigationMenuItemTag
    extends SelectItemTagBase
{

    private static final String ICON_ATTR   = "icon";
    private static final String ACTION_ATTR = "action";
    private static final String SPLIT_ATTR  = "split";

    private String _icon = null;
    private String _action = null;
    private String _split;

    // User Role support
    private String _enabledOnUserRole;
    private String _visibleOnUserRole;

    public String getComponentType()
    {
        return UINavigationMenuItem.COMPONENT_TYPE;
    }

    public String getRendererType()
    {
        return null;
    }

    protected void setProperties(UIComponent component)
    {
        setItemValue("0"); // itemValue not used
        super.setProperties(component);
        setStringProperty(component, ICON_ATTR, _icon);
        setStringProperty(component, ACTION_ATTR, _action);
        setBooleanProperty(component, SPLIT_ATTR, _split);

        setStringProperty(component, UserRoleAware.ENABLED_ON_USER_ROLE_ATTR, _enabledOnUserRole);
        setStringProperty(component, UserRoleAware.VISIBLE_ON_USER_ROLE_ATTR, _visibleOnUserRole);
    }


    public void setAction(String action)
    {
        _action = action;
    }

    public void setIcon(String icon)
    {
        _icon = icon;
    }

    public void setSplit(String split)
    {
        _split = split;
    }

    public void setVisibleOnUserRole(String visibleOnUserRole)
    {
        _visibleOnUserRole = visibleOnUserRole;
    }

    public void setEnabledOnUserRole(String enabledOnUserRole)
    {
        _enabledOnUserRole = enabledOnUserRole;
    }
}
