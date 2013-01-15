// This line must be the first line in the script
description "example", "Just an example script"

// You can set certain globals in the script if you want. 
// Don't normally do this though. As the default ones are good. 

host = "www.google.com"
port = "80";
apiVersion = "apiv1";
//key = null;


// Make a request to the server 
request("EligibleExternalServiceInstancesFlow", ["eligibleExternalServiceInstanceMap":"bogusData","eligibleExternalServiceInstances":"bogusData","fsRenderResult":"json"]);


// Pretty Print The Response
prettyPrintResponse();


// Access the response data 
def row1LookupKey = getResponseData().get(0).get("lookupKey")


// print the result

println "The lookup key in the first row of the response is: <<" + row1LookupKey + ">>"