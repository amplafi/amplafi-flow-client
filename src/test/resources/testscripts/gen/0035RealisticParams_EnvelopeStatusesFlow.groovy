request("EnvelopeStatusesFlow", ["broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","externalContentId":"null","externalContentIds":"['a','b','c']","messageEndPointTypes":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "EnvelopeStatusesFlow_7p8ch9iy",
    "fsParameters": {
        "broadcastEnvelopes": [],
        "externalIdentifierResourcePackage": [
            "//example.com/",
            "null",
            "message"
        ],
        "externalContentId": [null],
        "externalContentIds": [
            ["a"],
            ["b"],
            ["c"]
        ]
    }
}}""",ignorePathList)

