request("Logout", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("Logout", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
