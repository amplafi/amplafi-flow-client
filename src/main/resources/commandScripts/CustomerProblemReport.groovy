// This line must be the first line in the script

description "CustomerProblemReport", "Params: customerEmail=<customer_email> This tool is used to report and identify the customer problem"

// You can set certain globals in the script if you want.
// These will affect all susequent requests
// Don't normally do this though. As the default ones are good.

// setHost("www.google.com");
// setPort("90");
// setApiVersion("apiv12");
// setKey("newkey");

//def customerEmail = "admin@amplafi.com" ;
def customerEmail = "tuan08@gmail.com"

if (params&&params["customerEmail"]){
    customerEmail = params["customerEmail"]
}

println "Find the api key for the customer " + customerEmail ;

setApiVersion("suv1");
request("SuApiKeyFlow", ["fsRenderResult":"json", "email": customerEmail, "reasonForAccess": "Inspect the customer problem"]);
def data = getResponseData();

def apiKey = null ;
if(data instanceof JSONArray && data.length() == 1) {
  apiKey = data.getString(0) ;
}

println "API Key for the customer " + customerEmail + ": "+ apiKey ;

if(apiKey == null) {
  System.exit(0) ;
}

setApiVersion("suv1");
def httpStatus = "[400]" ;
request("ApiRequestAuditEntriesFlow", ["fsRenderResult":"json", "filterBy": "email", "email": customerEmail, "httpStatus": httpStatus]);
prettyPrintResponse();
