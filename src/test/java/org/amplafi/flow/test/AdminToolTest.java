package org.amplafi.flow.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.amplafi.flow.utils.AdminTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.*;

public class AdminToolTest {
	public static StringBuffer testBuffer;	
	AdminToolMock adminToolMock = null;
	public static final String PATH = "src\\test\\resources\\testscripts\\junitTestScript";
	public static final String CONFIG_FILE_NAME = "fareaches.fadmin.test.properties";
	public static final String KEY = "ampcb_720df5d558173cc402f59913355e9f5f1fb8b444cd7d43be01c89fe216500469";
	public static final String HOST = "http://apiv1.farreach.es";
	public static final String PORT = "8080";
	public static final String APIV = "apiv1";

	@Before
	public void setUp() throws Exception {
		adminToolMock = new AdminToolMock();
		testBuffer = new StringBuffer();
		adminToolMock.setComandScriptPath(PATH);
		adminToolMock.setConfigFileName(CONFIG_FILE_NAME);
	}

	@After
	public void tearDown() throws Exception {
		adminToolMock = null;
		// logMsg = null;
		testBuffer = null;
	}

	/**
     * This test test command FAdmin -l.
     */
	@Test
	public void testProcessCommandLineList() throws SecurityException,
			NoSuchFieldException, Exception {

		String[] args = { "-l" };
		adminToolMock.processCommandLine(args);
		List<String> logMsgList = adminToolMock.getLogMsgList();

		// fail("Not yet implemented");
		//This is test that use our own script
		String excepted1 = "     testScript2       - Just an test2 script";
		String excepted2 = "     testScript       - Just an test script";
		assertEquals(2,logMsgList.size());
		assertTrue(logMsgList.contains(excepted1));
		assertTrue(logMsgList.contains(excepted2));

	}
	
	
	/**
     * This test test command FAdmin -L.
     */
	@Test
	public void testProcessCommandLineListdetailed() throws SecurityException,
			NoSuchFieldException, Exception {
		
		String[] args = { "-L" };
		adminToolMock.processCommandLine(args);
		List<String> logMsgList = adminToolMock.getLogMsgList();

		// fail("Not yet implemented");
		//This is test that use our own script
		String excepted1 = "     testScript       - Just an test script       - \\src\\test\\resources\\testscripts\\junitTestScript\\testScript.groovy";
		String excepted2 = "     testScript2       - Just an test2 script       - \\src\\test\\resources\\testscripts\\junitTestScript\\testScript2.groovy";

		assertEquals(2,logMsgList.size());
		assertTrue(logMsgList.contains(excepted1));
		assertTrue(logMsgList.contains(excepted2));
	}
	
	
	/**
     * This test test command FAdmin -help.
     */
	@Test
	public void testProcessCommandLineHelp() {
		String[] args = { "-help", "testScript2" };
		adminToolMock.processCommandLine(args);
		String excepted = "Script Usage: usage test";
		List<String> logMsgList = adminToolMock.getLogMsgList();
		assertEquals(1,logMsgList.size());
		assertTrue(logMsgList.contains(excepted));
		

		
		// fail("Not yet implemented");

	}
	
	/**
     * This test test command FAdmin -help, but script does not have usage information .
     */
	@Test
	public void testProcessCommandLineHelpWithNoUsage() {
		String[] args = { "-help", "testScript" };
		adminToolMock.processCommandLine(args);
		String excepted = "Script testScript does not have usage information";
		List<String> logMsgList = adminToolMock.getLogMsgList();
		assertEquals(1,logMsgList.size());
		assertTrue(logMsgList.contains(excepted));
	}
	
	/**
     * This test test command FAdmin -x .
     */
	@Test
	public void testProcessCommandLineX() {
		String[] args = { "-x" };
		adminToolMock.processCommandLine(args);

		String keyValue = adminToolMock.getConfigProperties().getProperty("key", "");
		String exceptedKey = KEY;
		String portValue = adminToolMock.getConfigProperties().getProperty("port", "");
		String exceptedPort = "80";
		String hostValue = adminToolMock.getConfigProperties().getProperty("host", "");
		String exceptedHost = HOST;
		String apivValue = adminToolMock.getConfigProperties().getProperty("apiv", "");
		String exceptedApiv = APIV;
		assertEquals(exceptedKey,keyValue);
		assertEquals(exceptedPort,portValue);
		assertEquals(exceptedHost,hostValue);
		assertEquals(exceptedApiv,apivValue);
	}
	
	/**
     * This test test command FAdmin <script name> .
     */
	@Test
	public void testProcessCommandLineRunScript() {
		String[] args = { "testScript" };
		adminToolMock.processCommandLine(args);
	    String exString = "This is testScript";
	    assertEquals(exString,AdminToolTest.testBuffer.toString());
	}
	
	/**
     * This test test command FAdmin <scriptName> <paramName>=<paramValue> .
     */
	@Test
	public void testProcessCommandLineRunScriptWithParam() {
		String[] args = { "testScript", "param1=dog" };
		adminToolMock.processCommandLine(args);
	    String exString = "This is testScript param1 : dog";
	    assertEquals(exString,AdminToolTest.testBuffer.toString());
	}
	
	/**
     * This class is AdminTool Mock, it extends AdminTool and override some method for test.
     */
	class AdminToolMock extends AdminTool {
		List<String> logMsgList = new ArrayList();
		Properties configProperties = new Properties();;

		public AdminToolMock(){
			configProperties.setProperty("key", KEY);
			configProperties.setProperty("host", HOST);
			configProperties.setProperty("port", PORT);
			configProperties.setProperty("apiv", APIV);
		}
		
		/**
		 * @return the configProperties
		 */
		public Properties getConfigProperties() {
			return configProperties;
		}

		/**
		 * @return the log message list
		 */
		public List<String> getLogMsgList() {
			return logMsgList;
		}

		/**
		 * This method add the out put from adminTool to the message list
		 */
		@Override
		public void emitOutput(String msg) {
			logMsgList.add(msg);
		}
		
		/**
		 * This method mock user input
		 * @return test data for user input
		 */
		@Override
		public String getUserInput(String key){
			if(key.equals("host")){
				return HOST;
			}else if(key.equals("port")){
				return "80";
			}else if(key.equals("apiv")){
				return APIV;
			}else if(key.equals("key")){				
				return KEY;
			}else{
				return "";
			}
			
			
		}
		
		/**
		 * This method mock get properties
		 * @return configProperties
		 */
		@Override
		public Properties getProperties(){			
			
			return configProperties;
			 
		}
		
		/**
		 * This method override saveProperties method of AdminTool so that test can not create a properties file
		 */
		@Override
		public void saveProperties(){
			
		}

	}
	
}


