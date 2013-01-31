// This line must be the first line in the script
description "testScript", "Just an test script" 
import org.amplafi.flow.test.AdminToolTest;
   
AdminToolTest.testBuffer.append("This is testScript");

if (params&&params["param2"]){
    AdminToolTest.testBuffer.append(" param2 : "+ params["param2"]);
} 

if (params&&params["param1"]){
    AdminToolTest.testBuffer.append(" param1 : "+params["param1"]);
}


