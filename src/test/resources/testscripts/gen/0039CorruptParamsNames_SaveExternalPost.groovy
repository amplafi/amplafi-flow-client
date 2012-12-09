request("SaveExternalPost", ["bad token":"bogusData","bad.token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SaveExternalPost", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
