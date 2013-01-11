request("EditUserProfile", ["configuration":"","firstName":"","lastName":"","cellPhone":"","homePhone":"","defaultPhone":"","defaultOfficePhone":"","includeInMessage":"","sendExternal":"","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["configuration":"","defaultAddress":"","newEmail":"","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["configuration":"","timezone":"","browserUserTimeZoneOffset":"","browserUserTimeZone":"","dateFormat":"","timeFormat":"","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["authenticateOnFinish":"","authenticationProvider":"","resettingPassword":"","fsRenderResult":"json"])

checkReturnedValidJson()
