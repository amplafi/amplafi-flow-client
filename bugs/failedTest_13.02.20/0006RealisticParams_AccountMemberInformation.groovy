request("AccountMemberInformation", ["configuration":"abc","roles":"['a','b','c']","newRoles":"['a','b','c']","permitAnonymous":"true","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "AccountMemberInformation_5agu9eic",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "permitAnonymous": true
    }
}}""",ignorePathList)

