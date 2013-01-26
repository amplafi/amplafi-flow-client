request("CreateMessage", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("CreateMessage", ["fsFlowTransitionLabel":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
