/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanPrefixOutjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.outputs;

import com.uwyn.rife.engine.Element;

public class BeanPrefixOutjection extends Element
{
	public void setInbean1(BeanImpl inbean)
	{
	}
	
	public BeanImpl getOutbean1()
	{
		BeanImpl	bean = new BeanImpl();
		bean.setString("the string");
		bean.setStringbuffer(new StringBuffer("the stringbuffer"));
		bean.setInt(23154);
		bean.setInteger(new Integer(893749));
		bean.setChar('u');
		bean.setCharacter(new Character('R'));
		bean.setBoolean(true);
		bean.setBooleanObject(new Boolean(false));
		bean.setByte((byte)120);
		bean.setByteObject(new Byte((byte)21));
		bean.setDouble(34878.34);
		bean.setDoubleObject(new Double(25435.98));
		bean.setFloat((float)3434.76);
		bean.setFloatObject(new Float((float)6534.8));
		bean.setLong(34347897L);
		bean.setLongObject(new Long(2335454L));
		bean.setShort((short)32);
		bean.setShortObject(new Short((short)12));
		
		return bean;
	}
	
	public void processElement()
	{
		if (hasInputValue("prefix_string"))
		{
			print(getInput("prefix_string")+","+
				  getInput("prefix_stringbuffer")+","+
				  getInput("prefix_int")+","+
				  getInput("prefix_integer")+","+
				  getInput("prefix_char")+","+
				  getElementInfo().containsInput("prefix_character")+","+
				  getInput("prefix_boolean")+","+
				  getInput("prefix_booleanObject")+","+
				  getElementInfo().containsInput("prefix_byte")+","+
				  getInput("prefix_byteObject")+","+
				  getInput("prefix_double")+","+
				  getInput("prefix_doubleObject")+","+
				  getInput("prefix_float")+","+
				  getInput("prefix_floatObject")+","+
				  getInput("prefix_long")+","+
				  getInput("prefix_longObject")+","+
				  getInput("prefix_short")+","+
				  getInput("prefix_shortObject"));
		}
		else
		{
			exit("exit");
		}
	}
}

