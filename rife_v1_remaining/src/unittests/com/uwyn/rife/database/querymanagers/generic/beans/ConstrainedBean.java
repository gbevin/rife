/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConstrainedBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.database.querymanagers.generic.beans.LinkBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class ConstrainedBean extends MetaData
{
	private int 	mIdentifier = -1;
	private Integer mLinkBean = null;
	private String 	mTestString = null;
	
	public ConstrainedBean()
	{
	}
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("identifier").identifier(true));
		addConstraint(new ConstrainedProperty("linkBean").manyToOne(LinkBean.class, "id"));
	}
	
	public void setIdentifier(int identifier)
	{
		mIdentifier = identifier;
	}
	
	public int getIdentifier()
	{
		return mIdentifier;
	}
	
	public void setLinkBean(Integer linkBean)
	{
		mLinkBean = linkBean;
	}
	
	public Integer getLinkBean()
	{
		return mLinkBean;
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

