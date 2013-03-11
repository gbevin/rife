/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BrowseTemplateTransformer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.templates;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.crud.CrudPropertyNames;
import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.ClassUtils;

import java.util.Map;

public class BrowseTemplateTransformer extends AdminTemplateTransformer
{
	public BrowseTemplateTransformer(CrudElement element)
	{
		super(element);
	}
	
	public String getSupportedTemplateName()
	{
		return mElement.getPropertyString(CrudPropertyNames.TEMPLATE_NAME_BROWSE, buildGroupedTemplateName("browse"));
	}

	public void transformTemplate(Template t)
	{
		// display the constrained properties in the correct order
		for (ConstrainedProperty property : getPositionedProperties())
		{
			// only show the properties that should be listed
			if (!property.isListed())
			{
				continue;
			}
			
			addColumn(t, property.getPropertyName(), property);
		}

		// display the regular properties
		for (String property_name : getRegularProperties())
		{
			addColumn(t, property_name, null);
		}
		
		// add association columns
		final Map<Class, String> associations_columns = (Map<Class, String>)mElement.getProperty(mCrudPrefix+"-associations_columns");
		if (associations_columns != null)
		{
			int count = 1;
			for (Class klass : associations_columns.keySet())
			{
				t.setValue("association_count", count++);
				t.setValue("association_classname", t.getEncoder().encode(klass.getName()));
				t.setValue("association_short-classname", t.getEncoder().encode(ClassUtils.simpleClassName(klass)));
				if (t.hasValueId("colgroups_associations"))
				{
					t.appendBlock("colgroups_associations", "colgroup_association");
				}
				if (t.hasValueId("associationheaders"))
				{
					t.appendBlock("associationheaders", "associationheader");
				}
				t.appendBlock("associations", "association");
			}
		}
	}

	private void addColumn(Template t, String propertyName, ConstrainedProperty property)
	throws TemplateException
	{
		t.setValue("property", propertyName);
		if (property != null)
		{
			boolean		handled = false;
			MimeType	mimetype = property.getMimeType();
			if (mimetype != null)
			{
				if (!property.isAutoRetrieved())
				{
					if (0 == mimetype.toString().indexOf("image/"))
					{
						handled = true;
						if (t.hasBlock("form_field-legend"))
						{
							t.setBlock("form_field-legend", "form_field-legend");
						}
						t.setBlock("form_field", "form_field-image");
					}
					else if (MimeType.RAW == mimetype)
					{
						handled = true;
						if (t.hasBlock("form_field-legend"))
						{
							t.setBlock("form_field-legend", "form_field-legend");
						}
						t.setBlock("form_field", "form_field-raw");
					}
				}
			}
			
			if (!handled)
			{
				if (property.isUrl())
				{
					t.setBlock("form_field", "form_field-url");
				}
				else if (property.isEmail())
				{
					t.setBlock("form_field", "form_field-email");
				}
				else
				{
					t.setBlock("form_field", "form_field-generic");
				}
			}
		}
		else
		{
			t.setBlock("form_field", "form_field-generic");
		}
		
		if (mElement.hasProperty(mCrudPrefix+"-ordinal_property_name"))
		{
			t.setBlock("move_actions", "move_actions");
		}

		if (t.hasBlock("colgroup"))
		{
			t.appendBlock("colgroups", "colgroup");
		}
		if (t.hasBlock("columnheader"))
		{
			t.appendBlock("columnheaders", "columnheader");
		}
		t.appendBlock("columns", "column");

		if (t.hasBlock("form_field-legend"))
		{
			t.removeValue("form_field-legend");
		}
	}
}

