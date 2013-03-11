/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElementInfo.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;

import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

public class TestElementInfo extends TestCase
{
	public TestElementInfo(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	throws EngineException
	{
		ElementInfo element_info1 = null;
		ElementInfo element_info2 = null;
		
		element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		assertNotNull(element_info1);
		element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		assertNotNull(element_info2);
	}
	
	public void testGetElement()
	throws EngineException
	{
		// use publically available classes
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", com.uwyn.rife.engine.testelements.engine.Simple.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", com.uwyn.rife.engine.testelements.inputs.Normal.class.getName(), ElementType.JAVA_CLASS);
		
		// these can't be the same classes since the classloader is different
		assertEquals(element_info1.getElement().getClass().getName(), com.uwyn.rife.engine.testelements.engine.Simple.class.getName());
		assertEquals(element_info2.getElement().getClass().getName(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
	}
	
	public void testClone()
	throws EngineException
	{
		ElementInfo element_info_dest = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		element_info_dest.addInput("input1", null);
		element_info_dest.addInput("input2", null);
		element_info_dest.addInput("input3", null);

		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
	
		LinkedHashMap<String, GlobalExit>	globalexits = new LinkedHashMap<String, GlobalExit>();
		globalexits.put("globalexit1", new GlobalExit("DEST1", false, false, false, false, false, false).setGroupId(0));
		globalexits.put("globalexit2", new GlobalExit(null, true, false, false, false, false, true).setGroupId(0));
		globalexits.put("globalexit3", new GlobalExit(null, false, true, false, false, false, false).setGroupId(0));
		element_info.setGlobalExits(globalexits);

		LinkedHashMap<String, GlobalVar>	globalvars = new LinkedHashMap<String, GlobalVar>();
		globalvars.put("globalvar1", new GlobalVar(null).setGroupId(0));
		globalvars.put("globalvar2", new GlobalVar(null).setGroupId(0));
		globalvars.put("globalvar3", new GlobalVar(null).setGroupId(0));
		element_info.setGlobalVars(globalvars);

		element_info.addStaticProperty("property1", "value1");
		element_info.addStaticProperty("property2", "value2");
		element_info.addStaticProperty("property3", "value3");
	
		element_info.addInput("input1", null);
		element_info.addInput("input2", null);
		element_info.addInput("input3", null);

		element_info.addOutput("output1", null);
		element_info.addOutput("output2", null);
		element_info.addOutput("output3", null);
		
		element_info.addIncookie("incookie1", null);
		element_info.addIncookie("incookie2", null);
		element_info.addIncookie("incookie3", null);

		element_info.addOutcookie("outcookie1", null);
		element_info.addOutcookie("outcookie2", null);
		element_info.addOutcookie("outcookie3", null);

		LinkedHashMap<String, BeanDeclaration>	namedglobalbeans = new LinkedHashMap<String, BeanDeclaration>();
		namedglobalbeans.put("globalbean1", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", null, null));
		namedglobalbeans.put("globalbean2", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));
		element_info.setNamedGlobalBeans(namedglobalbeans);

		element_info.addNamedInbean("inbean1", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));
		element_info.addNamedInbean("inbean2", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", null, null));
		
		element_info.addNamedOutbean("outbean1", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));
		element_info.addNamedOutbean("outbean2", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", null, null));
		element_info.addNamedOutbean("outbean3", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));

		element_info.addExit("exit1");
		element_info.addExit("exit2");
		element_info.addExit("exit3");
		
		element_info.setFlowLink(new FlowLink("exit1", element_info_dest, false, false, false, false, true));
		FlowLink flowlink = new FlowLink("exit2", null, true, true, true, true, false);
		element_info.setFlowLink(flowlink);
		
		element_info.addDataLink(new DataLink("output1", element_info_dest, false, "input1", null));
		element_info.addDataLink(new DataLink("output1", element_info_dest, false, "input2", flowlink));
		element_info.addDataLink(new DataLink("output1", null, true, "input3", null));
		element_info.addDataLink(new DataLink("output2", element_info_dest, false, "input2", null));
		element_info.addDataLink(new DataLink("output2", null, true, "input3", null));

		Submission submission1 = new Submission();
		Submission submission2 = new Submission();
		Submission submission3 = new Submission();
		element_info.addSubmission("submission1", submission1);
		element_info.addSubmission("submission2", submission2);
		element_info.addSubmission("submission3", submission3);
		submission1.addParameter("parameter1", null);
		submission1.addParameter("parameter2", null);
		submission1.addParameter("parameter3", null);
		submission2.addParameter("parameter1", null);
		submission2.addParameter("parameter2", null);
		submission3.addParameter("parameter1", null);
		
		// make a clone
		ElementInfo element_info_clone = element_info.clone();
		assertNotNull(element_info_clone);

		// modify the original
		globalexits.put("globalexit4", new GlobalExit("DEST2", false, false, false, false, true, true).setGroupId(0));
		globalvars.put("globalvar4", new GlobalVar(null).setGroupId(0));
		namedglobalbeans.put("globalbean3", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", "peefix", null));
		element_info.addStaticProperty("property4", "value4");
		element_info.addInput("input4", null);
		element_info.addOutput("output4", null);
		element_info.addIncookie("incookie4", null);
		element_info.addOutcookie("outcookie4", null);
		element_info.addNamedInbean("inbean3", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));
		element_info.addNamedOutbean("outbean4", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));
		element_info.addExit("exit4");
		element_info.setFlowLink(new FlowLink("exit3", element_info_dest, false, false, false, false, false));
		element_info.addDataLink(new DataLink("output3", element_info_dest, false, "input3", flowlink));
		submission1.addParameter("parameter4", null);
		Submission submission4 = new Submission();
		element_info.addSubmission("submission4", submission4);
		submission4.addParameter("parameter1", null);
		
		// check if the clone has remained intact
		assertEquals(4, element_info.getGlobalExitNames().size());
		assertEquals(3, element_info_clone.getGlobalExitNames().size());
		assertEquals(4, element_info.getGlobalVarNames().size());
		assertEquals(3, element_info_clone.getGlobalVarNames().size());
		assertEquals(4+Rep.getProperties().size(), element_info.getPropertyNames().size());
		assertEquals(3+Rep.getProperties().size(), element_info_clone.getPropertyNames().size());
		assertEquals(4, element_info.getInputNames().size());
		assertEquals(3, element_info_clone.getInputNames().size());
		assertEquals(4, element_info.getOutputNames().size());
		assertEquals(3, element_info_clone.getOutputNames().size());
		assertEquals(4, element_info.getIncookieNames().size());
		assertEquals(3, element_info_clone.getIncookieNames().size());
		assertEquals(4, element_info.getOutcookieNames().size());
		assertEquals(3, element_info_clone.getOutcookieNames().size());
		assertEquals(3, element_info.getNamedGlobalBeanNames().size());
		assertEquals(2, element_info_clone.getNamedGlobalBeanNames().size());
		assertEquals(3, element_info.getNamedInbeanNames().size());
		assertEquals(2, element_info_clone.getNamedInbeanNames().size());
		assertEquals(4, element_info.getNamedOutbeanNames().size());
		assertEquals(3, element_info_clone.getNamedOutbeanNames().size());
		assertSame(element_info.getFlowLink("exit3").getTarget(), element_info_dest);
		assertNull(element_info_clone.getFlowLink("exit3"));
		assertEquals(1, element_info.getDataLinkInputs("output3", element_info_dest, false, flowlink).size());
		assertNull(element_info_clone.getDataLinkInputs("output3", element_info_dest, false, flowlink));
		assertEquals(4, element_info.getSubmission("submission1").getParameterNames().size());
		assertEquals(3, element_info_clone.getSubmission("submission1").getParameterNames().size());
		assertEquals(4, element_info.getSubmissionNames().size());
		assertEquals(3, element_info_clone.getSubmissionNames().size());
	}
	
	public void testGetUninstantiatableElement()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test_not_instantiatable.xml", "text/html", TestElementNotInstantiatable.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.getElement();
			fail();
		}
		catch (EngineException e)
		{
			assertTrue(true);
		}
	}
	
	public void testNoInitialPropertyNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getPropertyNames().size(), 0);
	}
	
	public void testNoInitialGlobalNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertNull(element_info.getGlobalVarNames());
	}
	
	public void testNoInitialInputNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getInputNames().size(), 0);
	}
	
	public void testNoInitialOutputNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getOutputNames().size(), 0);
	}
	
	public void testNoInitialIncookieNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getIncookieNames().size(), 0);
	}
	
	public void testNoInitialOutcookieNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getOutcookieNames().size(), 0);
	}
	
	public void testNoInitialNamedGlobalbeanNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertNull(element_info.getNamedGlobalBeanNames());
	}
	
	public void testNoInitialNamedInbeanNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getNamedInbeanNames().size(), 0);
	}
	
	public void testNoInitialNamedOutbeanNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getNamedOutbeanNames().size(), 0);
	}
	
	public void testNoInitialExitNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getExitNames().size(), 0);
	}
	
	public void testNoInitialSubmissionNames()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertEquals(element_info.getSubmissionNames().size(), 0);
	}
	
	public void testAddGlobal()
	throws EngineException
	{
		ElementInfo					element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		LinkedHashMap<String, GlobalVar>	globals = new LinkedHashMap<String, GlobalVar>();
		globals.put("globalvar1", new GlobalVar(null).setGroupId(0));
		globals.put("globalvar2", new GlobalVar(null).setGroupId(0));
		globals.put("globalvar3", new GlobalVar(null).setGroupId(0));
		
		element_info.setGlobalVars(globals);
		
		assertEquals(element_info.getGlobalVarNames().size(), 3);
	
		assertTrue(element_info.containsGlobalVar("globalvar1"));
		assertTrue(element_info.containsGlobalVar("globalvar2"));
		assertTrue(element_info.containsGlobalVar("globalvar3"));
	}
	
	public void testAddStaticProperty()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		element_info.addStaticProperty("property1", "value1");
		assertEquals(element_info.getPropertyNames().size(), 1+Rep.getProperties().size());
		element_info.addStaticProperty("property2", "value2");
		assertEquals(element_info.getPropertyNames().size(), 2+Rep.getProperties().size());
		element_info.addStaticProperty("property3", "value3");
		assertEquals(element_info.getPropertyNames().size(), 3+Rep.getProperties().size());
		
		assertTrue(element_info.containsProperty("property1"));
		assertTrue(element_info.containsProperty("property2"));
		assertTrue(element_info.containsProperty("property3"));
		
		assertEquals("value1", element_info.getProperty("property1"));
		assertEquals("value2", element_info.getProperty("property2"));
		assertEquals("value3", element_info.getProperty("property3"));
	}
	
	public void testAddInput()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		element_info.addInput("input1", null);
		assertEquals(element_info.getInputNames().size(), 1);
		element_info.addInput("input2", null);
		assertEquals(element_info.getInputNames().size(), 2);
		element_info.addInput("input3", null);
		assertEquals(element_info.getInputNames().size(), 3);
	
		assertTrue(element_info.containsInput("input1"));
		assertTrue(element_info.containsInput("input2"));
		assertTrue(element_info.containsInput("input3"));
	}
	
	public void testAddOutput()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		element_info.addOutput("output1", null);
		assertEquals(element_info.getOutputNames().size(), 1);
		element_info.addOutput("output2", null);
		assertEquals(element_info.getOutputNames().size(), 2);
		element_info.addOutput("output3", null);
		assertEquals(element_info.getOutputNames().size(), 3);
		
		assertTrue(element_info.containsOutput("output1"));
		assertTrue(element_info.containsOutput("output2"));
		assertTrue(element_info.containsOutput("output3"));
	}
	
	public void testAddIncookie()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		element_info.addIncookie("incookie1", null);
		assertEquals(element_info.getIncookieNames().size(), 1);
		element_info.addIncookie("incookie2", null);
		assertEquals(element_info.getIncookieNames().size(), 2);
		element_info.addIncookie("incookie3", null);
		assertEquals(element_info.getIncookieNames().size(), 3);
	
		assertTrue(element_info.containsIncookie("incookie1"));
		assertTrue(element_info.containsIncookie("incookie2"));
		assertTrue(element_info.containsIncookie("incookie3"));
	}
	
	public void testAddOutcookie()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		element_info.addOutcookie("outcookie1", null);
		assertEquals(element_info.getOutcookieNames().size(), 1);
		element_info.addOutcookie("outcookie2", null);
		assertEquals(element_info.getOutcookieNames().size(), 2);
		element_info.addOutcookie("outcookie3", null);
		assertEquals(element_info.getOutcookieNames().size(), 3);
		
		assertTrue(element_info.containsOutcookie("outcookie1"));
		assertTrue(element_info.containsOutcookie("outcookie2"));
		assertTrue(element_info.containsOutcookie("outcookie3"));
	}
	
	public void testAddNamedGlobalBean()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		BeanDeclaration	bean_declaration1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null);
		BeanDeclaration	bean_declaration2 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", null);
		LinkedHashMap<String, BeanDeclaration>	namedglobalbeans = new LinkedHashMap<String, BeanDeclaration>();
		namedglobalbeans.put("globalbean1", bean_declaration1);
		namedglobalbeans.put("globalbean2", bean_declaration2);
		namedglobalbeans.put("globalbean3", bean_declaration1);
		element_info.setNamedGlobalBeans(namedglobalbeans);

		assertEquals(element_info.getNamedGlobalBeanNames().size(), 3);
	
		assertTrue(element_info.containsNamedGlobalBean("globalbean1"));
		assertTrue(element_info.containsNamedGlobalBean("globalbean2"));
		assertTrue(element_info.containsNamedGlobalBean("globalbean3"));
		
		BeanDeclaration	bean_declaration = null;
		
		bean_declaration = element_info.getNamedGlobalBeanInfo("globalbean1");
		assertEquals(bean_declaration1.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration1.getPrefix(), bean_declaration.getPrefix());
		bean_declaration = null;
		bean_declaration = element_info.getNamedGlobalBeanInfo("globalbean2");
		assertEquals(bean_declaration2.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration2.getPrefix(), bean_declaration.getPrefix());
		bean_declaration = null;
		bean_declaration = element_info.getNamedGlobalBeanInfo("globalbean3");
		assertEquals(bean_declaration1.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration1.getPrefix(), bean_declaration.getPrefix());
	}
	
	public void testAddNamedInbean()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		BeanDeclaration	bean_declaration1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null);
		BeanDeclaration	bean_declaration2 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", null);
		element_info.addNamedInbean("inbean1", bean_declaration1);
		assertEquals(element_info.getNamedInbeanNames().size(), 1);
		element_info.addNamedInbean("inbean2", bean_declaration1);
		assertEquals(element_info.getNamedInbeanNames().size(), 2);
		element_info.addNamedInbean("inbean3", bean_declaration2);
		assertEquals(element_info.getNamedInbeanNames().size(), 3);
	
		assertTrue(element_info.containsNamedInbean("inbean1"));
		assertTrue(element_info.containsNamedInbean("inbean2"));
		assertTrue(element_info.containsNamedInbean("inbean3"));
		
		BeanDeclaration	bean_declaration = null;
		
		bean_declaration = element_info.getNamedInbeanInfo("inbean1");
		assertEquals(bean_declaration1.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration1.getPrefix(), bean_declaration.getPrefix());
		bean_declaration = null;
		bean_declaration = element_info.getNamedInbeanInfo("inbean2");
		assertEquals(bean_declaration1.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration1.getPrefix(), bean_declaration.getPrefix());
		bean_declaration = null;
		bean_declaration = element_info.getNamedInbeanInfo("inbean3");
		assertEquals(bean_declaration2.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration2.getPrefix(), bean_declaration.getPrefix());
	}
	
	public void testAddNamedOutbean()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		BeanDeclaration	bean_declaration1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null);
		BeanDeclaration	bean_declaration2 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", null);
		element_info.addNamedOutbean("outbean1", bean_declaration1);
		assertEquals(element_info.getNamedOutbeanNames().size(), 1);
		element_info.addNamedOutbean("outbean2", bean_declaration1);
		assertEquals(element_info.getNamedOutbeanNames().size(), 2);
		element_info.addNamedOutbean("outbean3", bean_declaration2);
		assertEquals(element_info.getNamedOutbeanNames().size(), 3);
	
		assertTrue(element_info.containsNamedOutbean("outbean1"));
		assertTrue(element_info.containsNamedOutbean("outbean2"));
		assertTrue(element_info.containsNamedOutbean("outbean3"));
		
		BeanDeclaration	bean_declaration = null;
		
		bean_declaration = element_info.getNamedOutbeanInfo("outbean1");
		assertEquals(bean_declaration1.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration1.getPrefix(), bean_declaration.getPrefix());
		bean_declaration = null;
		bean_declaration = element_info.getNamedOutbeanInfo("outbean2");
		assertEquals(bean_declaration1.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration1.getPrefix(), bean_declaration.getPrefix());
		bean_declaration = null;
		bean_declaration = element_info.getNamedOutbeanInfo("outbean3");
		assertEquals(bean_declaration2.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration2.getPrefix(), bean_declaration.getPrefix());
	}
	
	public void testAddChildTrigger()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addInput("childtrigger1", null);
		element_info.addInput("childtrigger2", null);
		element_info.addInput("childtrigger3", null);
		
		element_info.addChildTrigger("childtrigger1");
		assertEquals(element_info.getChildTriggerNames().size(), 1);
		element_info.addChildTrigger("childtrigger2");
		assertEquals(element_info.getChildTriggerNames().size(), 2);
		element_info.addChildTrigger("childtrigger3");
		assertEquals(element_info.getChildTriggerNames().size(), 3);
	
		assertTrue(element_info.containsChildTrigger("childtrigger1"));
		assertTrue(element_info.containsChildTrigger("childtrigger2"));
		assertTrue(element_info.containsChildTrigger("childtrigger3"));
	}
	
	public void testAddExit()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		element_info.addExit("exit1");
		assertEquals(element_info.getExitNames().size(), 1);
		element_info.addExit("exit2");
		assertEquals(element_info.getExitNames().size(), 2);
		element_info.addExit("exit3");
		assertEquals(element_info.getExitNames().size(), 3);
		
		assertTrue(element_info.containsExit("exit1"));
		assertTrue(element_info.containsExit("exit2"));
		assertTrue(element_info.containsExit("exit3"));
	}
	
	public void testAddSubmission()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		Submission submission1 = new Submission();
		Submission submission2 = new Submission();
		Submission submission3 = new Submission();
		
		element_info.addSubmission("submission1", submission1);
		assertEquals(element_info.getSubmissionNames().size(), 1);
		element_info.addSubmission("submission2", submission2);
		assertEquals(element_info.getSubmissionNames().size(), 2);
		element_info.addSubmission("submission3", submission3);
		assertEquals(element_info.getSubmissionNames().size(), 3);

		assertTrue(element_info.containsSubmission("submission1"));
		assertTrue(element_info.containsSubmission("submission2"));
		assertTrue(element_info.containsSubmission("submission3"));

		assertSame(element_info.getSubmission("submission1"), submission1);
		assertSame(element_info.getSubmission("submission2"), submission2);
		assertSame(element_info.getSubmission("submission3"), submission3);
	}
	
	public void testAddReservedInput()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addInput(ReservedParameters.SUBMISSION, null);
			fail();
		}
		catch (ReservedInputNameException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getInputName(), ReservedParameters.SUBMISSION);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			element_info.addInput(ReservedParameters.CHILDREQUEST, null);
			fail();
		}
		catch (ReservedInputNameException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getInputName(), ReservedParameters.CHILDREQUEST);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			element_info.addInput(ReservedParameters.TRIGGERLIST, null);
			fail();
		}
		catch (ReservedInputNameException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getInputName(), ReservedParameters.TRIGGERLIST);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertEquals(element_info.getInputNames().size(), 0);
	}
	
	public void testAddReservedOutput()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addOutput(ReservedParameters.SUBMISSION, null);
			fail();
		}
		catch (ReservedOutputNameException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getOutputName(), ReservedParameters.SUBMISSION);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			element_info.addOutput(ReservedParameters.CHILDREQUEST, null);
			fail();
		}
		catch (ReservedOutputNameException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getOutputName(), ReservedParameters.CHILDREQUEST);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			element_info.addOutput(ReservedParameters.TRIGGERLIST, null);
			fail();
		}
		catch (ReservedOutputNameException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getOutputName(), ReservedParameters.TRIGGERLIST);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertEquals(element_info.getOutputNames().size(), 0);
	}
	
	public void testAddDuplicateGlobal()
	throws EngineException
	{
		ElementInfo						element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		LinkedHashMap<String, GlobalVar>	globals = new LinkedHashMap<String, GlobalVar>();
		globals.put("globalvar1", new GlobalVar(null).setGroupId(0));
		globals.put("globalvar2", new GlobalVar(null).setGroupId(0));
		globals.put("globalvar3", new GlobalVar(null).setGroupId(0));
		globals.put("globalvar1", new GlobalVar(null).setGroupId(0));
		
		element_info.setGlobalVars(globals);
		
		assertEquals(element_info.getGlobalVarNames().size(), 3);
	
		assertTrue(element_info.containsGlobalVar("globalvar1"));
		assertTrue(element_info.containsGlobalVar("globalvar2"));
		assertTrue(element_info.containsGlobalVar("globalvar3"));
	}
	
	public void testAddDuplicateStaticProperty()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addStaticProperty("property1", "value1");
			assertEquals(element_info.getPropertyNames().size(), 1+Rep.getProperties().size());
			assertEquals("value1", element_info.getProperty("property1"));
			element_info.addStaticProperty("property1", "value2");
			assertEquals(element_info.getPropertyNames().size(), 1+Rep.getProperties().size());
			assertEquals("value2", element_info.getProperty("property1"));
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testAddDuplicateInput()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addInput("input1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getInputNames().size(), 1);
		try
		{
			element_info.addInput("input1", null);
			fail();
		}
		catch (InputExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getInputName(), "input1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getInputNames().size(), 1);
	}
	
	public void testAddDuplicateOutput()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addOutput("output1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getOutputNames().size(), 1);
		try
		{
			element_info.addOutput("output1", null);
			fail();
		}
		catch (OutputExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getOutputName(), "output1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getOutputNames().size(), 1);
	}

	public void testAddDuplicateIncookie()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addIncookie("incookie1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getIncookieNames().size(), 1);
		try
		{
			element_info.addIncookie("incookie1", null);
			fail();
		}
		catch (IncookieExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getIncookieName(), "incookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getIncookieNames().size(), 1);
	}
	
	public void testAddDuplicateOutcookie()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addOutcookie("outcookie1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getOutcookieNames().size(), 1);
		try
		{
			element_info.addOutcookie("outcookie1", null);
			fail();
		}
		catch (OutcookieExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getOutcookieName(), "outcookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getOutcookieNames().size(), 1);
	}
	
	public void testAddDuplicateNamedGlobalBean()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		BeanDeclaration	bean_declaration1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null);
		BeanDeclaration	bean_declaration2 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", null);
		LinkedHashMap<String, BeanDeclaration>	namedglobalbeans = new LinkedHashMap<String, BeanDeclaration>();
		namedglobalbeans.put("globalbean1", bean_declaration1);
		namedglobalbeans.put("globalbean1", bean_declaration2);
		element_info.setNamedGlobalBeans(namedglobalbeans);

		assertEquals(element_info.getNamedGlobalBeanNames().size(), 1);
	
		assertTrue(element_info.containsNamedGlobalBean("globalbean1"));
		
		BeanDeclaration	bean_declaration = null;
		
		bean_declaration = element_info.getNamedGlobalBeanInfo("globalbean1");
		assertEquals(bean_declaration2.getClassname(), bean_declaration.getClassname());
		assertEquals(bean_declaration2.getPrefix(), bean_declaration.getPrefix());
	}

	public void testAddDuplicateNamedInbean()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addNamedInbean("inbean1", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getNamedInbeanNames().size(), 1);
		try
		{
			element_info.addNamedInbean("inbean1", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", null, null));
			fail();
		}
		catch (NamedInbeanExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getInbeanName(), "inbean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getNamedInbeanNames().size(), 1);
	}
	
	public void testAddDuplicateNamedOutbean()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addNamedOutbean("outbean1", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null));
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getNamedOutbeanNames().size(), 1);
		try
		{
			element_info.addNamedOutbean("outbean1", new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", null, null));
			fail();
		}
		catch (NamedOutbeanExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getOutbeanName(), "outbean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getNamedOutbeanNames().size(), 1);
	}
	
	public void testAddDuplicateChildTrigger()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addInput("childtrigger1", null);
		
		try
		{
			element_info.addChildTrigger("childtrigger1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getChildTriggerNames().size(), 1);
		try
		{
			element_info.addChildTrigger("childtrigger1");
			fail();
		}
		catch (ChildTriggerExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getChildTriggerName(), "childtrigger1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getChildTriggerNames().size(), 1);
	}
	
	public void testAddDuplicateExit()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addExit("exit1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getExitNames().size(), 1);
		try
		{
			element_info.addExit("exit1");
			fail();
		}
		catch (ExitExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getExitName(), "exit1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getExitNames().size(), 1);
	}
	
	public void testAddDuplicateSubmission()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addSubmission("submission1", new Submission());
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getSubmissionNames().size(), 1);
		try
		{
			element_info.addSubmission("submission1", new Submission());
			fail();
		}
		catch (SubmissionExistsException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(element_info.getSubmissionNames().size(), 1);
	}
	
	public void testGlobalVarConflicts()
	throws EngineException
	{
		ElementInfo					element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		LinkedHashMap<String, GlobalVar>	globals = new LinkedHashMap<String, GlobalVar>();
		Submission					submission = null;
		
		globals.put("input1", new GlobalVar(null).setGroupId(0));
		globals.put("output1", new GlobalVar(null).setGroupId(0));
		globals.put("property1", new GlobalVar(null).setGroupId(0));
		globals.put("parameter1", new GlobalVar(null).setGroupId(0));
		globals.put("file1", new GlobalVar(null).setGroupId(0));
		
		try
		{
			element_info.setGlobalVars(globals);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addInput("input1", null);
		try
		{
			element_info.setGlobalVars(globals);
			fail();
		}
		catch (GlobalVarInputConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "input1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addOutput("output1", null);
		try
		{
			element_info.setGlobalVars(globals);
			fail();
		}
		catch (GlobalVarOutputConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "output1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addParameter("parameter1", null);
		try
		{
			element_info.setGlobalVars(globals);
			fail();
		}
		catch (GlobalVarParameterConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "parameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addFile("file1");
		try
		{
			element_info.setGlobalVars(globals);
			fail();
		}
		catch (GlobalVarFileConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGlobalCookieConflicts()
	throws EngineException
	{
		ElementInfo				element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		LinkedHashMap<String, String>	globals = new LinkedHashMap<String, String>();
		Submission				submission = null;

		globals.put("incookie1", null);
		globals.put("outcookie1", null);
		globals.put("property1", null);
		globals.put("parameter1", null);
		globals.put("file1", null);

		try
		{
			element_info.setGlobalCookies(globals);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addIncookie("incookie1", null);
		try
		{
			element_info.setGlobalCookies(globals);
			fail();
		}
		catch (GlobalCookieIncookieConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "incookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addOutcookie("outcookie1", null);
		try
		{
			element_info.setGlobalCookies(globals);
			fail();
		}
		catch (GlobalCookieOutcookieConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "outcookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addParameter("parameter1", null);
		try
		{
			element_info.setGlobalCookies(globals);
			fail();
		}
		catch (GlobalCookieParameterConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "parameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addFile("file1");
		try
		{
			element_info.setGlobalCookies(globals);
			fail();
		}
		catch (GlobalCookieFileConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testInputConflicts()
	throws EngineException
	{
		ElementInfo	element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);

		try
		{
			element_info.addInput("input1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		LinkedHashMap<String, GlobalVar>	globals = new LinkedHashMap<String, GlobalVar>();
		globals.put("input1", new GlobalVar(null).setGroupId(0));
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.setGlobalVars(globals);
		try
		{
			element_info.addInput("input1", null);
			fail();
		}
		catch (InputGlobalVarConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "input1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		Submission	submission = null;
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addParameter("input1", null);
		try
		{
			element_info.addInput("input1", null);
			fail();
		}
		catch (InputParameterConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "input1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addFile("input1");
		try
		{
			element_info.addInput("input1", null);
			fail();
		}
		catch (InputFileConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "input1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addParameterRegexp("regexpparameter(.*)");
		try
		{
			element_info.addInput("regexpparameter1", null);
			fail();
		}
		catch (InputParameterConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "regexpparameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testOutputConflicts()
	throws EngineException
	{
		ElementInfo	element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);

		try
		{
			element_info.addOutput("output1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		LinkedHashMap<String, GlobalVar>	globals = new LinkedHashMap<String, GlobalVar>();
		globals.put("output1", new GlobalVar(null).setGroupId(0));
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.setGlobalVars(globals);
		try
		{
			element_info.addOutput("output1", null);
			fail();
		}
		catch (OutputGlobalVarConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "output1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParamConflicts()
	throws EngineException
	{
		ElementInfo	element_info = null;
		Submission	submission = null;
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		try
		{
			submission.addParameter("parameter1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		element_info.addInput("parameter1", null);
		try
		{
			submission.addParameter("parameter1", null);
			fail();
		}
		catch (ParameterInputConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "parameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addFile("parameter1");
		try
		{
			submission.addParameter("parameter1", null);
			fail();
		}
		catch (ParameterFileConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "parameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		LinkedHashMap<String, GlobalVar>	globals = new LinkedHashMap<String, GlobalVar>();
		globals.put("parameter1", new GlobalVar(null).setGroupId(0));
		element_info.setGlobalVars(globals);
		try
		{
			submission.addParameter("parameter1", null);
			fail();
		}
		catch (ParameterGlobalVarConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "parameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testFileConflicts()
	throws EngineException
	{
		ElementInfo	element_info = null;
		Submission	submission = null;
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		try
		{
			submission.addFile("file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		element_info.addInput("file1", null);
		try
		{
			submission.addFile("file1");
			fail();
		}
		catch (FileInputConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addParameter("file1", null);
		try
		{
			submission.addFile("file1");
			fail();
		}
		catch (FileParameterConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		LinkedHashMap<String, GlobalVar>	globals = new LinkedHashMap<String, GlobalVar>();
		globals.put("file1", new GlobalVar(null).setGroupId(0));
		element_info.setGlobalVars(globals);
		try
		{
			submission.addFile("file1");
			fail();
		}
		catch (FileGlobalVarConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIncookieConflicts()
	throws EngineException
	{
		ElementInfo	element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);

		try
		{
			element_info.addIncookie("incookie1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		LinkedHashMap<String, String>	globals = new LinkedHashMap<String, String>();
		globals.put("incookie1",null);
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.setGlobalCookies(globals);
		try
		{
			element_info.addIncookie("incookie1", null);
			fail();
		}
		catch (IncookieGlobalCookieConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "incookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		Submission	submission = null;
		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addParameter("incookie1", null);
		try
		{
			element_info.addIncookie("incookie1", null);
			fail();
		}
		catch (IncookieParameterConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "incookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission = new Submission();
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addSubmission("submission1", submission);
		submission.addFile("incookie1");
		try
		{
			element_info.addIncookie("incookie1", null);
			fail();
		}
		catch (IncookieFileConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "incookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testOutcookieConflicts()
	throws EngineException
	{
		ElementInfo	element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);

		try
		{
			element_info.addOutcookie("outcookie1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		LinkedHashMap<String, String>	globals = new LinkedHashMap<String, String>();
		globals.put("outcookie1",null);
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.setGlobalCookies(globals);
		try
		{
			element_info.addOutcookie("outcookie1", null);
			fail();
		}
		catch (OutcookieGlobalCookieConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "outcookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testNamedGlobalBeanConflicts()
	throws EngineException
	{
		ElementInfo							element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		LinkedHashMap<String, BeanDeclaration>	namedglobalbeans = new LinkedHashMap<String, BeanDeclaration>();
		
		BeanDeclaration	bean_declaration1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null);
		BeanDeclaration	bean_declaration2 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", null);
		
		namedglobalbeans.put("inbean1", bean_declaration1);
		namedglobalbeans.put("outbean1", bean_declaration2);
		
		try
		{
			element_info.setNamedGlobalBeans(namedglobalbeans);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addNamedInbean("inbean1", bean_declaration2);
		try
		{
			element_info.setNamedGlobalBeans(namedglobalbeans);
			fail();
		}
		catch (NamedInbeanGlobalBeanConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "inbean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.addNamedOutbean("outbean1", bean_declaration1);
		try
		{
			element_info.setNamedGlobalBeans(namedglobalbeans);
			fail();
		}
		catch (NamedOutbeanGlobalBeanConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "outbean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testNamedInbeanConflicts()
	throws EngineException
	{
		ElementInfo							element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		LinkedHashMap<String, BeanDeclaration>	namedglobalbeans = new LinkedHashMap<String, BeanDeclaration>();
		
		BeanDeclaration	bean_declaration1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null);
		BeanDeclaration	bean_declaration2 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", null);
		
		namedglobalbeans.put("inbean1", bean_declaration1);
		
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.setNamedGlobalBeans(namedglobalbeans);
		try
		{
			element_info.addNamedInbean("inbean1", bean_declaration2);
			fail();
		}
		catch (NamedInbeanGlobalBeanConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "inbean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testNamedOutbeanConflicts()
	throws EngineException
	{
		ElementInfo							element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		LinkedHashMap<String, BeanDeclaration>	namedglobalbeans = new LinkedHashMap<String, BeanDeclaration>();
		
		BeanDeclaration	bean_declaration1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null);
		BeanDeclaration	bean_declaration2 = new BeanDeclaration("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", null);
		
		namedglobalbeans.put("outbean1", bean_declaration2);
		
		element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element_info.setNamedGlobalBeans(namedglobalbeans);
		try
		{
			element_info.addNamedOutbean("outbean1", bean_declaration1);
			fail();
		}
		catch (NamedOutbeanGlobalBeanConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "outbean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testAddChildTriggerWithoutInput()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info.addChildTrigger("childtrigger1");
		}
		catch (ChildTriggerVariableUnknownException e)
		{
			assertEquals("childtrigger1", e.getChildTriggerName());
		}
		assertEquals(element_info.getChildTriggerNames().size(), 0);
		assertTrue(false == element_info.containsChildTrigger("childtrigger1"));
	}
	
	public void testAddInputDefaultValues()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertTrue(false == element_info.hasInputDefaults());
	
		element_info.addInput("input1", new String[] {"one","two"});
		element_info.addInput("input2", new String[] {"three"});
		element_info.addInput("input3", null);
	
		assertTrue(element_info.hasInputDefaults());
	
		assertTrue(element_info.hasInputDefaultValues("input1"));
		assertTrue(element_info.hasInputDefaultValues("input2"));
		assertTrue(false == element_info.hasInputDefaultValues("input3"));
		
		assertNotNull(element_info.getInputDefaultValues("input1"));
		assertNotNull(element_info.getInputDefaultValues("input2"));
		assertNull(element_info.getInputDefaultValues("input3"));

		assertEquals(2, element_info.getInputDefaultValues("input1").length);
		assertEquals(1, element_info.getInputDefaultValues("input2").length);

		assertEquals("one", element_info.getInputDefaultValues("input1")[0]);
		assertEquals("two", element_info.getInputDefaultValues("input1")[1]);
		assertEquals("three", element_info.getInputDefaultValues("input2")[0]);
	}
	
	public void testAddOutputDefaultValues()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertTrue(false == element_info.hasOutputDefaults());
	
		String[] defaults1 = new String[]{"one", "two"};
		String[] defaults2 = new String[]{"three"};
		
		element_info.addOutput("output1", defaults1);
		element_info.addOutput("output2", defaults2);
		element_info.addOutput("output3", null);
		
		assertTrue(element_info.hasOutputDefaults());
	
		assertTrue(element_info.hasOutputDefaultValues("output1"));
		assertTrue(element_info.hasOutputDefaultValues("output2"));
		assertTrue(false == element_info.hasOutputDefaultValues("output3"));
		
		assertNotNull(element_info.getOutputDefaultValues("output1"));
		assertNotNull(element_info.getOutputDefaultValues("output2"));
		assertNull(element_info.getOutputDefaultValues("output3"));

		assertEquals(defaults1.length, element_info.getOutputDefaultValues("output1").length);
		assertEquals(defaults2.length, element_info.getOutputDefaultValues("output2").length);

		assertEquals(defaults1[0], element_info.getOutputDefaultValues("output1")[0]);
		assertEquals(defaults1[1], element_info.getOutputDefaultValues("output1")[1]);
		assertEquals(defaults2[0], element_info.getOutputDefaultValues("output2")[0]);
	}
	
	public void testAddIncookieDefaultValues()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertTrue(false == element_info.hasIncookieDefaults());
	
		element_info.addIncookie("incookie1", "one");
		element_info.addIncookie("incookie3", null);
	
		assertTrue(element_info.hasIncookieDefaults());
	
		assertTrue(element_info.hasIncookieDefaultValue("incookie1"));
		assertTrue(false == element_info.hasIncookieDefaultValue("incookie2"));
		
		assertNotNull(element_info.getIncookieDefaultValue("incookie1"));
		assertNull(element_info.getIncookieDefaultValue("incookie2"));

		assertEquals("one", element_info.getIncookieDefaultValue("incookie1"));
	}
	
	public void testAddOutcookieDefaultValues()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		assertTrue(false == element_info.hasOutcookieDefaults());
	
		element_info.addOutcookie("outcookie1", "one");
		element_info.addOutcookie("outcookie3", null);
	
		assertTrue(element_info.hasOutcookieDefaults());
	
		assertTrue(element_info.hasOutcookieDefaultValue("outcookie1"));
		assertTrue(false == element_info.hasOutcookieDefaultValue("outcookie2"));
		
		assertNotNull(element_info.getOutcookieDefaultValue("outcookie1"));
		assertNull(element_info.getOutcookieDefaultValue("outcookie2"));

		assertEquals("one", element_info.getOutcookieDefaultValue("outcookie1"));
	}
	
	public void testEmptyInitialFlowLink()
	throws EngineException
	{
		ElementInfo element_info = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		
		element_info.addExit("exit1");
		
		assertNull(element_info.getFlowLink("exit1"));
	}
	
	public void testSetFlowLink()
	throws EngineException
	{
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		
		element_info1.addExit("exit1");
		element_info1.addExit("exit2");
		
		element_info1.setFlowLink(new FlowLink("exit1", element_info2, false, false, false, true, false));
		element_info1.setFlowLink(new FlowLink("exit2", element_info2, false, true, true, false, true));
		
		assertSame(element_info1.getFlowLink("exit1").getTarget(), element_info2);
		assertEquals(element_info1.getFlowLink("exit1").cancelInheritance(), false);
		assertSame(element_info1.getFlowLink("exit2").getTarget(), element_info2);
		assertEquals(element_info1.getFlowLink("exit2").cancelInheritance(), true);
		
		Set<Map.Entry<String, FlowLink>>  exit_entries = element_info1.getExitEntries();
		assertEquals(exit_entries.size(), 2);
		Iterator<Map.Entry<String, FlowLink>> exit_entries_it = exit_entries.iterator();
		Map.Entry<String, FlowLink> exit_entry = null;
		assertTrue(exit_entries_it.hasNext());
		exit_entry = exit_entries_it.next();
		assertNotNull(exit_entry);
		assertEquals(exit_entry.getKey(), "exit1");
		assertSame(exit_entry.getValue().getTarget(), element_info2);
		assertEquals(exit_entry.getValue().cancelInheritance(), false);
		assertEquals(exit_entry.getValue().isRedirect(), true);
		assertEquals(exit_entry.getValue().cancelContinuations(), false);
		assertTrue(exit_entries_it.hasNext());
		exit_entry = exit_entries_it.next();
		assertNotNull(exit_entry);
		assertEquals(exit_entry.getKey(), "exit2");
		assertSame(exit_entry.getValue().getTarget(), element_info2);
		assertEquals(exit_entry.getValue().cancelInheritance(), true);
		assertEquals(exit_entry.getValue().isRedirect(), false);
		assertEquals(exit_entry.getValue().cancelContinuations(), true);
		assertTrue(false == exit_entries_it.hasNext());
	}
	
	public void testSetInexistantFlowLink()
	throws EngineException
	{
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		
		try
		{
			element_info1.addExit("exit1");
			element_info1.addExit("exit2");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		try
		{
			element_info2.setFlowLink(new FlowLink("exit3", element_info2, false, false, false, false, false));
		}
		catch (ExitUnknownException e)
		{
			assertEquals(e.getDeclarationName(), "element/test3.xml");
			assertEquals(e.getExitName(), "exit3");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testAddDataLink()
	throws EngineException
	{
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info3 = new ElementInfo("element/test3.xml", "text/html", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		
		element_info1.addOutput("output1", null);
		element_info1.addOutput("output2", null);
		element_info1.addOutput("output3", null);
		
		element_info2.addInput("input1", null);
		element_info2.addInput("input2", null);
		element_info2.addInput("input3", null);
		
		element_info3.addInput("input1", null);
		element_info3.addInput("input2", null);
		element_info3.addInput("input3", null);
		
		element_info1.addExit("exit1");
		element_info1.addExit("exit2");
		
		FlowLink flowlink1 = new FlowLink("exit1", element_info2, false, false, false, false, false);
		FlowLink flowlink2 = new FlowLink("exit2", null, true, false, false, false, false);
		element_info1.setFlowLink(flowlink1);
		element_info1.setFlowLink(flowlink2);

		assertTrue(false == element_info1.hasDataLink(element_info2));
		assertTrue(false == element_info1.hasDataLink(element_info3));
		assertTrue(false == element_info1.hasSnapbackDataLinks());

		element_info1.addDataLink(new DataLink("output1", element_info2, false, "input1", null));
		element_info1.addDataLink(new DataLink("output1", element_info2, false, "input2", null));
		element_info1.addDataLink(new DataLink("output1", element_info2, false, "input3", null));
		element_info1.addDataLink(new DataLink("output2", element_info2, false, "input2", flowlink2));
		element_info1.addDataLink(new DataLink("output2", element_info2, false, "input3", null));
		
		element_info1.addDataLink(new DataLink("output1", null, true, "input1", null));
		element_info1.addDataLink(new DataLink("output1", null, true, "input2", null));
		element_info1.addDataLink(new DataLink("output1", null, true, "input3", null));
		element_info1.addDataLink(new DataLink("output2", null, true, "input2", null));
		element_info1.addDataLink(new DataLink("output2", null, true, "input3", null));
		
		assertTrue(element_info1.hasDataLink(element_info2));
		assertTrue(element_info1.hasSnapbackDataLinks());
		
		Collection<String>	inputs = null;
		
		inputs = element_info1.getDataLinkInputs("output1", element_info2, false, null);
		assertNotNull(inputs);
		assertEquals(inputs.size(), 3);
		
		boolean has_input1 = false;
		boolean has_input2 = false;
		boolean has_input3 = false;
		
		for (String input : inputs)
		{
			if (input.equals("input1"))
			{
				assertFalse(has_input1);
				has_input1 = true;
			}
			else if (input.equals("input2"))
			{
				assertFalse(has_input2);
				has_input2 = true;
			}
			else if (input.equals("input3"))
			{
				assertFalse(has_input3);
				has_input3 = true;
			}
			else
			{
				fail();
			}
		}
		assertTrue(has_input1 && has_input2 && has_input3);
		
		inputs = element_info1.getDataLinkInputs("output2", element_info2, false, null);
		assertNotNull(inputs);
		assertEquals(inputs.size(), 1);
		assertEquals("input3", inputs.iterator().next());
		
		has_input1 = false;
		has_input2 = false;
		has_input3 = false;
		
		inputs = element_info1.getDataLinkInputs("output2", element_info2, false, flowlink2);
		assertNotNull(inputs);
		assertEquals(inputs.size(), 2);
		
		for (String input : inputs)
		{
			if (input.equals("input2"))
			{
				assertFalse(has_input2);
				has_input2 = true;
			}
			else if (input.equals("input3"))
			{
				assertFalse(has_input3);
				has_input3 = true;
			}
			else
			{
				fail();
			}
		}
		assertTrue(!has_input1 && has_input2 && has_input3);

		inputs = element_info1.getDataLinkInputs("output1", element_info3, false, null);
		assertNull(inputs);
			
		inputs = element_info1.getDataLinkInputs("output2", element_info3, false, null);
		assertNull(inputs);
			
		inputs = element_info1.getDataLinkInputs("output1", element_info3, true, null);
		assertNotNull(inputs);
		assertEquals(inputs.size(), 3);
		
		has_input1 = false;
		has_input2 = false;
		has_input3 = false;
		
		for (String input : inputs)
		{
			if (input.equals("input1"))
			{
				assertFalse(has_input1);
				has_input1 = true;
			}
			else if (input.equals("input2"))
			{
				assertFalse(has_input2);
				has_input2 = true;
			}
			else if (input.equals("input3"))
			{
				assertFalse(has_input3);
				has_input3 = true;
			}
			else
			{
				fail();
			}
		}
		assertTrue(has_input1 && has_input2 && has_input3);
		
		has_input1 = false;
		has_input2 = false;
		has_input3 = false;
		
		inputs = element_info1.getDataLinkInputs("output2", element_info3, true, null);
		assertNotNull(inputs);
		assertEquals(inputs.size(), 2);
		
		for (String input : inputs)
		{
			if (input.equals("input2"))
			{
				assertFalse(has_input2);
				has_input2 = true;
			}
			else if (input.equals("input3"))
			{
				assertFalse(has_input3);
				has_input3 = true;
			}
			else
			{
				fail();
			}
		}
		assertTrue(has_input2 && has_input3);
	}
	
	public void testInexistantDataLinks()
	throws EngineException
	{
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		
		element_info1.addOutput("output1", null);
		element_info1.addOutput("output2", null);
		element_info1.addOutput("output3", null);
		
		element_info2.addInput("input1", null);
		element_info2.addInput("input2", null);
		element_info2.addInput("input3", null);

		element_info1.addExit("exit");
		element_info1.setFlowLink(new FlowLink("exit", element_info2, false, false, false, false, false));
		
		assertTrue(false == element_info1.hasDataLink(element_info2));
		assertNull(element_info1.getDataLinkInputs("output3", element_info2, false, null));

		element_info1.addDataLink(new DataLink("output1", element_info2, false, "input1", null));

		assertTrue(element_info1.hasDataLink(element_info2));
		assertNull(element_info1.getDataLinkInputs("output3", element_info2, false, null));
	}
	
	public void testAddDataLinkWithoutFlowLink()
	throws EngineException
	{
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		
		element_info1.addOutput("output1", null);
		element_info2.addInput("input1", null);
		try
		{
			element_info1.addDataLink(new DataLink("output1", element_info2, false, "input1", null));
		}
		catch (FlowLinkMissingException e)
		{
			assertEquals(e.getSourceDeclarationName(), "element/test2.xml");
			assertEquals(e.getTargetDeclarationName(), "element/test3.xml");
		}
		catch (EngineException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testAddSnapbackDataLinkWithoutSnapbackFlowLink()
	throws EngineException
	{
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		
		element_info1.addOutput("output1", null);
		element_info2.addInput("input1", null);
		try
		{
			element_info1.addDataLink(new DataLink("output1", null, true, "input1", null));
		}
		catch (SnapbackFlowLinkMissingException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
		}
		catch (EngineException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testAddDataLinkErrors()
	throws EngineException
	{
		ElementInfo element_info1 = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo element_info2 = new ElementInfo("element/test3.xml", "text/html", TestElement3.class.getName(), ElementType.JAVA_CLASS);
		
		element_info1.addOutput("output1", null);
		element_info2.addInput("input1", null);
		
		try
		{
			element_info1.addDataLink(new DataLink("output2", element_info2, false, "input1", null));
		}
		catch (OutputUnknownException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getOutputName(), "output2");
		}
		catch (EngineException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		try
		{
			element_info1.addDataLink(new DataLink("output1", element_info2, false, "input2", null));
		}
		catch (InputUnknownException e)
		{
			assertEquals(e.getDeclarationName(), "element/test3.xml");
			assertEquals(e.getInputName(), "input2");
		}
		catch (EngineException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}

class TestElement2 extends Element
{
	public void processElement()
	throws EngineException
	{
		print("the content");
	}
}

class TestElement3 extends Element
{
	public void processElement()
	throws EngineException
	{
		print("other content");
	}
}

abstract class TestElementNotInstantiatable extends Element
{
	public void processElement()
	throws EngineException
	{
		print("the content");
	}
}

