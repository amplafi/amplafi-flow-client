package org.amplafi.dsl;

/**
 * TO_DAISY: Javadoc?
 * 
 * @author aectann
 *
 */
public class ParameterUsage {

    String name;

    String description;

    boolean optional;

    Object defaultValue;

    public ParameterUsage(String name, String description, boolean optional, Object defaultValue) {
        this.name = name;
        this.description = description;
        this.optional = optional;
        this.defaultValue = defaultValue;
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

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
