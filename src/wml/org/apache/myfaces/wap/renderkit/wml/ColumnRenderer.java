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
package org.apache.myfaces.wap.renderkit.wml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author  <a href="mailto:Jiri.Zaloudek@ivancice.cz">Jiri Zaloudek</a> (latest modification by $Author$)
 * @version $Revision$ $Date$ 
 * $Log$
 * Revision 1.1  2004/12/30 09:37:26  matzew
 * added a new RenderKit for WML. Thanks to Jir� �aloudek
 *
 */
public class ColumnRenderer extends GroupRenderer {
    private static Log log = LogFactory.getLog(ColumnRenderer.class);
    
    /** Creates a new instance of ColumnRenderer */
    public ColumnRenderer() {
        super();
        log.debug("created object " + this.getClass().getName());
    }    
}

