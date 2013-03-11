/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedBeanNormal.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.site.ValidationError;
import java.util.Set;

public class NamedBeanNormal extends Element
{
	public void processElement()
	{
		BeanImpl	bean = getNamedSubmissionBean("bean", "the_bean");
		boolean		notnumeric_int = false;
		boolean		notnumeric_double = false;
		boolean		notnumeric_longobject = false;
		
		Set<ValidationError> errors = bean.getValidationErrors();
		for (ValidationError error : errors)
		{
			print(error.getIdentifier()+" : "+error.getSubject()+"\n");
		}
		print(bean.getEnum()+","+bean.getString()+","+bean.getStringbuffer()+","+bean.getInt()+","+bean.getInteger()+","+bean.getChar()+","+bean.getCharacter()+","+bean.isBoolean()+","+bean.getBooleanObject()+","+bean.getByte()+","+bean.getByteObject()+","+bean.getDouble()+","+bean.getDoubleObject()+","+bean.getFloat()+","+bean.getFloatObject()+","+bean.getLong()+","+bean.getLongObject()+","+bean.getShort()+","+bean.getShortObject());
	}
}

