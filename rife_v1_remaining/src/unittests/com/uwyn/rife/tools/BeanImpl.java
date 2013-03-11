/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import java.math.BigDecimal;

public class BeanImpl
{
	private int					mPropertyReadonly = 23;
	private String				mPropertyString = null;
	private StringBuffer		mPropertyStringbuffer = null;
	private java.util.Date		mPropertyDate = null;
	private java.util.Calendar	mPropertyCalendar = null;
	private java.sql.Date		mPropertySqlDate = null;
	private java.sql.Time		mPropertyTime = null;
	private java.sql.Timestamp	mPropertyTimestamp = null;
	private char				mPropertyChar = 0;
	private boolean				mPropertyBoolean = false;
	private byte				mPropertyByte = 0;
	private double				mPropertyDouble = 0.0d;
	private float				mPropertyFloat = 0.0f;
	private int					mPropertyInt = 0;
	private long				mPropertyLong = 0;
	private short				mPropertyShort = 0;
	private BigDecimal			mPropertyBigDecimal = null;
	
	public BeanImpl()
	{
	}
	
	public int getPropertyReadonly()
	{
		return mPropertyReadonly;
	}
	
	public void setPropertyWriteonly(long propertyWriteonly)
	{
	}

	public int getPropertyInt()
	{
		return mPropertyInt;
	}

	public void setPropertyInt(int propertyInt)
	{
		mPropertyInt = propertyInt;
	}

	public String getPropertyString()
	{
		return mPropertyString;
	}

	public void setPropertyString(String propertyString)
	{
		mPropertyString = propertyString;
	}

	public double getPropertyDouble()
	{
		return mPropertyDouble;
	}

	public void setPropertyDouble(double propertyDouble)
	{
		mPropertyDouble = propertyDouble;
	}

	public StringBuffer getPropertyStringbuffer()
	{
		return mPropertyStringbuffer;
	}

	public void setPropertyStringbuffer(StringBuffer propertyStringbuffer)
	{
		mPropertyStringbuffer = propertyStringbuffer;
	}

	public java.util.Date getPropertyDate()
	{
		return mPropertyDate;
	}

	public void setPropertyDate(java.util.Date propertyDate)
	{
		mPropertyDate = propertyDate;
	}

	public java.util.Calendar getPropertyCalendar()
	{
		return mPropertyCalendar;
	}

	public void setPropertyCalendar(java.util.Calendar propertyCalendar)
	{
		mPropertyCalendar = propertyCalendar;
	}

	public java.sql.Date getPropertySqlDate()
	{
		return mPropertySqlDate;
	}

	public void setPropertySqlDate(java.sql.Date propertySqlDate)
	{
		mPropertySqlDate = propertySqlDate;
	}

	public java.sql.Time getPropertyTime()
	{
		return mPropertyTime;
	}

	public void setPropertyTime(java.sql.Time propertyTime)
	{
		mPropertyTime = propertyTime;
	}

	public java.sql.Timestamp getPropertyTimestamp()
	{
		return mPropertyTimestamp;
	}

	public void setPropertyTimestamp(java.sql.Timestamp propertyTimestamp)
	{
		mPropertyTimestamp = propertyTimestamp;
	}

	public boolean isPropertyBoolean()
	{
		return mPropertyBoolean;
	}

	public void setPropertyBoolean(boolean propertyBoolean)
	{
		mPropertyBoolean = propertyBoolean;
	}

	public byte getPropertyByte()
	{
		return mPropertyByte;
	}

	public void setPropertyByte(byte propertyByte)
	{
		mPropertyByte = propertyByte;
	}

	public float getPropertyFloat()
	{
		return mPropertyFloat;
	}

	public void setPropertyFloat(float propertyFloat)
	{
		mPropertyFloat = propertyFloat;
	}

	public long getPropertyLong()
	{
		return mPropertyLong;
	}

	public void setPropertyLong(long propertyLong)
	{
		mPropertyLong = propertyLong;
	}

	public short getPropertyShort()
	{
		return mPropertyShort;
	}

	public void setPropertyShort(short propertyShort)
	{
		mPropertyShort = propertyShort;
	}

	public char getPropertyChar()
	{
		return mPropertyChar;
	}

	public void setPropertyChar(char propertyChar)
	{
		mPropertyChar = propertyChar;
	}

	public BigDecimal getPropertyBigDecimal()
	{
		return mPropertyBigDecimal;
	}

	public void setPropertyBigDecimal(BigDecimal propertyBigDecimal)
	{
		mPropertyBigDecimal = propertyBigDecimal;
	}
}
