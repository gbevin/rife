/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutcookiesOutjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.cookies;

import com.uwyn.rife.engine.Element;

public class OutcookiesOutjection extends Element
{
	public String getFirstname()
	{
		return "John";
	}
	
	public String getLastname()
	{
		return "Darryl";
	}

	public void processElement()
	{
		print(getHtmlTemplate("engine_outcookies_generation"));
	}
}

