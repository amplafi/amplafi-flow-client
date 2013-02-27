// This line must be the first line in the script
//description "example", "Just an example script" 
description "example", "Just an example script", [paramDef("param1","test param1",true,"100"),
                                                    paramDef("param2","test param1",false,"100")]

//,[["param1","most favorite animal"], ["param2","other most favorite animal"], ["param3","dadgu"] ]

// You can set certain globals in the script if you want. 
// These will affect all susequent requests
// Don't normally do this though. As the default ones are good. 

// setHost("www.google.com");
// setPort("90");
// setApiVersion("apiv12");
// setKey("newkey");

// Example Import
import javax.net.*;




    println ">>>>param1 : "+param1;




    println ">>>>>>param2 : "+param2;

println("Call open port");
openPort(9090, 20,{ println("Paul says In do now");  },
{ request, response ->

        println("Paul says In handle " + request.getParameter("param1"))
        response.getWriter().println("<h1>Hello Paul</h1>");
//throw new Exception();
});

return;

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

println "call example2"
callScript("example2",["verbose":false]);

// print the result

println "The lookup key in the first row of the response is: <<" + row1LookupKey + ">>"
