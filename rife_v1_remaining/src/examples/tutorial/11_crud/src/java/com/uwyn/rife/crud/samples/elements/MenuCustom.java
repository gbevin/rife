/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MenuCustom.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.elements;

import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.crud.elements.admin.Menu;
import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.crud.templates.MenuTemplateTransformer;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.exceptions.EngineException;

import java.util.LinkedHashMap;

public class MenuCustom extends Menu
{
	public AdminTemplateTransformer getTransformer()
	{
		return new CustomMenuTemplateTransformer(this);
	}
}

class CustomMenuTemplateTransformer extends MenuTemplateTransformer
{
	public CustomMenuTemplateTransformer(CrudElement element)
	{
		super(element);
	}

	protected LinkedHashMap<ElementInfo, String> getTopMenuItems()
	throws EngineException
	{
		LinkedHashMap<ElementInfo, String> result = new LinkedHashMap<ElementInfo, String>();
		result.put(mElement.getSite().resolveId(".Html.Home"), "home");
		result.putAll(super.getTopMenuItems());
		return result;
	}
}

