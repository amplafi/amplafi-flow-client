request("ConfigureMessagePointCategories", ["headerText":"abc","configuration":"abc","messageEndPointList":"['a','b','c']","messageEndPointUri":"null","messageSourcePointUri":"null","messageSourcePointList":"['a','b','c']","messageSourcePointUriMap":"null","connectionGroup":"null","connectionGroupList":"['a','b','c']","internalSourcePoint":"null","messageEndPointType":"null","messageSourcePointType":"null","externalServiceDefinitionsMap":"null","externalServiceDefinition":"null","externalServiceInstance":"null","newExternalServiceInstance":"null","defaultExternalServiceInstance":"null","newExternalServiceInstanceLookupKey":"abc","useDefaultExternalServiceInstanceIfNeeded":"null","externalCredential":"null","publicUri":"null","messageEndPoint":"null","messageSourcePoint":"null","availableCategories":"null","selectedTopics":"null","allCategories":"true","categorySelection":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "topics",
        "fsLookupKey": "ConfigureMessagePointCategories_ksn7qa2f",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
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
            "publicUri": "null",
            "availableCategories": [],
            "selectedTopics": [],
            "allCategories": true
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": [
            "externalServiceDefinition",
            "No external service definition found to create a service instance."
        ]
    }]}}
}""",ignorePathList)

