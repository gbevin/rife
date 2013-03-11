/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Typed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.outputs;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class Typed extends Element
{
	public void processElement()
	{
		if (hasInputValue("input1"))
		{
			print(StringUtils.join(getInputValues("input1"), ","));
			print(StringUtils.join(getInputValues("input2"), ","));
			print(StringUtils.join(getInputValues("input3"), ","));
			print(StringUtils.join(getInputValues("input4"), ","));
			print(StringUtils.join(getInputValues("input5"), ","));
			print(StringUtils.join(getInputValues("input6"), ","));
			print(StringUtils.join(getInputValues("input7"), ","));
			return;
		}

		setOutput("outputstring", "astring");
		setOutput("outputchar", 'U');
		setOutput("outputchararray", new char[]{'b', 'k', 'o'});
		setOutput("outputint", Integer.MAX_VALUE);
		setOutput("outputlong", Long.MAX_VALUE);
		setOutput("outputdouble", 34798.43);
		setOutput("outputfloat", 43.18f);
		
		addOutputValue("outputstring", "astring2");
		addOutputValues("outputstring", new String[] {"astring3", "astring4"});
		addOutputValue("outputchar", 'V');
		addOutputValue("outputchararray", new char[]{'k', 'k', 'l'});
		addOutputValue("outputint", 78327);
		addOutputValue("outputlong", 83764987398L);
		addOutputValue("outputdouble", 893749.56);
		addOutputValue("outputfloat", 87.34f);

		exit("exit");
	}
}

