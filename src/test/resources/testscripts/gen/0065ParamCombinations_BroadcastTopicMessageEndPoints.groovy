request("BroadcastTopicMessageEndPoints", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["broadcastTopic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["configuration":"bogusData","broadcastTopic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["userNotSelectableMessageEndPoints":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["configuration":"bogusData","userNotSelectableMessageEndPoints":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["broadcastTopic":"bogusData","userNotSelectableMessageEndPoints":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["configuration":"bogusData","broadcastTopic":"bogusData","userNotSelectableMessageEndPoints":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["userSelectedMessageEndPoints":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicMessageEndPoints", ["configuration":"bogusData","userSelectedMessageEndPoints":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
