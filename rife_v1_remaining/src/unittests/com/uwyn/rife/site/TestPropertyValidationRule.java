/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPropertyValidationRule.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import junit.framework.TestCase;

public class TestPropertyValidationRule extends TestCase
{
	public TestPropertyValidationRule(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Rule rule = new Rule("property");
		assertNotNull(rule);
		assertEquals("property", rule.getPropertyName());
		assertNull(rule.getBean());
		assertEquals("property", rule.getSubject());
	}

	public void testPropertyName()
	{
		Rule rule = new Rule("property");
		assertSame(rule, rule.setPropertyName("property2"));
		assertEquals("property2", rule.getPropertyName());
		assertEquals("property", rule.getSubject());
	}

	public void testBean()
	{
		Rule rule = new Rule("property");
		assertSame(rule, rule.setBean(this));
		assertSame(this, rule.getBean());
	}

	public void testSubjectName()
	{
		Rule rule = new Rule("property");
		assertSame(rule, rule.setSubject("property2"));
		assertEquals("property", rule.getPropertyName());
		assertEquals("property2", rule.getSubject());
		assertSame(rule, rule.setSubject(null));
		assertEquals("property", rule.getSubject());
	}
	
	public class Rule extends PropertyValidationRule
	{
		Rule(String property)
		{
			super(property);
		}
		
		public boolean validate()
		{
			return false;
		}
		
		public ValidationError getError()
		{
			return null;
		}
	}
}
