request("PendingList", ["headerText":"abc","configuration":"abc","selectedActions":"null","selectedPartners":"['a','b','c']","messageSourcePointTypes":"null","messageCalendarable":"null","availableCategories":"null","availableSources":"['a','b','c']","selectedSources":"['a','b','c']","messageSourceNature":"null","messageCountBySourceNature":"null","daysPastExpiration":"100","daysBeforeExpiration":"100","minimumBroadcastEnvelopesDisplayed":"100","maximumBroadcastEnvelopesDisplayed":"100","orderingType":"null","allResultsCount":"null","broadcastEnvelopes":"['a','b','c']","indexOffset":"100","broadcastEnvelopesPerPage":"100","messageEndPointList":"['a','b','c']","messageEndPointUri":"null","messageSourcePointUri":"null","messageSourcePointList":"['a','b','c']","messageSourcePointUriMap":"null","connectionGroup":"null","connectionGroupList":"['a','b','c']","internalSourcePoint":"null","messageEndPointType":"null","messageSourcePointType":"null","externalServiceDefinitionsMap":"null","externalServiceDefinition":"null","externalServiceInstance":"null","newExternalServiceInstance":"null","defaultExternalServiceInstance":"null","newExternalServiceInstanceLookupKey":"abc","useDefaultExternalServiceInstanceIfNeeded":"null","externalCredential":"null","publicUri":"null","messageEndPoint":"null","messageSourcePoint":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsLookupKey": "PendingList_iu98116w",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
            "selectedActions": null,
            "selectedPartners": [],
            "messageCalendarable": false,
            "availableCategories": [],
            "availableSources": [],
            "selectedSources": [],
            "messageCountBySourceNature": null,
            "eligibleDestinationMessageEndPointListMap": null,
            "daysPastExpiration": 100,
            "daysBeforeExpiration": 100,
            "minimumBroadcastEnvelopesDisplayed": 100,
            "maximumBroadcastEnvelopesDisplayed": 100,
            "broadcastEnvelopes": [],
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

