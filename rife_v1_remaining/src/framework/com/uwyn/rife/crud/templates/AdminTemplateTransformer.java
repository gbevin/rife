/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AdminTemplateTransformer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.templates;

import com.uwyn.rife.crud.BeanClassFactory;
import com.uwyn.rife.crud.CrudPropertyNames;
import static com.uwyn.rife.crud.CrudPropertyNames.*;
import com.uwyn.rife.crud.CrudSiteProcessor;
import com.uwyn.rife.crud.CrudTemplateFactory;
import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.engine.UrlResource;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.site.*;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.template.TemplateTransformer;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.*;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

import java.io.*;
import java.net.URL;
import java.util.*;

public abstract class AdminTemplateTransformer implements TemplateTransformer
{
	public static final String RIFE_CRUD_TEMPLATE_PREFIX = "rife.crud.";

	protected String			mImplementation = null;
	protected String			mImplementationBase64 = null;
	protected Class				mBeanClass = null;
	protected CrudElement		mElement = null;
	protected ResourceFinder	mResourceFinder = null;

	protected String			mBeanClassName = null;
	protected String			mBeanClassNameEncoded = null;
	protected String			mCrudPrefix = null;
	protected String			mShortClassName = null;
	protected String			mSubmissionName = null;
	
	protected Constrained<ConstrainedBean, ConstrainedProperty>	mConstrained = null;

	public abstract String getSupportedTemplateName();
	public abstract void transformTemplate(Template t);

	protected String buildGroupedTemplateName(String name)
	{
		String group = mElement.getPropertyString(CrudPropertyNames.TEMPLATE_GROUP, "html");
		return RIFE_CRUD_TEMPLATE_PREFIX + group + "." + name;
	}
	
	public String getTemplateNameDifferentiator()
	{
		return null;
	}
	
	public AdminTemplateTransformer(CrudElement element)
	{
		mImplementation = (String)element.getProperty(CrudSiteProcessor.IDENTIFIER_IMPLEMENTATION);
		if (mImplementation != null)
		{
			try
			{
				mImplementationBase64 = Base64.encodeToString(mImplementation.getBytes("UTF-8"), false);
				mImplementationBase64 = StringUtils.replace(mImplementationBase64, "=", "_");
			}
			catch (UnsupportedEncodingException e)
			{
				// not possible, UTF-8 is always supported
			}
		}
		mBeanClass = (Class)element.getProperty(CrudSiteProcessor.IDENTIFIER_CLASS);
		if (mBeanClass != null)
		{
			mBeanClassName = mBeanClass.getName();
			mBeanClassNameEncoded = StringUtils.encodeClassname(mBeanClassName);
			mCrudPrefix = CrudSiteProcessor.CRUD_PREFIX+mBeanClassName;
		}
		mElement = element;
		
		assert mBeanClass != null;
		assert mElement != null;
	}
	
	public String getImplementation()
	{
		return mImplementation;
	}
	
	public Collection<URL> transform(String templateName, URL resource, OutputStream result, String encoding)
	throws TemplateException
	{
		Template t = prepareTemplate(templateName, resource, result);
		if (null == t)
		{
			return null;
		}
		
		// obtain a constrained instance of the class that has to be processed
		if (null == mConstrained)
		{
			mConstrained = ConstrainedUtils.getConstrainedInstance(mBeanClass);
		}
		
		// perform the transformation
		transformTemplate(t);
		transferTemplateToResult(t, templateName, result, encoding);
		
		URL bean_dependency_url = BeanClassFactory.INSTANCE.getClassResource(mImplementation);
		Set<URL> dependencies = new HashSet<URL>();
		dependencies.addAll(t.getDependencies().keySet());
		if (bean_dependency_url != null)
		{
			dependencies.add(bean_dependency_url);
		}
		for (UrlResource site_resource : mElement.getSite().getResourceModificationTimes().keySet())
		{
			dependencies.add(site_resource.getUrl());
		}
		return dependencies;
	}
	
	public Template prepareTemplate(String templateName, URL resource, OutputStream result)
	{
		// compensate for the classname prefix
		String short_name = templateName;
		if (mBeanClass != null &&
			templateName.startsWith(mImplementationBase64))
		{
			short_name = templateName.substring(mImplementationBase64.length());
		}
		
		// standard names
		if (mBeanClassName != null)
		{
			mShortClassName = mBeanClassName.substring(mBeanClassName.lastIndexOf(".")+1).toLowerCase();
			mSubmissionName = mShortClassName+"data";
		}
		
		// compensate for the differentiator
		String differentiator = getTemplateNameDifferentiator();
		if (differentiator != null &&
			differentiator.length() > 0)
		{
			if (short_name.startsWith(differentiator))
			{
				short_name = short_name.substring(differentiator.length());
			}
		}
		
		// compensate for the __crud__ seperator
		if (short_name.length() > CrudTemplateFactory.CRUD_SEPARATOR.length() &&
			short_name.startsWith(CrudTemplateFactory.CRUD_SEPARATOR))
		{
			short_name = short_name.substring(CrudTemplateFactory.CRUD_SEPARATOR.length());
		}
		
		// only transform the CMF admin template
		if (!getSupportedTemplateName().equals(short_name))
		{
			outputRawTemplate(templateName, resource, result);
			
			return null;
		}
		
		// get the generic template
		TemplateFactory base_factory = mElement.getTemplateFactory().getBase();
		Template t = base_factory.get(short_name);
		if (null == t)
		{
			return null;
		}

		// set global values
		if (t.hasValueId("classname"))
		{
			t.setValue("classname", mBeanClassName);
		}
		if (t.hasValueId("classname_encoded"))
		{
			t.setValue("classname_encoded", mBeanClassNameEncoded);
		}
		if (t.hasValueId("submission"))
		{
			t.setValue("submission", mSubmissionName);
		}
		if (t.hasValueId("exit_prefix"))
		{
			t.setValue("exit_prefix", CrudSiteProcessor.CRUD_PREFIX);
		}
		if (t.hasValueId("id_prefix"))
		{
			String home_element_id = mElement.getElementInfo().getFlowLink(mCrudPrefix+CrudSiteProcessor.SUFFIX_EXIT_HOME).getTarget().getId();
			String id_prefix = home_element_id.substring(0, home_element_id.lastIndexOf("."));
			t.setValue("id_prefix", id_prefix);
		}
		
		// adapt to the optional properties
		if (t.hasValueId(TEMPLATE_NAME_BLUEPRINT) &&
			mElement.hasProperty(TEMPLATE_NAME_BLUEPRINT))
		{
			t.setValue(TEMPLATE_NAME_BLUEPRINT, mElement.getPropertyString(TEMPLATE_NAME_BLUEPRINT));
		}
		if (t.hasValueId(TEMPLATE_NAME_RANGEDTABLECOMPONENTS) &&
			mElement.hasProperty(TEMPLATE_NAME_RANGEDTABLECOMPONENTS))
		{
			t.setValue(TEMPLATE_NAME_RANGEDTABLECOMPONENTS, mElement.getPropertyString(TEMPLATE_NAME_RANGEDTABLECOMPONENTS));
		}
		if (t.hasValueId(TEMPLATE_NAME_RANGEDTABLE) &&
			mElement.hasProperty(TEMPLATE_NAME_RANGEDTABLE))
		{
			t.setValue(TEMPLATE_NAME_BLUEPRINT, mElement.getPropertyString(TEMPLATE_NAME_RANGEDTABLE));
		}
		
		return t;
	}

	private void outputRawTemplate(String templateName, URL resource, OutputStream result)
	{
		try
		{
			// just copy over the template content as-is
			FileUtils.copy(resource.openStream(), result);
		}
		catch (IOException e)
		{
			throw new TemplateException("Unexpected opening a stream to the resource '"+resource+"'.", e);
		}
		catch (FileUtilsErrorException e)
		{
			throw new TemplateException("Unexpected error while transferring the transformed content of '"+templateName+"'.", e);
		}
	}

	public List<String> getMandatorySubjects()
	{
		ArrayList<String> mandatory_subjects = new ArrayList<String>();
		if (mConstrained instanceof Validated)
		{
			Validated validated = (Validated)mConstrained;
			validated.validate();
			Set<ValidationError> errors = validated.getValidationErrors();
			for (ValidationError error : errors)
			{
				if (ValidationError.IDENTIFIER_MANDATORY.equals(error.getIdentifier()) &&
					!mandatory_subjects.contains(error.getSubject()))
				{
					mandatory_subjects.add(error.getSubject());
				}
			}
		}
		
		if (mConstrained != null)
		{
			for (ConstrainedProperty property : mConstrained.getConstrainedProperties())
			{
				if (property.isNotNull() &&
					!mandatory_subjects.contains(property.getPropertyName()))
				{
					mandatory_subjects.add(property.getPropertyName());
				}
			}
		}
		
		return mandatory_subjects;
	}

	public List<ConstrainedProperty> getPositionedProperties()
	{
		List<ConstrainedProperty> result = new ArrayList<ConstrainedProperty>();
		
		// process the CMF admin properties to position them in the correct order
		if (mConstrained != null)
		{
			int	position = -1;
			
			ArrayList<ArrayList<ConstrainedProperty>>	positioned_properties = new ArrayList<ArrayList<ConstrainedProperty>>();
			ArrayList<ConstrainedProperty>				unpositioned_properties = new ArrayList<ConstrainedProperty>();
			
			// go over all constrained properties
			for (ConstrainedProperty property : mConstrained.getConstrainedProperties())
			{
				// create the correct property order
				// unpositioned properties will be added to the end, while
				// positioned properties will be inserted at their declared
				// location
				if (!property.hasPosition())
				{
					unpositioned_properties.add(property);
				}
				else
				{
					position = property.getPosition();
					
					ArrayList<ConstrainedProperty> properties = null;
					if (position < positioned_properties.size())
					{
						properties = positioned_properties.get(position);
					}
					else
					{
						properties = null;
						while (!(position < positioned_properties.size()))
						{
							positioned_properties.add(null);
						}
					}
					if (null == properties)
					{
						properties = new ArrayList<ConstrainedProperty>();
						positioned_properties.set(position, properties);
					}
					properties.add(property);
				}
			}
			
			// add unpositioned properties to the end
			positioned_properties.add(unpositioned_properties);
			
			// collapsed the positioned properties in a single list
			for (ArrayList<ConstrainedProperty> properties : positioned_properties)
			{
				if (null == properties)
				{
					continue;
				}
				
				for (ConstrainedProperty property : properties)
				{
					result.add(property);
				}
			}
		}
	
		return result;
	}

	public Collection<String> getRegularProperties()
	{
		ArrayList<String> excluded_properties_list = new ArrayList<String>();

		// exclude the constrained properties, these are already returned by the
		// getPositionedProperties method
		if (mConstrained != null)
		{
			for (ConstrainedProperty constrained_property : mConstrained.getConstrainedProperties())
			{
				excluded_properties_list.add(constrained_property.getPropertyName());
			}
		}

		// get the identity property which is excluded by default
		String identity = ConstrainedUtils.getIdentityProperty(mBeanClass);
		excluded_properties_list.add(identity);

		// build the excluded properties array
		String[] excluded_properties = null;
		if (excluded_properties_list.size() > 0)
		{
			excluded_properties = new String[excluded_properties_list.size()];
			excluded_properties_list.toArray(excluded_properties);
		}

		try
		{
			return BeanUtils.getPropertyNames(mBeanClass, null, excluded_properties, null);
		}
		catch (BeanUtilsException e)
		{
			throw new TemplateException("Unexpected error while obtaining the property names from class '"+mBeanClass.getName()+"'");
		}
	}

	public void transferTemplateToResult(Template t, String templateName, OutputStream result, String encoding)
	{
		String result_content = t.getContent();
		try
		{
			byte[]	result_bytes = null;
			if (null == encoding)
			{
				result_bytes = result_content.getBytes();
			}
			else
			{
				result_bytes = result_content.getBytes(encoding);
			}
			ByteArrayInputStream	result_in = new ByteArrayInputStream(result_bytes);
			BufferedInputStream		buffered_in = new BufferedInputStream(result_in);
			try
			{
				FileUtils.copy(buffered_in, result);
			}
			catch (FileUtilsErrorException e)
			{
				throw new TemplateException("Unexpected error while transferring the transformed content of '"+templateName+"'.", e);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			throw new TemplateException("Unexpected error converting the transformed content of '"+templateName+"' to bytes.", e);
		}
	}

	public void appendFormField(Template t, ConstrainedProperty property, String propertyName, List<String> mandatorySubjects)
	throws TemplateException
	{
		if (property != null)
		{
			propertyName = property.getPropertyName();
		}
		
		Class property_type = null;
		try
		{
			property_type = BeanUtils.getPropertyType(mBeanClass, propertyName);
		}
		catch (BeanUtilsException e)
		{
			throw new TemplateException("Unexpected error while obtaining the type of property '"+propertyName+"' from class '"+mBeanClass.getName()+"'");
		}
		
		// indicate mandatory fields
		if (mandatorySubjects.contains(propertyName))
		{
			t.setBlock("mandatory", "mandatory");
		}
		
		// setup the form field
		t.setValue("property", propertyName);
		if (property != null &&
			property.isInList())
		{
			t.setBlock("form_field", "form_field-list");
		}
		else if (property != null &&
				 property.isFile())
		{
			t.setBlock("form_field", "form_field-file");
		}
		else if (property_type != null &&
			(boolean.class == property_type ||
			 Boolean.class == property_type))
		{
			t.setBlock("form_field", "form_field-checkbox");
		}
		else if (property_type != null &&
			ClassUtils.isNumeric(property_type))
		{
			// detect many to one relationship on integer ids
			if (property != null &&
				(property_type == int.class ||
				property_type == Integer.class) &&
				property.hasManyToOne())
			{
				t.setBlock("form_field", "form_field-list");
			}
			else
			{
				t.setBlock("form_field", "form_field-numeric");
			}
		}
		else if (property_type != null &&
			ClassUtils.isText(property_type))
		{
			if ((property != null &&
				 property.hasLimitedLength()) ||
				 null == property)
			{
				t.setBlock("form_field", "form_field-textlimited");
			}
			else
			{
				t.setBlock("form_field", "form_field-textarea");
			}
		}
		else
		{
			t.setBlock("form_field", "form_field-generic");
		}
		
		t.appendBlock("fields", "field");
		t.removeValue("mandatory");
		
		if (t.hasValueId("first_property") &&
			!t.isValueSet("first_property"))
		{
			t.setValue("first_property", t.getEncoder().encode(propertyName));
		}
	}
	
	public String getState()
	{
		return "";
	}
	
	public String getEncoding()
	{
		return null;
	}
	
	public ResourceFinder getResourceFinder()
	{
		return mResourceFinder;
	}
	
	public void setResourceFinder(ResourceFinder resourceFinder)
	{
		mResourceFinder = resourceFinder;
	}
}

