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
    userEmail, apiHttpStatuses, apiMaxReturn ->
    printTaskInfo "CustomerProblemReport Script Help"
    println "This CustomerProblemReport script use to help the system administrator access"
    println "the various farreaches service configuration and user log information"
    println "Params: "
    println "  userEmail=<" + userEmail+ ">  The user that you want to inspect"
    println ""
    println "  apiHttpStatuses=<" + apiHttpStatuses + ">  The http status code list, empty to select all."
    println "  apiFlowType=<flowType>  The flow type."
    println "  apiMaxReturn=<" + apiMaxReturn + ">  The maximum number of the log entries that you want to return"
    println ""
    println "  externalApiNamespace=<twitter.com, facebook.com...>  The external service namespace"
    println "  externalApiMethod=<ADD_MESSAGE...>  The external method call name"
    println "  externalApiMaxReturn=<number>  The maximum number of the log entries that you want to return"
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
    apiKey, userEmail ->
    setApiVersion("suv1");
    setKey(apiKey);
    
    printTaskInfo "User Role And Configuration Information"
    request("UserRoleInfoFlow", ["fsRenderResult":"json", "email": userEmail]);
    println "User " + userEmail + " has the following roles:"
    prettyPrintResponse();
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
    apiKey, userEmail, apiFlowType, apiHttpStatuses, apiMaxReturn ->
    setApiVersion("suv1");
    setKey(apiKey);
    def reqParams = ["fsRenderResult":"json", "httpStatusCodeList": "[" + apiHttpStatuses + "]", "maxReturn": apiMaxReturn] ;
    if(userEmail != null) {
        reqParams["email"] = userEmail;
    } 
    if(apiFlowType != null) {
        reqParams["flowType"] = apiFlowType;
    } 
    printTaskInfo "ApiRequestAuditEntry log"
    request("ApiRequestAuditEntriesFlow", reqParams);
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        for(int i = 0; i < entries.length(); i++) {
            def entry = entries.get(i) ;
            def httpStatusCode = entry.getStringByPath('response.code') ;
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
    apiKey, userEmail, apiExternalNamespace, apiExternalMethod, apiExternalMaxReturn ->
    setApiVersion("suv1");
    setKey(apiKey);
    printTaskInfo "ExternalApiMethodCallAuditEntry log"
    def reqParams = ["fsRenderResult":"json", "maxReturn": apiExternalMaxReturn] ;
    if(apiExternalNamespace != null) {
        reqParams["apiExternalNamespace"] = apiExternalNamespace ;
    }
    if(apiExternalMethod != null) {
        reqParams["method"] = apiExternalMethod ;
    }
    request("ExternalApiMethodCallAuditEntriesFlow", reqParams);
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        for(int i = 0; i < entries.length(); i++) {
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

def apiHttpStatuses = "0, 400, 401"
if (params && params["apiHttpStatuses"]) {
    apiHttpStatuses = params["apiHttpStatuses"] ;
}

def apiMaxReturn = "5"
if (params && params["apiMaxReturn"]) {
    apiMaxReturn = params["apiMaxReturn"] ;
}

def apiFlowType = null 
if (params && params["apiFlowType"]) {
    apiFlowType = params["apiFlowType"] ;
}

def apiExternalNamespace = null ;
def apiExternalMethod = null ;
def apiExternalMaxReturn = "10" ;

printHelp userEmail, apiHttpStatuses, apiMaxReturn;

def suApiKey = getKey() ;
def userTmpApiKey = createTmpKey(userEmail) ;


printAvailableExternalServices(userTmpApiKey) ;

printAvailableCategories(userTmpApiKey) ;

printUserRoles(suApiKey, userEmail) ;

printMessageEndPoints(userTmpApiKey) ;

printApiRequestAuditEntry(suApiKey, userEmail, apiFlowType, apiHttpStatuses, apiMaxReturn) ;

printExternalApiMethodCalls(suApiKey, userEmail, apiExternalNamespace, apiExternalMethod, apiExternalMaxReturn) ;