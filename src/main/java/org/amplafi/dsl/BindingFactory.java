package org.amplafi.dsl;

import groovy.lang.Binding;

import java.util.Map;

/**
 * @author bfv
 * For including specific responsive bindings - useful for prompting the user for input or
 * acting appropriately depending.
 *
 */
public interface BindingFactory {
	public Binding getNewBinding(Map<String, String> paramsmap);
	void setParameter(String parameterName, String parameterValue);

	public FlowTestDSL getDSL();
	public void setDSL(FlowTestDSL ftdsl);
    public Map getDefaultParameters();
}
