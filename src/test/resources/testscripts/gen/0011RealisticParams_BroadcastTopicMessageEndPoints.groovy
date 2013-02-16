request("BroadcastTopicMessageEndPoints", ["configuration":"abc","userNotSelectableMessageEndPoints":"['a','b','c']","userSelectedMessageEndPoints":"['a','b','c']","originalUserSelectedMessageEndPoints":"['a','b','c']","userSelectableMessageEndPoints":"['a','b','c']","fsRenderResult":"json"])

expect("""{
    "errorMessage": "Error running a flow",
    "exception": "While trying to start flow=BroadcastTopicMessageEndPoints; launchMap={userNotSelectableMessageEndPoints=['a','b','c'], configuration=abc, flowAppearance=apiCall, originalUserSelectedMessageEndPoints=['a','b','c'], userSelectedMessageEndPoints=['a','b','c'], fsRenderResult=json, userSelectableMessageEndPoints=['a','b','c'], callback=ampcb_720df5d558173cc402f59913355e9f5f1fb8b444cd7d43be01c89fe216500469}"
}""")

