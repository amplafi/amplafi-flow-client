request("AccountCalendarInformation", ["configuration":"abc","timezone":"null","browserUserTimeZoneOffset":"100","browserUserTimeZone":"null","dateFormat":"abc","timeFormat":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "AccountCalendarInformation_pvwticdt",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "timezone": "GMT",
        "browserUserTimeZoneOffset": 100,
        "browserUserTimeZone": "GMT",
        "dateFormat": "abc",
        "timeFormat": "abc"
    }
}}""",ignorePathList)

