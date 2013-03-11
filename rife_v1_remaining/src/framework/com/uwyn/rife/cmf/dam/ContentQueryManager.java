/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentQueryManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.cmf.dam.exceptions.*;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentFactory;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.DbTransactionUser;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerDelegate;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerListener;
import com.uwyn.rife.database.querymanagers.generic.RestoreQuery;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.ClassUtils;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * The <code>ContentQueryManager</code> simplifies working with content a lot.
 * It extends {@link
 * com.uwyn.rife.database.querymanagers.generic.GenericQueryManager
 * GenericQueryManager} and is a drop-in replacement that can be used instead.
 * The <code>ContentQueryManager</code> class works hand-in-hand with
 * CMF-related constraints that are provided via the classes {@link
 * com.uwyn.rife.site.Validation Validation} and {@link
 * com.uwyn.rife.site.ConstrainedProperty ConstrainedProperty}. The additional constraints
 * allow you to provide CMF-related metadata for bean properties while still
 * having access to all regular constraints.
 * <p>The most important additional constraint is '{@link
 * com.uwyn.rife.site.ConstrainedProperty#mimeType(com.uwyn.rife.cmf.MimeType) mimeType}'. Setting this
 * constraint directs RIFE to delegate the handling of that property's data to
 * the CMF instead of storing it as a regular column in a database table. The
 * property content location (i.e. its full path) is generated automatically
 * based on the bean class name, the instance's identifier value (i.e. the
 * primary key used by <code>GenericQueryManager</code>), and the property
 * name. So for example, if you have an instance of the <code>NewsItem</code>
 * class whose identifier is <code>23</code>, then the full path that is
 * generated for a property named <code>text</code> is '<code>/newsitem/23/text</code>'.
 * Note that this always specifies the most recent version of the property,
 * but that older versions are also available from the content store.
 * <p>Before being able to use the CMF and a <code>ContentQueryManager</code>,
 * you must install both of them, as in this example:
 * <pre>Datasource ds = Datasources.getRepInstance().getDatasource("datasource");
 *DatabaseContentFactory.getInstance(ds).install();
 *new ContentQueryManager(ds, NewsItem.class).install();</pre>
 * <p>Apart from the handling of content, this query manager also integrates
 * the functionalities of the {@link OrdinalManager} class.
 * <p>The new '{@link com.uwyn.rife.site.ConstrainedProperty#ordinal(boolean) ordinal}'
 * constraint indicates which bean property will be used to order that table
 * rows. When saving and deleting beans, the ordinal values will be
 * automatically updated in the entire table. The
 * <code>ContentQueryManager</code> also provides the {@link
 * #move(Constrained, String, OrdinalManager.Direction) move}, {@link
 * #up(Constrained, String) up} and {@link #down(Constrained, String) down}
 * methods to easily manipulate the order of existing rows.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)i
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class ContentQueryManager<T> extends GenericQueryManagerDelegate<T> implements Cloneable
{
	private Class           mClass = null;
	private Class           mBackendClass = null;
	private DbQueryManager  mDbQueryManager = null;
	private ContentManager  mContentManager = null;
	private String			mRepository = null;
	
	private ThreadLocal<T>	mDeletedbean = new ThreadLocal<T>();
	
	/**
	 * Creates a new <code>ContentQueryManager</code> instance for a specific
	 * class.
	 * <p>All content will be stored in a {@link
	 * com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContent}.
	 *
	 * @param datasource the datasource that indicates where the data will be
	 * stored
	 * @param klass the class of the bean that will be handled by this
	 * <code>ContentQueryManager</code>
	 * @param backendClass the class the will be used by this
	 * <code>ContentQueryManager</code> to reference data in the backend
	 * @since 1.0
	 */
	public ContentQueryManager(Datasource datasource, Class<T> klass, Class backendClass)
	{
		super(datasource, klass, ClassUtils.shortenClassName(backendClass));
		
		mClass = klass;
		mBackendClass = backendClass;
		mDbQueryManager = new DbQueryManager(datasource);
		mContentManager = DatabaseContentFactory.getInstance(datasource);
		addListener(new Listener());
	}
	
	/**
	 * Creates a new <code>ContentQueryManager</code> instance for a specific
	 * class, but with a different table name for the database storage.
	 * <p>All content will be stored in a {@link
	 * com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContent}.
	 *
	 * @param datasource the datasource that indicates where the data will be
	 * stored
	 * @param klass the class of the bean that will be handled by this
	 * <code>ContentQueryManager</code>
	 * @param table the name of the database table in which the non CMF data will
	 * be stored
	 * @since 1.6
	 */
	public ContentQueryManager(Datasource datasource, Class<T> klass, String table)
	{
		super(datasource, klass, table);
		
		mClass = klass;
		mBackendClass = klass;
		mDbQueryManager = new DbQueryManager(datasource);
		mContentManager = DatabaseContentFactory.getInstance(datasource);
		addListener(new Listener());
	}
	
	/**
	 * Creates a new <code>ContentQueryManager</code> instance for a specific
	 * class.
	 * <p>All content will be stored in a {@link
	 * com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContent}.
	 *
	 * @param datasource the datasource that indicates where the data will be
	 * stored
	 * @param klass the class of the bean that will be handled by this
	 * <code>ContentQueryManager</code>
	 * @since 1.0
	 */
	public ContentQueryManager(Datasource datasource, Class<T> klass)
	{
		super(datasource, klass);
		
		mClass = klass;
		mBackendClass = klass;
		mDbQueryManager = new DbQueryManager(datasource);
		mContentManager = DatabaseContentFactory.getInstance(datasource);
		addListener(new Listener());
	}
	
	/**
	 * Creates a new <code>ContentQueryManager</code> instance for a specific
	 * class.
	 * <p>All content will be stored in the provided
	 * <code>ContentManager</code> instance. This constructor is handy if you
	 * want to integrate a custom content manager implementation.
	 *
	 * @param datasource the datasource that indicates where the data will be
	 * stored
	 * @param klass the class of the bean that will be handled by this
	 * <code>ContentQueryManager</code>
	 * @param contentManager a <code>ContentManager</code> instance
	 * @since 1.0
	 */
	public ContentQueryManager(Datasource datasource, Class<T> klass, ContentManager contentManager)
	{
		super(datasource, klass);

		mClass = klass;
		mContentManager = contentManager;
		addListener(new Listener());
	}
	
	/**
	 * Sets the default repository that will be used by this <code>ContentQueryManager</code>.
	 *
	 * @return this <code>ContentQueryManager</code>
	 * @see #getRepository
	 * @since 1.4
	 */
	public ContentQueryManager<T> repository(String repository)
	{
		mRepository = repository;
		
		return this;
	}
		
	/**
	 * Retrieves the default repository that is used by this <code>ContentQueryManager</code>.
	 *
	 * @return this <code>ContentQueryManager</code>'s repository
	 * @see #repository
	 * @since 1.4
	 */
	public String getRepository()
	{
		return mRepository;
	}

	/**
	 * Returns the <code>ContentManager</code> that is used to store and
	 * retrieve the content.
	 *
	 * @return the <code>ContentManager</code>
	 * @since 1.0
	 */
	public ContentManager getContentManager()
	{
		return mContentManager;
	}
	
	/**
	 * Moves the row that corresponds to the provided bean instance according
	 * to a property with an ordinal constraint.
	 *
	 * @param bean the bean instance that corresponds to the row that has to
	 * be moved
	 * @param propertyName the name of the property with an ordinal constraint
	 * @param direction {@link OrdinalManager#UP} or {@link
	 * OrdinalManager#DOWN}
	 * @return <code>true</code> if the row was moved successfully; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean move(Constrained bean, String propertyName, OrdinalManager.Direction direction)
	{
		if (null == bean)               throw new IllegalArgumentException("constrained can't be nul");
		if (null == propertyName)       throw new IllegalArgumentException("propertyName can't be null");
		if (0 == propertyName.length()) throw new IllegalArgumentException("propertyName can't be empty");

		ConstrainedProperty property = bean.getConstrainedProperty(propertyName);
		if (null == property)
		{
			throw new UnknownConstrainedPropertyException(bean.getClass(), propertyName);
		}
		
		if (!property.isOrdinal())
		{
			throw new ExpectedOrdinalConstraintException(bean.getClass(), propertyName);
		}
		
		// obtain the ordinal value
		int ordinal = -1;
		try
		{
			Object ordinal_object = BeanUtils.getPropertyValue(bean, propertyName);
			if (!(ordinal_object instanceof Integer))
			{
				throw new InvalidOrdinalTypeException(bean.getClass(), propertyName);
			}
			ordinal = ((Integer)ordinal_object).intValue();
		}
		catch (BeanUtilsException e)
		{
			throw new UnknownOrdinalException(bean.getClass(), propertyName);
		}

		OrdinalManager ordinals = null;
		
		if (property.hasOrdinalRestriction())
		{
			String restriction_name = property.getOrdinalRestriction();
			
			// initialy the ordinal manager, taking the restriction property into account
			ordinals = new OrdinalManager(getDatasource(), getTable(), propertyName, restriction_name);
			
			// obtain the restriction value
			long restriction = -1;
			try
			{
				Object restriction_object = BeanUtils.getPropertyValue(bean, restriction_name);
				if (null == restriction_object)
				{
					throw new OrdinalRestrictionCantBeNullException(bean.getClass(), property.getPropertyName(), restriction_name);
				}
				if (!(restriction_object instanceof Number))
				{
					throw new InvalidOrdinalRestrictionTypeException(bean.getClass(), propertyName, restriction_name, restriction_object.getClass());
				}
				restriction = ((Number)restriction_object).longValue();
			}
			catch (BeanUtilsException e)
			{
				throw new UnknownOrdinalRestrictionException(bean.getClass(), propertyName, restriction_name);
			}
			
			// obtain a new ordinal, taking the restriction value into account
			return ordinals.move(direction, restriction, ordinal);
		}
		else
		{
			ordinals = new OrdinalManager(getDatasource(), getTable(), propertyName);
			return ordinals.move(direction, ordinal);
		}
	}
	
	/**
	 * Moves the row that corresponds to the provided bean instance upwards
	 * according to a property with an ordinal constraint.
	 *
	 * @param bean the bean instance that corresponds to the row that has to
	 * be moved
	 * @param propertyName the name of the property with an ordinal constraint
	 * @return <code>true</code> if the row was moved successfully; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean up(Constrained bean, String propertyName)
	{
		return move(bean, propertyName, OrdinalManager.UP);
	}
	
	/**
	 * Moves the row that corresponds to the provided bean instance downwards
	 * according to a property with an ordinal constraint.
	 *
	 * @param bean the bean instance that corresponds to the row that has to
	 * be moved
	 * @param propertyName the name of the property with an ordinal constraint
	 * @return <code>true</code> if the row was moved successfully; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean down(Constrained bean, String propertyName)
	{
		return move(bean, propertyName, OrdinalManager.DOWN);
	}

	/**
	 * Empties the content of a certain bean property.
	 * <p>When a bean is saved, <code>null</code> content properties are
	 * simply ignored when the property hasn't got an <code>autoRetrieved</code>
	 * constraint. This is needed to make it possible to only update a
	 * bean's data without having to fetch the content from the back-end and
	 * store it together with the other data just to make a simple update.
	 * However, this makes it impossible to rely on <code>null</code> to
	 * indicate empty content. This method has thus been added explicitly for
	 * this purpose.
	 *
	 * @param bean the bean instance that contains the property
	 * @param propertyName the name of the property whose content has to be
	 * emptied in the database
	 * @return <code>true</code> if the empty content was stored successfully;
	 * or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean storeEmptyContent(final T bean, String propertyName)
	{
		if (null == bean)               throw new IllegalArgumentException("constrained can't be null");
		if (null == propertyName)       throw new IllegalArgumentException("propertyName can't be null");
		if (0 == propertyName.length()) throw new IllegalArgumentException("propertyName can't be empty");

		Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
		if (null == constrained)
		{
			return false;
		}

		int id = getIdentifierValue(bean);
		if (-1 == id)
		{
			throw new MissingIdentifierValueException(bean.getClass(), getIdentifierName());
		}

		ConstrainedProperty property = constrained.getConstrainedProperty(propertyName);
		if (null == property)
		{
			throw new UnknownConstrainedPropertyException(bean.getClass(), propertyName);
		}

		if (!property.hasMimeType())
		{
			throw new ExpectedMimeTypeConstraintException(bean.getClass(), propertyName);
		}

		try
		{
			Content content = new Content(property.getMimeType(), null)
				.fragment(property.isFragment())
				.name(property.getName())
				.attributes(property.getContentAttributes())
				.cachedLoadedData(property.getCachedLoadedData());

			return mContentManager.storeContent(buildCmfPath(constrained, id, property.getPropertyName()),
												content,
												property.getTransformer());
		}
		catch (ContentManagerException e)
		{
			throw new DatabaseException(e);
		}
	}

	/**
	 * Saves a bean.
	 * <p>This augments the regular <code>GenericQueryManager</code>'s
	 * <code>save</code> method with behaviour that correctly handles content
	 * or ordinal properties.
	 * When a bean is saved, <code>null</code> content properties are simply
	 * ignored when the property hasn't got an <code>autoRetrieved</code>
	 * constraint. This is needed to make it possible to only update a bean's
	 * data without having to fetch the content from the back-end and store it
	 * together with the other data just to make a simple update.
	 *
	 * @param bean the bean instance that has to be saved
	 * @return <code>true</code> if the bean was stored successfully; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public int save(final T bean)
	throws DatabaseException
	{
		return (Integer)mDbQueryManager.inTransaction(new DbTransactionUser() {
				public Integer useTransaction()
				throws InnerClassException
				{
					int id = -1;

					// determine if it's an update or an insert
					boolean update = false;
					id = getIdentifierValue(bean);
					if (id >= 0)
					{
						update = true;
					}

					// handle the pre-storage constraints logic
					Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
					Collection<ConstrainedProperty> properties = null;
					if (constrained != null)
					{
						properties = constrained.getConstrainedProperties();

						for (ConstrainedProperty property : properties)
						{
							if (!update)
							{
								if (property.isOrdinal())
								{
									try
									{
										OrdinalManager ordinals = null;

										int new_ordinal = -1;

										if (property.hasOrdinalRestriction())
										{
											String restriction_name = property.getOrdinalRestriction();

											// initialize the ordinal manager, taking the restriction property into account
											ordinals = new OrdinalManager(getDatasource(), getTable(), property.getPropertyName(), restriction_name);

											// obtain the restriction value
											long restriction = -1;
											try
											{
												Object restriction_object = BeanUtils.getPropertyValue(bean, restriction_name);
												if (null == restriction_object)
												{
													throw new OrdinalRestrictionCantBeNullException(bean.getClass(), property.getPropertyName(), restriction_name);
												}
												if (!(restriction_object instanceof Number))
												{
													throw new InvalidOrdinalRestrictionTypeException(bean.getClass(), property.getPropertyName(), restriction_name, restriction_object.getClass());
												}
												restriction = ((Number)restriction_object).longValue();
											}
											catch (BeanUtilsException e)
											{
												throw new UnknownOrdinalRestrictionException(bean.getClass(), property.getPropertyName(), restriction_name);
											}

											// obtain a new ordinal, taking the restriction value into account
											new_ordinal = ordinals.obtainInsertOrdinal(restriction);
										}
										else
										{
											ordinals = new OrdinalManager(getDatasource(), getTable(), property.getPropertyName());
											new_ordinal = ordinals.obtainInsertOrdinal();
										}
										BeanUtils.setPropertyValue(bean, property.getPropertyName(), new_ordinal);
									}
									catch (BeanUtilsException e)
									{
										throw new DatabaseException(e);
									}
								}
							}
						}
					}

					// store the new bean or update it
					id = ContentQueryManager.super.save(bean);

					return id;
				}
			});
	}
	
	/**
	 * Restores a bean according to its ID.
	 * <p>This augments the regular <code>GenericQueryManager</code>'s
	 * <code>restore</code> method with behaviour that correctly handles
	 * content properties.
	 *
	 * @param objectId the ID of the bean that has to be restored
	 * @return the bean instance if it was restored successfully; or
	 * <p><code>null</code> if it couldn't be found
	 * @since 1.0
	 */
	public T restore(int objectId)
	throws DatabaseException
	{
		return super.restore(objectId);
	}
	
	/**
	 * Restores the first bean from a <code>RestoreQuery</code>.
	 * <p>This augments the regular <code>GenericQueryManager</code>'s
	 * <code>restore</code> method with behaviour that correctly handles
	 * content properties.
	 *
	 * @param query the query that will be used to restore the beans
	 * @return the first bean instance that was found; or
	 * <p><code>null</code> if no beans could be found
	 * @since 1.0
	 */
	public T restoreFirst(RestoreQuery query)
	throws DatabaseException
	{
		return super.restoreFirst(query);
	}
	
	/**
	 * Restores all beans.
	 * <p>This augments the regular <code>GenericQueryManager</code>'s
	 * <code>restore</code> method with behaviour that correctly handles
	 * content properties.
	 *
	 * @return the list of beans; or
	 * <p><code>null</code> if no beans could be found
	 * @since 1.0
	 */
	public List<T> restore()
	throws DatabaseException
	{
		return super.restore();
	}
	
	/**
	 * Restores all beans from a <code>RestoreQuery</code>.
	 * <p>This augments the regular <code>GenericQueryManager</code>'s
	 * <code>restore</code> method with behaviour that correctly handles
	 * content properties.
	 *
	 * @param query the query that will be used to restore the beans
	 * @return the list of beans; or
	 * <p><code>null</code> if no beans could be found
	 * @since 1.0
	 */
	public List<T> restore(RestoreQuery query)
	throws DatabaseException
	{
		return super.restore(query);
	}

	private void restoreContent(int objectId, final T bean)
	{
		Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
		if (constrained != null)
		{
			Collection<ConstrainedProperty> properties = constrained.getConstrainedProperties();
			for (ConstrainedProperty property : properties)
			{
				if (property.hasMimeType() &&
					property.isAutoRetrieved())
				{
					mContentManager.useContentData(buildCmfPath(constrained, objectId, property.getPropertyName()), new ContentDataUser<Object, ConstrainedProperty>(property) {
								public Object useContentData(Object contentData)
								throws InnerClassException
								{
									try
									{
										BeanUtils.setPropertyValue(bean, getData().getPropertyName(), contentData);
									}
									catch (BeanUtilsException e)
									{
										throw new DatabaseException(e);
									}
									catch (ContentManagerException e)
									{
										throw new DatabaseException(e);
									}
									return null;
								}
							});
				}
			}
		}
	}
	
	/**
	 * Deletes a bean according to its ID.
	 * <p>This augments the regular <code>GenericQueryManager</code>'s
	 * <code>restore</code> method with behaviour that correctly handles
	 * content and ordinal properties.
	 *
	 * @param objectId the ID of the bean that has to be restored
	 * @return <code>true</code> if the bean was deleted successfully; or
	 * <p><code>false</code> if it couldn't be found
	 * @since 1.0
	 */
	public boolean delete(final int objectId)
	throws DatabaseException
	{
		Boolean result = mDbQueryManager.inTransaction(new DbTransactionUser() {
				public Boolean useTransaction()
				throws InnerClassException
				{
					T bean = restore(objectId);
					if (null == bean)
					{
						return false;
					}
					
					mDeletedbean.set(bean);
					try
					{
						if (ContentQueryManager.super.delete(objectId))
						{
							return true;
						}
					}
					finally
					{
						mDeletedbean.set(null);
					}
					
					return false;
				}
			});
		
		return null != result && result.booleanValue();
	}
	
	/**
	 * Checks if there's content available for a certain property of a bean.
	 *
	 * @param bean the bean instance that will be checked
	 * @param propertyName the name of the property whose content availability
	 * will be checked
	 * @return <code>true</code> if content is available; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean hasContent(T bean, String propertyName)
	throws DatabaseException
	{
		Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);

		return hasContent(constrained, getIdentifierValue(bean), propertyName);
	}
	
	/**
	 * Checks if there's content available for a certain property of a bean.
	 *
	 * @param objectId the ID of the bean instance that will be checked
	 * @param propertyName the name of the property whose content availability
	 * will be checked
	 * @return <code>true</code> if content is available; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean hasContent(int objectId, String propertyName)
	throws DatabaseException
	{
		Constrained constrained = ConstrainedUtils.getConstrainedInstance(mClass);

		return hasContent(constrained, objectId, propertyName);
	}
	
	private boolean hasContent(Constrained constrained, int objectId, String propertyName)
	throws DatabaseException
	{
		try
		{
			return mContentManager.hasContentData(buildCmfPath(constrained, objectId, propertyName));
		}
		catch (ContentManagerException e)
		{
			throw new DatabaseException(e);
		}
	}

	/**
	 * Builds the path that is used by the <code>ContentQueryManager</code>
	 * for a certain bean and property.
	 *
	 * @param bean the bean instance that will be used to construct the path
	 * @param propertyName the name of the property that will be used to
	 * construct the path
	 * @return the requested path
	 * @since 1.0
	 */
	public String buildCmfPath(T bean, String propertyName)
	{
		Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
		
		return buildCmfPath(constrained, getIdentifierValue(bean), propertyName, true);
	}
	
	/**
	 * Builds the path that is used by the <code>ContentQueryManager</code>
	 * for a certain bean ID and property.
	 *
	 * @param objectId the bean ID that will be used to construct the path
	 * @param propertyName the name of the property that will be used to
	 * construct the path
	 * @return the requested path
	 * @since 1.0
	 */
	public String buildCmfPath(int objectId, String propertyName)
	{
		Constrained constrained = ConstrainedUtils.getConstrainedInstance(mClass);

		return buildCmfPath(constrained, objectId, propertyName, true);
	}
	
	/**
	 * Builds the path that is used by the <code>ServeContent</code> element
	 * for a certain bean and property.
	 * <p>Any declaration of the repository name will be ignore, since the
	 * <code>ServeContent</code> element doesn't allow you to provide this
	 * through the URL for safety reasons.
	 *
	 * @param bean the bean instance that will be used to construct the path
	 * @param propertyName the name of the property that will be used to
	 * construct the path
	 * @return the requested path
	 * @since 1.4
	 */
	public String buildServeContentPath(T bean, String propertyName)
	{
		Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
		
		return buildCmfPath(constrained, getIdentifierValue(bean), propertyName, false);
	}
	
	/**
	 * Builds the path that is used by the <code>ServeContent</code> element
	 * for a certain bean ID and property.
	 * <p>Any declaration of the repository name will be ignore, since the
	 * <code>ServeContent</code> element doesn't allow you to provide this
	 * through the URL for safety reasons.
	 *
	 * @param objectId the bean ID that will be used to construct the path
	 * @param propertyName the name of the property that will be used to
	 * construct the path
	 * @return the requested path
	 * @since 1.4
	 */
	public String buildServeContentPath(int objectId, String propertyName)
	{
		Constrained constrained = ConstrainedUtils.getConstrainedInstance(mClass);
		
		return buildCmfPath(constrained, objectId, propertyName, false);
	}
	
	private String buildCmfPath(Constrained constrained, int objectId, String propertyName)
	{
		return buildCmfPath(constrained, objectId, propertyName, true);
	}
	
	private String buildCmfPath(Constrained constrained, int objectId, String propertyName, boolean useRepository)
	{
		String repository = null;
		if (useRepository)
		{
			if (mRepository != null)
			{
				repository = mRepository;
			}
			if (constrained != null)
			{
				ConstrainedProperty property = constrained.getConstrainedProperty(propertyName);
				if (property != null &&
					property.hasRepository())
				{
					repository = property.getRepository();
				}
			}
		}
		
		StringBuilder path = new StringBuilder("");
		if (repository != null &&
			repository.length() > 0)
		{
			path.append(repository);
			path.append(":");
		}
		path.append("/");
		String classname = mBackendClass.getName();
		classname = classname.substring(classname.lastIndexOf(".")+1);
		path.append(StringUtils.encodeUrl(classname));
		path.append("/");
		path.append(objectId);
		path.append("/");
		path.append(StringUtils.encodeUrl(propertyName));
		
		return path.toString().toLowerCase();
	}
	
	/**
	 * Retrieves a content data representation for use in html.
	 * <p>This is mainly used to integrate content data inside a html
	 * document. For instance, html content will be displayed as-is, while
	 * image content will cause an image tag to be generated with the correct
	 * source URL to serve the image.
	 *
	 * @param bean the bean instance that contains the data
	 * @param propertyName the name of the property whose html representation
	 * will be provided
	 * @param element an active element instance
	 * @param serveContentExitName the exit name that leads to a {@link
	 * com.uwyn.rife.cmf.elements.ServeContent ServeContent} element. This will
	 * be used to generate URLs for content that can't be directly displayed
	 * in-line.
	 * @return the html content representation
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public String getContentForHtml(T bean, String propertyName, Element element, String serveContentExitName)
	throws ContentManagerException
	{
		return getContentManager().getContentForHtml(buildCmfPath(bean, propertyName), element, serveContentExitName);
	}
	
	/**
	 * Retrieves a content data representation for use in html.
	 * <p>This is mainly used to integrate content data inside a html
	 * document. For instance, html content will be displayed as-is, while
	 * image content will cause an image tag to be generated with the correct
	 * source URL to serve the image.
	 *
	 * @param objectId the ID of the bean that contains the data
	 * @param propertyName the name of the property whose html representation
	 * will be provided
	 * @param element an active element instance
	 * @param serveContentExitName the exit name that leads to a {@link
	 * com.uwyn.rife.cmf.elements.ServeContent ServeContent} element. This will
	 * be used to generate URLs for content that can't be directly displayed
	 * in-line.
	 * @return the html content representation
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public String getContentForHtml(int objectId, String propertyName, Element element, String serveContentExitName)
	throws ContentManagerException
	{
		return getContentManager().getContentForHtml(buildCmfPath(objectId, propertyName), element, serveContentExitName);
	}
	
	/**
	 * Simply clones the instance with the default clone method. This creates
	 * a shallow copy of all fields and the clone will in fact just be another
	 * reference to the same underlying data. The independence of each cloned
	 * instance is consciously not respected since they rely on resources that
	 * can't be cloned.
	 *
	 * @since 1.0
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.cmf").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
			///CLOVER:ON
		}
	}
	
	class Listener implements GenericQueryManagerListener<T>
	{
		public void installed() {}
		public void removed() {}
		
		public void inserted(T bean)
		{
			saved(bean);
		}
		
		public void updated(T bean)
		{
			saved(bean);
		}
		
		public void saved(T bean)
		{
			Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
			Collection<ConstrainedProperty> properties = null;
			if (constrained != null)
			{
				properties = constrained.getConstrainedProperties();
			}
			
			// process the properties that have to be handled after the saving of the bean
			if (properties != null)
			{
				int id = getIdentifierValue(bean);
				
				for (ConstrainedProperty property : properties)
				{
					if (property.hasMimeType())
					{
						try
						{
							Object value = BeanUtils.getPropertyValue(bean, property.getPropertyName());
							if (value != null ||
								property.isAutoRetrieved())
							{
								Content content = new Content(property.getMimeType(), value)
									.fragment(property.isFragment())
									.name(property.getName())
									.attributes(property.getContentAttributes())
									.cachedLoadedData(property.getCachedLoadedData());
								
								mContentManager.storeContent(buildCmfPath(constrained, id, property.getPropertyName()),
															 content,
															 property.getTransformer());
							}
						}
						catch (BeanUtilsException e)
						{
							throw new DatabaseException(e);
						}
						catch (ContentManagerException e)
						{
							throw new DatabaseException(e);
						}
					}
				}
			}
		}
		
		public void restored(T bean)
		{
			restoreContent(getIdentifierValue(bean), bean);
		}
		
		public void deleted(int objectId)
		{
			T bean = mDeletedbean.get();
			if (null == bean)
			{
				return;
			}
			
			Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
			if (constrained != null)
			{
				Collection<ConstrainedProperty> properties = constrained.getConstrainedProperties();
				for (ConstrainedProperty property : properties)
				{
					if (property.hasMimeType())
					{
						mContentManager.deleteContent(buildCmfPath(constrained, objectId, property.getPropertyName()));
					}
					else if (property.isOrdinal())
					{
						OrdinalManager ordinals = null;
						if (property.hasOrdinalRestriction())
						{
							String restriction_name = property.getOrdinalRestriction();
							
							// initialize the ordinal manager, taking the restriction property into account
							ordinals = new OrdinalManager(getDatasource(), getTable(), property.getPropertyName(), restriction_name);
							
							// obtain the restriction value
							long restriction = -1;
							try
							{
								Object restriction_object = BeanUtils.getPropertyValue(bean, restriction_name);
								if (null == restriction_object)
								{
									throw new OrdinalRestrictionCantBeNullException(bean.getClass(), property.getPropertyName(), restriction_name);
								}
								if (!(restriction_object instanceof Number))
								{
									throw new InvalidOrdinalRestrictionTypeException(bean.getClass(), property.getPropertyName(), restriction_name, restriction_object.getClass());
								}
								restriction = ((Number)restriction_object).longValue();
							}
							catch (BeanUtilsException e)
							{
								throw new UnknownOrdinalRestrictionException(bean.getClass(), property.getPropertyName(), restriction_name);
							}
							
							// tighten the remaining ordinals, taking the restriction value into account
							ordinals.tighten(restriction);
						}
						else
						{
							ordinals = new OrdinalManager(getDatasource(), getTable(), property.getPropertyName());
							ordinals.tighten();
						}
					}
				}
			}
		}
	}

	public <OtherBeanType> GenericQueryManager<OtherBeanType> createNewManager(Class<OtherBeanType> type)
	{
		return new ContentQueryManager(getDatasource(), type, mContentManager);
	}
}
