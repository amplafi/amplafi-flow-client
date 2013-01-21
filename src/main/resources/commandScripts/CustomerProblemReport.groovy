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
println "verbose = " + params["verbose"] ;
if (params&&params["verbose"]) {
    verbose = "true".equals(params["verbose"]) ;
}

println "customerEmail = " + params["customerEmail"] ;
def customerEmail = "admin@amplafi.com"
if (params && params["customerEmail"]) {
    customerEmail = params["customerEmail"] ;
}

println "----------------------------------------------------------------------------------"
println "Create the temporaty api key for the customer " + customerEmail ;
println "----------------------------------------------------------------------------------"

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

//TO_KOSTYA and TO_PAT: Here are the list of the avaliable flows in the apiv1. I would like to run:
//1. List the avaiable social account(ext service) and the configured categories of each social account(ext service)
//TO_TUAN: MessageEndPointListFlow, see how it's used by plugin
//2. Customer role base on the customer email
//TO_TUAN: I'm not sure if we have right flow at the moment. Explore Account* flows.

setApiVersion("apiv1");
println "----------------------------------------------------------------------------------"
println "Account External Service Information"
println "----------------------------------------------------------------------------------"
request("AccountExternalServiceInformation", ["fsRenderResult":"json"]);
prettyPrintResponse();

println "----------------------------------------------------------------------------------"
println "Avaliable Categories"
println "----------------------------------------------------------------------------------"
request("AvailableCategoriesFlow", ["fsRenderResult":"json"]);
prettyPrintResponse();

println "----------------------------------------------------------------------------------"
println "Categories List"
println "----------------------------------------------------------------------------------"
request("CategoriesList", ["fsRenderResult":"json"]);
prettyPrintResponse();

println "----------------------------------------------------------------------------------"
println "ApiRequestAuditEntry log"
println "----------------------------------------------------------------------------------"
setApiVersion("suv1");
def httpStatusCodeList = "[0, 400, 401]" ;
request("ApiRequestAuditEntriesFlow", ["fsRenderResult":"json", "email": customerEmail, "httpStatusCodeList": httpStatusCodeList, "maxReturn": "10"]);
if(verbose) {
    prettyPrintResponse();
} else {
    def entries = getResponseData();
    if(entries instanceof JSONArray) {
        def tabularTmpl = '%1$3s %2$10s %3$35s %4$35s' ;
        println sprintf(tabularTmpl, '#', 'Http Status', 'Path', 'Message') ;
        for(int i = 0; i < entries.length(); i++) {
            def entry = entries.get(i) ;
            def path = entry.getStringByPath("request.parameters.requestPathArray") ;
            def responseHttpStatus = entry.getStringByPath("response.code") ;
            def message = entry.getStringByPath("message") ;
            println sprintf(tabularTmpl, i + 1, responseHttpStatus, path, message) ;
        }
    } else {
        prettyPrintResponse();
    }
}
