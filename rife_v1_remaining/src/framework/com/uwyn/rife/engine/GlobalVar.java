/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalVar.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

public class GlobalVar
{
	private int			mGroupId = -1;
	
	private String[]	mDefaultValues = null;
	
	GlobalVar(String[] defaultValues)
	{
		if (defaultValues != null && 0 == defaultValues.length)
		{
			defaultValues = null;
		}

		mDefaultValues = defaultValues;
	}
	
	GlobalVar setGroupId(int groupId)
	{
		assert groupId > -1;
		
		mGroupId = groupId;
		
		return this;
	}
	
	public int getGroupId()
	{
		return mGroupId;
	}
	
	public String[] getDefaultValues()
	{
		return mDefaultValues;
	}
}

