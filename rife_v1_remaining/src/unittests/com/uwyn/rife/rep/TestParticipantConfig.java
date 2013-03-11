/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestParticipantConfig.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.rep.BlockingRepository;
import com.uwyn.rife.rep.participants.ParticipantConfig;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import junit.framework.TestCase;

public class TestParticipantConfig extends TestCase
{
	private Repository mDefaultRep = null;
	
	public TestParticipantConfig(String name)
	{
		super(name);
	}

	public void setUp()
	{
		mDefaultRep = Rep.getDefaultRepository();
	}

	public void tearDown()
	{
		Rep.setDefaultRepository(mDefaultRep);
	}

	public void testParticipantNotDefined()
	throws Exception
	{
		BlockingRepository   rep = new BlockingRepository();
		rep.runParticipants(ResourceFinderClasspath.getInstance());
		Rep.setDefaultRepository(rep);
		assertFalse(Rep.hasParticipant("ParticipantConfig"));
		assertNull(Rep.getParticipant("ParticipantConfig"));
		assertFalse(Config.hasRepInstance());
		assertNull(Config.getRepInstance());
	}

	public void testParticipantMissingParameter()
	throws Exception
	{
		BlockingRepository   rep = new BlockingRepository();
		rep.addParticipant(ParticipantConfig.class.getName(), null, true, null);
		try
		{
			rep.runParticipants(ResourceFinderClasspath.getInstance());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(e.getMessage().indexOf("can't be null") != -1);
		}
	}
	
	public void testParticipantDefined()
	throws Exception
	{
		BlockingRepository   rep = new BlockingRepository();
		rep.addParticipant(ParticipantConfig.class.getName(), null, true, "xml/test_xml2config.xml");
		rep.runParticipants(ResourceFinderClasspath.getInstance());
		
		assertNotSame(Rep.getParticipant("ParticipantConfig"), rep.getParticipant("ParticipantConfig"));
		assertNotSame(Config.getRepInstance(), rep.getParticipant("ParticipantConfig").getObject());

		Rep.setDefaultRepository(rep);
		
		assertTrue(Rep.hasParticipant("ParticipantConfig"));
		assertNotNull(Rep.getParticipant("ParticipantConfig"));
		assertSame(Rep.getParticipant("ParticipantConfig"), rep.getParticipant("ParticipantConfig"));
		assertTrue(Config.hasRepInstance());
		assertNotNull(Config.getRepInstance());
		assertSame(Config.getRepInstance(), rep.getParticipant("ParticipantConfig").getObject());
		assertTrue(Config.getRepInstance() instanceof Config);
	}
	
	public void testParticipantXmlSelectorProperty()
	throws Exception
	{
		BlockingRepository   rep = new BlockingRepository();
		rep.addParticipant(ParticipantConfig.class.getName(), null, true, "XmlSelectorProperty");
		rep.runParticipants(ResourceFinderClasspath.getInstance());
		Rep.setDefaultRepository(rep);
		
		Config config = Config.getRepInstance();
		assertNotNull(config);
		
		assertEquals(config.getString("TEST_CONFIG_PARAM"), "this value is available in test config");
	}
}

