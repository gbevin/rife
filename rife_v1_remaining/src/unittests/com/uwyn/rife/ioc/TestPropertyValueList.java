/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPropertyValueList.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.ioc;

import com.uwyn.rife.database.Datasources;
import junit.framework.TestCase;

public class TestPropertyValueList extends TestCase
{
	public TestPropertyValueList(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		PropertyValueList list = new PropertyValueList();
		assertNotNull(list);
		assertEquals(0, list.size());
	}
	
	public void testSingleNonNeglectable()
	{
		PropertyValueList list = new PropertyValueList();
		PropertyValueParticipant value = new PropertyValueParticipant("ParticipantDatasources", new PropertyValueObject("unittestspgsql"));
		list.add(value);
		assertEquals(1, list.size());
		assertSame(value, list.makePropertyValue());
	}
	
	public void testOneNoneNeglectableOtherNeglectables()
	{
		PropertyValueParticipant value = new PropertyValueParticipant("ParticipantDatasources", new PropertyValueObject("unittestspgsql"));
		PropertyValueList list;
		list = new PropertyValueList();
		list.add(new PropertyValueObject("  "));
		list.add(value);
		list.add(new PropertyValueObject("    "));
		list.add(new PropertyValueObject(""));
		assertEquals(4, list.size());
		assertSame(value, list.makePropertyValue());
	}
	
	public void testOneNoneNeglectableOtherNonNeglectable()
	{
		PropertyValueParticipant value = new PropertyValueParticipant("ParticipantDatasources", new PropertyValueObject("unittestspgsql"));
		PropertyValueList list;
		list = new PropertyValueList();
		list.add(new PropertyValueObject("  "));
		list.add(value);
		list.add(new PropertyValueObject("    "));
		list.add(new PropertyValueObject("testing"));
		assertEquals(4, list.size());
		
		PropertyValue result = list.makePropertyValue();
		assertNotSame(value, result);
		assertEquals(Datasources.getRepInstance().getDatasource("unittestspgsql")+"    testing", result.getValueString());
	}
}
