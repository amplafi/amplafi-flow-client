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
		return request.getParameterMap().get("temporaryApiKey");
	}
)


emitOutput( "NEW KEY IS : " + key );

return key;
