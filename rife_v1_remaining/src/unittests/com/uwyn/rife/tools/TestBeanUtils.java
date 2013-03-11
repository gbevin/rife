/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestBeanUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.tools.BeanImpl;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.SerializationUtilsErrorException;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

public class TestBeanUtils extends TestCase
{
	public TestBeanUtils(String name)
	{
		super(name);
	}

	private BeanImpl getPopulatedBean()
	{
		BeanImpl bean = new BeanImpl();
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 11, 26, 22, 52, 31);
		cal.set(Calendar.MILLISECOND, 153);
		bean.setPropertyString("thisisastring");
		bean.setPropertyStringbuffer(new StringBuffer("butthisisastringbuffer"));
		bean.setPropertyDate(cal.getTime());
		bean.setPropertyCalendar(cal);
		bean.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
		bean.setPropertyTime(new Time(cal.getTime().getTime()));
		bean.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
		bean.setPropertyChar('g');
		bean.setPropertyBoolean(false);
		bean.setPropertyByte((byte)53);
		bean.setPropertyDouble(84578.42d);
		bean.setPropertyFloat(35523.967f);
		bean.setPropertyInt(978);
		bean.setPropertyLong(87346L);
		bean.setPropertyShort((short)31);
		bean.setPropertyBigDecimal(new BigDecimal("8347365990.387437894678"));
		
		return bean;
	}

	public void testSetUppercaseBeanPropertyIllegalArguments()
	throws BeanUtilsException
	{
		Map<String, PropertyDescriptor> bean_properties = BeanUtils.getUppercasedBeanProperties(BeanImpl2.class);

		try
		{
			BeanUtils.setUppercasedBeanProperty(null, null, null, bean_properties, new BeanImpl2(), null);
			fail("IllegalArgumentException expected.");
		}
		catch (IllegalArgumentException e)
		{
		}

		try
		{
			BeanUtils.setUppercasedBeanProperty("propertyString", null, null, null, new BeanImpl2(), null);
			fail("IllegalArgumentException expected.");
		}
		catch (IllegalArgumentException e)
		{
		}

		try
		{
			BeanUtils.setUppercasedBeanProperty("propertyString", null, null, bean_properties, null, null);
			fail("IllegalArgumentException expected.");
		}
		catch (IllegalArgumentException e)
		{
		}

		try
		{
			BeanUtils.setUppercasedBeanProperty("propertyString", null, null, bean_properties, new BeanImpl2(), null);
		}
		catch (IllegalArgumentException e)
		{
			fail("IllegalArgumentException not expected.");
		}
	}

	public void testSetUppercaseBeanPropertyNoOpArguments()
    throws BeanUtilsException
    {
        BeanImpl2 bean;
        Map<String, PropertyDescriptor> bean_properties = BeanUtils.getUppercasedBeanProperties(BeanImpl2.class);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyString", null, null, bean_properties, bean, null);
		assertNull(bean.getPropertyString());

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyString", new String[0], null, bean_properties, bean, null);
		assertNull(bean.getPropertyString());

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyString", new String[] {"one", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyString(), "one");
    }

	public void testSetUppercaseBeanPropertyNoSetter()
	throws BeanUtilsException
	{
		BeanImpl2 bean;
		Map<String, PropertyDescriptor> bean_properties = BeanUtils.getUppercasedBeanProperties(BeanImpl2.class);

		bean = new BeanImpl2();
		assertEquals(bean.getPropertyReadonly(), 23L);
		BeanUtils.setUppercasedBeanProperty("propertyReadonly", new String[] {"42131"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyReadonly(), 23L);
	}

	public void testSetUppercaseBeanProperty()
	throws BeanUtilsException, ParseException, SerializationUtilsErrorException
	{
		BeanImpl2 bean;
		Map<String, PropertyDescriptor> bean_properties = BeanUtils.getUppercasedBeanProperties(BeanImpl2.class);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyString", new String[] {"one", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyString(), "one");

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyInt", new String[] {"438", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyInt(), 438);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyChar", new String[] {"E", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyChar(), 'E');

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyBoolean", new String[] {"true", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.isPropertyBoolean(), true);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyByte", new String[] {"27", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyByte(), 27);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyDouble", new String[] {"80756.6287", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyDouble(), 80756.6287d);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyFloat", new String[] {"435.557", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyFloat(), 435.557f);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyLong", new String[] {"122875", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyLong(), 122875);

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyShort", new String[] {"3285", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyShort(), 3285);
		
		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyBigDecimal", new String[] {"983743.343", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyBigDecimal(), new BigDecimal("983743.343"));
		
		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyIntegerObject", new String[] {"438", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyIntegerObject(), new Integer(438));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyCharacterObject", new String[] {"E", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyCharacterObject(), new Character('E'));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyBooleanObject", new String[] {"true", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyBooleanObject(), new Boolean(true));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyByteObject", new String[] {"27", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyByteObject(), new Byte((byte)27));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyDoubleObject", new String[] {"80756.6287", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyDoubleObject(), new Double(80756.6287d));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyFloatObject", new String[] {"435.557", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyFloatObject(), new Float(435.557f));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyLongObject", new String[] {"122875", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyLongObject(), new Long(122875));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyShortObject", new String[] {"3285", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyShortObject(), new Short((short)3285));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyStringbuffer", new String[] {"one1", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyStringbuffer().toString(), "one1");

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyStringbuilder", new String[] {"one2", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyStringbuilder().toString(), "one2");

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyDate", new String[] {"2006-08-04 10:45", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertyDate(), RifeConfig.Tools.getDefaultInputDateFormat().parse("2006-08-04 10:45"));

		bean = new BeanImpl2();
		BeanImpl2.SerializableType serializable = new BeanImpl2.SerializableType(5686, "Testing");
		BeanUtils.setUppercasedBeanProperty("propertySerializableType", new String[] {SerializationUtils.serializeToString(serializable), "two"}, null, bean_properties, bean, new BeanImpl2());
		assertEquals(bean.getPropertySerializableType(), serializable);



		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyStringArray", new String[] {"one", "two"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyStringArray(), new String[] {"one", "two"}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyIntArray", new String[] {"438", "98455", "711"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyIntArray(), new int[] {438, 98455, 711}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyCharArray", new String[] {"E", "a", "x"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyCharArray(), new char[] {'E', 'a', 'x'}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyBooleanArray", new String[] {"true", "0", "t", "1"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyBooleanArray(), new boolean[] {true, false, true, true}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyByteArray", new String[] {"27", "78"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyByteArray(), new byte[] {27, 78}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyDoubleArray", new String[] {"80756.6287", "3214.75", "85796.6237"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyDoubleArray(), new double[] {80756.6287d, 3214.75d, 85796.6237d}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyFloatArray", new String[] {"435.557", "589.5"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyFloatArray(), new float[] {435.557f, 589.5f}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyLongArray", new String[] {"122875", "8526780", "3826589"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyLongArray(), new long[] {122875, 8526780, 3826589}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyShortArray", new String[] {"3285", "58"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyShortArray(), new short[] {3285, 58}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyIntegerObjectArray", new String[] {"438", "7865", "475"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyIntegerObjectArray(), new Integer[] {438, 7865, 475}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyCharacterObjectArray", new String[] {"E", "z"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyCharacterObjectArray(), new Character[] {'E', 'z'}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyBooleanObjectArray", new String[] {"fslse", "1", "true"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyBooleanObjectArray(), new Boolean[] {false, true, true}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyByteObjectArray", new String[] {"27", "78"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyByteObjectArray(), new Byte[] {(byte)27, (byte)78}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyDoubleObjectArray", new String[] {"80756.6287", "5876.14", "3268.57"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyDoubleObjectArray(), new Double[] {80756.6287d, 5876.14d, 3268.57d}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyFloatObjectArray", new String[] {"435.557", "7865.66"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyFloatObjectArray(), new Float[] {435.557f, 7865.66f}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyLongObjectArray", new String[] {"122875", "5687621", "66578"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyLongObjectArray(), new Long[] {122875L, 5687621L, 66578L}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyShortObjectArray", new String[] {"3285", "6588"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyShortObjectArray(), new Short[] {(short)3285, (short)6588}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyBigDecimalArray", new String[] {"32859837434343983.83749837498373434", "65884343.343"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyBigDecimalArray(), new BigDecimal[] {new BigDecimal("32859837434343983.83749837498373434"), new BigDecimal("65884343343E-3")}));
		
		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyStringbufferArray", new String[] {"one1", "two2"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(ArrayUtils.createStringArray(bean.getPropertyStringbufferArray()), new String[] {"one1", "two2"}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyStringbuilderArray", new String[] {"three3", "four4"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(ArrayUtils.createStringArray(bean.getPropertyStringbuilderArray()), new String[] {"three3", "four4"}));

		bean = new BeanImpl2();
		BeanUtils.setUppercasedBeanProperty("propertyDateArray", new String[] {"2006-08-04 10:45", "2006-07-08 11:05"}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertyDateArray(), new Date[] {RifeConfig.Tools.getDefaultInputDateFormat().parse("2006-08-04 10:45"), RifeConfig.Tools.getDefaultInputDateFormat().parse("2006-07-08 11:05")}));

		bean = new BeanImpl2();
		BeanImpl2.SerializableType serializable1 = new BeanImpl2.SerializableType(5682, "AnotherTest");
		BeanImpl2.SerializableType serializable2 = new BeanImpl2.SerializableType(850, "WhatTest");
		BeanUtils.setUppercasedBeanProperty("propertySerializableTypeArray", new String[] {SerializationUtils.serializeToString(serializable1), SerializationUtils.serializeToString(serializable2)}, null, bean_properties, bean, new BeanImpl2());
		assertTrue(Arrays.equals(bean.getPropertySerializableTypeArray(), new BeanImpl2.SerializableType[] {serializable1, serializable2}));
	}

	public void testSetUppercaseBeanPropertyConstrained()
	throws BeanUtilsException, ParseException, SerializationUtilsErrorException
	{
		BeanImpl3 bean;
		Map<String, PropertyDescriptor> bean_properties = BeanUtils.getUppercasedBeanProperties(BeanImpl3.class);

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyDate", new String[] {"custom format 2006-08-04 10:45", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyDate(), RifeConfig.Tools.getDefaultInputDateFormat().parse("2006-08-04 10:45"));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyInt", new String[] {"$438", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyInt(), 438);

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyByte", new String[] {"2,700%", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyByte(), 27);

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyDouble", new String[] {"80,756.6287", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyDouble(), 80756.6287d);

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyFloat", new String[] {"435,557", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyFloat(), 435.557f);

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyLong", new String[] {"122875 €", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyLong(), 122875);

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyShort", new String[] {"¤3285", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyShort(), 3285);

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyBigDecimal", new String[] {"4353344987349830948394893,55709384093", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyBigDecimal(), new BigDecimal("435334498734983094839489355709384093E-11"));
		
		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyIntegerObject", new String[] {"$438", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyIntegerObject(), new Integer(438));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyByteObject", new String[] {"2,700%", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyByteObject(), new Byte((byte)27));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyDoubleObject", new String[] {"80,756.6287", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyDoubleObject(), new Double(80756.6287d));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyFloatObject", new String[] {"435,557", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyFloatObject(), new Float(435.557f));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyLongObject", new String[] {"122875 €", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyLongObject(), new Long(122875));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyShortObject", new String[] {"¤3285", "two"}, null, bean_properties, bean, new BeanImpl3());
		assertEquals(bean.getPropertyShortObject(), new Short((short)3285));



		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyDateArray", new String[] {"custom format 2006-08-04 10:45", "custom format 2006-07-08 11:05"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyDateArray(), new Date[] {RifeConfig.Tools.getDefaultInputDateFormat().parse("2006-08-04 10:45"), RifeConfig.Tools.getDefaultInputDateFormat().parse("2006-07-08 11:05")}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyIntArray", new String[] {"$438", "$98455", "$711"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyIntArray(), new int[] {438, 98455, 711}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyByteArray", new String[] {"2,700%", "7,800%"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyByteArray(), new byte[] {27, 78}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyDoubleArray", new String[] {"80,756.6287", "3,214.75", "85,796.6237"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyDoubleArray(), new double[] {80756.6287d, 3214.75d, 85796.6237d}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyFloatArray", new String[] {"435,557", "589,5"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyFloatArray(), new float[] {435.557f, 589.5f}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyLongArray", new String[] {"122875 €", "8526780 €", "3826589 €"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyLongArray(), new long[] {122875, 8526780, 3826589}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyShortArray", new String[] {"¤3285", "¤58"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyShortArray(), new short[] {3285, 58}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyIntegerObjectArray", new String[] {"$438", "$7865", "$475"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyIntegerObjectArray(), new Integer[] {438, 7865, 475}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyByteObjectArray", new String[] {"2,700%", "7,800%"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyByteObjectArray(), new Byte[] {(byte)27, (byte)78}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyDoubleObjectArray", new String[] {"80,756.6287", "5,876.14", "3,268.57"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyDoubleObjectArray(), new Double[] {80756.6287d, 5876.14d, 3268.57d}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyFloatObjectArray", new String[] {"435,557", "7865,66"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyFloatObjectArray(), new Float[] {435.557f, 7865.66f}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyLongObjectArray", new String[] {"122875 €", "5687621 €", "66578 €"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyLongObjectArray(), new Long[] {122875L, 5687621L, 66578L}));

		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyShortObjectArray", new String[] {"¤3285", "¤6588"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyShortObjectArray(), new Short[] {(short)3285, (short)6588}));
		
		bean = new BeanImpl3();
		BeanUtils.setUppercasedBeanProperty("propertyBigDecimalArray", new String[] {"97687687998978673545669789,0000000000001", "34353"}, null, bean_properties, bean, new BeanImpl3());
		assertTrue(Arrays.equals(bean.getPropertyBigDecimalArray(), new BigDecimal[] {new BigDecimal("976876879989786735456697890000000000001E-13"), new BigDecimal("3.4353E4")}));
	}

	public void testPropertyNamesIllegal()
	{
		try
		{
			assertEquals(0, BeanUtils.getPropertyNames(null, null, null, null).size());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesEmpty()
	{
		try
		{
			assertEquals(0, BeanUtils.getPropertyNames(Object.class, null, null, null).size());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNames()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class, null, null, null);
			assertEquals(property_names.size(), 16);
			assertTrue(property_names.contains("propertyString"));
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyDate"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertySqlDate"));
			assertTrue(property_names.contains("propertyTime"));
			assertTrue(property_names.contains("propertyTimestamp"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyBoolean"));
			assertTrue(property_names.contains("propertyByte"));
			assertTrue(property_names.contains("propertyDouble"));
			assertTrue(property_names.contains("propertyFloat"));
			assertTrue(property_names.contains("propertyInt"));
			assertTrue(property_names.contains("propertyLong"));
			assertTrue(property_names.contains("propertyShort"));
			assertTrue(property_names.contains("propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class, null, null, null);
			assertEquals(property_names.size(), 17);
			assertTrue(property_names.contains("propertyReadonly"));
			assertTrue(property_names.contains("propertyString"));
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyDate"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertySqlDate"));
			assertTrue(property_names.contains("propertyTime"));
			assertTrue(property_names.contains("propertyTimestamp"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyBoolean"));
			assertTrue(property_names.contains("propertyByte"));
			assertTrue(property_names.contains("propertyDouble"));
			assertTrue(property_names.contains("propertyFloat"));
			assertTrue(property_names.contains("propertyInt"));
			assertTrue(property_names.contains("propertyLong"));
			assertTrue(property_names.contains("propertyShort"));
			assertTrue(property_names.contains("propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class, null, null, null);
			assertEquals(property_names.size(), 17);
			assertTrue(property_names.contains("propertyWriteonly"));
			assertTrue(property_names.contains("propertyString"));
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyDate"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertySqlDate"));
			assertTrue(property_names.contains("propertyTime"));
			assertTrue(property_names.contains("propertyTimestamp"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyBoolean"));
			assertTrue(property_names.contains("propertyByte"));
			assertTrue(property_names.contains("propertyDouble"));
			assertTrue(property_names.contains("propertyFloat"));
			assertTrue(property_names.contains("propertyInt"));
			assertTrue(property_names.contains("propertyLong"));
			assertTrue(property_names.contains("propertyShort"));
			assertTrue(property_names.contains("propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesPrefix()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class, null, null, "PREFIX:");
			assertEquals(property_names.size(), 16);
			assertTrue(property_names.contains("PREFIX:propertyString"));
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyDate"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertySqlDate"));
			assertTrue(property_names.contains("PREFIX:propertyTime"));
			assertTrue(property_names.contains("PREFIX:propertyTimestamp"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyBoolean"));
			assertTrue(property_names.contains("PREFIX:propertyByte"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
			assertTrue(property_names.contains("PREFIX:propertyFloat"));
			assertTrue(property_names.contains("PREFIX:propertyInt"));
			assertTrue(property_names.contains("PREFIX:propertyLong"));
			assertTrue(property_names.contains("PREFIX:propertyShort"));
			assertTrue(property_names.contains("PREFIX:propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesPrefixGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class, null, null, "PREFIX:");
			assertEquals(property_names.size(), 17);
			assertTrue(property_names.contains("PREFIX:propertyReadonly"));
			assertTrue(property_names.contains("PREFIX:propertyString"));
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyDate"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertySqlDate"));
			assertTrue(property_names.contains("PREFIX:propertyTime"));
			assertTrue(property_names.contains("PREFIX:propertyTimestamp"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyBoolean"));
			assertTrue(property_names.contains("PREFIX:propertyByte"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
			assertTrue(property_names.contains("PREFIX:propertyFloat"));
			assertTrue(property_names.contains("PREFIX:propertyInt"));
			assertTrue(property_names.contains("PREFIX:propertyLong"));
			assertTrue(property_names.contains("PREFIX:propertyShort"));
			assertTrue(property_names.contains("PREFIX:propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesPrefixSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class, null, null, "PREFIX:");
			assertEquals(property_names.size(), 17);
			assertTrue(property_names.contains("PREFIX:propertyWriteonly"));
			assertTrue(property_names.contains("PREFIX:propertyString"));
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyDate"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertySqlDate"));
			assertTrue(property_names.contains("PREFIX:propertyTime"));
			assertTrue(property_names.contains("PREFIX:propertyTimestamp"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyBoolean"));
			assertTrue(property_names.contains("PREFIX:propertyByte"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
			assertTrue(property_names.contains("PREFIX:propertyFloat"));
			assertTrue(property_names.contains("PREFIX:propertyInt"));
			assertTrue(property_names.contains("PREFIX:propertyLong"));
			assertTrue(property_names.contains("PREFIX:propertyShort"));
			assertTrue(property_names.contains("PREFIX:propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesIncluded()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null,
				null);
			assertEquals(property_names.size(), 7);
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertySqlDate"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyByte"));
			assertTrue(property_names.contains("propertyDouble"));
			assertTrue(property_names.contains("propertyShort"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesIncludedGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null,
				null);
			assertEquals(property_names.size(), 8);
			assertTrue(property_names.contains("propertyReadonly"));
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertySqlDate"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyByte"));
			assertTrue(property_names.contains("propertyDouble"));
			assertTrue(property_names.contains("propertyShort"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesIncludedSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null,
				null);
			assertEquals(property_names.size(), 8);
			assertTrue(property_names.contains("propertyWriteonly"));
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertySqlDate"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyByte"));
			assertTrue(property_names.contains("propertyDouble"));
			assertTrue(property_names.contains("propertyShort"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesIncludedPrefix()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_names.size(), 7);
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertySqlDate"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyByte"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
			assertTrue(property_names.contains("PREFIX:propertyShort"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesIncludedPrefixGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_names.size(), 8);
			assertTrue(property_names.contains("PREFIX:propertyReadonly"));
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertySqlDate"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyByte"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
			assertTrue(property_names.contains("PREFIX:propertyShort"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesIncludedPrefixSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_names.size(), 8);
			assertTrue(property_names.contains("PREFIX:propertyWriteonly"));
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertySqlDate"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyByte"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
			assertTrue(property_names.contains("PREFIX:propertyShort"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesExcluded()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class,
				null,
				new String[] {"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null);
			assertEquals(property_names.size(), 9);
			assertTrue(property_names.contains("propertyString"));
			assertTrue(property_names.contains("propertyDate"));
			assertTrue(property_names.contains("propertyTime"));
			assertTrue(property_names.contains("propertyTimestamp"));
			assertTrue(property_names.contains("propertyBoolean"));
			assertTrue(property_names.contains("propertyFloat"));
			assertTrue(property_names.contains("propertyInt"));
			assertTrue(property_names.contains("propertyLong"));
			assertTrue(property_names.contains("propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesExcludedGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class,
				null,
				new String[] {"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null);
			assertEquals(property_names.size(), 10);
			assertTrue(property_names.contains("propertyReadonly"));
			assertTrue(property_names.contains("propertyString"));
			assertTrue(property_names.contains("propertyDate"));
			assertTrue(property_names.contains("propertyTime"));
			assertTrue(property_names.contains("propertyTimestamp"));
			assertTrue(property_names.contains("propertyBoolean"));
			assertTrue(property_names.contains("propertyFloat"));
			assertTrue(property_names.contains("propertyInt"));
			assertTrue(property_names.contains("propertyLong"));
			assertTrue(property_names.contains("propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesExcludedSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class,
				null,
				new String[] {"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null);
			assertEquals(property_names.size(), 10);
			assertTrue(property_names.contains("propertyWriteonly"));
			assertTrue(property_names.contains("propertyString"));
			assertTrue(property_names.contains("propertyDate"));
			assertTrue(property_names.contains("propertyTime"));
			assertTrue(property_names.contains("propertyTimestamp"));
			assertTrue(property_names.contains("propertyBoolean"));
			assertTrue(property_names.contains("propertyFloat"));
			assertTrue(property_names.contains("propertyInt"));
			assertTrue(property_names.contains("propertyLong"));
			assertTrue(property_names.contains("propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesExcludedPrefix()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(property_names.size(), 9);
			assertTrue(property_names.contains("PREFIX:propertyString"));
			assertTrue(property_names.contains("PREFIX:propertyDate"));
			assertTrue(property_names.contains("PREFIX:propertyTime"));
			assertTrue(property_names.contains("PREFIX:propertyTimestamp"));
			assertTrue(property_names.contains("PREFIX:propertyBoolean"));
			assertTrue(property_names.contains("PREFIX:propertyFloat"));
			assertTrue(property_names.contains("PREFIX:propertyInt"));
			assertTrue(property_names.contains("PREFIX:propertyLong"));
			assertTrue(property_names.contains("PREFIX:propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesExcludedPrefixGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(property_names.size(), 10);
			assertTrue(property_names.contains("PREFIX:propertyReadonly"));
			assertTrue(property_names.contains("PREFIX:propertyString"));
			assertTrue(property_names.contains("PREFIX:propertyDate"));
			assertTrue(property_names.contains("PREFIX:propertyTime"));
			assertTrue(property_names.contains("PREFIX:propertyTimestamp"));
			assertTrue(property_names.contains("PREFIX:propertyBoolean"));
			assertTrue(property_names.contains("PREFIX:propertyFloat"));
			assertTrue(property_names.contains("PREFIX:propertyInt"));
			assertTrue(property_names.contains("PREFIX:propertyLong"));
			assertTrue(property_names.contains("PREFIX:propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesExcludedPrefixSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(property_names.size(), 10);
			assertTrue(property_names.contains("PREFIX:propertyWriteonly"));
			assertTrue(property_names.contains("PREFIX:propertyString"));
			assertTrue(property_names.contains("PREFIX:propertyDate"));
			assertTrue(property_names.contains("PREFIX:propertyTime"));
			assertTrue(property_names.contains("PREFIX:propertyTimestamp"));
			assertTrue(property_names.contains("PREFIX:propertyBoolean"));
			assertTrue(property_names.contains("PREFIX:propertyFloat"));
			assertTrue(property_names.contains("PREFIX:propertyInt"));
			assertTrue(property_names.contains("PREFIX:propertyLong"));
			assertTrue(property_names.contains("PREFIX:propertyBigDecimal"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesFiltered()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				new String[] {"propertySqlDate", "propertyByte", "propertyShort"},
				null);
			assertEquals(property_names.size(), 4);
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyDouble"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesFilteredGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				new String[] {"propertySqlDate", "propertyByte", "propertyShort"},
				null);
			assertEquals(property_names.size(), 5);
			assertTrue(property_names.contains("propertyReadonly"));
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyDouble"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesFilteredSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				new String[] {"propertySqlDate", "propertyByte", "propertyShort"},
				null);
			assertEquals(property_names.size(), 5);
			assertTrue(property_names.contains("propertyWriteonly"));
			assertTrue(property_names.contains("propertyStringbuffer"));
			assertTrue(property_names.contains("propertyCalendar"));
			assertTrue(property_names.contains("propertyChar"));
			assertTrue(property_names.contains("propertyDouble"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesFilteredPrefix()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertySqlDate", "PREFIX:propertyByte", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(property_names.size(), 4);
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesFilteredPrefixGetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertySqlDate", "PREFIX:propertyByte", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(property_names.size(), 5);
			assertTrue(property_names.contains("PREFIX:propertyReadonly"));
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyNamesFilteredPrefixSetters()
	{
		try
		{
			Set<String> property_names = BeanUtils.getPropertyNames(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertySqlDate", "PREFIX:propertyByte", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(property_names.size(), 5);
			assertTrue(property_names.contains("PREFIX:propertyWriteonly"));
			assertTrue(property_names.contains("PREFIX:propertyStringbuffer"));
			assertTrue(property_names.contains("PREFIX:propertyCalendar"));
			assertTrue(property_names.contains("PREFIX:propertyChar"));
			assertTrue(property_names.contains("PREFIX:propertyDouble"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesIllegal()
	{
		try
		{
			assertEquals(0, BeanUtils.countProperties(null, null, null, null));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountProperties()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanImpl.class, null, null, null);
			assertEquals(count, 16);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesGetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class, null, null, null);
			assertEquals(count, 17);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesSetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class, null, null, null);
			assertEquals(count, 17);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesPrefix()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanImpl.class, null, null, "PREFIX:");
			assertEquals(count, 16);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesPrefixGetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class, null, null, "PREFIX:");
			assertEquals(count, 17);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesPrefixSetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class, null, null, "PREFIX:");
			assertEquals(count, 17);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesIncluded()
	{
		try
		{
			assertEquals(7, BeanUtils.countProperties(BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null,
				null));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesIncludedGetters()
	{
		try
		{
			assertEquals(8, BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null,
				null));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesIncludedSetters()
	{
		try
		{
			assertEquals(8, BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null,
				null));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesIncludedPrefix()
	{
		try
		{
			assertEquals(7, BeanUtils.countProperties(BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				null,
				"PREFIX:"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesIncludedPrefixGetters()
	{
		try
		{
			assertEquals(8, BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				null,
				"PREFIX:"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesIncludedPrefixSetters()
	{
		try
		{
			assertEquals(8, BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				null,
				"PREFIX:"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesExcluded()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanImpl.class,
				null,
				new String[] {"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null);
			assertEquals(count, 9);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesExcludedGetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class,
				null,
				new String[] {"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null);
			assertEquals(count, 10);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesExcludedSetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class,
				null,
				new String[] {"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				null);
			assertEquals(count, 10);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesExcludedPrefix()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(count, 9);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesExcludedPrefixGetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(count, 10);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesExcludedPrefixSetters()
	{
		try
		{
			int count = BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				"PREFIX:");
			assertEquals(count, 10);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesFiltered()
	{
		try
		{
			assertEquals(3, BeanUtils.countProperties(BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				new String[] {"propertyStringbuffer", "propertyChar", "propertyByte", "propertyShort"},
				null));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesFilteredGetters()
	{
		try
		{
			assertEquals(4, BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				new String[] {"propertyStringbuffer", "propertyChar", "propertyByte", "propertyShort"},
				null));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesFilteredSetters()
	{
		try
		{
			assertEquals(4, BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyStringbuffer", "propertyCalendar", "propertySqlDate",
					"propertyChar", "propertyByte", "propertyDouble", "propertyShort"},
				new String[] {"propertyStringbuffer", "propertyChar", "propertyByte", "propertyShort"},
				null));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesFilteredPrefix()
	{
		try
		{
			assertEquals(3, BeanUtils.countProperties(BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyShort"},
				"PREFIX:"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesFilteredPrefixGetters()
	{
		try
		{
			assertEquals(4, BeanUtils.countProperties(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyShort"},
				"PREFIX:"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCountPropertiesFilteredPrefixSetters()
	{
		try
		{
			assertEquals(4, BeanUtils.countProperties(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyStringbuffer", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate",
					"PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyDouble", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyStringbuffer", "PREFIX:propertyChar", "PREFIX:propertyByte", "PREFIX:propertyShort"},
				"PREFIX:"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypeIllegal()
	{
		try
		{
			BeanUtils.getPropertyType(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			BeanUtils.getPropertyType(Object.class, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			BeanUtils.getPropertyType(Object.class, "");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyType()
	{
		try
		{
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyString"), String.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyStringbuffer"), StringBuffer.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyDate"), java.util.Date.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyCalendar"), java.util.Calendar.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertySqlDate"), java.sql.Date.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyTime"), java.sql.Time.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyChar"), char.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyBoolean"), boolean.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyByte"), byte.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyDouble"), double.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyFloat"), float.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyInt"), int.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyLong"), long.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyShort"), short.class);
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "propertyBigDecimal"), BigDecimal.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			assertSame(BeanUtils.getPropertyType(BeanImpl.class, "unknown"), String.class);
			fail();
		}
		catch (BeanUtilsException e)
		{
			assertSame(e.getBeanClass(), BeanImpl.class);
		}
	}

	public void testPropertyTypesIllegal()
	{
		try
		{
			assertEquals(0, BeanUtils.getPropertyTypes(null, null, null, null).size());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypes()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class, null, null, null);
			assertEquals(property_types.size(), 16);
			assertTrue(property_types.containsKey("propertyString"));
			assertTrue(property_types.containsKey("propertyStringbuffer"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyCalendar"));
			assertTrue(property_types.containsKey("propertySqlDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyTimestamp"));
			assertTrue(property_types.containsKey("propertyChar"));
			assertTrue(property_types.containsKey("propertyBoolean"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyDouble"));
			assertTrue(property_types.containsKey("propertyFloat"));
			assertTrue(property_types.containsKey("propertyInt"));
			assertTrue(property_types.containsKey("propertyLong"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertTrue(property_types.containsKey("propertyBigDecimal"));
			assertSame(property_types.get("propertyString"), String.class);
			assertSame(property_types.get("propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyCalendar"), java.util.Calendar.class);
			assertSame(property_types.get("propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("propertyChar"), char.class);
			assertSame(property_types.get("propertyBoolean"), boolean.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyDouble"), double.class);
			assertSame(property_types.get("propertyFloat"), float.class);
			assertSame(property_types.get("propertyInt"), int.class);
			assertSame(property_types.get("propertyLong"), long.class);
			assertSame(property_types.get("propertyShort"), short.class);
			assertSame(property_types.get("propertyBigDecimal"), BigDecimal.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class, null, null, null);
			assertEquals(property_types.size(), 17);
			assertTrue(property_types.containsKey("propertyReadonly"));
			assertTrue(property_types.containsKey("propertyString"));
			assertTrue(property_types.containsKey("propertyStringbuffer"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyCalendar"));
			assertTrue(property_types.containsKey("propertySqlDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyTimestamp"));
			assertTrue(property_types.containsKey("propertyChar"));
			assertTrue(property_types.containsKey("propertyBoolean"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyDouble"));
			assertTrue(property_types.containsKey("propertyFloat"));
			assertTrue(property_types.containsKey("propertyInt"));
			assertTrue(property_types.containsKey("propertyLong"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertTrue(property_types.containsKey("propertyBigDecimal"));
			assertSame(property_types.get("propertyReadonly"), int.class);
			assertSame(property_types.get("propertyString"), String.class);
			assertSame(property_types.get("propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyCalendar"), java.util.Calendar.class);
			assertSame(property_types.get("propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("propertyChar"), char.class);
			assertSame(property_types.get("propertyBoolean"), boolean.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyDouble"), double.class);
			assertSame(property_types.get("propertyFloat"), float.class);
			assertSame(property_types.get("propertyInt"), int.class);
			assertSame(property_types.get("propertyLong"), long.class);
			assertSame(property_types.get("propertyShort"), short.class);
			assertSame(property_types.get("propertyBigDecimal"), BigDecimal.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class, null, null, null);
			assertEquals(property_types.size(), 17);
			assertTrue(property_types.containsKey("propertyWriteonly"));
			assertTrue(property_types.containsKey("propertyString"));
			assertTrue(property_types.containsKey("propertyStringbuffer"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyCalendar"));
			assertTrue(property_types.containsKey("propertySqlDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyTimestamp"));
			assertTrue(property_types.containsKey("propertyChar"));
			assertTrue(property_types.containsKey("propertyBoolean"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyDouble"));
			assertTrue(property_types.containsKey("propertyFloat"));
			assertTrue(property_types.containsKey("propertyInt"));
			assertTrue(property_types.containsKey("propertyLong"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertTrue(property_types.containsKey("propertyBigDecimal"));
			assertSame(property_types.get("propertyWriteonly"), long.class);
			assertSame(property_types.get("propertyString"), String.class);
			assertSame(property_types.get("propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyCalendar"), java.util.Calendar.class);
			assertSame(property_types.get("propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("propertyChar"), char.class);
			assertSame(property_types.get("propertyBoolean"), boolean.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyDouble"), double.class);
			assertSame(property_types.get("propertyFloat"), float.class);
			assertSame(property_types.get("propertyInt"), int.class);
			assertSame(property_types.get("propertyLong"), long.class);
			assertSame(property_types.get("propertyShort"), short.class);
			assertSame(property_types.get("propertyBigDecimal"), BigDecimal.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesPrefix()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class, null, null, "PREFIX:");
			assertEquals(property_types.size(), 16);
			assertTrue(property_types.containsKey("PREFIX:propertyString"));
			assertTrue(property_types.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyCalendar"));
			assertTrue(property_types.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_types.containsKey("PREFIX:propertyChar"));
			assertTrue(property_types.containsKey("PREFIX:propertyBoolean"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_types.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_types.containsKey("PREFIX:propertyInt"));
			assertTrue(property_types.containsKey("PREFIX:propertyLong"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertTrue(property_types.containsKey("PREFIX:propertyBigDecimal"));
			assertSame(property_types.get("PREFIX:propertyString"), String.class);
			assertSame(property_types.get("PREFIX:propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyCalendar"), java.util.Calendar.class);
			assertSame(property_types.get("PREFIX:propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("PREFIX:propertyChar"), char.class);
			assertSame(property_types.get("PREFIX:propertyBoolean"), boolean.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyDouble"), double.class);
			assertSame(property_types.get("PREFIX:propertyFloat"), float.class);
			assertSame(property_types.get("PREFIX:propertyInt"), int.class);
			assertSame(property_types.get("PREFIX:propertyLong"), long.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
			assertSame(property_types.get("PREFIX:propertyBigDecimal"), BigDecimal.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesPrefixGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class, null, null, "PREFIX:");
			assertEquals(property_types.size(), 17);
			assertTrue(property_types.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyString"));
			assertTrue(property_types.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyCalendar"));
			assertTrue(property_types.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_types.containsKey("PREFIX:propertyChar"));
			assertTrue(property_types.containsKey("PREFIX:propertyBoolean"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_types.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_types.containsKey("PREFIX:propertyInt"));
			assertTrue(property_types.containsKey("PREFIX:propertyLong"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertTrue(property_types.containsKey("PREFIX:propertyBigDecimal"));
			assertSame(property_types.get("PREFIX:propertyReadonly"), int.class);
			assertSame(property_types.get("PREFIX:propertyString"), String.class);
			assertSame(property_types.get("PREFIX:propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyCalendar"), java.util.Calendar.class);
			assertSame(property_types.get("PREFIX:propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("PREFIX:propertyChar"), char.class);
			assertSame(property_types.get("PREFIX:propertyBoolean"), boolean.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyDouble"), double.class);
			assertSame(property_types.get("PREFIX:propertyFloat"), float.class);
			assertSame(property_types.get("PREFIX:propertyInt"), int.class);
			assertSame(property_types.get("PREFIX:propertyLong"), long.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
			assertSame(property_types.get("PREFIX:propertyBigDecimal"), BigDecimal.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesPrefixSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class, null, null, "PREFIX:");
			assertEquals(property_types.size(), 17);
			assertTrue(property_types.containsKey("PREFIX:propertyWriteonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyString"));
			assertTrue(property_types.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyCalendar"));
			assertTrue(property_types.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_types.containsKey("PREFIX:propertyChar"));
			assertTrue(property_types.containsKey("PREFIX:propertyBoolean"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_types.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_types.containsKey("PREFIX:propertyInt"));
			assertTrue(property_types.containsKey("PREFIX:propertyLong"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertTrue(property_types.containsKey("PREFIX:propertyBigDecimal"));
			assertSame(property_types.get("PREFIX:propertyWriteonly"), long.class);
			assertSame(property_types.get("PREFIX:propertyString"), String.class);
			assertSame(property_types.get("PREFIX:propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyCalendar"), java.util.Calendar.class);
			assertSame(property_types.get("PREFIX:propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("PREFIX:propertyChar"), char.class);
			assertSame(property_types.get("PREFIX:propertyBoolean"), boolean.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyDouble"), double.class);
			assertSame(property_types.get("PREFIX:propertyFloat"), float.class);
			assertSame(property_types.get("PREFIX:propertyInt"), int.class);
			assertSame(property_types.get("PREFIX:propertyLong"), long.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
			assertSame(property_types.get("PREFIX:propertyBigDecimal"), BigDecimal.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesIncluded()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyString", "propertyDate", "propertySqlDate", "propertyTime",
					"propertyByte", "propertyFloat", "propertyShort"},
				null,
				null);
			assertEquals(property_types.size(), 7);
			assertTrue(property_types.containsKey("propertyString"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertySqlDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyFloat"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyString"), String.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyFloat"), float.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesIncludedGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyString", "propertyDate", "propertySqlDate", "propertyTime",
					"propertyByte", "propertyFloat", "propertyShort"},
				null,
				null);
			assertEquals(property_types.size(), 8);
			assertTrue(property_types.containsKey("propertyReadonly"));
			assertTrue(property_types.containsKey("propertyString"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertySqlDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyFloat"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyReadonly"), int.class);
			assertSame(property_types.get("propertyString"), String.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyFloat"), float.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesIncludedSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly",
					"propertyString", "propertyDate", "propertySqlDate", "propertyTime",
					"propertyByte", "propertyFloat", "propertyShort"},
				null,
				null);
			assertEquals(property_types.size(), 8);
			assertTrue(property_types.containsKey("propertyWriteonly"));
			assertTrue(property_types.containsKey("propertyString"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertySqlDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyFloat"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyWriteonly"), long.class);
			assertSame(property_types.get("propertyString"), String.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyFloat"), float.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesIncludedPrefix()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate",
					"PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_types.size(), 7);
			assertTrue(property_types.containsKey("PREFIX:propertyString"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyString"), String.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyFloat"), float.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesIncludedPrefixGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate",
					"PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_types.size(), 8);
			assertTrue(property_types.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyString"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyReadonly"), int.class);
			assertSame(property_types.get("PREFIX:propertyString"), String.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyFloat"), float.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesIncludedPrefixSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly",
					"PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate",
					"PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_types.size(), 8);
			assertTrue(property_types.containsKey("PREFIX:propertyWriteonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyString"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyWriteonly"), long.class);
			assertSame(property_types.get("PREFIX:propertyString"), String.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertySqlDate"), java.sql.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyFloat"), float.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesExcluded()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class,
				null,
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_types.size(), 10);
			assertTrue(property_types.containsKey("propertyStringbuffer"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyTimestamp"));
			assertTrue(property_types.containsKey("propertyChar"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyDouble"));
			assertTrue(property_types.containsKey("propertyInt"));
			assertTrue(property_types.containsKey("propertyLong"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("propertyChar"), char.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyDouble"), double.class);
			assertSame(property_types.get("propertyInt"), int.class);
			assertSame(property_types.get("propertyLong"), long.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesExcludedGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class,
				null,
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_types.size(), 11);
			assertTrue(property_types.containsKey("propertyReadonly"));
			assertTrue(property_types.containsKey("propertyStringbuffer"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyTimestamp"));
			assertTrue(property_types.containsKey("propertyChar"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyDouble"));
			assertTrue(property_types.containsKey("propertyInt"));
			assertTrue(property_types.containsKey("propertyLong"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyReadonly"), int.class);
			assertSame(property_types.get("propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("propertyChar"), char.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyDouble"), double.class);
			assertSame(property_types.get("propertyInt"), int.class);
			assertSame(property_types.get("propertyLong"), long.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesExcludedSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class,
				null,
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_types.size(), 11);
			assertTrue(property_types.containsKey("propertyWriteonly"));
			assertTrue(property_types.containsKey("propertyStringbuffer"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyTimestamp"));
			assertTrue(property_types.containsKey("propertyChar"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyDouble"));
			assertTrue(property_types.containsKey("propertyInt"));
			assertTrue(property_types.containsKey("propertyLong"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyWriteonly"), long.class);
			assertSame(property_types.get("propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("propertyChar"), char.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyDouble"), double.class);
			assertSame(property_types.get("propertyInt"), int.class);
			assertSame(property_types.get("propertyLong"), long.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesExcludedPrefix()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_types.size(), 10);
			assertTrue(property_types.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_types.containsKey("PREFIX:propertyChar"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_types.containsKey("PREFIX:propertyInt"));
			assertTrue(property_types.containsKey("PREFIX:propertyLong"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("PREFIX:propertyChar"), char.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyDouble"), double.class);
			assertSame(property_types.get("PREFIX:propertyInt"), int.class);
			assertSame(property_types.get("PREFIX:propertyLong"), long.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesExcludedPrefixGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_types.size(), 11);
			assertTrue(property_types.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_types.containsKey("PREFIX:propertyChar"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_types.containsKey("PREFIX:propertyInt"));
			assertTrue(property_types.containsKey("PREFIX:propertyLong"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyReadonly"), int.class);
			assertSame(property_types.get("PREFIX:propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("PREFIX:propertyChar"), char.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyDouble"), double.class);
			assertSame(property_types.get("PREFIX:propertyInt"), int.class);
			assertSame(property_types.get("PREFIX:propertyLong"), long.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesExcludedPrefixSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class,
				null,
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_types.size(), 11);
			assertTrue(property_types.containsKey("PREFIX:propertyWriteonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_types.containsKey("PREFIX:propertyChar"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_types.containsKey("PREFIX:propertyInt"));
			assertTrue(property_types.containsKey("PREFIX:propertyLong"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyWriteonly"), long.class);
			assertSame(property_types.get("PREFIX:propertyStringbuffer"), StringBuffer.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyTimestamp"), java.sql.Timestamp.class);
			assertSame(property_types.get("PREFIX:propertyChar"), char.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyDouble"), double.class);
			assertSame(property_types.get("PREFIX:propertyInt"), int.class);
			assertSame(property_types.get("PREFIX:propertyLong"), long.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesFiltered()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_types.size(), 4);
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesFilteredGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_types.size(), 5);
			assertTrue(property_types.containsKey("propertyReadonly"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyReadonly"), int.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesFilteredSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_types.size(), 5);
			assertTrue(property_types.containsKey("propertyWriteonly"));
			assertTrue(property_types.containsKey("propertyDate"));
			assertTrue(property_types.containsKey("propertyTime"));
			assertTrue(property_types.containsKey("propertyByte"));
			assertTrue(property_types.containsKey("propertyShort"));
			assertSame(property_types.get("propertyWriteonly"), long.class);
			assertSame(property_types.get("propertyDate"), java.util.Date.class);
			assertSame(property_types.get("propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("propertyByte"), byte.class);
			assertSame(property_types.get("propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesFilteredPrefix()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_types.size(), 4);
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesFilteredPrefixGetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.GETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_types.size(), 5);
			assertTrue(property_types.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyReadonly"), int.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPropertyTypesFilteredPrefixSetters()
	{
		try
		{
			Map<String, Class> property_types = BeanUtils.getPropertyTypes(BeanUtils.SETTERS, BeanImpl.class,
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_types.size(), 5);
			assertTrue(property_types.containsKey("PREFIX:propertyWriteonly"));
			assertTrue(property_types.containsKey("PREFIX:propertyDate"));
			assertTrue(property_types.containsKey("PREFIX:propertyTime"));
			assertTrue(property_types.containsKey("PREFIX:propertyByte"));
			assertTrue(property_types.containsKey("PREFIX:propertyShort"));
			assertSame(property_types.get("PREFIX:propertyWriteonly"), long.class);
			assertSame(property_types.get("PREFIX:propertyDate"), java.util.Date.class);
			assertSame(property_types.get("PREFIX:propertyTime"), java.sql.Time.class);
			assertSame(property_types.get("PREFIX:propertyByte"), byte.class);
			assertSame(property_types.get("PREFIX:propertyShort"), short.class);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValueIllegal()
	{
		try
		{
			BeanUtils.getPropertyValue(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			BeanUtils.getPropertyValue(Object.class, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			BeanUtils.getPropertyValue(new Object(), null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			BeanUtils.getPropertyValue(new Object(), "");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValue()
	{
		Object bean = getPopulatedBean();
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyString"), "thisisastring");
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyDate"), cal.getTime());
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyCalendar"), cal);
			assertEquals(BeanUtils.getPropertyValue(bean, "propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyChar"), new Character('g'));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyBoolean"), new Boolean(false));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyByte"), new Byte((byte)53));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyDouble"), new Double(84578.42d));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyFloat"), new Float(35523.967f));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyInt"), new Integer(978));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyLong"), new Long(87346L));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyShort"), new Short((short)31));
			assertEquals(BeanUtils.getPropertyValue(bean, "propertyBigDecimal"), new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			BeanUtils.getPropertyValue(bean, "unknown");
			fail();
		}
		catch (BeanUtilsException e)
		{
			assertSame(e.getBeanClass(), bean.getClass());
		}
	}

	public void testSetPropertyValue()
	{
		BeanImpl bean = new BeanImpl();
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			BeanUtils.setPropertyValue(bean, "propertyString", "thisisastring");
			BeanUtils.setPropertyValue(bean, "propertyStringbuffer", new StringBuffer("butthisisastringbuffer"));
			BeanUtils.setPropertyValue(bean, "propertyDate", cal.getTime());
			BeanUtils.setPropertyValue(bean, "propertyCalendar", cal);
			BeanUtils.setPropertyValue(bean, "propertySqlDate", new java.sql.Date(cal.getTime().getTime()));
			BeanUtils.setPropertyValue(bean, "propertyTime", new Time(cal.getTime().getTime()));
			BeanUtils.setPropertyValue(bean, "propertyTimestamp", new Timestamp(cal.getTime().getTime()));
			BeanUtils.setPropertyValue(bean, "propertyChar", new Character('g'));
			BeanUtils.setPropertyValue(bean, "propertyBoolean", new Boolean(false));
			BeanUtils.setPropertyValue(bean, "propertyByte", new Byte((byte)53));
			BeanUtils.setPropertyValue(bean, "propertyDouble", new Double(84578.42d));
			BeanUtils.setPropertyValue(bean, "propertyFloat", new Float(35523.967f));
			BeanUtils.setPropertyValue(bean, "propertyInt", new Integer(978));
			BeanUtils.setPropertyValue(bean, "propertyLong", new Long(87346L));
			BeanUtils.setPropertyValue(bean, "propertyShort", new Short((short)31));
			BeanUtils.setPropertyValue(bean, "propertyBigDecimal", new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		BeanImpl populated = getPopulatedBean();
		assertEquals(bean.getPropertyString(), populated.getPropertyString());
		assertEquals(bean.getPropertyStringbuffer().toString(), populated.getPropertyStringbuffer().toString());
		assertEquals(bean.getPropertyDate(), populated.getPropertyDate());
		assertEquals(bean.getPropertyCalendar(), populated.getPropertyCalendar());
		assertEquals(bean.getPropertySqlDate(), populated.getPropertySqlDate());
		assertEquals(bean.getPropertyTime(), populated.getPropertyTime());
		assertEquals(bean.getPropertyTimestamp(), populated.getPropertyTimestamp());
		assertEquals(bean.getPropertyChar(), populated.getPropertyChar());
		assertEquals(bean.isPropertyBoolean(), populated.isPropertyBoolean());
		assertEquals(bean.getPropertyByte(), populated.getPropertyByte());
		assertEquals(bean.getPropertyDouble(), populated.getPropertyDouble());
		assertEquals(bean.getPropertyFloat(), populated.getPropertyFloat());
		assertEquals(bean.getPropertyInt(), populated.getPropertyInt());
		assertEquals(bean.getPropertyLong(), populated.getPropertyLong());
		assertEquals(bean.getPropertyShort(), populated.getPropertyShort());
		assertEquals(bean.getPropertyBigDecimal(), populated.getPropertyBigDecimal());

		try
		{
			BeanUtils.setPropertyValue(bean, "unknown", "ok");
			fail();
		}
		catch (BeanUtilsException e)
		{
			assertSame(e.getBeanClass(), bean.getClass());
		}
	}

	public void testGetPropertyValuesIllegal()
	{
		try
		{
			assertEquals(0, BeanUtils.getPropertyValues(null, null, null, null).size());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			BeanUtils.getPropertyValues(Object.class, null, null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValues()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(), null, null, null);
			assertEquals(property_values.size(), 16);
			assertTrue(property_values.containsKey("propertyString"));
			assertTrue(property_values.containsKey("propertyStringbuffer"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyCalendar"));
			assertTrue(property_values.containsKey("propertySqlDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyTimestamp"));
			assertTrue(property_values.containsKey("propertyChar"));
			assertTrue(property_values.containsKey("propertyBoolean"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyDouble"));
			assertTrue(property_values.containsKey("propertyFloat"));
			assertTrue(property_values.containsKey("propertyInt"));
			assertTrue(property_values.containsKey("propertyLong"));
			assertTrue(property_values.containsKey("propertyShort"));
			assertTrue(property_values.containsKey("propertyBigDecimal"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyString"), "thisisastring");
			assertEquals(property_values.get("propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyCalendar"), cal);
			assertEquals(property_values.get("propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyChar"), new Character('g'));
			assertEquals(property_values.get("propertyBoolean"), new Boolean(false));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("propertyInt"), new Integer(978));
			assertEquals(property_values.get("propertyLong"), new Long(87346L));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
			assertEquals(property_values.get("propertyBigDecimal"), new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(), null, null, null);
			assertEquals(property_values.size(), 17);
			assertTrue(property_values.containsKey("propertyReadonly"));
			assertTrue(property_values.containsKey("propertyString"));
			assertTrue(property_values.containsKey("propertyStringbuffer"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyCalendar"));
			assertTrue(property_values.containsKey("propertySqlDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyTimestamp"));
			assertTrue(property_values.containsKey("propertyChar"));
			assertTrue(property_values.containsKey("propertyBoolean"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyDouble"));
			assertTrue(property_values.containsKey("propertyFloat"));
			assertTrue(property_values.containsKey("propertyInt"));
			assertTrue(property_values.containsKey("propertyLong"));
			assertTrue(property_values.containsKey("propertyShort"));
			assertTrue(property_values.containsKey("propertyBigDecimal"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyReadonly"), 23);
			assertEquals(property_values.get("propertyString"), "thisisastring");
			assertEquals(property_values.get("propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyCalendar"), cal);
			assertEquals(property_values.get("propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyChar"), new Character('g'));
			assertEquals(property_values.get("propertyBoolean"), new Boolean(false));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("propertyInt"), new Integer(978));
			assertEquals(property_values.get("propertyLong"), new Long(87346L));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
			assertEquals(property_values.get("propertyBigDecimal"), new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(), null, null, null);
			assertEquals(property_values.size(), 16);
			assertTrue(property_values.containsKey("propertyString"));
			assertTrue(property_values.containsKey("propertyStringbuffer"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyCalendar"));
			assertTrue(property_values.containsKey("propertySqlDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyTimestamp"));
			assertTrue(property_values.containsKey("propertyChar"));
			assertTrue(property_values.containsKey("propertyBoolean"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyDouble"));
			assertTrue(property_values.containsKey("propertyFloat"));
			assertTrue(property_values.containsKey("propertyInt"));
			assertTrue(property_values.containsKey("propertyLong"));
			assertTrue(property_values.containsKey("propertyShort"));
			assertTrue(property_values.containsKey("propertyBigDecimal"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyString"), "thisisastring");
			assertEquals(property_values.get("propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyCalendar"), cal);
			assertEquals(property_values.get("propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyChar"), new Character('g'));
			assertEquals(property_values.get("propertyBoolean"), new Boolean(false));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("propertyInt"), new Integer(978));
			assertEquals(property_values.get("propertyLong"), new Long(87346L));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
			assertEquals(property_values.get("propertyBigDecimal"), new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesPrefix()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(), null, null, "PREFIX:");
			assertEquals(property_values.size(), 16);
			assertTrue(property_values.containsKey("PREFIX:propertyString"));
			assertTrue(property_values.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyCalendar"));
			assertTrue(property_values.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_values.containsKey("PREFIX:propertyChar"));
			assertTrue(property_values.containsKey("PREFIX:propertyBoolean"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_values.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_values.containsKey("PREFIX:propertyInt"));
			assertTrue(property_values.containsKey("PREFIX:propertyLong"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));
			assertTrue(property_values.containsKey("PREFIX:propertyBigDecimal"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyString"), "thisisastring");
			assertEquals(property_values.get("PREFIX:propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyCalendar"), cal);
			assertEquals(property_values.get("PREFIX:propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyChar"), new Character('g'));
			assertEquals(property_values.get("PREFIX:propertyBoolean"), new Boolean(false));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("PREFIX:propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("PREFIX:propertyInt"), new Integer(978));
			assertEquals(property_values.get("PREFIX:propertyLong"), new Long(87346L));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
			assertEquals(property_values.get("PREFIX:propertyBigDecimal"), new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesPrefixGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(), null, null, "PREFIX:");
			assertEquals(property_values.size(), 17);
			assertTrue(property_values.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_values.containsKey("PREFIX:propertyString"));
			assertTrue(property_values.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyCalendar"));
			assertTrue(property_values.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_values.containsKey("PREFIX:propertyChar"));
			assertTrue(property_values.containsKey("PREFIX:propertyBoolean"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_values.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_values.containsKey("PREFIX:propertyInt"));
			assertTrue(property_values.containsKey("PREFIX:propertyLong"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));
			assertTrue(property_values.containsKey("PREFIX:propertyBigDecimal"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyReadonly"), 23);
			assertEquals(property_values.get("PREFIX:propertyString"), "thisisastring");
			assertEquals(property_values.get("PREFIX:propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyCalendar"), cal);
			assertEquals(property_values.get("PREFIX:propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyChar"), new Character('g'));
			assertEquals(property_values.get("PREFIX:propertyBoolean"), new Boolean(false));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("PREFIX:propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("PREFIX:propertyInt"), new Integer(978));
			assertEquals(property_values.get("PREFIX:propertyLong"), new Long(87346L));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
			assertEquals(property_values.get("PREFIX:propertyBigDecimal"), new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesPrefixSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(), null, null, "PREFIX:");
			assertEquals(property_values.size(), 16);
			assertTrue(property_values.containsKey("PREFIX:propertyString"));
			assertTrue(property_values.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyCalendar"));
			assertTrue(property_values.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_values.containsKey("PREFIX:propertyChar"));
			assertTrue(property_values.containsKey("PREFIX:propertyBoolean"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_values.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_values.containsKey("PREFIX:propertyInt"));
			assertTrue(property_values.containsKey("PREFIX:propertyLong"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));
			assertTrue(property_values.containsKey("PREFIX:propertyBigDecimal"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyString"), "thisisastring");
			assertEquals(property_values.get("PREFIX:propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyCalendar"), cal);
			assertEquals(property_values.get("PREFIX:propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyChar"), new Character('g'));
			assertEquals(property_values.get("PREFIX:propertyBoolean"), new Boolean(false));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("PREFIX:propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("PREFIX:propertyInt"), new Integer(978));
			assertEquals(property_values.get("PREFIX:propertyLong"), new Long(87346L));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
			assertEquals(property_values.get("PREFIX:propertyBigDecimal"), new BigDecimal("8347365990.387437894678"));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesIncluded()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(),
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				null,
				null);
			assertEquals(property_values.size(), 7);
			assertTrue(property_values.containsKey("propertyString"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertySqlDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyFloat"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyString"), "thisisastring");
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesIncludedGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(),
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				null,
				null);
			assertEquals(property_values.size(), 8);
			assertTrue(property_values.containsKey("propertyReadonly"));
			assertTrue(property_values.containsKey("propertyString"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertySqlDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyFloat"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyReadonly"), 23);
			assertEquals(property_values.get("propertyString"), "thisisastring");
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesIncludedSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(),
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				null,
				null);
			assertEquals(property_values.size(), 7);
			assertTrue(property_values.containsKey("propertyString"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertySqlDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyFloat"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyString"), "thisisastring");
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesIncludedPrefix()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(),
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_values.size(), 7);
			assertTrue(property_values.containsKey("PREFIX:propertyString"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyString"), "thisisastring");
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesIncludedPrefixGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(),
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_values.size(), 8);
			assertTrue(property_values.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_values.containsKey("PREFIX:propertyString"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyReadonly"), 23);
			assertEquals(property_values.get("PREFIX:propertyString"), "thisisastring");
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesIncludedPrefixSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(),
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				null,
				"PREFIX:");
			assertEquals(property_values.size(), 7);
			assertTrue(property_values.containsKey("PREFIX:propertyString"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertySqlDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyFloat"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyString"), "thisisastring");
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertySqlDate"), new java.sql.Date(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyFloat"), new Float(35523.967f));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesExcluded()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(),
				null,
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_values.size(), 10);
			assertTrue(property_values.containsKey("propertyStringbuffer"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyTimestamp"));
			assertTrue(property_values.containsKey("propertyChar"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyDouble"));
			assertTrue(property_values.containsKey("propertyInt"));
			assertTrue(property_values.containsKey("propertyLong"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyChar"), new Character('g'));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("propertyInt"), new Integer(978));
			assertEquals(property_values.get("propertyLong"), new Long(87346L));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesExcludedGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(),
				null,
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_values.size(), 11);
			assertTrue(property_values.containsKey("propertyReadonly"));
			assertTrue(property_values.containsKey("propertyStringbuffer"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyTimestamp"));
			assertTrue(property_values.containsKey("propertyChar"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyDouble"));
			assertTrue(property_values.containsKey("propertyInt"));
			assertTrue(property_values.containsKey("propertyLong"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyReadonly"), 23);
			assertEquals(property_values.get("propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyChar"), new Character('g'));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("propertyInt"), new Integer(978));
			assertEquals(property_values.get("propertyLong"), new Long(87346L));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesExcludedSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(),
				null,
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_values.size(), 10);
			assertTrue(property_values.containsKey("propertyStringbuffer"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyTimestamp"));
			assertTrue(property_values.containsKey("propertyChar"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyDouble"));
			assertTrue(property_values.containsKey("propertyInt"));
			assertTrue(property_values.containsKey("propertyLong"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyChar"), new Character('g'));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("propertyInt"), new Integer(978));
			assertEquals(property_values.get("propertyLong"), new Long(87346L));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesExcludedPrefix()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(),
				null,
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_values.size(), 10);
			assertTrue(property_values.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_values.containsKey("PREFIX:propertyChar"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_values.containsKey("PREFIX:propertyInt"));
			assertTrue(property_values.containsKey("PREFIX:propertyLong"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyChar"), new Character('g'));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("PREFIX:propertyInt"), new Integer(978));
			assertEquals(property_values.get("PREFIX:propertyLong"), new Long(87346L));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesExcludedPrefixGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(),
				null,
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_values.size(), 11);
			assertTrue(property_values.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_values.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_values.containsKey("PREFIX:propertyChar"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_values.containsKey("PREFIX:propertyInt"));
			assertTrue(property_values.containsKey("PREFIX:propertyLong"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyReadonly"), 23);
			assertEquals(property_values.get("PREFIX:propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyChar"), new Character('g'));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("PREFIX:propertyInt"), new Integer(978));
			assertEquals(property_values.get("PREFIX:propertyLong"), new Long(87346L));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesExcludedPrefixSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(),
				null,
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_values.size(), 10);
			assertTrue(property_values.containsKey("PREFIX:propertyStringbuffer"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyTimestamp"));
			assertTrue(property_values.containsKey("PREFIX:propertyChar"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyDouble"));
			assertTrue(property_values.containsKey("PREFIX:propertyInt"));
			assertTrue(property_values.containsKey("PREFIX:propertyLong"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyStringbuffer").toString(), new StringBuffer("butthisisastringbuffer").toString());
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyTimestamp"), new Timestamp(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyChar"), new Character('g'));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyDouble"), new Double(84578.42d));
			assertEquals(property_values.get("PREFIX:propertyInt"), new Integer(978));
			assertEquals(property_values.get("PREFIX:propertyLong"), new Long(87346L));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesFiltered()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(),
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_values.size(), 4);
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesFilteredGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(),
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_values.size(), 5);
			assertTrue(property_values.containsKey("propertyReadonly"));
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyReadonly"), 23);
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesFilteredSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(),
				new String[] {"propertyReadonly", "propertyWriteonly", "propertyString", "propertyDate", "propertySqlDate", "propertyTime", "propertyByte", "propertyFloat", "propertyShort"},
				new String[] {"propertyString", "propertyCalendar", "propertySqlDate", "propertyBoolean", "propertyFloat", "propertyBigDecimal"},
				null);
			assertEquals(property_values.size(), 4);
			assertTrue(property_values.containsKey("propertyDate"));
			assertTrue(property_values.containsKey("propertyTime"));
			assertTrue(property_values.containsKey("propertyByte"));
			assertTrue(property_values.containsKey("propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("propertyDate"), cal.getTime());
			assertEquals(property_values.get("propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesFilteredPrefix()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(getPopulatedBean(),
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_values.size(), 4);
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesFilteredPrefixGetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.GETTERS, getPopulatedBean(),
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_values.size(), 5);
			assertTrue(property_values.containsKey("PREFIX:propertyReadonly"));
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyReadonly"), 23);
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetPropertyValuesFilteredPrefixSetters()
	{
		try
		{
			Map<String, Object> property_values = BeanUtils.getPropertyValues(BeanUtils.SETTERS, getPopulatedBean(),
				new String[] {"PREFIX:propertyReadonly", "PREFIX:propertyWriteonly", "PREFIX:propertyString", "PREFIX:propertyDate", "PREFIX:propertySqlDate", "PREFIX:propertyTime", "PREFIX:propertyByte", "PREFIX:propertyFloat", "PREFIX:propertyShort"},
				new String[] {"PREFIX:propertyString", "PREFIX:propertyCalendar", "PREFIX:propertySqlDate", "PREFIX:propertyBoolean", "PREFIX:propertyFloat", "PREFIX:propertyBigDecimal"},
				"PREFIX:");
			assertEquals(property_values.size(), 4);
			assertTrue(property_values.containsKey("PREFIX:propertyDate"));
			assertTrue(property_values.containsKey("PREFIX:propertyTime"));
			assertTrue(property_values.containsKey("PREFIX:propertyByte"));
			assertTrue(property_values.containsKey("PREFIX:propertyShort"));

			Calendar cal = Calendar.getInstance();
			cal.set(2002, 11, 26, 22, 52, 31);
			cal.set(Calendar.MILLISECOND, 153);
			assertEquals(property_values.get("PREFIX:propertyDate"), cal.getTime());
			assertEquals(property_values.get("PREFIX:propertyTime"), new Time(cal.getTime().getTime()));
			assertEquals(property_values.get("PREFIX:propertyByte"), new Byte((byte)53));
			assertEquals(property_values.get("PREFIX:propertyShort"), new Short((short)31));
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}
