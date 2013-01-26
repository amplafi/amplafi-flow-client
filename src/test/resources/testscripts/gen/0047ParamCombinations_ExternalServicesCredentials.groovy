request("ExternalServicesCredentials", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["externalServiceNatures":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["configuration":"bogusData","externalServiceNatures":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["configuration":"bogusData","externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["externalServiceNatures":"bogusData","externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["configuration":"bogusData","externalServiceNatures":"bogusData","externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["externalServiceDefinition":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ExternalServicesCredentials", ["configuration":"bogusData","externalServiceDefinition":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
