request("SignupIndividual", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","bad{token":"bogusData","bad token":"bogusData","bad.token":"bogusData","bad%token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["bad token":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
