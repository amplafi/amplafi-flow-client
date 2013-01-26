request("WelcomeFromEmail", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["topic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["configuration":"bogusData","topic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["messageEndPoint":"bogusData","topic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["configuration":"bogusData","messageEndPoint":"bogusData","topic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["updateFrequency":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["configuration":"bogusData","updateFrequency":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["fsFlowTransitionLabel":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
