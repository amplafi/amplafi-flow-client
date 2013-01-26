request("NewTemplate", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("NewTemplate", ["templateName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewTemplate", ["templateDescription":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("NewTemplate", ["templateName":"bogusData","templateDescription":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
