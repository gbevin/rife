/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationRuleLimitedDate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.Date;
import junit.framework.TestCase;

public class TestValidationRuleLimitedDate extends TestCase
{
	public TestValidationRuleLimitedDate(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Bean bean = new Bean(new Date(2003, 12, 11));
		ValidationRuleLimitedDate rule = new ValidationRuleLimitedDate("property", new Date(2003, 3, 1), new Date(2004, 3, 1)).setBean(bean);
		assertNotNull(rule);
	}
	
	public void testValid()
	{
		Bean bean = new Bean(new Date(2003, 12, 11));
		ValidationRuleLimitedDate rule = new ValidationRuleLimitedDate("property", new Date(2003, 3, 1), new Date(2004, 3, 1)).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testValidArray()
	{
		Bean bean = new Bean(new Date[] {new Date(2003, 12, 11), new Date(2005, 3, 7)});
		ValidationRuleLimitedDate rule = new ValidationRuleLimitedDate("arrayProperty", new Date(2003, 3, 1), new Date(2006, 3, 1)).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testInvalid()
	{
		Bean bean = new Bean(new Date(2003, 12, 11));
		ValidationRuleLimitedDate rule = new ValidationRuleLimitedDate("property", new Date(2004, 3, 1), new Date(2004, 4, 1)).setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testInvalidArray()
	{
		Bean bean = new Bean(new Date[] {new Date(2003, 12, 11), new Date(2006, 3, 7)});
		ValidationRuleLimitedDate rule = new ValidationRuleLimitedDate("arrayProperty", new Date(2003, 3, 1), new Date(2006, 3, 1)).setBean(bean);
		assertFalse(rule.validate());
	}
	
	public void testUnknownProperty()
	{
		Bean bean = new Bean(new Date(2003, 12, 11));
		ValidationRuleLimitedDate rule = new ValidationRuleLimitedDate("unknown_property", new Date(2003, 3, 1), new Date(2004, 3, 1)).setBean(bean);
		assertTrue(rule.validate());
	}
	
	public void testGetError()
	{
		Bean bean = new Bean(new Date(2003, 12, 11));
		ValidationRuleLimitedDate rule = new ValidationRuleLimitedDate("property", new Date(2004, 3, 1), new Date(2004, 4, 1)).setBean(bean);
		ValidationError error = rule.getError();
		assertEquals(ValidationError.IDENTIFIER_INVALID, error.getIdentifier());
		assertEquals("property", error.getSubject());
		assertEquals(rule.getSubject(), error.getSubject());
	}
	
	public class Bean
	{
		private Date	mProperty = null;
		private Date[]	mArrayProperty = null;
		
		public Bean(Date property)
		{
			mProperty = property;
		}
		
		public Bean(Date[] arrayProperty)
		{
			mArrayProperty = arrayProperty;
		}
		
		public void setProperty(Date property)
		{
			mProperty = property;
		}
		
		public Date getProperty()
		{
			return mProperty;
		}
		
		public void setArrayProperty(Date[] arrayProperty)
		{
			mArrayProperty = arrayProperty;
		}
		
		public Date[] getArrayProperty()
		{
			return mArrayProperty;
		}
	}
}
