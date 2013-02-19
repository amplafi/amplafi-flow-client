request("CategoryMessages", ["configuration":"abc","categorySelection":"null","fsRenderResult":"json"])

expect("""{
    "errorMessage": "Error running a flow",
    "exception": "While trying to start flow=CategoryMessages; launchMap={configuration=abc, flowAppearance=apiCall, fsRenderResult=json, callback=ampcb_e2ea34097f9aba4f69ff7c095d227beabccef732971eb2539065997d297b12c9, categorySelection=null}"
}""")

