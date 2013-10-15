package org.amplafi.dsl;

import groovy.lang.Binding;

import java.util.Map;


/**
 * @author bfv
 * A binding factory for plain groovy bindings to be used outside the customer service
 * shell
 */
public class GroovyBindingFactory implements BindingFactory {

	private FlowTestDSL flowDSL;
	@Override
	public Binding getNewBinding(Map<String, String> paramsmap) {
		Binding b = new Binding(paramsmap);
		return b;
	}

	@Override
	public FlowTestDSL getDSL() {
		return flowDSL;
	}

	@Override
	public void setDSL(FlowTestDSL ftdsl) {
		this.flowDSL = ftdsl;
	}
}
