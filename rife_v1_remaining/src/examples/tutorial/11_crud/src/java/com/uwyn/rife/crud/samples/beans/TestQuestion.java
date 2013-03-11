/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestQuestion.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class TestQuestion extends MetaData
{
	private int 	mId = -1;
	private int 	mTestId = -1;
	private int 	mOrdinal = -1;
	private String 	mQuestion = null;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedBean()
			.defaultOrder("ordinal"));
		
		addConstraint(new ConstrainedProperty("id")
			.editable(false)
			.identifier(true));
		
		addConstraint(new ConstrainedProperty("testId")
			.editable(false)
			.manyToOne(Test.class, "id", null, CreateTable.CASCADE));
		
		addConstraint(new ConstrainedProperty("ordinal")
			.editable(false)
			.ordinal(true, "testId"));
		
		addConstraint(new ConstrainedProperty("question")
			.notEmpty(true)
			.notNull(true)
			.maxLength(255)
			.listed(true));
		
		addConstraint(new ConstrainedBean()
			.defaultOrder("ordinal")
			.associations(TestAnswer.class));
	}

	public TestQuestion		id(int id)		{ setId(id); return this; }
	public void				setId(int id)	{ mId = id; }
	public int				getId() 		{ return mId; }
	
	public TestQuestion		testId(int testId)		{ setTestId(testId); return this; }
	public void				setTestId(int testId)	{ mTestId = testId; }
	public int				getTestId() 			{ return mTestId; }
	
	public TestQuestion		ordinal(int ordinal)	{ setOrdinal(ordinal); return this; }
	public void				setOrdinal(int ordinal)	{ mOrdinal = ordinal; }
	public int				getOrdinal()			{ return mOrdinal; }
	
	public TestQuestion		question(String question)		{ setQuestion(question); return this; }
	public void				setQuestion(String question)	{ mQuestion = question; }
	public String			getQuestion()					{ return mQuestion; }
}

