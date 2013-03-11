/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSubmissionParameterModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import junit.framework.TestCase;

public class TestSubmissionParameterModel extends TestCase
{
	public TestSubmissionParameterModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ElementModel	elementmodel_instance = null;
		SubmissionModel				submissionmodel_instance = null;
		SubmissionParameterModel	parametermodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
			parametermodel_instance = new SubmissionParameterModel(submissionmodel_instance, "parametermodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(parametermodel_instance != null);
		assertTrue(parametermodel_instance instanceof SubmissionParameterModel);
	}
}

