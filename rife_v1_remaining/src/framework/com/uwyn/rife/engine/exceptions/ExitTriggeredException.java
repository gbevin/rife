/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitTriggeredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.LightweightError;

public class ExitTriggeredException extends LightweightError implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = 2960346222846050453L;

	private String	mExitName = null;

	public ExitTriggeredException(String name)
	{
		super();
		
		mExitName = name;
	}
	
	public String getExitName()
	{
		return mExitName;
	}
}
