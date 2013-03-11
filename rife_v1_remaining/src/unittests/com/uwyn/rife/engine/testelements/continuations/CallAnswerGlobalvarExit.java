/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallAnswerGlobalvarExit.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class CallAnswerGlobalvarExit extends Element
{
	public void processElement()
	{
		print("the data:"+getInput("globalvar")+"\n");
		print("before answer\n");
		setOutput("globalvar", getInput("globalvar")+",beforeanswer");
		answer("the exit's answer\n");
		print("after answer");
	}
}

