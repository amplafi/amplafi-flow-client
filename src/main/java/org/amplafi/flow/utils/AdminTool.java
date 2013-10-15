package org.amplafi.flow.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.dsl.BindingFactory;
import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.definitions.FarReachesServiceInfo;

import com.sworddance.util.NotNullIterator;

/**
 * Generic interface to track state of farreach.es server communication. This is where all
 * connection information is loaded (look at the constructor. we use several properties files that
 * are loaded sequentially overriding one another.) The AdminTool is the new way to control the
 * {@link FlowTestDSL} from outside the scripts. Ideally you don't want to access the DSL directly
 * unless you're messing with script internals or modifying the {@link FarReachesServiceInfo}
 * embedded there.
 */
public class AdminTool {
    private Map<String, String> scriptsAvailable = new ConcurrentHashMap<>();

    private ScriptRunner runner;

    @Deprecated // put in FarReachesServiceInfo
    private Properties props;

    private BindingFactory bindingFactory;

    private FarReachesServiceInfo farReachesServiceInfo;

    public static final String CONFIG_FILE = "farreaches.fadmin.properties";

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("^(.*)\\.groovy$");

    public AdminTool(BindingFactory bindingFactory) {
        this.setBindingFactory(bindingFactory);
        this.props = new Properties();
        try (FileInputStream propertyStream = new FileInputStream(CONFIG_FILE)) {
            this.props.load(propertyStream);
            System.out.println(CONFIG_FILE + ": loaded");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load properties file: " + CONFIG_FILE);
        }
        boolean productionMode = props.getProperty("production").equals("true");
        String keyfileNameProperty = productionMode ? "productionKeyfile" : "keyfile";
        String keyfileName = props.getProperty(keyfileNameProperty);
        // TODO ask for the key on start up
        // TODO : allow keys to be changed.
        System.out.println("Loading keys from :" + keyfileName + "(property=" + keyfileNameProperty + ")");
        try (FileInputStream fis = new FileInputStream(keyfileName)) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println(keyfileName + ": No keyfile found. Check property " + keyfileNameProperty + " in " + CONFIG_FILE);
        }

        this.setServiceInfo( new FarReachesServiceInfo(props));
        // TODO: validate keys
        String scriptsFolder = props.getProperty("scripts_folder");

        loadScriptsAvailable(scriptsFolder);
        runner = new ScriptRunner(bindingFactory);
    }

    private void loadScriptsAvailable(String scriptsFolder) {
        System.out.println("loading scripts from "+scriptsFolder+" ('scripts_folder' directory)...");
        File dir = new File(scriptsFolder);
        File[] files = dir.listFiles();
        for (File file : NotNullIterator.<File> newNotNullIterator(files)) {
            Matcher m = SCRIPT_PATTERN.matcher(file.getName());
            if (m.matches()) {
                try {
                    System.out.println(m.group(1) +"\t\t=("+ file.getCanonicalPath()+")");
                    scriptsAvailable.put(m.group(1), file.getCanonicalPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void runScriptName(String name) throws IOException {
        this.runner.loadAndRunOneScript(name);
    }

    public boolean runScript(String script) throws IOException {
        String filePath = scriptsAvailable.get(script);
        if (null == filePath) {
            return false;
        } else {
            runner.loadAndRunOneScript(filePath);
            return true;
        }
    }

    public Map<String, String> getAvailableScripts() {
        return scriptsAvailable;
    }

    public boolean describeFlow(String api, String flow) {
        return getBindingFactory().getDSL().describeFlow(api, flow);
    }

    public void setServiceInfo(FarReachesServiceInfo serviceInfo) {
        System.out.println("Updated Service Info");
        this.farReachesServiceInfo = serviceInfo;
        getBindingFactory().setDSL(new FlowTestDSL(this.farReachesServiceInfo, this.props, runner));
    }

    public String getPrompt() {
        return this.farReachesServiceInfo.getPrompt();
    }
    public BindingFactory getBindingFactory() {
        return bindingFactory;
    }

    public void setBindingFactory(BindingFactory bindingFactory) {
        this.bindingFactory = bindingFactory;
    }

}
