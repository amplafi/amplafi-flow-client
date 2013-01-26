request("TemporaryApiKeyFlow", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("TemporaryApiKeyFlow", ["callbackUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
