/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidityChecks.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.tools.ArrayUtils;
import com.uwyn.rife.tools.StringUtils;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ValidityChecks
{
	public static boolean checkNotNull(Object value)
	{
		return null != value;
	}
	
	public static boolean checkNotEmpty(CharSequence value)
	{
		if (null == value)
		{
			return true;
		}

		String value_string = value.toString();
		return 0 != StringUtils.trim(value_string).length();
	}
	
	public static boolean checkNotEmpty(Object value)
	{
		if (null == value)
		{
			return true;
		}

		if (value instanceof CharSequence)
		{
			return checkNotEmpty((String)value);
		}
		else if (value instanceof Character)
		{
			return checkNotEmpty((Character)value);
		}
		else if (value instanceof Byte)
		{
			return checkNotEmpty((Byte)value);
		}
		else if (value instanceof Short)
		{
			return checkNotEmpty((Short)value);
		}
		else if (value instanceof Integer)
		{
			return checkNotEmpty((Integer)value);
		}
		else if (value instanceof Long)
		{
			return checkNotEmpty((Long)value);
		}
		else if (value instanceof Float)
		{
			return checkNotEmpty((Float)value);
		}
		else if (value instanceof Double)
		{
			return checkNotEmpty((Double)value);
		}
		else if (value.getClass().isArray())
		{
			for (int i = 0; i < Array.getLength(value); i++)
			{
				if (!checkNotEmpty(Array.get(value, i)))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static boolean checkNotEmpty(Character value)
	{
		if (null == value)
		{
			return true;
		}
		
		return checkNotEmpty(value.charValue());
	}
	
	public static boolean checkNotEmpty(char value)
	{
		return (char) 0 != value;
	}
	
	public static boolean checkNotEmpty(Byte value)
	{
		if (null == value)
		{
			return true;
		}
		
		return checkNotEmpty(value.byteValue());
	}
	
	public static boolean checkNotEmpty(byte value)
	{
		return (byte) 0 != value;
	}
	
	public static boolean checkNotEmpty(Short value)
	{
		if (null == value)
		{
			return true;
		}
		
		return checkNotEmpty(value.shortValue());
	}
	
	public static boolean checkNotEmpty(short value)
	{
		return (short) 0 != value;
	}
	
	public static boolean checkNotEmpty(Integer value)
	{
		if (null == value)
		{
			return true;
		}
		
		return checkNotEmpty(value.intValue());
	}
	
	public static boolean checkNotEmpty(int value)
	{
		return 0 != value;
	}
	
	public static boolean checkNotEmpty(Long value)
	{
		if (null == value)
		{
			return true;
		}
		
		return checkNotEmpty(value.longValue());
	}
	
	public static boolean checkNotEmpty(long value)
	{
		return 0L != value;
	}
	
	public static boolean checkNotEmpty(Float value)
	{
		if (null == value)
		{
			return true;
		}
		
		return checkNotEmpty(value.floatValue());
	}
	
	public static boolean checkNotEmpty(float value)
	{
		return 0.0f != value;
	}
	
	public static boolean checkNotEmpty(Double value)
	{
		if (null == value)
		{
			return true;
		}
		
		return checkNotEmpty(value.doubleValue());
	}
	
	public static boolean checkNotEmpty(double value)
	{
		return 0.0d != value;
	}
	
	public static boolean checkNotEqual(boolean value, boolean reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(byte value, byte reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(char value, char reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(short value, short reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(int value, int reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(long value, long reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(float value, float reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(double value, double reference)
	{
		return value != reference;
	}
	
	public static boolean checkNotEqual(Object value, Object reference)
	{
		if (null == value ||
			null == reference)
		{
			return true;
		}
		
		if (value.getClass().isArray())
		{
			for (int i = 0; i < Array.getLength(value); i++)
			{
				if (!checkNotEqual(Array.get(value, i), reference))
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return !value.equals(reference);
		}
	}
	
	public static boolean checkEqual(boolean value, boolean reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(byte value, byte reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(char value, char reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(short value, short reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(int value, int reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(long value, long reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(float value, float reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(double value, double reference)
	{
		return value == reference;
	}
	
	public static boolean checkEqual(Object value, Object reference)
	{
		if (null == value ||
			null == reference)
		{
			return true;
		}
		
		if (value.getClass().isArray())
		{
			for (int i = 0; i < Array.getLength(value); i++)
			{
				if (!checkEqual(Array.get(value, i), reference))
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return value.equals(reference);
		}
	}
	
	public static boolean checkLength(byte value, int min, int max)
	{
		return checkLength(String.valueOf(value), min, max);
	}
	
	public static boolean checkLength(char value, int min, int max)
	{
		return checkLength(String.valueOf(value), min, max);
	}
	
	public static boolean checkLength(short value, int min, int max)
	{
		return checkLength(String.valueOf(value), min, max);
	}
	
	public static boolean checkLength(int value, int min, int max)
	{
		return checkLength(String.valueOf(value), min, max);
	}
	
	public static boolean checkLength(long value, int min, int max)
	{
		return checkLength(String.valueOf(value), min, max);
	}
	
	public static boolean checkLength(float value, int min, int max)
	{
		return checkLength(String.valueOf(value), min, max);
	}
	
	public static boolean checkLength(double value, int min, int max)
	{
		return checkLength(String.valueOf(value), min, max);
	}
	
	public static boolean checkLength(CharSequence value, int min, int max)
	{
		if (null == value)
		{
			return true;
		}
		
		String string = value.toString();
		if (min > 0 &&
			string.length() < min)
		{
			return false;
		}

		return !(max >= 0 && string.length() > max);
	}
	
	public static boolean checkRegexp(CharSequence value, String pattern)
	{
		if (null == value ||
			null == pattern ||
			0 == pattern.length())
		{
			return true;
		}
		
		String string = value.toString();
		if (0 == string.length())
		{
			return true;
		}
		
		Pattern regexp;
		try
		{
			regexp = Pattern.compile(pattern);
		}
		catch (PatternSyntaxException e)
		{
			return false;
		}
		
		Matcher match = regexp.matcher(string);
		return match.matches();
	}
	
	public static boolean checkEmail(CharSequence value)
	{
		return checkRegexp(value, "^[a-zA-Z0-9][_\\+\\-\\.\\w]*@[\\w\\.\\-]+[\\w]\\.[a-zA-Z]{2,4}$");
	}
	
	public static boolean checkUrl(CharSequence value)
	{
		if (null == value)
		{
			return true;
		}
		
		String string = value.toString();
		if (0 == string.length())
		{
			return true;
		}

		if (string.startsWith("https://"))
		{
			string = "http" + string.substring(5);
		}
		
		try
		{
			new URL(string);
			
			return true;
		}
		catch (MalformedURLException e)
		{
			return false;
		}
	}
	
	public static boolean checkLaterThanNow(Date value)
	{
		if (null == value)
		{
			return true;
		}
		
		return value.after(new Date());
	}
	
	public static boolean checkLimitedDate(Object value, Date min, Date max)
	{
		if (null == value ||
			!(value instanceof Date))
		{
			return true;
		}
		
		Date date = (Date)value;
		if (min != null &&
			date.before(min))
		{
			return false;
		}

		return !(max != null && date.after(max));
	}
	
	public static boolean checkInList(Object value, String[] list)
	{
		if (null == value ||
			null == list ||
			0 == list.length)
		{
			return true;
		}
		
		String[] strings = ArrayUtils.createStringArray(value, null);
		if (null == strings)
		{
			return false;
		}
		if (0 == strings.length)
		{
			return true;
		}

		String[] sorted = list.clone();
		Arrays.sort(sorted);
	
		for (String string : strings)
		{
			if (0 == string.length())
			{
				continue;
			}
	
			if (Arrays.binarySearch(sorted, string) < 0)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean checkRange(byte value, byte begin, byte end)
	{
		if (value < begin)
		{
			return false;
		}

		return value <= end;
	}
	
	public static boolean checkRange(char value, char begin, char end)
	{
		if (value < begin)
		{
			return false;
		}

		return value <= end;
	}
	
	public static boolean checkRange(short value, short begin, short end)
	{
		if (value < begin)
		{
			return false;
		}

		return value <= end;
	}
	
	public static boolean checkRange(int value, int begin, int end)
	{
		if (value < begin)
		{
			return false;
		}

		return value <= end;
	}
	
	public static boolean checkRange(long value, long begin, long end)
	{
		if (value < begin)
		{
			return false;
		}

		return value <= end;
	}
	
	public static boolean checkRange(float value, float begin, float end)
	{
		if (value < begin)
		{
			return false;
		}

		return value <= end;
	}
	
	public static boolean checkRange(double value, double begin, double end)
	{
		if (value < begin)
		{
			return false;
		}

		return value <= end;
	}
	
	public static boolean checkRange(Object value, Comparable begin, Comparable end)
	{
		if (null == value)
		{
			return true;
		}
		
		if (value.getClass().isArray())
		{
			for (int i = 0; i < Array.getLength(value); i++)
			{
				if (!checkRange(Array.get(value, i), begin, end))
				{
					return false;
				}
			}
			
			return true;
		}
		else if (!(value instanceof Comparable))
		{
			return true;
		}
		
		Comparable comparable = (Comparable)value;
		if (begin != null &&
			comparable.compareTo(begin) < 0)
		{
			return false;
		}

		return !(end != null && comparable.compareTo(end) > 0);
	}
	
	public static boolean checkFormat(Object value, Format format)
	{
		if (null == value ||
			null == format)
		{
			return true;
		}
		
		if (!(value instanceof String))
		{
			return true;
		}
		

		String string = (String)value;
		try
		{
			Object parsed = format.parseObject(string);
			if (null == parsed)
			{
				return false;
			}
			
			return format.format(parsed).equals(string);
			
		}
		catch (ParseException e)
		{
			return false;
		}
	}
}