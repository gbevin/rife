/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UniqueBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

public class UniqueBean extends Validation
{
	private int 	mIdentifier = -1;
	private String 	mTestString = null;
	private String 	mAnotherString = null;
	private String 	mThirdString = null;
	
	public UniqueBean()
	{
	}
	
	protected void activateValidation()
	{
		addConstraint(new ConstrainedProperty("identifier").identifier(true));
		addGroup("group1")
			.addConstraint(new ConstrainedProperty("testString").unique(true).maxLength(20).notNull(true))
			.addConstraint(new ConstrainedProperty("anotherString").maxLength(20).notNull(true));
		addGroup("group2")
			.addConstraint(new ConstrainedProperty("thirdString").maxLength(20));
		
		addConstraint(new com.uwyn.rife.site.ConstrainedBean().unique("anotherString", "thirdString"));
	}
	
	public void setIdentifier(int identifier)
	{
		mIdentifier = identifier;
	}
	
	public int getIdentifier()
	{
		return mIdentifier;
	}
	
	public void setTestString(String testString)
	{
		this.mTestString = testString;
	}
	
	public String getTestString()
	{
		return mTestString;
	}
	
	public void setAnotherString(String anotherString)
	{
		mAnotherString = anotherString;
	}
	
	public String getAnotherString()
	{
		return mAnotherString;
	}
	
	public void setThirdString(String thirdString)
	{
		mThirdString = thirdString;
	}
	
	public String getThirdString()
	{
		return mThirdString;
	}
}

