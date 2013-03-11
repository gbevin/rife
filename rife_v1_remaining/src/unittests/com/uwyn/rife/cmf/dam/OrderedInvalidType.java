/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OrderedInvalidType.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

public class OrderedInvalidType extends Validation<ConstrainedBean, ConstrainedProperty>
{
	private int 		mId = -1;
	private String 		mName = null;
	private String 		mPriority = null;

	public OrderedInvalidType()
	{
	}

	protected void activateValidation()
	{
		addConstraint(new ConstrainedProperty("name").maxLength(64).notNull(true).notEmpty(true));
		addConstraint(new ConstrainedProperty("priority").rangeBegin(0).ordinal(true));
	}

	public void setId(int id)
	{
		mId = id;
	}

	public int getId()
	{
		return mId;
	}

	public void setPriority(String priority)
	{
		mPriority = priority;
	}

	public String getPriority()
	{
		return mPriority;
	}

	public OrderedInvalidType priority(String priority)
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

	public OrderedInvalidType name(String name)
	{
		mName = name;

		return this;
	}
}
