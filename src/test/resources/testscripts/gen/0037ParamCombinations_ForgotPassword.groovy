request("ForgotPassword", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgotPassword", ["email":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgotPassword", ["captchaProvided":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ForgotPassword", ["email":"bogusData","captchaProvided":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
