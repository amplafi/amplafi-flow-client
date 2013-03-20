/**
 * Configure message endpoint categories.
 */
description "ConfigureMessageEndPointCategories", "Configure message endpoint categories", [paramDef("callbackHost","The host to callback to",true,"example.com")];




def externalServiceDefinition = requestPost("ConfigureExtServices",["externalServiceDefinition":"testExternalServiceName",
                                                          "fsRedirectUrl":"http://www.linkedin.com/",
                                                          "redirectProperties":"messageEndPoint"]);//TODO:externalServiceDefinition is not found.


def msgEndPointList = requestPost("MessageEndPointList",["messageEndPointTypes":"['ext']",
                                                     "messageEndPointCompleteList":"true"]);//TODO:externalServiceDefinition is not found.
                                                     
                                                     
def msgEndPointList = requestPost("ActivateMessageEndPoint",["messageEndPoint":"mep_0"]);////TODO:externalServiceDefinition is not found.

emitOutput( "msgEndPointList : " + msgEndPointList );
                                                   
def response = requestPost("ConfigureMessagePointCategories",["externalCategorySelection":"[85,'tags']",
                                                               "messageEndPoint":"facebook.com",
                                                              "externalServiceDefinition":"testExternalServiceName"]);//TODO:externalServiceDefinition is not found.


//return response;

