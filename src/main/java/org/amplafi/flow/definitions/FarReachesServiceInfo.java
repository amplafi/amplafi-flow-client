package org.amplafi.flow.definitions;

/**
 * A class which instances provide information about FarReaches service: host address, port used,
 * and API version.
 * @author Haris Osmanagic
 *
 */
public class FarReachesServiceInfo {
    /** Host string. */
    private String host;
    /** Port string. */
    private String port;
    /** Api version. */
    private String apiVersion;

    /**
     * Constructor.
     * @param host host including protocol
     * @param port port
     * @param apiVersion e.g. apiv1
     */
    public FarReachesServiceInfo(String host, String port, String apiVersion) {
        super();
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
    }

    /**
     * Returns the host string.
     * @return host string
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host string.
     * @param host - host string
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port.
     * @return port
     */
    public String getPort() {
        return port;
    }

    /**
     * Sets the port string.
     * @param port - port string
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Gets the port.
     * @return port.
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Set the api version.
     * @param apiVersion - e.g. apiv1
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
