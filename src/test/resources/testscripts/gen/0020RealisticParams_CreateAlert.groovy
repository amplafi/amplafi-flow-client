request("CreateAlert", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","copyOriginalMessage":"null","deleteOriginalMessage":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateAlert", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","originalMessageBody":"abc","selectedEnvelopes":"['a','b','c']","language":"null","messageBody":"abc","messageHeadline":"abc","messageExcerpt":"abc","explicitMessageResources":"null","embeddedMessageResources":"null","messageResources":"null","publicUri":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsCurrentActivityByName": "showMessages",
    "fsLookupKey": "CreateAlert_2ifugaei",
    "fsParameters": {
        "messageCalendarable": false,
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
        "publicUri": "null"
    }
}}""",ignorePathList)

request("CreateAlert", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","messageCalendarable":"null","defaultTopic":"null","availableCategories":"null","selectedTopics":"null","selectedTags":"null","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateAlert", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","newStatus":"null","messageSourcePointTypes":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateAlert", ["fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "content",
        "fsLookupKey": "CreateAlert_b1va3vm7",
        "fsParameters": {
            "messageCalendarable": false,
            "broadcastMessageType": "nrm",
            "broadcastProvider": 2
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

request("CreateAlert", ["fsFlowTransitionLabel":"abc","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "content",
        "fsLookupKey": "CreateAlert_d6ueie2l",
        "fsParameters": {
            "messageCalendarable": false,
            "broadcastMessageType": "nrm",
            "broadcastProvider": 2
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

