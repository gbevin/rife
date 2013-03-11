/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractValidationRule.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.logging.Logger;

import com.uwyn.rife.tools.ExceptionUtils;

public abstract class AbstractValidationRule implements ValidationRule
{
	private Object	mBean = null;
	private	String	mCachedSubject = null;
	
	protected AbstractValidationRule()
	{
	}
	
	abstract public boolean validate();
	abstract public ValidationError getError();
	
	public String getSubject()
	{
		if (null == mCachedSubject)
		{
			mCachedSubject = getError().getSubject();
		}
		
		return mCachedSubject;
	}
	
	public <T extends ValidationRule> T setBean(Object bean)
	{
		mBean = bean;
		
		return (T)this;
	}
	
	public Object getBean()
	{
		return mBean;
	}

	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.site").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
			///CLOVER:ON
		}
	}
}
