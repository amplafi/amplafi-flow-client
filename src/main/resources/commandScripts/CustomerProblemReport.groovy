// This line must be the first line in the script


/**
 * 
 * TODO:
 * 1. “Farreach.es is broken.” - current state of customer’s account ?
 *    + Show the status of the categories and Message End Point List Flow. If not MEP , should report a problem.
 *    + if any external ADD_MESSAGE exception should be considered as broken.
 *    + 
 * 2. When user create a post and he does not leave the post screen. The event bus
 *    request continuously to the wireservice and produce a lot of EnvelopeStatuses log(TO_YURIY)
 * 3. Need to log the exception and exception type in ApiRequestAuditEntry?
 * 4. Should we move the statistic report to flow provider. A big max return parameter can cause the out of memory exception?
 * 5. GetWordpressPluginInfo: currently redirect to http://dl.dropbox.com/s/ps9nkwanrkut1hh/farreaches-wp-plugin-info.json
 */

//At this point it looks like you have the basics of the data gathering.
//    Are those 400's normal - because it is part of the authorization process?
//    Are the customers experiencing any errors?
//    Did all the posts get successfully to their destination MEPs?
//    What are error conditions vs normal behavior?
//
//How about the tool has these 2 choices:
//1.  display customer posts that failed to get sent to the MEPs they should have (enter customer uri) - 
//    answers "Is FortunateFamilies.com having any problems with posting?"
//2.  display external services having problems (so for all customers and all MEPs ) display errors by service 
//    (facebook, twitter, tumblr) - answers "Is FarReach.es have a twitter integration problem?"

import java.util.HashMap

String newLine = System.getProperty("line.separator");
String usage = "CustomerProblemReport Script Help" + newLine + "This CustomerProblemReport script use to help the system administrator access" + newLine + "the various farreaches service configuration and user log information" + newLine + "Params" + newLine + "  verbose=<true/false>  To print detail json log" + newLine + "  userEmail=<userEmail>  The user that you want to inspect. If the userEmail is not specified. The overall report will be run" + newLine + "" + newLine + "  apiHttpStatuses=<apiHttpStatuses>  The http status code list, empty to select all" + newLine +"  apiFlowType=<flowType>  The flow type."+ newLine + "  apiMaxReturn=<apiMaxReturn>  The maximum number of the log entries that you want to return"+ newLine + ""+ newLine + "  externalApiNamespace=<twitter.com, facebook.com...>  The external service namespace"+ newLine + "  externalApiMethod=<ADD_MESSAGE...>  The external method call name"+ newLine + "  externalApiMaxReturn=<number>  The maximum number of the log entries that you want to return";

 
description "CustomerProblemReport", "This tool is used to report and identify the customer problem", "${usage}";

def printTaskInfo = { 
    info ->
    println "\n"
    println "*********************************************************************************";
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
    def msg = "Available External Services: \n"
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        String tabularTmpl = '%1$3s %2$20s' ;
        def headers =  ['#', 'Ext Service'] ;
        def keyPaths = [     'name'] ;
        printTabular(entries, tabularTmpl, headers, keyPaths)
        if(entries.length() == 0) {
            msg = msg + 
                  "    FAIL(The post won't be forwarded to any external service since no external service is available)"
        } else {
            msg = msg + 
                  "    SUCCESS(" + entries.length() + " external services are found)" ;
        }
    } else {
        prettyPrintResponse();
    }
    return msg ;
}

def printAvailableCategories = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
    printTaskInfo "Avaliable Categories"
    request("AvailableCategoriesFlow", ["fsRenderResult":"json"]);
    def msg = "Available Categories:\n" ;
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        String tabularTmpl = '%1$3s%2$20s%3$20s' ;
        def headers =  ['#', 'Topic Id', 'Name'] ;
        def keyPaths = ['entityId', 'name'] ;
        printTabular(entries, tabularTmpl, headers, keyPaths)
        if(entries.length() == 0) {
            msg = msg + 
                   "    FAIL(The post won't be forwarded to any external service since no categories are configured)"
        } else {
            msg = msg + 
                   "    SUCCESS(" + entries.length() + " categories are found)" ;
        }
    } else {
        prettyPrintResponse();
        msg = msg +
              "    FAIL(Unknow Error. Cannot retrieve the list of categories)"
    }
    return msg ;
}

def printUserRoles = { 
    apiKey, userEmail ->
    setApiVersion("suv1");
    setKey(apiKey);
    
    printTaskInfo "User Role And Configuration Information"
    request("UserRoleInfoFlow", ["fsRenderResult":"json", "email": userEmail]);
    println "User " + userEmail + " has the following roles:"
    prettyPrintResponse();
    def msg = "User Roles:\n" ;
    def result = getResponseData() ;
    if(result instanceof JSONArray) {
        msg = msg + 
              "    SUCCESS(" + result.toString() + ")" ;
    } else {
        msg = msg + 
              "    FAIL(" + result.toString() + ")" ;
    }
    
}

def printMessageEndPoints = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
    
    printTaskInfo "Message End Point List Flow"
    request("MessageEndPointListFlow", ["fsRenderResult":"json", "messageEndPointCompleteList": "true"]);
    
    def msg = "Message End Points:\n" ;
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        String tabularTmpl = '%1$3s%2$15s%3$15s%4$15s%5$15s' ;
        def headers =  ['#', 'Ext Service', 'User Name', 'Full Name', 'Topic Ids'] ;
        def keyPaths = ['externalServiceDefinition', 'extServiceUsername', 'extServiceUserFullName', 'selectedTopics'] ;
        printTabular(entries, tabularTmpl, headers, keyPaths)
        
        if(entries.length()) {
            msg = msg +
                    "    SUCCESS(user is connected to " + entries.length() + " external services)" ;
        } else {
            msg = msg +
                    "    FAIL(User does not connect to any external service)" ;
        }
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

def printUserPostInfo = {
    apiKey, userEmail, apiMaxReturn, verbose ->
    
            
    setApiVersion("suv1");
    setKey(apiKey);
    def reqParams = ["fsRenderResult":"json", "email": userEmail, "flowType": "CreateAlert", "maxReturn": apiMaxReturn] ;
    printTaskInfo "Post Info For " + userEmail ; 
    request("ApiRequestAuditEntriesFlow", reqParams);
    def msg = "User Post Info: \n" ;
    def failCount = 0 ;
    def entries = getResponseData() ;
    for(entry in  entries) {
        def flowType = entry.getStringByPath('request.parameters.requestPathArray') ;
        def fsAltFinished = entry.getStringByPath('request.parameters.fsAltFinished') ;
        if("SaveAndPublish".equals(fsAltFinished)) {
            def externalContentId = entry.getStringByPath('request.parameters.externalContentId') ;
            def messageHeadLine = entry.getStringByPath('request.parameters.messageHeadline') ;
            def messageBody = entry.getStringByPath('request.parameters.messageBody') ;
            
            setApiVersion("apiv1");
            request("EnvelopeStatusesFlow", ["fsRenderResult":"json", "externalContentId": "[" + externalContentId + "]"]);
            def mepStatuses = getResponseData() ;
            
            printlnMsg "Id: " + externalContentId ;
            printlnMsg "Title: " + messageHeadLine ;
            printlnMsg "Body: " + messageBody ;
            
            String tabularTmpl = '%1$-25s%2$10s%3$30s' ;
            def headers =  ['External Service', 'Status', 'Complete Time'] ;
            printlnMsg sprintf(tabularTmpl, headers);
            printlnMsg "----------------------------------------------------------------------------------------------";
            for(mepStatus in mepStatuses) {
                def namespace = mepStatus.getStringByPath('externalServiceDefinition') ;
                def status = mepStatus.getStringByPath('externalEntityStatus') ;
                def completeTime = mepStatus.getStringByPath('unblockCompletedTime') ;
                printlnMsg sprintf(tabularTmpl, namespace, status, completeTime);
                if(!"pcd".equals(status)) {
                    msg = msg +
                          "    FAIL(message '" + messageHeadLine + "'" +  " with status " + status + " for " + namespace + ")\n" ;
                    failCount++ ;
                }
            }
            printlnMsg "\n";
        }
    }
    if(failCount == 0) {
        msg = msg +
              "    SUCCESS(No fail post is detected)" ;
    }
    return msg ;
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


if(userEmail != null) {
    printTaskInfo "Running the customer problem report for the customer " + userEmail
    def suApiKey = getKey() ;
    def userTmpApiKey = createTmpKey(userEmail) ;
    def summary = [] ;
    if(userTmpApiKey == null) {
        summary.add("Create a temporary key for the user " + userEmail + ": \n" + 
                    "    Pleasse check your su api key, wireservice and connection.\n" + 
                    "    FAIL") ;
        return ;
    } else {
        summary.add("Create a temporary key for the user " + userEmail + ": \n" + 
                    "    SUCCESS(Api Key = " + userTmpApiKey + ")") ;
        summary.add(printAvailableCategories(userTmpApiKey)) ;
        summary.add(printAvailableExternalServices(userTmpApiKey)) ;
        summary.add(printUserRoles(suApiKey, userEmail)) ;
        summary.add(printMessageEndPoints(userTmpApiKey)) ;
        
        printApiRequestAuditEntry(suApiKey, userEmail, apiFlowType, apiHttpStatuses, apiMaxReturn, verbose) ;
        printExternalApiMethodCalls(suApiKey, userEmail, externalApiNamespace, externalApiMethod, externalApiMaxReturn, verbose) ;    
        
        summary.add(printUserPostInfo(suApiKey, userEmail, apiMaxReturn, verbose)) ;
    }
    printTaskInfo "Summary report for the user " + userEmail
    for(message in summary) {
       printlnMsg message  + "\n"; 
    }
} else {
    def suApiKey = getKey() ;
    
    summary.add(printAvailableCategories(userTmpApiKey)) ;
    summary.add(printAvailableExternalServices(userTmpApiKey)) ;
    
    printMessageEndPoints(suApiKey) ;
    printOverallReportApiRequestAuditEntry(suApiKey, apiFlowType, apiHttpStatuses, apiMaxReturn) ;
    printExternalApiMethodCalls(suApiKey, null, externalApiNamespace, externalApiMethod, externalApiMaxReturn, verbose) ;
}