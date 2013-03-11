/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SimpleBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.database.SomeEnum;
import java.util.UUID;

public class SimpleBean
{
	private int 		mId = -1;
	private int 		mLinkBean = -1;
	private String 		mTestString = null;
	private UUID		mUuid = null;
	private SomeEnum	mEnum;
	
	public void setLinkBean(int linkBean)
	{
		mLinkBean = linkBean;
	}
	
	public int getLinkBean()
	{
		return mLinkBean;
	}
	
	public void setId(int id)
	{
		mId = id;
	}
	
	public int getId()
	{
		return mId;
	}
	
	public void setTestString(String testString)
	{
		this.mTestString = testString;
	}
	
	public String getTestString()
	{
		return mTestString;
	}
	
	public String toString()
	{
		return mId + ";" + mLinkBean + ";" + mTestString;
	}

	public UUID getUuid()
	{
		return mUuid;
	}

	public void setUuid(UUID uuid)
	{
		mUuid = uuid;
	}
	
	public void setEnum(SomeEnum e)
	{
		mEnum = e;
	}
	
	public SomeEnum getEnum()
	{
		return mEnum;
	}
}
