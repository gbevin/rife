/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteModel extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Engine model API test suite");

		suite.addTestSuite(com.uwyn.rife.gui.model.TestParticleModel.Test.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestParticlePropertyModel.Test.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestSiteModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestElementModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestElementImplementationModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestElementInputModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestElementOutputModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestElementExitModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestSubmissionModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestSubmissionParameterModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestXml2ElementModel.class);
		suite.addTestSuite(com.uwyn.rife.gui.model.TestXml2SiteModel.class);

		return suite;
	}
}

