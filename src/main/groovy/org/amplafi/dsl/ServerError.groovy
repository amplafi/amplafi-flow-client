package org.amplafi.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.FlowResponse;
import org.amplafi.flow.utils.GeneralFlowRequest;


/**
 * This class represents an error code from the server. I.e. not http 200
 */
public class ServerError extends RuntimeException {

    def FlowResponse response;
	
    public ServerError(FlowResponse response){
        this.response = response ;
    }
	
	public String toString(){
		response.getErrorMessage();
	
	}

}
