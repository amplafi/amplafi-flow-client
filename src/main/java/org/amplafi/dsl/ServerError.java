package org.amplafi.dsl;

import org.amplafi.flow.utils.FlowResponse;

public class ServerError extends RuntimeException {
    
    private FlowResponse response;

    public ServerError(FlowResponse response) {
        this.response = response;
    }

    public String toString() {
        return response.getErrorMessage();
    }
}
