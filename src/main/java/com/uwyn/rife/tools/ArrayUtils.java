/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

/**
 * General purpose class containing common array manipulation methods.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.0
 */

import com.uwyn.rife.datastructures.EnumClass;

import java.util.logging.Logger;

public class ArrayUtils
{
    public static ArrayType getArrayType(Object object)
    {
        String classname = object.getClass().getName();

        // check if it's an array
        if ('[' == classname.charAt(0))
        {
            for (int position = 1; position < classname.length(); position++)
            {
                if ('[' == classname.charAt(position))
                {
                    continue;
                }

                switch (classname.charAt(position))
                {
                    case 'L': return ArrayType.OBJECT_ARRAY;
                    case 'Z': return ArrayType.BOOLEAN_ARRAY;
                    case 'B': return ArrayType.BYTE_ARRAY;
                    case 'C': return ArrayType.CHAR_ARRAY;
                    case 'S': return ArrayType.SHORT_ARRAY;
                    case 'I': return ArrayType.INT_ARRAY;
                    case 'J': return ArrayType.LONG_ARRAY;
                    case 'F': return ArrayType.FLOAT_ARRAY;
                    case 'D': return ArrayType.DOUBLE_ARRAY;
                    default:
                        Logger.getLogger("com.uwyn.rife.tools").severe("Unknown primitive array class: " + classname);
                        return null;
                }
            }
            return null;
        }

        return ArrayType.NO_ARRAY;
    }

    public static String[] createStringArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }
// TODO
//	/**
//	 * Convert an <code>Object</code> to a textual representation in a
//	 * <code>String</code> array.
//	 * <p>
//	 * Note that array of type byte[] are explicitely not converted since that
//	 * would result in many binary data to create OutOfMemoryError exceptions.
//	 *
//	 * @param source The <code>Object</code> to convert.
//	 * @param constrainedProperty The <code>ConstrainedProperty</code> that gives more information about the source, can be <code>null</code>.
//	 *
//	 * @return The resulting <code>String</code> array; or
//	 * <p>
//	 * <code>null</code> if <code>source</code> is <code>null</code>.
//	 *
//	 * @since 1.0
//	 */
//    public static String[] createStringArray(Object source, ConstrainedProperty constrainedProperty)
//	{
//		if (null == source)
//		{
//			return null;
//		}
//
//		String[] result = null;
//
//		ArrayType type = getArrayType(source);
//
//		if (type == ArrayType.NO_ARRAY)
//		{
//			result = new String[] {BeanUtils.formatPropertyValue(source, constrainedProperty)};
//		}
//		else if (type == ArrayType.BYTE_ARRAY)
//		{
//			// explicitely don't convert byte arrays since they are often used
//			// to store binary data and converting them to a string array
//			// would result in an OutOfMemoryError exception easily
//			result = null;
//		}
//		else if (type == ArrayType.OBJECT_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((Object[])source);
//		}
//		else if (type == ArrayType.BOOLEAN_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((boolean[])source);
//		}
//		else if (type == ArrayType.CHAR_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((char[])source);
//		}
//		else if (type == ArrayType.SHORT_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((short[])source);
//		}
//		else if (type == ArrayType.INT_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((int[])source);
//		}
//		else if (type == ArrayType.LONG_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((long[])source);
//		}
//		else if (type == ArrayType.FLOAT_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((float[])source);
//		}
//		else if (type == ArrayType.DOUBLE_ARRAY)
//		{
//			result = ArrayUtils.createStringArray((double[])source);
//		}
//
//		return result;
//    }

    public static String[] createStringArray(boolean[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static String[] createStringArray(byte[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static String[] createStringArray(char[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static String[] createStringArray(short[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static String[] createStringArray(int[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static String[] createStringArray(long[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static String[] createStringArray(float[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static String[] createStringArray(double[] array)
    {
        if (null == array)
        {
            return null;
        }

        String[] new_array = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            new_array[i] = String.valueOf(array[i]);
        }

        return new_array;
    }

    public static boolean[] createBooleanArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        boolean[] new_array = new boolean[0];

        boolean converted_boolean;
        for (Object element : array)
        {
            if (element != null)
            {
                converted_boolean = Boolean.valueOf(String.valueOf(element));
                new_array = join(new_array, converted_boolean);
            }
        }

        return new_array;
    }

    public static byte[] createByteArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        byte[] new_array = new byte[0];

        byte converted_byte;
        for (Object element : array)
        {
            try
            {
                if (element != null)
                {
                    converted_byte = Byte.parseByte(String.valueOf(element));
                    new_array = join(new_array, converted_byte);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        return new_array;
    }

    public static char[] createCharArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        char[] new_array = new char[0];

        char converted_char;
        for (Object element : array)
        {
            if (element != null)
            {
                String string_value = String.valueOf(element);
                if (0 == string_value.length() ||
                    string_value.length() > 1)
                {
                    continue;
                }
                converted_char = string_value.charAt(0);
                new_array = join(new_array, converted_char);
            }
        }

        return new_array;
    }

    public static short[] createShortArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        short[] new_array = new short[0];

        short converted_short;
        for (Object element : array)
        {
            try
            {
                if (element != null)
                {
                    converted_short = Short.parseShort(String.valueOf(element));
                    new_array = join(new_array, converted_short);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        return new_array;
    }

    public static int[] createIntArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        int[] new_array = new int[0];

        int converted_int;
        for (Object element : array)
        {
            try
            {
                if (element != null)
                {
                    converted_int = Integer.parseInt(String.valueOf(element));
                    new_array = join(new_array, converted_int);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        return new_array;
    }

    public static long[] createLongArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        long[] new_array = new long[0];

        long converted_long;
        for (Object element : array)
        {
            try
            {
                if (element != null)
                {
                    converted_long = Long.parseLong(String.valueOf(element));
                    new_array = join(new_array, converted_long);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        return new_array;
    }

    public static float[] createFloatArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        float[] new_array = new float[0];

        float converted_float;
        for (Object element : array)
        {
            try
            {
                if (element != null)
                {
                    converted_float = Float.parseFloat(String.valueOf(element));
                    new_array = join(new_array, converted_float);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        return new_array;
    }

    public static double[] createDoubleArray(Object[] array)
    {
        if (null == array)
        {
            return null;
        }

        double[] new_array = new double[0];

        double converted_double;
        for (Object element : array)
        {
            try
            {
                if (element != null)
                {
                    converted_double = Double.parseDouble(String.valueOf(element));
                    new_array = join(new_array, converted_double);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        return new_array;
    }

    public static String[] join(String[] first, String[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        String[] new_array = new String[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static String[] join(String[] first, String second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return new String[]{second};
        }
        if (null == second)
        {
            return first;
        }

        String[] new_array = new String[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static byte[] join(byte[] first, byte[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        byte[] new_array = new byte[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static byte[] join(byte[] first, byte second)
    {
        if (null == first)
        {
            return new byte[]{second};
        }

        byte[] new_array = new byte[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static char[] join(char[] first, char[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        char[] new_array = new char[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static char[] join(char[] first, char second)
    {
        if (null == first)
        {
            return new char[]{second};
        }

        char[] new_array = new char[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static short[] join(short[] first, short[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        short[] new_array = new short[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static short[] join(short[] first, short second)
    {
        if (null == first)
        {
            return new short[]{second};
        }

        short[] new_array = new short[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static int[] join(int[] first, int[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        int[] new_array = new int[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static int[] join(int[] first, int second)
    {
        if (null == first)
        {
            return new int[]{second};
        }

        int[] new_array = new int[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static long[] join(long[] first, long[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        long[] new_array = new long[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static long[] join(long[] first, long second)
    {
        if (null == first)
        {
            return new long[]{second};
        }

        long[] new_array = new long[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static float[] join(float[] first, float[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        float[] new_array = new float[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static float[] join(float[] first, float second)
    {
        if (null == first)
        {
            return new float[]{second};
        }

        float[] new_array = new float[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static double[] join(double[] first, double[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        double[] new_array = new double[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static double[] join(double[] first, double second)
    {
        if (null == first)
        {
            return new double[]{second};
        }

        double[] new_array = new double[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static boolean[] join(boolean[] first, boolean[] second)
    {
        if (null == first &&
            null == second)
        {
            return null;
        }
        if (null == first)
        {
            return second;
        }
        if (null == second)
        {
            return first;
        }

        boolean[] new_array = new boolean[first.length + second.length];

        System.arraycopy(first, 0, new_array, 0, first.length);
        System.arraycopy(second, 0, new_array, first.length, second.length);

        return new_array;
    }

    public static boolean[] join(boolean[] first, boolean second)
    {
        if (null == first)
        {
            return new boolean[]{second};
        }

        boolean[] new_array = new boolean[first.length + 1];

        System.arraycopy(first, 0, new_array, 0, first.length);
        new_array[first.length] = second;

        return new_array;
    }

    public static class ArrayType extends EnumClass<String>
    {
        public static final ArrayType NO_ARRAY = new ArrayType("NO_ARRAY");
        public static final ArrayType OBJECT_ARRAY = new ArrayType("OBJECT_ARRAY");
        public static final ArrayType BYTE_ARRAY = new ArrayType("BYTE_ARRAY");
        public static final ArrayType BOOLEAN_ARRAY = new ArrayType("BOOLEAN_ARRAY");
        public static final ArrayType CHAR_ARRAY = new ArrayType("CHAR_ARRAY");
        public static final ArrayType SHORT_ARRAY = new ArrayType("SHORT_ARRAY");
        public static final ArrayType INT_ARRAY = new ArrayType("INT_ARRAY");
        public static final ArrayType LONG_ARRAY = new ArrayType("LONG_ARRAY");
        public static final ArrayType FLOAT_ARRAY = new ArrayType("FLOAT_ARRAY");
        public static final ArrayType DOUBLE_ARRAY = new ArrayType("DOUBLE_ARRAY");

        ArrayType(String identifier)
        {
            super(identifier);
        }

        public static ArrayType getArrayType(String name)
        {
            return getMember(ArrayType.class, name);
        }
    }
}

