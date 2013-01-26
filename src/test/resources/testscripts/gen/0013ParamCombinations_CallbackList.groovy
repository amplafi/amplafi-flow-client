request("CallbackList", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackList", ["selectedCallbacks":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackList", ["callbacks":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackList", ["selectedCallbacks":"bogusData","callbacks":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
