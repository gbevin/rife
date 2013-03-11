/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteFeed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteFeed extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Feeds element test suite");
		
		suite.addServersideTestSuite(com.uwyn.rife.feed.TestFeedProvider.class);
		
		return suite;
	}
}
