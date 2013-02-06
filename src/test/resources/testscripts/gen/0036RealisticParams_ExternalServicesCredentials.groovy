request("ExternalServicesCredentials", ["configuration":"abc","externalServiceNatures":"['a','b','c']","externalServiceDefinitionsMap":"null","externalServiceDefinition":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "ExternalServicesCredentials_zhkh6zg8",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "externalServiceDefinitionsMap": null
    }
}}""",ignorePathList)

