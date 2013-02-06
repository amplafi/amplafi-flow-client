request("MessageEndPointListFlow", ["messageEndPointCompleteList":"null","messageEndPointTypes":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "MessageEndPointListFlow_zl5v6p2z",
    "fsParameters": {"messageEndPointCompleteList": false}
}}""",ignorePathList)

