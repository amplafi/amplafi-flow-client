request("CampaignList", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignList", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignList", ["selectedCampaigns":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignList", ["configuration":"bogusData","selectedCampaigns":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignList", ["campaigns":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignList", ["configuration":"bogusData","campaigns":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignList", ["selectedCampaigns":"bogusData","campaigns":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CampaignList", ["configuration":"bogusData","selectedCampaigns":"bogusData","campaigns":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
