package org.amplafi.flow.utils;

import java.util.List;

import org.apache.http.NameValuePair;

public class SimpleFlowRequest {
	private String flowName;
	private List<NameValuePair> parameters;

	public SimpleFlowRequest(String flowName, List<NameValuePair> parameters) {
		super();
		this.flowName = flowName;
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

}
