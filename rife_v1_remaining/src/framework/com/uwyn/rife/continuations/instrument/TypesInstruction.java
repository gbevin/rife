/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TypesInstruction.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.continuations.instrument.TypesOpcode;

class TypesInstruction
{
	private byte	mOpcode = -1;
	private int		mArgument = -1;
	private String	mType = null;
	
	TypesInstruction(byte opcode)
	{
		mOpcode = opcode;
	}
	
	TypesInstruction(byte opcode, String type)
	{
		mOpcode = opcode;
		mType = type;
	}
	
	TypesInstruction(byte opcode, int argument)
	{
		mOpcode = opcode;
		mArgument = argument;
	}
	
	TypesInstruction(byte opcode, int argument, String type)
	{
		mOpcode = opcode;
		mArgument = argument;
		mType = type;
	}
	
	byte getOpcode()
	{
		return mOpcode;
	}
	
	int getArgument()
	{
		return mArgument;
	}
	
	String getType()
	{
		return mType;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder(TypesOpcode.toString(mOpcode));
		if (mArgument != -1)
		{
			result.append(", ");
			result.append(mArgument);
		}
		if (mType != null)
		{
			result.append(", ");
			result.append(mType);
		}
		
		return result.toString();
	}
}
