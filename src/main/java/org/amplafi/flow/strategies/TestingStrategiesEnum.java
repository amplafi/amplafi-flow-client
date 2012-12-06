package org.amplafi.flow.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Just a simple enum for managing the available strategies.
 * Nothing more complicated is needed just now.
 * @author paul
 */
public enum TestingStrategiesEnum  {

	BogusString(new BogusStringDataStrategy()), CorruptParams(new CorruptParamsNameTestingStrategy());
	
	private AbstractTestingStrategy strategy;
	
	/**
	 * Constructor for this enum value.
	 * @para 
	 */
	 TestingStrategiesEnum(AbstractTestingStrategy strategy){
		this.strategy = strategy;
	}
	
	/**
	 * @return the 
	 */
	public AbstractTestingStrategy getStrategy(){
		return strategy;
	}
	
	/**
	 * @return comma separated list of strategy names.
	 */ 
	public static String listStrategyNames(){
		StringBuffer out = new StringBuffer();
		
		for(TestingStrategiesEnum s : TestingStrategiesEnum.values()){
			out.append(s.name());
			out.append(",");
		}
		return out.toString();
	}

}
