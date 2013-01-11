request("SignupIndividual", ["configuration":"bogusData","externalAuthenticationUserProfile":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["configuration":"bogusData","lastName":"bogusData","firstName":"bogusData","email":"bogusData","cellPhone":"bogusData","homePhone":"bogusData","defaultPhone":"bogusData","officePhone":"bogusData","title":"bogusData","newUser":"bogusData","defaultContactUser":"bogusData","termsOfService":"bogusData","teenagerOrOlder":"bogusData","captchaProvided":"bogusData","authenticationProvider":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["fsCallbackNextFlow":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("SignupIndividual", ["fsFlowTransitionLabel":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
