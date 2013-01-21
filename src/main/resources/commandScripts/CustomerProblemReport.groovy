// This line must be the first line in the script

description "CustomerProblemReport", "Params: customerEmail=<customer_email> This tool is used to report and identify the customer problem"

// You can set certain globals in the script if you want.
// These will affect all susequent requests
// Don't normally do this though. As the default ones are good.

// setHost("www.google.com");
// setPort("90");
// setApiVersion("apiv12");
// setKey("newkey");

def verbose = false ; 
if (params && params["verbose"]) {
    verbose = params["verbose"] ;
}

//TO_TUAN wtf? Why defult to this email?
def customerEmail = "tuan08@gmail.com"
if (params && params["customerEmail"]) {
    customerEmail = params["customerEmail"] ;
}

println "Find the api key for the customer " + customerEmail ;

setApiVersion("suv1");
request("SuApiKeyFlow", ["fsRenderResult":"json", "email": customerEmail, "reasonForAccess": "Inspect the customer problem"]);
def data = getResponseData();

def apiKey = null ;
if(data instanceof JSONArray && data.length() == 1) {
  apiKey = data.getString(0) ;
}

if(apiKey == null) {
    println "An error when creating a temporary api key for the user " + customerEmail ;
    prettyPrintResponse();
    System.exit(0) ;
}

println "Create a temporary api key for the user " + customerEmail + ": " + apiKey ;

setApiVersion("suv1");
def httpStatusCodeList = "[0, 400, 401]" ;
request("ApiRequestAuditEntriesFlow", ["fsRenderResult":"json", "email": customerEmail, "httpStatusCodeList": httpStatusCodeList]);
if(verbose) {
    prettyPrintResponse();
} else {
    def entries = getResponseData();
    if(entries instanceof JSONArray) {
        println sprintf('%1$3s %2$10s %3$30s %4$30s', '#', 'Http Status', 'Path', 'Message') ;
        for(int i = 0; i < entries.length(); i++) {
            def entry = entries.get(i) ;
            def path = entry.getStringByPath("request.parameters.requestPathArray") ;
            def responseHttpStatus = entry.getStringByPath("response.code") ;
            def message = entry.getStringByPath("message") ;
            println sprintf('%1$3s %2$10s %3$30s %4$30s', i + 1, responseHttpStatus, path, message) ;
        }
    } else {
        prettyPrintResponse();
    }
}
