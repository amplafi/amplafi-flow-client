package org.amplafi.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;

/**
 * This class contains various methods for loading and running FlowTestDSL scripts
 */
public class EarlyExitException extends RuntimeException {

    ScriptDescription desc = null
    public EarlyExitException (ScriptDescription desc){
        this.desc = desc;
    }

}
