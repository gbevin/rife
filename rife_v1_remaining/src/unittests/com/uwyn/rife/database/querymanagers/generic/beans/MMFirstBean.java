/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MMFirstBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;
import java.util.Collection;

public class MMFirstBean extends MetaData
{
	private Integer mIdentifier;
	private Collection<MMSecondBean> mSecondBeans;
	private String mFirstString;
	
	public MMFirstBean()
	{
	}
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("identifier").identifier(true));
		addConstraint(new ConstrainedProperty("secondBeans").manyToMany());
	}
	
	public void setIdentifier(Integer identifier)
	{
		mIdentifier = identifier;
	}
	
	public Integer getIdentifier()
	{
		return mIdentifier;
	}
	
	public void setSecondBeans(Collection<MMSecondBean> secondBeans)
	{
		mSecondBeans = secondBeans;
	}
	
	public Collection<MMSecondBean> getSecondBeans()
	{
		return mSecondBeans;
	}
	
	public void setFirstString(String firstString)
	{
		mFirstString = firstString;
	}
	
	public String getFirstString()
	{
		return mFirstString;
	}
}

