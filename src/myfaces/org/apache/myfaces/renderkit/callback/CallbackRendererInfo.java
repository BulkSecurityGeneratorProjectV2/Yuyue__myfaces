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
package net.sourceforge.myfaces.renderkit.callback;

import javax.faces.component.UIComponent;

/**
 * Record of the CallbackRendererInfoMap, that is used by CallbackSupport
 * to track the registered CallbackRenderers.
 *
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
class CallbackRendererInfo
{
    public UIComponent _component;
    public CallbackRenderer _callbackRenderer;
    public boolean _onlyChildren;

    public CallbackRendererInfo(UIComponent component,
                                CallbackRenderer callbackRenderer,
                                boolean onlyChildren)
    {
        _component = component;
        _callbackRenderer = callbackRenderer;
        _onlyChildren = onlyChildren;
    }
}
