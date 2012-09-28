package org.amplafi.flow.utils;

/**
 * A class which instances provide information about FarReaches service: host address, port used, and API version.
 * @author haris
 *
 */
// TODO TO_HARIS Find a better place for this class
public class FarReachesServiceInfo {
	private String host;
	private String port;
	private String apiVersion;

	public FarReachesServiceInfo(String host, String port, String apiVersion) {
		super();
		this.host = host;
		this.port = port;
		this.apiVersion = apiVersion;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
}
