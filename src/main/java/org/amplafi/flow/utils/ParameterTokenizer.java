package org.amplafi.flow.utils;

import java.util.ArrayList;
import java.util.List;

//utility class to split command line arguments - a bit hackish but necessary to
//do some advanced argument passing in shell commands - flags, spaces, etc.
public class ParameterTokenizer {
	public static List<String> tokenize(String st){
		List<String> ret = new ArrayList<String>();
		List<Character> escapeTokens = new ArrayList<Character>();
		escapeTokens.add('"');
		escapeTokens.add('\'');
		String stCopy = st.trim();
		while(stCopy != ""){
			int i = 0;
			boolean escaped = false;
			boolean inText = false;
			//this will not break the loop below since the case should always be handled
			//by the previous if. hacky as hell.
			char escapeSeq='\\';
			while(stCopy.length() > i && (stCopy.charAt(i)!=' ' || inText)){
				if(inText){
					if(escaped){
						escaped = false;
					}else if(stCopy.charAt(i)=='\\'){
						escaped = true;
					}else if(stCopy.charAt(i)==escapeSeq){
						inText = false;
						escapeSeq = '\\';
					}
				}else{
					if(escapeTokens.contains(stCopy.charAt(i))){
						escapeSeq = stCopy.charAt(i);
					}
				}
				i++;
			};
			ret.add(stCopy.substring(0, i));
			stCopy = stCopy.substring(i);
			i=0;
		}
		return ret;
	}
}
