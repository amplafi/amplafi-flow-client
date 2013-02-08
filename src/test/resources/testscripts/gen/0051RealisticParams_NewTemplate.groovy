request("NewTemplate", ["templateName":"abc","templateDescription":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "NewTemplate_no1o92n9",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "templateName": "abc",
        "templateDescription": "abc"
    }
}}""",ignorePathList)

