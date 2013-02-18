description "AvailableExternalServices", "Available External Services","none";


setApiVersion("apiv1");

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




