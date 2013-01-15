// This line must be the first line in the script
description "example", "Just an example script"

// You can set certain globals in the script if you want. 
// These will affect all susequent requests
// Don't normally do this though. As the default ones are good. 

// setHost("www.google.com");
// setPort("90");
// setApiVersion("apiv12");
// setKey("newkey");


// Make a request to the server 
request("EligibleExternalServiceInstancesFlow", ["eligibleExternalServiceInstanceMap":"bogusData","eligibleExternalServiceInstances":"bogusData","fsRenderResult":"json"]);


// Pretty Print The Response
prettyPrintResponse();


// Access the response data 

def data = getResponseData();

def row1LookupKey = null;

if (data instanceof JSONArray){
	row1LookupKey = data.get(0).get("lookupKey")	
}



// print the result

println "The lookup key in the first row of the response is: <<" + row1LookupKey + ">>"