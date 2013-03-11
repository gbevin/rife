/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.config.RifeConfig;
import java.util.Calendar;
import java.util.Date;

public class BeanImpl
{
	private int				mPropertyInt = 34876;
	private String			mPropertyString = "oifuigygti";
	private StringBuffer	mPropertyStringBuffer = new StringBuffer("osduhbfezgb");
	private long			mPropertyLong = 982787834L;
	private char			mPropertyChar = 'O';
	private short			mPropertyShort = 423;
	private byte			mPropertyByte = (byte)92;
	private Date			mPropertyDate;
	
	public BeanImpl()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		cal.set(2005, 7, 18, 10, 36, 31);
		cal.set(Calendar.MILLISECOND, 874);
		mPropertyDate = cal.getTime();
	}
	
	public void setPropertyInt(int propertyInt)
	{
		this.mPropertyInt = propertyInt;
	}
	
	public int getPropertyInt()
	{
		return mPropertyInt;
	}
	
	public void setPropertyString(String propertyString)
	{
		this.mPropertyString = propertyString;
	}
	
	public String getPropertyString()
	{
		return mPropertyString;
	}
	
	public void setPropertyStringBuffer(StringBuffer propertyStringBuffer)
	{
		this.mPropertyStringBuffer = propertyStringBuffer;
	}
	
	public StringBuffer getPropertyStringBuffer()
	{
		return mPropertyStringBuffer;
	}
	
	public void setPropertyLong(long propertyLong)
	{
		this.mPropertyLong = propertyLong;
	}
	
	public long getPropertyLong()
	{
		return mPropertyLong;
	}
	
	public void setPropertyChar(char propertyChar)
	{
		this.mPropertyChar = propertyChar;
	}
	
	public char getPropertyChar()
	{
		return mPropertyChar;
	}
	
	public void setPropertyShort(short propertyShort)
	{
		this.mPropertyShort = propertyShort;
	}
	
	public short getPropertyShort()
	{
		return mPropertyShort;
	}
	
	public void setPropertyByte(byte propertyByte)
	{
		this.mPropertyByte = propertyByte;
	}
	
	public byte getPropertyByte()
	{
		return mPropertyByte;
	}
	
	public void setPropertyDate(Date propertyDate)
	{
		mPropertyDate = propertyDate;
	}
	
	public Date getPropertyDate()
	{
		return mPropertyDate;
	}
}

