/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationRuleNotNull.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import junit.framework.TestCase;

public class TestValidationRuleNotNull extends TestCase
{
	public TestValidationRuleNotNull(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Bean bean = new Bean("not null");
		ValidationRuleNotNull rule = new ValidationRuleNotNull("property").setBean(bean);
		assertNotNull(rule);
	}
	
	public void testValid()
	{
		Bean bean = new Bean("not null");
		ValidationRuleNotNull rule = new ValidationRuleNotNull("property").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testInvalid()
	{
		Bean bean = new Bean(null);
		ValidationRuleNotNull rule = new ValidationRuleNotNull("property").setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testUnknownProperty()
	{
		Bean bean = new Bean("not null");
		ValidationRuleNotNull rule = new ValidationRuleNotNull("unknown_property").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testGetError()
	{
		Bean bean = new Bean("");
		ValidationRuleNotNull rule = new ValidationRuleNotNull("property").setBean(bean);
		ValidationError error = rule.getError();
		assertEquals(ValidationError.IDENTIFIER_MANDATORY, error.getIdentifier());
		assertEquals("property", error.getSubject());
		assertEquals(rule.getSubject(), error.getSubject());
	}
	
	public class Bean
	{
		private String	mProperty = null;
		
		public Bean(String property)
		{
			mProperty = property;
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
