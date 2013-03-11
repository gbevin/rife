/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RifeTestSuite.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import java.util.ArrayList;

public class RifeTestSuite extends ParametrizedTestSuite
{
	public RifeTestSuite(String name)
	{
		super(name);
	}

	public void addDatasourcedTestSuite(Class testClass)
	{
		ArrayList<Object[]> args_list = new ArrayList<Object[]>();
		for (String datasource_name : Datasources.getRepInstance().getDatasourceNames())
		{
			if (datasource_name.startsWith("unittests"))
			{
				args_list.add(new Object[]{Datasources.getRepInstance().getDatasource(datasource_name), datasource_name, null});
			}
		}

		addTest(new ParametrizedTestSuite(testClass, new Class[] {Datasource.class, String.class, String.class}, args_list));
	}

	public void addServersideTestSuite(Class testClass)
	{
		ArrayList<Object[]> args_list = new ArrayList<Object[]>();
		args_list.add(new Object[]{TestCaseServerside.SITE_FILTER, null});
		args_list.add(new Object[]{TestCaseServerside.SITE_SERVLET, null});

		addTest(new ParametrizedTestSuite(testClass, new Class[] {int.class, String.class}, args_list));
	}

	public void addDatasourcedServersideTestSuite(Class testClass)
	{
		ArrayList<Object[]> args_list = new ArrayList<Object[]>();
		for (String datasource_name : Datasources.getRepInstance().getDatasourceNames())
		{
			if (datasource_name.startsWith("unittests"))
			{
				args_list.add(new Object[]{datasource_name, TestCaseServerside.SITE_FILTER, null});
				args_list.add(new Object[]{datasource_name, TestCaseServerside.SITE_SERVLET, null});
			}
		}

		addTest(new ParametrizedTestSuite(testClass, new Class[] {String.class, int.class, String.class}, args_list));
	}
}
