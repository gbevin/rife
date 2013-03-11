/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGroovy2ElementInfo.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import junit.framework.TestCase;

public class TestGroovy2ElementInfo extends TestCase
{
	public TestGroovy2ElementInfo(String name)
	{
		super(name);
	}
	
	public void testParser()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement("groovy:groovy/test_groovy2elementinfo1.groovy");
		
		elementinfo_builder.process();

		ElementInfo elementinfo = elementinfo_builder.createElementInfo(new LinkedHashMap<String, GlobalExit>(), new LinkedHashMap<String, GlobalVar>(), new LinkedHashMap<String, String>(), new LinkedHashMap<String, BeanDeclaration>(), new ArrayList<ErrorHandler>());
		
		assertEquals(elementinfo.getPropertyNames().size(), 4+Rep.getProperties().size());
		assertTrue(elementinfo.containsProperty("property1"));
		assertTrue(elementinfo.containsProperty("property2"));
		assertTrue(elementinfo.containsProperty("property3"));
		assertTrue(elementinfo.containsProperty("property4"));
		assertEquals("value1", elementinfo.getProperty("property1"));
		assertEquals("value2", elementinfo.getProperty("property2"));
		assertEquals("value3", elementinfo.getProperty("property3"));
		assertEquals("value4", elementinfo.getProperty("property4"));
		
		assertEquals(elementinfo.getInputNames().size(), 26);
		assertTrue(elementinfo.containsInput("input1"));
		assertTrue(elementinfo.containsInput("input2"));
		assertTrue(elementinfo.containsInput("input3"));
		assertTrue(elementinfo.containsInput("string1"));
		assertTrue(elementinfo.containsInput("string2"));
		assertTrue(elementinfo.containsInput("string3"));
		assertTrue(elementinfo.containsInput("enum4"));
		assertTrue(elementinfo.containsInput("date5"));
		assertTrue(elementinfo.containsInput("string6"));
		assertTrue(elementinfo.containsInput("string7"));
		assertTrue(elementinfo.containsInput("string8"));
		assertTrue(elementinfo.containsInput("prefix_string1"));
		assertTrue(elementinfo.containsInput("prefix_string2"));
		assertTrue(elementinfo.containsInput("prefix_string3"));
		assertTrue(elementinfo.containsInput("prefix_enum4"));
		assertTrue(elementinfo.containsInput("prefix_date5"));
		assertTrue(elementinfo.containsInput("prefixgroup_enum"));
		assertTrue(elementinfo.containsInput("prefixgroup_string"));
		assertTrue(elementinfo.containsInput("prefixgroup_int"));
		assertTrue(elementinfo.containsInput("prefixgroup_longObject"));
		assertTrue(elementinfo.containsInput("prefixgroup_short"));
		assertTrue(elementinfo.containsInput("enum"));
		assertTrue(elementinfo.containsInput("string"));
		assertTrue(elementinfo.containsInput("int"));
		assertTrue(elementinfo.containsInput("longObject"));
		assertTrue(elementinfo.containsInput("short"));
		
		assertEquals(elementinfo.getOutputNames().size(), 27);
		assertTrue(elementinfo.containsOutput("output1"));
		assertTrue(elementinfo.containsOutput("output2"));
		assertTrue(elementinfo.containsOutput("output3"));
		assertTrue(elementinfo.containsOutput("output4"));
		assertTrue(elementinfo.containsOutput("string1"));
		assertTrue(elementinfo.containsOutput("string2"));
		assertTrue(elementinfo.containsOutput("string3"));
		assertTrue(elementinfo.containsOutput("enum4"));
		assertTrue(elementinfo.containsOutput("date5"));
		assertTrue(elementinfo.containsOutput("string6"));
		assertTrue(elementinfo.containsOutput("string7"));
		assertTrue(elementinfo.containsOutput("string8"));
		assertTrue(elementinfo.containsOutput("prefix_enum4"));
		assertTrue(elementinfo.containsOutput("prefix_date5"));
		assertTrue(elementinfo.containsOutput("prefix_string6"));
		assertTrue(elementinfo.containsOutput("prefix_string7"));
		assertTrue(elementinfo.containsOutput("prefix_string8"));
		assertTrue(elementinfo.containsOutput("prefixgroup_enum"));
		assertTrue(elementinfo.containsOutput("prefixgroup_string"));
		assertTrue(elementinfo.containsOutput("prefixgroup_int"));
		assertTrue(elementinfo.containsOutput("prefixgroup_longObject"));
		assertTrue(elementinfo.containsOutput("prefixgroup_short"));
		assertTrue(elementinfo.containsOutput("enum"));
		assertTrue(elementinfo.containsOutput("string"));
		assertTrue(elementinfo.containsOutput("int"));
		assertTrue(elementinfo.containsOutput("longObject"));
		assertTrue(elementinfo.containsOutput("short"));
		
		assertEquals(elementinfo.getIncookieNames().size(), 2);
		assertTrue(elementinfo.containsIncookie("incookie1"));
		assertTrue(elementinfo.containsIncookie("incookie2"));
		
		assertEquals(elementinfo.getOutcookieNames().size(), 4);
		assertTrue(elementinfo.containsOutcookie("outcookie1"));
		assertTrue(elementinfo.containsOutcookie("outcookie2"));
		assertTrue(elementinfo.containsOutcookie("outcookie3"));
		assertTrue(elementinfo.containsOutcookie("outcookie4"));
		
		assertEquals(elementinfo.getNamedInbeanNames().size(), 2);
		assertTrue(elementinfo.containsNamedInbean("inbean1"));
		assertEquals(elementinfo.getNamedInbeanInfo("inbean1").getClassname(), "com.uwyn.rife.engine.testelements.exits.BeanImpl1");
		assertEquals(elementinfo.getNamedInbeanInfo("inbean1").getPrefix(), null);
		assertEquals(elementinfo.getNamedInbeanInfo("inbean1").getGroupName(), null);
		assertTrue(elementinfo.containsNamedInbean("inbean2"));
		assertEquals(elementinfo.getNamedInbeanInfo("inbean2").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo.getNamedInbeanInfo("inbean2").getPrefix(), "prefixgroup_");
		assertEquals(elementinfo.getNamedInbeanInfo("inbean2").getGroupName(), "somegroup");
		
		assertEquals(elementinfo.getNamedOutbeanNames().size(), 2);
		assertTrue(elementinfo.containsNamedOutbean("outbean1"));
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean1").getClassname(), "com.uwyn.rife.engine.testelements.exits.BeanImpl2");
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean1").getPrefix(), "prefix_");
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean1").getGroupName(), null);
		assertTrue(elementinfo.containsNamedOutbean("outbean2"));
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean2").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean2").getPrefix(), null);
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean2").getGroupName(), "somegroup");
		
		assertEquals(elementinfo.getChildTriggerNames().size(), 2);
		assertTrue(elementinfo.containsChildTrigger("input1"));
		assertTrue(elementinfo.containsChildTrigger("input2"));
		
		assertEquals(elementinfo.getExitNames().size(), 4);
		assertTrue(elementinfo.containsExit("exit1"));
		assertTrue(elementinfo.containsExit("exit2"));
		assertTrue(elementinfo.containsExit("exit3"));
		assertTrue(elementinfo.containsExit("exit4"));
		
		assertEquals(elementinfo.getSubmissionNames().size(), 2);
		assertTrue(elementinfo.containsSubmission("submission1"));
		assertTrue(elementinfo.containsSubmission("submission2"));
		
		assertNotNull(elementinfo.getSubmission("submission1"));
		assertNotNull(elementinfo.getSubmission("submission2"));
		
		assertEquals(elementinfo.getSubmission("submission1").getParameterNames().size(), 3);
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("param1"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("param2"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("param3"));
		assertEquals(elementinfo.getSubmission("submission1").getParameterRegexps().size(), 2);
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramA1"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramA2"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramB1"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramB2"));
		assertEquals(elementinfo.getSubmission("submission1").getFileNames().size(), 2);
		assertTrue(elementinfo.getSubmission("submission1").containsFile("file1"));
		assertTrue(elementinfo.getSubmission("submission1").containsFile("file2"));

		assertEquals(elementinfo.getSubmission("submission2").getParameterNames().size(), 2);
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("param1"));
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("param2"));
		assertEquals(elementinfo.getSubmission("submission2").getParameterRegexps().size(), 1);
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("paramC1"));
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("paramC2"));
		assertEquals(elementinfo.getSubmission("submission2").getFileNames().size(), 1);
		assertTrue(elementinfo.getSubmission("submission2").containsFile("file1"));
		
		assertEquals(elementinfo.getSubmission("submission2").getParameterDefaultValues("param1")[0], "default1");
		assertEquals(elementinfo.getSubmission("submission2").getParameterDefaultValues("param1")[1], "default2");
	}
}

