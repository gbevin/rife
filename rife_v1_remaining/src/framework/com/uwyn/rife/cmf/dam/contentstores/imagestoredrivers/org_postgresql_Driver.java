/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_postgresql_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.imagestoredrivers;

import com.uwyn.rife.database.Datasource;
import java.sql.Types;

public class org_postgresql_Driver extends generic
{
	public org_postgresql_Driver(Datasource datasource)
	{
		super(datasource);
	}
	
	protected int getNullSqlType()
	{
		return Types.LONGVARBINARY;
	}
}
