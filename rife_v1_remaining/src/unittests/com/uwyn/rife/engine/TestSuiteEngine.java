/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteEngine.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteEngine extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Engine API test suite");

		suite.addTestSuite(com.uwyn.rife.engine.TestSubmission.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestElementInfo.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestParticipantSite.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestSite.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestSiteBuilder.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestAnnotations2ElementInfo.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestXml2ElementInfo.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestXml2Site.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestGroovy2ElementInfo.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestGroovy2Site.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestJanino2ElementInfo.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestJanino2Site.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestReloadDeclarations.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestElements.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngine.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineStateSession.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineSubmissions.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineInputs.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineOutputs.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineExits.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineGlobals.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineCookies.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineInheritance.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEnginePrecedence.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineExtending.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineCasefigures.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineSubsites.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineEmbedding.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineScripted.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineSoapXFire.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineWebservicesHessian.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineDwr.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineContinuations.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineExpressionRoleUser.class);
		suite.addServersideTestSuite(com.uwyn.rife.engine.TestEngineExpressionElement.class);
		suite.addTestSuite(com.uwyn.rife.engine.TestEngineFilter.class);

		return suite;
	}
}
