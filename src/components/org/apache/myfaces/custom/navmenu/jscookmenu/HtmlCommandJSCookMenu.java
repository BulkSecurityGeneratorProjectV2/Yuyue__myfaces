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

import net.sourceforge.myfaces.component.UserRoleUtils;
import net.sourceforge.myfaces.component.UserRoleAware;

import javax.faces.component.UICommand;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;

/**
 * @author Thomas Spiegl (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          $Log$
 *          Revision 1.4  2004/07/01 21:53:09  mwessendorf
 *          ASF switch
 *
 *          Revision 1.3  2004/06/24 08:02:10  royalts
 *          no message
 *
 *          Revision 1.2  2004/06/23 14:35:18  royalts
 *          no message
 *
 *          Revision 1.1  2004/06/23 13:44:31  royalts
 *          no message
 *
 */
public class HtmlCommandJSCookMenu
    extends UICommand
    implements UserRoleAware
{
    //private static final Log log = LogFactory.getLog(HtmlCommandJSCookMenu.class);

    //------------------ GENERATED CODE BEGIN (do not modify!) --------------------

    public static final String COMPONENT_TYPE = "net.sourceforge.myfaces.JSCookMenu";
    public static final String COMPONENT_FAMILY = "javax.faces.Command";

    private String _layout = null;
    private String _theme = null;
    private String _enabledOnUserRole = null;
    private String _visibleOnUserRole = null;

    public HtmlCommandJSCookMenu()
    {
    }

    public String getFamily()
    {
        return COMPONENT_FAMILY;
    }

    public boolean isImmediate()
    {
        return true;
    }

    public void setLayout(String layout)
    {
        _layout = layout;
    }

    public String getLayout()
    {
        if (_layout != null) return _layout;
        ValueBinding vb = getValueBinding("layout");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setTheme(String theme)
    {
        _theme = theme;
    }

    public String getTheme()
    {
        if (_theme != null) return _theme;
        ValueBinding vb = getValueBinding("theme");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setEnabledOnUserRole(String enabledOnUserRole)
    {
        _enabledOnUserRole = enabledOnUserRole;
    }

    public String getEnabledOnUserRole()
    {
        if (_enabledOnUserRole != null) return _enabledOnUserRole;
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setVisibleOnUserRole(String visibleOnUserRole)
    {
        _visibleOnUserRole = visibleOnUserRole;
    }

    public String getVisibleOnUserRole()
    {
        if (_visibleOnUserRole != null) return _visibleOnUserRole;
        ValueBinding vb = getValueBinding("visibleOnUserRole");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public boolean isRendered()
    {
        if (!UserRoleUtils.isVisibleOnUserRole(this)) return false;
        return super.isRendered();
    }

    public Object saveState(FacesContext context)
    {
        Object values[] = new Object[5];
        values[0] = super.saveState(context);
        values[1] = _layout;
        values[2] = _theme;
        values[3] = _enabledOnUserRole;
        values[4] = _visibleOnUserRole;
        return ((Object) (values));
    }

    public void restoreState(FacesContext context, Object state)
    {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        _layout = (String)values[1];
        _theme = (String)values[2];
        _enabledOnUserRole = (String)values[3];
        _visibleOnUserRole = (String)values[4];
    }
    //------------------ GENERATED CODE END ---------------------------------------
}
