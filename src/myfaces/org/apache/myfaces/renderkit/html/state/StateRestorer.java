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
package net.sourceforge.myfaces.renderkit.html.state;

import net.sourceforge.myfaces.component.CommonComponentAttributes;
import net.sourceforge.myfaces.convert.ConverterUtils;
import net.sourceforge.myfaces.convert.impl.StringArrayConverter;
import net.sourceforge.myfaces.renderkit.html.jspinfo.JspInfo;
import net.sourceforge.myfaces.renderkit.html.jspinfo.JspInfoUtils;
import net.sourceforge.myfaces.renderkit.html.jspinfo.JspBeanInfo;
import net.sourceforge.myfaces.taglib.core.ActionListenerTag;
import net.sourceforge.myfaces.tree.TreeUtils;
import net.sourceforge.myfaces.util.logging.LogUtil;

import javax.faces.FacesException;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesListener;
import javax.faces.event.ValueChangedListener;
import javax.faces.tree.Tree;
import java.io.IOException;
import java.util.*;

/**
 * DOCUMENT ME!
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class StateRestorer
{
    private static final String STATE_MAP_REQUEST_ATTR = StateRestorer.class.getName() + ".STATE_MAP";

    public void restoreState(FacesContext facesContext) throws IOException
    {
        Tree requestTree = facesContext.getTree();
        Map stateMap = getStateMap(facesContext);

        String previousTreeId = getStateParameter(stateMap,
                                                  StateRenderer.TREE_ID_REQUEST_PARAM);
        if (previousTreeId != null && requestTree.getTreeId().equals(previousTreeId))
        {
            //recreate beans of "request" scope
            recreateRequestScopeBeans(facesContext);

            //restore model beans and values:
            restoreModelValues(facesContext, stateMap);

            //restore "hint" about unrendered components
            Set unrendererComponents = restoreUnrenderedComponents(stateMap);

            //restore tree
            Tree staticTree = JspInfo.getTree(facesContext, requestTree.getTreeId());
            TreeCopier treeCopier = new TreeCopier(facesContext);
            treeCopier.setOverwriteAttributes(false);
            treeCopier.setOverwriteComponents(false);
            treeCopier.setIgnoreComponents(unrendererComponents);
            treeCopier.copyTree(staticTree, requestTree);

            //restore component states
            for (Iterator it = TreeUtils.treeIterator(requestTree); it.hasNext();)
            {
                UIComponent comp = (UIComponent)it.next();
                restoreComponent(facesContext, stateMap, comp);
            }

            //remap tagHash
            TagHashHack.convertUniqueIdsBackToComponents(requestTree);

            //restore model beans and values:
            restoreModelValues(facesContext, stateMap);

            //restore listeners:
            restoreListeners(facesContext, stateMap);
        }

        restoreLocale(facesContext, stateMap);
    }

    protected Map getStateMap(FacesContext facesContext)
    {
        Map map = (Map)facesContext.getServletRequest().getAttribute(STATE_MAP_REQUEST_ATTR);
        if (map == null)
        {
            map = restoreStateMap(facesContext);
            facesContext.getServletRequest().setAttribute(STATE_MAP_REQUEST_ATTR, map);
        }
        return map;
    }

    protected Map restoreStateMap(FacesContext facesContext)
    {
        return facesContext.getServletRequest().getParameterMap();
    }

    protected String getStateParameter(Map stateMap, String paramName)
    {
        String[] values = (String[])stateMap.get(paramName);
        if (values == null || values.length == 0)
        {
            return null;
        }

        if (values.length == 1)
        {
            return values[0];
        }
        else //(values.length > 1)
        {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < values.length; i++)
            {
                if (i > 0)
                {
                    buf.append(',');
                }
                buf.append(values[i]);
            }
            return buf.toString();
        }
    }

    protected String getStateParameterValueAsString(Object paramValue)
    {
        if (paramValue instanceof String[])
        {
            return StringArrayConverter.getAsString((String[])paramValue,
                                                    false);
        }
        else
        {
            return (String)paramValue;
        }
    }



    /**
     * The model reference for each component is checked. If a model bean does not
     * yet exist it is automatically created in the defined scope.
     * @param facesContext
     * @param stateMap
     * @param uiComponent
     * @throws FacesException
     */
    protected void restoreComponent(FacesContext facesContext,
                                    Map stateMap,
                                    UIComponent uiComponent)
        throws FacesException
    {
        //Check model instance and create it, if it does not exist:
        /*
        --> already happend in recreateRequestScopeBeans

        String modelRef = uiComponent.getModelReference();
        if (modelRef != null)
        {
            JspInfoUtils.checkModelInstance(facesContext, modelRef);
        }
        */

        //Set valid as default
        if (uiComponent.getAttribute(CommonComponentAttributes.VALID_ATTR) == null &&
            uiComponent.isValid())
        {
            //component is valid by default, so no need to set explicitly
        }
        else
        {
            //StateSaver always saves a "valid" == false, so it will be
            //set to saved state later anyway.
            uiComponent.setValid(true);
        }

        //restore attributes
        for (Iterator it = stateMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            String attrName = RequestParameterNames.restoreUIComponentStateParameterAttributeName(facesContext,
                                                                                                  uiComponent,
                                                                                                  (String)entry.getKey());
            if (attrName != null)
            {
                restoreComponentAttribute(facesContext,
                                          uiComponent,
                                          attrName,
                                          entry.getValue());
            }
        }

        registerTagCreatedActionListeners(facesContext, stateMap, uiComponent);
    }


    protected void restoreComponentAttribute(FacesContext facesContext,
                                             UIComponent uiComponent,
                                             String attrName,
                                             Object paramValue)
    {
        String strValue = getStateParameterValueAsString(paramValue);

        if (TagHashHack.isTagHashAttribute(uiComponent, attrName))
        {
            Object objValue = TagHashHack.getAsObjectFromSaved(strValue);
            uiComponent.setAttribute(attrName, objValue);
            return;
        }

        //is it a null value?
        if (strValue.equals(StateRenderer.NULL_DUMMY_VALUE))
        {
            uiComponent.setAttribute(attrName, null);
            return;
        }

        //Find proper converter to convert back from external String
        Converter conv;
        if (attrName.equals(CommonComponentAttributes.VALUE_ATTR))
        {
            conv = ConverterUtils.findValueConverter(facesContext,
                                                     uiComponent);
        }
        else
        {
            conv = ConverterUtils.findAttributeConverter(facesContext,
                                                         uiComponent,
                                                         attrName);
        }

        Object objValue;
        if (conv != null)
        {
            //we have a converter, so StateSaver did convert to String
            if (conv instanceof StringArrayConverter &&
                paramValue instanceof String[])
            {
                //no need to convert the String value back to StringArray
                objValue = paramValue;
            }
            else
            {
                try
                {
                    objValue = conv.getAsObject(facesContext,
                                                uiComponent,
                                                strValue);
                }
                catch (ConverterException e)
                {
                    LogUtil.getLogger().severe("Value of attribute " + attrName + " will be lost, because of converter exception restoring state of component " + uiComponent.getClientId(facesContext) + ".");
                    return;
                }
            }
        }
        else
        {
            //we have no converter, so StateSaver did serialize the value
            try
            {
                objValue = ConverterUtils.deserialize(strValue);
            }
            catch (FacesException e)
            {
                LogUtil.getLogger().severe("Value of attribute " + attrName + " of component " + uiComponent.getClientId(facesContext) + " will be lost, because of exception during deserialization: " + e.getMessage());
                return;
            }
        }

        uiComponent.setAttribute(attrName, objValue);
    }




    /**
     * Register all listeners that would normally by added via f:action_listener tags.
     */
    protected void registerTagCreatedActionListeners(FacesContext facesContext,
                                                     Map stateMap,
                                                     UIComponent uiComponent)
    {
        List lst = JspInfo.getActionListenersTypeList(uiComponent);
        if (lst != null)
        {
            for (Iterator it = lst.iterator(); it.hasNext();)
            {
                String type = (String)it.next();
                ActionListenerTag.addActionListener(uiComponent, type);
            }
        }
    }



    protected void restoreModelValues(FacesContext facesContext, Map stateMap)
    {
        Iterator it = JspInfo.getUISaveStateComponents(facesContext,
                                                       facesContext.getTree().getTreeId());
        while (it.hasNext())
        {
            UIComponent uiSaveState = (UIComponent)it.next();
            restoreModelValue(facesContext, stateMap, uiSaveState);
        }
    }

    public void restoreModelValue(FacesContext facesContext, Map stateMap, UIComponent uiSaveState)
    {
        String modelRef = uiSaveState.getModelReference();
        if (modelRef == null)
        {
            throw new FacesException("UISaveState " + uiSaveState.getComponentId() + " has no model reference");
        }

        //Check model instance and create, if it does not exist:
        /*
        --> already happend in recreateRequestScopeBeans

        JspInfoUtils.checkModelInstance(facesContext, modelRef);
        */

        String paramName = RequestParameterNames.getModelValueStateParameterName(modelRef);
        String paramValue = getStateParameter(stateMap, paramName);
        if (paramValue != null)
        {
            Object propValue;
            Converter conv = ConverterUtils.findValueConverter(facesContext, uiSaveState);
            if (conv != null)
            {
                try
                {
                    propValue = conv.getAsObject(facesContext, uiSaveState, paramValue);
                }
                catch (ConverterException e)
                {
                    throw new FacesException("Error restoring state of model value " + modelRef + ": Converter exception!", e);
                }
            }
            else
            {
                propValue = ConverterUtils.deserialize(paramValue);
            }

            facesContext.setModelValue(modelRef, propValue);
        }
    }


    public void restoreComponentState(FacesContext facesContext, UIComponent uiComponent) throws IOException
    {
        Map stateMap = getStateMap(facesContext);
        restoreComponent(facesContext, stateMap, uiComponent);
    }


    protected void restoreLocale(FacesContext facesContext, Map stateMap)
    {
        String localeVal = getStateParameter(stateMap, StateRenderer.LOCALE_REQUEST_PARAM);
        if (localeVal != null)
        {
            StringTokenizer st = new StringTokenizer(localeVal, StateRenderer.LOCALE_REQUEST_PARAM_DELIMITER);
            String language = "";
            String country = "";
            String variant = "";
            if (st.hasMoreTokens())
            {
                language = st.nextToken();
            }
            if (st.hasMoreTokens())
            {
                country = st.nextToken();
            }
            if (st.hasMoreTokens())
            {
                variant = st.nextToken();
            }
            facesContext.setLocale(new Locale(language, country, variant));
        }
    }


    protected void restoreListeners(FacesContext facesContext, Map stateMap)
    {
        for (Iterator it = stateMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            String paramName = (String)entry.getKey();
            RequestParameterNames.ListenerParameterInfo info
                = RequestParameterNames.getListenerParameterInfo(paramName);
            if (info != null)
            {
                //UIComponent root = facesContext.getTree().getRoot();
                String paramValue = getStateParameter(stateMap, paramName);
                FacesListener listener;
                if (info.serializedListener)
                {
                    //deserialize Listener
                    listener = (FacesListener)ConverterUtils.deserialize(paramValue);
                }
                else
                {
                    //Listener is another component, paramValue is the uniqueId
                    //listener = (FacesListener)root.findComponent(paramValue);
                    listener = (FacesListener)JspInfo.findComponentByUniqueId(facesContext.getTree(),
                                                                              paramValue);
                    if (listener == null)
                    {
                        LogUtil.getLogger().severe("Could not find listener component for restored listener of type " + info.listenerType);
                        continue;
                    }
                }

                /*
                UIComponent uiComponent = root.findComponent(info.uniqueComponentId);
                */
                UIComponent uiComponent = JspInfo.findComponentByUniqueId(facesContext.getTree(),
                                                                          info.uniqueComponentId);
                if (uiComponent == null)
                {
                    LogUtil.getLogger().severe("Could not find component " + info.uniqueComponentId + " to add restored listener of type " + listener.getClass().getName());
                    continue;
                }

                if (info.listenerType.equals(StateRenderer.LISTENER_TYPE_ACTION))
                {
                    if (uiComponent instanceof UICommand)
                    {
                        ((UICommand)uiComponent).addActionListener((ActionListener)listener);
                    }
                    else
                    {
                        throw new FacesException("Expected UICommand component.");
                    }
                }
                else if (info.listenerType.equals(StateRenderer.LISTENER_TYPE_VALUE_CHANGED))
                {
                    if (uiComponent instanceof UIInput)
                    {
                        ((UIInput)uiComponent).addValueChangedListener((ValueChangedListener)listener);
                    }
                    else
                    {
                        throw new FacesException("Expected UIInput component.");
                    }
                }
                else
                {
                    throw new IllegalArgumentException(info.listenerType);
                }

            }
        }
    }




    protected Set restoreUnrenderedComponents(Map stateMap)
    {
        String[] values = (String[])stateMap.get(StateRenderer.UNRENDERED_COMPONENTS_REQUEST_PARAM);
        if (values == null || values.length == 0)
        {
            return Collections.EMPTY_SET;
        }

        Set set = new HashSet();
        if (values.length == 1)
        {
            StringTokenizer st = new StringTokenizer(values[0], ",");
            while(st.hasMoreTokens())
            {
                set.add(st.nextToken());
            }
            return set;
        }
        else
        {
            for (int i = 0; i < values.length; i++)
            {
                set.add(values[i]);
            }
            return set;
        }
    }


    protected void recreateRequestScopeBeans(FacesContext facesContext)
    {
        Iterator it = JspInfo.getJspBeanInfos(facesContext,
                                              facesContext.getTree().getTreeId());
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            JspInfoUtils.checkModelInstance(facesContext,
                                            (JspBeanInfo)entry.getValue());
        }
    }


}
