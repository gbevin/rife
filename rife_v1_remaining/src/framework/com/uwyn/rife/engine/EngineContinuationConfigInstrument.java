/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineContinuationConfigInstrument.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;

public class EngineContinuationConfigInstrument implements ContinuationConfigInstrument
{
	public String getContinuableMarkerInterfaceName()
	{
		// this method should never be called since the engine doesn't use
		// the ContinuableDetectionClassVisitor, but the
		// ElementDetectionClassVisitor instead
		throw new UnsupportedOperationException();
	}
	
	public String getContinuableSupportClassName()
	{
		return ElementSupport.class.getName();
	}	
	
	public String getEntryMethodName()
	{
		return "processElement";
	}
	
	public Class getEntryMethodReturnType()
	{
		return void.class;
	}
	
	public Class[] getEntryMethodArgumentTypes()
	{
		return null;
	}
	
	public String getPauseMethodName()
	{
		return "pause";
	}
	
	public String getStepbackMethodName()
	{
		return "stepBack";
	}
	
	public String getCallMethodName()
	{
		return "call";
	}
	
	public Class getCallMethodReturnType()
	{
		return Object.class;
	}
	
	public Class[] getCallMethodArgumentTypes()
	{
		return new Class[] {String.class};
	}
		
	public String getAnswerMethodName()
	{
		return "answer";
	}
}
