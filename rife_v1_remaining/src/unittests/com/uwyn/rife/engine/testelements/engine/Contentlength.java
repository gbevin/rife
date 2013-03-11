/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Contentlength.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import java.io.IOException;
import java.io.OutputStream;

import com.uwyn.rife.engine.Element;

public class Contentlength extends Element
{
	public void processElement()
	{
		String out = "this goes out";
		setContentLength(out.length());
		OutputStream outputstream = getOutputStream();
		try
		{
			outputstream.write(out.getBytes("ISO-8859-1"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

