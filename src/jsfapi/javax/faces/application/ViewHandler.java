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
package javax.faces.application;

import javax.faces.FacesException;
import java.util.Locale;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public abstract class ViewHandler
{
    public static final String CHARACTER_ENCODING_KEY = "javax.faces.request.charset";
    public static final String DEFAULT_SUFFIX_PARAM_NAME = "javax.faces.DEFAULT_SUFFIX";
    public static final String DEFAULT_SUFFIX = ".jsp";

    public abstract Locale calculateLocale(javax.faces.context.FacesContext context);

    public abstract String calculateRenderKitId(javax.faces.context.FacesContext context);

    public abstract javax.faces.component.UIViewRoot createView(javax.faces.context.FacesContext context,
                                                                String viewId);

    public abstract String getActionURL(javax.faces.context.FacesContext context,
                                                  String viewId);

    public abstract String getResourceURL(javax.faces.context.FacesContext context,
                                                    String path);

    public abstract void renderView(javax.faces.context.FacesContext context,
                                    javax.faces.component.UIViewRoot viewToRender)
            throws java.io.IOException,
                   FacesException;

    public abstract javax.faces.component.UIViewRoot restoreView(javax.faces.context.FacesContext context,
                                                                 String viewId);

    public abstract void writeState(javax.faces.context.FacesContext context)
            throws java.io.IOException;
}
