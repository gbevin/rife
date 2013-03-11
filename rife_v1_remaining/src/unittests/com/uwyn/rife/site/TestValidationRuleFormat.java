/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationRuleFormat.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.text.SimpleDateFormat;
import junit.framework.TestCase;

public class TestValidationRuleFormat extends TestCase
{
	public TestValidationRuleFormat(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Bean bean = new Bean("30/01/2004");
		ValidationRule rule = new ValidationRuleFormat("property", new SimpleDateFormat("dd/MM/yyyy")).setBean(bean);
		assertNotNull(rule);
	}
	
	public void testValid()
	{
		Bean bean = new Bean("30/01/2004");
		ValidationRule rule = new ValidationRuleFormat("property", new SimpleDateFormat("dd/MM/yyyy")).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testValidArray()
	{
		Bean bean = new Bean(new String[] {"30/01/2004", "01/03/2006"});
		ValidationRule rule = new ValidationRuleFormat("arrayProperty", new SimpleDateFormat("dd/MM/yyyy")).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testInvalid()
	{
		Bean bean = new Bean("3/01/2004");
		ValidationRule rule = new ValidationRuleFormat("property", new SimpleDateFormat("dd/MM/yyyy")).setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testInvalidArray()
	{
		Bean bean = new Bean(new String[] {"30/01/2004", "1/10/2006", "17/06/2006"});
		ValidationRule rule = new ValidationRuleFormat("arrayProperty", new SimpleDateFormat("dd/MM/yyyy")).setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testUnknownProperty()
	{
		Bean bean = new Bean("30/01/2004");
		ValidationRule rule = new ValidationRuleFormat("unknown_property", new SimpleDateFormat("dd/MM/yyyy")).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testGetError()
	{
		Bean bean = new Bean("82/01/2004");
		ValidationRule rule = new ValidationRuleFormat("property", new SimpleDateFormat("dd/MM/yyyy")).setBean(bean);
		ValidationError error = rule.getError();
		assertEquals(ValidationError.IDENTIFIER_INVALID, error.getIdentifier());
		assertEquals("property", error.getSubject());
		assertEquals(rule.getSubject(), error.getSubject());
	}
	
	public class Bean
	{
		private String		mProperty = null;
		private String[]	mArrayProperty = null;
		
		public Bean(String property)
		{
			mProperty = property;
		}
		
		public Bean(String[] arrayProperty)
		{
			mArrayProperty = arrayProperty;
		}
		
		public void setArrayProperty(String[] arrayProperty)
		{
			mArrayProperty = arrayProperty;
		}
		
		public String[] getArrayProperty()
		{
			return mArrayProperty;
		}
		
		public void setProperty(String property)
		{
			mProperty = property;
		}
		
		public String getProperty()
		{
			return mProperty;
		}
	}
}
