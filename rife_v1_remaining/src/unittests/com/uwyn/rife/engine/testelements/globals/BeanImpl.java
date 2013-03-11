/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

public class BeanImpl extends Validation
{
	private String			mString;
	private StringBuffer	mStringbuffer;
	private int				mInt;
	private Integer			mInteger;
	private char			mChar;
	private Character		mCharacter;
	private boolean			mBoolean;
	private Boolean			mBooleanObject;
	private byte			mByte;
	private Byte			mByteObject;
	private double			mDouble;
	private Double			mDoubleObject;
	private float			mFloat;
	private Float			mFloatObject;
	private long			mLong;
	private Long			mLongObject;
	private short			mShort;
	private Short			mShortObject;

	public void activateValidation()
	{
		addConstraint(new ConstrainedProperty("character").editable(false));
		addConstraint(new ConstrainedProperty("byte").editable(false));
	}

	public String getString()
	{
		return mString;
	}

	public void setString(String string)
	{
		mString = string;
	}

	public StringBuffer getStringbuffer()
	{
		return mStringbuffer;
	}

	public void setStringbuffer(StringBuffer stringbuffer)
	{
		mStringbuffer = stringbuffer;
	}

	public int getInt()
	{
		return mInt;
	}

	public void setInt(int anInt)
	{
		mInt = anInt;
	}

	public Integer getInteger()
	{
		return mInteger;
	}

	public void setInteger(Integer integer)
	{
		mInteger = integer;
	}

	public char getChar()
	{
		return mChar;
	}

	public void setChar(char aChar)
	{
		mChar = aChar;
	}

	public Character getCharacter()
	{
		return mCharacter;
	}

	public void setCharacter(Character character)
	{
		mCharacter = character;
	}

	public boolean getBoolean()
	{
		return mBoolean;
	}

	public void setBoolean(boolean aBoolean)
	{
		mBoolean = aBoolean;
	}

	public Boolean getBooleanObject()
	{
		return mBooleanObject;
	}

	public void setBooleanObject(Boolean aBooleanObject)
	{
		mBooleanObject = aBooleanObject;
	}

	public byte getByte()
	{
		return mByte;
	}

	public void setByte(byte aByte)
	{
		mByte = aByte;
	}

	public Byte getByteObject()
	{
		return mByteObject;
	}

	public void setByteObject(Byte byteObject)
	{
		mByteObject = byteObject;
	}

	public double getDouble()
	{
		return mDouble;
	}

	public void setDouble(double aDouble)
	{
		mDouble = aDouble;
	}

	public Double getDoubleObject()
	{
		return mDoubleObject;
	}

	public void setDoubleObject(Double doubleObject)
	{
		mDoubleObject = doubleObject;
	}

	public float getFloat()
	{
		return mFloat;
	}

	public void setFloat(float aFloat)
	{
		mFloat = aFloat;
	}

	public Float getFloatObject()
	{
		return mFloatObject;
	}

	public void setFloatObject(Float floatObject)
	{
		mFloatObject = floatObject;
	}

	public long getLong()
	{
		return mLong;
	}

	public void setLong(long aLong)
	{
		mLong = aLong;
	}

	public Long getLongObject()
	{
		return mLongObject;
	}

	public void setLongObject(Long longObject)
	{
		mLongObject = longObject;
	}

	public short getShort()
	{
		return mShort;
	}

	public void setShort(short aShort)
	{
		mShort = aShort;
	}

	public Short getShortObject()
	{
		return mShortObject;
	}

	public void setShortObject(Short shortObject)
	{
		mShortObject = shortObject;
	}
}

