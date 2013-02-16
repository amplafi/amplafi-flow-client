request("CategoryMessages", ["configuration":"abc","categorySelection":"null","fsRenderResult":"json"])

expect("""{
    "errorMessage": "Error running a flow",
    "exception": "While trying to start flow=CategoryMessages; launchMap={configuration=abc, flowAppearance=apiCall, fsRenderResult=json, callback=ampcb_720df5d558173cc402f59913355e9f5f1fb8b444cd7d43be01c89fe216500469, categorySelection=null}"
}""")

