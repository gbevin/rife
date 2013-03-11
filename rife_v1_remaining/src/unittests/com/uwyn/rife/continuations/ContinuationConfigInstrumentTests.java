/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationConfigInstrumentTests.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

public class ContinuationConfigInstrumentTests implements ContinuationConfigInstrument
{
	public String getContinuableMarkerInterfaceName()
	{
		return ContinuableObject.class.getName();
	}
	
	public String getContinuableSupportClassName()
	{
		return ContinuableSupport.class.getName();
	}
	
	public String getEntryMethodName()
	{
		return "execute";
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
		return "stepback";
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
		return new Class[] {Class.class};
	}
	
	public String getAnswerMethodName()
	{
		return "answer";
	}
}
