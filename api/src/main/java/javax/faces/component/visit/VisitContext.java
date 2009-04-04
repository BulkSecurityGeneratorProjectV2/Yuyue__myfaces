/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package javax.faces.component.visit;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author Simon Lessard (latest modification by $Author: slessard $)
 * @version $Revision: 696523 $ $Date: 2009-03-14 16:42:48 -0400 (mer., 17 sept. 2008) $
 * 
 * @since 2.0
 */
public abstract class VisitContext
{
    public static final Collection<String> ALL_IDS = new AllIdsCollection();

    public static VisitContext createVisitContext(FacesContext context)
    {
        // TODO: IMPLEMENT HERE
        return null;
    }

    public static VisitContext createVisitContext(FacesContext context, Collection<String> ids, Set<VisitHint> hints)
    {
        // TODO: IMPLEMENT HERE
        return null;
    }

    public abstract FacesContext getFacesContext();

    public abstract Set<VisitHint> getHints();

    public abstract Collection<String> getIdsToVisit();

    public abstract Collection<String> getSubtreeIdsToVisit(UIComponent component);

    public abstract VisitResult invokeVisitCallback(UIComponent component, VisitCallback callback);

    private static class AllIdsCollection implements Collection<String>, Serializable
    {
        @Override
        public boolean add(String e)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends String> c)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public Iterator<String> iterator()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a)
        {
            throw new UnsupportedOperationException();
        }
    }
}
