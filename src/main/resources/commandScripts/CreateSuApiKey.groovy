description "CreateSuApiKey", "Generates a new SU API key", "userEmail=<admin email> publicUri=<customerUri>";


def userEmail = "admin@amplafi.com" ;
if (params && params["userEmail"]) {
    userEmail = params["userEmail"] ;
}

def publicUri = null ;
if (params && params["publicUri"]) {
    publicUri = params["publicUri"] ;
}


printTaskInfo "Create the temporaty api key for the customer " + publicUri ;
setApiVersion("suv1");
def reqParams = ["fsRenderResult":"json", "reasonForAccess": "Inspect the customer problem", "publicUri": publicUri] ;
reqParams["email"] = userEmail;
def response = requestResponse("SuApiKeyFlow", reqParams);

if(response.hasError()) {
    println "An error when creating a temporary api key for the publicUri " + publicUri ;
    println response.getErrorMessage() ;
    System.exit(1) ;
}

def userTmpApiKey = response.getResponseAsJSONArray().getString(0) ;

println "Temporary Key: " + userTmpApiKey ;

return userTmpApiKey ;
