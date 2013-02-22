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

 
description "CustomerProblemReport", "This tool is used to report and identify the customer problem", "${usage}";


def printHelp = { 
    printTaskInfo "CustomerProblemReport Script Help"
    println usage
}

def addMessageEndPointToMap = {
    meps, entry ->
    def mepId = entry.opt("lookupKey") ;
    def publicUri = entry.opt("publicUri") ;
    def externalServiceDefinition = entry.opt("externalServiceDefinition") ;
    def extServiceUsername = entry.opt("extServiceUsername") ;
    def extServiceUserFullName = entry.opt("extServiceUserFullName") ;
    meps[mepId] = [
        "mepId": mepId, "publicUri": publicUri, "externalServiceDefinition": externalServiceDefinition, 
        "extServiceUsername": extServiceUsername, "extServiceUserFullName": extServiceUserFullName
    ];
}

def getMessageEndPoints = { 
    apiKey ->
    setApiVersion("apiv1");
    setKey(apiKey);
    
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

def userEmail = "admin@amplafi.com" ;
if (params && params["userEmail"]) {
    userEmail = params["userEmail"] ;
}

def publicUri = null ;
if (params && params["publicUri"]) {
    publicUri = params["publicUri"] ;
}

def apiMaxReturn = "1000"
if (params && params["apiMaxReturn"]) {
    apiMaxReturn = params["apiMaxReturn"] ;
}

def externalApiMaxReturn = "1000" ;
if (params && params["externalApiMaxReturn"]) {
    externalApiMaxReturn = params['externalApiMaxReturn'] ;
}

def fromDate = null ;
if (params && params["fromDate"]) {
    fromDate = params['fromDate'] ;
}

def toDate = null ;
if (params && params["toDate"]) {
    toDate = params['toDate'] ;
}

def verbose = false; 
if (params && params["verbose"]) {
    verbose = "true".equals(params["verbose"]) ;
}

try {
    def suApiKey = getKey() ;
    
    callScript("UserRoles",["apiKey": suApiKey, "publicUri": publicUri]);
    
    def apiKey = null ;
    if(publicUri != null) {
        // call other script and get return value.
        apiKey = callScript("CreateSuApiKey",["publicUri":publicUri,"userEmail":userEmail]);
    } else {
        apiKey = suApiKey ;
    }
    if(apiKey == null) {
        return ;
    }
    
    // This is a global settig that will effect subsequent scripts
    setKey(apiKey);
    callScript("AvailableCategories");
    
    // call other script
    callScript("AvailableExternalServices");

    def meps = getMessageEndPoints(apiKey) ;
    printMessageEndPoints(meps) ;
    
    callScript("ApiRequestAuditEntryLog",["apiKey": apiKey, "fromDate": fromDate, "toDate": toDate, "maxReturn": apiMaxReturn]);
    
    callScript("ExternalApiMethodCallLog",["apiKey": apiKey, "fromDate": fromDate, "toDate": toDate, "maxReturn": externalApiMaxReturn]);
    
    callScript("UserPost",["apiKey": apiKey, "fromDate": fromDate, "toDate": toDate]);
} catch(Throwable ex) {
    println "Error: " + ex.getMessage() ;
    if(verbose) {
        ex.printStackTrace();
    }
}
