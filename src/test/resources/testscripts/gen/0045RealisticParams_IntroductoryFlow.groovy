request("IntroductoryFlow", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","copyOriginalMessage":"null","deleteOriginalMessage":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("IntroductoryFlow", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","originalMessageBody":"abc","selectedEnvelopes":"['a','b','c']","language":"null","messageBody":"abc","messageHeadline":"abc","messageExcerpt":"abc","explicitMessageResources":"null","embeddedMessageResources":"null","messageResources":"null","publicUri":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsCurrentActivityByName": "selectLocation",
    "fsLookupKey": "IntroductoryFlow_2s1v3af3",
    "fsParameters": {
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
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
        "publicUri": "null",
        "derivedUrlNotRequired": true
    }
}}""",ignorePathList)

request("IntroductoryFlow", ["configuration":"abc","messageEndPointList":"['a','b','c']","messageEndPointUri":"null","messageSourcePointUri":"null","messageSourcePointList":"['a','b','c']","messageSourcePointUriMap":"null","connectionGroup":"null","connectionGroupList":"['a','b','c']","internalSourcePoint":"null","messageEndPointType":"null","messageSourcePointType":"null","externalServiceDefinitionsMap":"null","externalServiceDefinition":"null","externalServiceInstance":"null","newExternalServiceInstance":"null","defaultExternalServiceInstance":"null","newExternalServiceInstanceLookupKey":"abc","useDefaultExternalServiceInstanceIfNeeded":"null","externalCredential":"null","publicUri":"null","messageEndPoint":"null","messageSourcePoint":"null","validInsertionPointOffset":"null","insertionPointOffset":"abc","derivedUrlNotRequired":"null","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "content",
        "fsLookupKey": "IntroductoryFlow_3fqkvu7c",
        "fsParameters": {
            "broadcastMessageType": "nrm",
            "configuration": "abc",
            "broadcastProvider": 2,
            "publicUri": "null",
            "messageEndPointList": [],
            "messageEndPointUri": "null",
            "messageSourcePointUri": "null",
            "messageSourcePointList": [],
            "messageSourcePointUriMap": null,
            "connectionGroupList": [],
            "internalSourcePoint": false,
            "externalServiceDefinitionsMap": null,
            "newExternalServiceInstanceLookupKey": "abc",
            "useDefaultExternalServiceInstanceIfNeeded": false,
            "validInsertionPointOffset": "null",
            "insertionPointOffset": "abc",
            "derivedUrlNotRequired": false
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [
        {
            "key": "MissingRequiredTracking",
            "parameters": ["language"]
        },
        {
            "key": "MissingRequiredTracking",
            "parameters": ["messageBody"]
        },
        {
            "key": "MissingRequiredTracking",
            "parameters": ["messageHeadline"]
        }
    ]}}
}""",ignorePathList)

