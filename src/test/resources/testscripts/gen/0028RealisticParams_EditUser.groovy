request("EditUser", ["configuration":"abc","firstName":"abc","lastName":"abc","email":"abc","campaign":"null","cellPhone":"abc","homePhone":"abc","defaultPhone":"null","officePhone":"abc","title":"abc","newUser":"null","defaultContactUser":"null","termsOfService":"null","teenagerOrOlder":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{
    "flowState": {
        "fsComplete": true,
        "fsCurrentActivityByName": "EditUser",
        "fsLookupKey": "EditUser_tnlwvl83",
        "fsParameters": {
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
            "teenagerOrOlder": false,
            "roleType": "adm"
        }
    },
    "validationErrors": {"flow-result": {"flowValidationTracking": [
        {
            "key": "flow.did-not-agree-to-tos",
            "parameters": []
        },
        {
            "key": "flow.too-young",
            "parameters": []
        }
    ]}}
}""",ignorePathList)

