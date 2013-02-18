// This line must be the first line in the script


import java.util.HashMap

String newLine = System.getProperty("line.separator");
String usage = 
    "CustomerProblemReport Script Help" + newLine + 
    "This CustomerProblemReport script use to help the system administrator access" + newLine + 
    "the various farreaches service configuration and user log information" + newLine + 
    "Params" + newLine + 
    "  verbose=<true/false>  To print detail json log" + newLine + 
    "  userEmail=<userEmail>  The user that you want to inspect. If the userEmail is not specified. The overall report will be run" + newLine  + newLine +
    "  fromDate=<yyyy/MM/dd>  Limit the audit logs from date" + newLine +
    "  toDate=<yyyy/MM/dd>  Limit the audit logs to date" + newLine +
    
    "  apiFlowType=<flowType>  The flow type."+ newLine + 
    "  apiMaxReturn=<apiMaxReturn>  The maximum number of the log entries that you want to return"+ newLine + newLine + 
    
    "  externalApiNamespace=<twitter.com, facebook.com...>  The external service namespace"+ newLine + 
    "  externalApiMethod=<ADD_MESSAGE...>  The external method call name"+ newLine + 
    "  externalApiMaxReturn=<number>  The maximum number of the log entries that you want to return";

 
description "CustomerProblemReport", "This tool is used to report and identify the customer problem", "${usage}";

def printTaskInfo = { 
    info ->
    println "\n"
    println "*********************************************************************************";
    println info ;
    println "*********************************************************************************"
}

def printHelp = { 
    printTaskInfo "CustomerProblemReport Script Help"
    println usage
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
            value[j + 1] = entry.optStringByPath(keyPaths[j]) ;
        }
        println sprintf(tabularTmpl, value) ;
    }
}

def printTabularMap = { 
    map, tabularTmpl, headers, keys ->
    println sprintf(tabularTmpl, headers) ;
    println "-----------------------------------------------------------------------------------------"
    for(entry in map.values()) {
        def value = new String[keys.size()] ;
        for(int j = 0; j < value.length; j++) {
            value[j] = entry.get(keys[j]) ;
        }
        println sprintf(tabularTmpl, value) ;
    }
}

def toAmplafiJSONCalendar = { 
    dateString -> 
    def formater = new java.text.SimpleDateFormat("yyyy/MM/dd") ;
    def date = formater.parse(dateString) ;
    def json = sprintf('{"timeInMillis": %1d, "timeZoneID": "GMT"}', date.getTime()) ;
    return json ;
}

def createTmpKey = { 
    publicUri, userEmail ->
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
}

def getAvailableCategories = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
    request("AvailableCategoriesFlow", ["fsRenderResult":"json"]);
    def categories = [:];
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        for(entry in getResponseData()) {
            def id = entry.opt("entityId") ;
            def name = entry.opt("name") ;
            categories[id] = ["id": id, "name": name] ;
        }
    } else {
        println "ERROR: Cannot get the category list"
        prettyPrintResponse();
    }
    return categories ;
}

def printAvailableCategories = { 
    categories ->
    printTaskInfo "Available Categories"
    String tabularTmpl = '%1$10s%2$20s' ;
    def headers =  ['Topic Id', 'Name'] ;
    def keys = ['id', 'name'] ;
    printTabularMap(categories, tabularTmpl, headers, keys);
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
    } else {
        prettyPrintResponse();
    }
}

def printUserRoles = { 
    apiKey ->
    setApiVersion("suv1");
    setKey(apiKey);
    
    printTaskInfo "User Role And Configuration Information"
    request("UserRoleInfoFlow", ["fsRenderResult":"json"]);
    def result = getResponseData() ;
    if(result instanceof JSONArray) {
        for(entry in result) {
            def email = entry.opt("defaultEmail") ;
            def fullName = entry.opt("fullName") ;
            def role = entry.opt("role") ;
            println email 
            println "  [" + role + "]" 
            println "  " + fullName 
        }
    } else {
        prettyPrintResponse();
    }
}
def addMessageEndPointToMap = {
    meps, entry ->
    def mepId = entry.opt("lookupKey") ;
    def publicUri = entry.opt("publicUri") ;
    def externalServiceDefinition = entry.opt("externalServiceDefinition") ;
    def extServiceUsername = entry.opt("extServiceUsername") ;
    def extServiceUserFullName = entry.opt("extServiceUserFullName") ;
    meps[mepId] = [
        "mepId": mepId, "publicUri": publicUri, "externalServiceDefinition": externalServiceDefinition, 
        "extServiceUsername": extServiceUsername, "extServiceUserFullName": extServiceUserFullName
    ];
}

def getMessageEndPoints = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
    
    request("MessageEndPointListFlow", ["fsRenderResult":"json", "messageEndPointCompleteList": "true"]);
    
    def meps = [:] ;
    def entries = getResponseData();
    for(entry in entries) {
        addMessageEndPointToMap(meps, entry) ;
    }
    return meps;
}

def printMessageEndPoint = { 
    mep ->
    println "MEP ID:           " + mep['mepId'] ;
    println "External Service: " + mep['externalServiceDefinition'] ;
    println "Public Uri:       " + mep['publicUri'] ;
    println "Username:         " + mep['extServiceUsername'] ;
    println "Full Name:        " + mep['extServiceUserFullName'] ;
}

def printMessageEndPoints = { 
    meps ->
    printTaskInfo "Message End Points"
    def first = true ;
    for(mep in meps.values()) {
        if(!first) {
            println "-----------------------------------------------------------------------------------"
        }
        printMessageEndPoint(mep) ;
        first = false ;
    }
}

def printApiByUserEmailStatistic = {
    entries ->
    def byUserEmailStatistic = [:] ;
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def email = entry.optStringByPath('request.defaultEmail') ;
        def httpStatusCode = Integer.parseInt(entry.optStringByPath('response.code')) ;
        
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
        def flowType = entry.optStringByPath('request.parameters.requestPathArray') ;
        def httpStatusCode = Integer.parseInt(entry.optStringByPath('response.code')) ;
        
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

def printOverallReportApiRequestAuditEntry = {
    apiKey, apiFlowType, fromDate, toDate, apiMaxReturn ->
    setApiVersion("suv1");
    setKey(apiKey);
    def reqParams = ["fsRenderResult":"json", "maxReturn": apiMaxReturn] ;
    if(apiFlowType != null) {
        reqParams["flowType"] = apiFlowType;
    } 
    
    if(fromDate != null) {
        reqParams["fromDate"] = toAmplafiJSONCalendar(fromDate);
    } 
    
    if(toDate != null) {
        reqParams["toDate"] = toAmplafiJSONCalendar(toDate);
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
    def dateFormater = new java.text.SimpleDateFormat('dd/MM/yyyy@hh:mm:ss') ;
    String tabularTmpl = '%1$20s %2$-15s %3$30s %4$5s %5$6s %6$-50s' ;
    def headers =  ['Date', 'Namespace', 'Method', 'EES', 'Status', 'Message'] ;
    println '----------------------------------------------------------------------------------------------'
    println 'External Api Detail Calls'
    println '----------------------------------------------------------------------------------------------'
    println sprintf(tabularTmpl, headers) ;
    println '----------------------------------------------------------------------------------------------'
    for(int i = 0; i < entries.length(); i++) {
        def entry = entries.get(i) ;
        def timeInMillis = Long.parseLong(entry.optStringByPath('createTime.timeInMillis')) ;
        def date = dateFormater.format(new Date(timeInMillis)) ;
        def namespace = entry.optStringByPath('externalServiceDefinitionNamespace') ;
        def method = entry.optStringByPath('method') ;
        def externalEntityStatus = entry.optStringByPath('externalEntityStatus') ;
        def httpStatusCode = entry.optStringByPath('statusCode') ;
        def message = entry.optStringByPath('message') ;
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
        def namespace = entry.optStringByPath('externalServiceDefinitionNamespace') ;
        def httpStatusCode = Integer.parseInt(entry.optStringByPath('statusCode')) ;
        def method = entry.optStringByPath('method') ;
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
    String tabularTmpl = '%1$-30s%2$10s%3$10s%4$10s%5$10s%6$10s%7$10s' ;
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
    apiKey, externalApiNamespace, externalApiMethod, fromDate, toDate, externalApiMaxReturn ->
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
    if(fromDate != null) {
        reqParams["fromDate"] = toAmplafiJSONCalendar(fromDate) ;
    }
    if(toDate != null) {
        reqParams["toDate"] = toAmplafiJSONCalendar(toDate) ;
    }
    request("ExternalApiMethodCallAuditEntriesFlow", reqParams);
    if(getResponseData() instanceof JSONArray) {
        def entries = getResponseData();
        printExternalStatisticMethodCalls(entries) ;
        println "\n\n" ;
        printExternalApiDetailMethodCalls(entries) ;
    } else {
        prettyPrintResponse();
    }
}

def getUserPostInfo = {
    apiKey, fromDate, toDate->
    
    setApiVersion("suv1");
    setKey(apiKey);
    def reqParams = ["fsRenderResult":"json"] ;
    if(fromDate != null) {
        reqParams["fromDate"] = toAmplafiJSONCalendar(fromDate) ;
    }
    if(toDate != null) {
        reqParams["toDate"] = toAmplafiJSONCalendar(toDate) ;
    }
    
    request("BroadcastEnvelopesFlow", reqParams);
    def result = getResponseData() ;
    return result;
}

def printUserPostStatisticByCategory = {
    entries, verbose ->
    printTaskInfo "User Post By Categories"
    def categoriesStat = [:] ;
    for(entry in entries) {
        def broadcastEnvelope = entry.get("broadcastEnvelope")
        def selectedTopics = broadcastEnvelope.get("selectedTopics")
        def messageEndPointEnvelopeRecord = entry.get("messageEndPointEnvelopeRecord") ;
        for(topic in selectedTopics) {
            def name = topic.get("name");
            if(categoriesStat[name] == null) {
                categoriesStat[name] = [
                    "name": name, "entityId": topic.optString("entityId"), 
                    "externalIds": [broadcastEnvelope.optString("publicUri")], 
                    "messageEndPoint": [:], 
                    "postCount": 0
                ] ;
            }
            categoriesStat[name]["postCount"] += 1 ;
            for(endPoint in messageEndPointEnvelopeRecord) {
                def messagePointBroadcastTopics = endPoint.get("messagePointBroadcastTopics") ;
                def matchTopic = false ;
                for(messagePointBroadcastTopic in messagePointBroadcastTopics) {
                    if(name.equals(messagePointBroadcastTopic.get("name"))) {
                        matchTopic = true;
                        break ;
                    }    
                }
                if(!matchTopic) {
                    continue ;
                }
                
                def externalEntityStatus = endPoint.optString("externalEntityStatus") ;
                if(endPoint.has("publicUri")) {
                    categoriesStat[name]["externalIds"] << endPoint.get("publicUri") ;
                }
                def messagePointId = endPoint.get("messagePointId") ;
                if(categoriesStat[name]["messageEndPoint"][messagePointId] == null) {
                    def messagePointUri = endPoint.get("messagePointUri") ;
                    categoriesStat[name]["messageEndPoint"][messagePointId] = ["messagePointId" : messagePointId, "messagePointUri": messagePointUri] ;
                }
            }
        }
    }
    
    for(category in categoriesStat.values()) {
        println category["name"] + ":" ;
        println "  Entity Id: " + category["entityId"] 
        println "  External Ids: "
        for(externalId in category["externalIds"]) {
            println "    " + externalId
        }
        println "  Post Count: " + category["postCount"] ;
        println "  Message End Point: " 
        for(messageEndPoint in category["messageEndPoint"].values()) {
            println sprintf('%1$10s     %2$-40s', messageEndPoint["messagePointId"], messageEndPoint["messagePointUri"])
        }
        println "\n\n"
    }
}

def printUserPostStatisticByMessagePoint = {
    entries, verbose ->
    printTaskInfo "User Post By Message End Points"
    def mepStats = [:] ;
    for(entry in entries) {
        def broadcastEnvelope = entry.get("broadcastEnvelope")
        def selectedTopics = broadcastEnvelope.get("selectedTopics") ;
        def messageEndPointEnvelopeRecord = entry.get("messageEndPointEnvelopeRecord") ;
        for(mep in messageEndPointEnvelopeRecord) {
            def mepId = mep.get("messagePointId");
            def mepExternalServiceDefinition = mep.get("externalServiceDefinition") ;
            def mepMessagePointUri = mep.get("messagePointUri") ;
            def mepBroadcastTopics = mep.get("messagePointBroadcastTopics") ;
            if(mepStats[mepId] == null) {
                mepStats[mepId] = [
                    "messagePointId": mepId, "externalServiceDefinition": mepExternalServiceDefinition,
                    "messagePointUri": mepMessagePointUri, "categories": [:], "postCount": 0
                ] ;
            }
            mepStats[mepId]["postCount"] += 1 ;
            for(mepBroadcastTopic in mepBroadcastTopics) {
                def mepBroadcastTopicName = mepBroadcastTopic.get("name");
                def matchTopic = false ;
                for(selectedTopic in selectedTopics) {
                    if(mepBroadcastTopicName.equals(selectedTopic.get("name"))) {
                        matchTopic = true ;
                        break ;
                    }
                }
                if(matchTopic) {
                    mepStats[mepId]["categories"][mepBroadcastTopicName] = ["entityId": mepBroadcastTopic.get("entityId"), "name": mepBroadcastTopicName] ;
                }
            }
        }
    }
    
    for(mepStat in mepStats.values()) {
        println mepStat["messagePointUri"]
        println "  Farreaches Id: " + mepStat["messagePointId"]
        println "  External Service Definition: " + mepStat["externalServiceDefinition"]
        println "  Categories: "
        for(category in mepStat["categories"].values()) {
            println sprintf('%1$10s %2$-30s', category["entityId"], category["name"]) ;
        }
        println "  Posts: "  + mepStat["postCount"] ;
        println "\n"
    }
}

def printUserPostInfo = {
    entries, verbose ->
    printTaskInfo "User Post Info"
    def messageThreads = [:] ;
    for(entry in entries) {
        def broadcastEnvelope = entry.getJSONObject("broadcastEnvelope")
        def messageThreadId = broadcastEnvelope.get("messageThreadId");
        if(messageThreads[messageThreadId] == null) {
            messageThreads[messageThreadId] = [] ;
        }
        messageThreads[messageThreadId] << entry ;
    }
    
    for(messageThreadEntry in messageThreads) {
        println "Message Thread: " + messageThreadEntry.getKey() ;
        for(entry in messageThreadEntry.getValue()) {
            def broadcastEnvelope = entry.getJSONObject("broadcastEnvelope")
            println "  FarReaches Id:" + broadcastEnvelope.optString('entityId') ;
            println "  Title:" + broadcastEnvelope.optString('headLine') ;
            println "  Body:" + broadcastEnvelope.optString('messageText') ;
            def topicNames = "" ;
            for(selectedTopic in broadcastEnvelope.optJSONArray('selectedTopics')) {
                if(topicNames.length() > 0) {
                    topicNames += ", " ;
                }
                topicNames += selectedTopic.optString("name")  ;
            }
            println "  Selected Topics:" + topicNames;
           
            println "  MessageEndPointEnvelopeRecord:"
            def messageEndPointEnvelopeRecords = entry.optJSONArray("messageEndPointEnvelopeRecord") ; 
            println "    External Ids: "
            for(record in messageEndPointEnvelopeRecords) {
                def externalEntityStatus = record.optString("externalEntityStatus") ;
                if("pcd".equals(externalEntityStatus)) {
                   def externalServiceDefinition = record.optString("externalServiceDefinition") ;
                   def externalContentId = record.optString("externalContentId") ;
                   def publicUri = record.optString("publicUri") ;
                   println "      " + externalServiceDefinition + ": " + externalContentId + " (" + publicUri + ")" ;
                }
            }
            
            println "    Transmission: "
            for(record in messageEndPointEnvelopeRecords) {
                def externalEntityStatus = record.optString("externalEntityStatus") ;
                def messagePointUri = record.optString("messagePointUri") ;
                def unblockCompletedTime = "" ;
                if(record.has("unblockCompletedTime")) {
                    unblockCompletedTime = record.optString("unblockCompletedTime") ;
                }
                def status = null ;
                if("pcd".equals(externalEntityStatus)) {
                    status = "Successful" 
                } else if("nvr".equals(externalEntityStatus)) {
                    status = "Ignore" 
                } else {
                    status = "Failed(" + externalEntityStatus + ")" ; 
                }
                
                println "      " + messagePointUri ;
                println sprintf('        %1$-15s%2$40s', status, unblockCompletedTime) ;
            }
            println ""
        }
        println "\n" ;
    }
}

def userEmail = "admin@amplafi.com" ;
if (params && params["userEmail"]) {
    userEmail = params["userEmail"] ;
}

def publicUri = null ;
if (params && params["publicUri"]) {
    publicUri = params["publicUri"] ;
}

def apiMaxReturn = "1000"
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

def externalApiMaxReturn = "1000" ;
if (params && params["externalApiMaxReturn"]) {
    externalApiMaxReturn = params['externalApiMaxReturn'] ;
}

def fromDate = null ;
if (params && params["fromDate"]) {
    fromDate = params['fromDate'] ;
}

def toDate = null ;
if (params && params["toDate"]) {
    toDate = params['toDate'] ;
}

def verbose = false; 
if (params && params["verbose"]) {
    verbose = "true".equals(params["verbose"]) ;
}

try {
    def suApiKey = getKey() ;
    def apiKey = null ;
    if(publicUri != null) {
        apiKey = createTmpKey(publicUri, userEmail) ;
    } else {
        apiKey = suApiKey ;
    }
    if(apiKey == null) {
        return ;
    }
    
    def categories = getAvailableCategories(apiKey) ;
    printAvailableCategories(categories) ;
    
    printAvailableExternalServices(apiKey) ;
    
    def meps = getMessageEndPoints(apiKey) ;
    printMessageEndPoints(meps) ;
    
    printUserRoles(apiKey) ;
        
    printOverallReportApiRequestAuditEntry(apiKey, apiFlowType, fromDate, toDate, apiMaxReturn) ;
    printExternalApiMethodCalls(apiKey, externalApiNamespace, externalApiMethod, fromDate, toDate, externalApiMaxReturn) ;
    
    def userPostInfos = getUserPostInfo(apiKey, fromDate, toDate) ;
    printUserPostStatisticByCategory(userPostInfos, verbose) ;
    printUserPostStatisticByMessagePoint(userPostInfos, verbose) ;
    printUserPostInfo(userPostInfos, verbose) ;
} catch(Throwable ex) {
    println "Error: " + ex.getMessage() ;
    if(verbose) {
        ex.printStackTrace();
    }
}