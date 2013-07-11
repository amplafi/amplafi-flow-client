package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.AdminToolCommandLineOptions.API_KEY;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.API_VERSION;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.HOST;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.PORT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.dsl.BindingFactory;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.json.JSONArray;

import com.sworddance.util.NotNullIterator;

/**
 * Command line interface for running scripts to communicate with the
 * Farreach.es wire server. Please read AdminTool.md for more details
 */
public class AdminTool extends UtilParent {
	private Map<String, String> scriptsAvailable = null;
	private ScriptRunner runner;
	private boolean verbose = false;
	private Properties props;
	private BindingFactory bindingFactory;
	private static final String configFile = "fareaches.fadmin.properties";
	private static final Pattern scriptPattern = Pattern.compile("^(.*)\\.groovy$");

	public AdminTool(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
		this.props = new Properties();
		try {
			this.props.load(new FileInputStream(configFile));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn not load properties file: " + configFile + ".");
		}
		loadScriptsAvailable();
		runner = ScriptRunner.getNewScriptRunner(props,bindingFactory);
	}

	private void loadScriptsAvailable() {
		scriptsAvailable = new HashMap<String, String>();
		String scriptsFolder = props.getProperty("scripts_folder");
		File dir = new File(scriptsFolder);
		File[] files = dir.listFiles();
		for (File file : NotNullIterator.<File> newNotNullIterator(files)) {
			Matcher m = scriptPattern.matcher(file.getName());
			if (m.matches())
				try {
					scriptsAvailable.put(m.group(1), file.getCanonicalPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public void runScriptName(String name) {
		// TODO make this work

	}
	public boolean runScript(String script) {
		String filePath = scriptsAvailable.get(script);
		if(null==filePath){
			return false;
		}
		else{
			Map<String, String> commandLineParameters = new HashMap<>();
			runner.loadAndRunOneScript(filePath);
			return true;
		}
	}

	public void listFlows(AdminToolCommandLineOptions cmdOptions, String key,
			FarReachesServiceInfo service) {
		emitOutput("Using Key  >" + key);
		GeneralFlowRequest request = new GeneralFlowRequest(service, key, null);
		JSONArray<String> flows = request.listFlows();
		if (verbose) {
			emitOutput("");
			emitOutput(" Sent Request: " + request.getRequestString());
			emitOutput(" With key: " + request.getApiKey());
			emitOutput("");
		}
		emitOutput(flows.toString());

	}

	public Map<String, String> getAvailableScripts() {
		return scriptsAvailable;
	}

}
