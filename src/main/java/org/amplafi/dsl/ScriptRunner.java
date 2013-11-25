package org.amplafi.dsl;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Responsible for groovy script running Nowadays the class concerns itself exclusively with the
 * running of scripts, and state is, within reason, excluded from it.
 *
 * @author Paul
 */
public class ScriptRunner {

    /**
     * Description of all scripts known to this runner.
     */
    private Map<String, File> scriptLookup = new HashMap<String, File>();

    private Log log;

    private BindingFactory bindingFactory;

    private static final String NL = System.getProperty("line.separator");

    /**
     * Default path for test scripts
     */
    public static final String DEFAULT_SCRIPT_PATH = "src/test/resources/testscripts";

    /**
     * Constructs a Script runner with individual parameters that can be overridden in scripts.
     * passes a map of parameters to the script
     *
     */
    public ScriptRunner(BindingFactory bindingFactory) {
        this.bindingFactory = bindingFactory;
    }

    /**
     * Loads and runs one script specified by the file parameter.
     *
     * @param filePathOrName is the full path to the script.
     * @throws IOException
     */
    public Object loadAndRunOneScript(String filePathOrName) throws IOException {
        File file;
        if (scriptLookup.containsKey(filePathOrName)) {
            file = scriptLookup.get(filePathOrName);
        } else {
            file = new File(filePathOrName);
        }
        String script = readFile(file);
        getLog().debug("loadAndRunOneScript() start to run runScriptSource() method");
        Object value = runScriptSource(script, file.getName());
        getLog().debug("loadAndRunOneScript() start to run runScriptSource() method");
        return value;
    }

    private String readFile(File f) throws IOException {
        try (FileInputStream fis = new FileInputStream(f)) {
            return readFile(fis);
        }
    }

    private String readFile(InputStream is) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line != null) {
                result.append(line + "\n");
                line = br.readLine();
            }
            return result.toString();
        }
    }

    /**
     * Runs or describes a script from source code.
     *
     * @param sourceCode
     * @param execOrDescibe - If true then execute the source code as a command script if false run
     *            the code as a description DSL to obtain its description
     * @return The groovy closure (Why?)
     */
    public Object runScriptSource(String sourceCode, String scriptName) {
        // The script code must be pre-processed to add the contents of the file
        // into a call to FlowTestBuilder.build then the processed script is run
        // with the GroovyShell.
        Binding binding = this.getNewBinding(new HashMap());
        Closure closure = getClosure(sourceCode, binding, scriptName);
        closure.setDelegate(bindingFactory.getDSL());
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        return closure.call();
    }

    public Binding getNewBinding(Map<String, String> callParamsMap) {
        Binding binding = bindingFactory.getNewBinding(callParamsMap);
        binding.setProperty("out", System.out);
        return binding;
    }
    /**
     * Takes the source code string and wraps in into a valid groovy script that when run will
     * return a closure. that can be either configured to describe itself or to run as a sequence of
     * commands.
     *
     * @param sourceCode
     * @param paramsmap
     * @return
     */
    private Closure getClosure(String sourceCode, Binding binding, String scriptName) {
        StringBuilder script = new StringBuilder();
        // Extract the import statements from the input source code and re-add
        // them
        // to the top of the groovy program.
        script.append(getImportLines(sourceCode));
        // All the imports are prepended to the first line of the user script so
        // error messages
        // have the correct line number in them
        script.append("import org.amplafi.flow.utils.*;import org.amplafi.dsl.*;import org.amplafi.json.*;").append(NL);
        script.append("def init_key = { return callScript(\"GetNewPermanentKey\")}").append(NL);
        script.append("def source = {").append(NL);
        script.append(getValidClosureCode(sourceCode));
        script.append("}; return source;");
        GroovyShell shell = new GroovyShell(ScriptRunner.class.getClassLoader(), binding);
        return (Closure) shell.evaluate(script.toString(), scriptName);
    }

    /**
     * Creates an un-configured closure (no delegate set).
     *
     * @param scriptName
     * @param callParamsMap - map of parameters name to value
     * @return
     * @throws IOException
     */
    public Closure createClosure(String scriptName, Map<String, String> callParamsMap) {
        Binding binding = this.getNewBinding(callParamsMap);
        String filePath = getScriptPath(scriptName);
        if (filePath == null) {
            URL scriptResource = getClass().getResource("/commandScripts/" + scriptName + ".groovy");
            if (scriptResource != null) {
                filePath = scriptResource.getPath();
            }
        }
        if (filePath != null) {
            File file = new File(filePath);
            String sourceCode;
            try {
                System.out.println("Reading script from "+filePath);
                sourceCode = readFile(file);
                return getClosure(sourceCode, binding, file.getName());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            getLog().error("Script " + scriptName + " does not exist");
            return null;
        }
    }

    /**
     * Obtain the script file path from the short name in the script description directive. This
     * should be called after processScriptsInFolder(...) or it will return null.
     *
     * @param scriptName
     * @return file path string
     */
    private String getScriptPath(String scriptName) {
        String filePath = null;
        File file = scriptLookup.get(scriptName);
        if (file != null) {
            filePath = file.getPath();
        }
        return filePath;
    }

    /**
     * Returns the script source with import lines removed.
     *
     * @param source - original source code.
     * @return - modified code
     */
    private String getValidClosureCode(String source) {
        StringBuilder sb = new StringBuilder();
        try (Scanner s = new Scanner(source)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (!line.startsWith("import")) {
                    // Escape " for groovy..
                    line = line.replaceAll("\"", "\\\"");
                    sb.append(line).append(NL);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Returns the all of the import lines from the script so they can be put in the correct
     * location in the wrapper script.
     *
     * @param source - source code
     * @return - string of import statements.
     */
    private String getImportLines(String source) {
        StringBuilder sb = new StringBuilder();
        try (Scanner s = new Scanner(source)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("import")) {
                    sb.append(line).append(NL);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Get the logger for this class.
     */
    protected Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(ScriptRunner.class);
        }
        return this.log;
    }

}
