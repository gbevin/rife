/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Headers.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;
import java.util.Calendar;
import java.util.TimeZone;

public class Headers extends Element
{
	public void processElement()
	{
		addHeader("Content-Disposition", "attachment; filename=thefile.zip");
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(2002, 9, 25, 19, 20, 58);
		addDateHeader("DateHeader", cal.getTimeInMillis());
		addIntHeader("IntHeader", 1212);
	}
}

