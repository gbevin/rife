/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestJanino2Site.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import java.util.Collection;
import junit.framework.TestCase;

public class TestJanino2Site extends TestCase
{
	public TestJanino2Site(String name)
	{
		super(name);
	}

	public void testParser()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		SiteBuilder				builder = new SiteBuilder("janino/test_janino2site.janino", resourcefinder);
		
		Site site = builder.getSite();
		
		assertEquals(site.getUrls().size(), 6);
		assertEquals(site.getIds().size(), 9);
		
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
		assertEquals(elementinfo1.getPropertyNames().size(), 4+Rep.getProperties().size());
		assertEquals("value1", elementinfo1.getProperty("property1"));
		assertEquals("value2", elementinfo1.getProperty("property2"));
		assertEquals("value3", elementinfo1.getProperty("property3"));
		assertEquals("value4", elementinfo1.getProperty("property4"));
		
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
		assertEquals(0+Rep.getProperties().size(), subsite_elementinfo2.getPropertyNames().size());
		
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
		assertEquals(0+Rep.getProperties().size(), subsite_elementinfo3.getPropertyNames().size());
		
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
		assertSame(site.resolveId(".test_janino2elementinfo3"), elementinfo7);
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
		assertEquals(elementinfo1.getGlobalVarDefaultValues("globalvar1").length, 1);
		assertEquals(elementinfo1.getGlobalVarDefaultValues("globalvar1")[0], "default1");
		assertNotNull(elementinfo7.getInheritanceStack());
		assertEquals(2, elementinfo7.getInheritanceStack().size());
		assertEquals(elementinfo7.getInheritanceStack().get(0).getImplementation(), com.uwyn.rife.engine.testelements.inputs.Normal.class.getName());
		assertEquals(elementinfo7.getInheritanceStack().get(1).getImplementation(), com.uwyn.rife.engine.testelements.exits.SimpleSource.class.getName());
	
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

