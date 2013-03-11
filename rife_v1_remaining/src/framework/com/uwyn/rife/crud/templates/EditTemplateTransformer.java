/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EditTemplateTransformer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.templates;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.crud.CrudPropertyNames;
import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.template.Template;

import java.util.List;

public class EditTemplateTransformer extends AdminTemplateTransformer
{
	public EditTemplateTransformer(CrudElement element)
	{
		super(element);
	}
	
	public String getSupportedTemplateName()
	{
		return mElement.getPropertyString(CrudPropertyNames.TEMPLATE_NAME_EDIT, buildGroupedTemplateName("edit"));
	}

	public void transformTemplate(Template t)
	{
		// retrieve the mandatory subjects
		List<String> mandatory_subjects = getMandatorySubjects();
		
		// display the constrained properties in the correct order
		for (ConstrainedProperty property : getPositionedProperties())
		{
			// only show the CMF admin properties that can be edited
			if (!property.isEditable())
			{
				continue;
			}
			
			t.setValue("property", property.getPropertyName());

			boolean		handled = false;
			MimeType	mimetype = property.getMimeType();
			if (mimetype != null)
			{
				if (!property.isAutoRetrieved())
				{
					if (0 == mimetype.toString().indexOf("image/"))
					{
						handled = true;
						if (t.hasBlock("existing_field-legend"))
						{
							t.setBlock("existing_field-legend", "existing_field-legend");
						}
						t.setBlock("existing_field", "existing_field-image");
					}
					else if (MimeType.RAW == mimetype)
					{
						handled = true;
						if (t.hasBlock("existing_field-legend"))
						{
							t.setBlock("existing_field-legend", "existing_field-legend");
						}
						t.setBlock("existing_field", "existing_field-raw");
					}
				}
				else
				{
					if (mimetype == MimeType.APPLICATION_XHTML)
					{
						handled = true;
						t.setBlock("existing_field", "existing_field-xhtml");
					}
					else if (mimetype == MimeType.TEXT_PLAIN)
					{
						handled = true;
						t.setBlock("existing_field", "existing_field-text");
					}
				}
			}
			
			if (!handled)
			{
				if (property.isUrl())
				{
					t.setBlock("existing_field", "existing_field-url");
				}
				else if (property.isEmail())
				{
					t.setBlock("existing_field", "existing_field-email");
				}
				else
				{
					t.setBlock("existing_field", "existing_field-generic");
				}
			}
			
			appendFormField(t, property, null, mandatory_subjects);

			if (t.hasBlock("existing_field-legend"))
			{
				t.removeValue("existing_field-legend");
			}
		}

		// handle the regular properties
		for (String property_name : getRegularProperties())
		{
			appendFormField(t, null, property_name, mandatory_subjects);
		}
	}
}

