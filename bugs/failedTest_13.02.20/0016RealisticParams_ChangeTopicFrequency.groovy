request("ChangeTopicFrequency", ["messageEndPoint":"null","topic":"null","updateFrequency":"null","fsRenderResult":"json"])

expect("""{"flowState":{"fsComplete":true,"fsCurrentActivityByName":"complete","fsLookupKey":"ChangeTopicFrequency_x3ku7bk5","fsParameters":{"topic":""}}}""")

request("ChangeTopicFrequency", ["fsFlowTransitionLabel":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "changeFrequency",
        "fsLookupKey": "ChangeTopicFrequency_vtlujzoc",
        "fsParameters": null
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["updateFrequency"]
    }]}}
}""",ignorePathList)

