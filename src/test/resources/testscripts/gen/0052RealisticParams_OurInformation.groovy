request("OurInformation", ["configuration":"abc","defaultLanguage":"null","rootUrl":"null","bannerUri":"null","broadcastProviderName":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "OurInformation_so3tqkmo",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "bannerUri": "null",
        "broadcastProviderName": "abc"
    }
}}""",ignorePathList)

request("OurInformation", ["configuration":"abc","contactUser":"null","contactInformation":"abc","locations":"abc","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "general",
        "fsLookupKey": "OurInformation_gyhpne4b",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
            "rootUrl": 4,
            "bannerUri": "null",
            "broadcastProviderName": "abc",
            "contactInformation": "abc",
            "locations": "abc"
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["defaultLanguage"]
    }]}}
}""",ignorePathList)

request("OurInformation", ["configuration":"abc","providerLocations":"['a','b','c']","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "general",
        "fsLookupKey": "OurInformation_n74r8yey",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
            "rootUrl": 4,
            "bannerUri": "null",
            "broadcastProviderName": "abc",
            "providerLocations": []
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["defaultLanguage"]
    }]}}
}""",ignorePathList)

request("OurInformation", ["configuration":"abc","roles":"['a','b','c']","newRoles":"['a','b','c']","permitAnonymous":"true","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "general",
        "fsLookupKey": "OurInformation_zylpccpq",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
            "rootUrl": 4,
            "bannerUri": "null",
            "broadcastProviderName": "abc",
            "permitAnonymous": true
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["defaultLanguage"]
    }]}}
}""",ignorePathList)

request("OurInformation", ["configuration":"abc","timezone":"null","browserUserTimeZoneOffset":"100","browserUserTimeZone":"null","dateFormat":"abc","timeFormat":"abc","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "general",
        "fsLookupKey": "OurInformation_mvxgbnnk",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
            "rootUrl": 4,
            "bannerUri": "null",
            "broadcastProviderName": "abc",
            "timezone": "GMT",
            "browserUserTimeZoneOffset": 100,
            "browserUserTimeZone": "GMT",
            "dateFormat": "abc",
            "timeFormat": "abc"
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["defaultLanguage"]
    }]}}
}""",ignorePathList)

request("OurInformation", ["configuration":"abc","externalServiceNatures":"['a','b','c']","externalServiceDefinitionsMap":"null","externalServiceDefinition":"null","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "general",
        "fsLookupKey": "OurInformation_3px73hxy",
        "fsParameters": {
            "configuration": "abc",
            "user": 4,
            "broadcastProvider": 2,
            "rootUrl": 4,
            "bannerUri": "null",
            "broadcastProviderName": "abc",
            "externalServiceDefinitionsMap": null
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["defaultLanguage"]
    }]}}
}""",ignorePathList)

