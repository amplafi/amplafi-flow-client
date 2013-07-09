package org.amplafi.flow.strategies;

public class RequestParameter {
	String name;
	String type;
	String req;

	public RequestParameter(String name, String type, String req) {
		this.name = name;
		this.type = type;
		this.req = req;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}

}
