<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<html>

<%@include file="inc/head.inc" %>

<!--
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
//-->

<body>

<f:view>

    <f:loadBundle basename="org.apache.myfaces.examples.resource.example_messages" var="example_messages"/>

    <x:panelLayout id="page" layout="#{globalOptions.pageLayout}"
            styleClass="pageLayout"
            headerClass="pageHeader"
            navigationClass="pageNavigation"
            bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="header">
            <f:subview id="header">
                <jsp:include page="inc/page_header.jsp" />
            </f:subview>
        </f:facet>

        <f:facet name="navigation">
            <f:subview id="menu" >
                <jsp:include page="inc/navigation.jsp" />
            </f:subview>
        </f:facet>

        <f:facet name="body">
            <h:panelGroup id="body">

				<h:form>
					<f:verbatim>
						<h1>Html Editor</h1>
						Powered by the <a href="http://kupu.oscom.org">Kupu library</a>
					</f:verbatim>

					<x:htmlEditor value="#{editor.text}"
						style="height: 60ex;"
						allowEditSource="#{editor.allowEditSource}"
						showPropertiesToolBox="#{editor.showPropertiesToolBox}"
						showLinksToolBox="#{editor.showLinksToolBox}"
						showImagesToolBox="#{editor.showImagesToolBox}"
						showTablesToolBox="#{editor.showTablesToolBox}"
						showCleanupExpressionsToolBox="#{editor.showCleanupExpressionsToolBox}"
						showDebugToolBox="#{editor.showDebugToolBox}"/>
					
					<x:div>
						<h:outputText value="Note : You can drag & drop images in the editor."/>
					</x:div>
					
					<h:commandButton value="Submit"/>

					<f:verbatim>
						<h2>Component Options</h2>
					</f:verbatim>
					<h:panelGrid columns="2">
						<h:selectBooleanCheckbox value="#{editor.allowEditSource}" immediate="true"/>
						<h:outputText value="Allow Edit Source"/>

						<h:selectBooleanCheckbox value="#{editor.showPropertiesToolBox}" immediate="true"/>
						<h:outputText value="Show Properties tool box"/>

						<h:selectBooleanCheckbox value="#{editor.showLinksToolBox}" immediate="true"/>
						<h:outputText value="Show Links tool box"/>

						<h:selectBooleanCheckbox value="#{editor.showImagesToolBox}" immediate="true"/>
						<h:outputText value="Show Images tool box"/>

						<h:selectBooleanCheckbox value="#{editor.showTablesToolBox}" immediate="true"/>
						<h:outputText value="Show Tables tool box"/>

						<h:selectBooleanCheckbox value="#{editor.showCleanupExpressionsToolBox}" immediate="true"/>
						<h:outputText value="Show Cleanup expressions tool box"/>
						
						<h:selectBooleanCheckbox value="#{editor.showDebugToolBox}" immediate="true"/>
						<h:outputText value="Show Debug tool box"/>
					</h:panelGrid>

				</h:form>

            </h:panelGroup>
        </f:facet>

        <%@include file="inc/page_footer.jsp" %>

    </x:panelLayout>

</f:view>

</body>

</html>