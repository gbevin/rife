/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ErrorHandler.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;

public class ErrorHandler
{
	private final static Collection<Class>	DEFAULT_EXCEPTION_TYPES;

	static
	{
		Collection<Class> types = new ArrayList<Class>();
		types.add(RuntimeException.class);
		DEFAULT_EXCEPTION_TYPES = Collections.unmodifiableCollection(types);
	}

	private int					mGroupId = -1;

	private String				mDestId = null;
	private ElementInfo			mTarget = null;
	private Collection<Class>	mExceptionTypes = null;

	ErrorHandler(final String destId, final Collection<Class> exceptionTypes)
	{
		assert destId != null;

		mDestId = destId;
		if (null == exceptionTypes ||
			0 == exceptionTypes.size())
		{
			mExceptionTypes = DEFAULT_EXCEPTION_TYPES;
		}
		else
		{
			mExceptionTypes = exceptionTypes;
		}
	}

	String getDestId()
	{
		return mDestId;
	}

	void setTarget(ElementInfo target)
	{
		mTarget = target;
	}
	
	ErrorHandler setGroupId(int groupId)
	{
		assert groupId > -1;

		mGroupId = groupId;

		return this;
	}

	public int getGroupId()
	{
		return mGroupId;
	}

	public ElementInfo getTarget()
	{
		return mTarget;
	}

	public Collection<Class> getExceptionTypes()
	{
		return mExceptionTypes;
	}

	boolean appliesToException(final Throwable exception)
	{
		if (null == exception)
		{
			return false;
		}

		Throwable previous_cause = null;
		Throwable cause = exception;
		while (cause != null && previous_cause != cause)
		{
			for (Class type : mExceptionTypes)
			{
				if (type.isAssignableFrom(cause.getClass()))
				{
					return true;
				}
			}

			previous_cause = cause;
			cause = cause.getCause();
		}

		return false;
	}
}