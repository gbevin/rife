/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMemorySessions.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionAttributes;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.elements.exceptions.UnknownSessionValidatorFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import junit.framework.TestCase;

public class TestCustomSessionValidator extends TestCase
{
	private HierarchicalProperties	mProperties = null;

	/**
	 * Dummy session validator class; this is just a mock we can use to test
	 * that our factory is being used.
	 */
	public static class CustomSessionValidator extends AbstractSessionValidator
	{
		private String mId;
		
		public CustomSessionValidator(String id)
		{
			mId = id;
		}
		
		public String getId()
		{
			return mId;
		}
		
		public boolean isAccessAuthorized(int id)
		{
			return false;
		}
		
		public int validateSession(String authId, String hostIp, SessionAttributes attributes)
		{
			return 0;
		}
	}

	/**
	 * Custom session validator factory class. Returns instances of our custom
	 * session validator, above.
	 */
	public static class CustomValidatorFactory implements SessionValidatorFactory
	{
		public SessionValidator getValidator(HierarchicalProperties properties)
		throws PropertyValueException
		{
			String id = properties.getValueTyped("custom_id", String.class);
			
			if (null == id || id.length() == 0)
			{
				throw new MandatoryPropertyMissingException("custom_id");
			}
			
			return new CustomSessionValidator(id);
		}
	}
	
	public TestCustomSessionValidator(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		mProperties = new HierarchicalProperties();
		mProperties.put(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS, CustomValidatorFactory.class.getName());
		mProperties.put("custom_id", "x");
	}
	
	public void testInstantiation()
	{
		SessionValidatorFactory factory = SessionValidatorFactoryFactory.getInstance(mProperties);

		assertNotNull(factory);
		assertTrue(factory instanceof CustomValidatorFactory);
	}
	
	public void testSessionValidatorInstantiation()
	{
		SessionValidator validator = SessionValidatorFactoryFactory.getValidator(mProperties);
		
		assertNotNull(validator);
		assertTrue(validator instanceof CustomSessionValidator);
		assertEquals("x", ((CustomSessionValidator) validator).getId());
	}

	public void testExceptionOnMissingClass() throws Exception
	{
		try
		{
			SessionValidatorFactoryFactory.getInstance(new HierarchicalProperties());
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS, e.getPropertyName());
		}
	}

	public void testExceptionOnBogusClass() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS, "bad.class.name");
		try
		{
			SessionValidatorFactoryFactory.getInstance(properties);
			fail("Didn't get expected exception");
		}
		catch (UnknownSessionValidatorFactoryClassException e)
		{
			assertEquals(e.getValidatorClassName(), "bad.class.name");
		}
	}
	
	public void testExceptionOnMissingFactoryParameter() throws Exception
	{
		HierarchicalProperties properties = new HierarchicalProperties();
		properties.put(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS, CustomValidatorFactory.class.getName());
		try
		{
			SessionValidatorFactoryFactory.getValidator(properties);
			fail("Didn't get expected exception");
		}
		catch (MandatoryPropertyMissingException e)
		{
			assertEquals("custom_id", e.getPropertyName());
		}
	}
}
