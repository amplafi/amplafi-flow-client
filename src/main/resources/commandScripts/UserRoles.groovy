description "UserRoles", "User Roles", "apiKey=apiKey, publicUri=publicUri";

def apiKey = null ;
if (params && params["apiKey"]) {
    apiKey = params['apiKey'] ;
}

def publicUri = null ;
if (params && params["publicUri"]) {
    publicUri = params["publicUri"] ;
}


setApiVersion("suv1");
setKey(apiKey);

printTaskInfo "User Role And Configuration Information"
request("UserRoleInfoFlow", ["fsRenderResult":"json", "publicUri": publicUri]);
def result = getResponseData() ;
if(result instanceof JSONArray) {
    for(entry in result) {
        def email = entry.opt("defaultEmail") ;
        def fullName = entry.opt("fullName") ;
        def role = entry.opt("role") ;
        println email 
        println "  [" + role + "]" 
        println "  " + fullName 
    }
} else {
    prettyPrintResponse();
}