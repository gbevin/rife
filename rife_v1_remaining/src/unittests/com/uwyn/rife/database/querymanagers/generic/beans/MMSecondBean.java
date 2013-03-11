/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MMSecondBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;
import java.util.Collection;

public class MMSecondBean extends MetaData
{
	private Integer	mIdentifier;
	private Collection<MMFirstBean> mFirstBeans;
	private String 	mSecondString;
	
	public MMSecondBean()
	{
	}
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("identifier").identifier(true));
		addConstraint(new ConstrainedProperty("firstBeans").manyToManyAssociation());
	}
	
	public void setIdentifier(Integer identifier)
	{
		mIdentifier = identifier;
	}
	
	public Integer getIdentifier()
	{
		return mIdentifier;
	}
	
	public void setFirstBeans(Collection<MMFirstBean> firstBeans)
	{
		mFirstBeans = firstBeans;
	}
	
	public Collection<MMFirstBean> getFirstBeans()
	{
		return mFirstBeans;
	}
	
	public void setSecondString(String secondString)
	{
		mSecondString = secondString;
	}
	
	public String getSecondString()
	{
		return mSecondString;
	}
}

