/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestParticleModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import java.util.Iterator;
import junit.framework.TestCase;

public class TestParticleModel extends ParticleModel
{
	public static class Test extends TestCase implements ParticleModelListener
	{
		private boolean	mParentChanged = false;
		private Object	mChildAdded = null;
		private Object	mChildRemoved = null;
		private Object	mPropertyAdded = null;
		private Object	mPropertyRemoved = null;
		private Object	mPropertyRenamed = null;

		public Test(String name)
		{
			super(name);
		}

		public void testInstantiation()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();

			assertTrue(particlemodel_instance != null);
			assertTrue(particlemodel_instance instanceof ParticleModel);
		}

		public void testAddParticleListener()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();
			
			assertTrue(particlemodel_instance.addParticleListener(this));
		}

		public void testRemoveParticleListener()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();
			
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertTrue(particlemodel_instance.removeParticleListener(this));
		}

		public void testNoInitialDescription()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();

			assertNull(particlemodel_instance.getDescription());
		}

		public void testSetDescription()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			
			particlemodel_instance.setDescription("the description");

			assertEquals(particlemodel_instance.getDescription(), "the description");
		}
		
		public void testGetEmptyInitialParent()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();

			assertNull(particlemodel_instance.getParent());
		}

		public void testSetParent()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel_instance2 = new TestParticleModel();

			assertEquals(mParentChanged, false);
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertTrue(particlemodel_instance.setParent(particlemodel_instance2));
			assertEquals(mParentChanged, true);
			assertSame(particlemodel_instance.getParent(), particlemodel_instance2);
		}

		public void testSetSameParentTwice()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel_instance2 = new TestParticleModel();

			assertEquals(mParentChanged, false);
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertTrue(particlemodel_instance.setParent(particlemodel_instance2));
			assertEquals(mParentChanged, true);
			mParentChanged = false;
			assertEquals(particlemodel_instance.setParent(particlemodel_instance2), false);
			assertEquals(mParentChanged, false);
			assertSame(particlemodel_instance.getParent(), particlemodel_instance2);
		}

		public void testNoInitialChildren()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();

			assertEquals(particlemodel_instance.getChildren().size(), 0);
		}

		public void testInitialChildCountIsZero()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();

			assertEquals(particlemodel_instance.countChildren(), 0);
		}

		public void testAddOneChild()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel_instance2 = new TestParticleModel();

			assertNull(mChildAdded);
			assertTrue(particlemodel_instance.addParticleListener(this));
			try
			{
				particlemodel_instance.addChild(particlemodel_instance2);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mChildAdded, particlemodel_instance2);
			assertSame(particlemodel_instance2.getParent(), particlemodel_instance);
			Iterator<ParticleModel> child_it = particlemodel_instance.getChildren().iterator();
			Object child = null;
			assertTrue(child_it.hasNext());
			child = child_it.next();
			assertEquals(child_it.hasNext(), false);
			assertSame(child, particlemodel_instance2);
			assertTrue(particlemodel_instance.containsChild(particlemodel_instance2));
		}

		public void testAddTheSameChildTwice()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel_instance2 = new TestParticleModel();

			assertNull(mChildAdded);
			assertTrue(particlemodel_instance.addParticleListener(this));
			try
			{
				particlemodel_instance.addChild(particlemodel_instance2);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mChildAdded, particlemodel_instance2);
			mChildAdded = null;
			try
			{
				particlemodel_instance.addChild(particlemodel_instance2);
				fail();
			}
			catch (GuiModelException e)
			{
				assertTrue(true);
			}
			assertNull(mChildAdded);
			Iterator<ParticleModel> child_it = particlemodel_instance.getChildren().iterator();
			Object child = null;
			assertTrue(child_it.hasNext());
			child = child_it.next();
			assertEquals(child_it.hasNext(), false);
			assertSame(child, particlemodel_instance2);
		}

		public void testAddTwoChildren()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel_instance2 = new TestParticleModel();
			ParticleModel		particlemodel_instance3 = new TestParticleModel();

			assertNull(mChildAdded);
			assertTrue(particlemodel_instance.addParticleListener(this));
			try
			{
				particlemodel_instance.addChild(particlemodel_instance2);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mChildAdded, particlemodel_instance2);
			mChildAdded = null;
			try
			{
				particlemodel_instance.addChild(particlemodel_instance3);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mChildAdded, particlemodel_instance3);
			mChildAdded = null;
			assertSame(particlemodel_instance2.getParent(), particlemodel_instance);
			assertSame(particlemodel_instance3.getParent(), particlemodel_instance);
			Iterator<ParticleModel> child_it = particlemodel_instance.getChildren().iterator();
			Object child1 = null;
			Object child2 = null;
			assertTrue(child_it.hasNext());
			child1 = child_it.next();
			assertTrue(child_it.hasNext());
			child2 = child_it.next();
			assertEquals(child_it.hasNext(), false);
			assertTrue((child1 == particlemodel_instance2 && child2 == particlemodel_instance3) ||
				(child1 == particlemodel_instance3 && child2 == particlemodel_instance2));
			
			assertTrue(particlemodel_instance.containsChild(particlemodel_instance2));
			assertTrue(particlemodel_instance.containsChild(particlemodel_instance3));
		}

		public void testCountChildren()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel_instance2 = new TestParticleModel();
			ParticleModel		particlemodel_instance3 = new TestParticleModel();

			try
			{
				particlemodel_instance.addChild(particlemodel_instance2);
				particlemodel_instance.addChild(particlemodel_instance3);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertEquals(particlemodel_instance.countChildren(), 2);
		}

		public void testCountTypedChildren()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel2_instance1 = new TestParticleModel2();
			ParticleModel		particlemodel2_instance2 = new TestParticleModel2();
			ParticleModel		particlemodel3_instance = new TestParticleModel3();

			try
			{
				particlemodel_instance.addChild(particlemodel2_instance1);
				particlemodel_instance.addChild(particlemodel2_instance2);
				particlemodel_instance.addChild(particlemodel3_instance);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertEquals(particlemodel_instance.countChildren(TestParticleModel2.class), 2);
			assertEquals(particlemodel_instance.countChildren(TestParticleModel3.class), 1);
		}

		public void testGetTypedChildren()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel2_instance = new TestParticleModel2();
			ParticleModel		particlemodel3_instance = new TestParticleModel3();

			try
			{
				particlemodel_instance.addChild(particlemodel2_instance);
				particlemodel_instance.addChild(particlemodel3_instance);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			Iterator<TestParticleModel2>	child_it = particlemodel_instance.getChildren(TestParticleModel2.class).iterator();
			assertTrue(child_it.hasNext());
			TestParticleModel2 child = child_it.next();
			assertEquals(child_it.hasNext(), false);
			assertSame(child, particlemodel2_instance);
		}

		public void testRemoveInexistantChild()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel	particlemodel_instance2 = new TestParticleModel();

			assertNull(mChildRemoved);
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertEquals(particlemodel_instance.removeChild(particlemodel_instance2), false);
			assertNull(mChildRemoved);
		}

		public void testRemoveOneChild()
		{
			TestParticleModel	particlemodel_instance = new TestParticleModel();
			ParticleModel		particlemodel_instance2 = new TestParticleModel();

			assertNull(mChildRemoved);
			assertTrue(particlemodel_instance.addParticleListener(this));
			try
			{
				particlemodel_instance.addChild(particlemodel_instance2);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertTrue(particlemodel_instance.removeChild(particlemodel_instance2));
			assertSame(mChildRemoved, particlemodel_instance2);
			assertEquals(particlemodel_instance.getChildren().size(), 0);
		}

		public void testNoInitialProperties()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();

			assertEquals(particlemodel_instance.getProperties().size(), 0);
		}

		public void testInitialPropertyCountIsZero()
		{
			ParticleModel	particlemodel_instance = new TestParticleModel();

			assertEquals(particlemodel_instance.countProperties(), 0);
		}

		public void testAddOneProperty()
		{
			TestParticleModel		particlemodel_instance = null;
			ParticlePropertyModel	propertymodel_instance = null;

			particlemodel_instance = new TestParticleModel();
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertNull(mPropertyAdded);

			try
			{
				propertymodel_instance = new TestParticlePropertyModel1(particlemodel_instance, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mPropertyAdded, propertymodel_instance);

			assertSame(propertymodel_instance.getParticle(), particlemodel_instance);
			Iterator<TestParticlePropertyModel1> property_it = particlemodel_instance.getProperties(TestParticlePropertyModel1.class).iterator();
			assertTrue(property_it.hasNext());
			Object property = property_it.next();
			assertEquals(property_it.hasNext(), false);
			assertSame(property, propertymodel_instance);
		}

		public void testAddTheSamePropertyTwice()
		{
			TestParticleModel		particlemodel_instance = null;
			ParticlePropertyModel	propertymodel_instance = null;

			particlemodel_instance = new TestParticleModel();
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertNull(mPropertyAdded);

			try
			{
				propertymodel_instance = new TestParticlePropertyModel1(particlemodel_instance, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mPropertyAdded, propertymodel_instance);

			mPropertyAdded = null;
			try
			{
				particlemodel_instance.addProperty(propertymodel_instance);
				fail();
			}
			catch (GuiModelException e)
			{
				assertTrue(true);
			}
			assertNull(mPropertyAdded);
			Iterator<TestParticlePropertyModel1> property_it = particlemodel_instance.getProperties(TestParticlePropertyModel1.class).iterator();
			assertTrue(property_it.hasNext());
			TestParticlePropertyModel1 property = property_it.next();
			assertEquals(property_it.hasNext(), false);
			assertSame(property, propertymodel_instance);
		}

		public void testAddTwoProperties()
		{
			TestParticleModel		particlemodel_instance = null;
			ParticlePropertyModel	propertymodel_instance1 = null;
			ParticlePropertyModel	propertymodel_instance2 = null;

			particlemodel_instance = new TestParticleModel();
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertNull(mPropertyAdded);

			try
			{
				propertymodel_instance1 = new TestParticlePropertyModel1(particlemodel_instance, "particleproperty1");
				assertSame(mPropertyAdded, propertymodel_instance1);
				mPropertyAdded = null;
				propertymodel_instance2 = new TestParticlePropertyModel1(particlemodel_instance, "particleproperty2");
				assertSame(mPropertyAdded, propertymodel_instance2);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}

			assertSame(propertymodel_instance1.getParticle(), particlemodel_instance);
			assertSame(propertymodel_instance2.getParticle(), particlemodel_instance);
			Iterator<TestParticlePropertyModel1> property_it = particlemodel_instance.getProperties(TestParticlePropertyModel1.class).iterator();
			assertTrue(property_it.hasNext());
			TestParticlePropertyModel1 property1 = property_it.next();
			assertTrue(property_it.hasNext());
			TestParticlePropertyModel1 property2 = property_it.next();
			assertEquals(property_it.hasNext(), false);
			assertTrue((property1 == propertymodel_instance1 && property2 == propertymodel_instance2) ||
				(property2 == propertymodel_instance1 && property1 == propertymodel_instance2));
		}

		public void testCountProperties()
		{
			TestParticleModel particlemodel_instance = null;
			
			particlemodel_instance = new TestParticleModel();

			try
			{
				new TestParticlePropertyModel1(particlemodel_instance, "particleproperty1");
				new TestParticlePropertyModel1(particlemodel_instance, "particleproperty2");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			try
			{
				new TestParticlePropertyModel1(particlemodel_instance, "particleproperty3");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}

			assertEquals(particlemodel_instance.countProperties(), 3);
		}

		public void testCountTypedProperties()
		{
			TestParticleModel particlemodel_instance = null;

			particlemodel_instance = new TestParticleModel();

			try
			{
				new TestParticlePropertyModel1(particlemodel_instance, "particleproperty1");
				new TestParticlePropertyModel2(particlemodel_instance, "particleproperty1");
				new TestParticlePropertyModel3(particlemodel_instance, "particleproperty1");
				new TestParticlePropertyModel3(particlemodel_instance, "particleproperty2");
				new TestParticlePropertyModel4(particlemodel_instance, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}

			assertEquals(particlemodel_instance.countProperties(TestParticlePropertyModel1.class), 5);
			assertEquals(particlemodel_instance.countProperties(TestParticlePropertyModel2.class), 4);
			assertEquals(particlemodel_instance.countProperties(TestParticlePropertyModel3.class), 2);
			assertEquals(particlemodel_instance.countProperties(TestParticlePropertyModel4.class), 1);
		}

		public void testContainsProperty()
		{
			TestParticleModel particlemodel_instance1 = null;
			TestParticleModel particlemodel_instance2 = null;
			ParticlePropertyModel propertymodel_instance1 = null;
			ParticlePropertyModel propertymodel_instance2 = null;

			particlemodel_instance1 = new TestParticleModel();
			particlemodel_instance2 = new TestParticleModel();

			try
			{
				propertymodel_instance1 = new TestParticlePropertyModel(particlemodel_instance1, "particleproperty");
				propertymodel_instance2 = new TestParticlePropertyModel(particlemodel_instance2, "particleproperty");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertTrue(particlemodel_instance1.containsProperty(propertymodel_instance1));
			assertEquals(particlemodel_instance1.containsProperty(propertymodel_instance2), false);
		}

		public void testGetProperty()
		{
			TestParticleModel particlemodel_instance = null;
			ParticlePropertyModel propertymodel_instance1 = null;
			ParticlePropertyModel propertymodel_instance2 = null;
			
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
			assertSame(particlemodel_instance.getProperty(ParticlePropertyModel.class, "particleproperty1"), propertymodel_instance1);
			assertSame(particlemodel_instance.getProperty(TestParticlePropertyModel.class, "particleproperty1"), propertymodel_instance1);
			assertSame(particlemodel_instance.getProperty(TestParticlePropertyModel.class, "particleproperty2"), propertymodel_instance2);
			assertNull(particlemodel_instance.getProperty(TestParticlePropertyModel2.class, "particleproperty1"));
			assertNull(particlemodel_instance.getProperty(TestParticlePropertyModel.class, "particleproperty3"));
		}

		public void testRenameProperty()
		{
			TestParticleModel particlemodel_instance = null;
			ParticlePropertyModel propertymodel_instance1 = null;

			particlemodel_instance = new TestParticleModel();
			assertTrue(particlemodel_instance.addParticleListener(this));

			try
			{
				propertymodel_instance1 = new TestParticlePropertyModel(particlemodel_instance, "particleproperty1");
				new TestParticlePropertyModel(particlemodel_instance, "particleproperty2");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			try
			{
				assertEquals(particlemodel_instance.renameProperty(propertymodel_instance1, "particleproperty1"), false);
				assertNull(mPropertyRenamed);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			try
			{
				assertTrue(particlemodel_instance.renameProperty(propertymodel_instance1, "particleproperty3"));
				assertSame(mPropertyRenamed, propertymodel_instance1);
				mPropertyRenamed = null;
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			try
			{
				particlemodel_instance.renameProperty(propertymodel_instance1, "particleproperty2");
				fail();
			}
			catch (GuiModelException e)
			{
				assertTrue(true);
			}
		}
		public void testRemoveInexistantProperty()
		{
			TestParticleModel particlemodel_instance1 = null;
			TestParticleModel particlemodel_instance2 = null;
			ParticlePropertyModel propertymodel_instance = null;
			
			particlemodel_instance1 = new TestParticleModel();
			particlemodel_instance2 = new TestParticleModel();
			assertTrue(particlemodel_instance1.addParticleListener(this));
			assertTrue(particlemodel_instance2.addParticleListener(this));
			assertNull(mPropertyRemoved);
			
			try
			{
				propertymodel_instance = new TestParticlePropertyModel1(particlemodel_instance1, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}

			try
			{
				assertEquals(particlemodel_instance2.removeProperty(propertymodel_instance), false);
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertNull(mPropertyRemoved);
		}

		public void testRemoveOneProperty()
		{
			TestParticleModel particlemodel_instance = null;
			ParticlePropertyModel propertymodel_instance = null;
			
			particlemodel_instance = new TestParticleModel();
			assertTrue(particlemodel_instance.addParticleListener(this));
			assertNull(mPropertyRemoved);
			
			try
			{
				propertymodel_instance = new TestParticlePropertyModel1(particlemodel_instance, "particleproperty1");
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}

			try
			{
				assertTrue(particlemodel_instance.removeProperty(propertymodel_instance));
			}
			catch (GuiModelException e)
			{
				assertTrue(e.getMessage(), false);
			}
			assertSame(mPropertyRemoved, propertymodel_instance);
			assertNull(propertymodel_instance.getParticle());
			assertEquals(particlemodel_instance.getProperties(TestParticlePropertyModel1.class).size(), 0);
		}

		public void parentChanged()
		{
			mParentChanged = true;
		}

		public void childAdded(ParticleModel child)
		{
			mChildAdded = child;
		}

		public void childRemoved(ParticleModel child)
		{
			mChildRemoved = child;
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

	private static class TestParticleModel2 extends ParticleModel
	{
	}

	private static class TestParticleModel3 extends ParticleModel
	{
	}

	private static class TestParticlePropertyModel1 extends ParticlePropertyModel
	{
		public TestParticlePropertyModel1(ParticleModel particleModel, String name)
		throws GuiModelException
		{
			super(particleModel, name);
		}
	}

	private static class TestParticlePropertyModel2 extends TestParticlePropertyModel1
	{
		public TestParticlePropertyModel2(ParticleModel particleModel, String name)
		throws GuiModelException
		{
			super(particleModel, name);
		}
	}

	private static class TestParticlePropertyModel3 extends TestParticlePropertyModel2
	{
		public TestParticlePropertyModel3(ParticleModel particleModel, String name)
		throws GuiModelException
		{
			super(particleModel, name);
		}
	}

	private static class TestParticlePropertyModel4 extends TestParticlePropertyModel2
	{
		public TestParticlePropertyModel4(ParticleModel particleModel, String name)
		throws GuiModelException
		{
			super(particleModel, name);
		}
	}
}

