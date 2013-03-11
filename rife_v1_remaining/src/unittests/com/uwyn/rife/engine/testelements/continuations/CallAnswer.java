/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallAnswer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class CallAnswer extends Element
{
	public void processElement()
	{
		String before = "before call";
		String after = "after call";
		
		print(before+"\n"+getContinuationId()+"\n");
		setOutput("somedata", "somevalue");
		print(call("exit"));
		print(after);
	}
}

