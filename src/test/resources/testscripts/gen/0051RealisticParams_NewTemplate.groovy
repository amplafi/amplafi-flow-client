request("NewTemplate", ["templateName":"abc","templateDescription":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "NewTemplate_z69ash3w",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "templateName": "abc",
        "templateDescription": "abc"
    }
}}""",ignorePathList)

