request("PermanentApiKey", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("PermanentApiKey", ["temporaryApiKey":"null","usersList":"['a','b','c']","completeList":"null","callbackUri":"null","fsRenderResult":"json"])

checkReturnedValidJson()
