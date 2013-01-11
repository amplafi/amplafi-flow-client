request("SignupIndividual", ["configuration":"","externalAuthenticationUserProfile":"","authenticationProvider":"","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["configuration":"","lastName":"","firstName":"","email":"","cellPhone":"","homePhone":"","defaultPhone":"","officePhone":"","title":"","newUser":"","defaultContactUser":"","termsOfService":"","teenagerOrOlder":"","captchaProvided":"","authenticationProvider":"","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["fsCallbackNextFlow":"","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["fsFlowTransitionLabel":"","fsRenderResult":"json"])

checkReturnedValidJson()
