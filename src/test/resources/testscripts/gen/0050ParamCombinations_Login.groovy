request("Login", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("Login", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("Login", ["email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("Login", ["configuration":"bogusData","email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
