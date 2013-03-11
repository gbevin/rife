/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class BeanException extends DbQueryException
{
	private static final long serialVersionUID = 7745938017589820114L;

	private Class	mBean = null;
	
	public BeanException(String message, Class bean)
	{
		super(message);
		mBean = bean;
	}

	public BeanException(String message, Class bean, Throwable cause)
	{
		super(message, cause);
		mBean = bean;
	}

	public Class getBean()
	{
		return mBean;
	}
}
