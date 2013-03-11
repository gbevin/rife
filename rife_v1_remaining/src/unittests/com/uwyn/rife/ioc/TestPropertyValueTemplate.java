/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPropertyValueTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.ioc;

import com.uwyn.rife.ioc.exceptions.TemplateFactoryUnknownException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.exceptions.TemplateNotFoundException;
import junit.framework.TestCase;

public class TestPropertyValueTemplate extends TestCase
{
	public TestPropertyValueTemplate(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		PropertyValueTemplate object = new PropertyValueTemplate("enginehtml", "values");
		assertNotNull(object);
		assertFalse(object.isStatic());
	}
	
	public void testGetValue()
	{
		PropertyValueTemplate object = new PropertyValueTemplate("enginehtml", "values");
		assertNotNull(object.getValue());
		assertTrue(object.getValue() instanceof Template);
	}
	
	public void testGetValueUnknownFactory()
	{
		PropertyValueTemplate object = new PropertyValueTemplate("blah", "values");
		try
		{
			object.getValue();
			fail("TemplateFactoryUnknownException wasn't thrown");
		}
		catch (TemplateFactoryUnknownException e)
		{
			assertEquals("blah", e.getType());
		}
	}
	
	public void testGetValueUnknownTemplate()
	{
		PropertyValueTemplate object = new PropertyValueTemplate("enginehtml", "blahblihbloh");
		try
		{
			object.getValue();
			fail("template 'blahblihbloh' shouldn't have been found");
		}
		catch (TemplateNotFoundException e)
		{
			assertEquals("blahblihbloh", e.getName());
		}
	}
	
	public void testGetValueString()
	{
		PropertyValueTemplate object = new PropertyValueTemplate("enginehtml", "values");
		assertEquals("[!V 'VALUE1'/]<!--V VALUE2/--><r:v name=\"VALUE3\"/>\n", object.getValueString());
	}
	
	public void testToString()
	{
		PropertyValueTemplate object = new PropertyValueTemplate("enginehtml", "values");
		assertEquals("[!V 'VALUE1'/]<!--V VALUE2/--><r:v name=\"VALUE3\"/>\n", object.toString());
	}
	
	public void testIsNeglectable()
	{
		assertFalse(new PropertyValueTemplate("enginehtml", "values").isNeglectable());
	}
}
