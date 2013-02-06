request("EligibleDestinationMessageEndPointListFlow", ["fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsLookupKey": "EligibleDestinationMessageEndPointListFlow_8k1ygwfa",
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

