request("AccountContactInformation", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["contactUser":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["configuration":"bogusData","contactUser":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["contactInformation":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["configuration":"bogusData","contactInformation":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["contactUser":"bogusData","contactInformation":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["configuration":"bogusData","contactUser":"bogusData","contactInformation":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["locations":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountContactInformation", ["configuration":"bogusData","locations":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
