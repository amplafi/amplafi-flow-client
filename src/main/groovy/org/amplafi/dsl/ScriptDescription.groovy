package org.amplafi.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;

/**
 * This class contains various methods for loading and running FlowTestDSL scripts
 */
public class ScriptDescription {
    def String name = null
    def String description = null
    def String path = null
    def boolean hasErrors = null
    def String errorMesg = null

}
