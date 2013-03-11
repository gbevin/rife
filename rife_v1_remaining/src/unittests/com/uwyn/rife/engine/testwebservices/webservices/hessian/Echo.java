/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Echo.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testwebservices.webservices.hessian;

public class Echo implements EchoApi
{
	public String echo(String value)
	{
		return "I got : '"+value+"'";
	}
}
