/*
 * Copyright 2009 Ganesh Jung
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
 *
 * Author: Ganesh Jung (latest modification by $Author: ganeshpuri $)
 * Version: $Revision: 1.4 $ $Date: 2009/05/31 09:16:44 $
 *
 */

/**
 * Constructor
 * @param {Node} source - Item that triggered the request
 * @param {Node} sourceForm (form) - Form containing source
 * @param {Object} context (Map) - AJAX context
 * @param {Object} passThrough (Map) - parameters to pass through to the server (execute/render)
 */
myfaces._impl.core._Runtime.extendClass("myfaces._impl.xhrCore._AjaxRequest", Object, {

    constructor_: function(source, sourceForm, context, passThrough) {
        this.m_exception = new myfaces._impl.xhrCore._Exception("myfaces._impl.xhrCore._AjaxRequest", this.alarmThreshold);
        try {
            this.m_contentType = "application/x-www-form-urlencoded";
            this.m_source = source;
            this.m_xhr = null;
            // myfaces parameters
            this.m_partialIdsArray = null;
            var errorlevel = 'NONE';
            this.m_queuesize = -1;

            /*namespace remapping for readability*/
            var _Runtime = myfaces._impl.core._Runtime;
            var _Lang = myfaces._impl._util._Lang;

            if (_Runtime.getLocalOrGlobalConfig(context, "errorlevel", null) != null) {
                errorlevel = context.myfaces.errorlevel;
            }
            if (_Runtime.getLocalOrGlobalConfig(context, "queuesize", null) != null) {
                this.m_queuesize = context.myfaces.queuesize;
            }
            if (_Runtime.getLocalOrGlobalConfig(context, "pps", null) != null
                    && _Lang.exists(passThrough, myfaces._impl.core._jsfImpl._PROP_EXECUTE)
                    && passThrough[myfaces._impl.core._jsfImpl._PROP_EXECUTE].length > 0) {
                this.m_partialIdsArray = passThrough[myfaces._impl.core._jsfImpl._PROP_EXECUTE].split(" ");
            }
            if (_Runtime.getLocalOrGlobalConfig(context, "timeout", null) != null) {
                this.m_timeout = context.myfaces.timeout;
            }
            if (_Runtime.getLocalOrGlobalConfig(context, "delay", null) != null) {
                this.m_delay = context.myfaces.delay;
            }
            this.m_context = context;
            this.m_response = new myfaces._impl.xhrCore._AjaxResponse(errorlevel);
            this.m_ajaxUtil = new myfaces._impl.xhrCore._AjaxUtils(errorlevel);
            this.m_sourceForm = sourceForm;
            this.m_passThrough = passThrough;
            this.m_requestParameters = this.getViewState();
            for (var key in this.m_passThrough) {
                this.m_requestParameters = this.m_requestParameters +
                        "&" + encodeURIComponent(key) +
                        "=" + encodeURIComponent(this.m_passThrough[key]);
            }
        } catch (e) {
            this.m_exception.throwError(null, context, "Ctor", e);
        }
    },

    /**
     * Sends an Ajax request
     */
    send : function() {
        try {
            var ajaxRequestQueue = myfaces._impl.xhrCore._AjaxRequestQueue.queue;

            this.m_xhr = myfaces._impl.core._Runtime.getXHRObject();

            this.m_xhr.open("POST", this.m_sourceForm.action, true);
            this.m_xhr.setRequestHeader("Content-Type", this.m_contentType);
            this.m_xhr.setRequestHeader("Faces-Request", "partial/ajax");
            this.m_xhr.onreadystatechange = myfaces._impl.xhrCore._AjaxRequestQueue.handleCallback;
            var _Impl =  myfaces._impl.core._Runtime.getGlobalConfig("jsfAjaxImpl", myfaces._impl.core._jsfImpl);
            _Impl.sendEvent(this.m_xhr, this.m_context, myfaces._impl.core._jsfImpl._AJAX_STAGE_BEGIN);
            this.m_xhr.send(this.m_requestParameters);
            if ('undefined' != typeof this.m_timeout) {
                var timeoutId = window.setTimeout(
                        function() {
                            try {
                                if (ajaxRequestQueue.m_request.m_xhr.readyState > 0
                                        && ajaxRequestQueue.queue.m_request.m_xhr.readyState < 4) {
                                    ajaxRequestQueue.queue.m_request.m_xhr.abort();
                                }
                            } catch (e) {
                                // don't care about exceptions here
                            }
                        }, this.m_timeout);
            }
        } catch (e) {
            this.m_exception.throwError(this.m_xhr, this.m_context, "send", e);

        }
    },

    /**
     * Callback method to process the Ajax response
     * triggered by RequestQueue
     */
    requestCallback : function() {
        var READY_STATE_DONE = 4;
        try {
            //local namespace remapping
            var _Impl = myfaces._impl.core._Runtime.getGlobalConfig("jsfAjaxImpl", myfaces._impl.core._jsfImpl);

            if (this.m_xhr.readyState == READY_STATE_DONE) {
                if (this.m_xhr.status >= 200 && this.m_xhr.status < 300) {
                    _Impl.sendEvent(this.m_xhr, this.m_context, myfaces._impl.core._jsfImpl._AJAX_STAGE_COMPLETE);
                    _Impl.response(this.m_xhr, this.m_context);
                    _Impl.sendEvent(this.m_xhr, this.m_context, myfaces._impl.core._jsfImpl._AJAX_STAGE_SUCCESS);
                    myfaces._impl.xhrCore._AjaxRequestQueue.queue.processQueue();
                } else {
                    _Impl.sendEvent(this.m_xhr, this.m_context, myfaces._impl.core._jsfImpl._AJAX_STAGE_COMPLETE);
                    var errorText;
                    try {
                        errorText = "Request failed";
                        if (this.m_xhr.status) {
                            errorText += "with status " + this.m_xhr.status;
                            if (this.m_xhr.statusText) {
                                errorText += " and reason " + this.m_xhr.statusText;
                            }
                        }
                    } catch (e) {
                        errorText = "Request failed with unknown status";
                    }
                    _Impl.sendError(this.m_xhr, this.m_context, myfaces._impl.core._jsfImpl._ERROR_HTTPERROR,
                            myfaces._impl.core._jsfImpl._ERROR_HTTPERROR, errorText);
                }
            }
        } catch (e) {
            this.m_exception.throwError(this.m_xhr, this.m_context, "requestCallback", e);
        }
    },

    /**
     * Spec. 13.3.1
     * Collect and encode input elements.
     * Additionally the hidden element javax.faces.ViewState
     * @return {String} - Concatenated String of the encoded input elements
     *             and javax.faces.ViewState element
     */
    getViewState : function() {
        return this.m_ajaxUtil.processUserEntries(this.m_xhr, this.m_context, this.m_source,
                this.m_sourceForm, this.m_partialIdsArray);
    }

});

