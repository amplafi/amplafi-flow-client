package org.amplafi.flow.definitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public FarReachesServiceInfo(FarReachesServiceInfo farReachesServiceInfo) {
        this.propertiesMap = new HashMap<>(farReachesServiceInfo.propertiesMap);
        this.setMode(farReachesServiceInfo.activeKey);
    }

    private Properties loadProperties(String key) {
        Properties properties = loadPropertyFile("global");
        properties.putAll(loadPropertyFile(key));
        return putProperties(key, properties);
    }

    /**
     * For testing ; store properties at the supplied key location.
     * @param key
     * @param properties
     * @return
     */
    public Properties putProperties(String key, Properties properties) {
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
        try(InputStream in = new File(propertyFileName).exists() ? 
                new FileInputStream(propertyFileName) : 
                getClass().getResourceAsStream("/" + propertyFileName)) {
            if (in != null) {
                properties.load(in);
            } else {
                System.err.println(propertyFileName + ": No properties file found. Check for " + propertyFileName);                
            }
        } catch (IOException e) {
            System.err.println(propertyFileName + ": No properties file found. Check for " + propertyFileName);
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
    public FarReachesServiceInfo(String host, String port, String apiVersion) {
        this.host = host;
        this.port = port;
        this.path = null;
        this.apiVersion = apiVersion;
    }

    public void setMode(String mode) {
        if ( "l".equalsIgnoreCase(mode)) {
            mode = "local";
        } else if ( "p".equalsIgnoreCase(mode)) {
            mode = "production";
        } else if ( "t".equalsIgnoreCase(mode)) {
            mode = "test";
        }
        this.activeKey = mode;
        this.loadProperties(mode);
        this.host = this.getProperty("host");
        this.port = this.getProperty("port");
        this.path = this.getProperty("path");
        this.apiVersion = this.getProperty("apiv");
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
        Properties properties = this.propertiesMap.get(this.activeKey);
        return properties.getProperty(key);
    }
}
