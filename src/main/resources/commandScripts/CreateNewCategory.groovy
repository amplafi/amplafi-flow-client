/**
 * Obtains a new category from the wire server
 */
description "CreateNewCategory", "create a new category", [paramDef("topicName","The name of the category topic name",true,"How To use java"),
                                                            paramDef("topicTag","The tag of the category topic",true,"My first java application:hello world!"),
                                                            paramDef("topicAdditionalDescription","The description of the category topic",true,"class,function,main"),
                                                            paramDef("externalContentId","The external content id",true,"[85,'tag']")];

def category = null;

//def oldKey = getKey();

//emitOutput( "NEW CATEGORY IS : " + category );

//def tempApiKey = callScript("GetTempApiKey",["apiCall":"Category"]);

def response = requestPost("Category",[
                                        "topicName":"${topicName}",
                                        "topicTag":"${topicTag}",
                                        "topicAdditionalDescription":"${topicAdditionalDescription}",
                                        "externalContentId":"${externalContentId}"]);

//emitOutput( "response category = " + response );

return category;