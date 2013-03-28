

//println "In script " + getKey()
setApiVersion("api");

def response = requestResponse( "EnvelopeStatusList" , ["externalContentIds": """[[92,"msg"],[89,"msg"],[73,"msg"],[71,"msg"],[69,"msg"],[44,"msg"],[41,"msg"],[37,"msg"],[32,"msg"],[27,"msg"],[23,"msg"],[18,"msg"],[4,"msg"],[1,"msg"]]"""]);

if(response.hasError()){
    throw new Exception(response.getErrorMessage());
}

def entries = getResponseData();
//println entries
