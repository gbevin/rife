/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DeleteTemplateTransformer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.templates;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.crud.CrudPropertyNames;
import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.template.Template;

public class DeleteTemplateTransformer extends AdminTemplateTransformer
{
	public DeleteTemplateTransformer(CrudElement element, Object beanInstance)
	{
		super(element);
		
		mConstrained = ConstrainedUtils.makeConstrainedInstance(beanInstance);
	}
	
	public String getSupportedTemplateName()
	{
		return mElement.getPropertyString(CrudPropertyNames.TEMPLATE_NAME_DELETE, buildGroupedTemplateName("delete"));
	}

	public void transformTemplate(Template t)
	{
		// display the constrained properties in the correct order
		for (ConstrainedProperty property : getPositionedProperties())
		{
			// only show the CMF admin properties that can be edited
			if (!property.isEditable())
			{
				continue;
			}
			
			// setup the form field
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
				else
				{
					if (MimeType.APPLICATION_XHTML == mimetype)
					{
						handled = true;
						t.setBlock("form_field", "form_field-xhtml");
					}
					else if (MimeType.TEXT_PLAIN == mimetype)
					{
						handled = true;
						t.setBlock("form_field", "form_field-text");
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

			t.appendBlock("fields", "field");

			if (t.hasBlock("form_field-legend"))
			{
				t.removeValue("form_field-legend");
			}
		}

		// display the regular properties
		for (String property_name : getRegularProperties())
		{
			// setup the form field
			t.setValue("property", property_name);
			t.setBlock("form_field", "form_field-generic");
			t.appendBlock("fields", "field");
		}
	}
}
