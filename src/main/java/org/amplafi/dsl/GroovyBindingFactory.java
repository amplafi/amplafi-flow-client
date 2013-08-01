package org.amplafi.dsl;

import groovy.lang.Binding;

import java.util.Map;

public class GroovyBindingFactory implements BindingFactory {

	private FlowTestDSL flowDSL;
	@Override
	public Binding getNewBinding(Map<String, String> paramsmap) {
		Binding b = new Binding(paramsmap);
		return b;
	}

	public GroovyBindingFactory(){
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
