request("CategoryFlow", ["fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "CategoryFlow_ylbstbza",
    "fsParameters": null
}}""",ignorePathList)

