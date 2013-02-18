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
request("SuApiKeyFlow", reqParams);
def data = getResponseData();

def userTmpApiKey = null ;
if(data instanceof JSONArray && data.length() == 1) {
  userTmpApiKey = data.getString(0) ;
} else {
    println "An error when creating a temporary api key for the publicUri " + publicUri ;
    prettyPrintResponse();
}

println "Temporary Key: " + userTmpApiKey ;

return userTmpApiKey ;
