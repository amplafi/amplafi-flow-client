description "AvailableExternalServices", "Available External Services","none";


setApiVersion("apiv1");

printTaskInfo "Available External Services(Social Services)"
def response = requestResponse("EligibleExternalServiceInstanceList", ["fsRenderResult":"json"]);
checkError(response) ;

def entries = response.getResponseAsJSONArray();
String tabularTmpl = '%1$3s %2$20s' ;
def headers =  ['#', 'Ext Service'] ;
def keyPaths = [     'name'] ;
printTabular(entries, tabularTmpl, headers, keyPaths)




