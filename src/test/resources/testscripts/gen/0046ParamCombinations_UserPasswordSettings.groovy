request("UserPasswordSettings", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["authenticateOnFinish":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["user":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["authenticateOnFinish":"bogusData","user":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["authenticateOnFinish":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["user":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["authenticateOnFinish":"bogusData","user":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["resettingPassword":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserPasswordSettings", ["authenticateOnFinish":"bogusData","resettingPassword":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
