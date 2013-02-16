request("EligibleDestinationMessageEndPointListMapFlow", ["fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsLookupKey": "EligibleDestinationMessageEndPointListMapFlow_v877jx9q",
        "fsParameters": {"eligibleDestinationMessageEndPointListMap": null}
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": [
            "broadcastEnvelope",
            ""
        ]
    }]}}
}""",ignorePathList)

