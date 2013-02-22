description "ExternalApiMethodCallLog", "ExternalApiMethodCall Log", "apiKey=key, fromDate=date, toDate=date, maxReturn=number";


def apiKey = null ;
if (params && params["apiKey"]) {
    apiKey = params['apiKey'] ;
}

def fromDate = null ;
if (params && params["fromDate"]) {
    fromDate = params['fromDate'] ;
}

def toDate = null ;
if (params && params["toDate"]) {
    toDate = params['toDate'] ;
}

def maxReturn = "1000"
if (params && params["maxReturn"]) {
    maxReturn = params["maxReturn"] ;
}

def toAmplafiJSONCalendar = { 
    dateString -> 
    def formater = new java.text.SimpleDateFormat("yyyy/MM/dd") ;
    def date = formater.parse(dateString) ;
    def json = sprintf('{"timeInMillis": %1d, "timeZoneID": "GMT"}', date.getTime()) ;
    return json ;
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

setApiVersion("suv1");
setKey(apiKey);
printTaskInfo "ExternalApiMethodCallAuditEntry log"
def reqParams = ["fsRenderResult":"json", "maxReturn": maxReturn] ;
if(fromDate != null) {
    reqParams["fromDate"] = toAmplafiJSONCalendar(fromDate) ;
}
if(toDate != null) {
    reqParams["toDate"] = toAmplafiJSONCalendar(toDate) ;
}
def response = requestResponse("ExternalApiMethodCallAuditEntries", reqParams);
checkError(response) ;
def entries = response.getResponseAsJSONArray();
printExternalStatisticMethodCalls(entries) ;
println "\n\n" ;
printExternalApiDetailMethodCalls(entries) ;