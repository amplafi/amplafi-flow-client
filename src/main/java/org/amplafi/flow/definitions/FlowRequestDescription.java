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

	public FlowRequestDescription(String flowName, boolean describe, List<NameValuePair> parameters) {
		super();

		this.flowName = flowName;
		this.describe = describe;
		this.parameters = parameters;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public List<NameValuePair> getParameters() {
		return parameters;
	}

	public void setParameters(List<NameValuePair> parameters) {
		this.parameters = parameters;
	}

	public boolean isDescribe() {
		return describe;
	}

	public void setDescribe(boolean describe) {
		this.describe = describe;
	}

}
