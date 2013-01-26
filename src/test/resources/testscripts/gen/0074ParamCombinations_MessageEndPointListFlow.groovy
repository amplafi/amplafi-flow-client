request("MessageEndPointListFlow", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("MessageEndPointListFlow", ["messageEndPointCompleteList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("MessageEndPointListFlow", ["messageEndPointTypes":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("MessageEndPointListFlow", ["messageEndPointCompleteList":"bogusData","messageEndPointTypes":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
