/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PersonCallbacks.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rifetestmodels;

public class PersonCallbacks extends Person implements Cloneable
{
	private Integer	mId;
	
	public void setId(Integer id)
	{
		mId = id;
	}
	
	public Integer getId()
	{
		return mId;
	}
	
}
