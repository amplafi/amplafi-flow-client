package org.amplafi.dsl;

import java.util.Map;

import groovy.lang.Binding;

public class GroovyBindingFactory implements BindingFactory {

	@Override
	public Binding getNewBinding(Map<String, String> paramsmap) {
		return new Binding(paramsmap);
	}

}
