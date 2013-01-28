request("EditUserProfile", ["configuration":"abc","firstName":"abc","lastName":"abc","cellPhone":"abc","homePhone":"abc","defaultPhone":"null","defaultOfficePhone":"abc","includeInMessage":"null","sendExternal":"null","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["configuration":"abc","defaultAddress":"abc","newEmail":"abc","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["configuration":"abc","timezone":"null","browserUserTimeZoneOffset":"100","browserUserTimeZone":"null","dateFormat":"abc","timeFormat":"abc","fsRenderResult":"json"])

checkReturnedValidJson()
request("EditUserProfile", ["authenticateOnFinish":"null","authenticationProvider":"null","resettingPassword":"null","fsRenderResult":"json"])

checkReturnedValidJson()
