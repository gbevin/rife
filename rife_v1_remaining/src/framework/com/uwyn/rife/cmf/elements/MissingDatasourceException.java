/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingDatasourceException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.elements;

import com.uwyn.rife.engine.exceptions.EngineException;

public class MissingDatasourceException extends EngineException
{
	private static final long serialVersionUID = -2245599644065981426L;

	private String	mId = null;

	public MissingDatasourceException(String id)
	{
		super("The serve content element '"+id+"' couldn't obtain a datasource, either set the 'datasource' property with a Datasource instance, or set the 'DATASOURCE' config value to the datasource name that has to be used or define a datasource with the name 'datasource'.");

		mId = id;
	}

	public String getId()
	{
		return mId;
	}
}
