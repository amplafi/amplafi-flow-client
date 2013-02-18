description "UserPost", "User Post", "apiKey=key, fromDate=date, toDate=date";


def toAmplafiJSONCalendar = { 
    dateString -> 
    def formater = new java.text.SimpleDateFormat("yyyy/MM/dd") ;
    def date = formater.parse(dateString) ;
    def json = sprintf('{"timeInMillis": %1d, "timeZoneID": "GMT"}', date.getTime()) ;
    return json ;
}

def getUserPostInfo = {
    apiKey, fromDate, toDate->
    
    setApiVersion("suv1");
    setKey(apiKey);
    def reqParams = ["fsRenderResult":"json"] ;
    if(fromDate != null) {
        reqParams["fromDate"] = toAmplafiJSONCalendar(fromDate) ;
    }
    if(toDate != null) {
        reqParams["toDate"] = toAmplafiJSONCalendar(toDate) ;
    }
    
    request("BroadcastEnvelopesFlow", reqParams);
    def result = getResponseData() ;
    return result;
}

def printUserPostStatisticByCategory = { entries ->
    printTaskInfo "User Post By Categories"
    def categoriesStat = [:] ;
    for(entry in entries) {
        def broadcastEnvelope = entry.get("broadcastEnvelope")
        def selectedTopics = broadcastEnvelope.get("selectedTopics")
        def messageEndPointEnvelopeRecord = entry.get("messageEndPointEnvelopeRecord") ;
        for(topic in selectedTopics) {
            def name = topic.get("name");
            if(categoriesStat[name] == null) {
                categoriesStat[name] = [
                    "name": name, "entityId": topic.optString("entityId"), 
                    "externalIds": [broadcastEnvelope.optString("publicUri")], 
                    "messageEndPoint": [:], 
                    "postCount": 0
                ] ;
            }
            categoriesStat[name]["postCount"] += 1 ;
            for(endPoint in messageEndPointEnvelopeRecord) {
                def messagePointBroadcastTopics = endPoint.get("messagePointBroadcastTopics") ;
                def matchTopic = false ;
                for(messagePointBroadcastTopic in messagePointBroadcastTopics) {
                    if(name.equals(messagePointBroadcastTopic.get("name"))) {
                        matchTopic = true;
                        break ;
                    }    
                }
                if(!matchTopic) {
                    continue ;
                }
                
                def externalEntityStatus = endPoint.optString("externalEntityStatus") ;
                if(endPoint.has("publicUri")) {
                    categoriesStat[name]["externalIds"] << endPoint.get("publicUri") ;
                }
                def messagePointId = endPoint.get("messagePointId") ;
                if(categoriesStat[name]["messageEndPoint"][messagePointId] == null) {
                    def messagePointUri = endPoint.get("messagePointUri") ;
                    categoriesStat[name]["messageEndPoint"][messagePointId] = ["messagePointId" : messagePointId, "messagePointUri": messagePointUri] ;
                }
            }
        }
    }
    
    for(category in categoriesStat.values()) {
        println category["name"] + ":" ;
        println "  Entity Id: " + category["entityId"] 
        println "  External Ids: "
        for(externalId in category["externalIds"]) {
            println "    " + externalId
        }
        println "  Post Count: " + category["postCount"] ;
        println "  Message End Point: " 
        for(messageEndPoint in category["messageEndPoint"].values()) {
            println sprintf('%1$10s     %2$-40s', messageEndPoint["messagePointId"], messageEndPoint["messagePointUri"])
        }
        println "\n\n"
    }
}

def printUserPostStatisticByMessagePoint = { entries ->
    printTaskInfo "User Post By Message End Points"
    def mepStats = [:] ;
    for(entry in entries) {
        def broadcastEnvelope = entry.get("broadcastEnvelope")
        def selectedTopics = broadcastEnvelope.get("selectedTopics") ;
        def messageEndPointEnvelopeRecord = entry.get("messageEndPointEnvelopeRecord") ;
        for(mep in messageEndPointEnvelopeRecord) {
            def mepId = mep.get("messagePointId");
            def mepExternalServiceDefinition = mep.get("externalServiceDefinition") ;
            def mepMessagePointUri = mep.get("messagePointUri") ;
            def mepBroadcastTopics = mep.get("messagePointBroadcastTopics") ;
            if(mepStats[mepId] == null) {
                mepStats[mepId] = [
                    "messagePointId": mepId, "externalServiceDefinition": mepExternalServiceDefinition,
                    "messagePointUri": mepMessagePointUri, "categories": [:], "postCount": 0
                ] ;
            }
            mepStats[mepId]["postCount"] += 1 ;
            for(mepBroadcastTopic in mepBroadcastTopics) {
                def mepBroadcastTopicName = mepBroadcastTopic.get("name");
                def matchTopic = false ;
                for(selectedTopic in selectedTopics) {
                    if(mepBroadcastTopicName.equals(selectedTopic.get("name"))) {
                        matchTopic = true ;
                        break ;
                    }
                }
                if(matchTopic) {
                    mepStats[mepId]["categories"][mepBroadcastTopicName] = ["entityId": mepBroadcastTopic.get("entityId"), "name": mepBroadcastTopicName] ;
                }
            }
        }
    }
    
    for(mepStat in mepStats.values()) {
        println mepStat["messagePointUri"]
        println "  Farreaches Id: " + mepStat["messagePointId"]
        println "  External Service Definition: " + mepStat["externalServiceDefinition"]
        println "  Categories: "
        for(category in mepStat["categories"].values()) {
            println sprintf('%1$10s %2$-30s', category["entityId"], category["name"]) ;
        }
        println "  Posts: "  + mepStat["postCount"] ;
        println "\n"
    }
}

def printUserPostInfo = { entries->
    printTaskInfo "User Post Info"
    def messageThreads = [:] ;
    for(entry in entries) {
        def broadcastEnvelope = entry.getJSONObject("broadcastEnvelope")
        def messageThreadId = broadcastEnvelope.get("messageThreadId");
        if(messageThreads[messageThreadId] == null) {
            messageThreads[messageThreadId] = [] ;
        }
        messageThreads[messageThreadId] << entry ;
    }
    
    for(messageThreadEntry in messageThreads) {
        println "Message Thread: " + messageThreadEntry.getKey() ;
        for(entry in messageThreadEntry.getValue()) {
            def broadcastEnvelope = entry.getJSONObject("broadcastEnvelope")
            println "  FarReaches Id:" + broadcastEnvelope.optString('entityId') ;
            println "  Title:" + broadcastEnvelope.optString('headLine') ;
            println "  Body:" + broadcastEnvelope.optString('messageText') ;
            def topicNames = "" ;
            for(selectedTopic in broadcastEnvelope.optJSONArray('selectedTopics')) {
                if(topicNames.length() > 0) {
                    topicNames += ", " ;
                }
                topicNames += selectedTopic.optString("name")  ;
            }
            println "  Selected Topics:" + topicNames;
           
            println "  MessageEndPointEnvelopeRecord:"
            def messageEndPointEnvelopeRecords = entry.optJSONArray("messageEndPointEnvelopeRecord") ; 
            println "    External Ids: "
            for(record in messageEndPointEnvelopeRecords) {
                def externalEntityStatus = record.optString("externalEntityStatus") ;
                if("pcd".equals(externalEntityStatus)) {
                   def externalServiceDefinition = record.optString("externalServiceDefinition") ;
                   def externalContentId = record.optString("externalContentId") ;
                   def publicUri = record.optString("publicUri") ;
                   println "      " + externalServiceDefinition + ": " + externalContentId + " (" + publicUri + ")" ;
                }
            }
            
            println "    Transmission: "
            for(record in messageEndPointEnvelopeRecords) {
                def externalEntityStatus = record.optString("externalEntityStatus") ;
                def messagePointUri = record.optString("messagePointUri") ;
                def unblockCompletedTime = "" ;
                if(record.has("unblockCompletedTime")) {
                    unblockCompletedTime = record.optString("unblockCompletedTime") ;
                }
                def status = null ;
                if("pcd".equals(externalEntityStatus)) {
                    status = "Successful" 
                } else if("nvr".equals(externalEntityStatus)) {
                    status = "Ignore" 
                } else {
                    status = "Failed(" + externalEntityStatus + ")" ; 
                }
                
                println "      " + messagePointUri ;
                println sprintf('        %1$-15s%2$40s', status, unblockCompletedTime) ;
            }
            println ""
        }
        println "\n" ;
    }
}

def apiKey = null ;
if (params && params["apiKey"]) {
    apiKey = params['apiKey'] ;
}

def fromDate = null ;
if (params && params["fromDate"]) {
    fromDate = params['fromDate'] ;
}

def toDate = null ;
if (params && params["toDate"]) {
    toDate = params['toDate'] ;
}

def userPostInfos = getUserPostInfo(apiKey, fromDate, toDate) ;
printUserPostStatisticByCategory(userPostInfos) ;
printUserPostStatisticByMessagePoint(userPostInfos) ;
printUserPostInfo(userPostInfos) ;