request("SelectPartnerTopics", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["ownTopics":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["ownTopics":"bogusData","configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["ownTopics":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["ownTopics":"bogusData","configuration":"bogusData","messageEndPoint":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SelectPartnerTopics", ["ownTopics":"bogusData","messageEndPointList":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
