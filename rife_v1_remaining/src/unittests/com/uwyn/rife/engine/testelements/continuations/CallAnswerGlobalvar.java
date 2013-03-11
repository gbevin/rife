/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallAnswerGlobalvar.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class CallAnswerGlobalvar extends Element
{
	public void processElement()
	{
		String before = "before call";
		String after = "after call";
		
		print(before+"\n"+getContinuationId()+"\n");
		setOutput("globalvar", "beforecall");
		print(call("exit"));
		print("the data:"+StringUtils.join(getOutput("globalvar"), ",")+"\n");
		print(after);
	}
}

