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
import org.amplafi.dsl.FlowTestDSL;
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
	private FarReachesServiceInfo serviceInfo;
	private BindingFactory bindingFactory;
	private static final String configFile = "fareaches.fadmin.properties";
	private static final Pattern scriptPattern = Pattern.compile("^(.*)\\.groovy$");

	public AdminTool(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
		this.props = new Properties();
		try {
			this.props.load(new FileInputStream(configFile));
			try{
				props.load(new FileInputStream(props.getProperty("keyfile")));
			}catch(IOException e){
				System.out.println("No keyfile found");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn not load properties file: " + configFile + ".");
		}
		loadScriptsAvailable();
		initializeServiceInfo();
		
		//TODO BRUNO clean up this recursive initialization mess
		runner = ScriptRunner.getNewScriptRunner(props,bindingFactory);
		bindingFactory.setDSL(new FlowTestDSL(serviceInfo, this.props, runner));
	}

	private void initializeServiceInfo() {
		String host = (props.getProperty("production") == "true")? props.getProperty("productionHostUrl"):props.getProperty("testHostUrl");
		this.serviceInfo = new FarReachesServiceInfo(
				host , props.getProperty("port"),
				props.getProperty("path"), props.getProperty("apiv"));
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

	public Map<String, String> getAvailableScripts() {
		return scriptsAvailable;
	}
	public boolean describeFlow(String api, String flow) {
		return bindingFactory.getDSL().describeFlow(api,flow);
	}

}
