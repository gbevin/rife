/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalCookieDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;
 
import com.uwyn.rife.engine.Element;
import javax.servlet.http.Cookie;
 
public class GlobalCookieDestination extends Element
{
        public void processElement()
        {
                Cookie cookie1 = getCookie("cookie1");
                Cookie cookie2 = getCookie("cookie2");
                Cookie cookie3 = getCookie("cookie3");
 
                print(cookie1.getValue()+","+cookie2.getValue()+","+cookie3.getValue());
        }
}

