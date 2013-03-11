/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationRuleSameAs.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import junit.framework.TestCase;

public class TestValidationRuleSameAs extends TestCase
{
	public TestValidationRuleSameAs(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Bean bean = new Bean("value", "value");
		ValidationRuleSameAs rule = new ValidationRuleSameAs("other", "property").setBean(bean);
		assertNotNull(rule);
	}
	
	public void testValid()
	{
		Bean bean = new Bean("value", "value");
		ValidationRuleSameAs rule = new ValidationRuleSameAs("other", "property").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testValidArray()
	{
		Bean bean = new Bean(new String[] {"value", "value"}, "value");
		ValidationRuleSameAs rule = new ValidationRuleSameAs("propertyArray", "other").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testInvalid()
	{
		Bean bean = new Bean("value", "value2");
		ValidationRuleSameAs rule = new ValidationRuleSameAs("other", "property").setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testInalidArray()
	{
		Bean bean = new Bean(new String[] {"value", "value2"}, "value");
		ValidationRuleSameAs rule = new ValidationRuleSameAs("propertyArray", "other").setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testUnknownProperty()
	{
		Bean bean = new Bean("value", "value2");
		ValidationRuleSameAs rule = new ValidationRuleSameAs("other", "unknown_property").setBean(bean);
		assertTrue(rule.validate());
		rule = new ValidationRuleSameAs("unknown_other", "property").setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testGetError()
	{
		Bean bean = new Bean("value", "value2");
		ValidationRuleSameAs rule = new ValidationRuleSameAs("other", "property").setBean(bean);
		ValidationError error = rule.getError();
		assertEquals(ValidationError.IDENTIFIER_NOTSAME, error.getIdentifier());
		assertEquals("other", error.getSubject());
		assertEquals(rule.getSubject(), error.getSubject());
	}
	
	public class Bean
	{
		private String		mProperty = null;
		private String[]	mPropertyArray = null;
		private String		mOther = null;
		
		public Bean(String property, String other)
		{
			mProperty = property;
			mOther = other;
		}
		
		public Bean(String[] propertyArray, String other)
		{
			mPropertyArray = propertyArray;
			mOther = other;
		}
		
		public void setProperty(String property)
		{
			mProperty = property;
		}
		
		public String getProperty()
		{
			return mProperty;
		}
		
		public void setPropertyArray(String[] propertyArray)
		{
			mPropertyArray = propertyArray;
		}
		
		public String[] getPropertyArray()
		{
			return mPropertyArray;
		}
		
		public void setOther(String other)
		{
			mOther = other;
		}
		
		public String getOther()
		{
			return mOther;
		}
	}
}
