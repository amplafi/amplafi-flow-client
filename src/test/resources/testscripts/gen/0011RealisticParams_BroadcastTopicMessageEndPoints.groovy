request("BroadcastTopicMessageEndPoints", ["configuration":"abc","userNotSelectableMessageEndPoints":"['a','b','c']","userSelectedMessageEndPoints":"['a','b','c']","originalUserSelectedMessageEndPoints":"['a','b','c']","userSelectableMessageEndPoints":"['a','b','c']","fsRenderResult":"json"])

expect("""{
    "errorMessage": "Error running a flow",
    "exception": "While trying to start flow=BroadcastTopicMessageEndPoints; launchMap={userNotSelectableMessageEndPoints=['a','b','c'], configuration=abc, flowAppearance=apiCall, originalUserSelectedMessageEndPoints=['a','b','c'], userSelectedMessageEndPoints=['a','b','c'], fsRenderResult=json, userSelectableMessageEndPoints=['a','b','c'], callback=ampcb_e2ea34097f9aba4f69ff7c095d227beabccef732971eb2539065997d297b12c9}"
}""")

