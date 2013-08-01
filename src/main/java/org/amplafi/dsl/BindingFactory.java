package org.amplafi.dsl;

import groovy.lang.Binding;

import java.util.Map;

/**
 * 
 * For including specific responsive bindings - useful for prompting the user for input or 
 * acting appropriately depending.
 *
 */
public interface BindingFactory {
	public Binding getNewBinding(Map<String, String> paramsmap);

	public FlowTestDSL getDSL();
	public void setDSL(FlowTestDSL ftdsl);
}
