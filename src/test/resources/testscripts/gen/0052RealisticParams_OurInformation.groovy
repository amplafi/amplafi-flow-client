request("OurInformation", ["configuration":"abc","defaultLanguage":"null","rootUrl":"null","bannerUri":"null","broadcastProviderName":"abc","fsRenderResult":"json"])

checkReturnedValidJson()
request("OurInformation", ["configuration":"abc","contactUser":"null","contactInformation":"abc","locations":"abc","fsRenderResult":"json"])

checkReturnedValidJson()
request("OurInformation", ["configuration":"abc","providerLocations":"['a','b','c']","fsRenderResult":"json"])

checkReturnedValidJson()
request("OurInformation", ["configuration":"abc","roles":"['a','b','c']","newRoles":"['a','b','c']","permitAnonymous":"true","fsRenderResult":"json"])

checkReturnedValidJson()
request("OurInformation", ["configuration":"abc","timezone":"null","browserUserTimeZoneOffset":"100","browserUserTimeZone":"null","dateFormat":"abc","timeFormat":"abc","fsRenderResult":"json"])

checkReturnedValidJson()
request("OurInformation", ["configuration":"abc","externalServiceNatures":"['a','b','c']","externalServiceDefinitionsMap":"null","externalServiceDefinition":"null","fsRenderResult":"json"])

checkReturnedValidJson()
