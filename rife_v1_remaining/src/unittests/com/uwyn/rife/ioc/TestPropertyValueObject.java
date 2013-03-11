/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPropertyValueObject.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.ioc;

import junit.framework.TestCase;

public class TestPropertyValueObject extends TestCase
{
	public TestPropertyValueObject(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		Integer value = new Integer(25);
		
		PropertyValueObject object = new PropertyValueObject(value);
		assertNotNull(object);
		assertTrue(object.isStatic());
	}
	
	public void testGetValue()
	{
		Integer value = new Integer(25);
		
		PropertyValueObject object = new PropertyValueObject(value);
		assertSame(value, object.getValue());
	}
	
	public void testGetValueString()
	{
		Integer value = new Integer(25);
		
		PropertyValueObject object = new PropertyValueObject(value);
		assertEquals("25", object.getValueString());
	}
	
	public void testToString()
	{
		Integer value = new Integer(25);
		
		PropertyValueObject object = new PropertyValueObject(value);
		assertEquals("25", object.toString());
	}
	
	public void testIsNeglectable()
	{
		assertFalse(new PropertyValueObject("lhkjkj").isNeglectable());
		assertTrue(new PropertyValueObject("   	 ").isNeglectable());
		assertTrue(new PropertyValueObject("").isNeglectable());
		assertTrue(new PropertyValueObject(null).isNeglectable());
	}
}
