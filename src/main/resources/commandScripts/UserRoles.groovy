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
def response = requestResponse("UserRoleInfo", ["fsRenderResult":"json", "publicUri": publicUri]);

checkError(response) ;

for(entry in response.getResponseAsJSONArray()) {
    def email = entry.opt("defaultEmail") ;
    def fullName = entry.opt("fullName") ;
    def role = entry.opt("role") ;
    println email 
    println "  [" + role + "]" 
    println "  " + fullName 
}