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
package org.apache.myfaces.wap.base;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
/**
 *
 * @author  <a href="mailto:Jiri.Zaloudek@ivancice.cz">Jiri Zaloudek</a> (latest modification by $Author$)
 * @version $Revision$ $Date$ 
 * $Log$
 * Revision 1.1  2004/12/30 09:37:27  matzew
 * added a new RenderKit for WML. Thanks to Jir� �aloudek
 *
 */
public class ConstantMethodBinding extends MethodBinding implements StateHolder {
    private String outCome;
    private boolean _transient;
    
    /** Creates a new instance of ConstantMethodBinding */
    public ConstantMethodBinding() {
    }
    
    public ConstantMethodBinding(String outCome) {
        this.outCome = outCome;
    }

    public Class getType(FacesContext facesContext) throws MethodNotFoundException {
        return(String.class);
    }
    
    public Object invoke(FacesContext facesContext, Object[] obj) throws EvaluationException, MethodNotFoundException {
        return(outCome);
    }
    
    
    public void restoreState(FacesContext facesContext, Object obj) {
        this.outCome = (String)obj;
    }

    public Object saveState(FacesContext facesContext) {
        return(outCome);
    }

    public boolean isTransient() {
        return _transient;
    }

    public void setTransient(boolean _transient) {
        this._transient = _transient;
    }        
}
