package org.amplafi.dsl;

import groovy.lang.Binding;

import java.util.Map;

/**
 * A binding factory for plain groovy bindings to be used outside the customer service shell
 */
public class GroovyBindingFactory extends AbstractBindingFactory {

    @Override
    public Binding getNewBinding(Map<String, String> paramsmap) {
        Map completeParameters = createCompleteParametersMap(paramsmap);
        Binding b = new Binding(completeParameters);
        return b;
    }
}
