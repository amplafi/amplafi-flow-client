request("ActivateMessageEndPoint", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["configuration":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["messageEndPoint":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["configuration":"bogusData","messageEndPoint":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["messageEndPointUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ActivateMessageEndPoint", ["configuration":"bogusData","messageEndPointUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
