package org.amplafi.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;

/**
 * This class contains various methods for loading and running FlowTestDSL scripts
 */
public class ScriptDescription {
    def String name;
    def String description;
    def String path;
    def boolean hasErrors;
    def String errorMesg;

}
