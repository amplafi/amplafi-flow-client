request("GeneralUserSettings", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["firstName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["configuration":"bogusData","firstName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["configuration":"bogusData","lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["firstName":"bogusData","lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["configuration":"bogusData","firstName":"bogusData","lastName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["cellPhone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("GeneralUserSettings", ["configuration":"bogusData","cellPhone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
