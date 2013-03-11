/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalCookieDefaults.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.engine.Element;

public class GlobalCookieDefaults extends Element
{
	public void processElement()
	{
		if (hasCookie("defcookie1"))
		{
			print("defcookie1 : "+getCookie("defcookie1").getValue());
		}
		if (hasCookie("defcookie2"))
		{
			print("defcookie2 : "+getCookie("defcookie2").getValue());
		}
		if (hasCookie("defcookie3"))
		{
			print("defcookie3 : "+getCookie("defcookie3").getValue());
		}
	}
}
