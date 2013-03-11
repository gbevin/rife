/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElementModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import java.util.Iterator;

import junit.framework.TestCase;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;

public class TestElementModel extends TestCase implements ParticleModelListener
{
	private Object	mChildAdded = null;
	private Object	mPropertyAdded = null;
	private Object	mPropertyRemoved = null;
	private Object	mPropertyRenamed = null;

	public TestElementModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(elementmodel_instance != null);
		assertTrue(elementmodel_instance instanceof ElementModel);
	}

	public void testGetId()
	{
		ElementModel	elementmodel_instance = null;
		ElementIdModel	titlemodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		titlemodel_instance = elementmodel_instance.getId();

		assertTrue(titlemodel_instance != null);
		assertTrue(titlemodel_instance instanceof ElementIdModel);
		assertEquals(titlemodel_instance.getName(), "elementmodel1");
	}

	public void testChangeId()
	{
		ElementModel	elementmodel_instance = null;
		ElementIdModel	titlemodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		titlemodel_instance = elementmodel_instance.getId();
		assertNull(mPropertyRenamed);
			
		try
		{
			assertTrue(elementmodel_instance.renameProperty(titlemodel_instance, "elementmodel2"));
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyRenamed, titlemodel_instance);
	}

	public void testChangeIdToSameName()
	{
		ElementModel	elementmodel_instance = null;
		ElementIdModel	titlemodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		titlemodel_instance = elementmodel_instance.getId();
		assertNull(mPropertyRenamed);
			
		try
		{
			assertTrue(elementmodel_instance.renameProperty(titlemodel_instance, "elementmodel1") == false);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertNull(mPropertyRenamed);
	}

	public void testTryToRemoveId()
	{
		ElementModel	elementmodel_instance = null;
		ElementIdModel	titlemodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		titlemodel_instance = elementmodel_instance.getId();
		assertNull(mPropertyRemoved);
		
		try
		{
			elementmodel_instance.removeProperty(titlemodel_instance);
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
		ElementModel	elementmodel_instance1 = null;
		
		try
		{
			elementmodel_instance1 = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			elementmodel_instance1.setImplementation("classname1");
			elementmodel_instance1.addInput("inputmodel1");
			elementmodel_instance1.addOutput("outputmodel1");
			elementmodel_instance1.addExit("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			new ElementModel("classname1");
			new ElementModel("inputmodel1");
			new ElementModel("outputmodel1");
			new ElementModel("exitmodel1");
			new ElementModel("elementmodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			// no error since they are not part of the same group yet
			assertTrue(e.getMessage(), false);
		}
	}
	
	public void testNoInitialClassname()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertNull(elementmodel_instance.getImplementation());
	}

	public void testSetClassname()
	{
		ElementModel			elementmodel_instance = null;
		ElementImplementationModel	classnamemodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);

		try
		{
			classnamemodel_instance = elementmodel_instance.setImplementation("classnamemodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, classnamemodel_instance);

		assertSame(elementmodel_instance.getImplementation(), classnamemodel_instance);
		assertEquals(elementmodel_instance.countProperties(ElementImplementationModel.class), 1);
	}	

	public void testSetClassnameTwice()
	{
		ElementModel				elementmodel_instance = null;
		ElementImplementationModel	classnamemodel_instance2 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);

		try
		{
			classnamemodel_instance2 = elementmodel_instance.setImplementation("classnamemodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, classnamemodel_instance2);

		assertSame(elementmodel_instance.getImplementation(), classnamemodel_instance2);
		assertEquals(elementmodel_instance.countProperties(ElementImplementationModel.class), 1);
	}	

	public void testClassnameConflicts()
	{
		ElementModel	elementmodel_instance1 = null; 
		ElementModel	elementmodel_instance2 = null;
		
		try
		{
			elementmodel_instance1 = new ElementModel("elementmodel1");
			elementmodel_instance2 = new ElementModel("elementmodel2");
		
			elementmodel_instance1.setImplementation("classname1");
			elementmodel_instance1.addInput("inputmodel1");
			elementmodel_instance1.addOutput("outputmodel1");
			elementmodel_instance1.addExit("exitmodel1");
			elementmodel_instance2.setImplementation("classname2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			elementmodel_instance1.setImplementation("elementmodel1");
			elementmodel_instance1.setImplementation("classname1");
			elementmodel_instance1.setImplementation("inputmodel1");
			elementmodel_instance1.setImplementation("outputmodel1");
			elementmodel_instance1.setImplementation("exitmodel1");
			elementmodel_instance1.setImplementation("elementmodel2");
			elementmodel_instance1.setImplementation("classname2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
	}

	public void testNoInitialInputs()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.getInputs().size(), 0);
	}

	public void testInitialInputCountIsZero()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.countInputs(), 0);
	}

	public void testAddOneInput()
	{
		ElementModel		elementmodel_instance = null;
		ElementInputModel	inputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			inputmodel_instance = elementmodel_instance.addInput("inputmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, inputmodel_instance);

		Iterator<ElementInputModel> input_it = elementmodel_instance.getInputs().iterator();
		assertTrue(input_it.hasNext());
		ElementInputModel input = input_it.next();
		assertEquals(input_it.hasNext(), false);
		assertSame(input, inputmodel_instance);
	}

	public void testAddTheSameInputTwice()
	{
		ElementModel		elementmodel_instance = null;
		ElementInputModel	inputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			inputmodel_instance = elementmodel_instance.addInput("inputmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, inputmodel_instance);

		mPropertyAdded = null;
		try
		{
			elementmodel_instance.addInput("inputmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyAdded);
		Iterator<ElementInputModel> input_it = elementmodel_instance.getInputs().iterator();
		assertTrue(input_it.hasNext());
		ElementInputModel input = input_it.next();
		assertEquals(input_it.hasNext(), false);
		assertSame(input, inputmodel_instance);
	}

	public void testAddTwoInputs()
	{
		ElementModel		elementmodel_instance = null;
		ElementInputModel	inputmodel_instance1 = null;
		ElementInputModel	inputmodel_instance2 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			inputmodel_instance1 = elementmodel_instance.addInput("inputmodel1");
			assertSame(mPropertyAdded, inputmodel_instance1);
			inputmodel_instance2 = elementmodel_instance.addInput("inputmodel2");
			assertSame(mPropertyAdded, inputmodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		Iterator<ElementInputModel> input_it = elementmodel_instance.getInputs().iterator();
		assertTrue(input_it.hasNext());
		ElementInputModel input1 = input_it.next();
		assertTrue(input_it.hasNext());
		ElementInputModel input2 = input_it.next();
		assertEquals(input_it.hasNext(), false);
		assertTrue((input1 == inputmodel_instance1 && input2 == inputmodel_instance2) ||
			(input2 == inputmodel_instance1 && input1 == inputmodel_instance2));
	}

	public void testCountInputs()
	{
		ElementModel		elementmodel_instance = null;
		ElementInputModel	inputmodel_instance1 = null;
		ElementInputModel	inputmodel_instance2 = null;
		ElementExitModel	exitmodel_instance = null;
		ElementOutputModel	outputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		assertNull(mPropertyAdded);
		try
		{
			inputmodel_instance1 = elementmodel_instance.addInput("inputmodel1");
			assertSame(mPropertyAdded, inputmodel_instance1);
			inputmodel_instance2 = elementmodel_instance.addInput("inputmodel2");
			assertSame(mPropertyAdded, inputmodel_instance2);
			exitmodel_instance = elementmodel_instance.addExit("exitmodel");
			assertSame(mPropertyAdded, exitmodel_instance);
			outputmodel_instance = elementmodel_instance.addOutput("outputmodel");
			assertSame(mPropertyAdded, outputmodel_instance);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.countInputs(), 2);
		assertEquals(elementmodel_instance.countProperties(ElementInputModel.class), 2);
		assertEquals(elementmodel_instance.countProperties(ElementVariableModel.class), 3);
		assertEquals(elementmodel_instance.countProperties(ElementPropertyModel.class), 5);
	}

	public void testInputConflicts()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			
			elementmodel_instance.setImplementation("classname");
			elementmodel_instance.addInput("inputmodel1");
			elementmodel_instance.addOutput("outputmodel1");
			elementmodel_instance.addExit("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			elementmodel_instance.addInput("inputmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}		
		try
		{
			elementmodel_instance.addInput("outputmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}		
		try
		{
			elementmodel_instance.addInput("elementmodel1");
			elementmodel_instance.addInput("classname");
			elementmodel_instance.addInput("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
	}
	
	public void testRenameInputConflict()
	{
		ElementModel		elementmodel_instance = null;
		ElementInputModel	inputmodel_instance1 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			inputmodel_instance1 = elementmodel_instance.addInput("inputmodel1");
			elementmodel_instance.addInput("inputmodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyRenamed);
		
		try
		{
			assertTrue(elementmodel_instance.renameProperty(inputmodel_instance1, "inputmodel2"));
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyRenamed);
	}

	public void testNoInitialOutputs()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.getOutputs().size(), 0);
	}

	public void testInitialOutputCountIsZero()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.countOutputs(), 0);
	}

	public void testAddOneOutput()
	{
		ElementModel		elementmodel_instance = null;
		ElementOutputModel	outputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			outputmodel_instance = elementmodel_instance.addOutput("outputmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, outputmodel_instance);

		Iterator<ElementOutputModel> output_it = elementmodel_instance.getOutputs().iterator();
		assertTrue(output_it.hasNext());
		ElementOutputModel output = output_it.next();
		assertEquals(output_it.hasNext(), false);
		assertSame(output, outputmodel_instance);
	}

	public void testAddTheSameOutputTwice()
	{
		ElementModel		elementmodel_instance = null;
		ElementOutputModel	outputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			outputmodel_instance = elementmodel_instance.addOutput("outputmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, outputmodel_instance);

		mPropertyAdded = null;
		try
		{
			elementmodel_instance.addOutput("outputmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyAdded);
		Iterator<ElementOutputModel> output_it = elementmodel_instance.getOutputs().iterator();
		assertTrue(output_it.hasNext());
		ElementOutputModel output = output_it.next();
		assertEquals(output_it.hasNext(), false);
		assertSame(output, outputmodel_instance);
	}

	public void testAddTwoOutputs()
	{
		ElementModel		elementmodel_instance = null;
		ElementOutputModel	outputmodel_instance1 = null;
		ElementOutputModel	outputmodel_instance2 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));

		try
		{
			outputmodel_instance1 = elementmodel_instance.addOutput("outputmodel1");
			assertSame(mPropertyAdded, outputmodel_instance1);
			outputmodel_instance2 = elementmodel_instance.addOutput("outputmodel2");
			assertSame(mPropertyAdded, outputmodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		Iterator<ElementOutputModel> output_it = elementmodel_instance.getOutputs().iterator();
		assertTrue(output_it.hasNext());
		ElementOutputModel output1 = output_it.next();
		assertTrue(output_it.hasNext());
		ElementOutputModel output2 = output_it.next();
		assertEquals(output_it.hasNext(), false);
		assertTrue((output1 == outputmodel_instance1 && output2 == outputmodel_instance2) ||
			(output2 == outputmodel_instance1 && output1 == outputmodel_instance2));
	}

	public void testCountOutputs()
	{
		ElementModel		elementmodel_instance = null;
		ElementOutputModel	outputmodel_instance1 = null;
		ElementOutputModel	outputmodel_instance2 = null;
		ElementExitModel	exitmodel_instance = null;
		ElementInputModel	inputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		
		assertNull(mPropertyAdded);
		try
		{
			exitmodel_instance = elementmodel_instance.addExit("exitmodel1");
			assertSame(mPropertyAdded, exitmodel_instance);
			outputmodel_instance1 = elementmodel_instance.addOutput("outputmodel1");
			assertSame(mPropertyAdded, outputmodel_instance1);
			outputmodel_instance2 = elementmodel_instance.addOutput("outputmodel2");
			assertSame(mPropertyAdded, outputmodel_instance2);
			inputmodel_instance = elementmodel_instance.addInput("inputmodel");
			assertSame(mPropertyAdded, inputmodel_instance);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.countOutputs(), 2);
		assertEquals(elementmodel_instance.countProperties(ElementOutputModel.class), 2);
		assertEquals(elementmodel_instance.countProperties(ElementVariableModel.class), 3);
		assertEquals(elementmodel_instance.countProperties(ElementPropertyModel.class), 5);
	}

	public void testOutputConflicts()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			
			elementmodel_instance.setImplementation("classname");
			elementmodel_instance.addInput("inputmodel1");
			elementmodel_instance.addOutput("outputmodel1");
			elementmodel_instance.addExit("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			elementmodel_instance.addOutput("outputmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}		
		try
		{
			elementmodel_instance.addOutput("inputmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}		
		try
		{
			elementmodel_instance.addOutput("elementmodel1");
			elementmodel_instance.addOutput("classname");
			elementmodel_instance.addOutput("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
	}

	public void testRenameOutputConflict()
	{
		ElementModel		elementmodel_instance = null;
		ElementOutputModel	outputmodel_instance1 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			outputmodel_instance1 = elementmodel_instance.addOutput("outputmodel1");
			elementmodel_instance.addOutput("outputmodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyRenamed);
		
		try
		{
			assertTrue(elementmodel_instance.renameProperty(outputmodel_instance1, "outputmodel2"));
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyRenamed);
	}

	public void testNoInitialExits()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.getExits().size(), 0);
	}

	public void testInitialExitCountIsZero()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.countExits(), 0);
	}

	public void testAddOneExit()
	{
		ElementModel		elementmodel_instance = null;
		ElementExitModel	exitmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			exitmodel_instance = elementmodel_instance.addExit("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, exitmodel_instance);

		Iterator<ElementExitModel> exit_it = elementmodel_instance.getExits().iterator();
		assertTrue(exit_it.hasNext());
		ElementExitModel exit = exit_it.next();
		assertEquals(exit_it.hasNext(), false);
		assertSame(exit, exitmodel_instance);
	}

	public void testAddTheSameExitTwice()
	{
		ElementModel		elementmodel_instance = null;
		ElementExitModel	exitmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			exitmodel_instance = elementmodel_instance.addExit("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mPropertyAdded, exitmodel_instance);

		mPropertyAdded = null;
		try
		{
			elementmodel_instance.addExit("exitmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyAdded);
		Iterator<ElementExitModel> exit_it = elementmodel_instance.getExits().iterator();
		assertTrue(exit_it.hasNext());
		Object exit = exit_it.next();
		assertEquals(exit_it.hasNext(), false);
		assertSame(exit, exitmodel_instance);
	}

	public void testAddTwoExits()
	{
		ElementModel		elementmodel_instance = null;
		ElementExitModel	exitmodel_instance1 = null;
		ElementExitModel	exitmodel_instance2 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			exitmodel_instance1 = elementmodel_instance.addExit("exitmodel1");
			assertSame(mPropertyAdded, exitmodel_instance1);
			exitmodel_instance2 = elementmodel_instance.addExit("exitmodel2");
			assertSame(mPropertyAdded, exitmodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		Iterator<ElementExitModel> exit_it = elementmodel_instance.getExits().iterator();
		assertTrue(exit_it.hasNext());
		ElementExitModel exit1 = exit_it.next();
		assertTrue(exit_it.hasNext());
		ElementExitModel exit2 = exit_it.next();
		assertTrue(exit_it.hasNext() == false);
		assertTrue((exit1 == exitmodel_instance1 && exit2 == exitmodel_instance2) ||
			(exit2 == exitmodel_instance1 && exit1 == exitmodel_instance2));
	}

	public void testCountExits()
	{
		ElementModel		elementmodel_instance = null;
		ElementExitModel	exitmodel_instance1 = null;
		ElementExitModel	exitmodel_instance2 = null;
		ElementOutputModel	outputmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyAdded);
		
		try
		{
			outputmodel_instance = elementmodel_instance.addOutput("outputmodel1");
			assertSame(mPropertyAdded, outputmodel_instance);
			exitmodel_instance1 = elementmodel_instance.addExit("exitmodel1");
			assertSame(mPropertyAdded, exitmodel_instance1);
			exitmodel_instance2 = elementmodel_instance.addExit("exitmodel2");
			assertSame(mPropertyAdded, exitmodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.countExits(), 2);
		assertEquals(elementmodel_instance.countProperties(ElementExitModel.class), 2);
		assertEquals(elementmodel_instance.countProperties(ElementVariableModel.class), 1);
		assertEquals(elementmodel_instance.countProperties(ElementPropertyModel.class), 4);
	}

	public void testExitConflicts()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");

			elementmodel_instance.setImplementation("classname");
			elementmodel_instance.addInput("inputmodel1");
			elementmodel_instance.addOutput("outputmodel1");
			elementmodel_instance.addExit("exitmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		
		try
		{
			elementmodel_instance.addExit("exitmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}		
		try
		{
			elementmodel_instance.addExit("elementmodel1");
			elementmodel_instance.addExit("classname");
			elementmodel_instance.addExit("inputmodel1");
			elementmodel_instance.addExit("outputmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
	}

	public void testRenameExitConflict()
	{
		ElementModel		elementmodel_instance = null;
		ElementExitModel	exitmodel_instance1 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			exitmodel_instance1 = elementmodel_instance.addExit("exitmodel1");
			elementmodel_instance.addExit("exitmodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mPropertyRenamed);
		
		try
		{
			assertTrue(elementmodel_instance.renameProperty(exitmodel_instance1, "exitmodel2"));
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
		}
		assertNull(mPropertyRenamed);
	}

	public void testInitialSubmissionCountIsZero()
	{
		ElementModel	elementmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertEquals(elementmodel_instance.countSubmissions(), 0);
	}

	public void testAddOneSubmission()
	{
		ElementModel	elementmodel_instance = null;
		SubmissionModel submissionmodel_instance = null;
		
		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mChildAdded);

		try
		{
			submissionmodel_instance = elementmodel_instance.addSubmission("submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mChildAdded, submissionmodel_instance);

		Iterator<SubmissionModel> submission_it = elementmodel_instance.getSubmissions().iterator();
		assertTrue(submission_it.hasNext());
		Object submission = submission_it.next();
		assertEquals(submission_it.hasNext(), false);
		assertSame(submission, submissionmodel_instance);
	}

	public void testAddAnotherEqualSubmission()
	{
		ElementModel elementmodel_instance = null;
		SubmissionModel submissionmodel_instance1 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mChildAdded);

		try
		{
			submissionmodel_instance1 = elementmodel_instance.addSubmission("submissionmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mChildAdded, submissionmodel_instance1);

		try
		{
			elementmodel_instance.addSubmission("submissionmodel1");
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
			assertSame(mChildAdded, submissionmodel_instance1);
		}
		
		Iterator<SubmissionModel> submission_it = elementmodel_instance.getSubmissions().iterator();
		assertTrue(submission_it.hasNext());
		Object submission = submission_it.next();
		assertEquals(submission_it.hasNext(), false);
		assertSame(submission, submissionmodel_instance1);
	}

	public void testAddTwoSubmissions()
	{
		ElementModel elementmodel_instance = null;
		SubmissionModel submissionmodel_instance1 = null;
		SubmissionModel submissionmodel_instance2 = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertTrue(elementmodel_instance.addParticleListener(this));
		assertNull(mChildAdded);

		try
		{
			submissionmodel_instance1 = elementmodel_instance.addSubmission("submissionmodel1");
			assertSame(mChildAdded, submissionmodel_instance1);
			submissionmodel_instance2 = elementmodel_instance.addSubmission("submissionmodel2");
			assertSame(mChildAdded, submissionmodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		Iterator<SubmissionModel> submission_it = elementmodel_instance.getSubmissions().iterator();
		assertTrue(submission_it.hasNext());
		Object submission1 = submission_it.next();
		assertTrue(submission_it.hasNext());
		Object submission2 = submission_it.next();
		assertEquals(submission_it.hasNext(), false);
		assertTrue((submission1 == submissionmodel_instance1 && submission2 == submissionmodel_instance2) ||
			(submission2 == submissionmodel_instance1 && submission1 == submissionmodel_instance2));
	}

	public void testCountSubmissions()
	{
		ElementModel elementmodel_instance = null;

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}

		try
		{
			elementmodel_instance.addSubmission("submissionmodel1");
			elementmodel_instance.addSubmission("submissionmodel2");
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertEquals(elementmodel_instance.countSubmissions(), 2);
	}

	public void parentChanged()
	{
	}

	public void childAdded(ParticleModel child)
	{
		mChildAdded = child;
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

