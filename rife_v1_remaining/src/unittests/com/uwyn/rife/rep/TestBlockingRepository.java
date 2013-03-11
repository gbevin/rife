/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestBlockingRepository.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.rep.exceptions.BlockingParticipantExpectedException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import junit.framework.TestCase;

public class TestBlockingRepository extends TestCase
{
	public TestBlockingRepository(String name)
	{
		super(name);
	}

	public void testProperties()
	throws Exception
	{
		Repository rep = new BlockingRepository();
		assertEquals(rep.getProperties().size(), System.getProperties().size());
		for (Map.Entry<Object, Object> entry : (Set<Map.Entry<Object, Object>>)System.getProperties().entrySet())
		{
			assertTrue(rep.getProperties().contains((String)entry.getKey()));
			assertEquals(rep.getProperties().get((String)entry.getKey()).getValueString(), entry.getValue());
		}
	}

	public void testPropertiesSeperatedCollection()
	throws Exception
	{
		Repository rep = new BlockingRepository();
		rep.getProperties().put("os.name", "plif");
		assertFalse(System.getProperty("os.name").equals(rep.getProperties().get("os.name").getValueString()));
	}

	public static class TestParticipant extends SingleObjectParticipant
	{
		public Object getObject()
		{
			Config config = new Config();
			config.setParameter("param1", "value1");
			return config;
		}
	}
	
	public void testAddRegularParticipant()
	{
		BlockingRepository rep = new BlockingRepository();
		rep.addParticipant(TestParticipant.class, "ParticipantConfig", true, null);
		rep.runParticipants(ResourceFinderClasspath.getInstance());
		
		assertNotNull(rep.getParticipant("ParticipantConfig"));
		
		Participant participant = rep.getParticipant("ParticipantConfig");
		assertSame(participant, rep.getParticipant(TestParticipant.class.getName()));
		
		assertNotNull(participant.getObject());
		assertEquals("value1", ((Config)participant.getObject()).getString("param1"));
	}
	
	public void testAddNotParticipant()
	{
		BlockingRepository rep = new BlockingRepository();
		try
		{
			rep.addParticipant(Object.class, "ParticipantConfig", true, null);
			fail();
		}
		catch (BlockingParticipantExpectedException e)
		{
			assertEquals(Object.class.getName(), e.getClassName());
		}
	}
	
	public void testCleanup()
	{
		CleanupRepository rep = new CleanupRepository();

		assertEquals(0, rep.mParticipants.size());

		rep.addParticipant(CleanupParticipant.class, "CleanupParticipant1", true, null);
		rep.addParticipant(CleanupParticipant.class, "CleanupParticipant2", true, null);
		rep.addParticipant(CleanupParticipant.class, "CleanupParticipant3", true, null);
		rep.addParticipant(CleanupParticipant.class, "CleanupParticipant4", true, null);
		
		rep.runParticipants(ResourceFinderClasspath.getInstance());

		while (!rep.isFinished())
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{}
		}
		
		assertEquals(4, rep.mParticipants.size());

		rep.cleanup();
		
		assertEquals(0, rep.mParticipants.size());
	}
}

class CleanupRepository extends BlockingRepository
{
	public Stack<String> mParticipants = new Stack<String>();
}

class CleanupParticipant extends BlockingParticipant
{
	protected void initialize()
	{
		((CleanupRepository)getRepository()).mParticipants.push(getName());
	}
	
	protected Object _getObject()
	{
		return null;
	}
	
	protected void cleanup()
	{
		Stack<String> participants = ((CleanupRepository)getRepository()).mParticipants;
		if (participants.peek().equals(getName()))
		{
			participants.pop();
		}
	}
}

