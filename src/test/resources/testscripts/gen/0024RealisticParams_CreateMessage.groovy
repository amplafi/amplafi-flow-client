request("CreateMessage", ["fsFlowTransitionLabel":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "content",
        "fsLookupKey": "CreateAlert_z26aj2cr",
        "fsParameters": {
            "messageCalendarable": false,
            "broadcastMessageType": "nrm",
            "broadcastProvider": 2
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [
        {
            "key": "MissingRequiredTracking",
            "parameters": ["language"]
        },
        {
            "key": "MissingRequiredTracking",
            "parameters": ["messageBody"]
        },
        {
            "key": "MissingRequiredTracking",
            "parameters": ["messageHeadline"]
        }
    ]}}
}""",ignorePathList)

