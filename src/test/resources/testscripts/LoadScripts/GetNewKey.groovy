/**
 * Obtains a new api key from the wire server
 */
description "GetApiKey", "Generates a new API key", [paramDef("callbackHost","The host to callback to",true,"sandbox.farreach.es")];

// Clear current Key
setKey("");

def key = openPort(9123, 20,{
	// Send Post Request
	requestPost("TemporaryApiKey", ["callbackUri":"http://${callbackHost}:9123/",
									"apiCall":"PermanentApiKey"])

}
, { request, response ->
		// return response callback
		return request.getParameterMap().get("temporaryApiKey");
	}
)


emitOutput( "NEW KEY IS : " + key );

return key;
