// This line must be the first line in the script

//PROBLEMS:
//1. ApiRequestAuditEntriesFlow produce a very large log, especially if the 
//   maxReturn parameter is large and the flow is run multiple time.
//2. When user create a post and he does not leave the post screen. The event bus
//   request continuously to the wireservice and produce a lot of EnvelopeStatuses log.
//3. Need to be able to filter the ApiRequesAuditEntry by the flow type and fsAltFinished ?
//4. Need to add filter by user , http status , method , external entity status for 
//   ExternalApiMethodCallAuditEntry
//5. If user do not select the configured category , no call is made to the wireservice and no log.
//6. Do we need the user information such user roles ?
//7. Need to log the exception and exception type in ApiRequestAuditEntry
//8. Need a more flexible way to query the log with different parameters.
//9. “Farreach.es is broken.” - current state of customer’s account ? Which flow return this information
//10. Should we include the user email in the log to run stattistical analysis for the overall report. 
//    Security problem
//  Basically, we need a more flexible log query and categorized log to produce a good report
 
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

def createTmpKey = { 
    userEmail ->
    printTaskInfo "Create the temporaty api key for the customer " + userEmail ;
    setApiVersion("suv1");
    request("SuApiKeyFlow", ["fsRenderResult":"json", "email": userEmail, "reasonForAccess": "Inspect the customer problem"]);
    def data = getResponseData();
    
    def userTmpApiKey = null ;
    if(data instanceof JSONArray && data.length() == 1) {
      userTmpApiKey = data.getString(0) ;
    } else {
        println "An error when creating a temporary api key for the user " + userEmail ;
        prettyPrintResponse();
    }
    println "Temporary Key: " + userTmpApiKey ;
    return userTmpApiKey ;
}

def printAvailableExternalServices = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
    
    printTaskInfo "Available External Services(Social Services)"
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
}

def printAvailableCategories = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
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
}

def printUserRoles = { 
    apiKey ->
    setApiVersion("suv1");
    setKey(apiKey);
    
    printTaskInfo "User Role And Configuration Information"
    //request("UserRoleInfoFlow", ["fsRenderResult":"json", "email": userEmail]);
    println "TODO: Find an existing or implement an user information flow provider"
    //prettyPrintResponse();
}

def printMessageEndPoints = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
    
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
}

def printApiRequestAuditEntry = {
    apiKey, userEmail, flowTypeFilter, httpStatuses, maxLogReturn ->
    setApiVersion("suv1");
    setKey(apiKey);
    
    printTaskInfo "ApiRequestAuditEntry log"
    request("ApiRequestAuditEntriesFlow", ["fsRenderResult":"json", "email": userEmail, "httpStatusCodeList": "[" + httpStatuses + "]", "maxReturn": maxLogReturn]);
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        for(int i = 0; i < entries.length(); i++) {
            def entry = entries.get(i) ;
            def httpStatusCode = entry.getStringByPath('response.code') ;
            String flowType = entry.getStringByPath('request.parameters.requestPathArray') ;
            if(flowTypeFilter != null) {
                if(!flowType.matches(flowTypeFilter)) {
                    continue ;
                }
            }
            println "---------------------------------------------------------------------------------" ;
            println i + 1 + '. ' + entry.getStringByPath('request.parameters.requestPathArray') ;
            println '  Http Status Code: ' + httpStatusCode ;
            println '  Message: ' + entry.getStringByPath('message') ;
            println "..............................................................................." ;
            println entry.toString(2) ;
            println "----------------------------------------------------------------------------------\n\n" ;
        }
    } else {
        prettyPrintResponse();
    }
}

def printExternalApiMethodCalls = {
    apiKey, userEmail, maxLogReturn ->
    setApiVersion("suv1");
    setKey(apiKey);
    printTaskInfo "ExternalApiMethodCallAuditEntry log"
    request("ExternalApiMethodCallAuditEntriesFlow", ["fsRenderResult":"json"]);
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        def limit = entries.length() 
        if(limit > Integer.parseInt(maxLogReturn)) {
            limit = Integer.parseInt(maxLogReturn) ; 
        }
        for(int i = 0; i < limit; i++) {
            def entry = entries.get(i) ;
            def httpStatusCode = entry.getStringByPath('statusCode') ;
            println entry.toString(2) ;
            println "----------------------------------------------------------------------------------\n\n" ;
        }
    } else {
        println externalApiMethodCallAuditEntries.toString(2);
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

def flowTypeFilter = null 
if (params && params["flowTypeFilter"]) {
    flowTypeFilter = params["flowTypeFilter"] ;
}


printHelp userEmail, httpStatuses, maxLogReturn;

def suApiKey = getKey() ;
def userTmpApiKey = createTmpKey(userEmail) ;


printAvailableExternalServices(userTmpApiKey) ;

printAvailableCategories(userTmpApiKey) ;

printUserRoles(userTmpApiKey) ;

printMessageEndPoints(userTmpApiKey) ;

printApiRequestAuditEntry(userTmpApiKey, userEmail, flowTypeFilter, httpStatuses, maxLogReturn) ;

printExternalApiMethodCalls(suApiKey, userEmail, maxLogReturn) ;