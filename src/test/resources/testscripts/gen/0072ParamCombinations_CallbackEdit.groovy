request("CallbackEdit", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["callback":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["configuration":"bogusData","callback":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["configuration":"bogusData","description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["callback":"bogusData","description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["configuration":"bogusData","callback":"bogusData","description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["invitesUsed":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CallbackEdit", ["configuration":"bogusData","invitesUsed":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
