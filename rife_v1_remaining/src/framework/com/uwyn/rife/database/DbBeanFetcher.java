/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbBeanFetcher.java 3936 2008-04-26 12:05:37Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.BeanException;
import com.uwyn.rife.database.exceptions.DatabaseException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows a {@link ResultSet} to be easily processed into bean
 * instance.
 * <p>Multiple instances can be collected into a list when processing an
 * entire {@link ResultSet}, or as a single bean instance can be retrieved for
 * one row of a {@link ResultSet}. The default behavior is to not collect
 * instances.
 *
 * @author JR Boyens (jboyens[remove] at uwyn dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3936 $
 * @since 1.0
 */
public class DbBeanFetcher<BeanType> extends DbRowProcessor
{
	private Datasource							mDatasource = null;
	private Class<BeanType>						mBeanClass = null;
	private BeanType							mLastBeanInstance = null;
	private HashMap<String, PropertyDescriptor>	mBeanProperties = new HashMap<String, PropertyDescriptor>();
	private ArrayList<BeanType>					mCollectedInstances = null;

	/**
	 * Create a new DbBeanFetcher
	 *
	 * @param datasource the datasource to be used
	 * @param beanClass the type of bean that will be handled
	 * @exception BeanException thrown if there is an error getting
	 * information about the bean via the beanClass
	 * @since 1.0
	 */
	public DbBeanFetcher(Datasource datasource, Class<BeanType> beanClass)
	throws BeanException
	{
		this(datasource, beanClass, false);
	}
	/**
	 * Create a new DbBeanFetcher
	 *
	 * @param datasource the datasource to be used
	 * @param beanClass the type of bean that will be handled
	 * @param collectInstances <code>true</code> if the fetcher should
	 * collected the bean instances; <code>false</code> if otherwise
	 * @exception BeanException thrown if there is an error getting
	 * information about the bean via the beanClass
	 * @since 1.0
	 */
       
	public DbBeanFetcher(Datasource datasource, Class<BeanType> beanClass, boolean collectInstances)
	throws BeanException
	{
		if (null == datasource) throw new IllegalArgumentException("datasource can't be null.");
		if (null == beanClass)  throw new IllegalArgumentException("beanClass can't be null.");
		
		BeanInfo bean_info = null;

		mDatasource = datasource;
		mBeanClass = beanClass;
		try
		{
			bean_info = Introspector.getBeanInfo(beanClass);
		}
		catch (IntrospectionException e)
		{
			throw new BeanException("Couldn't introspect the bean with class '"+mBeanClass.getName()+"'.", beanClass, e);
		}
		PropertyDescriptor[] bean_properties = bean_info.getPropertyDescriptors();
		for (PropertyDescriptor bean_property : bean_properties)
		{
			mBeanProperties.put(bean_property.getName().toLowerCase(), bean_property);
		}
		
		if (collectInstances)
		{
			mCollectedInstances = new ArrayList<BeanType>();
		}

		assert mDatasource != null;
		assert mBeanClass != null;
		assert null == mLastBeanInstance;
		assert mBeanProperties != null;
	}
	
	/**
	 * Process a ResultSet row into a bean. Call this method on a {@link
	 * ResultSet} and the resulting bean will be stored and be accessible
	 * via {@link #getBeanInstance()}
	 *
	 * @param resultSet the {@link ResultSet} from which to process the
	 * row
	 * @exception SQLException thrown when there is a problem processing
	 * the row
	 * @return <code>true</code> if a bean instance was retrieved; or
	 * <p><code>false</code> if otherwise
	 */
	public boolean processRow(ResultSet resultSet)
	throws SQLException
	{
		if (null == resultSet)  throw new IllegalArgumentException("resultSet can't be null.");
		
		BeanType instance = null;
		try
		{
			instance = mBeanClass.newInstance();
		}
		catch (InstantiationException e)
		{
			SQLException e2 = new SQLException("Can't instantiate a bean with class '"+mBeanClass.getName()+"' : "+e.getMessage());
			e2.initCause(e);
			throw e2;
		}
		catch (IllegalAccessException e)
		{
			SQLException e2 = new SQLException("No permission to instantiate a bean with class '"+mBeanClass.getName()+"' : "+e.getMessage());
			e2.initCause(e);
			throw e2;
		}
		
		ResultSetMetaData	meta = resultSet.getMetaData();
		String				column_name = null;
		String				column_label = null;

		for (int i = 1; i <= meta.getColumnCount(); i++)
		{
			column_name = meta.getColumnName(i).toLowerCase();
			column_label = meta.getColumnLabel(i).toLowerCase();
			if (mBeanProperties.containsKey(column_name))
			{
				populateBeanProperty(instance, column_name, meta, resultSet, i);
			}
			else if (mBeanProperties.containsKey(column_label))
			{
				populateBeanProperty(instance, column_label, meta, resultSet, i);
			}
		}

		assert instance != null;
		
		mLastBeanInstance = instance;
		
		if (mCollectedInstances != null)
		{
			mCollectedInstances.add(instance);
		}

		return gotBeanInstance(instance);

	}

	private void populateBeanProperty(BeanType instance, String propertyName, ResultSetMetaData meta, ResultSet resultSet, int columnIndex) throws SQLException
	{
		PropertyDescriptor	property = mBeanProperties.get(propertyName);
		Method				write_method = property.getWriteMethod();
		if (write_method != null)
		{
			try
			{
				int column_type = meta.getColumnType(columnIndex);
				Object typed_object;
				try
				{
					typed_object = mDatasource.getSqlConversion().getTypedObject(resultSet, columnIndex, column_type, property.getPropertyType());
				}
				catch (DatabaseException e)
				{
					SQLException e2 = new SQLException("Data conversion error while obtaining the typed object.");
					e2.initCause(e);
					throw e2;
				}

				// the sql conversion couldn't create a typed value
				if (null == typed_object)
				{
					// check if the object returned by the resultset is of the same type hierarchy as the property type
					Object column_value = resultSet.getObject(columnIndex);
					if (column_value != null &&
						property.getPropertyType().isAssignableFrom(column_value.getClass()))
					{
						typed_object = column_value;
					}
					// otherwise try to call the property type's constructor with a string argument
					else
					{
						String column_stringvalue = resultSet.getString(columnIndex);
						if (column_stringvalue != null)
						{
							try
							{
								Constructor constructor = property.getPropertyType().getConstructor(new Class[] {String.class});
								if (constructor != null)
								{
									typed_object = constructor.newInstance((Object[])new String[] {column_stringvalue});
								}
							}
							catch (SecurityException e)
							{
								instance = null;
								SQLException e2 = new SQLException("No permission to obtain the String constructor of the property with name '"+property.getName()+"' and class '"+property.getPropertyType().getName()+"' of the bean with class '"+mBeanClass.getName()+"'.");
								e2.initCause(e);
								throw e2;
							}
							catch (NoSuchMethodException e)
							{
								instance = null;
								SQLException e2 = new SQLException("Couldn't find a String constructor for the property with name '"+property.getName()+"' and class '"+property.getPropertyType().getName()+"' of the bean with class '"+mBeanClass.getName()+"'.");
								e2.initCause(e);
								throw e2;
							}
							catch (InstantiationException e)
							{
								instance = null;
								SQLException e2 = new SQLException("Can't instantiate a new instance of the property with name '"+property.getName()+"' and class '"+property.getPropertyType().getName()+"' of the bean with class '"+mBeanClass.getName()+"'.");
								e2.initCause(e);
								throw e2;
							}
						}
					}
				}

				// if the typed object isn't null, set the value
				if (typed_object != null)
				{
					// stored the property type
					write_method.invoke(instance, new Object[] {typed_object});
				}
			}
			catch (IllegalAccessException e)
			{
				instance = null;
				SQLException e2 = new SQLException("No permission to invoke the '"+write_method.getName()+"' method on the bean with class '"+mBeanClass.getName()+"'.");
				e2.initCause(e);
				throw e2;
			}
			catch (IllegalArgumentException e)
			{
				instance = null;
				SQLException e2 = new SQLException("Invalid arguments while invoking the '"+write_method.getName()+"' method on the bean with class '"+mBeanClass.getName()+"'.");
				e2.initCause(e);
				throw e2;
			}
			catch (InvocationTargetException e)
			{
				instance = null;
				SQLException e2 = new SQLException("The '"+write_method.getName()+"' method of the bean with class '"+mBeanClass.getName()+"' has thrown an exception");
				e2.initCause(e);
				throw e2;
			}
			catch (SQLException e)
			{
				instance = null;
				SQLException e2 = new SQLException("SQLException while invoking the '"+write_method.getName()+"' method of the bean with class '"+mBeanClass.getName()+"'");
				e2.initCause(e);
				throw e2;
			}
		}
	}

	/**
	 * Hook method that can be overloaded to receive new bean instances as
	 * they are retrieved, without relying on the internal collection into
	 * a list.
	 *
	 * @param instance the received bean instance
	 * @return <code>true</code> if the bean fetcher should continue to
	 * retrieve the next bean; or
	 * <p><code>false</code> if the retrieval should stop after this bean
	 * @since 1.0
	 */
	public boolean gotBeanInstance(BeanType instance)
	{
		return true;
	}
	
	/**
	 * Get the last processed bean instance
	 *
	 * @return the last processed bean instance
	 * @since 1.0
	 */
	public BeanType getBeanInstance()
	{
		return mLastBeanInstance;
	}

	/**
	 * Get the collected bean instances
	 *
	 * @return the collected bean instances
	 * @since 1.0
	 */
	public List<BeanType> getCollectedInstances()
	{
		return mCollectedInstances;
	}
}
