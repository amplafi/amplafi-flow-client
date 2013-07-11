package org.amplafi.flow.ui;

import java.io.BufferedReader;
import java.util.Map;

import groovy.lang.Binding;

import org.amplafi.dsl.BindingFactory;

public class InteractiveBindingFactory implements BindingFactory {
	
	private BufferedReader bufferedReader;

	public InteractiveBindingFactory(BufferedReader bufferedReader){
		this.bufferedReader = bufferedReader;
	}
	
	@Override
	public Binding getNewBinding(Map<String, String> paramsmap) {
		return new InteractiveBinding(bufferedReader,paramsmap);
	}
	
}
