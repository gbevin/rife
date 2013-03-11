/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestOrdinalManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import junit.framework.TestCase;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerFactory;

public class TestOrdinalManager extends TestCase
{
	private Datasource 								mDatasource = null;
	private OrdinalManager 							mOrdinalManager = null;
	private GenericQueryManager<Ordered> 			mOrderedManager = null;
	private OrdinalManager 							mOrdinalRestrictedManager = null;
	private GenericQueryManager<OrderedRestricted> 	mOrderedRestrictedManager = null;

	public TestOrdinalManager(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		
		mDatasource = datasource;
	}
	
	protected void setUp() throws Exception
	{
		mOrderedManager = GenericQueryManagerFactory.getInstance(mDatasource, Ordered.class);
		mOrdinalManager = new OrdinalManager(mDatasource, mOrderedManager.getTable(), "priority");
		mOrderedManager.install();
		mOrderedRestrictedManager = GenericQueryManagerFactory.getInstance(mDatasource, OrderedRestricted.class);
		mOrdinalRestrictedManager = new OrdinalManager(mDatasource, mOrderedRestrictedManager.getTable(), "priority", "restricted");
		mOrderedRestrictedManager.install();
	}
	
	protected void tearDown() throws Exception
	{
		mOrderedManager.remove();
		mOrderedRestrictedManager.remove();
	}

	public void testGetDirection()
	{
		assertSame(OrdinalManager.UP, OrdinalManager.Direction.getDirection("up"));
		assertSame(OrdinalManager.DOWN, OrdinalManager.Direction.getDirection("down"));
	}

	public void testInitializationIllegalArguments()
	{
		try
		{
			new OrdinalManager(null, "table", "ordinal");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			new OrdinalManager(mDatasource, null, "ordinal");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			new OrdinalManager(mDatasource, "table", null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			new OrdinalManager(mDatasource, "table", "ordinal", null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testInitialization()
	{
		assertEquals(mOrdinalManager.getTable(), mOrderedManager.getTable());
		assertEquals(mOrdinalManager.getOrdinalColumn(), "priority");
		assertNull(mOrdinalManager.getRestrictColumn());
	}

	public void testInitializationRestricted()
	{
		assertEquals(mOrdinalRestrictedManager.getTable(), mOrderedRestrictedManager.getTable());
		assertEquals(mOrdinalRestrictedManager.getOrdinalColumn(), "priority");
		assertEquals(mOrdinalRestrictedManager.getRestrictColumn(), "restricted");
	}

	public void testFree()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertTrue(mOrdinalManager.free(1));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
 		assertEquals(2, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(5, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testFreeRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.free(1, 1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
 		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(3, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalRestrictedManager.free(2, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
 		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(3, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered5).getPriority());
	}

	public void testFreeOutOfBounds()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));
		
		assertTrue(mOrdinalManager.free(5));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());

		assertFalse(mOrdinalManager.free(-1));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testFreeOutOfBoundsRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.free(1, 3));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertFalse(mOrdinalRestrictedManager.free(2, -1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());
	}

	public void testFreeOutOfBoundsUnknownRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.free(3, 1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());
	}

	public void testMove()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertTrue(mOrdinalManager.free(3));
		assertTrue(mOrdinalManager.update(1, 3));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(5, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testTighten()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(3));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(13));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(28));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(56));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(300));

		assertTrue(mOrdinalManager.tighten());
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testTightenRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(3).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(13).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(28).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(56).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(300).restricted(2));

		assertTrue(mOrdinalRestrictedManager.tighten(1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(56, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(300, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalRestrictedManager.tighten(2));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertFalse(mOrdinalRestrictedManager.tighten(3));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());
	}

	public void testObtainInsertOrdinal()
	{
		mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		mOrderedManager.save(new Ordered().name("ordered 5").priority(4));
		assertEquals(5, mOrdinalManager.obtainInsertOrdinal());
	}

	public void testObtainInsertOrdinalRestricted()
	{
		mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));
		assertEquals(3, mOrdinalRestrictedManager.obtainInsertOrdinal(1));
		assertEquals(2, mOrdinalRestrictedManager.obtainInsertOrdinal(2));
		assertEquals(0, mOrdinalRestrictedManager.obtainInsertOrdinal(3));
	}

	public void testMoveDown()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertTrue(mOrdinalManager.move(OrdinalManager.DOWN, 3));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered5).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered4).getPriority());
	}

	public void testMoveDownRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.move(OrdinalManager.DOWN, 1, 1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(OrdinalManager.DOWN, 2, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered5).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());
	}

	public void testMoveUp()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertTrue(mOrdinalManager.move(OrdinalManager.UP, 3));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testMoveUpRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.move(OrdinalManager.UP, 1, 2));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(OrdinalManager.UP, 2, 1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered5).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());
	}

	public void testMoveOther()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertTrue(mOrdinalManager.move(3, 1));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalManager.move(2, 4));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testMoveOtherRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.move(1, 2, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(2, 1, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered5).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(1, 0, 2));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered5).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(2, 0, 1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered5).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());
	}

	public void testMoveDownExtremity()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertTrue(mOrdinalManager.move(OrdinalManager.DOWN, 4));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testMoveDownExtremityRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.move(OrdinalManager.DOWN, 1, 2));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(OrdinalManager.DOWN, 2, 1));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());
	}

	public void testMoveUpExtremity()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertFalse(mOrdinalManager.move(OrdinalManager.UP, 0));
		assertEquals(0, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());
	}

	public void testMoveUpExtremityRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertFalse(mOrdinalRestrictedManager.move(OrdinalManager.UP, 1, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertFalse(mOrdinalRestrictedManager.move(OrdinalManager.UP, 2, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());
	}

	public void testMoveOtherExtremity()
	{
		int ordered1 = mOrderedManager.save(new Ordered().name("ordered 1").priority(0));
		int ordered2 = mOrderedManager.save(new Ordered().name("ordered 2").priority(1));
		int ordered3 = mOrderedManager.save(new Ordered().name("ordered 3").priority(2));
		int ordered4 = mOrderedManager.save(new Ordered().name("ordered 4").priority(3));
		int ordered5 = mOrderedManager.save(new Ordered().name("ordered 5").priority(4));

		assertTrue(mOrdinalManager.move(3, 0));
		assertEquals(0, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered2).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalManager.move(2, 8));
		assertEquals(0, mOrderedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedManager.restore(ordered1).getPriority());
		assertEquals(2, mOrderedManager.restore(ordered3).getPriority());
		assertEquals(3, mOrderedManager.restore(ordered5).getPriority());
		assertEquals(4, mOrderedManager.restore(ordered2).getPriority());
	}

	public void testMoveOtherExtremityRestricted()
	{
		int ordered1 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 1").priority(0).restricted(1));
		int ordered2 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 2").priority(1).restricted(1));
		int ordered3 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 3").priority(2).restricted(1));
		int ordered4 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 4").priority(0).restricted(2));
		int ordered5 = mOrderedRestrictedManager.save(new OrderedRestricted().name("ordered 5").priority(1).restricted(2));

		assertTrue(mOrdinalRestrictedManager.move(1, 2, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(2, 1, 0));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered5).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(1, 0, 6));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered5).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered4).getPriority());

		assertTrue(mOrdinalRestrictedManager.move(2, 0, 7));
		assertEquals(0, mOrderedRestrictedManager.restore(ordered1).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered2).getPriority());
		assertEquals(2, mOrderedRestrictedManager.restore(ordered3).getPriority());
		assertEquals(0, mOrderedRestrictedManager.restore(ordered4).getPriority());
		assertEquals(1, mOrderedRestrictedManager.restore(ordered5).getPriority());
	}
}
