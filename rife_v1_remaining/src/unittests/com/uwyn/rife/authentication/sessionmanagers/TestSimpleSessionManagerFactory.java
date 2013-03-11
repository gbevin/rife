/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMemorySessions.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers;

import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.ioc.HierarchicalProperties;
import junit.framework.TestCase;

public class TestSimpleSessionManagerFactory extends TestCase
{
	private SimpleSessionManagerFactory	mFactory = null;
	private HierarchicalProperties		mProperties = null;
	
	public TestSimpleSessionManagerFactory(String name)
	{
		super(name);
	}

	public void setUp()
	{
		// For most of our tests we'll use MemorySessions, so default to that.
		mProperties = new HierarchicalProperties();
		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_CLASS, MemorySessions.class.getName());
		
		mFactory = new SimpleSessionManagerFactory();
	}

	public void testInstantiation()
	{
		SessionManager sessions = null;
		
		sessions = new SimpleSessionManagerFactory().getManager(mProperties);
		
		assertNotNull(sessions);
		assertTrue(sessions instanceof MemorySessions);
	}
	
	public void testMultipleSessionMangerIds()
	{
		SessionManager smA, smA2, smB;
		
		mProperties.put("sessionmanager_id", "a");
		smA = mFactory.getManager(mProperties);
		
		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_ID, "b");
		smB = mFactory.getManager(mProperties);
		
		assertNotSame(smA, smB);

		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_ID, "a");
		smA2 = mFactory.getManager(mProperties);

		assertSame(smA, smA2);
	}

	public void testDefaultIdIsBlank()
	{
		SessionManager smDefault, smBlank;
		
		smDefault = mFactory.getManager(mProperties);
		
		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_ID, "");
		smBlank = mFactory.getManager(mProperties);
		
		assertSame(smDefault, smBlank);
	}
	
	public void testSeparateNamespacesForDifferentClassesWithDefaultName()
	{
		SessionManager smA, smB;
		
		smA = mFactory.getManager(mProperties);
		
		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_CLASS, CustomSessionManager.class.getName());
		smB = mFactory.getManager(mProperties);
		
		assertNotSame(smA, smB);
		assertTrue(smA instanceof MemorySessions);
		assertTrue(smB instanceof CustomSessionManager);
	}
	
	public void testSeparateNamespacesForDifferentClassesWithExplicitName()
	{
		SessionManager smA, smB;
		
		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_ID, "x");
		smA = mFactory.getManager(mProperties);
		
		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_CLASS, CustomSessionManager.class.getName());
		smB = mFactory.getManager(mProperties);
		
		assertNotSame(smA, smB);
		assertTrue(smA instanceof MemorySessions);
		assertTrue(smB instanceof CustomSessionManager);
	}
}
