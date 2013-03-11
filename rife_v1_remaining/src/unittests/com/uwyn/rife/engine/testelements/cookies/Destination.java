/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Destination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.cookies;

import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;

public class Destination extends Element
{
	public void processElement()
	{
		Cookie cookie2 = getCookie("cookie2");
		Cookie cookie3 = getCookie("cookie3");
		Cookie cookie4 = getCookie("cookie4");
		
		print(cookie2.getValue()+","+cookie3.getValue()+","+cookie4.getValue());
	}
}

