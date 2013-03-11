/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SqlConversion.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.types;

import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.site.Constrained;
import java.sql.ResultSet;

public interface SqlConversion
{
	public String getSqlValue(Object value);
	public String getSqlType(Class type, int precision, int scale);
	public Object getTypedObject(ResultSet resultSet, int columnNumber, int type, Class targetType)	throws DatabaseException;
	public void setTypedParameter(DbPreparedStatement statement, int parameterIndex, Class targetType, String name, Object value, Constrained constrained) throws DatabaseException;
}
