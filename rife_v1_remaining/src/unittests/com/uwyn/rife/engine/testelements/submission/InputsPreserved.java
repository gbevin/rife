/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputsPreserved.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

public class InputsPreserved extends Generatedurl
{
	public final static String SUBMISSION_NAME1 = "submission5";

	public void processElement()
	{
		super.processElement();
		
		print(""+getInput("input1"));
		print(""+getInput("input2"));
		print(""+getInput("input3"));
	}
}

