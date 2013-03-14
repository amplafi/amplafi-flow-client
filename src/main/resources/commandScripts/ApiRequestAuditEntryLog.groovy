description "ApiRequestAuditEntryLog", "ApiRequestAuditEntry Log", [paramDef("apiKey","",false,null),
                                                                    paramDef("fromDate","",true,null),
                                                                    paramDef("toDate","",true,null),
                                                                    paramDef("maxReturn","",true,1000)];


def toAmplafiJSONCalendar = { 
    dateString -> 
    def formater = new java.text.SimpleDateFormat("yyyy/MM/dd") ;
    def date = formater.parse(dateString) ;
    def json = sprintf('{"timeInMillis": %1d, "timeZoneID": "GMT"}', date.getTime()) ;
    return json ;
}

def printByUserEmailStatistic = {
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

def printByFlowTypeStatistic = {
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

setApiVersion("suv1");
setKey(apiKey);
def reqParams = ["fsRenderResult":"json", "maxReturn": maxReturn] ;

if(fromDate != null) {
    reqParams["fromDate"] = toAmplafiJSONCalendar(fromDate);
} 

if(toDate != null) {
    reqParams["toDate"] = toAmplafiJSONCalendar(toDate);
} 

printTaskInfo "Overall Report For ApiRequestAuditEntry Log"

def response = requestResponse("ApiRequestAuditEntriesFlow", reqParams) ;
checkError(response) ;
def entries = response.getResponseAsJSONArray();
printByUserEmailStatistic(entries) ;
println '\n\n'
printByFlowTypeStatistic(entries) ;