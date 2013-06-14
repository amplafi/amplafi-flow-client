package org.amplafi.dsl;

import org.amplafi.flow.utils.FlowResponse;

/**
 * TO_DAISY: Javadoc?
 * 
 * @author aectann
 *
 */
public class ServerError extends RuntimeException {
    
    private FlowResponse response;

    public ServerError(FlowResponse response) {
        this.response = response;
    }

    public String toString() {
        return response.getErrorMessage();
    }
}
