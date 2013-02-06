request("AccountContactInformation", ["configuration":"abc","contactUser":"null","contactInformation":"abc","locations":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "AccountContactInformation_6xhvbklt",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "contactInformation": "abc",
        "locations": "abc"
    }
}}""",ignorePathList)

