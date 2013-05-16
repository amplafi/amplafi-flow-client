

description "bpNotCreatingContent", "Script for finding flow broadcastProviders NotCreating Content ", [paramDef("numberOfDays","since the BP created content",true,"10")];


//def tempApiKey = callScript("GetTempApiKey",["apiCall":"BroadcastProvidersNotCreatingContent"]);


//def tempApiKey = getKey();

//emitOutput("tempApiKey = " + tempApiKey);

//setKey(tempApiKey);

setApiVersion("su");


def response = request("broadcastProvidersNotCreatingContent",["numberOfDays":numberOfDays]);

println response;