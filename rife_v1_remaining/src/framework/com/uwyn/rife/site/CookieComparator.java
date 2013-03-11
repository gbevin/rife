/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CookieComparator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.Comparator;

import javax.servlet.http.Cookie;

public class CookieComparator implements Comparator
{
	public int compare(Object object1, Object object2)
	{
		return ((Cookie)object1).getName().compareTo(((Cookie)object2).getName());
	}
}
