request("CreateCancel", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","copyOriginalMessage":"null","deleteOriginalMessage":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateCancel", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","originalMessageBody":"abc","selectedEnvelopes":"['a','b','c']","language":"null","messageBody":"abc","messageHeadline":"abc","messageExcerpt":"abc","explicitMessageResources":"null","embeddedMessageResources":"null","messageResources":"null","publicUri":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsCurrentActivityByName": "store",
    "fsLookupKey": "CreateCancel_wn9ura7e",
    "fsParameters": {
        "deleteOriginalMessage": true,
        "configuration": "abc",
        "originalBroadcastEnvelopes": [],
        "broadcastEnvelopes": [],
        "originalMessageBody": "abc",
        "selectedEnvelopes": [],
        "messageBody": "abc",
        "messageHeadline": "abc",
        "messageExcerpt": "abc",
        "explicitMessageResources": "null",
        "embeddedMessageResources": [],
        "messageResources": [],
        "publicUri": "null"
    }
}}""",ignorePathList)

request("CreateCancel", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","newStatus":"null","messageSourcePointTypes":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

