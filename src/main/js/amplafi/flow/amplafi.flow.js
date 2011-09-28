// COPIED from amplafi-web on 28 sept 2011 (need to make build mechanism use this file)

// PATM 7/13/2011:
// The part of this file that has constants should be generated automatically from the 
// java definitions. this file will be automatically generated in the future?


// uses jquery a little bit 
// we also have our own private libraries in use. For now, keeping private libraries so that we do not have to think about 
// jquery or closure or ... dependencies.

// NOTE: private functions have leading '_'

if ( typeof(amplafi) == "undefined") {
    amplafi = {}
}
if (typeof(amplafi.flow) == "undefined") {
    amplafi.flow = {}
}
if (typeof(amplafi.flow.popup) == "undefined") {
    // startInFrame calls
    amplafi.flow.popup = {}
}
if (typeof(amplafi.flow.fp) == "undefined") {
//Flow parameter names
    amplafi.flow.fp = {
        completeFlow:'fsCompleteFlow',
        // com.amplafi.web.services.FlowServiceImpl.FS_BACKGROUND_FLOW
        inBackground:'fsInBackground',
        // org.amplafi.flow.FlowConstants.FSNEXT_FLOW
        nextFlow:'fsNextFlow',
        // com.amplafi.core.flows.ExtendedFlowConstants.PUBLIC_URI
        renderResult:'fsRenderResult',
        appearance:'flowAppearance',
        // com.amplafi.core.flows.FlowAppearance.minimized
        minimizedAppearance: 'minimized'
    };
}

if (typeof (amplafi.flow.flowUri) == "undefined") {
    amplafi.flow.flowUri = location.protocol + "//" + location.host + "/flow/";
}

if ( typeof(amplafi.flow._createFlowUri) == "undefined") {
    amplafi.flow._createFlowUri = function(/*String*/flow) {
        return amplafi.flow.flowUri + flow;
    };
}
if (typeof(amplafi.flow._getFlowValue) == "undefined") {
    amplafi.flow._getFlowValue = function(responseObject, parameterName) {
        return responseObject.flowState.fsParameters[parameterName];
    }
}
if ( typeof(amplafi.flow._xhrFlowGet) == "undefined") {
    amplafi.flow._xhrFlowGet = function(/*String*/flowUrl, /*Function*/onload, /*Object*/content) {
        // Summary: Async call a flow.
        content = content || {};
        var af = amplafi.flow;
        content[af.fp.renderResult] = 'json';
        content[af.fp.completeFlow] = 'advance';
        content[af.fp.inBackground] = true;
    
        amplafi.insertion.startLoading();
        $.ajax({
          url: flowUrl,
          data: content, 
          dataType: 'json'}).success(function(response) {
              amplafi.insertion.stopLoading();
              onload.call(this, response);
          }).complete(function(){
              amplafi.insertion.stopLoading();
          });
    };
}
if ( typeof(amplafi.flow._xhrFlowPost) == "undefined") {
    amplafi.flow._xhrFlowPost = function(/*String*/flowUrl, /*Function*/onload, /*Object*/content) {
        // Summary: Async call a flow.
        content = content || {};
        var af = amplafi.flow;
        content[af.fp.renderResult] = 'json';
        content[af.fp.completeFlow] = 'advance';
        content[af.fp.inBackground] = true;
    
        try {
            amplafi.insertion.startLoading();
            // NOTE: for x-domain support we'd need to set handleAs to json (or friends)
            $.post(flowUrl, content, function(response) {
                amplafi.insertion.stopLoading();
                // TODO : need safe eval -- or rather a jsonp response?
                var obj = eval("["+response+"]")[0];
                onload.call(this, obj);
                });
        } catch (e) {
            if (console&&console.debug) console.debug(e);
            amplafi.insertion.stopLoading();
        }
    }
}

if (typeof(amplafi.flow._startInFrame) == "undefined") {
    amplafi.flow._startInFrame = function(/*String*/url, /*Object*/flowParams, /*Boolean*/post) {
        // summary: starts a flow within an iframe
        // NOTE: js/amplafi/utils.js contains a copy of this.
        // PATM : 27 apr 2010 -- if there are extra slashes ( ie. http://localhost:8080//flow.... )
        // there were javascript errors reported and there seem to be problems with cookies.
        // not completely certain about this. I also had FireCookie 1.0.2 running - but turning FC off did not produce definitive behavior.
        
        // TODO: convert to more jquery? 
        var ah = amplafi.html;
        if ( typeof(flowParams) == "undefined") {
            flowParams = {};
        }
        flowParams[amplafi.flow.fp.appearance] = amplafi.flow.fp.minimizedAppearance;
    
        var table = ah.createNode('table', document.body, {
            className: 'amp_floatingFrame'});
        var tr = ah.createNode('tr', table,
            {innerHTML:'<td class="amp_title">' + 'Flow' + '<span class="close" title="Close">x</span></td>'});
        var tr2 = ah.createNode('tr', table);
        var td = ah.createNode('td', tr2);
    
        var ifrm;
        if (post) {
            ifrm = ah.createNode('iframe', td)
            var frDiv = ah.createNode('div', ifrm.contentDocument.documentElement, {innerHTML: ah.doPostHtml(url, flowParams)});
    
            setTimeout(function(){
                    var f=frDiv.getElementsByTagName('form');
                    if(f&&f[0]) f[0].submit();
                }, 50);
        } else {
            ifrm = ah.createNode('iframe', td, { src: ah.doGetUrl(url, flowParams) });
        }
        var closeBtn = tr.getElementsByTagName("span")[0];
        closeBtn.onclick = ah.closeFrame;
    
        var scrollTop = window.pageYOffset || document.documentElement.scrollTop || 0;
        ah.addStyle(table, {top: (40+scrollTop)+'px'});
    
        ah.makeFloating(table, tr);
    }
}

if (typeof(amplafi.flow._closure_startInFrame) == "undefined") {
    amplafi.flow._closure_startInFrame = function(/*String*/url, /*Object*/flowParams){
        var ah = amplafi.html;
        if ( typeof(flowParams) == "undefined") {
            flowParams = {};
        }
        flowParams[amplafi.flow.fp.appearance] = amplafi.flow.fp.minimizedAppearance;
    
        var table = ah.createNode('table', document.body, {
            className: 'amp_floatingFrame'});
        var tr = ah.createNode('tr', table,
            {innerHTML:'<td class="amp_title">' + 'Flow' + '<span class="close" title="Close">x</span></td>'});
        var tr2 = ah.createNode('tr', table);
        var td = ah.createNode('td', tr2);
    
        var ifrm = ah.createNode('iframe', td, { src: ah.doGetUrl(url, flowParams) });
        var closeBtn = tr.getElementsByTagName("span")[0];
        closeBtn.onclick = ah.closeFrame;
    
        var scrollTop = window.pageYOffset || document.documentElement.scrollTop || 0;
        ah.addStyle(table, {top: (40+scrollTop)+'px'});
    
        ah.makeFloating(table, tr);
    };
}

if ( typeof(amplafi.flow.defs) == "undefined") {
	amplafi.flow.defs = {
	    /**
	     *  add the remote call to make the api calls
	     *  @param key hash or string
	     *  @param options if key is string 
	     */
	    postMethod: amplafi.flow._xhrFlowPost,
	    getMethod: amplafi.flow._xhrFlowGet,
	    /**
	     * Creates amplafi.flow.<key>(fn_options) function
	     *   When called, an ajax call is made to the flow on the server.
         * Creates amplafi.flow.popup.<key>(fn_options) function
         *   When called, an popup is created to handle the call.
	     * 
	     * options = {
	     * flowTypeName: flow name if not same as key,
	     * fsMethod : either amplafi.flow.defs.getMethod or amplafi.flow.defs.postMethod
	     * fsParameters : parameters to pass to server
	     * }
	     * @param key
	     * @param options 
	     */
        defineCall: function defineCall(key, options) {
	        if ( typeof(options) == "undefined" ) {
	            // passed a map of multiple definitions.
	            for(var realKey in key) {
	                defineCall(realKey, key[realKey]);
	            }
	        } else {
	            var flowTypeName = options.flowTypeName;
	            if ( typeof(flowTypeName) == "undefined") {
	                // TODO: need to capitalize the first letter.
	                flowTypeName = key;
	            }
	            var lazy = options.lazy;
	            delete options.lazy;
	            var url = amplafi.flow._createFlowUri(flowTypeName);
	            amplafi.flow.defs[key] = options;
    		    amplafi.flow[key] = function(fn_options) {
    		        var calling_options = $.extend({ fsMethod: amplafi.flow.defs.getMethod }, options.fsParameters, fn_options);
    		        if(typeof(calling_options.fsMethod) == "undefined") {
    		            var fsMethod = amplafi.flow.defs.getMethod;
    		        } else {
    		            var fsMethod = calling_options.fsMethod;
    		        }
    		        // null out otherwise $.extend() will attempt to call it when doing a deep merge
    		        calling_options.fsMethod = null;
    		        fsMethod(url, options.success, calling_options);
    		    };
    		    amplafi.flow.popup[key] = function(fn_options) {
                    var calling_options = $.extend( options.fsParameters,  fn_options);
                    calling_options.flow = options.flowTypeName;
//                    // forcing the post method because this is interactive
//    		        amplafi.flow._startInFrame(url, calling_options, true);
    		        amplafi.flow._closure_startInFrame(ampConfig.js.scriptHost + 'misc/closure-app.html', calling_options);
    		    };
    		    if ( typeof("lazy") != "undefined") {
    		        // TODO: define lazy loading 
    		    }
	        }
	    }
	}
}

amplafi.flow.defs.defineCall({
    categoriesListFlow: { flowTypeName: 'CategoriesList',
        success: function(responseObject) {
            var af = amplafi.flow;
            var cats = af._getFlowValue(responseObject, amplafi.flow.fp.availableCategories);
            var vals = amplafi.insertion.data['availableCategories'] = cats;
            if (vals) {
                var categoriesHash = {};
                for (var i=0; i<vals.length; i++) {
                    categoriesHash[vals[i].entityId] = vals[i]; 
                }
                amplafi.insertion.data['categoriesHash'] = categoriesHash;
            }
            
        }
    },
    categoryMessages:{ flowTypeName: 'CategoryMessages', 
        success: function(responseObject) {
            var af = amplafi.flow;
            var envs = $.evalJSON(af._getFlowValue(responseObject,amplafi.flow.fp.broadcastEnvelopes));
            amplafi.insertion.data['envelopes'] = envs;

            var messages = envs.messages;
            var mepId = 'newId';
            var ctx = {messageEndPointLookupKey:mepId};
            var envelopes = [];
            for (var msg_index in messages){
                var msg = messages[msg_index];
                var env = {lookupKey:'ampbe_xx', messageLookupKey:'ampmsg_'+msg.messageId,
                    headline:msg.headline, updateDate:msg.lastUpdate, message:msg.fullText,
                    eventStart:msg.eventStart, eventLocation:msg.eventLocation};
                envelopes.push(env);
            }
            ctx.envelopes = envelopes;

            ctx.beforeText = template.substitutions.beforeText || {};
            ctx.afterText = template.substitutions.afterText || {};

            if (template)
                node.innerHTML = freemarker.render(template.template, ctx);
            else
                node.innerHTML = response;

            amplafi.insertion.startEditing();
        }
    },
    createMessageFlow: { flowTypeName: 'CreateAlert', 
                         fsLinkText :'Configure Reusing Selection',
                         fsContinueLinkText :'(continue) Reusing Selection',
                         fsMethod: amplafi.flow.defs.postMethod },
    websiteMEPCreateFlow: { flowTypeName: 'CreateWebsite',
                             fsLinkText:'Replace and discard or insert after',
                             fsContinueLinkText: 'Modify configuration'},
    editWebPointsFlow: { flowTypeName: 'EditWebPoints'},
    messageEndPointListFlow: { flowTypeName: 'MessageEndPointList', 
        success: function(responseObject) {
            var af = amplafi.flow;            
            var mepsData = af._getFlowValue(responseObject, amplafi.flow.fp.messageEndPointsInfo);
            amplafi.insertion.data['meplist'] = mepsData;

            var meps = amplafi.insertion.data['meplist'];
            if (!meps) {
                return;
            }
            for (var i in amplafi.insertion.state) {
                var stateMep = amplafi.insertion.state[i];
                var initStateMep = amplafi.insertion.initialState[i];
                if (meps[i]) {
                    initStateMep.template = {id:meps[i].template.lookupKey};
                    if (stateMep && !stateMep.template)
                        stateMep.template = initStateMep.template;

                    var topicsObj = meps[i].topics;
                    var topics = [];
                    for (var j in topicsObj) {
                        topics.push(''+topicsObj[j].entityId);
                    }
                    initStateMep.topics = topics;
                    if (stateMep && !stateMep.topics)
                        stateMep.topics = initStateMep.topics;
                }
            }
            for (var i in meps) {
                if (!amplafi.insertion.state[i]) {
                    // mep was not found on page, add it to the paste list!
                    var template = meps[i].template;
                    template.id = template.lookupKey;
                    var topics = [];
                    for (var j=0; j<meps[i].topics.length; j++) {
                        topics[j] = meps[i].topics[j].entityId;
                    }
                    amplafi.insertion.state[i] = {
                            disabled : true,
                            template : template,
                            topics : topics
                    };
                    // also add it to the initial state (as disabled)
                    var clonedTopics = topics.slice(0);
                    amplafi.insertion.initialState[i] = {
                            disabled : true,
                            template : template,
                            topics : clonedTopics
                    };
                }
            }
            
        }},
    templatesListFlow: { flowTypeName: 'TemplatesList', 
        success : function(responseObject) {
            var af = amplafi.flow;            
            var templates = af._getFlowValue(responseObject, amplafi.flow.fp.templates);
            var vals = amplafi.insertion.data['templates'] = templates;
            if (vals) {
                var templatesHash = {};
                for (var i=0; i<vals.length; i++) {
                    templatesHash[vals[i].id] = vals[i]; 
                }
                amplafi.insertion.data['templatesHash'] = templatesHash;
            }
        }
    },
    tipsListFlow: { flowTypeName: 'tipsList',
        success: function(responseObject) {
            var af = amplafi.flow;
            
            var tipsList = af._getFlowValue(responseObject, 'tipsList');
            $.trigger("addTips", {tipsList : tipsList});            
        }
    },
 
    emptyFlow: { flowTypeName: 'Empty'}
    });
    
    