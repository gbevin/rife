/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.databasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbTransactionUserWithoutResult;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Query;
import com.uwyn.rife.database.queries.SequenceValue;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import java.sql.Statement;

public class org_apache_derby_jdbc_EmbeddedDriver<BeanType> extends generic<BeanType> implements GenericQueryManager<BeanType>
{
	private CreateTable 	mCreateTableDerby = null;
	private Insert		 	mSaveDerby = null;
	
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource, String tableName, String primaryKey, Class<BeanType> beanClass, boolean hasIdentifier)
	throws DatabaseException
	{
		super(datasource, tableName, primaryKey, beanClass, hasIdentifier);
	}
	
	protected CreateTable getInternalCreateTableQuery()
	{
		if (null == mCreateTableDerby)
		{
			CreateTable query = new CreateTable(getDatasource())
				.table(mTableName)
				.columns(mBaseClass);
			if (!isIdentifierSparse())
			{
				query
					.customAttribute(mPrimaryKey, "GENERATED ALWAYS AS IDENTITY");
			}
			if (!mHasIdentifier)
			{
				query
					.primaryKey(mPrimaryKey);
			}
			
			addCreateTableManyToOneColumns(query);
			
			mCreateTableDerby = query;
		}
		
		return mCreateTableDerby;
	}
	
	protected Insert getInternalSaveQuery()
	{
		if (null == mSaveDerby)
		{
			Insert query = new Insert(getDatasource())
				.into(mTableName);
			if (!isIdentifierSparse())
			{
				query
					.fieldsParametersExcluded(mBaseClass, new String[]{mPrimaryKey});
			}
			else
			{
				query
					.fieldsParameters(mBaseClass);
			}
			
			addSaveManyToOneFields(query);

			mSaveDerby = query;
		}
		
		return mSaveDerby;
	}
	
	public void install()
	throws DatabaseException
	{
		executeUpdate(getInternalCreateTableQuery());
		installManyToMany();
		
		fireInstalled();
	}
	
	public void install(CreateTable query)
	throws DatabaseException
	{
		executeUpdate(query);
		installManyToMany();
		
		fireInstalled();
	}
	
	public void remove()
	throws DatabaseException
	{
		removeManyToMany();
		executeUpdate(getInternalDropTableQuery());
		
		fireRemoved();
	}
	
	protected int _insertWithoutCallbacks(final SequenceValue nextId, final Insert save, final BeanType bean) throws DatabaseException
	{
		final int[] result = new int[] {getIdentifierValue(bean)};
		
		inTransaction(new DbTransactionUserWithoutResult() {
				public void useTransactionWithoutResult()
				{
					storeManyToOne(bean);

					executeUpdate(save, new DbPreparedStatementHandler() {
							public DbPreparedStatement getPreparedStatement(Query query, DbConnection connection)
							{
								return connection.getPreparedStatement(query, Statement.RETURN_GENERATED_KEYS);
							}
							
							public void setParameters(DbPreparedStatement statement)
							{
								statement
									.setBean(bean);
											
								setManyToOneJoinParameters(statement, bean);
							}
							
							public int performUpdate(DbPreparedStatement statement)
							{
								setParameters(statement);
								int query_result = statement.executeUpdate();
								if (isIdentifierSparse())
								{
									result[0] = getIdentifierValue(bean);
								}
								else
								{
									result[0] = statement.getFirstGeneratedIntKey();
								}
								
								return query_result;
							}
						});
					
					if (result[0] != -1)
					{
						try
						{
							mSetPrimaryKeyMethod.invoke(bean, new Object[] {new Integer(result[0])});
						}
						catch (Throwable e)
						{
							throw new DatabaseException(e);
						}
						
						storeManyToOneAssociations(bean, result[0]);
						storeManyToMany(bean, result[0]);
					}
				}
			});
		
		// handle listeners
		if (result[0] != -1)
		{
			fireInserted(bean);
		}
		
		return result[0];
	}
}
