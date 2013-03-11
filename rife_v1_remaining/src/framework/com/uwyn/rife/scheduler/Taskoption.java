/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Taskoption.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.site.Validation;
import com.uwyn.rife.site.ValidationRuleNotNull;
import com.uwyn.rife.site.ValidationRuleRange;

public class Taskoption extends Validation implements Cloneable
{
	private int		mTaskId = -1;
	private String	mName = null;
	private String	mValue = null;

	public Taskoption()
	{
	}
	
	protected void activateValidation()
	{
		addRule(new ValidationRuleRange("taskId", new Integer(0), null));
		addRule(new ValidationRuleNotNull("name"));
		addRule(new ValidationRuleNotNull("value"));
	}

	public void setTaskId(int taskid)
	{
		mTaskId = taskid;
	}

	public int getTaskId()
	{
		return mTaskId;
	}

	public void setName(String name)
	{
		if (null == name && 0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		mName = name;
	}

	public String getName()
	{
		return mName;
	}

	public void setValue(String value)
	{
		mValue = value;
	}

	public String getValue()
	{
		return mValue;
	}

	public Taskoption clone()
	throws CloneNotSupportedException
	{
		return (Taskoption)super.clone();
	}

	public boolean equals(Object object)
	{
		Taskoption other_taskoption = (Taskoption)object;
		
		if (null != other_taskoption &&
			other_taskoption.getTaskId() == getTaskId() &&
			other_taskoption.getName().equals(getName()) &&
			other_taskoption.getValue().equals(getValue()))
		{
			return true;
		}
		
		return false;
	}
}
