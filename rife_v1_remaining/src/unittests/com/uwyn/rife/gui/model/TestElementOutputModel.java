/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElementOutputModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import junit.framework.TestCase;

public class TestElementOutputModel extends TestCase
{
	public TestElementOutputModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ElementModel		elementmodel_instance = null;
		ElementOutputModel	outputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			outputmodel_instance = new ElementOutputModel(elementmodel_instance, "outputmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(outputmodel_instance != null);
		assertTrue(outputmodel_instance instanceof ElementOutputModel);
	}
}

