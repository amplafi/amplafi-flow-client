request("AccountGeneralInformation", ["configuration":"abc","defaultLanguage":"null","rootUrl":"null","bannerUri":"null","broadcastProviderName":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "AccountGeneralInformation_1kugyge4",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "bannerUri": "null",
        "broadcastProviderName": "abc"
    }
}}""",ignorePathList)

