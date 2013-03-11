/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestParticlePropertyModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import junit.framework.TestCase;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;

public class TestParticlePropertyModel extends ParticlePropertyModel
{
	public TestParticlePropertyModel(ParticleModel particleModel, String name)
	throws GuiModelException
	{
		super(particleModel, name);
	}

	public static class Test extends TestCase implements ParticleModelListener
	{
		private Object	mPropertyAdded = null;

		public Test(String name)
		{
			super(name);
		}

		public void testInstantiation()
		{
			ParticleModel			particlemodel_instance = null;
			ParticlePropertyModel	propertymodel_instance = null;
			
			particlemodel_instance = new TestParticleModel();
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertNull(mPropertyAdded);
			
			try
			{
				propertymodel_instance = new TestParticlePropertyModel(particlemodel_instance, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mPropertyAdded, propertymodel_instance);
			assertNotNull(propertymodel_instance);
			assertTrue(propertymodel_instance instanceof ParticlePropertyModel);
		}

		public void testDifferentButEqualPropertyObjectsCreation()
		{
			ParticleModel			particlemodel_instance = null;
			ParticlePropertyModel	propertymodel_instance1 = null;
			
			particlemodel_instance = new TestParticleModel();
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertNull(mPropertyAdded);
			
			try
			{
				propertymodel_instance1 = new TestParticlePropertyModel(particlemodel_instance, "particleproperty1");
				assertSame(mPropertyAdded, propertymodel_instance1);
				mPropertyAdded = null;
				new TestParticlePropertyModel(particlemodel_instance, "particleproperty1");
				fail();
			}
			catch (GuiModelException e)
			{
				assertTrue(true);
			}
			assertNull(mPropertyAdded);
		}

		public void testValidName()
		{
			ParticleModel			particlemodel_instance = null;
			ParticlePropertyModel	propertymodel_instance1 = null;
			ParticlePropertyModel	propertymodel_instance2 = null;
			
			particlemodel_instance = new TestParticleModel();
			
			try
			{
				propertymodel_instance1 = new TestParticlePropertyModel(particlemodel_instance, "particleproperty1");
				propertymodel_instance2 = new TestParticlePropertyModel(particlemodel_instance, "particleproperty2");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertEquals(propertymodel_instance1.isValidName("particleproperty1"), true);
			assertEquals(propertymodel_instance1.isValidName("particleproperty2"), false);
			assertEquals(propertymodel_instance1.isValidName("particleproperty3"), true);
			assertEquals(propertymodel_instance2.isValidName("particleproperty1"), false);
			assertEquals(propertymodel_instance2.isValidName("particleproperty2"), true);
			assertEquals(propertymodel_instance2.isValidName("particleproperty3"), true);
		}

		public void testNoInitialDescription()
		{
			ParticleModel				particlemodel_instance = null;
			TestParticlePropertyModel	propertymodel_instance = null;
			
			particlemodel_instance = new TestParticleModel();
			
			try
			{
				propertymodel_instance = new TestParticlePropertyModel(particlemodel_instance, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}

			assertNull(propertymodel_instance.getDescription());
		}

		public void testSetDescription()
		{
			ParticleModel				particlemodel_instance = null;
			TestParticlePropertyModel	propertymodel_instance = null;
			
			particlemodel_instance = new TestParticleModel();
			
			try
			{
				propertymodel_instance = new TestParticlePropertyModel(particlemodel_instance, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			
			propertymodel_instance.setDescription("the description");

			assertEquals(propertymodel_instance.getDescription(), "the description");
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
		}

		public void propertyRenamed(ParticlePropertyModel property)
		{
		}
	}

	private static class TestParticleModel extends ParticleModel
	{
	}
}

