/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mysql_jdbc_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.Insert;
import java.io.IOException;
import java.io.InputStream;

public class com_mysql_jdbc_Driver extends generic
{
	public com_mysql_jdbc_Driver(Datasource datasource)
	{
		super(datasource);
	}
	
	protected int storeChunks(Insert storeContentChunk, final int id, InputStream data)
	throws IOException
	{
		return storeChunksNoStream(storeContentChunk, id, data);
	}
}
