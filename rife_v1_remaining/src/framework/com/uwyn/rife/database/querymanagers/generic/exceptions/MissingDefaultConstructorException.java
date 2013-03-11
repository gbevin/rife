/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingDefaultConstructorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;

public class MissingDefaultConstructorException extends DatabaseException
{
	static final long serialVersionUID = 5950649556111250164L;
	
	private Class mBeanClass;
	
	public MissingDefaultConstructorException(Class beanClass, Throwable cause)
	{
		super("The bean '"+beanClass.getName()+" has no default constructor. This constructor is required to make is usable by the generic query manager.", cause);
		mBeanClass = beanClass;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
}
