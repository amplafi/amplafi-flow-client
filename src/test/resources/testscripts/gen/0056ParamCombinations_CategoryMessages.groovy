request("CategoryMessages", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoryMessages", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoryMessages", ["categorySelection":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoryMessages", ["configuration":"bogusData","categorySelection":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
