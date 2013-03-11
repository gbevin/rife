/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CommonJdk15.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.types.databasedrivers;

import java.lang.reflect.Method;
import java.sql.Types;

import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.site.Constrained;

public abstract class CommonJdk15
{
	public static String getSqlType(Class type, int precision, int scale)
	{
		try
		{
			Class uuid_class = Class.forName("java.util.UUID");
			if (type == uuid_class)
			{
				return "VARCHAR(36)";
			}
		}
		catch (ClassNotFoundException e)
		{
			// swallow exception, just don't handle UUID
		}

		return null;
	}

	public static Object getTypedObject(Object result, Class targetType)
	{
		try
		{
			Class uuid_class = Class.forName("java.util.UUID");
			if (targetType == uuid_class)
			{
				Method method = uuid_class.getMethod("fromString", new Class[] {String.class});
				return method.invoke(null, new Object[] {result.toString()});
			}
		}
		catch (Exception e)
		{
			// swallow exception, just don't handle UUID
		}

		return null;
	}

	public static boolean setTypedParameter(DbPreparedStatement statement, int parameterIndex, Class targetType, String name, Object value, Constrained constrained)
	{
		try
		{
			Class uuid_class = Class.forName("java.util.UUID");
			if (targetType == uuid_class)
			{
				if (null == value)
				{
					statement.setNull(parameterIndex, Types.VARCHAR);
				}
				else
				{
					statement.setString(parameterIndex, value.toString());
				}
				
				return true;
			}
		}
		catch (ClassNotFoundException e)
		{
			// swallow exception, just don't handle UUID
		}

		return false;
	}
}
