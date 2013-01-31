// This line must be the first line in the script
description "testScript2", "Just an test2 script", "usage test" 

import javax.net.*;


// proof it works
Socket socket = SocketFactory.getDefault() .createSocket();

if (params&&params["param2"]){
    println "param2 : "+ params["param2"]
} 

if (params&&params["param1"]){
    println "param1 : "+params["param1"]

}

// Make a request to the server 
request("EligibleExternalServiceInstancesFlow", ["eligibleExternalServiceInstanceMap":"bogusData","eligibleExternalServiceInstances":"bogusData","fsRenderResult":"json"] );


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

