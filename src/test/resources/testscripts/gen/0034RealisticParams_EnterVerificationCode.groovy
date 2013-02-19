request("EnterVerificationCode", ["callback":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "EnterVerificationCode_dzjlilvr",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "newSession": true,
        "callback": "ampcb_e2ea34097f9aba4f69ff7c095d227beabccef732971eb2539065997d297b12c9"
    }
}}""",ignorePathList)

