request("ForgetMessageEndPoint", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["inactiveState":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["inactiveState":"bogusData","configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["inactiveState":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["inactiveState":"bogusData","configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgetMessageEndPoint", ["inactiveState":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
