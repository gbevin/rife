/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSubmissionModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import java.util.Iterator;

import junit.framework.TestCase;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;

public class TestSubmissionModel extends TestCase implements ParticleModelListener
{
	private Object	mPropertyAdded = null;
	private Object	mPropertyRemoved = null;
	private Object	mPropertyRenamed = null;

	public TestSubmissionModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ElementModel	elementmodel_instance = null;
		SubmissionModel	submissionmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(submissionmodel_instance != null);
		assertTrue(submissionmodel_instance instanceof SubmissionModel);
		assertSame(submissionmodel_instance.getParent(), elementmodel_instance);
		assertTrue(elementmodel_instance.containsChild(submissionmodel_instance));
	}

	public void testGetId()
	{
		ElementModel		elementmodel_instance = null;
		SubmissionModel		submissionmodel_instance = null;
		SubmissionIdModel	titlemodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		titlemodel_instance = submissionmodel_instance.getId();

		assertTrue(titlemodel_instance != null);
		assertTrue(titlemodel_instance instanceof SubmissionIdModel);
		assertEquals(titlemodel_instance.getName(), "submissionmodel1");
	}

	public void testChangeId()
	{
		ElementModel		elementmodel_instance = null;
		SubmissionModel		submissionmodel_instance = null;
		SubmissionIdModel	titlemodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		titlemodel_instance = submissionmodel_instance.getId();
		assertNull(mPropertyRenamed);
			
		try
		{
			assertTrue(submissionmodel_instance.renameProperty(titlemodel_instance, "submissionmodel2"));
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyRenamed, titlemodel_instance);
	}

	public void testChangeIdToSameName()
	{
		ElementModel		elementmodel_instance = null;
		SubmissionModel		submissionmodel_instance = null;
		SubmissionIdModel	titlemodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		titlemodel_instance = submissionmodel_instance.getId();
		assertNull(mPropertyRenamed);
			
		try
		{
			assertTrue(submissionmodel_instance.renameProperty(titlemodel_instance, "submissionmodel1") == false);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertNull(mPropertyRenamed);
	}

	public void testTryToRemoveId()
	{
		ElementModel		elementmodel_instance = null;
		SubmissionModel		submissionmodel_instance = null;
		SubmissionIdModel	titlemodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		titlemodel_instance = submissionmodel_instance.getId();
		assertNull(mPropertyRemoved);
		
		try
		{
			submissionmodel_instance.removeProperty(titlemodel_instance);
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyRemoved);
	}

	public void testIdConflicts()
	{
		ElementModel	elementmodel_instance = null;
		SubmissionModel	submissionmodel_instance1 = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance1 = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			submissionmodel_instance1.addParameter("parameter1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			new SubmissionModel(elementmodel_instance, "parameter1");
			new SubmissionModel(elementmodel_instance, "submissionmodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			new SubmissionModel(elementmodel_instance, "submissionmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
	}
	
	public void testNoInitialParameters()
	{
		ElementModel	elementmodel_instance = null;
		SubmissionModel	submissionmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(submissionmodel_instance.getParameters().size(), 0);
	}

	public void testInitialParameterCountIsZero()
	{
		ElementModel	elementmodel_instance = null;
		SubmissionModel	submissionmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(submissionmodel_instance.countParameters(), 0);
	}

	public void testAddOneParameter()
	{
		ElementModel				elementmodel_instance = null;
		SubmissionModel				submissionmodel_instance = null;
		SubmissionParameterModel	parametermodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			parametermodel_instance = submissionmodel_instance.addParameter("parametermodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, parametermodel_instance);

		Iterator<SubmissionParameterModel> parameter_it = submissionmodel_instance.getParameters().iterator();
		assertTrue(parameter_it.hasNext());
		SubmissionParameterModel parameter = parameter_it.next();
		assertEquals(parameter_it.hasNext(), false);
		assertSame(parameter, parametermodel_instance);
	}

	public void testAddTheSameParameterTwice()
	{
		ElementModel				elementmodel_instance = null;
		SubmissionModel				submissionmodel_instance = null;
		SubmissionParameterModel	parametermodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			parametermodel_instance = submissionmodel_instance.addParameter("parametermodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, parametermodel_instance);

		mPropertyAdded = null;
		try
		{
			submissionmodel_instance.addParameter("parametermodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyAdded);
		Iterator<SubmissionParameterModel> parameter_it = submissionmodel_instance.getParameters().iterator();
		assertTrue(parameter_it.hasNext());
		Object parameter = parameter_it.next();
		assertEquals(parameter_it.hasNext(), false);
		assertSame(parameter, parametermodel_instance);
	}

	public void testAddTwoParameters()
	{
		ElementModel				elementmodel_instance = null;
		SubmissionModel				submissionmodel_instance = null;
		SubmissionParameterModel	parametermodel_instance1 = null;
		SubmissionParameterModel	parametermodel_instance2 = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			parametermodel_instance1 = submissionmodel_instance.addParameter("parametermodel1");
			assertSame(mPropertyAdded, parametermodel_instance1);
			parametermodel_instance2 = submissionmodel_instance.addParameter("parametermodel2");
			assertSame(mPropertyAdded, parametermodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		Iterator<SubmissionParameterModel> parameter_it = submissionmodel_instance.getParameters().iterator();
		assertTrue(parameter_it.hasNext());
		SubmissionParameterModel parameter1 = parameter_it.next();
		assertTrue(parameter_it.hasNext());
		SubmissionParameterModel parameter2 = parameter_it.next();
		assertTrue(parameter_it.hasNext() == false);
		assertTrue((parameter1 == parametermodel_instance1 && parameter2 == parametermodel_instance2) ||
			(parameter2 == parametermodel_instance1 && parameter1 == parametermodel_instance2));
	}

	public void testCountParameters()
	{
		ElementModel				elementmodel_instance = null;
		SubmissionModel				submissionmodel_instance = null;
		SubmissionParameterModel	parametermodel_instance1 = null;
		SubmissionParameterModel	parametermodel_instance2 = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			parametermodel_instance1 = submissionmodel_instance.addParameter("parametermodel1");
			assertSame(mPropertyAdded, parametermodel_instance1);
			parametermodel_instance2 = submissionmodel_instance.addParameter("parametermodel2");
			assertSame(mPropertyAdded, parametermodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(submissionmodel_instance.countParameters(), 2);
		assertEquals(submissionmodel_instance.countProperties(SubmissionParameterModel.class), 2);
		assertEquals(submissionmodel_instance.countProperties(ParticlePropertyModel.class), 3);
	}

	public void testParameterConflicts()
	{
		ElementModel	elementmodel_instance = null;
		SubmissionModel	submissionmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");

			submissionmodel_instance.addParameter("parametermodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			submissionmodel_instance.addParameter("parametermodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}		
		try
		{
			submissionmodel_instance.addParameter("submissionmodel1");
			submissionmodel_instance.addParameter("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
	 }

	public void testRenameParameterConflict()
	{
		ElementModel				elementmodel_instance = null;
		SubmissionModel				submissionmodel_instance = null;
		SubmissionParameterModel	parametermodel_instance1 = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			submissionmodel_instance = new SubmissionModel(elementmodel_instance, "submissionmodel1");
			parametermodel_instance1 = submissionmodel_instance.addParameter("parametermodel1");
			submissionmodel_instance.addParameter("parametermodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(submissionmodel_instance.addParticleListener(this));
		assertNull(mPropertyRenamed);
		assertEquals(submissionmodel_instance.countParameters(), 2);

		try
		{
			assertTrue(submissionmodel_instance.renameProperty(parametermodel_instance1, "parametermodel2"));
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		
		assertNull(mPropertyRenamed);
		assertEquals(submissionmodel_instance.countParameters(), 2);
		assertSame(submissionmodel_instance.getParameters().iterator().next(), parametermodel_instance1);
		assertEquals(parametermodel_instance1.getName(), "parametermodel1");
	}

	public void parentChanged()
	{
	}

	public void childAdded(ParticleModel child)
	{
	}

	public void childRemoved(ParticleModel child)
	{
	}

	public void propertyAdded(ParticlePropertyModel property)
	{
		mPropertyAdded = property;
	}

	public void propertyRemoved(ParticlePropertyModel property)
	{
		mPropertyRemoved = property;
	}

	public void propertyRenamed(ParticlePropertyModel property)
	{
		mPropertyRenamed = property;
	}
}

