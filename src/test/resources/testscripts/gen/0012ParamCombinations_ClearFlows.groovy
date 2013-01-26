request("ClearFlows", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ClearFlows", ["flows":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
