/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElementInputModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import junit.framework.TestCase;

public class TestElementInputModel extends TestCase
{
	public TestElementInputModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ElementModel		elementmodel_instance = null;
		ElementInputModel	inputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			inputmodel_instance = new ElementInputModel(elementmodel_instance, "inputmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(inputmodel_instance != null);
		assertTrue(inputmodel_instance instanceof ElementInputModel);
	}
}

