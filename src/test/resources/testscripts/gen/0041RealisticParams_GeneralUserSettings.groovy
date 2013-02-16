request("GeneralUserSettings", ["configuration":"abc","firstName":"abc","lastName":"abc","cellPhone":"abc","homePhone":"abc","defaultPhone":"null","defaultOfficePhone":"abc","includeInMessage":"null","sendExternal":"null","fsRenderResult":"json"])

def ignorePathList = ["/flowState/fsLookupKey/"];

expect("""{"flowState": {
    "fsComplete": true,
    "fsCurrentActivityByName": "GeneralInformation",
    "fsLookupKey": "GeneralUserSettings_feh5yx9j",
    "fsParameters": {
        "configuration": "abc",
        "user": 4,
        "broadcastProvider": 2,
        "firstName": "abc",
        "lastName": "abc",
        "cellPhone": "abc",
        "homePhone": "abc",
        "defaultOfficePhone": "abc",
        "includeInMessage": false,
        "sendExternal": false
    }
}}""",ignorePathList)

