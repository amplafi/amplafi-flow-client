request("EditUserProfile", ["configuration":"bogusData","firstName":"bogusData","lastName":"bogusData","cellPhone":"bogusData","homePhone":"bogusData","defaultPhone":"bogusData","defaultOfficePhone":"bogusData","includeInMessage":"bogusData","sendExternal":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["configuration":"bogusData","defaultAddress":"bogusData","newEmail":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["configuration":"bogusData","timezone":"bogusData","browserUserTimeZoneOffset":"bogusData","browserUserTimeZone":"bogusData","dateFormat":"bogusData","timeFormat":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["authenticateOnFinish":"bogusData","authenticationProvider":"bogusData","resettingPassword":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
