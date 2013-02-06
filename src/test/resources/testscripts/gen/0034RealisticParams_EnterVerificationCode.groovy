request("EnterVerificationCode", ["callback":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "EnterVerificationCode_eg5xhhcz",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "newSession": true,
        "callback": "ampcb_720df5d558173cc402f59913355e9f5f1fb8b444cd7d43be01c89fe216500469"
    }
}}""",ignorePathList)

