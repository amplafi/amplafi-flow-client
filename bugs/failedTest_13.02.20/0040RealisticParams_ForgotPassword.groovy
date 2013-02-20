request("ForgotPassword", ["email":"abc","captchaProvided":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsLookupKey": "ForgotPassword_dwl2idid",
    "fsParameters": {
        "fsFlowTransitions": null,
        "fsSuggestedNextFlowType": null,
        "newSession": true,
        "email": "abc",
        "captchaProvided": false
    }
}}""",ignorePathList)

