/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalCookieSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;
 
import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;
 
public class GlobalCookieSource extends Element
{
        public void processElement()
        {
			if (hasCookie("cookie1") &&
					hasCookie("cookie2") &&
					hasCookie("cookie3"))
			{
					Cookie cookie1 = getCookie("cookie1");
					Cookie cookie2 = getCookie("cookie2");
					Cookie cookie3 = getCookie("cookie3");

					cookie1.setValue("cookie4");
					cookie2.setValue("cookie5");
					cookie3.setValue("cookie6");

					setCookie(cookie1);
					setCookie(cookie2);
					setCookie(cookie3);
			}

			print("source");
        }
}

