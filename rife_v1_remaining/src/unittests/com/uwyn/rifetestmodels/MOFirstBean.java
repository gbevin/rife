/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MOFirstBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rifetestmodels;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class MOFirstBean extends MetaData
{
	private Integer 		mIdentifier;
	private MOSecondBean	mSecondBean = null;
	private MOSecondBean	mSecondBean2 = null;
	private MOThirdBean		mThirdBean = null;
	private String 			mFirstString = null;
	
	public MOFirstBean()
	{
	}
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("identifier").identifier(true));
		addConstraint(new ConstrainedProperty("secondBean").manyToOne());
		addConstraint(new ConstrainedProperty("secondBean2").manyToOne(MOSecondBean.class, "identifier"));
		addConstraint(new ConstrainedProperty("thirdBean").manyToOne(MOThirdBean.class));
	}
	
	public void setIdentifier(Integer identifier)
	{
		mIdentifier = identifier;
	}
	
	public Integer getIdentifier()
	{
		return mIdentifier;
	}
	
	public void setSecondBean(MOSecondBean secondBean)
	{
		mSecondBean = secondBean;
	}
	
	public MOSecondBean getSecondBean()
	{
		return mSecondBean;
	}
	
	public void setSecondBean2(MOSecondBean secondBean)
	{
		mSecondBean2 = secondBean;
	}
	
	public MOSecondBean getSecondBean2()
	{
		return mSecondBean2;
	}
	
	public void setThirdBean(MOThirdBean thirdBean)
	{
		mThirdBean = thirdBean;
	}
	
	public MOThirdBean getThirdBean()
	{
		return mThirdBean;
	}
	
	public void setFirstString(String firstString)
	{
		mFirstString = firstString;
	}
	
	public String getFirstString()
	{
		return mFirstString;
	}
	
	public static String getStaticFirstString()
	{
		return "test";
	}
}
