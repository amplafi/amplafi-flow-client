request("NewUser", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["organization":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["configuration":"bogusData","organization":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["configuration":"bogusData","email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["organization":"bogusData","email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["configuration":"bogusData","organization":"bogusData","email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["password":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewUser", ["configuration":"bogusData","password":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
