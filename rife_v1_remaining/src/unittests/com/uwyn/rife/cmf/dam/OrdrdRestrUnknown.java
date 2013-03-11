/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OrdrdRestrUnknown.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

public class OrdrdRestrUnknown extends Validation<ConstrainedBean, ConstrainedProperty>
{
	private int 	mId = -1;
	private String 	mName = null;
	private int 	mPriority = -1;
	private int 	mRestricted = -1;

	public OrdrdRestrUnknown()
	{
		mPriority = 0;
	}

	protected void activateValidation()
	{
		addConstraint(new ConstrainedProperty("name").maxLength(64).notNull(true).notEmpty(true));
		addConstraint(new ConstrainedProperty("restricted").rangeBegin(0));
		addConstraint(new ConstrainedProperty("priority").rangeBegin(0).ordinal(true, "restrictedunknown"));
	}

	public void setId(int id)
	{
		mId = id;
	}

	public int getId()
	{
		return mId;
	}

	public void setPriority(int priority)
	{
		mPriority = priority;
	}

	public int getPriority()
	{
		return mPriority;
	}

	public OrdrdRestrUnknown priority(int priority)
	{
		mPriority = priority;

		return this;
	}

	public void setName(String name)
	{
		mName = name;
	}

	public String getName()
	{
		return mName;
	}

	public OrdrdRestrUnknown name(String name)
	{
		mName = name;

		return this;
	}

	public int getRestricted()
	{
		return mRestricted;
	}

	public void setRestricted(int restricted)
	{
		mRestricted = restricted;
	}

	public OrdrdRestrUnknown restricted(int restricted)
	{
		setRestricted(restricted);

		return this;
	}
}
