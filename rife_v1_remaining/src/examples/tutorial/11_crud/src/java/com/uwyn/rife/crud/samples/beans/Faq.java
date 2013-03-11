/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Faq.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;
import java.io.InputStream;

public class Faq extends MetaData
{
	private int 			mId = -1;
	private int 			mOrdinal = -1;
	private String 			mQuestion = null;
	private String 			mAnswer = null;
	private InputStream 	mAttachment = null;
	private int 			mCategoryId = -1;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedBean()
			.defaultOrder("ordinal"));
		
		addConstraint(new ConstrainedProperty("id")
			.editable(false)
			.identifier(true));
		
		addConstraint(new ConstrainedProperty("ordinal")
			.editable(false)
			.ordinal(true));
		
		addConstraint(new ConstrainedProperty("question")
			.notEmpty(true)
			.notNull(true)
			.maxLength(255)
			.listed(true));

		addConstraint(new ConstrainedProperty("answer")
			.mimeType(MimeType.APPLICATION_XHTML)
			.notEmpty(true)
			.notNull(true)
			.autoRetrieved(true)
			.fragment(true));
		
		addConstraint(new ConstrainedProperty("categoryId")
			.notNull(true)
			.manyToOne(Category.class, "id")
			.listed(true));
		
		addConstraint(new ConstrainedProperty("attachment")
			.mimeType(MimeType.RAW)
			.notNull(true)
			.file(true)
			.listed(true));
	}

	public Faq			id(int id)		{ setId(id); return this; }
	public void			setId(int id)	{ mId = id; }
	public int			getId() 		{ return mId; }
	
	public Faq			ordinal(int ordinal)	{ setOrdinal(ordinal); return this; }
	public void			setOrdinal(int ordinal)	{ mOrdinal = ordinal; }
	public int			getOrdinal()			{ return mOrdinal; }
	
	public Faq			question(String question)		{ setQuestion(question); return this; }
	public void			setQuestion(String question)	{ mQuestion = question; }
	public String		getQuestion()					{ return mQuestion; }
	
	public Faq			answer(String answer)		{ setAnswer(answer); return this; }
	public void			setAnswer(String answer)	{ mAnswer = answer; }
	public String		getAnswer()					{ return mAnswer; }

	public Faq			categoryId(int categoryId)		{ setCategoryId(categoryId); return this; }
	public void			setCategoryId(int categoryId)	{ mCategoryId = categoryId; }
	public int			getCategoryId() 				{ return mCategoryId; }
	
	public Faq			attachment(InputStream attachment)		{ setAttachment(attachment); return this; }
	public void			setAttachment(InputStream attachment)	{ mAttachment = attachment; }
	public InputStream	getAttachment()							{ return mAttachment; }
}

