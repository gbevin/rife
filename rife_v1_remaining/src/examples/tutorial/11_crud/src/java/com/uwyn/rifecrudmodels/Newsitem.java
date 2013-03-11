/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Newsitem.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rifecrudmodels;

import java.util.Date;

public class Newsitem
{
	private int		mId = -1;
	private Date	mMoment = new Date();
	private byte[]	mImage = null;
	private String	mTitle = null;
	private String	mBody = null;
	private String	mExcerpt = null;
	private String	mExtended = null;
	private String	mSource = null;
	private boolean	mDraft = false;
	
	public Newsitem	id(int id)		{ setId(id); return this; }
	public void		setId(int id)	{ mId = id; }
	public int		getId()			{ return mId; }
	
	public Newsitem	moment(Date moment)		{ setMoment(moment); return this; }
	public void		setMoment(Date moment)	{ mMoment = moment; }
	public Date		getMoment()				{ return mMoment; }
	
	public Newsitem	title(String title)		{ setTitle(title); return this; }
	public void		setTitle(String title)	{ mTitle = title; }
	public String	getTitle()				{ return mTitle; }
	
	public Newsitem	body(String body)		{ setBody(body); return this; }
	public void		setBody(String body)	{ mBody = body; }
	public String	getBody()				{ return mBody; }
	
	public Newsitem	excerpt(String excerpt)		{ setExcerpt(excerpt); return this; }
	public void		setExcerpt(String excerpt)	{ mExcerpt = excerpt; }
	public String	getExcerpt()				{ return mExcerpt; }
	
	public Newsitem	extended(String extended)		{ setExtended(extended); return this; }
	public void		setExtended(String extended)	{ mExtended = extended; }
	public String	getExtended()					{ return mExtended; }
	
	public Newsitem image(byte[] image)		{ setImage(image); return this; }
	public void 	setImage(byte[] image)	{ mImage = image; }
	public byte[]	getImage()				{ return mImage; }
	
	public Newsitem	source(String source)		{ setSource(source); return this; }
	public void		setSource(String source)	{ mSource = source; }
	public String	getSource()					{ return mSource; }
	
	public Newsitem	draft(boolean draft)	{ setDraft(draft); return this; }
	public void		setDraft(boolean draft)	{ mDraft = draft; }
	public boolean	isDraft()				{ return mDraft; }
}
