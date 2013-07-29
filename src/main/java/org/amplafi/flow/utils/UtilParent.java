package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.AdminToolCommandLineOptions.API_KEY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.dsl.GroovyBindingFactory;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common functions and constants that several utility tools may need.
 */
public class UtilParent {
	/** Standard location for admin scripts. */
	public static final String DEFAULT_COMMAND_SCRIPT_PATH = "../farreaches-customer-service-client/src/main/resources/commandScripts";
	private Properties configProperties;
	private String comandScriptPath;
	private String configFileName;
	private Log log;

	/**
	 * Get the relative path by the absolute path.
	 * 
	 * @param filePath
	 *            is the full path to the script.
	 * @return relative path of the file.
	 */
	public String getRelativePath(String filePath) {
		String relativePath = filePath;
		try {
			String currentPath = new File(".").getCanonicalPath();
			if (filePath.contains(currentPath)) {
				relativePath = filePath.substring(currentPath.length());
			}
		} catch (Exception e) {
			// Do nothing
		}
		return relativePath;
	}

	/**
	 * Return the script parameters as a map of param name to param valye.
	 * 
	 * @param remainderList
	 *            is command arg list.
	 * @return map of the user input params.
	 */
	public Map<String, String> getCommandLineParameters(
			List<String> remainderList) {
		Map<String, String> map = new HashMap<String, String>();
		// On linux, options like param1=cat comes through as a single param
		// On windows they come through as 2 params.
		// To match options like param1=cat
		String patternStr = "(\\w+)=(\\S+)";
		Pattern p = Pattern.compile(patternStr);
		for (int i = 0; i < remainderList.size(); i++) {
			Matcher matcher = p.matcher(remainderList.get(i));
			if (matcher.matches()) {
				// If mathces then we are looking at param1=cat as a single
				// param
				map.put(matcher.group(1), matcher.group(2));
			} else {
				if (remainderList.size() > i + 1) {
					map.put(remainderList.get(i), remainderList.get(i + 1));
				}
				i++;
			}
		}
		return map;
	}

	// TODO Bruno clean this up
	/**
	 * Gets the program options, either from the command line, from the saved
	 * properties or asks the user.
	 * 
	 * @param cmdOptions
	 *            - Command line options
	 * @param key
	 *            - name of property
	 * @param defaultVal
	 *            - default value to suggest
	 * @return the option value.
	 * @throws IOException
	 */
	/*
	 * public String getOption(AbstractCommandLineClientOptions cmdOptions,
	 * String key, String defaultVal) throws IOException { Properties props =
	 * getProperties(); String value = null; if (cmdOptions.hasOption(key)) { //
	 * if option passed in on commandline then use that value =
	 * cmdOptions.getOptionValue(key); } else { // if option is in properties
	 * then use that String prefValue = props.getProperty(key, ""); if
	 * (cmdOptions.hasOption(NOCACHE) || prefValue.equals("")) { // prompt the
	 * user for the option emitOutput("Please, Enter : " + key +
	 * " ( Enter defaults to: " + defaultVal + ") "); value = getUserInput(key);
	 * if ("".equals(value)) { value = defaultVal; } } else { return prefValue;
	 * } } props.setProperty(key, value); return value; }
	 */

	/**
	 * Gets the configuration properties, loading it if hasn't been loaded.
	 * 
	 * @return configuration properties.
	 */
	public Properties getProperties() {
		if (configProperties == null) {
			configProperties = new Properties();
			try {
				// load a properties file
				configProperties.load(new FileInputStream(getConfigFileName()));
			} catch (IOException ex) {
				getLog().error("Error loading file " + getConfigFileName());
			}
		}
		return configProperties;
	}

	/**
	 * Saves the configuration properties, loading it if hasn't been loaded.
	 */
	public void saveProperties() {
		if (configProperties != null) {
			try {
				// load a properties file
				configProperties.store(
						new FileOutputStream(getConfigFileName()),
						"Farreach.es Admin tool properties");
			} catch (IOException ex) {
				getLog().error("Error saving file " + getConfigFileName());
			}
		}
	}

	/**
	 * @param msg
	 *            - message to emit
	 */

	public void emitOutput(String msg) {
		getLog().info(msg);
	}

	/**
	 * Get the logger for this class.
	 */
	public Log getLog() {
		if (this.log == null) {
			this.log = LogFactory.getLog(this.getClass());
		}
		return this.log;
	}

	/**
	 * Gets the script path for the tool.
	 * 
	 * @return path of the file commandScript
	 */
	public String getComandScriptPath() {
		if (comandScriptPath != null) {
			return comandScriptPath;
		} else {
			comandScriptPath = DEFAULT_COMMAND_SCRIPT_PATH;
		}
		return comandScriptPath;
	}

	/**
	 * @param comandScriptPath
	 *            the comandScriptPath to set
	 */
	public void setComandScriptPath(String comandScriptPath) {
		this.comandScriptPath = comandScriptPath;
	}

	/**
	 * Gets the script for creating a new api key path for the tool.
	 * 
	 * @return the path to the script for creating a new api key
	 */
	public String getApiKeyScriptPath() {
		return getComandScriptPath();
	}

	/**
	 * Gets the script path for the tool.
	 * 
	 * @return the name of the config file
	 */
	public String getConfigFileName() {
		return configFileName;
	}

	/**
	 * @param configFileName
	 *            the name of the config file
	 */
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

}
