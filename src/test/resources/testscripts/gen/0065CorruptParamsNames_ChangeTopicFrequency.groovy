request("ChangeTopicFrequency", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ChangeTopicFrequency", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
