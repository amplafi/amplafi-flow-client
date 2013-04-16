/**
 * Obtains a new category from the wire server
 */
description "CreateNewCategory", "create a new category", [paramDef("topicName","The name of the category topic name",true,"How To use java"),
                                                            paramDef("topicTag","The tag of the category topic",true,"My first java application:hello world!"),
                                                            paramDef("topicAdditionalDescription","The description of the category topic",true,"class,function,main"),
                                                            paramDef("externalContentId","The external content id",true,"[88,'tag']")];
setApiVersion("api");
def response = requestPost("Category",[
                                        "topicName":topicName,
                                        "topicTag":topicTag,
                                        "topicAdditionalDescription":topicAdditionalDescription,
                                        "externalContentId":externalContentId]);

println "response" + response;

return response;