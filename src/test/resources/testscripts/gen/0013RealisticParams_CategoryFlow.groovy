request("CategoryFlow", ["fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "CategoryFlow_iloeplhl",
    "fsParameters": null
}}""",ignorePathList)

