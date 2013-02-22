package org.amplafi.dsl;

public class ParameterUsge {

    String name;
    String description;
    boolean optional;
    Object defaultValue;
    
    public ParameterUsge(String name,String description,boolean optional,Object defaultValue){
        this.name = name;
        this.description = description;
        this.optional = optional;
        this.defaultValue = defaultValue;
    }
    
}