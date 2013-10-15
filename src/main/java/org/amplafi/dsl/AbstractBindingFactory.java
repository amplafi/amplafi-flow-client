package org.amplafi.dsl;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBindingFactory implements BindingFactory {

    private Map defaultParameters = new HashMap();
    /**
     * @deprecated ? maybe - not certain why this thing shouldn't be collapsed with another class
     */
    @Deprecated
    private FlowTestDSL flowDSL;

    public AbstractBindingFactory() {
    }
    public AbstractBindingFactory(Map defaultParameters) {
        if ( defaultParameters != null) {
            this.defaultParameters.putAll(defaultParameters);
        }
    }

    @Override
    public void setParameter(String parameterName, String parameterValue) {
        if ( parameterValue == null || parameterValue.isEmpty()) {
            this.defaultParameters.remove(parameterName);
        } else {
            this.defaultParameters.put(parameterName, parameterValue);
        }
    }

    protected Map createCompleteParametersMap(Map<String, String> paramsmap) {
        Map completeParameters = new HashMap<>(this.defaultParameters);
        if ( paramsmap != null && !paramsmap.isEmpty()) {
            completeParameters.putAll(paramsmap);
        }
        return completeParameters;
    }

    @Override
    public FlowTestDSL getDSL() {
    	return flowDSL;
    }

    @Override
    public void setDSL(FlowTestDSL ftdsl) {
    	this.flowDSL = ftdsl;
    }

    @Override
    public Map getDefaultParameters() {
        return defaultParameters;
    }

    public void setDefaultParameters(Map defaultParameters) {
        this.defaultParameters = defaultParameters;
    }
}
