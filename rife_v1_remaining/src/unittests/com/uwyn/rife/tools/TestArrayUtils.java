/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestArrayUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.tools.ArrayUtils;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import junit.framework.TestCase;

public class TestArrayUtils extends TestCase
{
	public TestArrayUtils(String name)
	{
		super(name);
	}

	public void testGetArrayType()
	{
		assertEquals(ArrayUtils.ArrayType.NO_ARRAY, ArrayUtils.getArrayType(new Object()));
		
		assertEquals(ArrayUtils.ArrayType.BOOLEAN_ARRAY, ArrayUtils.getArrayType(new boolean[1]));
		assertEquals(ArrayUtils.ArrayType.BYTE_ARRAY, ArrayUtils.getArrayType(new byte[1]));
		assertEquals(ArrayUtils.ArrayType.SHORT_ARRAY, ArrayUtils.getArrayType(new short[1]));
		assertEquals(ArrayUtils.ArrayType.CHAR_ARRAY, ArrayUtils.getArrayType(new char[1]));
		assertEquals(ArrayUtils.ArrayType.INT_ARRAY, ArrayUtils.getArrayType(new int[1]));
		assertEquals(ArrayUtils.ArrayType.LONG_ARRAY, ArrayUtils.getArrayType(new long[1]));
		assertEquals(ArrayUtils.ArrayType.FLOAT_ARRAY, ArrayUtils.getArrayType(new float[1]));
		assertEquals(ArrayUtils.ArrayType.DOUBLE_ARRAY, ArrayUtils.getArrayType(new double[1]));
		assertEquals(ArrayUtils.ArrayType.OBJECT_ARRAY, ArrayUtils.getArrayType(new Object[1]));
		
		assertEquals(ArrayUtils.ArrayType.BOOLEAN_ARRAY, ArrayUtils.getArrayType(new boolean[1][1]));
		assertEquals(ArrayUtils.ArrayType.BYTE_ARRAY, ArrayUtils.getArrayType(new byte[1][1]));
		assertEquals(ArrayUtils.ArrayType.SHORT_ARRAY, ArrayUtils.getArrayType(new short[1][1]));
		assertEquals(ArrayUtils.ArrayType.CHAR_ARRAY, ArrayUtils.getArrayType(new char[1][1]));
		assertEquals(ArrayUtils.ArrayType.INT_ARRAY, ArrayUtils.getArrayType(new int[1][1]));
		assertEquals(ArrayUtils.ArrayType.LONG_ARRAY, ArrayUtils.getArrayType(new long[1][1]));
		assertEquals(ArrayUtils.ArrayType.FLOAT_ARRAY, ArrayUtils.getArrayType(new float[1][1]));
		assertEquals(ArrayUtils.ArrayType.DOUBLE_ARRAY, ArrayUtils.getArrayType(new double[1][1]));
		assertEquals(ArrayUtils.ArrayType.OBJECT_ARRAY, ArrayUtils.getArrayType(new Object[1][1]));
	}

	public void testCreateStringArray()
	{
		assertNull(ArrayUtils.createStringArray((Object)null, null));
		String[] converted = null;
		
		converted = ArrayUtils.createStringArray("just a test", null);
		assertEquals(1, converted.length);
		assertEquals("just a test", converted[0]);

		String[]	source_string = new String[] {"9", "kojk", "4", "3", "ok", "6.0", "8"};
		converted = ArrayUtils.createStringArray((Object)source_string, null);
		assertEquals(source_string.length, converted.length);
		assertEquals(source_string[0], converted[0]);
		assertEquals(source_string[1], converted[1]);
		assertEquals(source_string[2], converted[2]);
		assertEquals(source_string[3], converted[3]);
		assertEquals(source_string[4], converted[4]);
		assertEquals(source_string[5], converted[5]);
		assertEquals(source_string[6], converted[6]);

		boolean[]	source_boolean = new boolean[] {false, false, true, false, true};
		converted = ArrayUtils.createStringArray((Object)source_boolean, null);
		assertEquals(source_boolean.length, converted.length);
		assertEquals(String.valueOf(source_boolean[0]), converted[0]);
		assertEquals(String.valueOf(source_boolean[1]), converted[1]);
		assertEquals(String.valueOf(source_boolean[2]), converted[2]);
		assertEquals(String.valueOf(source_boolean[3]), converted[3]);
		assertEquals(String.valueOf(source_boolean[4]), converted[4]);

		byte[]		source_byte = new byte[] {9, 4, 3, 6, 8};
		converted = ArrayUtils.createStringArray((Object)source_byte, null);
		assertNull(converted);

		char[]		source_char = new char[] {'w','o','r', 'k' ,'s'};
		converted = ArrayUtils.createStringArray((Object)source_char, null);
		assertEquals(source_char.length, converted.length);
		assertEquals(String.valueOf(source_char[0]), converted[0]);
		assertEquals(String.valueOf(source_char[1]), converted[1]);
		assertEquals(String.valueOf(source_char[2]), converted[2]);
		assertEquals(String.valueOf(source_char[3]), converted[3]);
		assertEquals(String.valueOf(source_char[4]), converted[4]);

		short[]		source_short = new short[] {84, 23, 43, 12, 5};
		converted = ArrayUtils.createStringArray((Object)source_short, null);
		assertEquals(source_short.length, converted.length);
		assertEquals(String.valueOf(source_short[0]), converted[0]);
		assertEquals(String.valueOf(source_short[1]), converted[1]);
		assertEquals(String.valueOf(source_short[2]), converted[2]);
		assertEquals(String.valueOf(source_short[3]), converted[3]);
		assertEquals(String.valueOf(source_short[4]), converted[4]);
		
		int[]		source_int = new int[] {9834, 454, 2355, 2398, 4834};
		converted = ArrayUtils.createStringArray((Object)source_int, null);
		assertEquals(source_int.length, converted.length);
		assertEquals(String.valueOf(source_int[0]), converted[0]);
		assertEquals(String.valueOf(source_int[1]), converted[1]);
		assertEquals(String.valueOf(source_int[2]), converted[2]);
		assertEquals(String.valueOf(source_int[3]), converted[3]);
		assertEquals(String.valueOf(source_int[4]), converted[4]);

		long[]		source_long = new long[] {59035, 90465, 723479, 47543, 987543};
		converted = ArrayUtils.createStringArray((Object)source_long, null);
		assertEquals(source_long.length, converted.length);
		assertEquals(String.valueOf(source_long[0]), converted[0]);
		assertEquals(String.valueOf(source_long[1]), converted[1]);
		assertEquals(String.valueOf(source_long[2]), converted[2]);
		assertEquals(String.valueOf(source_long[3]), converted[3]);
		assertEquals(String.valueOf(source_long[4]), converted[4]);

		float[]		source_float = new float[] {228.02f, 8734.3f, 8634.2f, 34321.9f, 3478.2f};
		converted = ArrayUtils.createStringArray((Object)source_float, null);
		assertEquals(source_float.length, converted.length);
		assertEquals(String.valueOf(source_float[0]), converted[0]);
		assertEquals(String.valueOf(source_float[1]), converted[1]);
		assertEquals(String.valueOf(source_float[2]), converted[2]);
		assertEquals(String.valueOf(source_float[3]), converted[3]);
		assertEquals(String.valueOf(source_float[4]), converted[4]);

		double[]	source_double = new double[] {987634.3434d, 653928.434d, 394374.34387d, 3847764332.3434d, 3434d};
		converted = ArrayUtils.createStringArray((Object)source_double, null);
		assertEquals(source_double.length, converted.length);
		assertEquals(String.valueOf(source_double[0]), converted[0]);
		assertEquals(String.valueOf(source_double[1]), converted[1]);
		assertEquals(String.valueOf(source_double[2]), converted[2]);
		assertEquals(String.valueOf(source_double[3]), converted[3]);
		assertEquals(String.valueOf(source_double[4]), converted[4]);
	}

	public void testCreateStringArrayObject()
	{
		assertNull(ArrayUtils.createStringArray((Object[])null));
		
		String[]	source = new String[] {"9", "kojk", "4", "3", "ok", "6.0", "8"};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(source[0], converted[0]);
		assertEquals(source[1], converted[1]);
		assertEquals(source[2], converted[2]);
		assertEquals(source[3], converted[3]);
		assertEquals(source[4], converted[4]);
		assertEquals(source[5], converted[5]);
		assertEquals(source[6], converted[6]);
	}
	
	public void testCreateStringArrayDate()
	{
		assertNull(ArrayUtils.createStringArray((Date)null, null));
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		cal.set(2005, 7, 18, 9, 27, 13);
		cal.set(Calendar.MILLISECOND, 552);
		String[] converted = ArrayUtils.createStringArray(cal.getTime(), null);
		assertEquals(1, converted.length);
		assertEquals("20050818092713552+0200", converted[0]);
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss", Locale.ENGLISH);
		sf.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		converted = ArrayUtils.createStringArray(cal.getTime(), new ConstrainedProperty("someProperty").format(sf));
		assertEquals(1, converted.length);
		assertEquals("2005.08.18 AD at 09:27:13", converted[0]);
	}
	
	public void testCreateStringArrayDoubleFormat()
	{
		assertNull(ArrayUtils.createStringArray((Double)null, null));
		
		String[] converted = ArrayUtils.createStringArray(6782.349876675, new ConstrainedProperty("someProperty").format(NumberFormat.getCurrencyInstance(Locale.US)));
		assertEquals(1, converted.length);
		assertEquals("$6,782.35", converted[0]);
	}
	
	public void testCreateStringArrayBoolean()
	{
		assertNull(ArrayUtils.createStringArray((boolean[])null));

		boolean[]	source = new boolean[] {false, false, true, false, true};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateStringArrayByte()
	{
		assertNull(ArrayUtils.createStringArray((byte[])null));

		byte[]		source = new byte[] {9, 4, 3, 6, 8};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateStringArrayChar()
	{
		assertNull(ArrayUtils.createStringArray((char[])null));

		char[]		source = new char[] {'w','o','r', 'k' ,'s'};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateStringArrayShort()
	{
		assertNull(ArrayUtils.createStringArray((short[])null));

		short[]		source = new short[] {84, 23, 43, 12, 5};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateStringArrayInt()
	{
		assertNull(ArrayUtils.createStringArray((int[])null));

		int[]		source = new int[] {9834, 454, 2355, 2398, 4834};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateStringArrayLong()
	{
		assertNull(ArrayUtils.createStringArray((long[])null));

		long[]		source = new long[] {59035, 90465, 723479, 47543, 987543};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateStringArrayFloat()
	{
		assertNull(ArrayUtils.createStringArray((float[])null));

		float[]		source = new float[] {228.02f, 8734.3f, 8634.2f, 34321.9f, 3478.2f};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateStringArrayDouble()
	{
		assertNull(ArrayUtils.createStringArray((double[])null));

		double[]	source = new double[] {987634.3434d, 653928.434d, 394374.34387d, 3847764332.3434d, 3434d};
		String[]	converted = ArrayUtils.createStringArray(source);
		assertEquals(source.length, converted.length);
		assertEquals(String.valueOf(source[0]), converted[0]);
		assertEquals(String.valueOf(source[1]), converted[1]);
		assertEquals(String.valueOf(source[2]), converted[2]);
		assertEquals(String.valueOf(source[3]), converted[3]);
		assertEquals(String.valueOf(source[4]), converted[4]);
	}

	public void testCreateBooleanArray()
	{
		assertNull(ArrayUtils.createBooleanArray(null));

		String[]	source = new String[] {"false", "false", null, "true", "false", "true"};
		boolean[]	target = new boolean[] {false, false, true, false, true};
		boolean[]	converted = ArrayUtils.createBooleanArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0]);
		assertEquals(target[1], converted[1]);
		assertEquals(target[2], converted[2]);
		assertEquals(target[3], converted[3]);
		assertEquals(target[4], converted[4]);
	}

	public void testCreateByteArray()
	{
		assertNull(ArrayUtils.createByteArray(null));

		Object[]	source = new Object[] {new Integer(9), "ko", "4", null, new Long(3), "ok", "6", "8"};
		byte[]		target = new byte[] {9, 4, 3, 6, 8};
		byte[]		converted = ArrayUtils.createByteArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0]);
		assertEquals(target[1], converted[1]);
		assertEquals(target[2], converted[2]);
		assertEquals(target[3], converted[3]);
		assertEquals(target[4], converted[4]);
	}

	public void testCreateCharArray()
	{
		assertNull(ArrayUtils.createCharArray(null));

		Object[]	source = new Object[] {new Character('w'), "loo", null, "ko", "o", "r", "k" ,"s", new StringBuffer("oook")};
		char[]		target = new char[] {'w','o','r', 'k' ,'s'};
		char[]		converted = ArrayUtils.createCharArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0]);
		assertEquals(target[1], converted[1]);
		assertEquals(target[2], converted[2]);
		assertEquals(target[3], converted[3]);
		assertEquals(target[4], converted[4]);
	}

	public void testCreateShortArray()
	{
		assertNull(ArrayUtils.createShortArray(null));

		Object[]	source = new Object[] {"84", "ko", new Byte((byte)23), "43", "ok", null, new Short((short)12), "5"};
		short[]		target = new short[] {84, 23, 43, 12, 5};
		short[]		converted = ArrayUtils.createShortArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0]);
		assertEquals(target[1], converted[1]);
		assertEquals(target[2], converted[2]);
		assertEquals(target[3], converted[3]);
		assertEquals(target[4], converted[4]);
	}

	public void testCreateIntArray()
	{
		assertNull(ArrayUtils.createIntArray(null));

		Object[]	source = new Object[] {"ok", new Integer(9834), null, "454", new StringBuffer("2355"), "ko", "2398", new Long(4834L), "koko"};
		int[]		target = new int[] {9834, 454, 2355, 2398, 4834};
		int[]		converted = ArrayUtils.createIntArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0]);
		assertEquals(target[1], converted[1]);
		assertEquals(target[2], converted[2]);
		assertEquals(target[3], converted[3]);
		assertEquals(target[4], converted[4]);
	}

	public void testCreateLongArray()
	{
		assertNull(ArrayUtils.createLongArray(null));

		Object[]	source = new Object[] {new Integer(59035), "90465", "ok", "723479", null, "47543", "ko", new Integer(987543)};
		long[]		target = new long[] {59035, 90465, 723479, 47543, 987543};
		long[]		converted = ArrayUtils.createLongArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0]);
		assertEquals(target[1], converted[1]);
		assertEquals(target[2], converted[2]);
		assertEquals(target[3], converted[3]);
		assertEquals(target[4], converted[4]);
	}

	public void testCreateFloatArray()
	{
		assertNull(ArrayUtils.createFloatArray(null));

		Object[]	source = new Object[] {"228.02", new Float(8734.3f), "lokoko", "8634.2", null, "kokiro", new Double(34321.9d), "3478.2"};
		float[]		target = new float[] {228.02f, 8734.3f, 8634.2f, 34321.9f, 3478.2f};
		float[]		converted = ArrayUtils.createFloatArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0], 0);
		assertEquals(target[1], converted[1], 0);
		assertEquals(target[2], converted[2], 0);
		assertEquals(target[3], converted[3], 0);
		assertEquals(target[4], converted[4], 0);
	}

	public void testCreateDoubleArray()
	{
		assertNull(ArrayUtils.createDoubleArray(null));

		Object[]	source = new Object[] {new Double(987634.3434d), null, "653928.434", "oooook", new Double(394374.34387d), "3847764332.3434", "koooko", new Integer(3434)};
		double[]	target = new double[] {987634.3434d, 653928.434d, 394374.34387d, 3847764332.3434d, 3434d};
		double[]	converted = ArrayUtils.createDoubleArray(source);
		assertEquals(target.length, converted.length);
		assertEquals(target[0], converted[0], 0);
		assertEquals(target[1], converted[1], 0);
		assertEquals(target[2], converted[2], 0);
		assertEquals(target[3], converted[3], 0);
		assertEquals(target[4], converted[4], 0);
	}
	
	public void testJoinString()
	{
		String[]	first = new String[] {"lkjhkjh", "uhggh", "kgyugioh", "kjhgkhjh", "phhgg"};
		String[]	second = new String[] {"ihhjf", "hhjgvgjfc", "oighiufhuf", "uiguhgi", "iuggiug"};
		
		assertNull(ArrayUtils.join((String[])null, (String[])null));
		assertSame(first, ArrayUtils.join(first, (String[])null));
		assertSame(second, ArrayUtils.join((String[])null, second));

		String[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second[0]);
		assertEquals(join[6], second[1]);
		assertEquals(join[7], second[2]);
		assertEquals(join[8], second[3]);
		assertEquals(join[9], second[4]);
	}

	public void testJoinStringSingle()
	{
		String[]	first = new String[] {"lkjhkjh", "uhggh", "kgyugioh", "kjhgkhjh", "phhgg"};
		String		second = "ihhjf";
		
		assertNull(ArrayUtils.join((String[])null, (String)null));
		assertSame(first, ArrayUtils.join(first, (String)null));
		assertEquals(1, ArrayUtils.join((String[])null, second).length);
		assertEquals(second, ArrayUtils.join((String[])null, second)[0]);

		String[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second);
	}

	public void testJoinByte()
	{
		byte[]	first = new byte[] {2, 5, 5, 4, 6};
		byte[]	second = new byte[] {9, 4, 3, 6, 8};
		
		assertNull(ArrayUtils.join((byte[])null, (byte[])null));
		assertSame(first, ArrayUtils.join(first, (byte[])null));
		assertSame(second, ArrayUtils.join((byte[])null, second));

		byte[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second[0]);
		assertEquals(join[6], second[1]);
		assertEquals(join[7], second[2]);
		assertEquals(join[8], second[3]);
		assertEquals(join[9], second[4]);
	}

	public void testJoinByteSingle()
	{
		byte[]	first = new byte[] {2, 5, 5, 4, 6};
		byte	second = 9;
		
		assertEquals(1, ArrayUtils.join((byte[])null, second).length);
		assertEquals(second, ArrayUtils.join((byte[])null, second)[0]);

		byte[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second);
	}

	public void testJoinChar()
	{
		char[]	first = new char[] {'t', 'h', 'i', 's', ' '};
		char[]	second = new char[] {'w','o','r', 'k' ,'s'};
		
		assertNull(ArrayUtils.join((char[])null, (char[])null));
		assertSame(first, ArrayUtils.join(first, (char[])null));
		assertSame(second, ArrayUtils.join((char[])null, second));

		char[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second[0]);
		assertEquals(join[6], second[1]);
		assertEquals(join[7], second[2]);
		assertEquals(join[8], second[3]);
		assertEquals(join[9], second[4]);
	}

	public void testJoinCharSingle()
	{
		char[]	first = new char[] {'t', 'h', 'i', 's', ' '};
		char	second = 'w';
		
		assertEquals(1, ArrayUtils.join((char[])null, second).length);
		assertEquals(second, ArrayUtils.join((char[])null, second)[0]);

		char[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second);
	}

	public void testJoinShort()
	{
		short[]	first = new short[] {8, 3, 54, 23, 54};
		short[]	second = new short[] {84, 23, 43, 12, 5};
		
		assertNull(ArrayUtils.join((short[])null, (short[])null));
		assertSame(first, ArrayUtils.join(first, (short[])null));
		assertSame(second, ArrayUtils.join((short[])null, second));

		short[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second[0]);
		assertEquals(join[6], second[1]);
		assertEquals(join[7], second[2]);
		assertEquals(join[8], second[3]);
		assertEquals(join[9], second[4]);
	}

	public void testJoinShortSingle()
	{
		short[]	first = new short[] {8, 3, 54, 23, 54};
		short	second = 84;
		
		assertEquals(1, ArrayUtils.join((short[])null, second).length);
		assertEquals(second, ArrayUtils.join((short[])null, second)[0]);

		short[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second);
	}

	public void testJoinInt()
	{
		int[]	first = new int[] {834, 3476, 343, 234, 545};
		int[]	second = new int[] {9834, 454, 2355, 2398, 4834};
		
		assertNull(ArrayUtils.join((int[])null, (int[])null));
		assertSame(first, ArrayUtils.join(first, (int[])null));
		assertSame(second, ArrayUtils.join((int[])null, second));

		int[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second[0]);
		assertEquals(join[6], second[1]);
		assertEquals(join[7], second[2]);
		assertEquals(join[8], second[3]);
		assertEquals(join[9], second[4]);
	}

	public void testJoinIntSingle()
	{
		int[]	first = new int[] {834, 3476, 343, 234, 545};
		int		second = 9834;
		
		assertEquals(1, ArrayUtils.join((int[])null, second).length);
		assertEquals(second, ArrayUtils.join((int[])null, second)[0]);

		int[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second);
	}

	public void testJoinLong()
	{
		long[]	first = new long[] {987634, 98785, 54654, 9864, 4697932};
		long[]	second = new long[] {59035, 90465, 723479, 47543, 987543};
		
		assertNull(ArrayUtils.join((long[])null, (long[])null));
		assertSame(first, ArrayUtils.join(first, (long[])null));
		assertSame(second, ArrayUtils.join((long[])null, second));

		long[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second[0]);
		assertEquals(join[6], second[1]);
		assertEquals(join[7], second[2]);
		assertEquals(join[8], second[3]);
		assertEquals(join[9], second[4]);
	}

	public void testJoinLongSingle()
	{
		long[]	first = new long[] {987634, 98785, 54654, 9864, 4697932};
		long	second = 59035;
		
		assertEquals(1, ArrayUtils.join((long[])null, second).length);
		assertEquals(second, ArrayUtils.join((long[])null, second)[0]);

		long[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second);
	}

	public void testJoinFloat()
	{
		float[]	first = new float[] {43.3f, 7489.2f, 7634.98f, 343.8f, 736.9f};
		float[]	second = new float[] {228.02f, 8734.3f, 8634.2f, 34321.9f, 3478.2f};
		
		assertNull(ArrayUtils.join((float[])null, (float[])null));
		assertSame(first, ArrayUtils.join(first, (float[])null));
		assertSame(second, ArrayUtils.join((float[])null, second));

		float[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0], 0);
		assertEquals(join[1], first[1], 0);
		assertEquals(join[2], first[2], 0);
		assertEquals(join[3], first[3], 0);
		assertEquals(join[4], first[4], 0);
		assertEquals(join[5], second[0], 0);
		assertEquals(join[6], second[1], 0);
		assertEquals(join[7], second[2], 0);
		assertEquals(join[8], second[3], 0);
		assertEquals(join[9], second[4], 0);
	}

	public void testJoinFloatSingle()
	{
		float[]	first = new float[] {43.3f, 7489.2f, 7634.98f, 343.8f, 736.9f};
		float	second = 228.02f;
		
		assertEquals(1, ArrayUtils.join((float[])null, second).length);
		assertEquals(second, ArrayUtils.join((float[])null, second)[0]);

		float[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0], 0);
		assertEquals(join[1], first[1], 0);
		assertEquals(join[2], first[2], 0);
		assertEquals(join[3], first[3], 0);
		assertEquals(join[4], first[4], 0);
		assertEquals(join[5], second, 0);
	}

	public void testJoinDouble()
	{
		double[]	first = new double[] {973284.678943d, 8936498736.232d, 78634.9834d, 37467.334d, 986347.234243d};
		double[]	second = new double[] {987634.3434d, 653928.434d, 394374.34387d, 3847764332.3434d, 3434d};
		
		assertNull(ArrayUtils.join((double[])null, (double[])null));
		assertSame(first, ArrayUtils.join(first, (double[])null));
		assertSame(second, ArrayUtils.join((double[])null, second));

		double[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0], 0);
		assertEquals(join[1], first[1], 0);
		assertEquals(join[2], first[2], 0);
		assertEquals(join[3], first[3], 0);
		assertEquals(join[4], first[4], 0);
		assertEquals(join[5], second[0], 0);
		assertEquals(join[6], second[1], 0);
		assertEquals(join[7], second[2], 0);
		assertEquals(join[8], second[3], 0);
		assertEquals(join[9], second[4], 0);
	}

	public void testJoinDoubleSingle()
	{
		double[]	first = new double[] {973284.678943d, 8936498736.232d, 78634.9834d, 37467.334d, 986347.234243d};
		double		second = 987634.3434d;
		
		assertEquals(1, ArrayUtils.join((double[])null, second).length);
		assertEquals(second, ArrayUtils.join((double[])null, second)[0]);

		double[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0], 0);
		assertEquals(join[1], first[1], 0);
		assertEquals(join[2], first[2], 0);
		assertEquals(join[3], first[3], 0);
		assertEquals(join[4], first[4], 0);
		assertEquals(join[5], second, 0);
	}

	public void testJoinBoolean()
	{
		boolean[]	first = new boolean[] {true, false, false, true, true};
		boolean[]	second = new boolean[] {false, false, true, false, true};
		
		assertNull(ArrayUtils.join((boolean[])null, (boolean[])null));
		assertSame(first, ArrayUtils.join(first, (boolean[])null));
		assertSame(second, ArrayUtils.join((boolean[])null, second));

		boolean[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second[0]);
		assertEquals(join[6], second[1]);
		assertEquals(join[7], second[2]);
		assertEquals(join[8], second[3]);
		assertEquals(join[9], second[4]);
	}

	public void testJoinBooleanSingle()
	{
		boolean[]	first = new boolean[] {true, false, false, true, true};
		boolean		second = false;
		
		assertEquals(1, ArrayUtils.join((boolean[])null, second).length);
		assertEquals(second, ArrayUtils.join((boolean[])null, second)[0]);

		boolean[]	join = ArrayUtils.join(first, second);
		assertEquals(join[0], first[0]);
		assertEquals(join[1], first[1]);
		assertEquals(join[2], first[2]);
		assertEquals(join[3], first[3]);
		assertEquals(join[4], first[4]);
		assertEquals(join[5], second);
	}
}
