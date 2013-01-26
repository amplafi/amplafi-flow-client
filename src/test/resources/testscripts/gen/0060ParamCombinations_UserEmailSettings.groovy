request("UserEmailSettings", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("UserEmailSettings", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserEmailSettings", ["defaultAddress":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserEmailSettings", ["configuration":"bogusData","defaultAddress":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserEmailSettings", ["newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserEmailSettings", ["configuration":"bogusData","newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserEmailSettings", ["defaultAddress":"bogusData","newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserEmailSettings", ["configuration":"bogusData","defaultAddress":"bogusData","newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
