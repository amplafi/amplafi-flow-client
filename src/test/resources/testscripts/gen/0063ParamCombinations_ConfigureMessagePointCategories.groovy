request("ConfigureMessagePointCategories", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["headerText":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["headerText":"bogusData","configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["headerText":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["headerText":"bogusData","configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("ConfigureMessagePointCategories", ["headerText":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
