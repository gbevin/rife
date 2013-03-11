/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SuccessiveGlobalcookie2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

public class SuccessiveGlobalcookie2 extends Element
{
	public void processElement()
	{
		if (hasCookie("cookie1"))
		{
			print(getCookie("cookie1").getValue());
		}
		else
		{
			print("no cookie1");
		}
		if (hasCookie("cookie2"))
		{
			print(getCookie("cookie2").getValue());
		}
		else
		{
			print("no cookie2");
		}
		if (hasCookie("cookie3"))
		{
			print(getCookie("cookie3").getValue());
		}
		else
		{
			print("no cookie3");
		}
	}
}

