package org.amplafi.dsl;

import java.util.Map;

import groovy.lang.Binding;

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
