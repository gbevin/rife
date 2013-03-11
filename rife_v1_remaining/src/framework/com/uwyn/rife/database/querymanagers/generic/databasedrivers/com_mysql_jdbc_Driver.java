/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mysql_jdbc_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.databasedrivers;

import com.uwyn.rife.database.*;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.exceptions.ExecutionErrorException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.database.queries.Update;
import com.uwyn.rife.database.querymanagers.generic.Callbacks;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import java.sql.Types;

public class com_mysql_jdbc_Driver<BeanType> extends generic<BeanType> implements GenericQueryManager<BeanType>
{
	private CreateTable 	mCreateTableMysql = null;
	private Insert 			mSaveMysql = null;
	private Select 			mLastIdMysql = null;
	
	public com_mysql_jdbc_Driver(Datasource datasource, String tableName, String primaryKey, Class<BeanType> beanClass, boolean hasIdentifier)
	throws DatabaseException
	{
		super(datasource, tableName, primaryKey, beanClass, hasIdentifier);
	}
	
	protected CreateTable getInternalCreateTableQuery()
	{
		if (null == mCreateTableMysql)
		{
			CreateTable query = new CreateTable(getDatasource())
				.table(mTableName)
				.columns(mBaseClass);
			if (!isIdentifierSparse())
			{
				query
					.customAttribute(mPrimaryKey, "AUTO_INCREMENT");
			}
			if (!mHasIdentifier)
			{
				query
					.primaryKey(mPrimaryKey);
			}
			
			addCreateTableManyToOneColumns(query);

			mCreateTableMysql = query;
		}
		
		return mCreateTableMysql;
	}
	
	protected Insert getInternalSaveQuery()
	{
		if (null == mSaveMysql)
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

			mSaveMysql = query;
		}
		
		return mSaveMysql;
	}
	
	protected Select getInternalLastIdQuery()
	{
		if (null == mLastIdMysql)
		{
			Select query = new Select(getDatasource())
				.from(mTableName)
				.field("LAST_INSERT_ID()");
			mLastIdMysql = query;
		}
		
		return mLastIdMysql;
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
	
	public int save(BeanType bean) throws DatabaseException
	{
		return _save(getInternalLastIdQuery(), getInternalSaveQuery(), getInternalSaveUpdateQuery(), bean);
	}
	
	public int insert(BeanType bean) throws DatabaseException
	{
		return _insert(getInternalLastIdQuery(), getInternalSaveQuery(), bean);
	}
	
	protected int _insert(final Select lastId, final Insert save, final BeanType bean)
	{
		// handle before callback
		Callbacks callbacks = getCallbacks(bean);
		if (callbacks != null &&
			!callbacks.beforeInsert(bean))
		{
			return -1;
		}
		
		// perform insert
		int result = _insertWithoutCallbacks(lastId, save, bean);
		
		// handle after callback
		if (callbacks != null)
		{
			callbacks.afterInsert(bean, result != -1);
		}
		
		return result;
	}
	
	protected int _insertWithoutCallbacks(final Select lastId, final Insert save, final BeanType bean)
	throws DatabaseException
	{
		int result = -1;
		
		result = ((Integer)inTransaction(new DbTransactionUser() {
				public Integer useTransaction()
				{
					// reserving the connection inside the transaction user
					// since there are versions of MySQL that don't support tranactions
					// and in that case the thread connection isn't reserved to obtain
					// the generated primary key
					return reserveConnection(new DbConnectionUser() {
							public Integer useConnection(DbConnection connection)
							{
								storeManyToOne(bean);

								executeUpdate(save, new DbPreparedStatementHandler() {
										public void setParameters(DbPreparedStatement statement)
										{
											statement.setBean(bean);
											
											if (!isIdentifierSparse() &&
												save.getFields().containsKey(getIdentifierName()))
											{
												statement.setNull(getIdentifierName(), Types.INTEGER);
											}
											
											setManyToOneJoinParameters(statement, bean);
										}
									});
								
								Integer primary_key_id;
								if (isIdentifierSparse())
								{
									primary_key_id = new Integer(getIdentifierValue(bean));
								}
								else
								{
									primary_key_id = new Integer(executeGetFirstInt(lastId));
								}
								
								storeManyToOneAssociations(bean, primary_key_id);
								storeManyToMany(bean, primary_key_id);
								
								return primary_key_id;
							}
						});
				}
			})).intValue();
		
		if (result != -1)
		{
			try
			{
				mSetPrimaryKeyMethod.invoke(bean, new Object[] {new Integer(result)});
			}
			catch (Throwable e)
			{
				throw new DatabaseException(e);
			}
		}
		
		// handle listeners
		if (result != -1)
		{
			fireInserted(bean);
		}
		
		return result;
	}
	
	protected int _save(final Select lastId, final Insert save, final Update saveUpdate, final BeanType bean)
	throws DatabaseException
	{
		int value = -1;
		
		// handle before callback
		final Callbacks callbacks = getCallbacks(bean);
		if (callbacks != null &&
			!callbacks.beforeSave(bean))
		{
			return -1;
		}
		
		boolean update = false;
		try
		{
			int id = getIdentifierValue(bean);
			if (id >= 0)
			{
				value = id;
				update = true;
			}
		}
		catch (Throwable e)
		{
			throw new DatabaseException(e);
		}
		
		if (isIdentifierSparse())
		{
			// handle before callback
			if (callbacks != null &&
				!callbacks.beforeInsert(bean))
			{
				return -1;
			}
			
			// try to perform the insert
			try
			{
				value = _insertWithoutCallbacks(lastId, save, bean);
			}
			catch (ExecutionErrorException e)
			{
				value = -1;
			}
			
			// handle after callback
			if (callbacks != null &&
				!callbacks.afterInsert(bean, value != -1))
			{
				return value;
			}
			
			if (-1 == value)
			{
				// handle before callback
				if (callbacks != null &&
					!callbacks.beforeUpdate(bean))
				{
					return -1;
				}
				
				value = _updateWithoutCallbacks(saveUpdate, bean);
				
				// handle after callback
				if (callbacks != null &&
					!callbacks.afterUpdate(bean, value != -1))
				{
					return value;
				}
			}
		}
		else
		{
			if (update)
			{
				// handle before callback
				if (callbacks != null &&
					!callbacks.beforeUpdate(bean))
				{
					return -1;
				}
				
				value = _updateWithoutCallbacks(saveUpdate, bean);
				
				// handle after callback
				if (callbacks != null &&
					!callbacks.afterUpdate(bean, value != -1))
				{
					return value;
				}
			}
			
			if (-1 == value)
			{
				// handle before callback
				if (callbacks != null &&
					!callbacks.beforeInsert(bean))
				{
					return -1;
				}
				
				value = _insertWithoutCallbacks(lastId, save, bean);
				
				// handle after callback
				if (callbacks != null &&
					!callbacks.afterInsert(bean, value != -1))
				{
					return value;
				}
			}
		}
		
		// handle after callback
		if (callbacks != null)
		{
			callbacks.afterSave(bean, value != -1);
		}
		
		return value;
	}
}
