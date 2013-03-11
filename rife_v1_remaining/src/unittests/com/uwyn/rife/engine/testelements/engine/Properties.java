/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Properties.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;

public class Properties extends Element
{
	private String mProperty4 = null;
	
	public void setProperty4(String property)	{ mProperty4 = property; }
	
	public void processElement()
	{
		print("Property 1 = "+getProperty("property1"));
		print("Property 2 = "+getProperty("property2"));
		print("Property 3 = "+getProperty("property3"));
		print("Property 4 = "+mProperty4);
	}
}

