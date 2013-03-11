/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteMail.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteMail extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Mail API test suite");

		suite.addTestSuite(com.uwyn.rife.mail.TestEmail.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.mail.executors.TestDatabaseMailQueueExecutor.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.mail.dam.TestDatabaseMailQueue.class);

		return suite;
	}
}
