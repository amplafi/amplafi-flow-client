package org.amplafi.flow.utils;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * TO_HARIS: This looks very similar to {@link FlowRequestDescription}
 * and named similarly to {@link GeneralFlowRequest}.. what's the difference?
 * Please provide javadocs. Also it looks like you can use inheritance to eliminate
 * some code duplication.
 *
 *
 */
public class SimpleFlowRequest {
	private String flowName;
	private List<NameValuePair> parameters;
	private boolean describe;

	public SimpleFlowRequest(String flowName, boolean describe, List<NameValuePair> parameters) {
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
