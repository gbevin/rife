/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RequestMethod.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.datastructures.EnumClass;

public class RequestMethod extends EnumClass<String>
{
	public static final RequestMethod	GET = new RequestMethod("GET");
	public static final RequestMethod	POST = new RequestMethod("POST");
	public static final RequestMethod	HEAD = new RequestMethod("HEAD");
	public static final RequestMethod	TRACE = new RequestMethod("TRACE");
	public static final RequestMethod	PUT = new RequestMethod("PUT");
	public static final RequestMethod	DELETE = new RequestMethod("DELETE");
	public static final RequestMethod	OPTIONS = new RequestMethod("OPTIONS");
	public static final RequestMethod	EXIT = new RequestMethod("EXIT");
	public static final RequestMethod	PRECEDENCE = new RequestMethod("PRECEDENCE");

	// WebDAV methods
	public static final RequestMethod	PROPFIND = new RequestMethod("PROPFIND");
	public static final RequestMethod	PROPPATCH = new RequestMethod("PROPPATCH");
	public static final RequestMethod	MKCOL = new RequestMethod("MKCOL");
	public static final RequestMethod	COPY = new RequestMethod("COPY");
	public static final RequestMethod	MOVE = new RequestMethod("MOVE");
	public static final RequestMethod	LOCK = new RequestMethod("LOCK");
	public static final RequestMethod	UNLOCK = new RequestMethod("UNLOCK");

	RequestMethod(String identifier)
	{
		super(identifier);
	}

	public static RequestMethod getMethod(String name)
	{
		if (null == name)
		{
			return GET;
		}

		name = name.toUpperCase();
		RequestMethod method = getMember(RequestMethod.class, name);
		if (null == method)
		{
			method = new RequestMethod(name);
		}
		return method;
	}
}

