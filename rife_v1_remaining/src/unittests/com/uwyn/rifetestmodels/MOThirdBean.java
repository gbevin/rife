/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MOThirdBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rifetestmodels;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class MOThirdBean extends MetaData
{
	private Integer	mId;
//	private Collection<MOFirstBean> mFirstBeans;
	private String 	mThirdString;
	
	public MOThirdBean()
	{
	}
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("id").identifier(true));
//		addConstraint(new ConstrainedProperty("firstBeans").manyToManyAssociation());
	}
	
	public void setId(Integer id)
	{
		mId = id;
	}
	
	public Integer getId()
	{
		return mId;
	}
	
//	public void setFirstBeans(Collection<MOFirstBean> firstBeans)
//	{
//		mFirstBeans = firstBeans;
//	}
//	
//	public Collection<MOFirstBean> getFirstBeans()
//	{
//		return mFirstBeans;
//	}
	
	public void setThirdString(String thirdString)
	{
		mThirdString = thirdString;
	}
	
	public String getThirdString()
	{
		return mThirdString;
	}
}

