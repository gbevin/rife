/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Add.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.site.*;

import com.uwyn.rife.cmf.dam.ContentQueryManager;
import com.uwyn.rife.crud.CrudSiteProcessor;
import com.uwyn.rife.crud.templates.AddTemplateTransformer;
import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.ConversionException;

public class Add extends CrudElement
{
	public AdminTemplateTransformer getTransformer()
	{
		return new AddTemplateTransformer(this);
	}
	
	public void processElement()
	{
		Template template = getTemplate();
		
		ContentQueryManager	manager = getContentQueryManager();
		
		if (hasSubmission(getSubmissionName()))
		{
			handleSubmission(template, manager);
		}
		else
		{
			displayInitialForm(template);
		}
		
		print(template);
	}

	protected void displayInitialForm(Template template)
	{
		Object instance = getDefaultBeanConstrainedInstance();
		if (null == instance)
		{
			try
			{
				instance = getBeanClass().newInstance();
			}
			catch (Exception e)
			{
				throw new EngineException(e);
			}
		}
		displayAddForm(template, instance);
	}

	protected void displayAddForm(Template template, Object instance)
	{
		Constrained<ConstrainedBean, ConstrainedProperty> constrained = ConstrainedUtils.makeConstrainedInstance(instance);
		generateManyToOneSelectFields(template, constrained);
		
		generateForm(template, instance);
	}
	
	protected void handleSubmission(Template template, ContentQueryManager manager)
	{
		Object bean = getSubmittedEntity();
		
		injectManyToOnePropertyValue(bean);
		
		if (bean instanceof Validated)
		{
			// validate the bean
			Validated validated = (Validated)bean;
			validateEntity(manager, validated);
			
			// handle validation errors
			if (0 == validated.countValidationErrors())
			{
				saveAddedEntity(template, validated);
			}
			else
			{
				displaySubmissionForm(template, bean);
			}
		}
		else
		{
			saveAddedEntity(template, bean);
		}
	}

	protected void displaySubmissionForm(Template template, Object bean)
	{
		displayAddForm(template, bean);
	}

	protected Object getSubmittedEntity() throws EngineException
	{
		return getSubmissionBean(getBeanClass());
	}
	
	/**
	 * Detect the many to one property that should be auto-set from an input that
	 * contain the value of the associated table column
	 *
	 * @param    bean                an Object
	 */
	protected void injectManyToOnePropertyValue(Object bean)
	{
		String manytoone_property_name = getPropertyString(CrudSiteProcessor.IDENTIFIER_MANYTOONE_PROPERTYNAME);
		if (manytoone_property_name != null)
		{
			Constrained constrained = ConstrainedUtils.makeConstrainedInstance(bean);
			ConstrainedProperty manytoone_property = constrained.getConstrainedProperty(manytoone_property_name);
			ConstrainedProperty.ManyToOne manytoone_constraint = manytoone_property.getManyToOne();
			String input_name = manytoone_constraint.getDerivedTable() + manytoone_constraint.getColumn();
			if (hasInputValue(input_name))
			{
				try
				{
					Class property_type = BeanUtils.getPropertyType(getBeanClass(), manytoone_property_name);
					Object property_value = Convert.toType(getInput(input_name), property_type);
					BeanUtils.setPropertyValue(bean, manytoone_property_name, property_value);
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
	}
	
	protected boolean validateEntity(ContentQueryManager manager, Validated validated)
	{
		return validated.validate((ValidationContext)manager);
	}
	
	protected void saveAddedEntity(Template template, Object bean)
	{
		if (getContentQueryManager().save(bean) != -1)
		{
			if (template.hasValueId("document_attributes"))
			{
				template.setBlock("document_attributes", "document_attributes_success");
			}
			template.setBlock("crud_content", "crud_content_added");
		}
		else
		{
			// todo: handle addition problem
		}
	}
}

