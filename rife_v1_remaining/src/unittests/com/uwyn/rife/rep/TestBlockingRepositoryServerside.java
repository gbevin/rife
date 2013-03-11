/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestBlockingRepositoryServerside.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.TestCaseServerside;

public class TestBlockingRepositoryServerside extends TestCaseServerside
{
	private Repository mDefaultRep = null;
	
	public TestBlockingRepositoryServerside(int siteType, String name)
	{
		super(siteType, name);
	}

	public void setUp()
	{
		mDefaultRep = Rep.getDefaultRepository();
	}

	public void tearDown()
	throws Exception
	{
		Rep.setDefaultRepository(mDefaultRep);
		super.tearDown();
	}

	public void testInitParams()
	throws Exception
	{
		Repository rep = new BlockingRepository();
		Rep.setDefaultRepository(rep);
		rep.getProperties().remove("new.property");
		rep.getProperties().remove("another.new.property");
		assertFalse(rep.getProperties().contains("new.property"));
		assertFalse(rep.getProperties().contains("another.new.property"));
		
		setupSite("site/empty.xml", new String[][] {{"new.property", "new value"}, {"another.new.property", "another new value"}});
		
		assertTrue(rep.getProperties().contains("new.property"));
		assertEquals(rep.getProperties().get("new.property").getValueString(), "new value");
		assertTrue(rep.getProperties().contains("another.new.property"));
		assertEquals(rep.getProperties().get("another.new.property").getValueString(), "another new value");
	}
}
