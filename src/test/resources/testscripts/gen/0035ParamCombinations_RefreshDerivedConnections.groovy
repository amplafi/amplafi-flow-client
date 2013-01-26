request("RefreshDerivedConnections", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["configuration":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["messageEndPoint":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["configuration":"bogusData","messageEndPoint":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["messageEndPointUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("RefreshDerivedConnections", ["configuration":"bogusData","messageEndPointUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
