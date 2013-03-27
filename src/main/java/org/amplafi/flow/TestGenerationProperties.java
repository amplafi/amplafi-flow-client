package org.amplafi.flow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
/**
 * Provides convenient access to the system properties used by wire server
 * tests.
 *
 * @author Paul Smout
 */
public final class TestGenerationProperties {

    /**
     * Prevent instantiation of this class.
     */
    private TestGenerationProperties() {
    }

    /**
     * Singleton instance.
     */
    private static TestGenerationProperties instance = null;

    /**
     * @return instance.
     */
    public static TestGenerationProperties getInstance() {
        if (instance == null) {
            instance = new TestGenerationProperties();
            instance.init();
        }
        return instance;
    }

    /**
     * @return the requestUriString
     */
    public FarReachesServiceInfo getRequestUriString() {
        return requestUriString;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return the ignoredFlows
     */
    public Set<String> getIgnoredFlows() {
        return ignoredFlows;
    }

    /**
     * These static variables are overridden in the static initializer below.
     */
    private FarReachesServiceInfo requestUriString;

    /**
     * The api key for the wire server.
     */
    private String apiKey = "dummyKey";

    /**
     * The following Set contains flows that seem broken or suspicious. Having
     * been identified and reported they are awaiting fixing and will be exempt
     * from the test These values are set in the pom.xml in test configuration.
     */
    private Set<String> ignoredFlows;

    /** JSON property name for activities. */
    public static final String JSON_ACTIVITY_KEY = "activities";
    /** JSON property name for parameters. */
    public static final String JSON_PARAMETER_KEY = "parameters";
    /** JSON property name for parameter name. */
    public static final String JSON_PARAMETER_NAME_KEY = "name";
    /** JSON property name for error messages. */
    public static final String JSON_GENERIC_ERROR_MESSAGE_KEY = "errorMessage";
    /** Normal wire server error messages. */
    public static final String JSON_GENERIC_ERROR_MESSAGE =
                                          "Exception while running flowState";
    /** Host param property. */
    public static final String HOST_PROPERTY_KEY = "host";
    /** Port param property. */
    public static final String PORT_PROPERTY_KEY = "port";
    /** Key param property. */
    public static final String API_PROPERTY_KEY = "key";

    /**
     * Obtain the system properties for the test These may be set up in the
     * pom.xml or passed in from the command line with -D options.
     */
    private void init() {
        apiKey = System.getProperty(API_PROPERTY_KEY, "");
        String host = System.getProperty(HOST_PROPERTY_KEY,
                "sandbox.farreach.es");
        String port = System.getProperty(PORT_PROPERTY_KEY, "8080");
        requestUriString = new FarReachesServiceInfo( host, port, "apiv1");
        String ignoredFlowsStr = System.getProperty("ignoreFlows", "");
        String[] ignoredFlowsArr = ignoredFlowsStr.split(",");
        ignoredFlows = new HashSet<String>(Arrays.asList(ignoredFlowsArr));
    }
}
