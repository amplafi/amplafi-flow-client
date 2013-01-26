request("AmplafiThis", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("AmplafiThis", ["fsFlowTransitionLabel":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
