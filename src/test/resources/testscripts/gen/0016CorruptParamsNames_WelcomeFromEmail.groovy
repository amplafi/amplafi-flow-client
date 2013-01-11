request("WelcomeFromEmail", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("WelcomeFromEmail", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
