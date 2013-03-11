/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestParticipantSite.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import junit.framework.TestCase;

public class TestParticipantSite extends TestCase
{
	public TestParticipantSite(String name)
	{
		super(name);
	}

	public void testParticipant()
	{
		Site site = Site.getRepInstance();
		
		assertEquals(site.getUrls().size(), 1);
		
		assertEquals("com.uwyn.rife.engine.testelements.engine.Simple", site.resolveUrl("/participant/element", null).getImplementation());

		ElementInfo elementinfo1 = site.resolveUrl("/participant/element", null);
		assertNotNull(elementinfo1);
		assertTrue(!elementinfo1.hasGlobalVars());
		assertNull(elementinfo1.getInheritanceStack());
	}
}

