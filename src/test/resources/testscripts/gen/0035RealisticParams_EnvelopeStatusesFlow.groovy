request("EnvelopeStatusesFlow", ["broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","externalContentId":"null","externalContentIds":"['a','b','c']","messageEndPointTypes":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "EnvelopeStatusesFlow_r3wo23sx",
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

