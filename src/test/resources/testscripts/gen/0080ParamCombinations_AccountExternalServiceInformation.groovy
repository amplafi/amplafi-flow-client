request("AccountExternalServiceInformation", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["externalServiceNatures":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["configuration":"bogusData","externalServiceNatures":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["configuration":"bogusData","externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["externalServiceNatures":"bogusData","externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["configuration":"bogusData","externalServiceNatures":"bogusData","externalServiceDefinitionsMap":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["externalServiceDefinition":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountExternalServiceInformation", ["configuration":"bogusData","externalServiceDefinition":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
