/**
 * Obtains a new api key from the wire server
 */
description "GetPermApiKey", "Generates a new Permanent API key", [paramDef("callbackHost","The host to callback to",true,"example.com")];

emitOutput( "GetTempApiKey : ${callbackHost}" );

// Clear current Key
setKey("");

def tempApiKey = callScript("GetTempApiKey",["apiCall":"PermanentApiKey"])[0];

println "tempApiKey = " + tempApiKey;

def key = openPort(9123, 20,{
	// Send Post Request
	requestPost("PermanentApiKey", ["callbackUri":"http://${callbackHost}:9123/?action=farreaches_server_reply&apiCall=PermanentApiKey&externalUserId=1&farreachesNonce=54127ddbe5",
									"action":"farreaches_server_reply",
									"apiCall":"PermanentApiKey",
									"externalUserId":"1",
									"farreachesNonce":"54127ddbe5",
									"selfName":"user's Blog!",
									"usersList":"[{'email':'daisy@bizpoint.cn','roleType':'adm','displayName':'user','externalId':1}]",
									"completeList":"true",
									"defaultLanguage":"en",
									"security_level": "IDENTIFIED",
									"api_key":tempApiKey,
									"temporaryApiKey":tempApiKey])
}
, { request, response ->
		// return response callback
		return request.getParameterMap().get("temporaryApiKey");
	}
)


emitOutput( "NEW KEY IS : " + key );

return key;




//callbackUri="http://example.com/wordpress/wp-admin/admin-ajax.php?action=farreaches_server_reply"+
//"&apiCall=PermanentApiKey&externalUserId=1&farreachesNonce=54127ddbe5&temporaryApiKey=ampcb_c98e9c33070508da2414c6bd9958f306ab43b34098ef9bb70fdac9d7cb00521d"+
//"&selfName=user's+Blog!&defaultLanguage=en&usersList=[{"email":"daisy@bizpoint.cn","roleType":"adm","displayName":"user","externalId":1}]&completeList=true"