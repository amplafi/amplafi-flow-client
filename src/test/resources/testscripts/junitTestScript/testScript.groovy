// This line must be the first line in the script
description "testScript", "Just an test script" 
import org.amplafi.flow.test.TestAdminTool;

log("This is testScript1");   


if (params&&params["param2"]){
    log(" param2 : "+ params["param2"]);
} 

if (params&&params["param1"]){
    log(" param1 : "+params["param1"]);
}


