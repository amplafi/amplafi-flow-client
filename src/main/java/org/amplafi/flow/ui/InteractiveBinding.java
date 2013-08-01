package org.amplafi.flow.ui;

import groovy.lang.Binding;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

//this groovy binding will manually prompt the user for data whenever it is missing
public class InteractiveBinding extends Binding {

	private BufferedReader reader;

	public InteractiveBinding(BufferedReader reader, Map<String, String> paramsmap) {
		super(paramsmap);
		this.reader = reader;
	}

	@Override
	public Object  getVariable(String vName) {
        if(!super.hasVariable(vName)){
        	System.out.print("Enter " + vName.replace('_', ' ') + ":");
        	try {
				String vValue = reader.readLine();
				super.setVariable(vName, vValue);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("InteractiveBinding: problem reading variable from stream");
			}
        }
        return super.getVariable(vName);
    }
}
