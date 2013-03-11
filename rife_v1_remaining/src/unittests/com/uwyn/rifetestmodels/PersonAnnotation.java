/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PersonAnnotation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rifetestmodels;

import com.uwyn.rife.site.annotations.MetaDataClass;

@MetaDataClass(PersonMetaData.class)
public class PersonAnnotation
{
	private String	mFirstname;
	private String	mLastname;

	public void setFirstname(String firstname)
	{
		mFirstname = firstname;
	}

	public String getFirstname()
	{
		return mFirstname;
	}

	public void setLastname(String lastname)
	{
		mLastname = lastname;
	}

	public String getLastname()
	{
		return mLastname;
	}
}