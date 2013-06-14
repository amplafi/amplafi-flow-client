package org.amplafi.dsl;

public class EarlyExitException extends RuntimeException {
    
    ScriptDescription desc;
    
    public EarlyExitException (ScriptDescription desc){
        this.desc = desc;
    }

}
