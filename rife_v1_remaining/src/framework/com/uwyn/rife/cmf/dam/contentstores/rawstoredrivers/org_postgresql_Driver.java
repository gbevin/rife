/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_postgresql_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbTransactionUser;
import com.uwyn.rife.database.DbTransactionUserWithoutResult;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.tools.exceptions.InnerClassException;

public class org_postgresql_Driver extends generic
{
	public org_postgresql_Driver(Datasource datasource)
	{
		super(datasource);
	}
	
	protected <ResultType> ResultType _useContentData(final Select retrieveContentChunks, final int id, final ContentDataUser user)
	throws ContentManagerException
	{
		return (ResultType)inTransaction(new DbTransactionUser<ResultType, Object>() {
				public ResultType useTransaction()
				throws InnerClassException
				{
					return (ResultType)org_postgresql_Driver.super._useContentData(retrieveContentChunks, id, user);
				}
			});
	}
		
	protected void _serveContentData(final Select retrieveContentChunks, final ElementSupport element, final int id)
	throws ContentManagerException
	{
		inTransaction(new DbTransactionUserWithoutResult() {
				public void useTransactionWithoutResult()
				throws InnerClassException
				{
					org_postgresql_Driver.super._serveContentData(retrieveContentChunks, element, id);
				}
			});
	}
}
