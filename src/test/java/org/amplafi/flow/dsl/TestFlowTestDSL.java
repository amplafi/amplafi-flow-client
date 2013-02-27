package org.amplafi.flow.dsl;

import static org.testng.Assert.*;
import static org.amplafi.dsl.ScriptRunner.DEFAULT_SCRIPT_PATH;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.TestGenerationProperties;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.amplafi.dsl.FlowTestBuilder;
import org.amplafi.dsl.FlowTestDSL;
import java.util.*;

/**
 * This is a test for the TestScriptRunner itself, not the wire server
 */
public class TestFlowTestDSL{

    private FlowTestDSL dslInstance = null;

    @BeforeMethod
    public void setup(){
        dslInstance = new FlowTestDSL("hello", null, true);
    }

    @AfterMethod
    public void tearDown(){
        dslInstance = null;
    }
	
	/**
	 * create two null object to compare
	 */
    @Test 
    public void testCompareEmptyObjects() throws Exception{
		JSONObject o1 = new JSONObject();
		JSONObject o2 = new JSONObject();
        boolean ret = dslInstance.compare(o1, o2, null);
	    assertTrue(ret);
    }

	/**
	 * create two null object to compare
	 */
    @Test 
    public void testCompareNullObjects() throws Exception{
		JSONObject o1 = new JSONObject();
		JSONObject o2 = null;
        //boolean ret = dslInstance.compare(o1, o2, null);
	    //assertFalse(ret);
		
		try{
			boolean ret = dslInstance.compare(o2, o1, null);
			fail("Expected Excetion was not thrown");
		} catch (AssertionError ae){
			// Do Nothing
			System.err.println("Caught " + ae);
		}

    }


	/**
	 * create two simple objects and compare
	 */
    @Test 
    public void testCompareSimpleMatchingObjects() throws Exception{	
		JSONObject o1 = new JSONObject();
		o1.put("name","samantha");
		o1.put("name2","ccc");
		JSONObject o2 = new JSONObject();
		o2.put("name","samantha");
		o2.put("name2","ccc");
        boolean ret = dslInstance.compare(o1, o2, null);
	    assertTrue(ret);
    }

	/**
	 * create two simple different objects and compare
	 */
    @Test 
    public void testCompareSimpleNonMatchingObjects() throws Exception{
		JSONObject o1 = new JSONObject();
		o1.put("name","samantha");
		o1.put("name2","DDD");
		JSONObject o2 = new JSONObject();
		o2.put("name","samantha");
		o2.put("name2","ccc");
		try{
		    boolean ret = dslInstance.compare(o1, o2, null);
			fail("Expected Excetion was not thrown");
		} catch (AssertionError ae){
			// Do Nothing
			System.err.println("Caught " + ae);
		}
    }
	
		/**
	 * create two identical objects with a interal list and compare
	 */
	@Test 
    public void testCompareWithListMatchingObjects() throws Exception{
		JSONObject o1 = new JSONObject();
		o1.put("name","samantha");
		List inJsonObject = new ArrayList();
		inJsonObject.add("123");
		o1.put("name2",inJsonObject);
		JSONObject o2 = new JSONObject();
		o2.put("name","samantha");
		List inJsonObject2 = new ArrayList();
		inJsonObject2.add("123");
		o2.put("name2",inJsonObject2);
        boolean ret = dslInstance.compare(o1, o2, null);
	    assertTrue(ret);
    }
	

	/**
	 * create two objects with a different interal list and compare
	 */
	@Test 
    public void testCompareWithListNonMatchingObjects() throws Exception{
		JSONObject o1 = new JSONObject();
		o1.put("name","samantha");
		List inJsonObject = new ArrayList();
		inJsonObject.add("123");
		inJsonObject.add("456");
		o1.put("name2",inJsonObject);
		JSONObject o2 = new JSONObject();
		o2.put("name","samantha");
		List inJsonObject2 = new ArrayList();
		inJsonObject2.add("123");
		inJsonObject.add("789");
		o2.put("name2",inJsonObject2);
		try{
			boolean ret = dslInstance.compare(o1, o2, null);
			fail("Expected Excetion was not thrown");
		} catch (AssertionError ae){
			// Do Nothing
			System.err.println("Caught " + ae);
		}
    }

	/**
	 * create two identical objects with a interal jsonObject and compare
	 */	
	@Test 
    public void testCompareWithJsonObjectMatchingObjects() throws Exception{
		JSONObject o1 = new JSONObject();
		o1.put("name","samantha");
		JSONObject inJsonObject1 = new JSONObject();
		inJsonObject1.put("grade","123");
		o1.put("name2",inJsonObject1);
		JSONObject o2 = new JSONObject();
		o2.put("name","samantha");
		JSONObject inJsonObject2 = new JSONObject();
		inJsonObject2.put("grade","123");
		o2.put("name2",inJsonObject2);
        boolean ret = dslInstance.compare(o1, o2, null);
	    assertTrue(ret);
    }

	/**
	 * create two objects with a different interal jsonObject and compare
	 */	
	@Test 
    public void testCompareWithJsonObjectNonMatchingObjects() throws Exception{
		
		JSONObject o1 = new JSONObject();
		o1.put("name","samantha");
		JSONObject inJsonObject1 = new JSONObject();
		inJsonObject1.put("grade","123");
		o1.put("name2",inJsonObject1);
		JSONObject o2 = new JSONObject();
		o2.put("name","samantha");
		JSONObject inJsonObject2 = new JSONObject();
		inJsonObject2.put("grade","456");
		o2.put("name2",inJsonObject2);
		try{
			boolean ret = dslInstance.compare(o1, o2, null);
			fail("Expected Excetion was not thrown");
		} catch (AssertionError ae){
			// Do Nothing
			System.err.println("Caught " + ae);
		}
    }
	
	/**
	 * Create 2 identical objects with some interal structure and compare them
	 */
	
	@Test 
    public void testCompareComplexObjectsMatch() throws Exception{
		
		String o1Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ "              'age' : 17, "
						+ "              'height' : 50.00}}";
		JSONObject o1 = new JSONObject(o1Str);

		String o2Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ " 'age' : 17, "
						+ " 'height' : 50.00}}";
		JSONObject o2 = new JSONObject(o2Str);

		boolean ret = dslInstance.compare(o1, o2, null);
	    assertTrue(ret);
    }
	
	/**
	 * Create 2 objects with identical structure but different field values
	 */
	@Test 
    public void testCompareComplexObjectsDontMatch1() throws Exception{
		
		String o1Str = "{  'name2' : {'grade' : '124'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ "              'age' : 17, "
						+ "              'height' : 50.00}}";
		JSONObject o1 = new JSONObject(o1Str);

		String o2Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ " 'age' : 17, "
						+ " 'height' : 50.00}}";
		JSONObject o2 = new JSONObject(o2Str);
		try{
			boolean ret = dslInstance.compare(o1, o2, null);
			fail("Expected Excetion was not thrown");
		} catch (AssertionError ae){
			// Do Nothing
			System.err.println("Caught " + ae);
		}

    }	
	
	/**
	 * Create 2 objects different structure
	 */
	@Test 
    public void testCompareComplexObjectsDontMatch2() throws Exception{
		
		String o1Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student3' : { 'name' : 'Samantha', "
						+ "              'age' : 17, "
						+ "              'height' : 50.00}}";
		JSONObject o1 = new JSONObject(o1Str);

		String o2Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ " 'age' : 17, "
						+ " 'height' : 50.00}}";
		JSONObject o2 = new JSONObject(o2Str);
		try{
			boolean ret = dslInstance.compare(o1, o2, null);
			fail("Expected Excetion was not thrown");
		} catch (AssertionError ae){
			// Do Nothing
			System.err.println("Caught " + ae);
		}

    }

	/**
	 * Create 2 objects with some interal structure and compare them with a ignore path
	 */
	@Test 
    public void testCompareComplexObjectsWithIgnoreMatch() throws Exception{
		
		String o1Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ "              'age' : 17, "
						+ "              'height' :  50.00}}";
		JSONObject o1 = new JSONObject(o1Str);

		String o2Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ " 'age' : 17, "
						+ " 'height' : 60.00}}"; // different
		JSONObject o2 = new JSONObject(o2Str);
		
		List<String> ignoreList = new ArrayList<String>();
		ignoreList.add("/student/");
		boolean ret = dslInstance.compare(o1, o2,ignoreList);
	    assertTrue(ret);
    }

	/**
	 * Create 2 objects with some interal structure but different field values and compare them with a ignore path
	 */
	@Test 
    public void testCompareComplexObjectsWithIgnoreNonMatch() throws Exception{
		
		String o1Str = "{  'name2' : {'grade' : '1234'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ "              'age' : 17, "
						+ "              'height' : 50.00}}";
		JSONObject o1 = new JSONObject(o1Str);

		String o2Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ " 'age' : 17, "
						+ " 'height' : 60.00}}";
		JSONObject o2 = new JSONObject(o2Str);
		
		List<String> ignoreList = new ArrayList<String>();
		ignoreList.add("/student/");
		try {
			boolean ret = dslInstance.compare(o1, o2,ignoreList);
			fail("Expected Excetion was not thrown");
		} catch (AssertionError ae){
			// Do Nothing
			System.err.println("Caught " + ae);
		}
    }
	
		/**
	 * Create 2 objects with some interal structure and compare them with a ignore path
	 */
	@Test 
    public void testCompareComplexObjectsWithIgnoreMatch2() throws Exception{
		
		String o1Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ "              'age' : 17, "
						+ "              'height' : 50.00}}";
		JSONObject o1 = new JSONObject(o1Str);

		String o2Str = "{  'name2' : {'grade' : '123'}, "
					    + "'student' : { 'name' : 'Samantha', "
						+ "              'age' : 17, "
						+ "              'height' : 60.00}}"; // different
		JSONObject o2 = new JSONObject(o2Str);
		
		List<String> ignoreList = new ArrayList<String>();
		ignoreList.add("/student/height/");
		boolean ret = dslInstance.compare(o1, o2,ignoreList);
	    assertTrue(ret);
    }

	
}
