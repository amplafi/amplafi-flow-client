request("EditUserProfile", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
