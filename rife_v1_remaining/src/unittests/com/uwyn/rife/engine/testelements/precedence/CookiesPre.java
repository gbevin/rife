/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CookiesPre.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.precedence;

import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;

public class CookiesPre extends Element
{
	public void processElement()
	{
		setCookie(new Cookie("cookie1", "cookie1_prevalue"));
		setCookie(new Cookie("cookie3", "cookie3_prevalue"));
		setCookie(new Cookie("cookie4", "cookie4_prevalue"));
		setCookie(new Cookie("cookie5", "cookie5_prevalue"));
	}
}

