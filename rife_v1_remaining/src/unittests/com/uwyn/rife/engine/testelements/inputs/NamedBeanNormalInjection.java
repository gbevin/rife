/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedBeanNormalInjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.site.ValidationError;

import java.util.Set;

public class NamedBeanNormalInjection extends Element
{
	private BeanImpl	bean;
	public void setThe_bean(BeanImpl bean) { this.bean = bean; }

	public void processElement()
	{
		boolean		notnumeric_int = false;
		boolean		notnumeric_double = false;
		boolean		notnumeric_longobject = false;

		Set<ValidationError> errors = bean.getValidationErrors();
		for (ValidationError error : errors)
		{
			if (error.getIdentifier().equals("NOTNUMERIC"))
			{
				if (error.getSubject().equals("int"))
				{
					notnumeric_int = true;
				}
				if (error.getSubject().equals("double"))
				{
					notnumeric_double = true;
				}
				if (error.getSubject().equals("longObject"))
				{
					notnumeric_longobject = true;
				}
			}
		}
		if (notnumeric_int)
		{
			print("NOTNUMERIC : int\n");
		}
		if (notnumeric_double)
		{
			print("NOTNUMERIC : double\n");
		}
		if (notnumeric_longobject)
		{
			print("NOTNUMERIC : longObject\n");
		}
		print(bean.getString()+","+bean.getStringbuffer()+","+bean.getInt()+","+bean.getInteger()+","+bean.getChar()+","+bean.getCharacter()+","+bean.isBoolean()+","+bean.getBooleanObject()+","+bean.getByte()+","+bean.getByteObject()+","+bean.getDouble()+","+bean.getDoubleObject()+","+bean.getFloat()+","+bean.getFloatObject()+","+bean.getLong()+","+bean.getLongObject()+","+bean.getShort()+","+bean.getShortObject());
	}
}
