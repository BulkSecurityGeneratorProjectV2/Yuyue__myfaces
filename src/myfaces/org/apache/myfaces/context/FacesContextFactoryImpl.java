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
package net.sourceforge.myfaces.context;

import net.sourceforge.myfaces.context.servlet.ServletFacesContextImpl;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * DOCUMENT ME!
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class FacesContextFactoryImpl
        extends FacesContextFactory
{
    public FacesContext getFacesContext(Object context,
                                        Object request,
                                        Object response,
                                        Lifecycle lifecycle)
            throws FacesException
    {
        if (context instanceof ServletContext)
        {
            return new ServletFacesContextImpl((ServletContext)context,
                                               (ServletRequest)request,
                                               (ServletResponse)response);
        }
        else
        {
            throw new FacesException("Unsupported context type " + context.getClass().getName());
        }
    }
}
