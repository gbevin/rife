/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestAnswer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class TestAnswer extends MetaData
{
	private int 	mId = -1;
	private int 	mQuestionId = -1;
	private int 	mOrdinal = -1;
	private String 	mAnswer = null;
	private Integer	mPoints = null;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedBean()
			.defaultOrder("ordinal"));
		
		addConstraint(new ConstrainedProperty("id")
			.editable(false)
			.identifier(true));
		
		addConstraint(new ConstrainedProperty("questionId")
			.editable(false)
			.manyToOne(TestQuestion.class, "id", null, CreateTable.CASCADE));
		
		addConstraint(new ConstrainedProperty("ordinal")
			.editable(false)
			.ordinal(true, "questionId"));
		
		addConstraint(new ConstrainedProperty("answer")
			.notEmpty(true)
			.notNull(true)
			.maxLength(255)
			.listed(true));
		
		addConstraint(new ConstrainedProperty("points")
			.notEmpty(true)
			.notNull(true)
			.listed(true));
		
		addConstraint(new ConstrainedBean()
			.defaultOrder("ordinal"));
	}
	
	public TestAnswer	id(int id)		{ setId(id); return this; }
	public void			setId(int id)	{ mId = id; }
	public int			getId() 		{ return mId; }
	
	public TestAnswer	questionId(int questionId)		{ setQuestionId(questionId); return this; }
	public void			setQuestionId(int questionId)	{ mQuestionId = questionId; }
	public int			getQuestionId() 				{ return mQuestionId; }
	
	public TestAnswer	ordinal(int ordinal)	{ setOrdinal(ordinal); return this; }
	public void			setOrdinal(int ordinal)	{ mOrdinal = ordinal; }
	public int			getOrdinal()			{ return mOrdinal; }
	
	public TestAnswer	answer(String answer)		{ setAnswer(answer); return this; }
	public void			setAnswer(String answer)	{ mAnswer = answer; }
	public String		getAnswer()					{ return mAnswer; }
	
	public TestAnswer	points(Integer points)		{ setPoints(points); return this; }
	public void			setPoints(Integer points)	{ mPoints = points; }
	public Integer		getPoints()					{ return mPoints; }
}

