request("EnterVerificationCode", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("EnterVerificationCode", ["callback":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
