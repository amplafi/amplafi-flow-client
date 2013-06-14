package org.amplafi.dsl;

import java.util.List;

/**
 * TO_DAISY: Javadoc?
 * 
 * @author aectann
 *
 */
public class ScriptDescription {
    /** Name of the script */
    String name;

    /** Description of the script */
    String description;

    /** The path to the script file */
    String path;

    /** Were errors detected when pre-processing script */
    boolean hasErrors;

    String errorMesg;

    String usage;

    List<ParameterUsage> usageList;

    public ScriptDescription() {
    }
    
    public ScriptDescription(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    public ScriptDescription(String name, String description, String usage, List<ParameterUsage> usageList) {
        this(name, description, usage);
        this.usageList = usageList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public String getErrorMesg() {
        return errorMesg;
    }

    public void setErrorMesg(String errorMesg) {
        this.errorMesg = errorMesg;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public List<ParameterUsage> getUsageList() {
        return usageList;
    }

    public void setUsageList(List<ParameterUsage> usageList) {
        this.usageList = usageList;
    }
}
