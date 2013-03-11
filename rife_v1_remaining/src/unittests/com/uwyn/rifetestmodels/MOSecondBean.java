/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MOSecondBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rifetestmodels;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;
import java.util.Collection;

public class MOSecondBean extends MetaData
{
	private Integer	mIdentifier;
	private Collection<MOFirstBean> mFirstBeans;
	private String 	mSecondString;
	
	public MOSecondBean()
	{
	}
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("identifier").identifier(true));
		addConstraint(new ConstrainedProperty("firstBeans").manyToOneAssociation());
	}
	
	public void setIdentifier(Integer identifier)
	{
		mIdentifier = identifier;
	}
	
	public Integer getIdentifier()
	{
		return mIdentifier;
	}
	
	public void setFirstBeans(Collection<MOFirstBean> firstBeans)
	{
		mFirstBeans = firstBeans;
	}
	
	public Collection<MOFirstBean> getFirstBeans()
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

