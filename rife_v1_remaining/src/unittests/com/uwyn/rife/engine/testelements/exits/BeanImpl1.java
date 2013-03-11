/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanImpl1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import java.util.Date;

public class BeanImpl1
{
	public enum Day { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
	
	private String	mString1 = null;
	private String	mString2 = null;
	private String	mString3 = null;
	private Day		mEnum4 = null;
	private Date	mDate5 = null;
	
	public void setString1(String string1)
	{
		mString1 = string1;
	}
	
	public String getString1()
	{
		return mString1;
	}
	
	public void setString2(String string2)
	{
		mString2 = string2;
	}
	
	public String getString2()
	{
		return mString2;
	}
	
	public void setString3(String string3)
	{
		mString3 = string3;
	}
	
	public String getString3()
	{
		return mString3;
	}
	
	public void setEnum4(Day enum4)
	{
		mEnum4 = enum4;
	}
	
	public Day getEnum4()
	{
		return mEnum4;
	}
	
	public void setDate5(Date date5)
	{
		mDate5 = date5;
	}
	
	public Date getDate5()
	{
		return mDate5;
	}
}

