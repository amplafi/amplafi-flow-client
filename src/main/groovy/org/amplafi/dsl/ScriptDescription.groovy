package org.amplafi.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;

/**
 * This class contains various methods for loading and running FlowTestDSL scripts
 * @author Daisy
 */
public class ScriptDescription {
    /** Name of the script */
    def String name;
    /** Description of the script */
    def String description;
    /** The path to the script file */
    def String path;
    /** Were errors detected when pre-processing script */
    def boolean hasErrors;

    def String errorMesg;
    def String usage;
    def List<ParameterUsge> usageList;
}
