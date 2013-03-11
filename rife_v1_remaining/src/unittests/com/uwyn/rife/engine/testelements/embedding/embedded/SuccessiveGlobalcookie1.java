/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SuccessiveGlobalcookie1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;

public class SuccessiveGlobalcookie1 extends Element
{
	public void processElement()
	{
		setCookie(new Cookie("cookie1", "embedded value 1"));
		setCookie(new Cookie("cookie2", "embedded value 2"));
		setCookie(new Cookie("cookie4", "embedded value 4"));
		setCookie(new Cookie("cookie5", "embedded value 5"));
		print("embedded");
	}
}

