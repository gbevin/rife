/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CookiesOutjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;

public class CookiesOutjection extends Element
{
	public String getCookie1()
	{
		return "value 1";
	}
	
	public String getCookie2()
	{
		return "value 2";
	}
	
	public String getCookie3()
	{
		return "value 3";
	}
	
	public String getCookie4()
	{
		return "value 4";
	}
		
	public void processElement()
	{
		print(getHtmlTemplate("engine_embedding_cookies_outjection"));
	}
}

