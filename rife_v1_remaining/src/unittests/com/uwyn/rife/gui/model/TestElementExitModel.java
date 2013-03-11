/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElementExitModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import junit.framework.TestCase;

public class TestElementExitModel extends TestCase
{
	public TestElementExitModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ElementModel		elementmodel_instance = null;
		ElementExitModel	exitmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			exitmodel_instance = new ElementExitModel(elementmodel_instance, "exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(exitmodel_instance != null);
		assertTrue(exitmodel_instance instanceof ElementExitModel);
	}
}

