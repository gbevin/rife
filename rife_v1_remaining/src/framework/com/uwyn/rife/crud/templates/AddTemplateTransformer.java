/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AddTemplateTransformer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.templates;

import com.uwyn.rife.crud.CrudPropertyNames;
import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.template.Template;

import java.util.List;

public class AddTemplateTransformer extends AdminTemplateTransformer
{
	public AddTemplateTransformer(CrudElement element)
	{
		super(element);
	}
	
	public String getSupportedTemplateName()
	{
		return mElement.getPropertyString(CrudPropertyNames.TEMPLATE_NAME_ADD, buildGroupedTemplateName("add"));
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
			
			appendFormField(t, property, null, mandatory_subjects);
		}

		// handle the regular properties
		for (String property_name : getRegularProperties())
		{
			appendFormField(t, null, property_name, mandatory_subjects);
		}
	}
}

