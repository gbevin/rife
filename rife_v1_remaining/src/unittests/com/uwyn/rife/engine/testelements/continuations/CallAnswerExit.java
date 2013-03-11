/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallAnswerExit.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class CallAnswerExit extends Element
{
	public void processElement()
	{
		print("the data:"+getInput("somedata")+"\n");
		print("before answer\n");
		answer("the exit's answer\n");
		print("after answer");
	}
}

