request("AccountLocationInformation", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountLocationInformation", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountLocationInformation", ["providerLocations":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountLocationInformation", ["configuration":"bogusData","providerLocations":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
