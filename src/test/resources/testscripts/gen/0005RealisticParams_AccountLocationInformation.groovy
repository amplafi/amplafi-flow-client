request("AccountLocationInformation", ["configuration":"abc","providerLocations":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "AccountLocationInformation_rqf61zzs",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "providerLocations": []
    }
}}""",ignorePathList)

