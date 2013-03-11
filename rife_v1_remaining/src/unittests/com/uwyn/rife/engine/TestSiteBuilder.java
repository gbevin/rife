/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSiteBuilder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.ioc.PropertyValueParticipant;
import com.uwyn.rife.ioc.PropertyValueTemplate;
import com.uwyn.rife.rep.BlockingRepository;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.rep.Repository;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import java.util.Collection;
import junit.framework.TestCase;

public class TestSiteBuilder extends TestCase
{
	public TestSiteBuilder(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder builder = new SiteBuilder("", resourcefinder);

		assertNotNull(builder);
	}
	
	public void testRepProperties()
	{
		BlockingRepository rep = new BlockingRepository();
		Repository orig_rep = Rep.getDefaultRepository();
		try
		{
			Rep.setDefaultRepository(rep);
			
			rep.getProperties()
				.put("prop1", "reppropval1")
				.put("prop3", "reppropval2");
				
			ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
			SiteBuilder				builder = new SiteBuilder("test", resourcefinder);
			builder
				.enterElement("ELEMENT1")
					.setImplementation("com.uwyn.rife.engine.testelements.outputs.Normal")
					.setUrl("/test/element1")
					.addProperty("prop1", new PropertyValueObject("elementpropval"))
					.addProperty("prop2", new PropertyValueTemplate("enginehtml", "noblocks_in"))
				.leaveElement();
			
			Site site = builder.getSite();
			ElementInfo element = site.resolveId(".ELEMENT1");
			assertNotNull(element);
			assertEquals(2+1+System.getProperties().size(), element.getPropertyNames().size());
			assertEquals("elementpropval", element.getProperty("prop1"));
			assertEquals("<html>\n"+
						 "	<head>\n"+
						 "		<title>Template without blocks</title>\n"+
						 "	</head>\n"+
						 "\n"+
						 "	<body>\n"+
						 "		<h1>This is a template without blocks.</h1>\n"+
						 "	</body>\n"+
						 "</html>", ((Template)element.getProperty("prop2")).getContent());
			assertEquals("reppropval2", element.getProperty("prop3"));
		}
		finally
		{
			Rep.setDefaultRepository(orig_rep);
		}
	}
	
	public void testManualBuild()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("test_xml2site", resourcefinder);
		builder
			.setFallback("ELEMENT4")
			.addGlobalExit("globalexit1", "ELEMENT6")
			.addGlobalExit("globalexit2", "SUBSITE.ELEMENT2")
			.addGlobalVar("globalvar1", null)
			.addGlobalVar("globalvar2", null)
			.addGlobalBean("com.uwyn.rife.engine.testelements.submission.BeanImpl", "onemoreprefixgroup_", "globalbean1", "anothergroup")
			.addGlobalBean("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, null, "anothergroup")
			.addProperty("prop1", new PropertyValueObject("sitepropval"))
			.addProperty("prop6", new PropertyValueParticipant(Datasources.DEFAULT_PARTICIPANT_NAME, new PropertyValueObject("purgingunittestspgsql")))
		
			.enterSubsiteDeclaration("SUBSITE")
				.setUrlPrefix("/subsite")
				.addProperty("prop1", new PropertyValueObject("subsitepropval"))
				.addProperty("prop2", new PropertyValueTemplate("enginehtml", "noblocks_in"))
				.addProperty("prop3", new PropertyValueObject(Rep.getParticipant("configuration").getObject("EXPRESSION_CONFIG_VALUE")+" 3"))
				.addProperty("prop4", new PropertyValueParticipant(Datasources.DEFAULT_PARTICIPANT_NAME, new PropertyValueObject("unittestspgsql")))
				.addProperty("prop5", new PropertyValueObject(""+Rep.getParticipant("configuration").getObject("DATASOURCE_USER")+"\n			"+Rep.getParticipant("configuration").getObject(Config.getRepInstance().getString("IOC_CONFIG"))))
		
				.enterSubsite()
					.addGlobalExit("globalexit3", "ELEMENT3")
					.setFallback("ELEMENT2")
					.setInherits("ELEMENT4")
					.addGlobalVar("globalvar3", null)
					.addDeparture("ELEMENT3")
		
					.enterElement("ELEMENT2")
						.setImplementation("com.uwyn.rife.engine.testelements.outputs.Normal")
						.setUrl("/test/element2")
						.addInput("input1", null)
						.addInput("input2", null)
						.addOutput("output1", null)
						.addExit("exit1")
					.leaveElement()
		
					.enterElement("ELEMENT3")
						.setImplementation("com.uwyn.rife.engine.testelements.inputs.Normal")
						.setUrl("/test/element3")
					.leaveElement()
		
					.enterSubsiteDeclaration("SUBSITE2")
						.setUrlPrefix("subsite2")
						.enterSubsite()
							.enterElement("ELEMENT8")
								.setImplementation("com.uwyn.rife.engine.testelements.inputs.Normal")
								.setUrl("test/element8")
							.leaveElement()
						.leaveSubsite()
					.leaveSubsiteDeclaration()
				.leaveSubsite()
			.leaveSubsiteDeclaration()
		
			.enterGroup()
				.setInherits("ELEMENT5")
				.addGlobalVar("globalvar4", null)
		
				.enterElement("ELEMENT6")
					.setImplementation("com.uwyn.rife.engine.testelements.outputs.Normal")
					.setUrl("/test/element6")
					.setInherits("ELEMENT3")
					.addInput("input1", null)
					.addInput("input2", null)
					.addOutput("output1", null)
					.addExit("exit1")
				.leaveElement()
		
				.enterElement("ELEMENT7")
					.setImplementation("com.uwyn.rife.engine.testelements.inputs.Normal")
					.setUrl("/test/element7")
				.leaveElement()
			.leaveGroup()
		
			.enterElement("ELEMENT1")
				.setImplementation("com.uwyn.rife.engine.testelements.engine.Simple")
				.setUrl("/test/element1")
				.addDataLink("output1", "ELEMENT2", "input1")
				.addDataLink("output1", "ELEMENT2", "input2")
				.addFlowLink("exit1", "ELEMENT2", false)
				.enterFlowLink("exit3")
					.destId("ELEMENT2")
					.addDataLink("output3", "input1")
				.leaveFlowLink()
				.enterFlowLink("exit4")
					.destId("ELEMENT2")
					.addDataLink("output4", "input1")
				.leaveFlowLink()
				.addSnapbackFlowLink("exit2", false)
				.addSnapbackDataLink("output2", "input2")
				.addStaticProperty("property1", "value1")
				.addStaticProperty("property2", "value2")
				.addStaticProperty("property3", "value3")
				.addStaticProperty("property4", "value4")
				.addInput("input1", null)
				.addInput("input2", null)
				.addInput("input3", null)
				.addOutput("output1", null)
				/* .addOutput("output2", null) */ // auto-added by datalink
				.addIncookie("incookie1", null)
				.addIncookie("incookie2", null)
				.addOutcookie("outcookie1", null)
				.addOutcookie("outcookie2", null)
				.addOutcookie("outcookie3", null)
				.addOutcookie("outcookie4", null)
				.addInBean("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, "inbean1", null)
				.addInBean("com.uwyn.rife.engine.testelements.exits.BeanImpl2", null, null, null)
				.addInBean("com.uwyn.rife.engine.testelements.exits.BeanImpl1", "prefix_", null, null)
				.addInBean("com.uwyn.rife.engine.testelements.submission.BeanImpl", "prefixgroup_", "inbean2", "somegroup")
				.addInBean("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, null, "somegroup")
				.addOutBean("com.uwyn.rife.engine.testelements.exits.BeanImpl1", null, null, null)
				.addOutBean("com.uwyn.rife.engine.testelements.exits.BeanImpl2", null, null, null)
				.addOutBean("com.uwyn.rife.engine.testelements.exits.BeanImpl2", "prefix_", "outbean1", null)
				.addOutBean("com.uwyn.rife.engine.testelements.submission.BeanImpl", "prefixgroup_", null, "somegroup")
				.addOutBean("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, "outbean2", "somegroup")
				.addChildTrigger("input1")
				.addChildTrigger("input2")
				/* .addExit("exit1") */ // auto-added by flowlink
				.addExit("exit2")
				.addExit("exit3")
				.addExit("exit4")
				.enterSubmission("submission1")
					.addParameter("param1", null)
					.addParameter("param2", null)
					.addParameter("param3", null)
					.addParameterRegexp("paramA(\\d+)")
					.addParameterRegexp("paramB(\\d+)")
					.addFile("file1")
					.addFile("file2")
				.leaveSubmission()
				.enterSubmission("submission2")
					.addParameter("param1", null)
					.addParameter("param2", null)
					.addParameterRegexp("paramC(\\d+)")
					.addFile("file1")
				.leaveSubmission()
				.addProperty("prop1", new PropertyValueObject("elementpropval"))
				.addProperty("prop7", new PropertyValueParticipant(Datasources.DEFAULT_PARTICIPANT_NAME, new PropertyValueObject("sessiondurationunittestspgsql")))
			.leaveElement()
		
			.addProperty("prop2", new PropertyValueTemplate("enginehtml", "unsetvalues_output_in"))
		
			.enterElement("ELEMENT2")
				.setImplementation("com.uwyn.rife.engine.testelements.outputs.Normal")
				.setUrl("/test/element2")
				.setInherits("ELEMENT3")
				.addInput("input1", null)
				.addInput("input2", null)
				.addOutput("output1", null)
				.addExit("exit1")
			.leaveElement()
		
			.enterElement("ELEMENT3")
				.setImplementation("com.uwyn.rife.engine.testelements.inputs.Normal")
			.leaveElement()
		
			.enterElement("ELEMENT4")
				.setImplementation("com.uwyn.rife.engine.testelements.submission.Normal")
			.leaveElement()
		
			.enterElement("ELEMENT5")
				.setImplementation("com.uwyn.rife.engine.testelements.exits.SimpleSource")
			.leaveElement();
			
		Site site = builder.getSite();
		
		assertEquals(site.getUrls().size(), 7);
		assertEquals(site.getIds().size(), 10);
		
		assertEquals("com.uwyn.rife.engine.testelements.engine.Simple", site.resolveUrl("/test/element1", null). getImplementation());
		assertNull(site.resolveUrl("/test/element1/path/info", null));
		assertNull(site.resolveUrl("/test/element1_notthere", null));
		assertEquals("com.uwyn.rife.engine.testelements.submission.Normal", site.searchFallback("").getImplementation());
		assertEquals("com.uwyn.rife.engine.testelements.submission.Normal", site.searchFallback("/hskjhjdz").getImplementation());
		assertEquals("com.uwyn.rife.engine.testelements.outputs.Normal", site.searchFallback("/subsite").getImplementation());
		assertEquals("com.uwyn.rife.engine.testelements.outputs.Normal", site.searchFallback("/subsitedfzefze").getImplementation());
		assertEquals("com.uwyn.rife.engine.testelements.outputs.Normal", site.searchFallback("/subsite/kjdkfj").getImplementation());

		Collection<String>	global_exits = null;
		Collection<String>	global_vars = null;

		ElementInfo elementinfo1 = site.resolveUrl("/test/element1", null);
		assertNotNull(elementinfo1);
		assertSame(site.resolveId(".ELEMENT1"), elementinfo1);
		assertEquals(elementinfo1.getImplementation(), com.uwyn.rife.engine.testelements.engine.Simple.class.getName());
		assertTrue(elementinfo1.hasGlobalExits());
		global_exits = elementinfo1.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 2);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(elementinfo1.hasGlobalVars());
		global_vars = elementinfo1.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 8);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(elementinfo1.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo1.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo1.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo1.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNull(elementinfo1.getInheritanceStack());
		assertEquals(8+Rep.getProperties().size(), elementinfo1.getPropertyNames().size());
		assertEquals("elementpropval", elementinfo1.getProperty("prop1"));
		assertTrue(elementinfo1.getProperty("prop2") instanceof Template);
		assertEquals(((Template)elementinfo1.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("unsetvalues_output_in").getContent());
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), elementinfo1.getProperty("prop6"));
		assertSame(Datasources.getRepInstance().getDatasource("sessiondurationunittestspgsql"), elementinfo1.getProperty("prop7"));
		
		ElementInfo elementinfo2 = site.resolveUrl("/test/element2", null);
		assertNotNull(elementinfo2);
		assertSame(site.resolveId(".ELEMENT2"), elementinfo2);
		assertEquals(elementinfo2.getImplementation(), com.uwyn.rife.engine.testelements.outputs.Normal.class.getName());
		assertTrue(elementinfo2.hasGlobalExits());
		global_exits = elementinfo2.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 2);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(elementinfo2.hasGlobalVars());
		global_vars = elementinfo2.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 8);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(elementinfo2.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo2.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo2.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo2.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNotNull(elementinfo2.getInheritanceStack());
		assertEquals(2, elementinfo2.getInheritanceStack().size());
		assertEquals(elementinfo2.getInheritanceStack().get(0).getImplementation(), com.uwyn.rife.engine.testelements.outputs.Normal.class.getName());
		assertEquals(elementinfo2.getInheritanceStack().get(1).getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertEquals(3+Rep.getProperties().size(), elementinfo2.getPropertyNames().size());
		assertEquals("sitepropval", elementinfo2.getProperty("prop1"));
		assertTrue(elementinfo2.getProperty("prop2") instanceof Template);
		assertEquals(((Template)elementinfo2.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("unsetvalues_output_in").getContent());
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), elementinfo2.getProperty("prop6"));
		
		ElementInfo elementinfo3 = site.resolveId(".ELEMENT3");
		assertNotNull(elementinfo3);
		assertEquals(elementinfo3.getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertTrue(elementinfo3.hasGlobalExits());
		global_exits = elementinfo3.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 2);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(elementinfo3.hasGlobalVars());
		global_vars = elementinfo3.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 8);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(elementinfo3.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo3.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo3.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo3.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNull(elementinfo3.getInheritanceStack());
		assertEquals(3+Rep.getProperties().size(), elementinfo3.getPropertyNames().size());
		assertEquals("sitepropval", elementinfo3.getProperty("prop1"));
		assertTrue(elementinfo3.getProperty("prop2") instanceof Template);
		assertEquals(((Template)elementinfo3.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("unsetvalues_output_in").getContent());
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), elementinfo3.getProperty("prop6"));
		
		ElementInfo elementinfo4 = site.resolveId(".ELEMENT4");
		assertNotNull(elementinfo4);
		assertEquals(elementinfo4.getImplementation(), com.uwyn.rife.engine.testelements.submission.Normal.class.getName());
		assertTrue(elementinfo4.hasGlobalExits());
		global_exits = elementinfo4.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 2);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(elementinfo4.hasGlobalVars());
		global_vars = elementinfo4.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 8);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(elementinfo4.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo4.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo4.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo4.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNull(elementinfo4.getInheritanceStack());
		assertEquals(3+Rep.getProperties().size(), elementinfo4.getPropertyNames().size());
		assertEquals("sitepropval", elementinfo4.getProperty("prop1"));
		assertTrue(elementinfo4.getProperty("prop2") instanceof Template);
		assertEquals(((Template)elementinfo4.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("unsetvalues_output_in").getContent());
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), elementinfo4.getProperty("prop6"));
		
		ElementInfo subsite_elementinfo2 = site.resolveUrl("/subsite/test/element2", null);
		assertNotNull(subsite_elementinfo2);
		assertSame(site.resolveId(".SUBSITE.ELEMENT2"), subsite_elementinfo2);
		assertEquals(subsite_elementinfo2.getImplementation(), com.uwyn.rife.engine.testelements.outputs.Normal.class.getName());
		assertTrue(subsite_elementinfo2.hasGlobalExits());
		global_exits = subsite_elementinfo2.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 3);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(global_exits.contains("globalexit3"));
		assertTrue(subsite_elementinfo2.hasGlobalVars());
		global_vars = subsite_elementinfo2.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 9);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("globalvar3"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(subsite_elementinfo2.getNamedGlobalBeanNames().size(), 1);
		assertEquals(subsite_elementinfo2.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(subsite_elementinfo2.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(subsite_elementinfo2.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNotNull(subsite_elementinfo2.getInheritanceStack());
		assertEquals(2, subsite_elementinfo2.getInheritanceStack().size());
		assertEquals(subsite_elementinfo2.getInheritanceStack().get(0).getImplementation(), com.uwyn.rife.engine.testelements.outputs.Normal.class.getName());
		assertEquals(subsite_elementinfo2.getInheritanceStack().get(1).getImplementation(), com.uwyn.rife.engine.testelements.submission.Normal.class.getName());
		assertEquals(6+Rep.getProperties().size(), subsite_elementinfo2.getPropertyNames().size());
		assertEquals("subsitepropval", subsite_elementinfo2.getProperty("prop1"));
		assertTrue(subsite_elementinfo2.getProperty("prop2") instanceof Template);
		assertEquals(((Template)subsite_elementinfo2.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("noblocks_in").getContent());
		assertEquals("the value 3", subsite_elementinfo2.getProperty("prop3"));
		assertSame(Datasources.getRepInstance().getDatasource("unittestspgsql"), subsite_elementinfo2.getProperty("prop4"));
		assertEquals("unittests\n			pgsql", subsite_elementinfo2.getProperty("prop5"));
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), subsite_elementinfo2.getProperty("prop6"));
		
		ElementInfo subsite_elementinfo3 = site.resolveUrl("/subsite/test/element3", null);
		assertNotNull(subsite_elementinfo3);
		assertSame(site.resolveId(".SUBSITE.ELEMENT3"), subsite_elementinfo3);
		assertEquals(subsite_elementinfo3.getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertTrue(subsite_elementinfo3.hasGlobalExits());
		global_exits = subsite_elementinfo3.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 3);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(global_exits.contains("globalexit3"));
		assertTrue(subsite_elementinfo3.hasGlobalVars());
		global_vars = subsite_elementinfo3.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 9);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("globalvar3"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(subsite_elementinfo3.getNamedGlobalBeanNames().size(), 1);
		assertEquals(subsite_elementinfo3.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(subsite_elementinfo3.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(subsite_elementinfo3.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNotNull(subsite_elementinfo3.getInheritanceStack());
		assertEquals(2, subsite_elementinfo3.getInheritanceStack().size());
		assertEquals(subsite_elementinfo3.getInheritanceStack().get(0).getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertEquals(subsite_elementinfo3.getInheritanceStack().get(1).getImplementation(), com.uwyn.rife.engine.testelements.submission.Normal.class.getName());
		assertTrue(subsite_elementinfo3.hasDepartureVars());
		assertEquals(1, subsite_elementinfo3.getDepartureVars().size());
		assertEquals("globalvar3", subsite_elementinfo3.getDepartureVars().get(0));
		assertEquals(6+Rep.getProperties().size(), subsite_elementinfo3.getPropertyNames().size());
		assertEquals("subsitepropval", subsite_elementinfo3.getProperty("prop1"));
		assertTrue(subsite_elementinfo3.getProperty("prop2") instanceof Template);
		assertEquals(((Template)subsite_elementinfo3.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("noblocks_in").getContent());
		assertEquals("the value 3", subsite_elementinfo3.getProperty("prop3"));
		assertSame(Datasources.getRepInstance().getDatasource("unittestspgsql"), subsite_elementinfo3.getProperty("prop4"));
		assertEquals("unittests\n			pgsql", subsite_elementinfo3.getProperty("prop5"));
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), subsite_elementinfo3.getProperty("prop6"));
		
		ElementInfo elementinfo5 = site.resolveId(".ELEMENT5");
		assertNotNull(elementinfo5);
		assertEquals(elementinfo5.getImplementation(), com.uwyn.rife.engine.testelements.exits.SimpleSource.class.getName());
		assertTrue(elementinfo5.hasGlobalExits());
		global_exits = elementinfo5.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 2);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(elementinfo5.hasGlobalVars());
		global_vars = elementinfo5.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 8);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertNull(elementinfo5.getInheritanceStack());
		assertEquals(3+Rep.getProperties().size(), elementinfo5.getPropertyNames().size());
		assertEquals("sitepropval", elementinfo5.getProperty("prop1"));
		assertTrue(elementinfo5.getProperty("prop2") instanceof Template);
		assertEquals(((Template)elementinfo5.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("unsetvalues_output_in").getContent());
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), elementinfo5.getProperty("prop6"));
		
		ElementInfo elementinfo6 = site.resolveUrl("/test/element6", null);
		assertNotNull(elementinfo6);
		assertSame(site.resolveId(".ELEMENT6"), elementinfo6);
		assertEquals(elementinfo6.getImplementation(), com.uwyn.rife.engine.testelements.outputs.Normal.class.getName());
		assertTrue(elementinfo6.hasGlobalExits());
		global_exits = elementinfo6.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 2);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(elementinfo6.hasGlobalVars());
		global_vars = elementinfo6.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 9);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("globalvar4"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(elementinfo6.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo6.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo6.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo6.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNotNull(elementinfo6.getInheritanceStack());
		assertEquals(3, elementinfo6.getInheritanceStack().size());
		assertEquals(elementinfo6.getInheritanceStack().get(0).getImplementation(), com.uwyn.rife.engine.testelements.outputs.Normal.class.getName());
		assertEquals(elementinfo6.getInheritanceStack().get(1).getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertEquals(elementinfo6.getInheritanceStack().get(2).getImplementation(), com.uwyn.rife.engine.testelements.exits.SimpleSource.class.getName());
		assertEquals(3+Rep.getProperties().size(), elementinfo6.getPropertyNames().size());
		assertEquals("sitepropval", elementinfo6.getProperty("prop1"));
		assertTrue(elementinfo6.getProperty("prop2") instanceof Template);
		assertEquals(((Template)elementinfo6.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("unsetvalues_output_in").getContent());
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), elementinfo6.getProperty("prop6"));
		
		ElementInfo elementinfo7 = site.resolveUrl("/test/element7", null);
		assertNotNull(elementinfo7);
		assertSame(site.resolveId(".ELEMENT7"), elementinfo7);
		assertEquals(elementinfo7.getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertTrue(elementinfo7.hasGlobalExits());
		global_exits = elementinfo7.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 2);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(elementinfo7.hasGlobalVars());
		global_vars = elementinfo7.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 9);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("globalvar4"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(elementinfo7.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo7.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo7.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo7.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNotNull(elementinfo7.getInheritanceStack());
		assertEquals(2, elementinfo7.getInheritanceStack().size());
		assertEquals(elementinfo7.getInheritanceStack().get(0).getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertEquals(elementinfo7.getInheritanceStack().get(1).getImplementation(), com.uwyn.rife.engine.testelements.exits.SimpleSource.class.getName());
		assertEquals(3+Rep.getProperties().size(), elementinfo7.getPropertyNames().size());
		assertEquals("sitepropval", elementinfo7.getProperty("prop1"));
		assertTrue(elementinfo7.getProperty("prop2") instanceof Template);
		assertEquals(((Template)elementinfo7.getProperty("prop2")).getContent(), TemplateFactory.ENGINEHTML.get("unsetvalues_output_in").getContent());
		assertSame(Datasources.getRepInstance().getDatasource("purgingunittestspgsql"), elementinfo7.getProperty("prop6"));
		
		ElementInfo elementinfo8 = site.resolveUrl("/subsite/subsite2/test/element8", null);
		assertNotNull(elementinfo8);
		assertSame(site.resolveId(".SUBSITE.SUBSITE2.ELEMENT8"), elementinfo8);
		assertEquals(elementinfo8.getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertTrue(elementinfo8.hasGlobalExits());
		global_exits = elementinfo8.getGlobalExitNames();
		assertNotNull(global_exits);
		assertEquals(global_exits.size(), 3);
		assertTrue(global_exits.contains("globalexit1"));
		assertTrue(global_exits.contains("globalexit2"));
		assertTrue(global_exits.contains("globalexit3"));
		assertTrue(elementinfo8.hasGlobalVars());
		global_vars = elementinfo8.getGlobalVarNames();
		assertNotNull(global_vars);
		assertEquals(global_vars.size(), 9);
		assertTrue(global_vars.contains("globalvar1"));
		assertTrue(global_vars.contains("globalvar2"));
		assertTrue(global_vars.contains("globalvar3"));
		assertTrue(global_vars.contains("onemoreprefixgroup_double"));
		assertTrue(global_vars.contains("onemoreprefixgroup_long"));
		assertTrue(global_vars.contains("onemoreprefixgroup_shortObject"));
		assertTrue(global_vars.contains("double"));
		assertTrue(global_vars.contains("long"));
		assertTrue(global_vars.contains("shortObject"));
		assertEquals(elementinfo7.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo7.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo7.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo7.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNotNull(elementinfo8.getInheritanceStack());
		assertEquals(2, elementinfo8.getInheritanceStack().size());
		assertEquals(elementinfo8.getInheritanceStack().get(0).getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertEquals(elementinfo8.getInheritanceStack().get(1).getImplementation(), com.uwyn.rife.engine.testelements.submission.Normal.class.getName());
	
		assertSame(elementinfo1.getFlowLink("exit1").getTarget(), elementinfo2);
		assertEquals(elementinfo1.getFlowLink("exit1").isSnapback(), false);
		assertNull(elementinfo1.getFlowLink("exit2").getTarget());
		assertEquals(elementinfo1.getFlowLink("exit2").isSnapback(), true);
		
		Collection<String>	inputs = null;
		
		inputs = elementinfo1.getDataLinkInputs("output1", elementinfo2, false, null);
		assertNotNull(inputs);
		assertEquals(inputs.size(), 2);
		assertTrue(inputs.contains("input1"));
		assertTrue(inputs.contains("input2"));
		
		inputs = elementinfo1.getDataLinkInputs("output1", elementinfo3, false, null);
		assertNull(inputs);
			
		inputs = elementinfo1.getDataLinkInputs("output1", elementinfo3, true, null);
		assertNull(inputs);
	
		inputs = elementinfo1.getDataLinkInputs("output2", elementinfo2, false, null);
		assertNull(inputs);
		
		inputs = elementinfo1.getDataLinkInputs("output2", elementinfo3, false, null);
		assertNull(inputs);
		
		inputs = elementinfo1.getDataLinkInputs("output2", elementinfo3, true, null);
		assertNotNull(inputs);
		assertEquals(inputs.size(), 1);
		assertTrue(inputs.contains("input2"));
		
		inputs = elementinfo1.getDataLinkInputs("output3", elementinfo2, false, elementinfo1.getFlowLink("exit3"));
		assertNotNull(inputs);
		assertEquals(inputs.size(), 1);
		assertTrue(inputs.contains("input1"));
		
		inputs = elementinfo1.getDataLinkInputs("output3", elementinfo2, false, null);
		assertNull(inputs);
		
		inputs = elementinfo1.getDataLinkInputs("output4", elementinfo2, false, elementinfo1.getFlowLink("exit4"));
		assertNotNull(inputs);
		assertEquals(inputs.size(), 1);
		assertTrue(inputs.contains("input1"));
		
		inputs = elementinfo1.getDataLinkInputs("output4", elementinfo2, false, null);
		assertNull(inputs);
	}
}

