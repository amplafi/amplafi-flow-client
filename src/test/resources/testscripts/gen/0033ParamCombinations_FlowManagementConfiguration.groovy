request("FlowManagementConfiguration", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("FlowManagementConfiguration", ["autorunDefinitions":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
