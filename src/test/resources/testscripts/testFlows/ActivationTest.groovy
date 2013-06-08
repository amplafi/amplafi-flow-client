
//restore the original key.
def old_key = getKey();



//call TemperyApiKey and PermenantApiKey
def permApiKey = callScript("..\\farreaches-customer-service-client\\src\\main\\resources\\commandScripts\\GetNewPermanentKey.groovy",["callbackHost":"example.com"]);

// assert:the PermenantApiKey is calling correct on the server.
def statusCode = getHttpStatusCode();
if(statusCode != 200){
	fail("The activication stage : request PermenantApiKey to the server failed");
}

setKey(permApiKey);


//call EnvelopeStatusList flow
//def response = callScript("src\\test\\resources\\loadTests\\loadTest1.groovy");
def response = requestResponse( "EnvelopeStatusList" , ["externalContentIds": "[]"]);

// assert:the EnvelopeStatusList is calling correct on the server.
statusCode = getHttpStatusCode();
if(statusCode != 200){
	fail("The activication stage : request EnvelopeStatusList to the server failed");
}


response = requestResponse("PaidUntil",[:]);

// assert:the PaidUntil is calling correct on the server.
statusCode = getHttpStatusCode();
if(statusCode != 200){
	fail("The activication stage : request PaidUntil to the server failed");
}


def mepList = callScript("..\\farreaches-customer-service-client\\src\\main\\resources\\commandScripts\\GetMessageEndPointsList.groovy");

// assert:the MessageEndPointsList is calling correct on the server.
statusCode = getHttpStatusCode();
if(statusCode != 200){
	fail("The activication stage : request MessageEndPointList to the server failed");
}

//Give the original key back.
setKey(old_key);
