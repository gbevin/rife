/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ScriptedEngine.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;

abstract class ScriptedEngine
{
	protected ElementScripted	mElement = null;
	protected String			mLanguage = null;
	protected String			mCode = null;

	ScriptedEngine(String language, String code)
	throws EngineException
	{
		mLanguage = language;
		mCode = code;
	}
	
	void setElement(ElementScripted element)
	{
		mElement = element;
	}
	
	ElementScripted getElement()
	{
		return mElement;
	}
	
	String getLanguage()
	{
		return mLanguage;
	}
	
	String getCode()
	{
		return mCode;
	}
	
	abstract void processElement();
	abstract boolean childTriggered(String name, String[] values);
}

