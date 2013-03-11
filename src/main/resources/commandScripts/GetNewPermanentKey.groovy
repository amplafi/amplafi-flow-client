/**
 * Obtains a new api key from the wire server
 */
description "GetPermApiKey", "Generates a new Permanent API key", [paramDef("callbackHost","The host to callback to",true,"example.com")];

emitOutput( "GetTempApiKey : ${callbackHost}" );

// Clear current Key
setKey("");

def tempApiKey = callScript("GetTempApiKey",["apiCall":"PermanentApiKey"])[0];

println "tempApiKey = " + tempApiKey;

setKey(tempApiKey);

def key = openPort(9123,10,{
	// Send Post Request
	requestPost("PermanentApiKey", ["callbackUri":"http://${callbackHost}:9123/?action=farreaches_server_reply&apiCall=PermanentApiKey&externalUserId=1&farreachesNonce=54127ddbe5",
									"action":"farreaches_server_reply",
									"apiCall":"PermanentApiKey",
									"usersList":"[{'email':'daisy@bizpoint.cn','roleType':'adm','displayName':'user','externalId':1}]",
									"temporaryApiKey":tempApiKey
									])
	}
	, { request, response ->
			// return response callback
			def permanentKey = request.getParameterMap().get("permanentApiKeys")[0];
			return new JSONObject(permanentKey).get("1");
		}
	)

emitOutput( "NEW KEY IS : " + key );

return key;
