package org.amplafi.flow.definitions;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * Describes a request to FarReaches service to execute or describe a flow.
 *
 * @author Haris Osmanagic
 *
 */
public class FlowRequestDescription {
    private String flowName;

    // a list of query parameters for this flow
    private List<NameValuePair> parameters;

    // if set to true, this instance is a request to describe a flow
    private boolean describe;

    /**
     * Constructor.
     * @param flowName flow to call
     * @param describe if true retur description for flow
     * @param parameters to flow
     */
    public FlowRequestDescription(String flowName,
                                  boolean describe,
                                  List<NameValuePair> parameters) {
        super();
        this.flowName = flowName;
        this.describe = describe;
        this.parameters = parameters;
    }

    /**
     * @return flow name
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * @param flowName - name of flow to call.
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    /**
     * @return parameters
     */
    public List<NameValuePair> getParameters() {
        return parameters;
    }

    /**
     * @param parameters - request params
     */
    public void setParameters(List<NameValuePair> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return is this a description request?
     */
    public boolean isDescribe() {
        return describe;
    }

    /**
     * @param describe - configure as a description request.
     */
    public void setDescribe(boolean describe) {
        this.describe = describe;
    }

}
