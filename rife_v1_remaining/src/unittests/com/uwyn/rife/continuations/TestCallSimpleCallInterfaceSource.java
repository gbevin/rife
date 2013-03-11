/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallSimpleCallInterfaceSource.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallSimpleCallInterfaceSource implements ContinuableObject, ContinuableSupportAware
{
	private ContinuableSupport mSupport;
	public void setContinuableSupport(ContinuableSupport support) { mSupport = support; }
	
	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	private StringBuffer mResult;
	
	public void execute()
	{
		mResult = new StringBuffer("before call\n");
		String answer = (String)mSupport.call(TestCallSimpleCallInterfaceTarget1.class);
		mResult.append(answer);
		mResult.append("after call");
	}
	
	public String getResult()
	{
		return mResult.toString();
	}
}