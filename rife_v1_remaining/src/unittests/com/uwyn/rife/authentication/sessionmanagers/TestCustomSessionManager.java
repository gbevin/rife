/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMemorySessions.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers;

import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.elements.exceptions.UnknownSessionManagerFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import junit.framework.TestCase;

public class TestCustomSessionManager extends TestCase
{
	private HierarchicalProperties mProperties = null;

	/**
	 * Custom session manager class. Returns instances of our custom session
	 * manager, above.
	 */
	public static class CustomSessionsFactory implements SessionManagerFactory
	{
		public SessionManager getManager(HierarchicalProperties properties)
		throws PropertyValueException
		{
			String id = properties.getValueTyped("custom_id", String.class);
			if (null == id || id.length() == 0)
			{
				throw new MandatoryPropertyMissingException("custom_id");
			}
			
			return new CustomSessionManager(id);
		}
	}
	
	public TestCustomSessionManager(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		mProperties = new HierarchicalProperties();
		mProperties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, CustomSessionsFactory.class.getName());
		mProperties.put("custom_id", "x");
	}
	
	public void testInstantiation()
	{
		SessionManagerFactory factory = SessionManagerFactoryFactory.getInstance(mProperties);

		assertNotNull(factory);
		assertTrue(factory instanceof CustomSessionsFactory);
	}
	
	public void testSessionManagerInstantiation()
	{
		SessionManager manager = SessionManagerFactoryFactory.getManager(mProperties);
		
		assertNotNull(manager);
		assertTrue(manager instanceof CustomSessionManager);
		assertEquals("x", ((CustomSessionManager) manager).getId());
	}

	public void testExceptionOnMissingClass() throws Exception
	{
		try
		{
			SessionManagerFactoryFactory.getInstance(new HierarchicalProperties());
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, e.getPropertyName());
		}
	}

	public void testExceptionOnBogusClass() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, "bad.class.name");
		try
		{
			SessionManagerFactoryFactory.getInstance(properties);
			fail("Didn't get expected exception");
		}
		catch (UnknownSessionManagerFactoryClassException e)
		{
			assertEquals("bad.class.name", e.getFactoryClassName());
		}
	}
	
	public void testExceptionOnMissingFactoryParameter() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, CustomSessionsFactory.class.getName());
		try
		{
			SessionManagerFactoryFactory.getManager(properties);
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals("custom_id", e.getPropertyName());
		}
	}
}
