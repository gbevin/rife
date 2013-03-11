/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationRuleRange.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import junit.framework.TestCase;

public class TestValidationRuleRange extends TestCase
{
	public TestValidationRuleRange(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Bean bean = new Bean(21);
		ValidationRuleRange rule = new ValidationRuleRange("property", new Integer(12), new Integer(30)).setBean(bean);
		assertNotNull(rule);
	}
	
	public void testValid()
	{
		Bean bean = new Bean(21);
		ValidationRuleRange rule = new ValidationRuleRange("property", new Integer(12), new Integer(30)).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testValidArray()
	{
		Bean bean = new Bean(new int[] {21, 24, 30});
		ValidationRuleRange rule = new ValidationRuleRange("arrayproperty", 12, 30).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testInvalid()
	{
		Bean bean = new Bean(21);
		ValidationRuleRange rule = new ValidationRuleRange("property", new Integer(12), new Integer(20)).setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testInvalidArray()
	{
		Bean bean = new Bean(new int[] {21, 24, 30});
		ValidationRuleRange rule = new ValidationRuleRange("arrayproperty", 12, 29).setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testUnknownProperty()
	{
		Bean bean = new Bean(21);
		ValidationRuleRange rule = new ValidationRuleRange("unknown_property", new Integer(12), new Integer(30)).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testGetError()
	{
		Bean bean = new Bean(21);
		ValidationRuleRange rule = new ValidationRuleRange("property", new Integer(12), new Integer(20)).setBean(bean);
		ValidationError error = rule.getError();
		assertEquals(ValidationError.IDENTIFIER_INVALID, error.getIdentifier());
		assertEquals("property", error.getSubject());
		assertEquals(rule.getSubject(), error.getSubject());
	}
	
	public class Bean
	{
		private int		mProperty;
		private int[]	mArrayproperty;
		
		public Bean(int property)
		{
			mProperty = property;
		}
		
		public Bean(int[] arrayproperty)
		{
			mArrayproperty = arrayproperty;
		}
		
		public void setProperty(int property)
		{
			mProperty = property;
		}
		
		public int getProperty()
		{
			return mProperty;
		}
		
		public void setArrayproperty(int[] arrayproperty)
		{
			mArrayproperty = arrayproperty;
		}
		
		public int[] getArrayproperty()
		{
			return mArrayproperty;
		}
	}
}
