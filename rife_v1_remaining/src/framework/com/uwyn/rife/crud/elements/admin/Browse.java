/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Browse.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentQueryManager;
import com.uwyn.rife.cmf.dam.OrdinalManager;
import static com.uwyn.rife.crud.CrudPropertyNames.*;
import com.uwyn.rife.crud.CrudSiteProcessor;
import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.crud.templates.BrowseTemplateTransformer;
import com.uwyn.rife.database.DbBeanFetcher;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.DbTransactionUserWithoutResult;
import com.uwyn.rife.database.querymanagers.generic.CountQuery;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerFactory;
import com.uwyn.rife.database.querymanagers.generic.RestoreQuery;
import com.uwyn.rife.engine.ElementContext;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.site.*;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.ConversionException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Browse extends CrudElement
{
	public AdminTemplateTransformer getTransformer()
	{
		return new BrowseTemplateTransformer(this);
	}
	
	public void processElement()
	throws EngineException
	{
		final Template template = getTemplate();
		
		new DbQueryManager(getDatasource()).inTransaction(new DbTransactionUserWithoutResult() {
				public void useTransactionWithoutResult()
				throws InnerClassException
				{
					// setup the paging variables
					int paging_limit = Convert.toInt(getProperty(PAGING_LIMIT), DEFAULT_PAGING_LIMIT);
					int paging_span = Convert.toInt(getProperty(PAGING_SPAN), DEFAULT_PAGING_SPAN);
					
					ContentQueryManager manager = getContentQueryManager();

					CountQuery		count_query = manager.getCountQuery();
					RestoreQuery	restore_query = manager.getRestoreQuery();
					
					prepareQueries(count_query, restore_query, paging_limit);
					
					final int count = countEntities(manager, count_query);
					if (0 == count)
					{
						template.setBlock("crud_content", "crud_content_none");
					}
					else
					{
						if (paging_limit > 0)
						{
							// generated the paged navigation
							PagedNavigation.generateNavigation(Browse.this, template, count, paging_limit, getPagingOffset(), paging_span);
						}
						else
						{
							template.setValue("ranged_table_area", "");
						}
						
						// retrieve and output the beans
						restoreEntities(template, count, manager, restore_query);
					}
				}
			});
		
		print(template);
	}
	
	protected void prepareQueries(CountQuery countQuery, RestoreQuery restoreQuery, int pagingLimit)
	{
		// if this is an association that's tied through a many-to-one property to another class
		// filter the search on the column value
		ConstrainedProperty manytoone_property = getManytooneConstrainedProperty();
		if (manytoone_property != null)
		{
			ConstrainedProperty.ManyToOne manytoone_constraint = manytoone_property.getManyToOne();
			String input_name = manytoone_constraint.getDerivedTable() + manytoone_constraint.getColumn();
			if (hasInputValue(input_name))
			{
				try
				{
					Class property_type = BeanUtils.getPropertyType(getBeanClass(), manytoone_property.getPropertyName());
					Object property_value = Convert.toType(getInput(input_name), property_type);
					
					countQuery.where(manytoone_property.getPropertyName(), "=", property_value);
					restoreQuery.where(manytoone_property.getPropertyName(), "=", property_value);
				}
				catch (ConversionException e)
				{
					throw new EngineException(e);
				}
				catch (BeanUtilsException e)
				{
					throw new EngineException(e);
				}
			}
		}
		
		// add the paging query parts
		if (pagingLimit > 0)
		{
			restoreQuery.limit(pagingLimit);
		}
		
		if (getPagingOffset() > 0)
		{
			restoreQuery.offset(getPagingOffset());
		}
	}
	
	protected int countEntities(ContentQueryManager manager, CountQuery countQuery)
	{
		return manager.count(countQuery);
	}
	
	protected void restoreEntities(Template template, int count, ContentQueryManager manager, RestoreQuery restore_query)
	{
		manager.restore(restore_query, new CrudBeanFetcher(template, count));
	}
	
	protected int getPagingOffset()
	{
		int offset = -1;
		if (hasInputValue("offset"))
		{
			offset = getInputInt("offset");
		}
		else
		{
			offset = 0;
		}
		
		return offset;
	}
	
	public class CrudBeanFetcher extends DbBeanFetcher
	{
		protected Map<Class, String>				mAssociationsColumns = null;
		protected Map<Class, GenericQueryManager>	mAssociationQuerymanagers = null;
		protected Map<String, Map<String, String>>	mManyToOneIdentifiers = null;
		protected String							mIdentityProperty = null;
		
		protected Template	mTemplate = null;
		protected int		mEntityCount = 0;

		protected Object	mIdentity = null;
		protected int		mCounter = 1;
		
		public CrudBeanFetcher(Template template, int entityCount)
		{
			super(getDatasource(), getBeanClass());
			mTemplate = template;
			mEntityCount = entityCount;
			
			mAssociationsColumns = prepareAssociationColumns();
			mAssociationQuerymanagers = prepareAssociationQueryManagers();
			mManyToOneIdentifiers = prepareManyToOneIdentifiers(template);
			
			mIdentityProperty = ConstrainedUtils.getIdentityProperty(getBeanClass());
		}
		
		protected Map<Class, String> prepareAssociationColumns()
		{
			return (Map<Class, String>)getProperty(getCrudPrefix() + "-associations_columns");
		}
		
		protected Map<Class, GenericQueryManager> prepareAssociationQueryManagers()
		{
			// prepare for associations and collect related data
			if (mAssociationsColumns != null)
			{
				Map<Class, GenericQueryManager> managers = new HashMap<Class, GenericQueryManager>();
				for (Class association : mAssociationsColumns.keySet())
				{
					managers.put(association, GenericQueryManagerFactory.getInstance(getDatasource(), association));
				}
				
				return managers;
			}
			else
			{
				return null;
			}
		}
		
		protected Map<String, Map<String, String>> prepareManyToOneIdentifiers(Template template)
		{
			// prepare for many-to-one textual identifiers
			Constrained constrained = getDefaultBeanConstrainedInstance();
			Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
			if (constrained != null)
			{
				for (ConstrainedProperty property : (Collection<ConstrainedProperty>)constrained.getConstrainedProperties())
				{
					// if the property isn'template present in the template, it doesn'template need to be output
					if (!template.hasValueId(property.getPropertyName()))
					{
						continue;
					}
					
					// get the textual identifier map and store it for this property
					Map<String, String> many_to_one_map = generateManyToOneIdentifiers(property);
					if (many_to_one_map != null &&
						many_to_one_map.size() > 0)
					{
						result.put(property.getPropertyName(), many_to_one_map);
					}
				}
			}
			
			return result;
		}
		
		public boolean gotBeanInstance(Object instance)
		{
			// output the bean values
			mTemplate.setBean(instance);
			
			// handle constraints
			Constrained<ConstrainedBean, ConstrainedProperty> constrained = ConstrainedUtils.makeConstrainedInstance(instance);
			if (constrained != null)
			{
				// display the properties in the correct order
				ContentQueryManager	manager = getContentQueryManager();
				for (ConstrainedProperty property : constrained.getConstrainedProperties())
				{
					// handle many-to-one properties
					displayConstrainedProperty(mTemplate, manager, constrained, property);
				}
			}
			
			try
			{
				mIdentity = BeanUtils.getPropertyValue(instance, mIdentityProperty);
			}
			catch (BeanUtilsException e)
			{
				throw new EngineException(e);
			}
			
			setOutput(getIdentityVarName(), String.valueOf(mIdentity));

			String exit_edit = getCrudPrefix()+"-edit";
			if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_QUERY+exit_edit))
			{
				setExitQuery(mTemplate, exit_edit);
			}
			else if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_FORM+exit_edit))
			{
				setExitForm(mTemplate, exit_edit);
			}
			
			String exit_delete = getCrudPrefix()+"-delete";
			if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_QUERY+exit_delete))
			{
				setExitQuery(mTemplate, exit_delete);
			}
			else if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_FORM+exit_delete))
			{
				setExitForm(mTemplate, exit_delete);
			}
			
			displayMoveButtons(mTemplate, mEntityCount, mCounter);
			displayAssociations(mTemplate);
			
			appendTableRow(mTemplate);
			
			mCounter++;
			
			return true;
		}
		
		protected void displayConstrainedProperty(Template template, ContentQueryManager manager, Constrained<ConstrainedBean, ConstrainedProperty> constrained, ConstrainedProperty property)
		{
			if (mManyToOneIdentifiers.containsKey(property.getPropertyName()))
			{
				Map<String, String> many_to_one_map = mManyToOneIdentifiers.get(property.getPropertyName());
				try
				{
					Object value = BeanUtils.getPropertyValue(constrained, property.getPropertyName());
					if (value != null)
					{
						String value_string = String.valueOf(value);
						if (many_to_one_map.containsKey(value_string))
						{
							template.setValue(property.getPropertyName(), template.getEncoder().encode(many_to_one_map.get(value_string)));
						}
					}
				}
				catch (BeanUtilsException e)
				{
					// if the property bean value couldn'template be retrieved, use the default value
					// and don'template try to obtain the textual identifier
				}
			}
			
			// handle cmf constraints
			if (!property.isAutoRetrieved())
			{
				MimeType	mimetype = property.getMimeType();
				if (mimetype != null)
				{
					if (0 == mimetype.toString().indexOf("image/"))
					{
						ContentInfo info = manager.getContentManager().getContentInfo(manager.buildCmfPath(constrained, property.getPropertyName()));
						displayImageProperty(template, property, info, "");
					}
					else if (template.hasValueId(property.getPropertyName() + "-url") &&
							 MimeType.RAW == mimetype)
					{
						String cmf_path = manager.buildCmfPath(constrained, property.getPropertyName());
						displayRawProperty(template, property, cmf_path, "");
					}
				}
			}
		}


		protected void displayMoveButtons(Template template, int entityCount, int counter)
		{
			// handle ordinal move buttons
			if (hasProperty(getCrudPrefix() + "-ordinal_property_name"))
			{
				if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_QUERY+"up"))
				{
					setExitQuery(template, "up", new String[] {"direction", OrdinalManager.UP.toString()});
				}
				else if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_FORM+"up"))
				{
					setExitForm(template, "up", new String[] {"direction", OrdinalManager.UP.toString()});
				}
				if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_QUERY+"down"))
				{
					setExitQuery(template, "down", new String[] {"direction", OrdinalManager.DOWN.toString()});
				}
				else if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_FORM+"down"))
				{
					setExitForm(template, "down", new String[] {"direction", OrdinalManager.DOWN.toString()});
				}
				
				int offsetted_counter = counter+getPagingOffset();
				if (1 == offsetted_counter)
				{
					template.setBlock("move_disabled_up", "move_disabled");
				}
				if (offsetted_counter == entityCount)
				{
					template.setBlock("move_disabled_down", "move_disabled");
				}
			}
		}
		
		protected void displayAssociations(Template template)
		{
			if (mAssociationsColumns != null)
			{
				GenericQueryManager gqm = null;
				Class association = null;
				int counter = 1;
				for (Map.Entry<Class, String> association_column : mAssociationsColumns.entrySet())
				{
					association = association_column.getKey();
					gqm = mAssociationQuerymanagers.get(association);
					int count = gqm.count(gqm.getCountQuery()
										  .where(association_column.getValue(), "=", mIdentity));
					template.setValue("association_entrycount" + counter, count);

					String exit_browse = CrudSiteProcessor.CRUD_PREFIX + association.getName() + "-browse";
					if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_QUERY+exit_browse))
					{
						setExitQuery(template, exit_browse);
					}
					else if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_FORM+exit_browse))
					{
						setExitForm(template, exit_browse);
					}
					
					String exit_add = CrudSiteProcessor.CRUD_PREFIX + association.getName() + "-add";
					if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_QUERY+exit_add))
					{
						setExitQuery(template, exit_add);
					}
					else if (mTemplate.hasValueId(ElementContext.PREFIX_EXIT_FORM+exit_add))
					{
						setExitForm(template, exit_add);
					}
					
					counter++;
				}
			}
		}
		
		protected void appendTableRow(Template template)
		{
			template.appendBlock("rows", "row");
			
			// clear the ordinal move buttons that are disabled
			if (hasProperty(getCrudPrefix() + "-ordinal_property_name"))
			{
				template.removeValue("move_disabled_up");
				template.removeValue("move_disabled_down");
			}
		}
	}
}

