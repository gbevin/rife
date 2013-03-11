/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRifeTests.java 3944 2008-04-27 21:07:04Z gbevin $
 */
package com.uwyn.rife;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.rep.Rep;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestRifeTests extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Uwyn RIFE tests");

		Rep.initialize("rep/unittests_participants.xml");
		Logger.getLogger("com.uwyn.rife").setLevel(Level.WARNING);
		RifeConfig.Engine.setLogEngineExceptions(false);

		suite.addTest(com.uwyn.rife.authentication.TestSuiteAuthentication.suite());
		suite.addTest(com.uwyn.rife.cmf.TestSuiteCmf.suite());
		suite.addTest(com.uwyn.rife.config.TestSuiteConfig.suite());
		suite.addTest(com.uwyn.rife.continuations.TestSuiteContinuations.suite());
		suite.addTest(com.uwyn.rife.database.TestSuiteDatabase.suite());
		suite.addTest(com.uwyn.rife.datastructures.TestSuiteDatastructures.suite());
		suite.addTest(com.uwyn.rife.engine.TestSuiteEngine.suite());
		suite.addTest(com.uwyn.rife.feed.TestSuiteFeed.suite());
		suite.addTest(com.uwyn.rife.ioc.TestSuiteIoc.suite());
		suite.addTest(com.uwyn.rife.mail.TestSuiteMail.suite());
		suite.addTest(com.uwyn.rife.resources.TestSuiteResources.suite());
		suite.addTest(com.uwyn.rife.rep.TestSuiteRep.suite());
		suite.addTest(com.uwyn.rife.scheduler.TestSuiteScheduler.suite());
		suite.addTest(com.uwyn.rife.site.TestSuiteSite.suite());
		suite.addTest(com.uwyn.rife.template.TestSuiteTemplate.suite());
		suite.addTest(com.uwyn.rife.test.TestSuiteTest.suite());
		suite.addTest(com.uwyn.rife.tools.TestSuiteTools.suite());
//		suite.addTest(com.uwyn.rife.gui.TestSuiteGui.suite());

		return suite;
	}
}
