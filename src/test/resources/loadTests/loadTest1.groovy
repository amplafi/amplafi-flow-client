import groovy.sql.Sql
import groovy.sql.DataSet
//import java.sql.Connection
//import java.sql.DriverManager
//import javax.sql.DataSource

description "loadTestEnvelopeStatusList", "load test for EnvelopeStatusList flow", [paramDef("dbUserName","local database user name",true,"amplafi"),
                                                                                    paramDef("dbPwd","local database password",true,"amplafi"),
                                                                                    paramDef("ids","the param externalContentIds's value",true,"")];

setApiVersion("api");

// get external ids from local db
// local db username and password,default is amplafi

def externalContentIds;

externalContentIds = "[";

//manual list of ids
if(ids!=null){
    
    def idArray = ids.split(",");
    
    for(def id:idArray){
    
        externalContentIds = externalContentIds + "[" + id + ",\"msg\"],";
        
    
    }
    
}else{
    // if ( cache does not have ids )
    if(!getStash()['data']){
        
        // put data in cache
        synchronized(Sql.class){
        
            if(!getStash()['data']){
            
                //get external_id from external_identifier_tracking
                
                def sql = Sql.newInstance("jdbc:mysql://localhost:3306/amplafi", dbUserName,dbPwd, "com.mysql.jdbc.Driver");
                
                sql.eachRow("select distinct external_id from external_identifier_tracking where base_uri='//daisy.com/'") { row ->
                
                    def external_id = row.EXTERNAL_ID;
                    
                    externalContentIds = externalContentIds + "[" + external_id + ",\"msg\"],";
                    
                }
                
                sql.close();
                
                getStash()['data'] =externalContentIds;
                println "#########################get data from database !!!!!!!!!!!!!!!!!!!!";
            }
            
            
            externalContentIds = getStash()['data'];
            
        }
        
        
        
    }else{
        // else use data in cache
        externalContentIds = getStash()['data'];
        
        //println "#########################get data from cache####################";
    }
    
}

//externalContentIds = externalContentIds.substring(0, externalContentIds.length()-1);//remove the last comma

externalContentIds = externalContentIds + "]";


println "externalContentIds = " + externalContentIds;

//send param externalContentIds fixed.
//def response = requestResponse( "EnvelopeStatusList" , ["externalContentIds": """[[43,\"msg\"],[41,\"msg\"],[39,\"msg\"],[37,\"msg\"],[35,\"msg\"],[31,\"msg\"],[29,\"msg\"],[27,\"msg\"],[25,\"msg\"],[23,\"msg\"],[21,\"msg\"],[18,\"msg\"],[16,\"msg\"],[4,\"msg\"],[1,\"msg\"]]"""]);



def response = requestResponse( "EnvelopeStatusList" , ["externalContentIds": externalContentIds]);

/*

{
    "flowTitle": "message:EnvelopeStatusList",
    "activities": [{
        "activity": "FA",
        "activityTitle": "message:flow.activity.f-a.title",
        "invisible": true,
        "finishing": false,
        "parameters": [
            {
                "name": "broadcastEnvelopes",
                "type": "List",
                "req": "optional"
            },
            {
                "name": "externalContentId",
                "type": "JSONArray",
                "req": "optional"
            },
            {
                "name": "externalContentIds",
                "type": "List",
                "req": "optional"
            },
            {
                "name": "messageEndPointTypes",
                "type": "List",
                "req": "optional"
            }
        ]
    }]
}
*/

if(response.hasError()){
    throw new Exception(response.getErrorMessage());
}


def entries = getResponseData();
//println entries;
