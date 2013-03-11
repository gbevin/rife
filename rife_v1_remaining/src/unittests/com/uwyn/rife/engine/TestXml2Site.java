/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXml2Site.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;

import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import java.util.Collection;
import junit.framework.TestCase;

public class TestXml2Site extends TestCase
{
	public TestXml2Site(String name)
	{
		super(name);
	}

	public void testParser()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("xml/test_xml2site.xml", resourcefinder);

		Site site = builder.getSite();

		assertEquals(site.getUrls().size(), 6);
		assertEquals(site.getIds().size(), 9);

		assertEquals("com.uwyn.rife.engine.testelements.engine.Simple", site.resolveUrl("/test/element1", null).getImplementation());
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
		assertEquals(11+Rep.getProperties().size(), elementinfo1.getPropertyNames().size());
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
		assertEquals("elementpropval", subsite_elementinfo2.getProperty("prop1"));
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
		assertEquals(elementinfo5.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo5.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo5.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo5.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
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
		assertSame(site.resolveId(".test_xml2elementinfo3"), elementinfo7);
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

	public void testElementsInlinedParser()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("xml/test_xml2site_inlinedelements.xml", resourcefinder);

		Site site = builder.getSite();

		assertEquals(site.getUrls().size(), 6);
		assertEquals(site.getIds().size(), 9);

		assertEquals("com.uwyn.rife.engine.testelements.engine.Simple", site.resolveUrl("/test/element1", null).getImplementation());
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
		assertEquals(elementinfo1.getPropertyNames().size(), 5+Rep.getProperties().size());
		assertTrue(elementinfo1.containsProperty("property1"));
		assertTrue(elementinfo1.containsProperty("property2"));
		assertTrue(elementinfo1.containsProperty("property3"));
		assertTrue(elementinfo1.containsProperty("property4"));
		assertEquals("value1", elementinfo1.getProperty("property1"));
		assertTrue(elementinfo1.getProperty("property2") instanceof Template);
		assertEquals(((Template)elementinfo1.getProperty("property2")).getContent(), TemplateFactory.ENGINEHTML.get("noblocks_in").getContent());
		assertEquals("the value 3", elementinfo1.getProperty("property3"));
		assertSame(Datasources.getRepInstance().getDatasource("unittestspgsql"), elementinfo1.getProperty("property4"));
		assertEquals("unittests\n			pgsql", elementinfo1.getProperty("property5"));

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
		assertEquals(0+Rep.getProperties().size(), elementinfo2.getPropertyNames().size());

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
		assertEquals(0+Rep.getProperties().size(), elementinfo3.getPropertyNames().size());

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
		assertEquals(0+Rep.getProperties().size(), elementinfo4.getPropertyNames().size());

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
		assertEquals(1+Rep.getProperties().size(), subsite_elementinfo2.getPropertyNames().size());
		assertEquals("elementpropval", subsite_elementinfo2.getProperty("prop1"));

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
		assertEquals(1+Rep.getProperties().size(), subsite_elementinfo3.getPropertyNames().size());
		assertEquals("subsitepropval", subsite_elementinfo3.getProperty("prop1"));

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
		assertEquals(elementinfo5.getNamedGlobalBeanNames().size(), 1);
		assertEquals(elementinfo5.getNamedGlobalBeanInfo("globalbean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo5.getNamedGlobalBeanInfo("globalbean1").getPrefix(), "onemoreprefixgroup_");
		assertEquals(elementinfo5.getNamedGlobalBeanInfo("globalbean1").getGroupName(), "anothergroup");
		assertNull(elementinfo5.getInheritanceStack());
		assertEquals(0+Rep.getProperties().size(), elementinfo5.getPropertyNames().size());

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
		assertEquals(0+Rep.getProperties().size(), elementinfo6.getPropertyNames().size());

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
		assertEquals(0+Rep.getProperties().size(), elementinfo7.getPropertyNames().size());

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
	}

	public void testCircularSubsites()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/circularsubite_parent.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (ProcessingErrorException e)
		{
			assertTrue(true);
		}
	}

	public void testGlobalConflictInput()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/global_conflict_input.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (EngineException e)
		{
			assertTrue(true);
		}
	}

	public void testGlobalConflictOutput()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/global_conflict_input.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (EngineException e)
		{
			assertTrue(true);
		}
	}

	public void testFlawedElementUrl()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_element_url.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (ElementUrlInvalidException e)
		{
			assertEquals(e.getId(), "test_xml2elementinfo1");
			assertEquals(e.getUrl(), "/cant/end/with/slash/");
		}
	}

	public void testFlawedElementUrl2()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_element_url2.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (ElementUrlInvalidException e)
		{
			assertEquals(e.getId(), "test_xml2elementinfo1");
			assertEquals(e.getUrl(), "");
		}
	}

	public void testFlawedDataLinkDestid()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_destid.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (ElementIdNotFoundInSiteException e)
		{
			assertEquals(e.getId(), ".ELEMENT32");
			assertEquals(e.getSiteId(), ".");
		}
	}

	public void testFlawedDataLinkDestinput()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_destinput.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (OutputUnknownException e)
		{
			assertEquals(e.getDeclarationName(), "xml/test_xml2elementinfo1.xml");
			assertEquals(e.getOutputName(), "Output1");
		}
	}

	public void testFlawedDataLinkSrcoutput()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_srcoutput.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (OutputUnknownException e)
		{
			assertEquals(e.getDeclarationName(), "xml/test_xml2elementinfo1.xml");
			assertEquals(e.getOutputName(), "Output14");
		}
	}

	public void testFlawedDataLinkTargetMissing()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_target_missing.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkTargetRequiredException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_target_missing.xml");
			assertEquals(e.getOutputName(), "Output1");
			assertEquals(e.getElementId(), "ELEMENT1");
		}
	}

	public void testFlawedDataLinkAmbigousTarget()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_ambigous_target.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkAmbiguousTargetException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_ambigous_target.xml");
			assertEquals(e.getOutputName(), "Output1");
			assertEquals(e.getElementId(), "ELEMENT1");
		}
	}

	public void testFlawedDataLinkOutputMissing()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_output_missing.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkOutputRequiredException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_output_missing.xml");
			assertEquals(e.getElementId(), "ELEMENT1");
		}
	}

	public void testFlawedDataLinkAmbigousOutput()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_ambigous_output.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkAmbiguousOutputException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_ambigous_output.xml");
			assertEquals(e.getElementId(), "ELEMENT1");
		}
	}

	public void testFlawedDataLinkInputMissing()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_input_missing.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkInputRequiredException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_input_missing.xml");
			assertEquals(e.getElementId(), "ELEMENT1");
			assertEquals(e.getDestinationId(), "ELEMENT2");
			assertFalse(e.getSnapback());
		}
	}

	public void testFlawedDataLinkAmbigousInput()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_ambigous_input.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkAmbiguousInputException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_ambigous_input.xml");
			assertEquals(e.getElementId(), "ELEMENT1");
			assertEquals(e.getDestinationId(), null);
			assertTrue(e.getSnapback());
		}
	}

	public void testFlawedDataLinkIncompatibleInputOutput()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_incompatible_inputoutput1.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkIncompatibleInputOutputException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_incompatible_inputoutput1.xml");
			assertEquals(e.getElementId(), "ELEMENT1");
			assertEquals(e.getDestinationId(), null);
			assertTrue(e.getSnapback());
		}

		builder = new SiteBuilder("site/flawed_datalink_incompatible_inputoutput2.xml", resourcefinder);
		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkIncompatibleInputOutputException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_incompatible_inputoutput2.xml");
			assertEquals(e.getElementId(), "ELEMENT1");
			assertEquals(e.getDestinationId(), null);
			assertTrue(e.getSnapback());
		}
	}

	public void testFlawedDataLinkUnknownSrcOutbean()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_unknown_outbean.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkUnknownSrcOutbeanException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_unknown_outbean.xml");
			assertEquals(e.getSrcOutbean(), "unknownoutbean1");
			assertEquals(e.getElementId(), "ELEMENT1");
			assertEquals(e.getDestinationId(), ".ELEMENT2");
			assertFalse(e.getSnapback());
		}
	}

	public void testFlawedDataLinkUnknownDestInbean()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_datalink_unknown_inbean.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (DataLinkUnknownDestInbeanException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_datalink_unknown_inbean.xml");
			assertEquals(e.getDestInbean(), "unknowninbean1");
			assertEquals(e.getElementId(), "ELEMENT1");
			assertEquals(e.getDestinationId(), ".ELEMENT2");
			assertFalse(e.getSnapback());
		}
	}

	public void testFlawedFlowLinkDestidInvalid()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_flowlink_destid_invalid.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (ElementIdNotFoundInSiteException e)
		{
			assertEquals(e.getId(), ".ELEMENT22");
			assertEquals(e.getSiteId(), ".");
		}
	}

	public void testFlawedFlowLinkTargetMissing()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_flowlink_target_missing.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (FlowLinkTargetRequiredException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_flowlink_target_missing.xml");
			assertEquals(e.getExitName(), "Exit1");
			assertEquals(e.getElementId(), "ELEMENT1");
		}
	}

	public void testFlawedFlowLinkAmbigousTarget()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_flowlink_ambigous_target.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (FlowLinkAmbiguousTargetException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_flowlink_ambigous_target.xml");
			assertEquals(e.getExitName(), "Exit1");
			assertEquals(e.getElementId(), "ELEMENT1");
		}
	}

	public void testFlawedFlowLinkSrcexit()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_flowlink_srcexit.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (ExitUnknownException e)
		{
			assertEquals(e.getDeclarationName(), "xml/test_xml2elementinfo1.xml");
			assertEquals(e.getExitName(), "Exit19");
		}
	}
	
	public void testFlawedFlowLinkSpecificDataLinkDestid()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_flowlink_specific_datalink_destid.xml", resourcefinder);
		
		try
		{
			builder.getSite();
			fail();
		}
		catch (FlowLinkSpecificDataLinkDestIdSpecifiedException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_flowlink_specific_datalink_destid.xml");
			assertEquals(e.getExitName(), "Exit1");
			assertEquals(e.getElementId(), "ELEMENT1");
		}
	}
	
	public void testFlawedGlobalExitAmbigousTarget1()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_globalexit_ambigous_target1.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (GlobalExitAmbiguousTargetException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_globalexit_ambigous_target1.xml");
			assertEquals(e.getGlobalExitName(), "exit1");
		}
	}

	public void testFlawedGlobalExitAmbigousTarget2()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_globalexit_ambigous_target2.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (GlobalExitAmbiguousTargetException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_globalexit_ambigous_target2.xml");
			assertEquals(e.getGlobalExitName(), "exit2");
		}
	}

	public void testFlawedGlobalExitAmbigousTarget3()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_globalexit_ambigous_target3.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (GlobalExitAmbiguousTargetException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_globalexit_ambigous_target3.xml");
			assertEquals(e.getGlobalExitName(), "exit3");
		}
	}

	public void testFlawedGlobalExitTargetMissing()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_globalexit_target_missing.xml", resourcefinder);

		try
		{
			builder.getSite();
			fail();
		}
		catch (GlobalExitTargetRequiredException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_globalexit_target_missing.xml");
			assertEquals(e.getGlobalExitName(), "exit1");
		}
	}

	public void testMissingElementImplementation()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("test_site", resourcefinder);
		builder
			.enterElement("not_found_declaration_name")
			.leaveElement();

		try
		{
			builder.getSite();
			fail();
		}
		catch (MissingImplementationException e)
		{
			assertEquals("not_found_declaration_name", e.getDeclarationName());
		}
	}
	
	public void testFlawedUnknownInbeanGroup()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_unknown_inbean_group.xml", resourcefinder);
		
		try
		{
			builder.getSite();
			fail();
		}
		catch (InbeanGroupNotFoundException e)
		{
			assertEquals(e.getDeclarationName(), "xml/test_xml2elementinfo1.xml");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
			assertEquals(e.getGroupName(), "unknowngroup");
		}
	}
	
	public void testFlawedUnknownOutbeanGroup()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_unknown_outbean_group.xml", resourcefinder);
		
		try
		{
			builder.getSite();
			fail();
		}
		catch (OutbeanGroupNotFoundException e)
		{
			assertEquals(e.getDeclarationName(), "xml/test_xml2elementinfo1.xml");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
			assertEquals(e.getGroupName(), "unknowngroup");
		}
	}
	
	public void testFlawedUnknownGlobalbeanGroup()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_unknown_globalbean_group.xml", resourcefinder);
		
		try
		{
			builder.getSite();
			fail();
		}
		catch (GlobalBeanGroupNotFoundException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_unknown_globalbean_group.xml");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
			assertEquals(e.getGroupName(), "unknowngroup");
		}
	}
	
	public void testFlawedNonValidationInbeanGroup()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_nonvalidation_inbean_group.xml", resourcefinder);
		
		try
		{
			builder.getSite();
			fail();
		}
		catch (InbeanGroupRequiresValidatedConstrainedException e)
		{
			assertEquals(e.getDeclarationName(), "xml/test_xml2elementinfo1.xml");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.exits.BeanImpl1");
			assertEquals(e.getGroupName(), "somegroup");
		}
	}
	
	public void testFlawedNonValidationOutbeanGroup()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_nonvalidation_outbean_group.xml", resourcefinder);
		
		try
		{
			builder.getSite();
			fail();
		}
		catch (OutbeanGroupRequiresValidatedConstrainedException e)
		{
			assertEquals(e.getDeclarationName(), "xml/test_xml2elementinfo1.xml");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.exits.BeanImpl1");
			assertEquals(e.getGroupName(), "somegroup");
		}
	}
	
	public void testFlawedNonValidationGlobalbeanGroup()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("site/flawed_nonvalidation_globalbean_group.xml", resourcefinder);
		
		try
		{
			builder.getSite();
			fail();
		}
		catch (GlobalBeanGroupRequiresValidatedConstrainedException e)
		{
			assertEquals(e.getSiteDeclarationName(), "site/flawed_nonvalidation_globalbean_group.xml");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.exits.BeanImpl1");
			assertEquals(e.getGroupName(), "somegroup");
		}
	}
}

