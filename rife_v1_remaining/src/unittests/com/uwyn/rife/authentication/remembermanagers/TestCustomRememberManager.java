/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.elements.exceptions.UnknownRememberManagerFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import junit.framework.TestCase;

public class TestCustomRememberManager extends TestCase
{
	private HierarchicalProperties	mProperties = null;

	/**
	 * Custom remember manager factory. Returns instances of our custom remember
	 * manager.
	 */
	public static class Factory implements RememberManagerFactory
	{
		public RememberManager getRememberManager(HierarchicalProperties properties)
		throws PropertyValueException
		{
			String id = properties.getValueTyped("custom_id", String.class);
			if (null == id || id.length() == 0)
			{
				throw new MandatoryPropertyMissingException("custom_id");
			}
			
			return new CustomRememberManager(id);
		}
	}
	
	public TestCustomRememberManager(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		mProperties = new HierarchicalProperties();
		mProperties.put(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, Factory.class.getName());
		mProperties.put("custom_id", "x");
	}
	
	public void testInstantiation()
	{
		RememberManagerFactory factory = RememberManagerFactoryFactory.getInstance(mProperties);

		assertNotNull(factory);
		assertTrue(factory instanceof Factory);
	}
	
	public void testRememberManagerInstantiation()
	{
		RememberManager manager = RememberManagerFactoryFactory.getManager(mProperties);
		
		assertNotNull(manager);
		assertTrue(manager instanceof CustomRememberManager);
		assertEquals("x", ((CustomRememberManager) manager).getId());
	}

	public void testExceptionOnMissingClass() throws Exception
	{
		try
		{
			RememberManagerFactoryFactory.getInstance(new HierarchicalProperties());
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, e.getPropertyName());
		}
	}

	public void testExceptionOnBogusClass() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, "bad.class.name");
		try
		{
			RememberManagerFactoryFactory.getInstance(properties);
			fail("Didn't get expected exception");
		}
		catch (UnknownRememberManagerFactoryClassException e)
		{
			assertEquals("bad.class.name", e.getFactoryClassName());
		}
	}
	
	public void testExceptionOnMissingFactoryParameter() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, Factory.class.getName());
		try
		{
			RememberManagerFactoryFactory.getManager(properties);
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals("custom_id", e.getPropertyName());
		}
	}
}
