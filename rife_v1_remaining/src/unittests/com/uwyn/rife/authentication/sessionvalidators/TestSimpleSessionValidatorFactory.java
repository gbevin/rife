/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMemorySessions.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.ioc.HierarchicalProperties;
import junit.framework.TestCase;

public class TestSimpleSessionValidatorFactory extends TestCase
{
	private SimpleSessionValidatorFactory	mFactory = null;
	private HierarchicalProperties			mProperties = null;
	
	public TestSimpleSessionValidatorFactory(String name)
	{
		super(name);
	}

	public void setUp()
	{
		// For most of our tests we'll use a BasicSessionValidator.
		mProperties = new HierarchicalProperties();
		mProperties.put(SimpleSessionValidatorFactory.PROPERTYNAME_MANAGER_CLASS, BasicSessionValidator.class.getName());
		
		mFactory = new SimpleSessionValidatorFactory();
	}

	public void testInstantiation()
	{
		SessionValidator sessions = null;
		
		sessions = new SimpleSessionValidatorFactory().getValidator(mProperties);
		
		assertNotNull(sessions);
		assertTrue(sessions instanceof BasicSessionValidator);
	}
	
	public void testNotSingleton()
	{
		SessionValidator v1, v2;

		v1 = mFactory.getValidator(mProperties);
		v2 = mFactory.getValidator(mProperties);
		
		assertNotSame(v1, v2);
	}
}
