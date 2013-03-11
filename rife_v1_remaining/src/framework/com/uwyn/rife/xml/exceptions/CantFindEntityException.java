/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CantFindEntityException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class CantFindEntityException extends XmlErrorException
{
	private static final long serialVersionUID = -2964903002673180847L;
	
	private String	mEntity = null;
	
	public CantFindEntityException(String entity, Throwable e)
	{
		super("Can't find the entity '"+entity+"'.", e);
		
		mEntity = entity;
	}
	
	public String getEntity()
	{
		return mEntity;
	}
}
