/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
myfaces._impl.core._Runtime.singletonExtendClass("myfaces._impl.core.Impl", Object, {

    //third option myfaces._impl.xhrCoreAjax which will be the new core impl for now
    _transport : new (myfaces._impl.core._Runtime.getGlobalConfig("transport", myfaces._impl.xhrCore._Transports))(),

    /**
     * external event listener queue!
     */
    _eventListenerQueue : new (myfaces._impl.core._Runtime.getGlobalConfig("eventListenerQueue", myfaces._impl._util._ListenerQueue))(),

    /**
     * external error listener queue!
     */
    _errorListenerQueue : new (myfaces._impl.core._Runtime.getGlobalConfig("errorListenerQueue", myfaces._impl._util._ListenerQueue))(),

    /*CONSTANTS*/

    /*internal identifiers for options*/
    _OPT_IDENT_ALL : "@all",
    _OPT_IDENT_NONE : "@none",
    _OPT_IDENT_THIS : "@this",
    _OPT_IDENT_FORM : "@form",

    /*
     * [STATIC] constants
     */

    _PROP_PARTIAL_SOURCE : "javax.faces.source",
    _PROP_VIEWSTATE : "javax.faces.ViewState",
    _PROP_AJAX : "javax.faces.partial.ajax",
    _PROP_EXECUTE : "javax.faces.partial.execute",
    _PROP_RENDER : "javax.faces.partial.render",
    _PROP_EVENT : "javax.faces.partial.event",

    /* message types */
    _MSG_TYPE_ERROR : "error",
    _MSG_TYPE_EVENT : "event",

    /* event emitting stages */
    _AJAX_STAGE_BEGIN : "begin",
    _AJAX_STAGE_COMPLETE : "complete",
    _AJAX_STAGE_SUCCESS : "success",

    /*ajax errors spec 14.4.2*/
    _ERROR_HTTPERROR : "httpError",
    _ERROR_EMPTY_RESPONSE : "emptyResponse",
    _ERROR_MALFORMEDXML : "malformedXML",
    _ERROR_SERVER_ERROR : "serverError",
    _ERROR_CLIENT_ERROR : "clientError",



    /**
     * collect and encode data for a given form element (must be of type form)
     * find the javax.faces.ViewState element and encode its value as well!
     * return a concatenated string of the encoded values!
     *
     * @throws error in case of the given element not being of type form!
     * https://issues.apache.org/jira/browse/MYFACES-2110
     */
    getViewState : function(formElement) {
        /**
         *  typecheck assert!, we opt for strong typing here
         *  because it makes it easier to detect bugs
         */
        if ('undefined' != typeof formElement && null != formElement) {
            formElement = myfaces._impl._util._Lang.byId(formElement);
        }

        if ('undefined' == typeof(formElement)
                || null == formElement
                || 'undefined' == typeof(formElement.nodeName)
                || null == formElement.nodeName
                || formElement.nodeName.toLowerCase() != "form") {
            throw new Error("jsf.viewState: param value not of type form!");
        }

        var ajaxUtils = new myfaces._impl.xhrCore._AjaxUtils(0);
        return ajaxUtils.encodeSubmittableFields(null, null, null, formElement, null);

    },

    /**
     * internal assertion check for the element parameter
     * it cannot be null or undefined
     * it must be either a string or a valid existing dom node
     */
    _assertElement : function(/*String|Dom Node*/ element) {
        /*namespace remap for our local function context we mix the entire function namespace into
         *a local function variable so that we do not have to write the entire namespace
         *all the time
         **/
        var _Lang = myfaces._impl._util._Lang;

        /**
         * assert element
         */
        if ('undefined' == typeof( element ) || null == element) {
            throw new Error("jsf.ajax, element must be set!");
        }
        //        if (!JSF2Utils.isString(element) && !(element instanceof Node)) {
        //            throw new Error("jsf.ajax, element either must be a string or a dom node");
        //        }

        element = _Lang.byId(element);
        if ('undefined' == typeof element || null == element) {
            throw new Error("Element either must be a string to a or must be a valid dom node");
        }
    },

    _assertFunction : function(func) {
        if ('undefined' == typeof func || null == func) {
            return;
        }
        if (!(func instanceof Function)) {
            throw new Error("Functioncall " + func + " is not a function! ");
        }
    },

    /**
     * this function has to send the ajax requests
     *
     * following request conditions must be met:
     * <ul>
     *  <li> the request must be sent asynchronously! </li>
     *  <li> the request must be a POST!!! request </li>
     *  <li> the request url must be the form action attribute </li>
     *  <li> all requests must be queued with a client side request queue to ensure the request ordering!</li>
     * </ul>
     *
     * @param {String|Node} element any dom element no matter being it html or jsf, from which the event is emitted
     * @param {|Event|} event any javascript event supported by that object
     * @param {|Object|} options  map of options being pushed into the ajax cycle
     */
    request : function(element, event, options) {

        /*namespace remap for our local function context we mix the entire function namespace into
         *a local function variable so that we do not have to write the entire namespace
         *all the time
         **/
        var _Lang = myfaces._impl._util._Lang;
        var elementId = null;
        /**
         * we cross reference statically hence the mapping here
         * the entire mapping between the functions is stateless
         */
        element = _Lang.byId(element);
        event = ('undefined' == typeof event && window.event) ? window.event : event;

        if ('undefined' != typeof element && null != element) {
            //detached element handling, we also store the element name
            //to get a fallback option in case the identifier is not determinable
            // anymore, in case of a framework induced detachment the element.name should
            // be shared if the identifier is not determinable anymore
            elementId = ('undefined' != typeof element.id) ? element.id : null;
            if ((elementId == null || elementId == '') && 'undefined' != typeof element.name) {
                elementId = element.name;
            }
        }

        /*assert if the onerror is set and once if it is set it must be of type function*/
        this._assertFunction(options.onerror);
        /*assert if the onevent is set and once if it is set it must be of type function*/
        this._assertFunction(options.onevent);

        /*
         * We make a copy of our options because
         * we should not touch the incoming params!
         */
        var passThroughArguments = _Lang.mixMaps({}, options, true);

        /*additional passthrough cleanup*/
        /*ie6 supportive code to prevent browser leaks*/
        passThroughArguments.onevent = null;
        delete passThroughArguments.onevent;
        /*ie6 supportive code to prevent browser leaks*/
        passThroughArguments.onerror = null;
        delete passThroughArguments.onerror;

        if ('undefined' != typeof event && null != event) {
            passThroughArguments[this._PROP_EVENT] = event.type;
        }

        /**
         * ajax pass through context with the source
         * onevent and onerror
         */
        var ajaxContext = {};
        ajaxContext.source = element;
        ajaxContext.onevent = options.onevent;
        ajaxContext.onerror = options.onerror;

        /**
         * fetch the parent form
         */

        var sourceForm = myfaces._impl._util._Dom.fuzzyFormDetection(element);
        var _Lang = myfaces._impl._util._Lang;
        if (null == sourceForm && 'undefined' != typeof event && null != event) {
            sourceForm = myfaces._impl._util._Dom.fuzzyFormDetection(_Lang.getEventTarget(event));
            if (null == sourceForm) {
                throw Error("Sourceform could not be determined, either because element is not attached to a form or we have multiple forms with named elements of the same identifier or name, stopping the ajax processing");
            }
        } else if (null == sourceForm) {
            throw Error("Sourceform could not be determined, either because element is not attached to a form or we have multiple forms with named elements of the same identifier or name, stopping the ajax processing");
        }

        /**
         * binding contract the javax.faces.source must be set
         */
        passThroughArguments[this._PROP_PARTIAL_SOURCE] = elementId;

        /**
         * javax.faces.partial.ajax must be set to true
         */
        passThroughArguments[this._PROP_AJAX] = true;

        /**
         * if execute or render exist
         * we have to pass them down as a blank delimited string representation
         * of an array of ids!
         */
        if (_Lang.exists(passThroughArguments, "execute")) {
            /*the options must be a blank delimited list of strings*/
            var execString = _Lang.arrayToString(passThroughArguments.execute, ' ');
            var execNone = execString.indexOf(this._OPT_IDENT_NONE) != -1;
            var execAll = execString.indexOf(this._OPT_IDENT_ALL) != -1;
            if (!execNone && !execAll) {
                execString = execString.replace(this._OPT_IDENT_FORM, sourceForm.id);
                execString = execString.replace(this._OPT_IDENT_THIS, elementId);

                passThroughArguments[this._PROP_EXECUTE] = execString;
            } else if (execAll) {
                passThroughArguments[this._PROP_EXECUTE] = this._OPT_IDENT_ALL;
            }

            passThroughArguments.execute = null;
            /*remap just in case we have a valid pointer to an existing object*/
            delete passThroughArguments.execute;
        } else {
            passThroughArguments[this._PROP_EXECUTE] = elementId;
        }

        if (_Lang.exists(passThroughArguments, "render")) {
            var renderString = _Lang.arrayToString(passThroughArguments.render, ' ');
            var renderNone = renderString.indexOf(this._OPT_IDENT_NONE) != -1;
            var renderAll = renderString.indexOf(this._OPT_IDENT_ALL) != -1;
            if (!renderNone && !renderAll) {
                renderString = renderString.replace(this._OPT_IDENT_FORM, sourceForm.id);
                renderString = renderString.replace(this._OPT_IDENT_THIS, elementId);
                passThroughArguments[this._PROP_RENDER] = renderString;
                passThroughArguments.render = null;
            } else if (renderAll) {
                passThroughArguments[this._PROP_RENDER] = this._OPT_IDENT_ALL;

            }
            delete passThroughArguments.render;
        }

        //implementation specific options are added to the context for further processing
        if ('undefined' != typeof passThroughArguments.myfaces && null != passThroughArguments.myfaces) {
            ajaxContext.myfaces = passThroughArguments.myfaces;
            delete passThroughArguments.myfaces;
        }

        this._transport.xhrQueuedPost(element, sourceForm, ajaxContext, passThroughArguments);

    },

    addOnError : function(/*function*/errorListener) {
        /*error handling already done in the assert of the queue*/
        this._errorListenerQueue.enqueue(errorListener);
    },

    addOnEvent : function(/*function*/eventListener) {
        /*error handling already done in the assert of the queue*/
        this._eventListenerQueue.enqueue(eventListener);
    },

    /**
     * implementation triggering the error chain
     *
     * @param {Object} request the request object which comes from the xhr cycle
     * @param {Object} context (Map) the context object being pushed over the xhr cycle keeping additional metadata
     * @param {String} name the error name
     * @param {String} serverErrorName the server error name in case of a server error
     * @param {String} serverErrorMessage the server error message in case of a server error
     *
     *  handles the errors, in case of an onError exists within the context the onError is called as local error handler
     *  the registered error handlers in the queue receiv an error message to be dealt with
     *  and if the projectStage is at development an alert box is displayed
     *
     *  note: we have additional functionality here, via the global config myfaces.config.defaultErrorOutput a function can be provided
     *  which changes the default output behavior from alert to something else
     *
     *
     */
    sendError : function sendError(/*Object*/request, /*Object*/ context, /*String*/ name, /*String*/ serverErrorName, /*String*/ serverErrorMessage) {
        var eventData = {};
        //we keep this in a closure because we might reuse it for our serverErrorMessage
        var malFormedMessage = function() {
            return ('undefined' != typeof name && name == myfaces._impl.core.Impl._ERROR_MALFORMEDXML) ? "The server response could not be parsed, the server has returned with a response which is not xml !" : "";
        };

        eventData.type = this._MSG_TYPE_ERROR;

        eventData.status = name;
        eventData.serverErrorName = serverErrorName;
        eventData.serverErrorMessage = serverErrorMessage;

        try {
            eventData.source = context.source;
            eventData.responseCode = request.status;
            eventData.responseText = request.responseText;
            eventData.responseXML = request.responseXML;
        } catch (e) {
            // silently ignore: user can find out by examining the event data
        }

        /**/
        if (myfaces._impl._util._Lang.exists(context, "onerror")) {
            context.onerror(eventData);
        }

        /*now we serve the queue as well*/
        this._errorListenerQueue.broadcastEvent(eventData);

        if (jsf.getProjectStage() === "Development" && this._errorListenerQueue.length() == 0) {
            var defaultErrorOutput = myfaces._impl.core._Runtime.getGlobalConfig("defaultErrorOutput", alert);
            var finalMessage = [];

            finalMessage.push(('undefined' != typeof name && null != name) ? name : "");
            finalMessage.push(('undefined' != typeof serverErrorName && null != serverErrorName) ? serverErrorName : "");
            finalMessage.push(('undefined' != typeof serverErrorMessage && null != serverErrorMessage) ? serverErrorMessage : "");
            finalMessage.push(malFormedMessage());

            defaultErrorOutput(finalMessage.join("-") + " Note, this message is only sent, because project stage is development and no " +
                    "other error listeners are registered.");
        }
    },

    /**
     * sends an event
     */
    sendEvent : function sendEvent(/*Object*/request, /*Object*/ context, /*event name*/ name) {
        var eventData = {};
        eventData.type = this._MSG_TYPE_EVENT;

        eventData.status = name;
        eventData.source = context.source;

        if (name !== this._AJAX_STAGE_BEGIN) {

            try {
                eventData.responseCode = request.status;
                eventData.responseText = request.responseText;
                eventData.responseXML = request.responseXML;

            } catch (e) {
                var impl = myfaces._impl.core._Runtime.getGlobalConfig("jsfAjaxImpl", myfaces._impl.core.Impl);
                impl.sendError(request, context, this._ERROR_CLIENT_ERROR, "ErrorRetrievingResponse",
                        "Parts of the response couldn't be retrieved when constructing the event data: " + e);
                //client errors are not swallowed
                throw e;
            }

        }

        /**/
        if (myfaces._impl._util._Lang.exists(context, "onevent")) {
            /*calling null to preserve the original scope*/
            context.onevent.call(null, eventData);
        }

        /*now we serve the queue as well*/
        this._eventListenerQueue.broadcastEvent(eventData);
    },

    /**
     * processes the ajax response if the ajax request completes successfully
     * @param {Object} request (xhrRequest) the ajax request!
     * @param {Object} context (Map) context map keeping context data not being passed down over
     * the request boundary but kept on the client
     */
    response : function(request, context) {
        this._transport.response(request, context);
    },

    /**
     * @return the project stage also emitted by the server:
     * it cannot be cached and must be delivered over the server
     * The value for it comes from the request parameter of the jsf.js script called "stage".
     */
    getProjectStage : function() {
        /* run through all script tags and try to find the one that includes jsf.js */
        var scriptTags = document.getElementsByTagName("script");
        for (var i = 0; i < scriptTags.length; i++)
        {
            if (scriptTags[i].src.search(/\/javax\.faces\.resource\/jsf\.js.*ln=javax\.faces/) != -1)
            {
                /* try to extract stage=XXX */
                var result = scriptTags[i].src.match(/stage=([^&;]*)/);
                if (result)
                {
                    /* we found stage=XXX */
                    /* return only valid values of ProjectStage */
                    if (result[1] == "Production"
                            || result[1] == "Development"
                            || result[1] == "SystemTest"
                            || result[1] == "UnitTest")
                    {
                        return result[1];
                    }
                }
                else
                {
                    /* we found the script, but there was no stage parameter --> Production */
                    return "Production";
                }
            }
        }
        /* we could not find anything valid --> return the default value */
        return "Production";
    },

    /**
     * implementation of the external chain function
     * moved into the impl
     *
     *  @param {Object} source the source which also becomes
     * the scope for the calling function (unspecified side behavior)
     * the spec states here that the source can be any arbitrary code block.
     * Which means it either is a javascript function directly passed or a code block
     * which has to be evaluated separately.
     *
     * After revisiting the code additional testing against components showed that
     * the this parameter is only targeted at the component triggering the eval
     * (event) if a string code block is passed. This is behavior we have to resemble
     * in our function here as well, I guess.
     *
     * @param {Event} event the event object being passed down into the the chain as event origin
     *   the spec is contradicting here, it on one hand defines event, and on the other
     *   it says it is optional, after asking, it meant that event must be passed down
     *   but can be undefined
     */
    chain : function(source, event) {
        var len = arguments.length;
        //the spec is contradicting here, it on one hand defines event, and on the other
        //it says it is optional, I have cleared this up now
        //the spec meant the param must be passed down, but can be 'undefined'
        if (len < 2) {
            throw new Error(" an event object or unknown must be passed as second parameter ");
        } else if (len < 3) {
            if ('function' == typeof event || myfaces._impl._util._Lang.isString(event)) {
                throw new Error(" an event must be passed down (either a an event object null or undefined) ");
            }
            //nothing to be done here, move along
            return true;
        }
        //now we fetch from what is given from the parameter list
        //we cannot work with splice here in any performant way so we do it the hard way
        //arguments only are give if not set to undefined even null values!

        //assertions source either null or set as dom element:

        if ('undefined' == typeof source) {
            throw new Error(" source must be defined");
            //allowed chain datatypes
        } else if ('function' == typeof source) {
            throw new Error(" source cannot be a function (probably source and event were not defined or set to null");
        }
        if (myfaces._impl._util._Lang.isString(source)) {
            throw new Error(" source cannot be a string ");
        }

        //assertion if event is a function or a string we already are in our function elements
        //since event either is undefined, null or a valid event object

        if ('function' == typeof event || myfaces._impl._util._Lang.isString(event)) {
            throw new Error(" an event must be passed down (either a an event object null or undefined) ");
        }

        for (var loop = 2; loop < len; loop++) {
            //we do not change the scope of the incoming functions
            //but we reuse the argument array capabilities of apply
            var retVal;

            if ('function' == typeof arguments[loop]) {
                retVal = arguments[loop].call(source, event);
            } else {
                //either a function or a string can be passed in case of a string we have to wrap it into another function
                retVal = new Function("event", arguments[loop]).call(source, event);
            }
            //now if one function returns false in between we stop the execution of the cycle
            //here, note we do a strong comparison here to avoid constructs like 'false' or null triggering
            if ('undefined' != typeof retVal && retVal === false) {
                return false;
            }

        }
        return true;

    }
});    
