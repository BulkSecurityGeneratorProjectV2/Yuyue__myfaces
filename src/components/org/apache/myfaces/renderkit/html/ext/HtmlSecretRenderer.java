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
package net.sourceforge.myfaces.renderkit.html.ext;

import net.sourceforge.myfaces.component.UserRoleUtils;
import net.sourceforge.myfaces.renderkit.html.HtmlSecretRendererBase;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 * $Log$
 * Revision 1.2  2004/07/01 21:53:06  mwessendorf
 * ASF switch
 *
 * Revision 1.1  2004/05/18 14:31:38  manolito
 * user role support completely moved to components source tree
 *
 */
public class HtmlSecretRenderer
        extends HtmlSecretRendererBase
{
    protected boolean isDisabled(FacesContext facesContext, UIComponent uiComponent)
    {
        if (!UserRoleUtils.isEnabledOnUserRole(uiComponent))
        {
            return false;
        }
        else
        {
            return super.isDisabled(facesContext, uiComponent);
        }
    }
}
