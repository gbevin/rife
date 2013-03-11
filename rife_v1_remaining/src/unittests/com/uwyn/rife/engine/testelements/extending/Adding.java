/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Adding.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.extending;

import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;

public class Adding extends Element
{
	public void processElement()
	{
		if (hasInputValue("switch"))
		{
			setOutput("output1", "output1"+getInput("switch"));
			setOutput("output3", "output3"+getInput("switch"));
			exit(getInput("switch"));
		}
		
		print("Adding");
		print(getPropertyString("property1"));
		print(getPropertyString("property2"));
		print(getPropertyString("property3"));
		print(getPropertyString("property4"));
		print(getInput("input1"));
		print(getInput("input2"));
		print(getInput("input3"));
		print(getInput("input4"));
		print(getCookie("incookie1").getValue());
		print(getCookie("incookie2").getValue());
		print(getCookie("incookie3").getValue());
		print(getCookie("incookie4").getValue());
		if (hasSubmission("submission1"))
		{
			print(getParameter("param1"));
			print(getParameter("param2"));
		}
		if (hasSubmission("submission2"))
		{
			print(getParameter("param1"));
			print(getParameter("param2"));
		}
		setCookie(new Cookie("outcookie1", "outcookie1elementvalue"));
		setCookie(new Cookie("outcookie3", "outcookie3elementvalue"));
	}
}

