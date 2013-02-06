request("CreateEvent", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","copyOriginalMessage":"null","deleteOriginalMessage":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateEvent", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","originalMessageBody":"abc","selectedEnvelopes":"['a','b','c']","language":"null","messageBody":"abc","messageHeadline":"abc","messageExcerpt":"abc","explicitMessageResources":"null","embeddedMessageResources":"null","messageResources":"null","publicUri":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "calendaring",
        "fsLookupKey": "CreateEvent_nh4waso1",
        "fsParameters": {
            "messageCalendarable": true,
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
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": [
            "broadcastEnvelope",
            ""
        ]
    }]}}
}""",ignorePathList)

request("CreateEvent", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","messageCalendarable":"null","defaultTopic":"null","availableCategories":"null","selectedTopics":"null","selectedTags":"null","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateEvent", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","deadline":"null","deadlineTime":"null","deadlineData":"null","timezone":"null","browserUserTimeZoneOffset":"100","browserUserTimeZone":"null","calendarInfo":"null","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateEvent", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","physicalLocation":"null","locationFindQueryResult":"['a','b','c']","locationQuery":"null","providerLocations":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateEvent", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","newStatus":"null","messageSourcePointTypes":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

expect("""{"errorMessage": "Failed to render flow state. Cause: language(scope=flowLocal, usage=use) :com.amplafi.foundation.Language: PropertyValueProvider threw an exception. propertyValueProvider=com.amplafi.core.member.flows.properties.LanguageFlowPropertyValueProvider@3fa312"}""")

request("CreateEvent", ["fsFlowTransitionLabel":"abc","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "content",
        "fsLookupKey": "CreateEvent_evtwtw3v",
        "fsParameters": {
            "broadcastMessageType": "nrm",
            "messageCalendarable": true,
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

