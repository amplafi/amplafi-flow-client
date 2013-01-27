// This line must be the first line in the script

//TODO:
//1. “Farreach.es is broken.” - current state of customer’s account ? Which flow return this information
//2. When user create a post and he does not leave the post screen. The event bus
//   request continuously to the wireservice and produce a lot of EnvelopeStatuses log(TO_YURIY)
//3. Need to log the exception and exception type in ApiRequestAuditEntry

import java.util.HashMap 
 
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
    println "  userEmail=<" + userEmail+ ">  The user that you want to inspect. If the userEmail is not specified. The overall report will be run"
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

def printOverallApiRequestAuditEntry = {
    apiKey, apiFlowType, apiHttpStatuses, apiMaxReturn ->
    setApiVersion("suv1");
    setKey(apiKey);
    def reqParams = ["fsRenderResult":"json", "maxReturn": apiMaxReturn] ;
    if(apiFlowType != null) {
        reqParams["flowType"] = apiFlowType;
    } 
    if(apiHttpStatuses != null) {
        reqParams["apiStatusCodeList"] = "[" + apiHttpStatuses + "]";
    }
    printTaskInfo "Overall Report For ApiRequestAuditEntry Log"
    
    request("ApiRequestAuditEntriesFlow", reqParams);
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        def byUserEmailStatistic = [:] ;
        def byFlowTypeStatistic = [:] ;
        for(int i = 0; i < entries.length(); i++) {
            def entry = entries.get(i) ;
            def email = entry.getStringByPath('request.defaultEmail') ;
            def flowType = entry.getStringByPath('request.parameters.requestPathArray') ;
            def httpStatusCode = Integer.parseInt(entry.getStringByPath('response.code')) ;
            
            if(byUserEmailStatistic[email] == null) {
                byUserEmailStatistic[email] = [
                   "User": email, "Http 1xx": 0, "Http 200": 0, "Http 2xx": 0, "Http 3xx": 0, "Http 4xx": 0, "Http 5xx": 0
                ]; 
            }
            
            if(byFlowTypeStatistic[flowType] == null) {
                byFlowTypeStatistic[flowType] = [
                   "Flow Type": flowType, "Http 1xx": 0, "Http 200": 0, "Http 2xx": 0, "Http 3xx": 0, "Http 4xx": 0, "Http 5xx": 0
                ]; 
            }
            
            if(httpStatusCode < 200) {
                byUserEmailStatistic[email]["Http 1xx"] += 1; 
                byFlowTypeStatistic[flowType]["Http 1xx"] += 1; 
            } else if(httpStatusCode == 200) {
                byUserEmailStatistic[email]["Http 200"] += 1; 
                byFlowTypeStatistic[flowType]["Http 200"] += 1; 
            } else if(httpStatusCode > 200 && httpStatusCode < 300) {
                byUserEmailStatistic[email]["Http 2xx"] += 1; 
                byFlowTypeStatistic[flowType]["Http 2xx"] += 1; 
            } else if(httpStatusCode >= 300 && httpStatusCode <400) {
                byUserEmailStatistic[email]["Http 3xx"] += 1; 
                byFlowTypeStatistic[flowType]["Http 3xx"] += 1; 
            } else if(httpStatusCode >= 400 && httpStatusCode < 500) {
                byUserEmailStatistic[email]["Http 4xx"] += 1; 
                byFlowTypeStatistic[flowType]["Http 4xx"] += 1; 
            } else {
                byUserEmailStatistic[email]["Http 5xx"] += 1; 
                byFlowTypeStatistic[flowType]["Http 5xx"] += 1; 
            }
        }
        
        String tabularTmpl = '%1$-40s%2$10s%3$10s%4$10s%5$10s%6$10s%7$10s' ;
        def headers =  ['User', 'Http 1xx', 'Http 200', 'Http 2xx', 'Http 3xx', 'Http 4xx', 'Http 5xx'] ;
        println sprintf(tabularTmpl, headers) ;
        println '-----------------------------------------------------------------------------------------------------'
        for(entry in byUserEmailStatistic.values()) {
            println sprintf(tabularTmpl, entry['User'], entry['Http 1xx'], entry['Http 200'], entry['Http 2xx'], entry['Http 3xx'], entry['Http 4xx'], entry['Http 5xx']);
        }
        println "\n\n"
        headers =  ['Flow', 'Http 1xx', 'Http 200', 'Http 2xx', 'Http 3xx', 'Http 4xx', 'Http 5xx'] ;
        println sprintf(tabularTmpl, headers) ;
        println '-----------------------------------------------------------------------------------------------------'
        for(entry in byFlowTypeStatistic.values()) {
            println sprintf(tabularTmpl, entry['Flow Type'], entry['Http 1xx'], entry['Http 200'], entry['Http 2xx'], entry['Http 3xx'], entry['Http 4xx'], entry['Http 5xx']);
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

def userEmail = null ;
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

if(userEmail != null) {
    printTaskInfo "Running the customer problem report for the customer " + userEmail
    def suApiKey = getKey() ;
    def userTmpApiKey = createTmpKey(userEmail) ;
    printAvailableExternalServices(userTmpApiKey) ;
    printAvailableCategories(userTmpApiKey) ;
    printUserRoles(suApiKey, userEmail) ;
    printMessageEndPoints(userTmpApiKey) ;
    printApiRequestAuditEntry(suApiKey, userEmail, apiFlowType, apiHttpStatuses, apiMaxReturn) ;
    printExternalApiMethodCalls(suApiKey, userEmail, apiExternalNamespace, apiExternalMethod, apiExternalMaxReturn) ;
} else {
    def suApiKey = getKey() ;
    printAvailableCategories(suApiKey) ;
    printAvailableExternalServices(suApiKey) ;
    printOverallApiRequestAuditEntry(suApiKey, apiFlowType, apiHttpStatuses, apiMaxReturn) ;
}