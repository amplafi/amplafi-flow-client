package org.amplafi.dsl;

public class ParameterUsage {

    String name;
    String description;
    boolean optional;
    Object defaultValue;
    
    public ParameterUsage(String name,String description,boolean optional,Object defaultValue){
        this.name = name;
        this.description = description;
        this.optional = optional;
        this.defaultValue = defaultValue;
    }
    
}