/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Regular.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

public class Regular
{
	private int		mId = -1;
	private String	mTitle = null;
	private String	mText = null;

	public Regular	id(int id)		{ setId(id); return this; }
	public void		setId(int id)	{ mId = id; }
	public int		getId() 		{ return mId; }

	public Regular	title(String title)		{ setTitle(title); return this; }
	public void		setTitle(String title)	{ mTitle = title; }
	public String	getTitle()				{ return mTitle; }

	public Regular	text(String text)		{ setText(text); return this; }
	public void		setText(String text)	{ mText = text; }
	public String	getText()				{ return mText; }
}
