/**
 * Obtains a new api key from the wire server
 */
description "GetTempApiKey", "Generates a new API key", [paramDef("callbackHost","The host to callback to",true,"example.com"),
                                                     paramDef("apiCall","The api that we want to use. e.g. PermanentApiKey",false,"-")];

emitOutput( "GetTempApiKey : ${callbackHost}  ${apiCall} " );

// Clear current Key
setKey("");

def key = openPort(9123, 10,{
    // Send Post Request
    requestPost("TemporaryApiKey", ["callbackUri":"http://${callbackHost}:9123/",
                                    "apiCall":apiCall])
}
, { request, response ->
        // return response callback
        def keyStr = request.getParameterMap().get("temporaryApiKey");
        def keyData = null;
        if (keyStr) {
            keyData = new JSONArray(keyStr[0]);
            println "keyData.length() = " + keyData.length();
            keyData = keyData.get(0);
        }
        return keyData;
    }
);

emitOutput( "NEW TEMP KEY IS : " + key );

//set temp key as a apiKey for a permenant key request. 
setKey(key);

return key;
