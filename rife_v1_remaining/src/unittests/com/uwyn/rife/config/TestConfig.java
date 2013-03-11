/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestConfig.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.config;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import com.uwyn.rife.config.exceptions.ConfigErrorException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.SerializationUtils;
import com.uwyn.rife.tools.exceptions.SerializationUtilsErrorException;

public class TestConfig extends TestCase
{
	public TestConfig(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		Config config = new Config();
		
		assertNotNull(config);
	}
	
	public void testValidParameters()
	{
		Config config = new Config();
		
		assertEquals(config.countParameters(), 0);
		config.setParameter("paramstring", "astring");
		config.setParameter("parambool1", "0");
		config.setParameter("parambool2", "1");
		config.setParameter("parambool3", "false");
		config.setParameter("parambool4", "true");
		config.setParameter("parambool5", "f");
		config.setParameter("parambool6", "t");
		config.setParameter("paramchar", "C");
		config.setParameter("paramint", "5133");
		config.setParameter("paramlong", "8736478");
		config.setParameter("paramfloat", "545.2546");
		config.setParameter("paramdouble", "7863.3434353");
		
		assertTrue(config.hasParameter("paramstring"));
		assertTrue(config.hasParameter("parambool1"));
		assertTrue(config.hasParameter("parambool2"));
		assertTrue(config.hasParameter("parambool3"));
		assertTrue(config.hasParameter("parambool4"));
		assertTrue(config.hasParameter("parambool5"));
		assertTrue(config.hasParameter("parambool6"));
		assertTrue(config.hasParameter("paramchar"));
		assertTrue(config.hasParameter("paramint"));
		assertTrue(config.hasParameter("paramlong"));
		assertTrue(config.hasParameter("paramfloat"));
		assertTrue(config.hasParameter("paramdouble"));
		assertEquals(config.countParameters(), 12);
		
		assertFalse(config.isFinalParameter("paramstring"));
		assertFalse(config.isFinalParameter("parambool1"));
		assertFalse(config.isFinalParameter("parambool2"));
		assertFalse(config.isFinalParameter("parambool3"));
		assertFalse(config.isFinalParameter("parambool4"));
		assertFalse(config.isFinalParameter("parambool5"));
		assertFalse(config.isFinalParameter("parambool6"));
		assertFalse(config.isFinalParameter("paramchar"));
		assertFalse(config.isFinalParameter("paramint"));
		assertFalse(config.isFinalParameter("paramlong"));
		assertFalse(config.isFinalParameter("paramfloat"));
		assertFalse(config.isFinalParameter("paramdouble"));

		assertEquals(config.getString("paramstring"), "astring");
		assertEquals(config.getBool("parambool1"), false);
		assertEquals(config.getBool("parambool2"), true);
		assertEquals(config.getBool("parambool3"), false);
		assertEquals(config.getBool("parambool4"), true);
		assertEquals(config.getBool("parambool5"), false);
		assertEquals(config.getBool("parambool6"), true);
		assertEquals(config.getChar("paramchar"), 'C');
		assertEquals(config.getInt("paramint"), 5133);
		assertEquals(config.getLong("paramlong"), 8736478L);
		assertEquals(config.getFloat("paramfloat"), 545.2546f, 0);
		assertEquals(config.getDouble("paramdouble"), 7863.3434353d, 0);

		config.removeParameter("paramstring");
		assertFalse(config.hasParameter("paramstring"));
		assertNull(config.getString("paramstring"));
	}
	
	public void testInvalidParameters()
	{
		Config config = new Config();
		
		assertEquals(config.countParameters(), 0);
		assertEquals(config.hasParameter("paramstring"), false);
		assertEquals(config.hasParameter("parambool"), false);
		assertEquals(config.hasParameter("paramchar"), false);
		assertEquals(config.hasParameter("paramint"), false);
		assertEquals(config.hasParameter("paramlong"), false);
		assertEquals(config.hasParameter("paramfloat"), false);
		assertEquals(config.hasParameter("paramdouble"), false);
		
		assertEquals(config.getString("paramstring"), null);
		assertEquals(config.getBool("parambool"), false);
		assertEquals(config.getChar("paramchar"), 0);
		assertEquals(config.getInt("paramint"), 0);
		assertEquals(config.getLong("paramlong"), 0);
		assertEquals(config.getFloat("paramfloat"), 0, 0);
		assertEquals(config.getDouble("paramdouble"), 0, 0);
	}
	
	public void testDefaultParameters()
	{
		Config config = new Config();
		
		config.setParameter("paramstring", "astring");
		config.setParameter("parambool", true);
		config.setParameter("paramchar", 'C');
		config.setParameter("paramint", 5133);
		config.setParameter("paramlong", 8736478L);
		config.setParameter("paramfloat", 545.2546f);
		config.setParameter("paramdouble", 7863.3434353d);
		
		assertEquals(config.getString("paramstring", "defaultstring"), "astring");
		assertEquals(config.getBool("parambool", false), true);
		assertEquals(config.getChar("paramchar", 'H'), 'C');
		assertEquals(config.getInt("paramint", 834), 5133);
		assertEquals(config.getLong("paramlong", 349875L), 8736478L);
		assertEquals(config.getFloat("paramfloat", 354.9457f), 545.2546f, 0);
		assertEquals(config.getDouble("paramdouble", 9347.784578d), 7863.3434353d, 0);
		
		assertEquals(config.getString("paramstring2", "defaultstring"), "defaultstring");
		assertEquals(config.getBool("parambool2", false), false);
		assertEquals(config.getChar("paramchar2", 'H'), 'H');
		assertEquals(config.getInt("paramint2", 834), 834);
		assertEquals(config.getLong("paramlong2", 349875L), 349875L);
		assertEquals(config.getFloat("paramfloat2", 354.9457f), 354.9457f, 0);
		assertEquals(config.getDouble("paramdouble2", 9347.784578d), 9347.784578d, 0);
	}
	
	public void testFinalParameters()
	{
		Config config = new Config();
		
		config.setParameter("final", "first");
		assertFalse(config.isFinalParameter("final"));
		assertEquals(config.getString("final"), "first");
		
		config.setFinalParameter("final", true);
		assertTrue(config.isFinalParameter("final"));
		config.setParameter("final", "second");
		assertEquals(config.getString("final"), "first");
		
		config.removeParameter("final");
		assertTrue(config.isFinalParameter("final"));
		assertEquals(config.getString("final"), "first");
		
		config.setFinalParameter("final", false);
		assertFalse(config.isFinalParameter("final"));
		config.setParameter("final", "second");
		assertEquals(config.getString("final"), "second");

		config.removeParameter("final");
		assertNull(config.getString("final"));
	}
	
	public void testSerialization()
	{
		Config config = new Config();
		
		SerializableClass serializable1 = new SerializableClass(459, "thestring");
		SerializableClass serializable2 = new SerializableClass(680824, "thesecondstring");
		SerializableClass serializable3 = null;
		try
		{
			config.setParameter("paramserializable", serializable1);
			serializable3 = config.getSerializable("paramserializable");
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(serializable1, serializable3);
		assertNotSame(serializable1, serializable3);

		config.setParameter("paramstring", "astring");
		serializable3 = config.getSerializable("paramstring");
		assertNull(serializable3);
		serializable3 = config.getSerializable("paramstring", serializable2);
		assertEquals(serializable3, serializable2);
	}
	
	public void testLists()
	{
		Config config = new Config();
		
		assertEquals(config.countLists(), 0);
		config.addListItem("list1", "item1");
		config.addListItem("list1", "item2");
		config.addListItem("list1", "item3");
		config.addListItem("list2", "item4");
		config.addListItem("list2", "item5");
		
		assertTrue(config.hasList("list1"));
		assertTrue(config.hasList("list2"));
		assertEquals(config.countLists(), 2);
		
		assertFalse(config.isFinalList("list1"));
		assertFalse(config.isFinalList("list2"));

		Collection<String>	items = null;
		Iterator<String>	item_it = null;
		
		items = config.getStringItems("list1");
		assertEquals(items.size(), 3);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item1");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item2");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item3");
		assertEquals(item_it.hasNext(), false);
		
		config.addListItem("list1", "item3");
		assertEquals(items.size(), 4);
		config.addListItem("list1", "item6");
		assertEquals(items.size(), 5);
		
		items = config.getStringItems("list2");
		assertEquals(items.size(), 2);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item4");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item5");
		assertEquals(item_it.hasNext(), false);
		
		config.clearList("list1");
		assertEquals(config.getStringItems("list1").size(), 0);
		
		config.removeList("list2");
		assertTrue(config.hasList("list1"));
		assertTrue(!config.hasList("list2"));
		assertEquals(config.countLists(), 1);
	}
	
	public void testListBool()
	{
		Config config = new Config();
		
		Collection<Boolean> booleans = null;
		config.addListItem("booleanlist", true);
		config.addListItem("booleanlist", false);
		config.addListItem("booleanlist", true);
		booleans = config.getBoolItems("booleanlist");
		
		assertEquals(3, booleans.size());
		Iterator<Boolean> booleans_it = booleans.iterator();
		assertEquals(booleans_it.hasNext(), true);
		assertEquals(booleans_it.next().booleanValue(), true);
		assertEquals(booleans_it.hasNext(), true);
		assertEquals(booleans_it.next().booleanValue(), false);
		assertEquals(booleans_it.hasNext(), true);
		assertEquals(booleans_it.next().booleanValue(), true);
		assertEquals(booleans_it.hasNext(), false);
		
		config.addListItem("stringlist", "thestring");
		config.addListItem("stringlist", "anotherstring");
		config.addListItem("stringlist", true);
		config.addListItem("stringlist", "athirdstring");
		booleans = config.getBoolItems("stringlist");
		assertEquals(4, booleans.size());
		booleans_it = booleans.iterator();
		assertEquals(booleans_it.hasNext(), true);
		assertEquals(booleans_it.next().booleanValue(), false);
		assertEquals(booleans_it.hasNext(), true);
		assertEquals(booleans_it.next().booleanValue(), false);
		assertEquals(booleans_it.hasNext(), true);
		assertEquals(booleans_it.next().booleanValue(), true);
		assertEquals(booleans_it.hasNext(), true);
		assertEquals(booleans_it.next().booleanValue(), false);
		assertEquals(booleans_it.hasNext(), false);
		
		Collection<String> items = config.getStringItems("stringlist");
		assertEquals(items.size(), 4);
		Iterator<String>item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "thestring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "anotherstring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "true");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "athirdstring");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testListChar()
	{
		Config config = new Config();
		
		Collection<Character> chars = null;
		config.addListItem("charlist", 'G');
		config.addListItem("charlist", 'i');
		config.addListItem("charlist", 'M');
		chars = config.getCharItems("charlist");
		
		assertEquals(3, chars.size());
		Iterator<Character> chars_it = chars.iterator();
		assertEquals(chars_it.hasNext(), true);
		assertEquals(chars_it.next().charValue(), 'G');
		assertEquals(chars_it.hasNext(), true);
		assertEquals(chars_it.next().charValue(), 'i');
		assertEquals(chars_it.hasNext(), true);
		assertEquals(chars_it.next().charValue(), 'M');
		assertEquals(chars_it.hasNext(), false);
		
		config.addListItem("stringlist", "thestring");
		config.addListItem("stringlist", "anotherstring");
		config.addListItem("stringlist", 'O');
		config.addListItem("stringlist", "athirdstring");
		chars = config.getCharItems("stringlist");
		assertEquals(4, chars.size());
		chars_it = chars.iterator();
		assertEquals(chars_it.hasNext(), true);
		assertEquals(chars_it.next().charValue(), 't');
		assertEquals(chars_it.hasNext(), true);
		assertEquals(chars_it.next().charValue(), 'a');
		assertEquals(chars_it.hasNext(), true);
		assertEquals(chars_it.next().charValue(), 'O');
		assertEquals(chars_it.hasNext(), true);
		assertEquals(chars_it.next().charValue(), 'a');
		assertEquals(chars_it.hasNext(), false);
		
		Collection<String> items = config.getStringItems("stringlist");
		assertEquals(items.size(), 4);
		Iterator<String>item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "thestring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "anotherstring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "O");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "athirdstring");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testListInt()
	{
		Config config = new Config();
		
		Collection<Integer> ints = null;
		config.addListItem("intlist", 96285);
		config.addListItem("intlist", 1596);
		config.addListItem("intlist", 4174);
		ints = config.getIntItems("intlist");
		
		assertEquals(3, ints.size());
		Iterator<Integer> ints_it = ints.iterator();
		assertEquals(ints_it.hasNext(), true);
		assertEquals(ints_it.next().intValue(), 96285);
		assertEquals(ints_it.hasNext(), true);
		assertEquals(ints_it.next().intValue(), 1596);
		assertEquals(ints_it.hasNext(), true);
		assertEquals(ints_it.next().intValue(), 4174);
		assertEquals(ints_it.hasNext(), false);
		
		config.addListItem("stringlist", "thestring");
		config.addListItem("stringlist", "anotherstring");
		config.addListItem("stringlist", 88646);
		config.addListItem("stringlist", "athirdstring");
		ints = config.getIntItems("stringlist");
		assertEquals(1, ints.size());
		ints_it = ints.iterator();
		assertEquals(ints_it.hasNext(), true);
		assertEquals(ints_it.next().intValue(), 88646);
		assertEquals(ints_it.hasNext(), false);
		
		Collection<String> items = config.getStringItems("stringlist");
		assertEquals(items.size(), 4);
		Iterator<String>item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "thestring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "anotherstring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "88646");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "athirdstring");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testListLong()
	{
		Config config = new Config();
		
		Collection<Long> longs = null;
		config.addListItem("longlist", 69778249L);
		config.addListItem("longlist", 6687792094L);
		config.addListItem("longlist", 24425829L);
		longs = config.getLongItems("longlist");
		
		assertEquals(3, longs.size());
		Iterator<Long> longs_it = longs.iterator();
		assertEquals(longs_it.hasNext(), true);
		assertEquals(longs_it.next().longValue(), 69778249L);
		assertEquals(longs_it.hasNext(), true);
		assertEquals(longs_it.next().longValue(), 6687792094L);
		assertEquals(longs_it.hasNext(), true);
		assertEquals(longs_it.next().longValue(), 24425829L);
		assertEquals(longs_it.hasNext(), false);
		
		config.addListItem("stringlist", "thestring");
		config.addListItem("stringlist", "anotherstring");
		config.addListItem("stringlist", 7098634812L);
		config.addListItem("stringlist", "athirdstring");
		longs = config.getLongItems("stringlist");
		assertEquals(1, longs.size());
		longs_it = longs.iterator();
		assertEquals(longs_it.hasNext(), true);
		assertEquals(longs_it.next().longValue(), 7098634812L);
		assertEquals(longs_it.hasNext(), false);
		
		Collection<String> items = config.getStringItems("stringlist");
		assertEquals(items.size(), 4);
		Iterator<String>item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "thestring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "anotherstring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "7098634812");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "athirdstring");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testListFloat()
	{
		Config config = new Config();
		
		Collection<Float> floats = null;
		config.addListItem("floatlist", 425.68f);
		config.addListItem("floatlist", 9682.54f);
		config.addListItem("floatlist", 134.98f);
		floats = config.getFloatItems("floatlist");
		
		assertEquals(3, floats.size());
		Iterator<Float> floats_it = floats.iterator();
		assertEquals(floats_it.hasNext(), true);
		assertEquals(floats_it.next(), 425.68f);
		assertEquals(floats_it.hasNext(), true);
		assertEquals(floats_it.next(), 9682.54f);
		assertEquals(floats_it.hasNext(), true);
		assertEquals(floats_it.next(), 134.98f);
		assertEquals(floats_it.hasNext(), false);
		
		config.addListItem("stringlist", "thestring");
		config.addListItem("stringlist", "anotherstring");
		config.addListItem("stringlist", 4512.78f);
		config.addListItem("stringlist", "athirdstring");
		floats = config.getFloatItems("stringlist");
		assertEquals(1, floats.size());
		floats_it = floats.iterator();
		assertEquals(floats_it.hasNext(), true);
		assertEquals(floats_it.next(), 4512.78f);
		assertEquals(floats_it.hasNext(), false);
		
		Collection<String> items = config.getStringItems("stringlist");
		assertEquals(items.size(), 4);
		Iterator<String>item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "thestring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "anotherstring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "4512.78");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "athirdstring");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testListDouble()
	{
		Config config = new Config();
		
		Collection<Double> doubles = null;
		config.addListItem("doublelist", 69978.23524d);
		config.addListItem("doublelist", 413387.23451d);
		config.addListItem("doublelist", 441534.79798d);
		doubles = config.getDoubleItems("doublelist");
		
		assertEquals(3, doubles.size());
		Iterator<Double> doubles_it = doubles.iterator();
		assertEquals(doubles_it.hasNext(), true);
		assertEquals(doubles_it.next(), 69978.23524d);
		assertEquals(doubles_it.hasNext(), true);
		assertEquals(doubles_it.next(), 413387.23451d);
		assertEquals(doubles_it.hasNext(), true);
		assertEquals(doubles_it.next(), 441534.79798d);
		assertEquals(doubles_it.hasNext(), false);
		
		config.addListItem("stringlist", "thestring");
		config.addListItem("stringlist", "anotherstring");
		config.addListItem("stringlist", 551348.7986d);
		config.addListItem("stringlist", "athirdstring");
		doubles = config.getDoubleItems("stringlist");
		assertEquals(1, doubles.size());
		doubles_it = doubles.iterator();
		assertEquals(doubles_it.hasNext(), true);
		assertEquals(doubles_it.next(), 551348.7986d);
		assertEquals(doubles_it.hasNext(), false);
		
		Collection<String> items = config.getStringItems("stringlist");
		assertEquals(items.size(), 4);
		Iterator<String>item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "thestring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "anotherstring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "551348.7986");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "athirdstring");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testListSerialization() throws SerializationUtilsErrorException
	{
		Config config = new Config();
		
		SerializableClass serializable1 = new SerializableClass(459, "thestring");
		SerializableClass serializable2 = new SerializableClass(69823, "anotherstring");
		SerializableClass serializable3 = new SerializableClass(499417, "athirdstring");
		Collection<SerializableClass> serializables = null;
		try
		{
			config.addListItem("serializablelist", serializable1);
			config.addListItem("serializablelist", serializable2);
			config.addListItem("serializablelist", serializable3);
			serializables = config.getSerializableItems("serializablelist");
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		assertEquals(3, serializables.size());
		Iterator<SerializableClass> serializables_it = serializables.iterator();
		assertEquals(serializables_it.hasNext(), true);
		assertEquals(serializables_it.next(), serializable1);
		assertEquals(serializables_it.hasNext(), true);
		assertEquals(serializables_it.next(), serializable2);
		assertEquals(serializables_it.hasNext(), true);
		assertEquals(serializables_it.next(), serializable3);
		assertEquals(serializables_it.hasNext(), false);
		
		config.addListItem("stringlist", "thestring");
		config.addListItem("stringlist", "anotherstring");
		try
		{
			config.addListItem("stringlist", serializable2);
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		config.addListItem("stringlist", "athirdstring");
		serializables = config.getSerializableItems("stringlist");
		assertEquals(1, serializables.size());
		serializables_it = serializables.iterator();
		assertEquals(serializables_it.hasNext(), true);
		assertEquals(serializables_it.next(), serializable2);
		assertEquals(serializables_it.hasNext(), false);
		
		Collection<String> items = config.getStringItems("stringlist");
		assertEquals(items.size(), 4);
		Iterator<String>item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "thestring");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "anotherstring");
		assertEquals(item_it.hasNext(), true);
		String serialized = item_it.next();
		assertEquals(SerializationUtils.serializeToString(serializable2), serialized);
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "athirdstring");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testFinalLists()
	{
		Config config = new Config();
		
		Collection<String>	items = null;
		Iterator<String>	item_it = null;

		config.addListItem("final", "item1");
		config.addListItem("final", "item2");
		config.addListItem("final", "item3");
		assertFalse(config.isFinalList("final"));
		
		items = config.getStringItems("final");
		assertEquals(items.size(), 3);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item1");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item2");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item3");
		assertEquals(item_it.hasNext(), false);
		
		config.setFinalList("final", true);
		assertTrue(config.isFinalList("final"));
		config.addListItem("final", "item4");
		items = config.getStringItems("final");
		assertEquals(items.size(), 3);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item1");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item2");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item3");
		assertEquals(item_it.hasNext(), false);
		
		config.clearList("final");
		assertEquals(config.getStringItems("final").size(), 3);
		
		config.removeList("final");
		assertTrue(config.hasList("final"));
		assertEquals(config.getStringItems("final").size(), 3);
		
		config.setFinalList("final", false);
		assertFalse(config.isFinalList("final"));
		config.addListItem("final", "item4");
		items = config.getStringItems("final");
		assertEquals(items.size(), 4);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item1");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item2");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item3");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item4");
		assertEquals(item_it.hasNext(), false);

		config.clearList("final");
		assertEquals(config.getStringItems("final").size(), 0);
		
		config.removeList("final");
		assertFalse(config.hasList("final"));
	}
	
	public void testXmlOutput() throws SerializationUtilsErrorException
	{
		Config config = new Config();
		
		config.setParameter("paramstring", "astring");
		config.setFinalParameter("paramstring", true);
		config.setParameter("parambool", true);
		config.setParameter("paramchar", 'C');
		config.setParameter("paramint", 5133);
		config.setParameter("paramlong", 8736478L);
		config.setParameter("paramfloat", 545.2546f);
		config.setParameter("paramdouble", 7863.3434353d);
		config.addListItem("list1", "item1");
		config.addListItem("list1", "item2");
		config.addListItem("list1", "item3");
		config.addListItem("list2", "item4");
		config.addListItem("list2", "item5");
		config.setFinalList("list2", true);
		SerializableClass serializable1 = new SerializableClass(69823, "anotherstring");
		SerializableClass serializable2 = new SerializableClass(459, "thestring");
		SerializableClass serializable3 = new SerializableClass(499417, "athirdstring");
		try
		{
			config.setParameter("paramserializable", serializable1);
			config.addListItem("serializablelist", serializable2);
			config.addListItem("serializablelist", serializable3);
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		String xml = config.toXml();
		assertEquals(xml, "<config>\n"+
			"\t<list name=\"list1\">\n"+
			"\t\t<item>item1</item>\n"+
			"\t\t<item>item2</item>\n"+
			"\t\t<item>item3</item>\n"+
			"\t</list>\n"+
			"\t<list name=\"list2\" final=\"true\">\n"+
			"\t\t<item>item4</item>\n"+
			"\t\t<item>item5</item>\n"+
			"\t</list>\n"+
			"\t<list name=\"serializablelist\">\n"+
			"\t\t<item>"+SerializationUtils.serializeToString(serializable2)+"</item>\n"+
			"\t\t<item>"+SerializationUtils.serializeToString(serializable3)+"</item>\n"+
			"\t</list>\n"+
			"\t<param name=\"parambool\">true</param>\n"+
			"\t<param name=\"paramchar\">C</param>\n"+
			"\t<param name=\"paramdouble\">7863.3434353</param>\n"+
			"\t<param name=\"paramfloat\">545.2546</param>\n"+
			"\t<param name=\"paramint\">5133</param>\n"+
			"\t<param name=\"paramlong\">8736478</param>\n"+
			"\t<param name=\"paramserializable\">"+SerializationUtils.serializeToString(serializable1)+"</param>\n"+
			"\t<param name=\"paramstring\" final=\"true\">astring</param>\n"+
			"</config>\n");
	}

	public void testStoreXml()
	{
		try
		{
			Config config = new Config();
			
			config.setParameter("paramstring", "astring");
			config.setFinalParameter("paramstring", true);
			config.setParameter("parambool", true);
			config.setParameter("paramchar", 'C');
			config.setParameter("paramint", 5133);
			config.setParameter("paramlong", 8736478L);
			config.setParameter("paramfloat", 545.2546f);
			config.setParameter("paramdouble", 7863.3434353d);
			config.addListItem("list1", "item1");
			config.addListItem("list1", "item2");
			config.addListItem("list1", "item3");
			config.addListItem("list2", "item4");
			config.addListItem("list2", "item5");
			config.setFinalList("list2", true);
			
			String	xml_filename = "config_storexml_test.xml";
			String	xml_path = RifeConfig.Global.getTempPath()+File.separator+xml_filename;
			File	xml_file = new File(xml_path);
			config.storeToXml(xml_file);
			
			Config config_stored = new Config(xml_filename, ResourceFinderClasspath.getInstance());
			
			assertEquals(config_stored.countParameters(), 7);
			
			assertTrue(config_stored.hasParameter("paramstring"));
			assertTrue(config_stored.hasParameter("parambool"));
			assertTrue(config_stored.hasParameter("paramchar"));
			assertTrue(config_stored.hasParameter("paramint"));
			assertTrue(config_stored.hasParameter("paramlong"));
			assertTrue(config_stored.hasParameter("paramfloat"));
			assertTrue(config_stored.hasParameter("paramdouble"));
			assertEquals(config_stored.getString("paramstring"), "astring");
			assertEquals(config_stored.getBool("parambool"), true);
			assertEquals(config_stored.getChar("paramchar"), 'C');
			assertEquals(config_stored.getInt("paramint"), 5133);
			assertEquals(config_stored.getLong("paramlong"), 8736478L);
			assertEquals(config_stored.getFloat("paramfloat"), 545.2546f, 0);
			assertEquals(config_stored.getDouble("paramdouble"), 7863.3434353d, 0);
			assertTrue(config_stored.isFinalParameter("paramstring"));
			assertFalse(config_stored.isFinalParameter("parambool"));
			assertFalse(config_stored.isFinalParameter("paramchar"));
			assertFalse(config_stored.isFinalParameter("paramint"));
			assertFalse(config_stored.isFinalParameter("paramlong"));
			assertFalse(config_stored.isFinalParameter("paramfloat"));
			assertFalse(config_stored.isFinalParameter("paramdouble"));
			
			assertEquals(config_stored.countLists(), 2);
			
			assertTrue(config_stored.hasList("list1"));
			assertTrue(config_stored.hasList("list2"));
			assertFalse(config_stored.isFinalList("list1"));
			assertTrue(config_stored.isFinalList("list2"));

			Collection<String>	items = null;
			Iterator<String>	item_it = null;
			
			items = config_stored.getStringItems("list1");
			assertEquals(items.size(), 3);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item1");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item2");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item3");
			assertEquals(item_it.hasNext(), false);
			
			items = config_stored.getStringItems("list2");
			assertEquals(items.size(), 2);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item4");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item5");
			assertEquals(item_it.hasNext(), false);
			
			config_stored.setParameter("paramstring2", "anotherstring");
			config_stored.addListItem("list3", "item6");
			config_stored.addListItem("list3", "item7");
			
			assertTrue(config_stored.hasList("list3"));
			
			config_stored.storeToXml();

			Config config_stored2 = new Config(xml_filename, ResourceFinderClasspath.getInstance());
			assertEquals(config_stored2.countParameters(), 8);
			assertEquals(config_stored2.getString("paramstring2"), "anotherstring");
			assertEquals(config_stored2.countLists(), 3);
			assertTrue(config_stored2.hasList("list1"));
			assertTrue(config_stored2.hasList("list2"));
			assertTrue(config_stored2.hasList("list3"));
			items = config_stored.getStringItems("list3");
			assertEquals(items.size(), 2);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item6");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item7");
			assertEquals(item_it.hasNext(), false);

			xml_file.delete();
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testStorePreferences()
	{
		String		preferences_path = "test_rifeconfig_preferences_integration";
		Preferences	preferences = Preferences.userRoot().node(preferences_path);
		
		try
		{
			Config config = new Config();
			
			config.setParameter("paramstring", "astring");
			config.setParameter("paramstring2", "astring2");
			config.setParameter("parambool", true);
			config.setParameter("paramchar", 'C');
			config.setParameter("paramint", 5133);
			config.setParameter("paramlong", 8736478L);
			config.setParameter("paramfloat", 545.2546f);
			config.setParameter("paramdouble", 7863.3434353d);
			config.addListItem("list1", "item1");
			config.addListItem("list1", "item2");
			config.addListItem("list1", "item3");
			config.addListItem("list2", "item4");
			config.addListItem("list2", "item5");
			config.addListItem("list3", "item6");
			config.addListItem("list3", "item7");
			config.addListItem("list3", "item8");
			
			config.storeToPreferences(preferences);
			
			Config config_other = new Config();
			
			assertEquals(config_other.countParameters(), 0);
			
			config_other.setParameter("paramstring", "afirststring");
			config_other.setParameter("paramstring2", "afirststring2");
			config_other.setFinalParameter("paramstring2", true);
			config_other.setParameter("paramstring3", "afirststring3");
			config_other.setParameter("parambool", false);
			config_other.setParameter("paramchar", 'D');
			config_other.setParameter("paramint", 698);
			config_other.setParameter("paramlong", 985835L);
			config_other.setParameter("paramfloat", 978.14898f);
			config_other.setParameter("paramdouble", 6098.1439724d);
			config_other.addListItem("list1", "item1a");
			config_other.addListItem("list1", "item2a");
			config_other.addListItem("list2", "item3a");
			config_other.setFinalList("list2", true);

			assertEquals(config_other.countParameters(), 9);
			
			assertTrue(config_other.hasList("list1"));
			assertTrue(config_other.hasList("list2"));
			
			assertTrue(config_other.hasParameter("paramstring"));
			assertTrue(config_other.hasParameter("paramstring2"));
			assertTrue(config_other.hasParameter("paramstring3"));
			assertTrue(config_other.hasParameter("parambool"));
			assertTrue(config_other.hasParameter("paramchar"));
			assertTrue(config_other.hasParameter("paramint"));
			assertTrue(config_other.hasParameter("paramlong"));
			assertTrue(config_other.hasParameter("paramfloat"));
			assertTrue(config_other.hasParameter("paramdouble"));
			assertEquals(config_other.getString("paramstring"), "afirststring");
			assertEquals(config_other.getString("paramstring2"), "afirststring2");
			assertEquals(config_other.getString("paramstring3"), "afirststring3");
			assertEquals(config_other.getBool("parambool"), false);
			assertEquals(config_other.getChar("paramchar"), 'D');
			assertEquals(config_other.getInt("paramint"), 698);
			assertEquals(config_other.getLong("paramlong"), 985835L);
			assertEquals(config_other.getFloat("paramfloat"), 978.14898f, 0);
			assertEquals(config_other.getDouble("paramdouble"), 6098.1439724d, 0);
			
			assertEquals(config_other.countLists(), 2);

			Collection<String>	items = null;
			Iterator<String>	item_it = null;
			
			items = config_other.getStringItems("list1");
			assertEquals(items.size(), 2);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item1a");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item2a");
			assertEquals(item_it.hasNext(), false);
			
			items = config_other.getStringItems("list2");
			assertEquals(items.size(), 1);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item3a");
			assertEquals(item_it.hasNext(), false);
			
			config_other.setPreferencesNode(preferences);
			
			assertEquals(config_other.countParameters(), 10);
			
			assertTrue(config_other.hasParameter("paramstring"));
			assertTrue(config_other.hasParameter("paramstring2"));
			assertTrue(config_other.hasParameter("paramstring3"));
			assertTrue(config_other.hasParameter("parambool"));
			assertTrue(config_other.hasParameter("paramchar"));
			assertTrue(config_other.hasParameter("paramint"));
			assertTrue(config_other.hasParameter("paramlong"));
			assertTrue(config_other.hasParameter("paramfloat"));
			assertTrue(config_other.hasParameter("paramdouble"));
			assertEquals(config_other.getString("paramstring"), "astring");
			assertEquals(config_other.getString("paramstring2"), "afirststring2");
			assertEquals(config_other.getString("paramstring3"), "afirststring3");
			assertEquals(config_other.getBool("parambool"), true);
			assertEquals(config_other.getChar("paramchar"), 'C');
			assertEquals(config_other.getInt("paramint"), 5133);
			assertEquals(config_other.getLong("paramlong"), 8736478L);
			assertEquals(config_other.getFloat("paramfloat"), 545.2546f, 0);
			assertEquals(config_other.getDouble("paramdouble"), 7863.3434353d, 0);
			
			assertEquals(config_other.countLists(), 3);

			assertTrue(config_other.hasList("list1"));
			assertTrue(config_other.hasList("list2"));
			assertTrue(config_other.hasList("list3"));
			
			items = config_other.getStringItems("list1");
			assertEquals(items.size(), 3);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item1");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item2");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item3");
			assertEquals(item_it.hasNext(), false);
			
			items = config_other.getStringItems("list2");
			assertEquals(items.size(), 1);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item3a");
			assertEquals(item_it.hasNext(), false);
			
			items = config_other.getStringItems("list3");
			assertEquals(items.size(), 3);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item6");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item7");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item8");
			assertEquals(item_it.hasNext(), false);
			
			config_other.removeParameter("paramint");
			config_other.setParameter("paramstring3", "anotherstring");
			config_other.addListItem("list1", "item9");
			config_other.addListItem("list4", "item10");
			config_other.addListItem("list4", "item11");

			assertTrue(config_other.hasList("list4"));

			config_other.storeToPreferences();

			Config config_other2 = new Config();
			config_other2.setParameter("paramstring2", "oncemoreastring");
			config_other2.setPreferencesNode(preferences);
			
			assertEquals(config_other2.countParameters(), 9);
			assertFalse(config_other.hasParameter("paramint"));
			assertNull(config_other.getString("paramint"));
			assertTrue(config_other.hasParameter("paramstring2"));
			assertEquals(config_other2.getString("paramstring2"), "astring2");
			config_other2.setFinalParameter("paramstring2", true);
			assertEquals(config_other2.getString("paramstring2"), "oncemoreastring");
			assertTrue(config_other.hasParameter("paramstring3"));
			assertEquals(config_other2.getString("paramstring3"), "anotherstring");
			
			assertEquals(config_other2.countLists(), 4);
			assertTrue(config_other2.hasList("list1"));
			assertTrue(config_other2.hasList("list2"));
			assertTrue(config_other2.hasList("list3"));
			assertTrue(config_other2.hasList("list4"));
			
			items = config_other2.getStringItems("list1");
			assertEquals(items.size(), 4);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item1");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item2");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item3");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item9");
			assertEquals(item_it.hasNext(), false);
			
			items = config_other2.getStringItems("list2");
			assertEquals(items.size(), 2);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item4");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item5");
			assertEquals(item_it.hasNext(), false);
			
			items = config_other.getStringItems("list3");
			assertEquals(items.size(), 3);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item6");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item7");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item8");
			assertEquals(item_it.hasNext(), false);
			
			items = config_other2.getStringItems("list4");
			assertEquals(items.size(), 2);
			item_it = items.iterator();
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item10");
			assertEquals(item_it.hasNext(), true);
			assertEquals(item_it.next(), "item11");
			assertEquals(item_it.hasNext(), false);

			Config config_other3 = new Config();
			config_other3.setPreferencesNode(preferences);
			
			assertEquals(config_other3.countLists(), 4);
			assertTrue(config_other3.hasList("list1"));
			assertTrue(config_other3.hasList("list2"));
			assertTrue(config_other3.hasList("list3"));
			assertTrue(config_other3.hasList("list4"));
			
			items = config_other3.getStringItems("list1");
			assertEquals(items.size(), 4);
			items = config_other3.getStringItems("list2");
			assertEquals(items.size(), 2);
			items = config_other3.getStringItems("list3");
			assertEquals(items.size(), 3);
			items = config_other3.getStringItems("list4");
			assertEquals(items.size(), 2);
			
			config_other3.clearList("list2");
			config_other3.removeList("list3");
			
			assertEquals(config_other3.countLists(), 3);
			assertTrue(config_other3.hasList("list1"));
			assertTrue(config_other3.hasList("list2"));
			assertFalse(config_other3.hasList("list3"));
			assertTrue(config_other3.hasList("list4"));
			items = config_other3.getStringItems("list1");
			assertEquals(items.size(), 4);
			items = config_other3.getStringItems("list2");
			assertNull(items);
			items = config_other3.getStringItems("list4");
			assertEquals(items.size(), 2);

			Config config_other4 = new Config();
			config_other4.setPreferencesNode(preferences);
			
			assertEquals(config_other4.countLists(), 3);
			assertTrue(config_other4.hasList("list1"));
			assertTrue(config_other4.hasList("list2"));
			assertFalse(config_other4.hasList("list3"));
			assertTrue(config_other4.hasList("list4"));
			
			items = config_other4.getStringItems("list1");
			assertEquals(items.size(), 4);
			items = config_other4.getStringItems("list2");
			assertNull(items);
			items = config_other4.getStringItems("list4");
			assertEquals(items.size(), 2);
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				preferences.removeNode();
			}
			catch (BackingStoreException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testClone()
	{
		Config config1 = new Config();
		
		config1.setParameter("paramstring", "astring");
		config1.setFinalParameter("paramstring", true);
		config1.setParameter("parambool", true);
		config1.setParameter("paramchar", 'C');
		config1.setParameter("paramint", 5133);
		config1.setParameter("paramlong", 8736478L);
		config1.setParameter("paramfloat", 545.2546f);
		config1.setParameter("paramdouble", 7863.3434353d);
		config1.addListItem("list1", "item1");
		config1.addListItem("list1", "item2");
		config1.addListItem("list1", "item3");
		config1.addListItem("list2", "item4");
		config1.addListItem("list2", "item5");
		config1.setFinalList("list2", true);
		
		Config config2 = config1.clone();
		
		assertNotSame(config1, config2);
		
		assertEquals(config1.toXml(), config2.toXml());
		
		config2.setFinalParameter("paramstring", false);
		config2.setParameter("paramstring", "astring2");
		config2.setParameter("parambool", false);
		config2.setFinalParameter("parambool", true);
		config2.setParameter("paramchar", 'D');
		config2.setParameter("paramint", 442);
		config2.setParameter("paramlong", 9121L);
		config2.setParameter("paramfloat", 112.87f);
		config2.setParameter("paramdouble", 1313.2232887d);
		config2.setParameter("paramnew", 2223);
		config2.addListItem("list1", "item6");
		config2.setFinalList("list1", true);
		config2.setFinalList("list2", false);
		config2.addListItem("list2", "item7");
		config2.addListItem("list3", "item8");
		
		assertFalse(config1.toXml().equals(config2.toXml()));
		
		assertEquals(config1.countParameters(), 7);
		
		assertTrue(config1.hasParameter("paramstring"));
		assertTrue(config1.hasParameter("parambool"));
		assertTrue(config1.hasParameter("paramchar"));
		assertTrue(config1.hasParameter("paramint"));
		assertTrue(config1.hasParameter("paramlong"));
		assertTrue(config1.hasParameter("paramfloat"));
		assertTrue(config1.hasParameter("paramdouble"));
		assertEquals(config1.getString("paramstring"), "astring");
		assertEquals(config1.getBool("parambool"), true);
		assertEquals(config1.getChar("paramchar"), 'C');
		assertEquals(config1.getInt("paramint"), 5133);
		assertEquals(config1.getLong("paramlong"), 8736478L);
		assertEquals(config1.getFloat("paramfloat"), 545.2546f, 0);
		assertEquals(config1.getDouble("paramdouble"), 7863.3434353d, 0);
		assertTrue(config1.isFinalParameter("paramstring"));
		assertFalse(config1.isFinalParameter("parambool"));
		assertFalse(config1.isFinalParameter("paramchar"));
		assertFalse(config1.isFinalParameter("paramint"));
		assertFalse(config1.isFinalParameter("paramlong"));
		assertFalse(config1.isFinalParameter("paramfloat"));
		assertFalse(config1.isFinalParameter("paramdouble"));
		
		assertEquals(config1.countLists(), 2);
		
		assertTrue(config1.hasList("list1"));
		assertTrue(config1.hasList("list2"));
		assertFalse(config1.isFinalList("list1"));
		assertTrue(config1.isFinalList("list2"));

		Collection<String>	items = null;
		Iterator<String>	item_it = null;
		
		items = config1.getStringItems("list1");
		assertEquals(items.size(), 3);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item1");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item2");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item3");
		assertEquals(item_it.hasNext(), false);
		
		items = config1.getStringItems("list2");
		assertEquals(items.size(), 2);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item4");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item5");
		assertEquals(item_it.hasNext(), false);
		
		assertEquals(config2.countParameters(), 8);
		
		assertTrue(config2.hasParameter("paramstring"));
		assertTrue(config2.hasParameter("parambool"));
		assertTrue(config2.hasParameter("paramchar"));
		assertTrue(config2.hasParameter("paramint"));
		assertTrue(config2.hasParameter("paramlong"));
		assertTrue(config2.hasParameter("paramfloat"));
		assertTrue(config2.hasParameter("paramdouble"));
		assertEquals(config2.getString("paramstring"), "astring2");
		assertEquals(config2.getBool("parambool"), false);
		assertEquals(config2.getChar("paramchar"), 'D');
		assertEquals(config2.getInt("paramint"), 442);
		assertEquals(config2.getLong("paramlong"), 9121L);
		assertEquals(config2.getFloat("paramfloat"), 112.87f, 0);
		assertEquals(config2.getDouble("paramdouble"), 1313.2232887d, 0);
		assertEquals(config2.getInt("paramnew"), 2223);
		assertFalse(config2.isFinalParameter("paramstring"));
		assertTrue(config2.isFinalParameter("parambool"));
		assertFalse(config2.isFinalParameter("paramchar"));
		assertFalse(config2.isFinalParameter("paramint"));
		assertFalse(config2.isFinalParameter("paramlong"));
		assertFalse(config2.isFinalParameter("paramfloat"));
		assertFalse(config2.isFinalParameter("paramdouble"));
		assertFalse(config2.isFinalParameter("paramnew"));
		
		assertEquals(config2.countLists(), 3);
		
		assertTrue(config2.hasList("list1"));
		assertTrue(config2.hasList("list2"));
		assertTrue(config2.hasList("list3"));
		assertTrue(config2.isFinalList("list1"));
		assertFalse(config2.isFinalList("list2"));
		assertFalse(config2.isFinalList("list3"));

		items = config2.getStringItems("list1");
		assertEquals(items.size(), 4);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item1");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item2");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item3");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item6");
		assertEquals(item_it.hasNext(), false);
		
		items = config2.getStringItems("list2");
		assertEquals(items.size(), 3);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item4");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item5");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item7");
		assertEquals(item_it.hasNext(), false);
		
		items = config2.getStringItems("list3");
		assertEquals(items.size(), 1);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item8");
		assertEquals(item_it.hasNext(), false);
	}
}
	
class SerializableClass implements Serializable
{
	private int		mNumber = -1;
	private String	mString = null;
	
	public SerializableClass(int number, String string)
	{
		mNumber = number;
		mString = string;
	}
	
	public void setNumber(int number)
	{
		mNumber = number;
	}
	
	public int getNumber()
	{
		return mNumber;
	}
	
	public void setString(String string)
	{
		mString = string;
	}
	
	public String getString()
	{
		return mString;
	}
	
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		
		if (null == other)
		{
			return false;
		}
		
		if (!(other instanceof SerializableClass))
		{
			return false;
		}
		
		SerializableClass other_datalink = (SerializableClass)other;
		if (!other_datalink.getString().equals(getString()))
		{
			return false;
		}
		if (other_datalink.getNumber() != getNumber())
		{
			return false;
		}
		
		return true;
	}
}

