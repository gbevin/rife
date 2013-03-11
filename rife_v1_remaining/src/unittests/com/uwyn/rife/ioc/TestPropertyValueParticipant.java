/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPropertyValueParticipant.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.ioc;

import junit.framework.TestCase;

import com.uwyn.rife.ioc.exceptions.ParticipantUnknownException;

public class TestPropertyValueParticipant extends TestCase
{
	public TestPropertyValueParticipant(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		PropertyValueParticipant object = new PropertyValueParticipant("ParticipantConfig", new PropertyValueObject("IOC_CONFIG"));
		assertNotNull(object);
		assertFalse(object.isStatic());
	}
	
	public void testGetValue()
	{
		PropertyValueParticipant object = new PropertyValueParticipant("ParticipantConfig", new PropertyValueObject("IOC_CONFIG"));
		assertNotNull(object.getValue());
	}
	
	public void testGetValueUnknownParticipant()
	{
		PropertyValueParticipant object = new PropertyValueParticipant("unknown", new PropertyValueObject("IOC_CONFIG"));
		try
		{
			object.getValue();
			fail("ParticipantUnknownException wasn't thrown");
		}
		catch (ParticipantUnknownException e)
		{
			assertEquals("unknown", e.getName());
		}
	}
	
	public void testGetValueUnknownKey()
	{
		PropertyValueParticipant object = new PropertyValueParticipant("ParticipantConfig", new PropertyValueObject("blubber"));
		assertNull(object.getValue());
	}
	
	public void testGetValueString()
	{
		PropertyValueParticipant object = new PropertyValueParticipant("ParticipantConfig", new PropertyValueObject(new PropertyValueParticipant("ParticipantConfig", new PropertyValueObject("IOC_CONFIG"))));
		assertEquals("pgsql", object.getValueString());
	}
	
	public void testToString()
	{
		PropertyValueParticipant object = new PropertyValueParticipant("ParticipantConfig", new PropertyValueObject("IOC_CONFIG"));
		assertEquals("IOC_DATABASE", object.toString());
	}
	
	public void testIsNeglectable()
	{
		assertFalse(new PropertyValueParticipant("ParticipantConfig", new PropertyValueObject("IOC_CONFIG")).isNeglectable());
	}
}
