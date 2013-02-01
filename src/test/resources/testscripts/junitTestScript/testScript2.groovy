// This line must be the first line in the script
description "testScript2", "Just an test2 script", "usage test" 
import org.amplafi.flow.test.AdminToolTest;

AdminToolTest.testBuffer.append("This is testScript2");

if (params&&params["param2"]){
    AdminToolTest.testBuffer.append(" param2 : "+ params["param2"]);
} 

if (params&&params["param1"]){
    AdminToolTest.testBuffer.append(" param1 : "+params["param1"]);
}

