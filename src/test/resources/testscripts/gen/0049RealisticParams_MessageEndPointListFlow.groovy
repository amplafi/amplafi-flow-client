request("MessageEndPointListFlow", ["messageEndPointCompleteList":"null","messageEndPointTypes":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "MessageEndPointListFlow_2mpmynuc",
    "fsParameters": {"messageEndPointCompleteList": false}
}}""",ignorePathList)

