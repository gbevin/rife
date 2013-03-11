/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Source.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.cookies;

import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;

public class Source extends Element
{
	public void processElement()
	{
		if (hasCookie("cookie1") &&
			hasCookie("cookie2") &&
			hasCookie("cookie3"))
		{
			Cookie cookie1 = getCookie("cookie1");
			Cookie cookie2 = getCookie("cookie2");
			Cookie cookie3 = new Cookie("cookie3", cookie1.getValue());
			Cookie cookie4 = new Cookie("cookie4", cookie2.getValue());
			
			setCookie(cookie3);
			setCookie(cookie4);
		}
		
		print("source");
	}
}

