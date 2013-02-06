request("ChangePassword", ["authenticateOnFinish":"null","user":"null","authenticationProvider":"null","resettingPassword":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "changePasswordFlowActivity",
        "fsLookupKey": "ChangePassword_4nbvq2ay",
        "fsParameters": {
            "authenticateOnFinish": false,
            "resettingPassword": false
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "flow.password-new",
        "parameters": []
    }]}}
}""",ignorePathList)

