/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestAbstractValidationRule.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import junit.framework.TestCase;

public class TestAbstractValidationRule extends TestCase
{
	private String mValue = null;
	
	public TestAbstractValidationRule(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		MyRule rule = new MyRule();
		assertNotNull(rule);
	}

	public void testGetSubject()
	{
		MyRule rule = new MyRule();
		String subject  = rule.getSubject();
		assertEquals("the value", subject);
		assertSame(subject, rule.getSubject());
	}
	
	public void testValid()
	{
		MyRule rule = new MyRule();
		mValue = "ok";
		assertTrue(rule.validate());
	}
	
	public void testInvalid()
	{
		MyRule rule = new MyRule();
		mValue = null;
		assertFalse(rule.validate());
	}
	
	public void testGetError()
	{
		MyRule rule = new MyRule();
		ValidationError error = rule.getError();
		assertEquals(ValidationError.IDENTIFIER_MANDATORY, error.getIdentifier());
		assertEquals("the value", error.getSubject());
		assertEquals(rule.getSubject(), error.getSubject());
	}
	
	class MyRule extends AbstractValidationRule
	{
		public ValidationError getError()
		{
			return new ValidationError.MANDATORY("the value");
		}
		
		public boolean validate()
		{
			return mValue != null;
		}
	}
}
