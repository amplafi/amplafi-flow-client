request("CreateRevoke", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","copyOriginalMessage":"null","deleteOriginalMessage":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsCurrentActivityByName": "store",
    "fsLookupKey": "CreateRevoke_aoax64us",
    "fsParameters": {
        "fsAltFinished": "SaveAndPublish",
        "deleteOriginalMessage": false,
        "configuration": "abc",
        "originalBroadcastEnvelopes": [],
        "broadcastEnvelopes": [],
        "copyOriginalMessage": false,
        "externalContentId": [null],
        "externalContentIds": [
            ["a"],
            ["b"],
            ["c"]
        ]
    }
}}""",ignorePathList)

request("CreateRevoke", ["configuration":"abc","broadcastMessageType":"null","originalBroadcastEnvelope":"null","originalBroadcastEnvelopes":"['a','b','c']","broadcastEnvelope":"null","broadcastEnvelopes":"['a','b','c']","newStatus":"null","messageSourcePointTypes":"null","standardMessageSourcePointType":"null","externalContentId":"null","externalContentIds":"['a','b','c']","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsCurrentActivityByName": "store",
    "fsLookupKey": "CreateRevoke_zaqhfp9c",
    "fsParameters": {
        "fsAltFinished": "SaveAndPublish",
        "deleteOriginalMessage": true,
        "configuration": "abc",
        "originalBroadcastEnvelopes": [],
        "broadcastEnvelopes": [],
        "externalContentId": [null],
        "externalContentIds": [
            ["a"],
            ["b"],
            ["c"]
        ]
    }
}}""",ignorePathList)

