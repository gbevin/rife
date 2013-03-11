/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationRuleNotEqual.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import junit.framework.TestCase;

public class TestValidationRuleNotEqual extends TestCase
{
	public TestValidationRuleNotEqual(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Bean bean = new Bean("value");
		ValidationRuleNotEqual rule = new ValidationRuleNotEqual("property", "").setBean(bean);
		assertNotNull(rule);
	}
	
	public void testValid()
	{
		Bean bean = new Bean("value");
		ValidationRuleNotEqual rule = new ValidationRuleNotEqual("property", "other").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testValidArray()
	{
		Bean bean = new Bean(new String[] {"value", "something"});
		ValidationRuleNotEqual rule = new ValidationRuleNotEqual("arrayProperty", "other").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testInvalid()
	{
		Bean bean = new Bean("value");
		ValidationRuleNotEqual rule = new ValidationRuleNotEqual("property", "value").setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testInvalidArray()
	{
		Bean bean = new Bean(new String[] {"value", "other"});
		ValidationRuleNotEqual rule = new ValidationRuleNotEqual("arrayProperty", "other").setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testUnknownProperty()
	{
		Bean bean = new Bean("value");
		ValidationRuleNotEqual rule = new ValidationRuleNotEqual("unknown_property", "blurp").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testGetError()
	{
		Bean bean = new Bean("value");
		ValidationRuleNotEqual rule = new ValidationRuleNotEqual("property", "value").setBean(bean);
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
