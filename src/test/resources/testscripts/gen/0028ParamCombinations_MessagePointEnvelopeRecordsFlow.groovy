request("MessagePointEnvelopeRecordsFlow", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("MessagePointEnvelopeRecordsFlow", ["messagePointEnvelopeRecords":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
