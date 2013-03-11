/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementOutputModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;

public class ElementOutputModel extends ElementVariableModel
{
	public ElementOutputModel(ElementModel element, String name)
	throws GuiModelException
	{
		super(element, name);
	}
}


