description "DisplayAvailableExternalServices", "Display Available External Services","none";


setApiVersion("apiv1");

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




