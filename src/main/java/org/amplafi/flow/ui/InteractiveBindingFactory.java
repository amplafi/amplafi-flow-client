package org.amplafi.flow.ui;

import groovy.lang.Binding;

import java.io.BufferedReader;
import java.util.Map;

import org.amplafi.dsl.BindingFactory;
import org.amplafi.dsl.FlowTestDSL;

public class InteractiveBindingFactory implements BindingFactory {
	
	private BufferedReader bufferedReader;
	private FlowTestDSL flowDSL;
	
	public InteractiveBindingFactory(BufferedReader bufferedReader){
		this.bufferedReader = bufferedReader;
	}
	@Override
	public Binding getNewBinding(Map<String, String> paramsmap) {
		return new InteractiveBinding(bufferedReader,paramsmap);
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
