description "UserRoles", "User Roles", [paramDef("apiKey","",false,null),paramDef("publicUri","",false,null)];

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