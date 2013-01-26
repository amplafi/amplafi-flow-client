request("CampaignEdit", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["campaign":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["configuration":"bogusData","campaign":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["configuration":"bogusData","description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["campaign":"bogusData","description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["configuration":"bogusData","campaign":"bogusData","description":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["campaignCode":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignEdit", ["configuration":"bogusData","campaignCode":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
