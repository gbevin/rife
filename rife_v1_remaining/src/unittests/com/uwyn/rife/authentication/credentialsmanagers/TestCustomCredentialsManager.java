/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.elements.exceptions.UnknownCredentialsManagerFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import junit.framework.TestCase;

public class TestCustomCredentialsManager extends TestCase
{
	private HierarchicalProperties mProperties;

	/**
	 * Custom credentials manager factory. Returns instances of our custom
	 * credentials manager.
	 */
	public static class Factory implements CredentialsManagerFactory
	{
		public CredentialsManager getCredentialsManager(HierarchicalProperties properties)
		throws PropertyValueException
		{
			String id = properties.getValueTyped("custom_id", String.class);
			if (null == id || id.length() == 0)
			{
				throw new MandatoryPropertyMissingException("custom_id");
			}
			
			return new CustomCredentialsManager(id);
		}
	}
	
	public TestCustomCredentialsManager(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		mProperties = new HierarchicalProperties();
		mProperties.put(CredentialsManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, Factory.class.getName());
		mProperties.put("custom_id", "x");
	}
	
	public void testInstantiation()
	{
		CredentialsManagerFactory factory = CredentialsManagerFactoryFactory.getInstance(mProperties);

		assertNotNull(factory);
		assertTrue(factory instanceof Factory);
	}
	
	public void testCredentialsManagerInstantiation()
	{
		CredentialsManager manager = CredentialsManagerFactoryFactory.getManager(mProperties);
		
		assertNotNull(manager);
		assertTrue(manager instanceof CustomCredentialsManager);
		assertEquals("x", ((CustomCredentialsManager) manager).getId());
	}

	public void testExceptionOnMissingClass() throws Exception
	{
		try
		{
			CredentialsManagerFactoryFactory.getInstance(new HierarchicalProperties());
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals(CredentialsManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, e.getPropertyName());
		}
	}

	public void testExceptionOnBogusClass() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(CredentialsManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, "bad.class.name");
		try
		{
			CredentialsManagerFactoryFactory.getInstance(properties);
			fail("Didn't get expected exception");
		}
		catch (UnknownCredentialsManagerFactoryClassException e)
		{
			assertEquals("bad.class.name", e.getManagerClassName());
		}
	}
	
	public void testExceptionOnMissingFactoryParameter() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(CredentialsManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, Factory.class.getName());
		try
		{
			CredentialsManagerFactoryFactory.getManager(properties);
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals("custom_id", e.getPropertyName());
		}
	}
}
