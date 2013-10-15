package org.amplafi.flow.ui;

import groovy.lang.Binding;

import java.io.BufferedReader;
import java.util.Map;

import org.amplafi.dsl.AbstractBindingFactory;


/**
 * @author bfv
 * The interactive binding is related to the user shell. It knows how to ask for input using it's BufferedReader object
 * through groovy's binding variable resolution mechanism.
 *
 */
public class InteractiveBindingFactory extends AbstractBindingFactory {

	private BufferedReader bufferedReader;
	public InteractiveBindingFactory(BufferedReader bufferedReader){
		this.bufferedReader = bufferedReader;
	}
	@Override
	public Binding getNewBinding(Map<String, String> paramsmap) {
        Map completeParameters = createCompleteParametersMap(paramsmap);
		return new InteractiveBinding(bufferedReader,completeParameters);
	}

}
