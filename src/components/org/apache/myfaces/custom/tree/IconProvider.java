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
package net.sourceforge.myfaces.custom.tree;

/**
 * <p>Interface to be implemented by node icon providers.</p>
 *
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 * @version $Revision$ $Date$
 *          $Log$
 *          Revision 1.2  2004/07/01 21:53:07  mwessendorf
 *          ASF switch
 *
 *          Revision 1.1  2004/05/04 12:19:14  o_rossmueller
 *          added icon provider
 *
 */
public interface IconProvider
{

    String getIconUrl(Object userObject, int childCount, boolean isLeaf);
}
