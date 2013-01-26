request("AccountMemberInformation", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["roles":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["configuration":"bogusData","roles":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["newRoles":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["configuration":"bogusData","newRoles":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["roles":"bogusData","newRoles":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["configuration":"bogusData","roles":"bogusData","newRoles":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["permitAnonymous":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountMemberInformation", ["configuration":"bogusData","permitAnonymous":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
