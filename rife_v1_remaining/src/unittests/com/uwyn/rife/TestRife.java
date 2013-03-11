/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRife.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife;

import junit.textui.TestRunner;
import org.apache.derby.jdbc.EmbeddedSimpleDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.NamingException;
import java.util.Locale;

public class TestRife
{
	public static void main(String[] args)
	{
		// set the default locale to be English, otherwise some tests will
		// fail in other contries or for other languages
		Locale.setDefault(Locale.ENGLISH);
		
		// initialize a mock JNDI context
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		EmbeddedSimpleDataSource dataSource = new EmbeddedSimpleDataSource();
		dataSource.setDatabaseName("embedded_dbs/derby");
		dataSource.setUser("");
		dataSource.setPassword("");
		
		builder.bind("java:comp/env/jdbc/unittestsderby", dataSource);

		try
		{
			builder.activate();
		}
		catch (NamingException e)
		{
			throw new RuntimeException(e);
		}
		
		// run tests
		TestRunner.run(TestRifeTests.suite());
	}
}
