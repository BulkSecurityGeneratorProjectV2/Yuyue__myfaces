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
package net.sourceforge.myfaces.taglib.html;

import net.sourceforge.myfaces.renderkit.html.HTML;

import javax.faces.component.UIComponent;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 * $Log$
 * Revision 1.2  2004/07/01 22:01:11  mwessendorf
 * ASF switch
 *
 * Revision 1.1  2004/04/01 12:57:44  manolito
 * additional extended component classes for user role support
 *
 */
public abstract class HtmlFormTagBase
        extends HtmlComponentTagBase
{
    //private static final Log log = LogFactory.getLog(HtmlFormTag.class);

    // UIComponent attributes --> already implemented in UIComponentTagBase

    // user role attributes --> already implemented in UIComponentTagBase

    // HTML universal attributes --> already implemented in HtmlComponentTagBase

    // HTML event handler attributes --> already implemented in HtmlComponentTagBase

    // HTML form attributes

    private String _accept;
    private String _acceptCharset;
    private String _enctype;
    private String _name;
    private String _onreset;
    private String _onsubmit;
    private String _target;

    // UIForm attributes --> none so far


    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        setStringProperty(component, HTML.ACCEPT_ATTR, _accept);
        setStringProperty(component, HTML.ACCEPT_CHARSET_ATTR, _acceptCharset);
        setStringProperty(component, HTML.ENCTYPE_ATTR, _enctype);
        setStringProperty(component, HTML.NAME_ATTR, _name);
        setStringProperty(component, HTML.ONRESET_ATTR, _onreset);
        setStringProperty(component, HTML.ONSUMBIT_ATTR, _onsubmit);
        setStringProperty(component, HTML.TARGET_ATTR, _target);
    }

    public void setAccept(String accept)
    {
        _accept = accept;
    }

    public void setAcceptCharset(String acceptCharset)
    {
        _acceptCharset = acceptCharset;
    }

    public void setEnctype(String enctype)
    {
        _enctype = enctype;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public void setOnreset(String onreset)
    {
        _onreset = onreset;
    }

    public void setOnsubmit(String onsubmit)
    {
        _onsubmit = onsubmit;
    }

    public void setTarget(String target)
    {
        _target = target;
    }

}
