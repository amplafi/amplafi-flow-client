request("EnvelopeStatusesFlow", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["broadcastEnvelope":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["broadcastEnvelopes":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["broadcastEnvelope":"bogusData","broadcastEnvelopes":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["externalContentId":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["broadcastEnvelope":"bogusData","externalContentId":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["broadcastEnvelopes":"bogusData","externalContentId":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["broadcastEnvelope":"bogusData","broadcastEnvelopes":"bogusData","externalContentId":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["externalContentIds":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("EnvelopeStatusesFlow", ["broadcastEnvelope":"bogusData","externalContentIds":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
