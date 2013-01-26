request("ChangeTopicFrequency", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["topic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["messageEndPoint":"bogusData","topic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["updateFrequency":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["messageEndPoint":"bogusData","updateFrequency":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["topic":"bogusData","updateFrequency":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["messageEndPoint":"bogusData","topic":"bogusData","updateFrequency":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["fsFlowTransitionLabel":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
