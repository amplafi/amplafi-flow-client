request("BroadcastTopicEntry", ["configuration":"abc","topicName":"abc","topicTag":"abc","topicSummary":"abc","topicAdditionalDescription":"abc","availableCategories":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "BroadcastTopicEntry",
        "fsLookupKey": "BroadcastTopicEntry_7fxhjmlt",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
            "topicName": "abc",
            "topicTag": "abc",
            "topicSummary": "abc",
            "topicAdditionalDescription": "abc",
            "availableCategories": []
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "InconsistencyTracking.topicTag",
        "parameters": ["category already exists with that tag."]
    }]}}
}""",ignorePathList)

