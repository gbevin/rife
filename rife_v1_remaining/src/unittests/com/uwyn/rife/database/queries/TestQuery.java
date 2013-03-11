/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import junit.framework.TestCase;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;

public abstract class TestQuery extends TestCase
{
	protected Datasource	mPgsql = null;
	protected Datasource	mOracle = null;
	protected Datasource	mHsqldb = null;
	protected Datasource	mH2 = null;
	protected Datasource	mMysql = null;
	protected Datasource	mFirebird = null;
	protected Datasource	mMckoi = null;
	protected Datasource	mDerby = null;
	protected Datasource	mDaffodil = null;

	protected TestQuery(String name)
	{
		super(name);
	}

	public void setUp()
	{
		mPgsql = Datasources.getRepInstance().getDatasource("unittestspgsql");
		mOracle = Datasources.getRepInstance().getDatasource("unittestsoracle");
		mMysql = Datasources.getRepInstance().getDatasource("unittestsmysql");
		mHsqldb = Datasources.getRepInstance().getDatasource("unittestshsqldb");
		mH2 = Datasources.getRepInstance().getDatasource("unittestsh2");
		mFirebird = Datasources.getRepInstance().getDatasource("unittestsfirebird");
		mMckoi = Datasources.getRepInstance().getDatasource("unittestsmckoi");
		mDerby = Datasources.getRepInstance().getDatasource("unittestsderby");
		mDaffodil = Datasources.getRepInstance().getDatasource("unittestsdaffodil");
	}
}
