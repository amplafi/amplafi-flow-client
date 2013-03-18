/**
 * Obtains a new api key from the wire server
 */
description "GetPermApiKey", "Generates a new Permanent API key", [paramDef("callbackHost","The host to callback to",true,"example.com")];

emitOutput( "GetTempApiKey : ${callbackHost}" );

// Clear current Key

def tempApiKey = callScript("GetTempApiKey",["apiCall":"PermanentApiKey"]);


//def tempApiKey = getKey();

emitOutput("tempApiKey = " + tempApiKey);

setKey(tempApiKey);

def key = openPort(9123,10,{
    // Send Post Request
    requestPost("PermanentApiKey", ["callbackUri":"http://${callbackHost}:9123/?action=farreaches_server_reply&apiCall=PermanentApiKey&externalUserId=1&farreachesNonce=54127ddbe5",
                                    "action":"farreaches_server_reply",
                                    "apiCall":"PermanentApiKey",
                                    "usersList":"[{'email':'admin@amplafi.com','roleType':'adm','displayName':'user','externalId':1}]",
                                    "temporaryApiKey":tempApiKey,
                                    "logonBroadcastProvider":"2",
                                    "logonBroadcastProviderLookupKey": "bpr_9f688fc3-c5e5-45ce-a05f-681be979d7af",
                                    "security_level": "IDENTIFIED",
                                    "externalUserId": "1",
                                    "defaultLanguage": "en",
                                    "selfName": "user's Blog!",
                                    "requestPathArray": "PermanentApiKey",
                                    "completeList": "true"
                                    ])
    }
    , { request, response ->
            // return response callback
            def permanentKey = request.getParameterMap().get("permanentApiKeys")[0];
            return new JSONObject(permanentKey).get("1");
        }
    )
    
setKey(key);

emitOutput( "NEW KEY IS : " + key );

return key;
