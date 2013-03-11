/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteAuthentication.java 3936 2008-04-26 12:05:37Z gbevin $
 */
package com.uwyn.rife.authentication;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteAuthentication extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Authentication API test suite");

		suite.addTestSuite(com.uwyn.rife.authentication.credentials.TestRoleUser.class);
		suite.addTestSuite(com.uwyn.rife.authentication.credentialsmanagers.TestRoleUserAttributes.class);

		suite.addTestSuite(com.uwyn.rife.authentication.credentialsmanagers.TestMemoryUsers.class);
		suite.addTestSuite(com.uwyn.rife.authentication.credentialsmanagers.TestXml2MemoryUsers.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.authentication.credentialsmanagers.TestDatabaseUsers.class);
		suite.addTestSuite(com.uwyn.rife.authentication.credentialsmanagers.TestCustomCredentialsManager.class);

		suite.addServersideTestSuite(com.uwyn.rife.authentication.credentialsmanagers.TestRoleUsersManagerRetriever.class);
		suite.addServersideTestSuite(com.uwyn.rife.authentication.sessionvalidators.TestSessionValidatorRetriever.class);
		suite.addServersideTestSuite(com.uwyn.rife.authentication.TestAuthenticationUtils.class);

		suite.addTestSuite(com.uwyn.rife.authentication.sessionmanagers.TestSimpleSessionManagerFactory.class);
		suite.addTestSuite(com.uwyn.rife.authentication.sessionmanagers.TestCustomSessionManager.class);
		suite.addTestSuite(com.uwyn.rife.authentication.sessionmanagers.TestMemorySessions.class);
		suite.addTestSuite(com.uwyn.rife.authentication.sessionmanagers.TestPurgingMemorySessions.class);

		suite.addTestSuite(com.uwyn.rife.authentication.sessionvalidators.TestSimpleSessionValidatorFactory.class);
		suite.addTestSuite(com.uwyn.rife.authentication.sessionvalidators.TestBasicSessionValidator.class);
		suite.addTestSuite(com.uwyn.rife.authentication.sessionvalidators.TestCustomSessionValidator.class);

		suite.addDatasourcedTestSuite(com.uwyn.rife.authentication.sessionmanagers.TestDatabaseSessions.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.authentication.sessionmanagers.TestPurgingDatabaseSessions.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.authentication.sessionvalidators.TestDatabaseSessionValidator.class);

		suite.addDatasourcedTestSuite(com.uwyn.rife.authentication.remembermanagers.TestDatabaseRemember.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.authentication.remembermanagers.TestPurgingDatabaseRemember.class);
		suite.addTestSuite(com.uwyn.rife.authentication.remembermanagers.TestCustomRememberManager.class);

		suite.addServersideTestSuite(com.uwyn.rife.authentication.elements.TestMemoryAuthenticated.class);
		suite.addServersideTestSuite(com.uwyn.rife.authentication.elements.TestPurgingMemoryAuthenticated.class);
		suite.addServersideTestSuite(com.uwyn.rife.authentication.elements.TestMemoryLogout.class);

		suite.addDatasourcedServersideTestSuite(com.uwyn.rife.authentication.elements.TestDatabaseAuthenticated.class);
		suite.addDatasourcedServersideTestSuite(com.uwyn.rife.authentication.elements.TestDatabaseAuthenticatedSessionduration.class);
		suite.addDatasourcedServersideTestSuite(com.uwyn.rife.authentication.elements.TestDatabaseLogout.class);
		suite.addDatasourcedServersideTestSuite(com.uwyn.rife.authentication.elements.TestMixedAuthenticated.class);
		suite.addDatasourcedServersideTestSuite(com.uwyn.rife.authentication.elements.TestPurgingDatabaseAuthenticated.class);
		suite.addDatasourcedServersideTestSuite(com.uwyn.rife.authentication.elements.TestPurgingMixedAuthenticated.class);

		return suite;
	}
}

