/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentRaw.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;
import java.io.InputStream;

public class ContentRaw extends Validation
{
	private int 		mId = -1;
	private String 		mName = null;
	private InputStream	mRaw = null;

	public ContentRaw()
	{
	}

	protected void activateValidation()
	{
		addConstraint(new ConstrainedProperty("name")
					  	.maxLength(64)
					  	.notNull(true)
					  	.notEmpty(true));
		addConstraint(new ConstrainedProperty("raw")
					  	.notNull(true)
					  	.mimeType(MimeType.RAW));
	}

	public void setId(int id)
	{
		mId = id;
	}

	public int getId()
	{
		return mId;
	}

	public void setName(String name)
	{
		mName = name;
	}

	public String getName()
	{
		return mName;
	}

	public ContentRaw name(String name)
	{
		mName = name;

		return this;
	}

	public InputStream getRaw()
	{
		return mRaw;
	}

	public void setRaw(InputStream image)
	{
		mRaw = image;
	}

	public ContentRaw raw(InputStream raw)
	{
		mRaw = raw;

		return this;
	}
}
