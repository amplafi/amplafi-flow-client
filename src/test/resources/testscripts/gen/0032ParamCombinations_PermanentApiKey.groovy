request("PermanentApiKey", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["temporaryApiKey":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["usersList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["temporaryApiKey":"bogusData","usersList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["completeList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["temporaryApiKey":"bogusData","completeList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["usersList":"bogusData","completeList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["temporaryApiKey":"bogusData","usersList":"bogusData","completeList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["callbackUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["temporaryApiKey":"bogusData","callbackUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
