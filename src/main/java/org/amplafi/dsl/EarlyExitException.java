package org.amplafi.dsl;

/**
 * TO_DAISY: Javadoc?
 * 
 * @author aectann
 *
 */
public class EarlyExitException extends RuntimeException {
    
    ScriptDescription desc;
    
    public EarlyExitException (ScriptDescription desc){
        this.desc = desc;
    }

}
