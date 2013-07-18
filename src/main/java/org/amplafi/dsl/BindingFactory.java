package org.amplafi.dsl;

import java.util.Map;

import groovy.lang.Binding;

// for including specific responsive bindings - useful for prompting the user for input or 
// acting appropriately depending
public interface BindingFactory {
	public Binding getNewBinding(Map<String, String> paramsmap);

	public FlowTestDSL getDSL();
	public void setDSL(FlowTestDSL ftdsl);
}
