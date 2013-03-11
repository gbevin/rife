/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineInputs.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.*;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.InputUnknownException;
import com.uwyn.rife.tools.BeanUtils;
import java.util.Calendar;

public class TestEngineInputs extends TestCaseServerside
{
	public TestEngineInputs(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testValid()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/valid");
		WebResponse response = conversation.getResponse(request);
		assertEquals("another response", response.getText());
	}

	public void testTyped()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/inputs/typed");
		request.setParameter("inputstring1", "astring");
		request.setParameter("inputint1", ""+Integer.MAX_VALUE);
		request.setParameter("inputlong1", ""+Long.MAX_VALUE);
		request.setParameter("inputdouble1", "9873434.4334");
		request.setParameter("inputfloat1", "23.12");
		Calendar cal = Calendar.getInstance();
		cal.set(2007, 6, 11, 12, 58, 22);
		cal.set(Calendar.MILLISECOND, 666);
		cal.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		request.setParameter("inputdate1", BeanUtils.getConcisePreciseDateFormat().format(cal.getTime()));
		response = conversation.getResponse(request);
		assertEquals("inputstring1:astring"+
			"inputstring2:null"+
			"inputstring2default:stringdefault"+
			"inputint1:"+Integer.MAX_VALUE+
			"inputint2:0"+
			"inputint2default:123"+
			"inputlong1:"+Long.MAX_VALUE+
			"inputlong2:0"+
			"inputlong2default:983749876"+
			"inputdouble1:9873434.4334"+
			"inputdouble2:0.0"+
			"inputdouble2default:34778.34"+
			"inputfloat1:23.12"+
			"inputfloat2:0.0"+
			"inputfloat2default:324.34"+
			"inputdate1:20070711125822666+0200"+
			"inputdate2:null"+
			"inputdate2default:20060610115711555+0200", response.getText());
	}

	public void testInputsBean()
	throws Exception
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = new GetMethodWebRequest("http://localhost:8181/inputs/bean/normal");
		WebResponse		response = null;
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");

		response = conversation.getResponse(request);

		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/bean/normal");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : double\nNOTNUMERIC : longObject\nthe string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testInputsBeanPrefix()
	throws Exception
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = new GetMethodWebRequest("http://localhost:8181/inputs/bean/prefix");
		WebResponse		response = null;
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23154");
		request.setParameter("prefix_integer", "893749");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "34878.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "2335454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "12");

		response = conversation.getResponse(request);

		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/bean/prefix");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23fd33");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "zef.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "233f5454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : double\nNOTNUMERIC : longObject\nthe string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testNamedInputsBean()
	throws Exception
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/normal");
		WebResponse		response = null;
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");

		response = conversation.getResponse(request);

		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/normal");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : double\nNOTNUMERIC : longObject\nthe string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testNamedInputsBeanPrefix()
	throws Exception
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/prefix");
		WebResponse		response = null;
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23154");
		request.setParameter("prefix_integer", "893749");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "34878.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "2335454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "12");

		response = conversation.getResponse(request);

		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/prefix");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23fd33");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "zef.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "233f5454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : double\nNOTNUMERIC : longObject\nthe string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testGenerated()
	throws Exception
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/inputs/generated");
		request.setParameter("wantsupdates", "on");
		request.setParameter("colors", new String[] {"orange", "blue", "green"});
		request.setParameter("firstname", "Geert");
		request.setParameter("lastname", "Bevin");
		response = conversation.getResponse(request);
		assertEquals("Geert, Bevin\n"+
			"<input type=\"checkbox\" name=\"wantsupdates\" checked=\"checked\"> I want updates<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"orange\" checked=\"checked\">orange<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"blue\" checked=\"checked\">blue<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"red\">red<br />\n"+
			"<input type=\"radio\" name=\"firstname\" checked=\"checked\"> Geert\n"+
			"<input type=\"radio\" name=\"firstname\"> Nathalie\n"+
			"<select name=\"lastname\">\n"+
			"\t<option value=\"Bevin\" selected=\"selected\">Bevin</option>\n"+
			"\t<option value=\"Mafessoni\">Mafessoni</option>\n"+
			"</select>\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/generated");
		response = conversation.getResponse(request);
		assertEquals("<!--V 'INPUT:firstname'/-->, <!--V 'INPUT:lastname'/-->\n"+
			"<input type=\"checkbox\" name=\"wantsupdates\"> I want updates<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"orange\">orange<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"blue\">blue<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"red\">red<br />\n"+
			"<input type=\"radio\" name=\"firstname\"> Geert\n"+
			"<input type=\"radio\" name=\"firstname\"> Nathalie\n"+
			"<select name=\"lastname\">\n"+
			"\t<option value=\"Bevin\">Bevin</option>\n"+
			"\t<option value=\"Mafessoni\">Mafessoni</option>\n"+
			"</select>\n", response.getText());
	}

	public void testDirectAccess()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/valid");
		request.setParameter("input1", "first");
		request.setParameter("input3", "third");
		request.setParameter("input2", "second");
		WebResponse response = conversation.getResponse(request);
		assertEquals("another responsefirstsecondthird", response.getText());
	}

	public void testDirectAccessInjection()
	throws Exception
	{
		Calendar cal = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone());
		cal.clear();

		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/valid_injection");
		request.setParameter("input1", "first");
		cal.set(2006, 9, 23, 10, 23, 11);
		request.setParameter("input3", BeanUtils.getConcisePreciseDateFormat().format(cal.getTime()));
		cal.set(2007, 2, 11, 15, 4, 54);
		request.setParameter("input2", String.valueOf(cal.getTimeInMillis()));
		WebResponse response = conversation.getResponse(request);
		assertEquals("another responsefirst,20070311150454000+0100,1161591791000", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/valid_injection");
		request.setParameter("input1", "first");
		cal.set(2006, 9, 23, 10, 23, 11);
		request.setParameter("input3", String.valueOf(cal.getTimeInMillis()));
		cal.set(2007, 2, 11, 15, 4, 54);
		request.setParameter("input2", RifeConfig.Tools.getDefaultInputDateFormat().format(cal.getTimeInMillis()));
		response = conversation.getResponse(request);
		assertEquals("another responsefirst,20070311150400000+0100,1161591791000", response.getText());
	}

	public void testNamedInputsBeanInjection()
	throws Throwable
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/normal/injection");
		WebResponse		response = null;
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");

		response = conversation.getResponse(request);

		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/normal/injection");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : double\nNOTNUMERIC : longObject\nthe string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testNamedInputsBeanPrefixInjection()
	throws Throwable
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/prefix/injection");
		WebResponse		response = null;
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23154");
		request.setParameter("prefix_integer", "893749");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "34878.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "2335454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "12");

		response = conversation.getResponse(request);

		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/named_bean/prefix/injection");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23fd33");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "zef.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "233f5454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : double\nNOTNUMERIC : longObject\nthe string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testInvalid()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/invalid");
		try
		{
			WebResponse response = conversation.getResponse(request);
			fail();
			assertNotNull(response);
		}
		catch (HttpInternalErrorException e)
		{
			assertTrue(getLogSink().getInternalException() instanceof InputUnknownException);

			InputUnknownException	e2 = (InputUnknownException)getLogSink().getInternalException();
			assertEquals("input1", e2.getInputName());
			assertEquals(e2.getDeclarationName(), "element/inputs/invalid.xml");
		}
	}

	public void testDefaults()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/defaults");
		request.setParameter("input1", "overridden");
		WebResponse response = conversation.getResponse(request);
		assertEquals("overridden"+
			"the second value"+
			"3rda-3rdd-3rdc-3rdb"+
			"the element config value", response.getText());
	}

	public void testTargetChild()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/targetchild");
		request.setParameter("childinput", "requestvalue");
		request.setParameter("overridden", "requestvalue");
		request.setParameter("aninput", "requestvalue");
		WebResponse response = conversation.getResponse(request);
		assertEquals("globalvalue,requestvalue,middleparentvalue,requestvalue", response.getText());
	}

	public void testMiddleParent()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/targetchild");
		request.setParameter("middleparentinput", "requestvalue");
		request.setParameter("overridden", "requestvalue");
		request.setParameter("aninput", "requestvalue");
		WebResponse response = conversation.getResponse(request);
		assertEquals("globalvalue,requestvalue,topparentvalue,requestvalue", response.getText());
	}

	public void testTopParent()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/targetchild");
		request.setParameter("topparentinput", "requestvalue");
		request.setParameter("overridden", "requestvalue");
		request.setParameter("aninput", "requestvalue");
		WebResponse response = conversation.getResponse(request);
		assertEquals("globalvalue,requestvalue,requestvalue,requestvalue", response.getText());
	}

	public void testThroughExit()
	throws Exception
	{
		setupSite("site/inputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/inputs/exitsource");
		request.setParameter("exitinput", "requestvalue");
		request.setParameter("aninput", "requestvalue");
		WebResponse response = conversation.getResponse(request);
		assertEquals("exitsourcevalue,null", response.getText());
	}

	public void testPathInfoMapping()
	throws Throwable
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request;
		WebResponse response;

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfosource?switch=1");
		response = conversation.getResponse(request);
		response = response.getLinkWith("go").click();
		assertEquals("/anotherinput/523exitsourcevalue1/suffix\n"+
					 "exitsourcevalue1,523,null,null,exitsourcevalue5", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfosource?switch=2");
		response = conversation.getResponse(request);
		response = response.getLinkWith("go").click();
		assertEquals("/\n"+
					 "exitsourcevalue1,exitsourcevalue2,null,null,exitsourcevalue5", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfosource?switch=3");
		response = conversation.getResponse(request);
		response = response.getLinkWith("go").click();
		assertEquals("/myinput/value1/222122/bacacdd\n"+
					 "value1,222122,baca,cdd,exitsourcevalue5", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfosource?switch=4");
		response = conversation.getResponse(request);
		response = response.getForms()[0].submit();
		assertEquals("/anotherinput/523exitsourcevalue1/suffix\n"+
					 "exitsourcevalue1,523,null,null,exitsourcevalue5", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfosource?switch=5");
		response = conversation.getResponse(request);
		response = response.getForms()[0].submit();
		assertEquals("/\n"+
					 "exitsourcevalue1,exitsourcevalue2,null,null,exitsourcevalue5", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfosource?switch=6");
		response = conversation.getResponse(request);
		response = response.getForms()[0].submit();
		assertEquals("/myinput/value1/222122/bacacdd\n"+
					 "value1,222122,baca,cdd,exitsourcevalue5", response.getText());
	}

	public void testPathInfoMappingDirectAccess()
	throws Throwable
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request;
		WebResponse response;

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfotarget/myinput/value1/222122/bacacdd?exitinput3=yoyo");
		response = conversation.getResponse(request);
		assertEquals("/myinput/value1/222122/bacacdd\n"+
					 "value1,222122,yoyo,cdd,null", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfotarget/anotherinput/6786chars/suffix");
		response = conversation.getResponse(request);
		assertEquals("/anotherinput/6786chars/suffix\n"+
					 "chars,6786,null,null,null", response.getText());
	}

	public void testPathInfoMappingReflexive()
	throws Throwable
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request;
		WebResponse response;

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinforeflexive/890");
		response = conversation.getResponse(request);
		assertEquals("/inputs/pathinforeflexive?globalvar=globalvalue&amp;overridden=globalvalue", response.getText());
	}

	public void testPathInfoMappingStrict()
	throws Throwable
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request;
		WebResponse response;

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfostrict/theid1/890");
		response = conversation.getResponse(request);
		assertEquals(".PATHINFOSTRICT1,890", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfostrict/theid1/in/valid");
		try
		{
			response = conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertTrue(true);
		}

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfostrict/9456/theid2");
		response = conversation.getResponse(request);
		assertEquals(".PATHINFOSTRICT2,9456", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inputs/pathinfostrict/in/valid/theid2");
		try
		{
			response = conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertTrue(true);
		}
	}

	public void testReflexiveSubmission()
	throws Throwable
	{
		setupSite("site/inputs.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request;
		WebResponse response;

		request = new GetMethodWebRequest("http://localhost:8181/inputs/reflexivesubmission?inputreflexive=requestvalue1&inputnormal=requestvalue2");
		response = conversation.getResponse(request);
		assertEquals("requestvalue1", response.getElementWithID("inputreflexive").getText());
		assertEquals("requestvalue2", response.getElementWithID("inputnormal").getText());

		WebForm form = response.getForms()[0];
		form.setParameter("param", "paramvalue");
		response = form.submit();
		assertEquals("paramvalue\n"+
					 "outputreflexivevalue\n"+
					 "requestvalue2", response.getText());
	}
}

