request("FindBroadcastProviderFlow", ["selectedBroadcastProvider":"null","providerFindQueryResult":"['a','b','c']","providerFindQuery":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "FindBroadcastProviderFlowActivity_0",
        "fsLookupKey": "FindBroadcastProviderFlow_p5tm944x",
        "fsParameters": {
            "providerFindQueryResult": [],
            "providerFindQuery": "abc"
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "InconsistencyTracking.empty-partner",
        "parameters": []
    }]}}
}""",ignorePathList)

