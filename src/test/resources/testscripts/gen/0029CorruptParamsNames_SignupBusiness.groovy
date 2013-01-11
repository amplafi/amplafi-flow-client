request("SignupBusiness", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupBusiness", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupBusiness", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupBusiness", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupBusiness", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
