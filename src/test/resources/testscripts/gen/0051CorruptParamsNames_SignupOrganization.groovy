request("SignupOrganization", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupOrganization", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupOrganization", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupOrganization", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupOrganization", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
