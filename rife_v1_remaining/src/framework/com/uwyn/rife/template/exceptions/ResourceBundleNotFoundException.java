/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceBundleNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class ResourceBundleNotFoundException extends ProcessingException
{
	private static final long serialVersionUID = -4573009433238119230L;

	private String	mTemplateName = null;
	private String	mValueTag = null;
	private String 	mBundleName = null;

	public ResourceBundleNotFoundException(String templateName, String valueTag, String bundleName)
	{
		super("Couldn't find the resource bundle '"+bundleName+"' in template '"+templateName+"' while processing the filtered value tag '"+valueTag+"'.");

		mTemplateName = templateName;
		mValueTag = valueTag;
		mBundleName = bundleName;
	}

	public String getTemplateName()
	{
		return mTemplateName;
	}

	public String getValueTag()
	{
		return mValueTag;
	}

	public String getBundleName()
	{
		return mBundleName;
	}
}