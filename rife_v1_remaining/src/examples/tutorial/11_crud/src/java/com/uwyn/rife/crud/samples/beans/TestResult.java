/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestResult.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class TestResult extends MetaData
{
	private int 	mId = -1;
	private int 	mTestId = -1;
	private Integer mMinpoints = null;
	private String 	mResult = null;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedBean()
			.defaultOrder("minpoints"));
		
		addConstraint(new ConstrainedProperty("id")
			.editable(false)
			.identifier(true));
		
		addConstraint(new ConstrainedProperty("testId")
			.editable(false)
			.manyToOne(Test.class, "id", null, CreateTable.CASCADE));
		
		addConstraint(new ConstrainedProperty("minpoints")
			.notNull(true)
			.listed(true));
		
		addConstraint(new ConstrainedProperty("result")
			.notEmpty(true)
			.notNull(true));
		
		addConstraint(new ConstrainedBean()
			.defaultOrder("minpoints"));
	}

	public TestResult	id(int id)		{ setId(id); return this; }
	public void			setId(int id)	{ mId = id; }
	public int			getId() 		{ return mId; }
	
	public TestResult	testId(int testId)		{ setTestId(testId); return this; }
	public void			setTestId(int testId)	{ mTestId = testId; }
	public int			getTestId() 			{ return mTestId; }
	
	public TestResult	minpoints(Integer minpoints)	{ setMinpoints(minpoints); return this; }
	public void			setMinpoints(Integer minpoints)	{ mMinpoints = minpoints; }
	public Integer		getMinpoints()					{ return mMinpoints; }
	
	public TestResult	result(String result)		{ setResult(result); return this; }
	public void			setResult(String result)	{ mResult = result; }
	public String		getResult()					{ return mResult; }
}

