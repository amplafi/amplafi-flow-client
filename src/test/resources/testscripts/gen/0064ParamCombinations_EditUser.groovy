request("EditUser", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["firstName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["configuration":"bogusData","firstName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["configuration":"bogusData","lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["firstName":"bogusData","lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["configuration":"bogusData","firstName":"bogusData","lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUser", ["configuration":"bogusData","email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
