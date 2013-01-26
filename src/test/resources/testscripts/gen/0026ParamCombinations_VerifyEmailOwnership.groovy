request("VerifyEmailOwnership", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("VerifyEmailOwnership", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("VerifyEmailOwnership", ["defaultAddress":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("VerifyEmailOwnership", ["configuration":"bogusData","defaultAddress":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("VerifyEmailOwnership", ["newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("VerifyEmailOwnership", ["configuration":"bogusData","newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("VerifyEmailOwnership", ["defaultAddress":"bogusData","newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("VerifyEmailOwnership", ["configuration":"bogusData","defaultAddress":"bogusData","newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
