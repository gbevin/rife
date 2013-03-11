/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.config.exceptions;

public class ConfigsourceNotFoundException extends ConfigErrorException
{
	private static final long serialVersionUID = 4477782139859668000L;

	private String	mConfigSource = null;
	private String	mXmlPath = null;

	public ConfigsourceNotFoundException(String configSource)
	{
		super("Couldn't find a valid resource for config source '"+configSource+"'.");

		mConfigSource = configSource;
	}

	public ConfigsourceNotFoundException(String configSource, String xmlPath)
	{
		super("Couldn't find a valid resource for config source '"+configSource+"', tried xml path '"+xmlPath+"'.");

		mConfigSource = configSource;
		mXmlPath = xmlPath;
	}

	public String getConfigSource()
	{
		return mConfigSource;
	}

	public String getXmlPath()
	{
		return mXmlPath;
	}
}
