package org.amplafi.flow.definitions;

/**
 * A class which instances provide information about FarReaches service: host address, port used,
 * and API version.
 */
public class FarReachesServiceInfo {
    /** Host string. */
    private final String host;

    /** Port string. */
    private final String port;

    /** Api version. */
    private String apiVersion;

    /** Path prefix to service call. */
    private final String path;

    /** This is a prefix of a host */
    private String PROTOCOL = "http://";

    /**
     * Constructor.
     *
     * @param host host including protocol
     * @param port port
     * @param apiVersion e.g. apiv1
     */
    public FarReachesServiceInfo(String host, String port, String path, String apiVersion) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.apiVersion = apiVersion;
    }

    /**
     * Constructor.
     *
     * @param host host including protocol
     * @param port port
     * @param apiVersion e.g. apiv1
     */
    public FarReachesServiceInfo(String host, String port, String apiVersion) {
        this(host, port, null, apiVersion);
    }

    public FarReachesServiceInfo(FarReachesServiceInfo other) {
        this.host = other.host;
        this.port = other.port;
        this.path = other.path;
        this.apiVersion = other.apiVersion;
    }

    /**
     * Returns the host string.
     *
     * @return host string
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the port.
     *
     * @return port
     */
    public String getPort() {
        return port;
    }

    /**
     * Gets the port.
     *
     * @return port.
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Set the api version.
     *
     * @param apiVersion - e.g. apiv1
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * Add a prefix of host.
     */
    private String addHttpPrexBeforeString(String host) {
        if (!host.contains(PROTOCOL)) {
            host = PROTOCOL + host;
        }
        return host;
    }

    /**
     * @return the request string
     */
    public String getRequestString() {
        String pathD = "";
        if (this.path != null) {
            pathD = this.path + "/";
        }

        return addHttpPrexBeforeString(this.host) + ":" + this.port + "/" + pathD + this.apiVersion;
    }

    @Override
    public FarReachesServiceInfo clone() {
        return new FarReachesServiceInfo(this);
    }
}
