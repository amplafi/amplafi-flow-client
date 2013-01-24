// This line must be the first line in the script

description "CustomerProblemReport", "Params: userEmail=<userEmail> This tool is used to report and identify the customer problem" ;

def printTaskInfo = { 
    info ->
    println "\n"
    println "*********************************************************************************"
    println info ;
    println "*********************************************************************************"
}

def printHelp = { 
    userEmail, httpStatuses, maxLogReturn ->
    printTaskInfo "CustomerProblemReport Script Help"
    println "This CustomerProblemReport script use to help the system administrator access"
    println "the various farreaches service configuration and user log information"
    println "Params: "
    println "  userEmail=<" + userEmail+ ">  The user that you want to inspect"
    println "  httpStatuses=<" + httpStatuses + ">  The http status code list, empty to select all."
    println "  maxLogReturn=<" + maxLogReturn + ">  The maximum number of the log entries that you want to return"
}

def printTabular = { 
    entries, tabularTmpl, headers, keyPaths ->
    println sprintf(tabularTmpl, headers) ;
    println "-----------------------------------------------------------------------------------------"
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def value = new String[keyPaths.size() + 1] ;
        value[0] = Integer.toString(i + 1) ;
        for(int j = 0; j < value.length - 1; j++) {
            value[j + 1] = entry.getStringByPath(keyPaths[j]) ;
        }
        println sprintf(tabularTmpl, value) ;
    }
}

def userEmail = "admin@amplafi.com"
if (params && params["userEmail"]) {
    userEmail = params["userEmail"] ;
}

def httpStatuses = "0, 400, 401"
if (params && params["httpStatuses"]) {
    httpStatuses = params["httpStatuses"] ;
}

def maxLogReturn = "5"
if (params && params["maxLogReturn"]) {
    maxLogReturn = params["maxLogReturn"] ;
}

printHelp userEmail, httpStatuses, maxLogReturn;

printTaskInfo "Create the temporaty api key for the customer " + userEmail ;

setApiVersion("suv1");
request("SuApiKeyFlow", ["fsRenderResult":"json", "email": userEmail, "reasonForAccess": "Inspect the customer problem"]);
def data = getResponseData();

def apiKey = null ;
if(data instanceof JSONArray && data.length() == 1) {
  apiKey = data.getString(0) ;
}

if(apiKey == null) {
    println "An error when creating a temporary api key for the user " + userEmail ;
    prettyPrintResponse();
    System.exit(0) ;
} else {
    println "Temporary Api Key: " + apiKey ;
}

setApiVersion("apiv1");

printTaskInfo "Available Social Services(External Services)"
request("EligibleExternalServiceInstancesFlow", ["fsRenderResult":"json"]);
if(getResponseData() instanceof JSONArray) {
    def entries = getResponseData();
    String tabularTmpl = '%1$3s %2$20s' ;
    def headers =  ['#', 'Ext Service'] ;
    def keyPaths = [     'name'] ;
    printTabular(entries, tabularTmpl, headers, keyPaths)
} else {
    prettyPrintResponse();
}

printTaskInfo "Avaliable Categories"
request("AvailableCategoriesFlow", ["fsRenderResult":"json"]);

if(getResponseData() instanceof JSONArray) {
    def entries = getResponseData();
    String tabularTmpl = '%1$3s%2$20s%3$20s' ;
    def headers =  ['#', 'Topic Id', 'Name'] ;
    def keyPaths = ['entityId', 'name'] ;
    printTabular(entries, tabularTmpl, headers, keyPaths)
} else {
    prettyPrintResponse();
}

printTaskInfo "User Role And Configuration Information"

println "TODO: Find an existing or implement an user information flow provider"

printTaskInfo "Message End Point List Flow"
request("MessageEndPointListFlow", ["fsRenderResult":"json", "messageEndPointCompleteList": "true"]);
if(getResponseData() instanceof JSONArray) {
    def entries = getResponseData();
    String tabularTmpl = '%1$3s%2$15s%3$15s%4$15s%5$15s' ;
    def headers =  ['#', 'Ext Service', 'User Name', 'Full Name', 'Topic Ids'] ;
    def keyPaths = ['externalServiceDefinition', 'extServiceUsername', 'extServiceUserFullName', 'selectedTopics'] ;
    printTabular(entries, tabularTmpl, headers, keyPaths)
} else {
    prettyPrintResponse();
}


printTaskInfo "ApiRequestAuditEntry log"
setApiVersion("suv1");
request("ApiRequestAuditEntriesFlow", ["fsRenderResult":"json", "email": userEmail, "httpStatusCodeList": "[" + httpStatuses + "]", "maxReturn": maxLogReturn]);
def entries = getResponseData();
if(entries instanceof JSONArray) {
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def httpStatusCode = entry.getStringByPath('response.code') ;
        println "---------------------------------------------------------------------------------" ;
        println i + 1 + '. ' + entry.getStringByPath('request.parameters.requestPathArray') ;
        println '  Http Status Code: ' + httpStatusCode ;
        println '  Message: ' + entry.getStringByPath('message') ;
        if(!"200".equals(httpStatusCode)) {
            println "..............................................................................." ;
            println entry.toString(2) ;
        }
        println "----------------------------------------------------------------------------------\n\n" ;
    }
} else {
    prettyPrintResponse();
}