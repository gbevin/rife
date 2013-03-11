/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AnnotationsDest.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InputProperty;

@Elem
public class AnnotationsDest extends Element
{
	private String	mInput1;
	private String	mInput2;
	
	@InputProperty
	public void setInput1(String value)
	{
		mInput1 = value;
	}
	
	@InputProperty
	public void setInput2(String value)
	{
		mInput2 = value;
	}

	public void processElement()
	{
		print(mInput1+":"+mInput2);
	}
}

