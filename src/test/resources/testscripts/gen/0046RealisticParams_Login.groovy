request("Login", ["configuration":"abc","email":"abc","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "login",
        "fsLookupKey": "Login_y85evvcu",
        "fsParameters": {
            "configuration": "abc",
            "newSession": true,
            "email": "abc"
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [{
        "key": "error.invalidEmailPassword",
        "parameters": []
    }]}}
}""",ignorePathList)

