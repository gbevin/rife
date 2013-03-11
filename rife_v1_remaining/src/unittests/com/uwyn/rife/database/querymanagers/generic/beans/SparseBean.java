/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SparseBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class SparseBean extends MetaData
{
	private int 	mId = -1;
	private String 	mTestString = null;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("id").identifier(true).sparse(true));
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
}

