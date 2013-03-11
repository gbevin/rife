/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputsNonParam.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;

public class InputsNonParam extends Element
{
	public void processElement()
	{
		print(""+getInput("input1"));
		print(""+getInput("input2"));
		print(""+getInput("input3"));
		print("<a href=\""+getSubmissionQueryUrl("submission1")+"\">thelink</a>");
	}
	
	public void doSubmission1()
	{
		print(""+getParameter("parameter1"));
		print(""+getParameter("parameter2"));
		processElement();
	}
}

