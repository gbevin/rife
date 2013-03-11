/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalVarUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalVarUnknownException extends EngineException
{
	private static final long serialVersionUID = 5756413265812783173L;

	private String	mGlobalVarName = null;

	public GlobalVarUnknownException(String name)
	{
		super("The global variable '"+name+"' couldn't be found.");
		
		mGlobalVarName = name;
	}
	
	public String getGlobalVarName()
	{
		return mGlobalVarName;
	}
}
