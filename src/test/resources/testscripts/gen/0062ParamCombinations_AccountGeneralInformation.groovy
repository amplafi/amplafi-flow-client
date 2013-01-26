request("AccountGeneralInformation", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["defaultLanguage":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["configuration":"bogusData","defaultLanguage":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["rootUrl":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["configuration":"bogusData","rootUrl":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["defaultLanguage":"bogusData","rootUrl":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["configuration":"bogusData","defaultLanguage":"bogusData","rootUrl":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["bannerUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountGeneralInformation", ["configuration":"bogusData","bannerUri":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
