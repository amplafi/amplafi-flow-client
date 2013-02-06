request("FinishSignUp", ["user":"null","broadcastProviderType":"null","sendNotification":"null","campaign":"null","selfName":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "createUserFlowActivity",
        "fsLookupKey": "FinishSignUp_jv2zguoc",
        "fsParameters": {
            "newSession": true,
            "sendNotification": false,
            "selfName": "abc",
            "defaultPhone": "cel",
            "termsOfService": false,
            "teenagerOrOlder": false,
            "roleType": "adm"
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [
        {
            "key": "MissingRequiredTracking",
            "parameters": ["firstName"]
        },
        {
            "key": "MissingRequiredTracking",
            "parameters": ["lastName"]
        },
        {
            "key": "MissingRequiredTracking",
            "parameters": ["email"]
        }
    ]}}
}""",ignorePathList)

request("FinishSignUp", ["configuration":"abc","firstName":"abc","lastName":"abc","email":"abc","campaign":"null","cellPhone":"abc","homePhone":"abc","defaultPhone":"null","officePhone":"abc","title":"abc","newUser":"null","defaultContactUser":"null","termsOfService":"null","teenagerOrOlder":"null","user":"null","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "complete",
        "fsLookupKey": "FinishSignUp_cys5eifq",
        "fsParameters": {
            "newSession": true,
            "broadcastProviderType": "b",
            "sendNotification": false,
            "configuration": "abc",
            "firstName": "abc",
            "lastName": "abc",
            "email": "abc",
            "cellPhone": "abc",
            "homePhone": "abc",
            "officePhone": "abc",
            "title": "abc",
            "newUser": false,
            "defaultContactUser": false,
            "termsOfService": false,
            "teenagerOrOlder": false
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["selfName"]
    }]}}
}""",ignorePathList)

request("FinishSignUp", ["authenticateOnFinish":"null","user":"null","authenticationProvider":"null","resettingPassword":"null","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "complete",
        "fsLookupKey": "FinishSignUp_bkfzxuls",
        "fsParameters": {
            "newSession": true,
            "broadcastProviderType": "b",
            "sendNotification": false,
            "resettingPassword": false
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["selfName"]
    }]}}
}""",ignorePathList)

request("FinishSignUp", ["campaign":"null","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "complete",
        "fsLookupKey": "FinishSignUp_rr8paznp",
        "fsParameters": {
            "newSession": true,
            "broadcastProviderType": "b",
            "sendNotification": false
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["selfName"]
    }]}}
}""",ignorePathList)

request("FinishSignUp", ["fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "complete",
        "fsLookupKey": "FinishSignUp_2f1vfeqn",
        "fsParameters": {
            "newSession": true,
            "broadcastProviderType": "b",
            "sendNotification": false
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["selfName"]
    }]}}
}""",ignorePathList)

request("FinishSignUp", ["configuration":"abc","messagesOwners":"null","fsRenderResult":"json"])

ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "complete",
        "fsLookupKey": "FinishSignUp_8nn5yx8f",
        "fsParameters": {
            "newSession": true,
            "broadcastProviderType": "b",
            "sendNotification": false,
            "configuration": "abc",
            "messagesOwners": []
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "MissingRequiredTracking",
        "parameters": ["selfName"]
    }]}}
}""",ignorePathList)

