/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SimpleInterface.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.ElementAware;
import com.uwyn.rife.engine.ElementSupport;

public class SimpleInterface implements ElementAware, Cloneable
{
	private ElementSupport	mElement = null;
	
	public void noticeElement(ElementSupport element)
	{
		mElement = element;
	}
	
	public void processElement()
	{
		String before = "before simple pause";
		String after = "after simple pause";
		
		mElement.print(before+"\n"+mElement.getContinuationId());
		mElement.pause();
		mElement.print(after);
	}
	
	public Object clone() 
	throws CloneNotSupportedException
	{
		return super.clone();
	}
}

