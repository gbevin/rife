/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSiteModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import java.util.Iterator;

import junit.framework.TestCase;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;

public class TestSiteModel extends TestCase implements ParticleModelListener
{
	private Object	mChildAdded = null;

	public TestSiteModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		SiteModel sitemodel_instance = new SiteModel();

		assertNotNull(sitemodel_instance);
		assertTrue(sitemodel_instance instanceof SiteModel);
	}

	public void testNoInitialElements()
	{
		SiteModel sitemodel_instance = new SiteModel();

		assertEquals(sitemodel_instance.getElements().size(), 0);
	}

	public void testInitialElementCountIsZero()
	{
		SiteModel sitemodel_instance = new SiteModel();

		assertEquals(sitemodel_instance.countElements(), 0);
	}

	public void testAddOneElement()
	{
		SiteModel sitemodel_instance = null;
		ElementModel elementmodel_instance = null;

		sitemodel_instance = new SiteModel();
		assertTrue(sitemodel_instance.addParticleListener(this));
		assertNull(mChildAdded);

		try
		{
			elementmodel_instance = new ElementModel("elementmodel1");
			sitemodel_instance.addElement(elementmodel_instance);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mChildAdded, elementmodel_instance);

		Iterator<ElementModel> element_it = sitemodel_instance.getElements().iterator();
		assertTrue(element_it.hasNext());
		Object element = element_it.next();
		assertEquals(element_it.hasNext(), false);
		assertSame(element, elementmodel_instance);
	}

	public void testAddAnotherEqualElement()
	{
		SiteModel sitemodel_instance = null;
		ElementModel elementmodel_instance1 = null;
		ElementModel elementmodel_instance2 = null;

		sitemodel_instance = new SiteModel();
		assertTrue(sitemodel_instance.addParticleListener(this));
		assertNull(mChildAdded);

		try
		{
			elementmodel_instance1 = new ElementModel("elementmodel1");
			elementmodel_instance2 = new ElementModel("elementmodel1");
			sitemodel_instance.addElement(elementmodel_instance1);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertSame(mChildAdded, elementmodel_instance1);

		try
		{
			sitemodel_instance.addElement(elementmodel_instance2);
			fail();
		}
		catch (GuiModelException e)
		{
			assertTrue(true);
			assertSame(mChildAdded, elementmodel_instance1);
		}
		
		Iterator<ElementModel> element_it = sitemodel_instance.getElements().iterator();
		assertTrue(element_it.hasNext());
		Object element = element_it.next();
		assertEquals(element_it.hasNext(), false);
		assertSame(element, elementmodel_instance1);
	}

	public void testAddTwoElements()
	{
		SiteModel sitemodel_instance = null;
		ElementModel elementmodel_instance1 = null;
		ElementModel elementmodel_instance2 = null;

		sitemodel_instance = new SiteModel();
		assertTrue(sitemodel_instance.addParticleListener(this));
		assertNull(mChildAdded);

		try
		{
			elementmodel_instance1 = new ElementModel("elementmodel1");
			elementmodel_instance2 = new ElementModel("elementmodel2");
			sitemodel_instance.addElement(elementmodel_instance1);
			assertSame(mChildAdded, elementmodel_instance1);
			sitemodel_instance.addElement(elementmodel_instance2);
			assertSame(mChildAdded, elementmodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		Iterator<ElementModel> element_it = sitemodel_instance.getElements().iterator();
		assertTrue(element_it.hasNext());
		Object element1 = element_it.next();
		assertTrue(element_it.hasNext());
		Object element2 = element_it.next();
		assertEquals(element_it.hasNext(), false);
		assertTrue((element1 == elementmodel_instance1 && element2 == elementmodel_instance2) ||
			(element2 == elementmodel_instance1 && element1 == elementmodel_instance2));
	}

	public void testCountElements()
	{
		SiteModel sitemodel_instance = null;
		ElementModel elementmodel_instance1 = null;
		ElementModel elementmodel_instance2 = null;

		sitemodel_instance = new SiteModel();

		try
		{
			elementmodel_instance1 = new ElementModel("elementmodel1");
			elementmodel_instance2 = new ElementModel("elementmodel2");
			sitemodel_instance.addElement(elementmodel_instance1);
			sitemodel_instance.addElement(elementmodel_instance2);
		}
		catch (GuiModelException e)
		{
			assertTrue(e.getMessage(), false);
		}
		assertEquals(sitemodel_instance.countElements(), 2);
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
	}

	public void propertyRemoved(ParticlePropertyModel property)
	{
	}

	public void propertyRenamed(ParticlePropertyModel property)
	{
	}
}

