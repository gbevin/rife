/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElementImplementationModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import junit.framework.TestCase;

public class TestElementImplementationModel extends TestCase
{
	public TestElementImplementationModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ElementModel				elementmodel_instance = null; 
		ElementImplementationModel	implementationmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			implementationmodel_instance = new ElementImplementationModel(elementmodel_instance, "implementationmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(implementationmodel_instance != null);
		assertTrue(implementationmodel_instance instanceof ElementImplementationModel);
	}
}

