request("InactivateMessageEndPoint", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["inactiveState":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["inactiveState":"bogusData","configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["inactiveState":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["inactiveState":"bogusData","configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("InactivateMessageEndPoint", ["inactiveState":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
