// This line must be the first line in the script

import java.util.HashMap

String newLine = System.getProperty("line.separator");
String usage =
    "CustomerProblemReport Script Help" + newLine +
    "This CustomerProblemReport script use to help the system administrator access" + newLine +
    "the various farreaches service configuration and user log information" + newLine +
    "Params" + newLine +
    "  verbose=<true/false>  To print detail json log" + newLine +
    "  userEmail=<userEmail>  The user that you want to inspect. If the userEmail is not specified. The overall report will be run" + newLine  + newLine +
    "  fromDate=<yyyy/MM/dd>  Limit the audit logs from date" + newLine +
    "  toDate=<yyyy/MM/dd>  Limit the audit logs to date" + newLine +

    "  apiMaxReturn=<apiMaxReturn>  The maximum number of the log entries that you want to return"+ newLine + newLine +

    "  externalApiMaxReturn=<number>  The maximum number of the log entries that you want to return";

description "CustomerProblemReport", """This tool is used to report and identify the customer problem
    This CustomerProblemReport script use to help the system administrator access
    the various farreaches service configuration and user log information
    """,
     [paramDef("verbose","To print detail json log",true,"false"),
      paramDef("userEmail","The user that you want to inspect. If the userEmail is not specified. The overall report will be run",true,"admin@amplafi.com"),
      paramDef("fromDate","Limit the audit logs from date",true,null),
      paramDef("toDate","Limit the audit logs to date",true,null),
      paramDef("apiMaxReturn","The maximum number of the log entries that you want to return",true,1000),
      paramDef("externalApiMaxReturn","The maximum number of the log entries that you want to return",true,1000),
      paramDef("publicUri","The public uri",false,null)];

def printHelp = {
    printTaskInfo "CustomerProblemReport Script Help"
    println usage
}

def addMessageEndPointToMap = {
    meps, entry ->
    def mepId = entry.opt("lookupKey") ;
    def publicUri2 = entry.opt("publicUri") ;
    def externalServiceDefinition = entry.opt("externalServiceDefinition") ;
    def extServiceUsername = entry.opt("extServiceUsername") ;
    def extServiceUserFullName = entry.opt("extServiceUserFullName") ;
    meps[mepId] = [
        "mepId": mepId, "publicUri": publicUri2, "externalServiceDefinition": externalServiceDefinition,
        "extServiceUsername": extServiceUsername, "extServiceUserFullName": extServiceUserFullName
    ];
}

def getMessageEndPoints = {
    apiKey1 ->
    setApiVersion("apiv1");
    setKey(apiKey1);

    request("MessageEndPointList", ["fsRenderResult":"json", "messageEndPointCompleteList": "true"]);

    def meps = [:] ;
    def entries = getResponseData();
    for(entry in entries) {
        addMessageEndPointToMap(meps, entry) ;
    }
    return meps;
}

def printMessageEndPoint = {
    mep ->
    println "MEP ID:           " + mep['mepId'] ;
    println "External Service: " + mep['externalServiceDefinition'] ;
    println "Public Uri:       " + mep['publicUri'] ;
    println "Username:         " + mep['extServiceUsername'] ;
    println "Full Name:        " + mep['extServiceUserFullName'] ;
}

def printMessageEndPoints = {
    meps ->
    printTaskInfo "Message End Points"
    def first = true ;
    for(mep in meps.values()) {
        if(!first) {
            println "-----------------------------------------------------------------------------------"
        }
        printMessageEndPoint(mep) ;
        first = false ;
    }
}


try {

    def suApiKey = getKey() ;
    // UserRoles flow has been removed
    // callScript("UserRoles",["apiKey": suApiKey, "publicUri": publicUri]);

    def apiKey = null ;
    if(publicUri != null) {
        // call other script and get return value.
        // CreateSuApiKey flow has been removed
        apiKey = callScript("CreateSuApiKey",["publicUri":publicUri,"userEmail":userEmail]);
    } else {
        apiKey = suApiKey ;
    }
    if(apiKey == null) {
        return ;
    }

    // This is a global settig that will effect subsequent scripts
    setKey(apiKey);

    callScript("AvailableCategories");//flow does not exists.

    // call other script
    callScript("AvailableExternalServices");//flow does not exists.

    def meps = getMessageEndPoints(apiKey) ;

    printMessageEndPoints(meps) ;

    callScript("ApiRequestAuditEntryLog",["apiKey": apiKey, "fromDate": fromDate, "toDate": toDate, "maxReturn": apiMaxReturn]);

    callScript("ExternalApiMethodCallLog",["apiKey": apiKey, "fromDate": fromDate, "toDate": toDate, "maxReturn": externalApiMaxReturn]);

    callScript("UserPost",["apiKey": apiKey, "fromDate": fromDate, "toDate": toDate]);//BroadcastEnvelopes does not exists.
} catch(Throwable ex) {

    println "Error: " + ex.getMessage() ;
    if(verbose) {
        ex.printStackTrace();
    }
}
