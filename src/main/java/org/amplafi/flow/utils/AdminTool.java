package org.amplafi.flow.utils;

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

import com.sworddance.util.NotNullIterator;

/**
 * Generic interface to track state of farreach.es server communication.
 * This is where all connection information is loaded (look at the constructor. we use several properties files
 * that are loaded sequentially overriding one another.)
 * 
 * The AdminTool is the new way to control the {@link FlowTestDSL} from outside the scripts. Ideally you don't want to access
 * the DSL directly unless you're messing with script internals.
 * 
 */
public class AdminTool {
	private Map<String, String> scriptsAvailable = null;
	private ScriptRunner runner;
	private Properties props;
	private FarReachesServiceInfo serviceInfo;
	private BindingFactory bindingFactory;
	public static final String CONFIG_FILE = "fareaches.fadmin.properties";
	private static final Pattern SCRIPT_PATTERN = Pattern.compile("^(.*)\\.groovy$");

	public AdminTool(BindingFactory bindingFactory) {
		this.setBindingFactory(bindingFactory);
		this.props = new Properties();
		try {
			this.props.load(new FileInputStream(CONFIG_FILE));
			String keyfileName = (props.getProperty("production").equals("true"))?props.getProperty("productionKeyfile"):props.getProperty("keyfile");
			try(FileInputStream fis = new FileInputStream(keyfileName)){
				 props.load(fis);
			}catch(IOException e){
				System.out.println("No keyfile found");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn not load properties file: " + CONFIG_FILE + ".");
		}
		loadScriptsAvailable();
		initializeServiceInfo();
		
		//TODO BRUNO clean up this recursive initialization mess
		// suggestion: for next dev, the functions that are needed on admintool maybe can be kept inside ScriptRunner
		runner = ScriptRunner.getNewScriptRunner(props,bindingFactory);
		bindingFactory.setDSL(new FlowTestDSL(getServiceInfo(), this.props, runner));
	}

	private void initializeServiceInfo() {
		String host = (props.getProperty("production").equals("true"))? props.getProperty("productionHostUrl"):props.getProperty("testHostUrl");
		String port =  (props.getProperty("production").equals("true"))? props.getProperty("productionPort"):props.getProperty("testPort");
		this.setServiceInfo(new FarReachesServiceInfo(
				host , port,
				props.getProperty("path"), props.getProperty("apiv")));
		System.out.println("Service initialized to " + host + ":" + port);
	}

	private void loadScriptsAvailable() {
		scriptsAvailable = new HashMap<String, String>();
		String scriptsFolder = props.getProperty("scripts_folder");
		File dir = new File(scriptsFolder);
		File[] files = dir.listFiles();
		for (File file : NotNullIterator.<File> newNotNullIterator(files)) {
			Matcher m = SCRIPT_PATTERN.matcher(file.getName());
			if (m.matches())
				try {
					scriptsAvailable.put(m.group(1), file.getCanonicalPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public void runScriptName(String name) {
		this.runner.loadAndRunOneScript(name);

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
		return getBindingFactory().getDSL().describeFlow(api,flow);
	}

	public FarReachesServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(FarReachesServiceInfo serviceInfo) {
		System.out.println("Updated Service Info");
		getBindingFactory().setDSL(new FlowTestDSL(getServiceInfo(), this.props, runner));
		this.serviceInfo = serviceInfo;
	}

	public BindingFactory getBindingFactory() {
		return bindingFactory;
	}

	public void setBindingFactory(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
	}

}
