request("ChangePassword", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["authenticateOnFinish":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["user":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["authenticateOnFinish":"bogusData","user":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["authenticateOnFinish":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["user":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["authenticateOnFinish":"bogusData","user":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["resettingPassword":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangePassword", ["authenticateOnFinish":"bogusData","resettingPassword":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
