/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalCookieSource2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;
 
import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;

public class GlobalCookieSource2 extends Element
{
	public void processElement()
	{
		Cookie cookie1 = getCookie("cookie1");
		Cookie cookie2 = getCookie("cookie2");
		Cookie cookie3 = new Cookie("cookie3", "invalidcookie");

		cookie1.setValue("validcookie");
		cookie2.setValue("validcookie");

		setCookie(cookie1);
		setCookie(cookie2);
		setCookie(cookie3);

		print("source2");
	}
}

