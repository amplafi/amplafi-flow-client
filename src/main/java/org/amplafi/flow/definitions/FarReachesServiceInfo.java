package org.amplafi.flow.definitions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    private String activeKey;

    private Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

    public FarReachesServiceInfo() {
    }

    private Properties loadProperties(String key) {
        Properties properties = loadPropertyFile("global");
        properties.putAll(loadPropertyFile(key));
        this.propertiesMap.put(key, properties);
        String keyFileName = properties.getProperty("keyfile");
        if ( keyFileName != null) {
            System.out.println("Loading keys from :" + keyFileName);
            try (FileInputStream fis = new FileInputStream(keyFileName)) {
                properties.load(fis);
            } catch (IOException e) {
                System.out.println(keyFileName + ": No keyfile found. Check property keyfile in " + "farreaches.cs-"+key+".properties");
            }
        }
        return properties;
    }

    private Properties loadPropertyFile(String key) {
        Properties properties = new Properties();
        String propertyFileName = "farreaches.cs-"+key+".properties";
        System.out.println("Loading properties from :" + propertyFileName);
        try(FileInputStream fileInputStream = new FileInputStream(propertyFileName)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.out.println(propertyFileName + ": No properties file found. Check for " + propertyFileName);
        }
        return properties;
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

    @Deprecated
    public FarReachesServiceInfo(FarReachesServiceInfo farReachesServiceInfo) {
        this.activeKey = farReachesServiceInfo.activeKey;
        this.apiVersion = farReachesServiceInfo.apiVersion;
        this.host= farReachesServiceInfo.host;
        this.path = farReachesServiceInfo.path;
        this.port = farReachesServiceInfo.port;
        this.propertiesMap = new HashMap<>(farReachesServiceInfo.propertiesMap);
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

    public void setMode(String mode) {
        if ( "l".equalsIgnoreCase(mode)) {
            mode = "local";
        } else if ( "p".equalsIgnoreCase(mode)) {
            mode = "production";
        } else if ( "t".equalsIgnoreCase(mode)) {
            mode = "test";
        }
        Properties properties = this.loadProperties(mode);
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.path = properties.getProperty("path");
        this.apiVersion = properties.getProperty("apiv");
        this.activeKey = mode;
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

        return addHttpPrexBeforeString(this.host) + ":" + this.port + "/" + pathD + ( this.apiVersion != null? this.apiVersion: "");
    }

    public String getPrompt() {
        return this.activeKey+ "("+this.getRequestString()+")>";
    }

    public String getProperty(String key) {
        return this.propertiesMap.get(this.activeKey).getProperty(key);
    }
}
