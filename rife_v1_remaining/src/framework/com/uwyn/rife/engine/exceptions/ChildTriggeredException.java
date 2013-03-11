/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ChildTriggeredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.LightweightError;

public class ChildTriggeredException extends LightweightError implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = -4327850854725968145L;

	private String		mChildTriggerName = null;
	private String[]	mChildTriggerValues = null;
	
	public ChildTriggeredException(String childTriggerName, String[] childTriggerValues)
	{
		super();
		mChildTriggerName = childTriggerName;
		mChildTriggerValues = childTriggerValues;
	}
	
	public String getChildTriggerName()
	{
		return mChildTriggerName;
	}
	
	public String[] getChildTriggerValues()
	{
		return mChildTriggerValues;
	}
}

