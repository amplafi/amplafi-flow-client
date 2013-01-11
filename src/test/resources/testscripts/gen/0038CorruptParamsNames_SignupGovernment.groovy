request("SignupGovernment", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupGovernment", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupGovernment", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupGovernment", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupGovernment", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
