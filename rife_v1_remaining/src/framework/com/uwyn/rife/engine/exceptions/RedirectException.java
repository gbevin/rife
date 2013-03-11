/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RedirectException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.LightweightError;

public class RedirectException extends LightweightError implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = -4659026261646459653L;

	private String	mUrl = null;

	public RedirectException(String url)
	{
		super();
		
		mUrl = url;
	}
	
	public String getUrl()
	{
		return mUrl;
	}
}
