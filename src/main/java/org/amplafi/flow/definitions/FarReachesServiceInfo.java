package org.amplafi.flow.definitions;

import java.util.Properties;

/**
 * A class which instances provide information about FarReaches service: host address, port used,
 * and API version.
 */
public class FarReachesServiceInfo {
    /** Host string. */
    private String host;

    /** Port string. */
    private String port;

    /** Api version. */
    private String apiVersion;

    /** Path prefix to service call. */
    private String path;

    /** This is a prefix of a host */
    private String PROTOCOL = "http://";

    private boolean productionMode;

    private Properties properties;

    public FarReachesServiceInfo(Properties properties) {
        this.properties = properties;
        boolean productionMode = properties.getProperty("production").equals("true");
        this.setProductionMode(productionMode);
    }
    /**
     * Constructor.
     *
     * @param host host including protocol
     * @param port port
     * @param apiVersion e.g. apiv1
     */
    @Deprecated
    public FarReachesServiceInfo(String host, String port, String path, String apiVersion) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.apiVersion = apiVersion;
    }

    public FarReachesServiceInfo(FarReachesServiceInfo farReachesServiceInfo) {
        this(farReachesServiceInfo.properties);
    }

    /**
     * Constructor.
     *
     * @param host host including protocol
     * @param port port
     * @param apiVersion e.g. apiv1
     */
    @Deprecated
    public FarReachesServiceInfo(String host, String port, String apiVersion) {
        this(host, port, null, apiVersion);
    }

    public boolean isProductionMode() {
        return productionMode;
    }
    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
        if (productionMode) {
            this.host = properties.getProperty("productionHostUrl");
            this.port = properties.getProperty("productionPort");
        } else {
            this.host = properties.getProperty("testHostUrl");
            this.port = properties.getProperty("testPort");
        }
        this.path = properties.getProperty("path");
        this.apiVersion = properties.getProperty("apiv");
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

    public String getPrompt() {
        return this.isProductionMode()?"PRODUCTION ("+this.getRequestString()+")>":"local ("+this.getRequestString()+")>";
    }
}
