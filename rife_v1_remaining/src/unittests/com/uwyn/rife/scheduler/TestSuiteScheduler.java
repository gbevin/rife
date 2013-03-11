/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteScheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteScheduler extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Scheduler API test suite");

		suite.addTestSuite(com.uwyn.rife.scheduler.TestFrequency.class);
		suite.addTestSuite(com.uwyn.rife.scheduler.TestTask.class);
		suite.addTestSuite(com.uwyn.rife.scheduler.TestTaskoption.class);

		suite.addTestSuite(com.uwyn.rife.scheduler.taskmanagers.TestMemoryTasks.class);
		suite.addTestSuite(com.uwyn.rife.scheduler.taskoptionmanagers.TestMemoryTaskoptions.class);
		suite.addTestSuite(com.uwyn.rife.scheduler.schedulermanagers.TestMemoryScheduler.class);
		suite.addTestSuite(com.uwyn.rife.scheduler.schedulermanagers.TestXml2MemoryScheduler.class);

		suite.addDatasourcedTestSuite(com.uwyn.rife.scheduler.schedulermanagers.TestDatabaseSchedulerInstallation.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.scheduler.taskmanagers.TestDatabaseTasks.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.scheduler.taskoptionmanagers.TestDatabaseTaskoptions.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.scheduler.schedulermanagers.TestDatabaseScheduler.class);

		return suite;
	}
}
