/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Edit.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentQueryManager;
import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.crud.templates.EditTemplateTransformer;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.site.*;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

public class Edit extends CrudElement
{
	private	int		mIdentityValue = -1;
	private Object	mBeanInstance = null;
	
	public AdminTemplateTransformer getTransformer()
	{
		return new EditTemplateTransformer(this);
	}
	
	public void initialize()
	{
		super.initialize();
		
		mIdentityValue = getIdentityValue();
		if (mIdentityValue >= 0)
		{
			mBeanInstance = getBeanInstance();
		}
		if (null == mBeanInstance)
		{
			exit(getCrudPrefix()+"-home");
		}
	}
	
	protected int getIdentityValue()
	{
		return getInputInt(getIdentityVarName(), -1);
	}
	
	protected Object getBeanInstance()
	{
		return getContentQueryManager().restore(mIdentityValue);
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
			displayEditForm(template, manager, mBeanInstance);
		}
		
		print(template);
	}

	protected void handleSubmission(Template template, ContentQueryManager manager)
	{
		fillSubmissionBean(mBeanInstance);
		
		String identity_property = ConstrainedUtils.getIdentityProperty(getBeanClass());
		// todo: detect inexistent id
		try
		{
			BeanUtils.setPropertyValue(mBeanInstance, identity_property, mIdentityValue);
		}
		catch (BeanUtilsException e)
		{
			throw new EngineException(e);
		}
		
		if (mBeanInstance instanceof Validated)
		{
			// validate the bean
			Validated validated = (Validated)mBeanInstance;
			validateEntity(manager, validated);
			
			// handle validatione errors
			if (0 == validated.countValidationErrors())
			{
				saveEditedEntity(template, validated);
			}
			else
			{
				displayEditForm(template, manager, validated);
			}
		}
		else
		{
			saveEditedEntity(template, mBeanInstance);
		}
	}

	protected boolean validateEntity(ContentQueryManager manager, Validated validated)
	{
		validated.validate((ValidationContext)manager);
		
		// make subject mandatory errors valid if it's handled by the
		// the cmf and not auto retrieved
		Constrained<ConstrainedBean, ConstrainedProperty> constrained = ConstrainedUtils.makeConstrainedInstance(mBeanInstance);
		if (constrained != null)
		{
			for (ConstrainedProperty property : constrained.getConstrainedProperties())
			{
				if (property.hasMimeType() &&
					!property.isAutoRetrieved())
				{
					validated.makeErrorValid(ValidationError.IDENTIFIER_MANDATORY, property.getPropertyName());
				}
			}
		}
		
		return 0 == validated.countValidationErrors();
	}
	
	protected void saveEditedEntity(Template template, Object entity)
	{
		if (getContentQueryManager().save(entity) != -1)
		{
			if (template.hasValueId("document_attributes"))
			{
				template.setBlock("document_attributes", "document_attributes_success");
			}
			template.setBlock("crud_content", "crud_content_edited");
		}
		else
		{
			// todo: handle edit problem
		}
	}
	
	protected void displayEditForm(Template template, ContentQueryManager manager, Object instance)
	{
		Constrained<ConstrainedBean, ConstrainedProperty> constrained = ConstrainedUtils.makeConstrainedInstance(mBeanInstance);
		generateManyToOneSelectFields(template, constrained);

		generateForm(template, instance);
		
		if (constrained != null)
		{
			template.setBean(mBeanInstance, "existing_");
			
			for (ConstrainedProperty property : constrained.getConstrainedProperties())
			{
				displayConstrainedProperty(template, manager, constrained, property);
			}
		}
	}

	protected void displayConstrainedProperty(Template template, ContentQueryManager manager, Constrained<ConstrainedBean, ConstrainedProperty> constrained, ConstrainedProperty property)
	{
		MimeType	mimetype = property.getMimeType();
		if (mimetype != null)
		{
			if (property.isAutoRetrieved())
			{
				if (mimetype == MimeType.TEXT_PLAIN)
				{
					String valueid = "existing_" + property.getPropertyName();
					if (template.hasValueId(valueid))
					{
						try
						{
							Object property_value = BeanUtils.getPropertyValue(constrained, property.getPropertyName());
							String property_value_string = "";
							if (property_value != null)
							{
								property_value_string = String.valueOf(property_value);
							}
							// todo: properly handle line breaks across template types (<br> for html, nothing special for xml)
							template.setValue(valueid, template.getEncoder().encode(property_value_string));
						}
						catch (BeanUtilsException e)
						{
							throw new EngineException(e);
						}
					}
				}
			}
			else
			{
				if (0 == mimetype.toString().indexOf("image/"))
				{
					ContentInfo info = getContentQueryManager().getContentManager().getContentInfo(manager.buildCmfPath(constrained, property.getPropertyName()));
					displayImageProperty(template, property, info, "existing_");
				}
				else if (MimeType.RAW == mimetype)
				{
					String cmf_path = manager.buildCmfPath(constrained, property.getPropertyName());
					displayRawProperty(template, property, cmf_path, "existing_");
				}
			}
		}
	}
}

