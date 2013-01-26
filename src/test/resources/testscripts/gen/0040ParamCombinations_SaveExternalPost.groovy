request("SaveExternalPost", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("SaveExternalPost", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SaveExternalPost", ["originalBroadcastEnvelope":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SaveExternalPost", ["configuration":"bogusData","originalBroadcastEnvelope":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SaveExternalPost", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("SaveExternalPost", ["fsFlowTransitionLabel":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
