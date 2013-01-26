request("TemplatesList", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("TemplatesList", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
