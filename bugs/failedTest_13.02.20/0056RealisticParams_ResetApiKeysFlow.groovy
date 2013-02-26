request("ResetApiKeysFlow", ["configuration":"abc","publicUri":"null","recreate":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "ResetApiKeysFlow_7h7yoong",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "publicUri": [],
        "recreate": false
    }
}}""",ignorePathList)

