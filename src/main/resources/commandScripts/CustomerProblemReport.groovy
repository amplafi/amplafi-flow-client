// This line must be the first line in the script


/**
 * 
 * TODO:
 * 1. “Farreach.es is broken.” - current state of customer’s account ?
 * 2. When user create a post and he does not leave the post screen. The event bus
 *    request continuously to the wireservice and produce a lot of EnvelopeStatuses log(TO_YURIY)
 * 3. Need to log the exception and exception type in ApiRequestAuditEntry?
 * 4. Should we move the statistic report to flow provider. A big max return parameter can cause the out of memory exception?
 */


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
    println "  verbose=<true/false>  To print detail json log"
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

def printApiByUserEmailStatistic = {
    entries ->
    def byUserEmailStatistic = [:] ;
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def email = entry.getStringByPath('request.defaultEmail') ;
        def httpStatusCode = Integer.parseInt(entry.getStringByPath('response.code')) ;
        
        if(byUserEmailStatistic[email] == null) {
            byUserEmailStatistic[email] = [
               "User": email, "Http 1xx": 0, "Http 200": 0, "Http 2xx": 0, "Http 3xx": 0, "Http 4xx": 0, "Http 5xx": 0
            ]; 
        }
        
        if(httpStatusCode < 200) {
            byUserEmailStatistic[email]["Http 1xx"] += 1; 
        } else if(httpStatusCode == 200) {
            byUserEmailStatistic[email]["Http 200"] += 1; 
        } else if(httpStatusCode > 200 && httpStatusCode < 300) {
            byUserEmailStatistic[email]["Http 2xx"] += 1; 
        } else if(httpStatusCode >= 300 && httpStatusCode <400) {
            byUserEmailStatistic[email]["Http 3xx"] += 1; 
        } else if(httpStatusCode >= 400 && httpStatusCode < 500) {
            byUserEmailStatistic[email]["Http 4xx"] += 1; 
        } else {
            byUserEmailStatistic[email]["Http 5xx"] += 1; 
        }
    }
    
    String tabularTmpl = '%1$-40s%2$10s%3$10s%4$10s%5$10s%6$10s%7$10s' ;
    def headers =  ['User', 'Http 1xx', 'Http 200', 'Http 2xx', 'Http 3xx', 'Http 4xx', 'Http 5xx'] ;
    println sprintf(tabularTmpl, headers) ;
    println '-----------------------------------------------------------------------------------------------------'
    for(entry in byUserEmailStatistic.values()) {
        println sprintf(tabularTmpl, entry['User'], entry['Http 1xx'], entry['Http 200'], entry['Http 2xx'], entry['Http 3xx'], entry['Http 4xx'], entry['Http 5xx']);
    }
}

def printApiByFlowTypeStatistic = {
    entries ->
    def byFlowTypeStatistic = [:] ;
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def flowType = entry.getStringByPath('request.parameters.requestPathArray') ;
        def httpStatusCode = Integer.parseInt(entry.getStringByPath('response.code')) ;
        
        if(byFlowTypeStatistic[flowType] == null) {
            byFlowTypeStatistic[flowType] = [
               "Flow Type": flowType, "Http 1xx": 0, "Http 200": 0, "Http 2xx": 0, "Http 3xx": 0, "Http 4xx": 0, "Http 5xx": 0
            ]; 
        }
        
        if(httpStatusCode < 200) {
            byFlowTypeStatistic[flowType]["Http 1xx"] += 1; 
        } else if(httpStatusCode == 200) {
            byFlowTypeStatistic[flowType]["Http 200"] += 1; 
        } else if(httpStatusCode > 200 && httpStatusCode < 300) {
            byFlowTypeStatistic[flowType]["Http 2xx"] += 1; 
        } else if(httpStatusCode >= 300 && httpStatusCode <400) {
            byFlowTypeStatistic[flowType]["Http 3xx"] += 1; 
        } else if(httpStatusCode >= 400 && httpStatusCode < 500) {
            byFlowTypeStatistic[flowType]["Http 4xx"] += 1; 
        } else {
            byFlowTypeStatistic[flowType]["Http 5xx"] += 1; 
        }
    }
    
    String tabularTmpl = '%1$-40s%2$10s%3$10s%4$10s%5$10s%6$10s%7$10s' ;
    def headers =  ['Flow', 'Http 1xx', 'Http 200', 'Http 2xx', 'Http 3xx', 'Http 4xx', 'Http 5xx'] ;
    println sprintf(tabularTmpl, headers) ;
    println '-----------------------------------------------------------------------------------------------------'
    for(entry in byFlowTypeStatistic.values()) {
        println sprintf(tabularTmpl, entry['Flow Type'], entry['Http 1xx'], entry['Http 200'], entry['Http 2xx'], entry['Http 3xx'], entry['Http 4xx'], entry['Http 5xx']);
    }
}

def printApiRequestAuditEntry = {
    apiKey, userEmail, apiFlowType, apiHttpStatuses, apiMaxReturn, verbose ->
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
        printApiByFlowTypeStatistic(entries) ;
        if(verbose) {
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
        }
    } else {
        prettyPrintResponse();
    }
}


def printOverallReportApiRequestAuditEntry = {
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
        printApiByUserEmailStatistic(entries) ;
        println '\n\n'
        printApiByFlowTypeStatistic(entries) ;
    } else {
        prettyPrintResponse();
    }
}

def printExternalApiDetailMethodCalls = {
    entries ->
    def dateFormater = new java.text.SimpleDateFormat('DD/MM/yy@hh:mm:ss') ;
    String tabularTmpl = '%1$17s %2$-15s %3$20s %4$5s %5$10s %6$-50s' ;
    def headers =  ['Date', 'Namespace', 'Method', 'EES', 'Http Status', 'Message'] ;
    println '----------------------------------------------------------------------------------------------'
    println 'External Api Detail Calls'
    println '----------------------------------------------------------------------------------------------'
    println sprintf(tabularTmpl, headers) ;
    println '----------------------------------------------------------------------------------------------'
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def timeInMillis = Long.parseLong(entry.getStringByPath('createTime.timeInMillis')) ;
        def date = dateFormater.format(new Date(timeInMillis)) ;
        def namespace = entry.getStringByPath('externalServiceDefinitionNamespace') ;
        def method = entry.getStringByPath('method') ;
        def externalEntityStatus = entry.getStringByPath('externalEntityStatus') ;
        def httpStatusCode = entry.getStringByPath('statusCode') ;
        def message = entry.getStringByPath('message') ;
        if(message != null) {
            message = message.replace("\n", " ") ;
        }
        println sprintf(tabularTmpl, date, namespace, method, externalEntityStatus, httpStatusCode, message) ;
    }
}

def printExternalStatisticMethodCalls = {
    entries ->
    def namespaceHolder = [:] ;
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def namespace = entry.getStringByPath('externalServiceDefinitionNamespace') ;
        def httpStatusCode = Integer.parseInt(entry.getStringByPath('statusCode')) ;
        def method = entry.getStringByPath('method') ;
        if(namespaceHolder[namespace] == null) {
           namespaceHolder[namespace] = [:]  ;
        }
        
        if(namespaceHolder[namespace][method] == null) {
            namespaceHolder[namespace][method] = [
               "method": method, "Http 1xx": 0, "Http 200": 0, "Http 2xx": 0, "Http 3xx": 0, "Http 4xx": 0, "Http 5xx": 0
            ]  ;
        }
        
        if(httpStatusCode < 200) {
            namespaceHolder[namespace][method]["Http 1xx"] += 1; 
        } else if(httpStatusCode == 200) {
            namespaceHolder[namespace][method]["Http 200"] += 1; 
        } else if(httpStatusCode > 200 && httpStatusCode < 300) {
            namespaceHolder[namespace][method]["Http 2xx"] += 1; 
        } else if(httpStatusCode >= 300 && httpStatusCode <400) {
            namespaceHolder[namespace][method]["Http 3xx"] += 1; 
        } else if(httpStatusCode >= 400 && httpStatusCode < 500) {
            namespaceHolder[namespace][method]["Http 4xx"] += 1; 
        } else {
            namespaceHolder[namespace][method]["Http 5xx"] += 1; 
        }
        
    }
    String tabularTmpl = '%1$-25s%2$10s%3$10s%4$10s%5$10s%6$10s%7$10s' ;
    def headers =  ['Method', 'Http 1xx', 'Http 200', 'Http 2xx', 'Http 3xx', 'Http 4xx', 'Http 5xx'] ;
    for(namespace in namespaceHolder.entrySet()) {
        def name = namespace.getKey() ;
        def entry = namespace.getValue() ;
        println '-----------------------------------------------------------------------------------------------------' ;
        println "Statistic for " + name ;
        println '-----------------------------------------------------------------------------------------------------' ;
        println sprintf(tabularTmpl, headers) ;
        println '-----------------------------------------------------------------------------------------------------' ;
        for(method in entry.values()) {
          println sprintf(tabularTmpl, method['method'], method['Http 1xx'], method['Http 200'], method['Http 2xx'], method['Http 3xx'], method['Http 4xx'], method['Http 5xx']);
        }
        println "\n\n" ;
    }
}

def printExternalApiMethodCalls = {
    apiKey, userEmail, externalApiNamespace, externalApiMethod, externalApiMaxReturn, verbose ->
    setApiVersion("suv1");
    setKey(apiKey);
    printTaskInfo "ExternalApiMethodCallAuditEntry log"
    def reqParams = ["fsRenderResult":"json", "maxReturn": externalApiMaxReturn] ;
    if(externalApiNamespace != null) {
        reqParams["namespace"] = externalApiNamespace ;
    }
    if(externalApiMethod != null) {
        reqParams["method"] = externalApiMethod ;
    }
    request("ExternalApiMethodCallAuditEntriesFlow", reqParams);
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        printExternalStatisticMethodCalls(entries) ;
        println "\n\n" ;
        printExternalApiDetailMethodCalls(entries) ;
        if(verbose) {
            for(int i = 0; i < entries.length(); i++) {
                def entry = entries.get(i) ;
                println entry.toString(2) ;
                println "----------------------------------------------------------------------------------\n\n" ;
            }
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

def externalApiNamespace = null ;
if (params && params["externalApiNamespace"]) {
    externalApiNamespace = params['externalApiNamespace'] ;

}
def externalApiMethod = null ;
if (params && params["externalApiMethod"]) {
    externalApiMethod = params['externalApiMethod'] ;
}
def externalApiMaxReturn = "10" ;
if (params && params["externalApiMaxReturn"]) {
    externalApiMaxReturn = params['externalApiMaxReturn'] ;
}

def verbose = false; 
if (params && params["verbose"]) {
    verbose = "true".equals(params["verbose"]) ;
}

printHelp userEmail, apiHttpStatuses, apiMaxReturn;

if(userEmail != null) {
    printTaskInfo "Running the customer problem report for the customer " + userEmail
    def suApiKey = getKey() ;
    def userTmpApiKey = createTmpKey(userEmail) ;
    printAvailableCategories(userTmpApiKey) ;
    printAvailableExternalServices(userTmpApiKey) ;
    printUserRoles(suApiKey, userEmail) ;
    printMessageEndPoints(userTmpApiKey) ;
    printApiRequestAuditEntry(suApiKey, userEmail, apiFlowType, apiHttpStatuses, apiMaxReturn, verbose) ;
    printExternalApiMethodCalls(suApiKey, userEmail, externalApiNamespace, externalApiMethod, externalApiMaxReturn, verbose) ;
} else {
    def suApiKey = getKey() ;
    printAvailableCategories(suApiKey) ;
    printAvailableExternalServices(suApiKey) ;
    printMessageEndPoints(suApiKey) ;
    printOverallReportApiRequestAuditEntry(suApiKey, apiFlowType, apiHttpStatuses, apiMaxReturn) ;
    printExternalApiMethodCalls(suApiKey, null, externalApiNamespace, externalApiMethod, externalApiMaxReturn, verbose) ;
}