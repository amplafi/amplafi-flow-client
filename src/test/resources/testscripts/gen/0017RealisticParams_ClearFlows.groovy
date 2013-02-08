request("ClearFlows", ["flows":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "ClearFlows_rbzfs815",
    "fsParameters": {
        "fsFlowTransitions": null,
        "flows": [
            "a",
            "b",
            "c"
        ]
    }
}}""",ignorePathList)

