/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Delete.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentQueryManager;
import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.crud.templates.DeleteTemplateTransformer;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

import java.util.Map;

public class Delete extends CrudElement
{
	private int		mIdentityValue = -1;
	private Object	mBeanInstance = null;
	
	public AdminTemplateTransformer getTransformer()
	{
		return new DeleteTemplateTransformer(this, mBeanInstance);
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
		displayEntityData(template, manager, mBeanInstance);
		
		print(template);
	}
	
	protected void displayEntityData(Template template, ContentQueryManager manager, Object instance)
	{
		template.setBean(instance);
		
		Constrained<ConstrainedBean, ConstrainedProperty> constrained = ConstrainedUtils.makeConstrainedInstance(instance);
		if (constrained != null)
		{
			// display the properties in the correct order
			for (ConstrainedProperty property : constrained.getConstrainedProperties())
			{
				displayConstrainedProperty(template, manager, constrained, property);
			}
		}
	}
	
	protected void displayConstrainedProperty(Template template, ContentQueryManager manager, Constrained<ConstrainedBean, ConstrainedProperty> constrained, ConstrainedProperty property)
	{
		// only show the CMF admin properties that can be edited
		if (!property.isEditable())
		{
			return;
		}
		
		// handle many-to-one properties
		Map<String, String> many_to_one_map = generateManyToOneIdentifiers(property);
		if (many_to_one_map != null &&
			many_to_one_map.size() > 0)
		{
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
		
		// handle the cmf constraints
		MimeType	mimetype = property.getMimeType();
		if (mimetype != null)
		{
			if (property.isAutoRetrieved())
			{
				if (mimetype == MimeType.TEXT_PLAIN)
				{
					String valueid = property.getPropertyName();
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
					displayImageProperty(template, property, info, "");
				}
				else if (MimeType.RAW == mimetype)
				{
					String cmf_path = manager.buildCmfPath(constrained, property.getPropertyName());
					displayRawProperty(template, property, cmf_path, "");
				}
			}
		}
	}
	
	public void doConfirm()
	{
		Template template = getTemplate();
		
		if (getContentQueryManager().delete(mIdentityValue))
		{
			if (template.hasValueId("document_attributes"))
			{
				template.setBlock("document_attributes", "document_attributes_success");
			}
			template.setBlock("crud_content", "crud_content_deleted");
		}
		else
		{
			// todo: handle deletion problem
		}
		
		print(template);
	}
}

