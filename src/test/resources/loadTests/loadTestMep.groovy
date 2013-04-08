

//println "In script " + getKey()

setApiVersion("api");

def response = requestResponse("MessageEndPointList" , ["messageEndPointTypes": "[\"ext\"]",
                                                         "requestPathArray": "MessageEndPointList",
                                                         "messageEndPointCompleteList": "true"]);

if(response.hasError()){
    throw new Exception(response.getErrorMessage());
}


def entries = getResponseData();

//println "###########" + entries;
