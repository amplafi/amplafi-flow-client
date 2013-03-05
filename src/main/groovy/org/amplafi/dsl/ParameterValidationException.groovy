package org.amplafi.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;

/**
 * This represents a parameter vaildation error condition
 */
public class ParameterValidationException extends RuntimeException {
    public ParameterValidationException(String msg){
        super(msg);
    }

}
