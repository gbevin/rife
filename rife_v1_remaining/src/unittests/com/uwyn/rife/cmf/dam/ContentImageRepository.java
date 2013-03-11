/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentImageRepository.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.cmf.CmfProperty;
import com.uwyn.rife.cmf.CmfValidation;
import com.uwyn.rife.cmf.MimeType;

public class ContentImageRepository extends CmfValidation
{
	private int 		mId = -1;
	private String 		mName = null;
	private byte[]		mImage = null;

	public ContentImageRepository()
	{
	}

	protected void activateValidation()
	{
		addConstraint(new CmfProperty("name")
					  	.maxLength(64)
					  	.notNull(true)
					  	.notEmpty(true));
		addConstraint(new CmfProperty("image")
					  	.notNull(true)
					  	.mimeType(MimeType.IMAGE_PNG)
						.name("myimage.png")
						.repository("testrep"));
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

	public ContentImageRepository name(String name)
	{
		mName = name;

		return this;
	}

	public byte[] getImage()
	{
		return mImage;
	}

	public void setImage(byte[] image)
	{
		mImage = image;
	}

	public ContentImageRepository image(byte[] image)
	{
		mImage = image;

		return this;
	}
}
