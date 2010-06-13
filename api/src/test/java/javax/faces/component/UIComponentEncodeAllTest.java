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
package javax.faces.component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.myfaces.TestRunner;
import org.apache.myfaces.Assert;
import org.easymock.EasyMock;

/**
     * Tests for {@link UIComponent#encodeAll(javax.faces.context.FacesContext)}.
 */
public class UIComponentEncodeAllTest extends UIComponentTestBase
{
    private UIComponent _testimpl;

    @Override
    @BeforeMethod(alwaysRun = true)
    protected void setUp() throws Exception
    {
        super.setUp();
        Collection<Method> mockedMethods = new ArrayList<Method>();
        Class<UIComponent> clazz = UIComponent.class;
        mockedMethods.add(clazz.getDeclaredMethod("isRendered", (Class<?>[])null));
        mockedMethods.add(clazz.getDeclaredMethod("encodeBegin", new Class[] { FacesContext.class }));
        mockedMethods.add(clazz.getDeclaredMethod("getRendersChildren", (Class<?>[])null));
        mockedMethods.add(clazz.getDeclaredMethod("encodeChildren", new Class[] { FacesContext.class }));
        mockedMethods.add(clazz.getDeclaredMethod("getChildren", (Class<?>[])null));
        mockedMethods.add(clazz.getDeclaredMethod("getChildCount", (Class<?>[])null));
        mockedMethods.add(clazz.getDeclaredMethod("encodeEnd", new Class[] { FacesContext.class }));

        _testimpl = _mocksControl.createMock(clazz, mockedMethods.toArray(new Method[mockedMethods.size()]));
        _mocksControl.checkOrder(true);
    }

    @Test
    public void testEncodeAllNullContext() throws Exception
    {
        Assert.assertException(NullPointerException.class, new TestRunner()
        {
            public void run() throws Throwable
            {
                _testimpl.encodeAll(null);
            }
        });
    }

    @Test
    public void testEncodeAllNotRendered() throws Exception
    {
        EasyMock.expect(_testimpl.isRendered()).andReturn(false);
        _mocksControl.replay();
        _testimpl.encodeAll(_facesContext);
        _mocksControl.verify();
    }

    @Test
    public void testEncodeAllRenderesChildren() throws Exception
    {
        EasyMock.expect(_testimpl.isRendered()).andReturn(true);
        _testimpl.encodeBegin(EasyMock.same(_facesContext));
        EasyMock.expect(_testimpl.getRendersChildren()).andReturn(true);
        _testimpl.encodeChildren(EasyMock.same(_facesContext));
        _testimpl.encodeEnd(EasyMock.same(_facesContext));
        _mocksControl.replay();
        _testimpl.encodeAll(_facesContext);
        _mocksControl.verify();
    }

    @Test
    public void testEncodeAllNotRenderesChildren() throws Exception
    {
        EasyMock.expect(_testimpl.isRendered()).andReturn(true);
        _testimpl.encodeBegin(EasyMock.same(_facesContext));
        EasyMock.expect(_testimpl.getRendersChildren()).andReturn(false);

        List<UIComponent> childs = new ArrayList<UIComponent>();
        UIComponent testChild = _mocksControl.createMock(UIComponent.class);
        childs.add(testChild);
        EasyMock.expect(_testimpl.getChildCount()).andReturn(childs.size());        
        EasyMock.expect(_testimpl.getChildren()).andReturn(childs);
        testChild.encodeAll(EasyMock.same(_facesContext));

        _testimpl.encodeEnd(EasyMock.same(_facesContext));
        _mocksControl.replay();
        _testimpl.encodeAll(_facesContext);
        _mocksControl.verify();
    }
}
