request("EligibleDestinationMessageEndPointListFlow", ["fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsLookupKey": "EligibleDestinationMessageEndPointListFlow_pp2cm6yq",
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

