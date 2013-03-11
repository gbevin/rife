/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BaseSimple.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.annotations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InputProperty;

@Elem(
	id = "ELEMENTBASE",
	url = "test/elementbase",
	contentType = "text/xml"
)
public abstract class BaseSimple extends Element
{
	protected int mInput1;
	protected String mInput2;

	@InputProperty
	public void setInput1(int input1)
	{
		mInput1 = input1;
	}

	@InputProperty
	public void setInput2(String input2)
	{
		mInput2 = input2;
	}
}
