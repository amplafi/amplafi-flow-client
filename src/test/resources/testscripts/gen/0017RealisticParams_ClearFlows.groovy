request("ClearFlows", ["flows":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "ClearFlows_xs9ta2is",
    "fsParameters": {
        "fsFlowTransitions": null,
        "flows": [
            "a",
            "b",
            "c"
        ]
    }
}}""",ignorePathList)

