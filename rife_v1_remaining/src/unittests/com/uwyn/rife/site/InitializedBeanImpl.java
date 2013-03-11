/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InitializedBeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

public class InitializedBeanImpl
{
	private String			mString = "default";
	private StringBuffer	mStringbuffer = null;
	private int				mInt = -1;
	private Integer			mInteger = null;
	private char			mChar = 'i';
	private Character		mCharacter = new Character('k');
	
	public InitializedBeanImpl()
	{
	}
	
	public void setString(String string)
	{
		mString = string;
	}
	
	public String getString()
	{
		return mString;
	}
	
	public void setStringbuffer(StringBuffer stringbuffer)
	{
		mStringbuffer = stringbuffer;
	}
	
	public StringBuffer getStringbuffer()
	{
		return mStringbuffer;
	}
	
	public void setInt(int integer)
	{
		mInt = integer;
	}
	
	public int getInt()
	{
		return mInt;
	}
	
	public void setInteger(Integer integer)
	{
		mInteger = integer;
	}
	
	public Integer getInteger()
	{
		return mInteger;
	}
	
	public void setChar(char character)
	{
		mChar = character;
	}
	
	public char getChar()
	{
		return mChar;
	}
	
	public void setCharacter(Character character)
	{
		mCharacter = character;
	}
	
	public Character getCharacter()
	{
		return mCharacter;
	}
}

