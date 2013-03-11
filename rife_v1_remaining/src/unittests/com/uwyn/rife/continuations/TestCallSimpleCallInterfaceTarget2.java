/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallSimpleCallInterfaceTarget2.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallSimpleCallInterfaceTarget2 implements ContinuableObject, ContinuableSupportAware
{
	private ContinuableSupport mSupport;
	public void setContinuableSupport(ContinuableSupport support) { mSupport = support; }
	
	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	public void execute()
	{
		String answer = "during call target 2\n";
		mSupport.answer(answer);
	}
}