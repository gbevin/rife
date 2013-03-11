/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanImplementationNotFoundException.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.exceptions;

public class BeanImplementationNotFoundException extends ClassNotFoundException
{
	private String	mImplementation = null;
	
	public BeanImplementationNotFoundException(String implementation, Throwable cause)
	{
		super("The bean implementation '"+implementation+"' couldn't be found.", cause);
		
		mImplementation = implementation;
	}
	
	public String getImplementation()
	{
		return mImplementation;
	}
}
