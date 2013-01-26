request("BroadcastTopicEntry", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["broadcastTopic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["configuration":"bogusData","broadcastTopic":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["topicName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["configuration":"bogusData","topicName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["broadcastTopic":"bogusData","topicName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["configuration":"bogusData","broadcastTopic":"bogusData","topicName":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["topicTag":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("BroadcastTopicEntry", ["configuration":"bogusData","topicTag":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
