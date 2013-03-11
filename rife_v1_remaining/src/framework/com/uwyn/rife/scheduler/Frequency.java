/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Frequency.java 3957 2008-05-26 07:57:51Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.scheduler.exceptions.FrequencyException;
import com.uwyn.rife.tools.Localization;
import com.uwyn.rife.tools.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

class Frequency
{
	final static private int	MAX_YEAR = 2050;

	final static private byte[]	ALL_MINUTES = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59};
	final static private byte[]	ALL_HOURS = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
	final static private byte[]	ALL_DATES = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
	final static private byte[]	ALL_MONTHS = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
	final static private byte[]	ALL_WEEKDAYS = new byte[] {1, 2, 3, 4, 5, 6, 7};
	final static private byte[]	EMPTY_DATE_OVERFLOW = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

	private String	mFrequency = null;
	
	private byte[]	mMinutes = null;
	private byte[]	mHours = null;
	private byte[]	mDates = null;
	private byte[]	mDatesUnderflow = null;
	private byte[]	mDatesOverflow = null;
	private byte[]	mMonths = null;
	private byte[]	mWeekdays = null;
	
	private boolean	mParsed = false;
	
	Frequency(String frequency)
	throws FrequencyException
	{
		parse(frequency);
	}
	
	long getNextDate(long start)
	throws FrequencyException
	{
		if (start < 0)	throw new IllegalArgumentException("start should be positive");
		
		Calendar calendar = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale());
		calendar.setTimeInMillis(start);
		
		int minute = calendar.get(Calendar.MINUTE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int date = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);

		// got to next valid time
		minute++;
		if (-1 == (minute = getNextValidMinute(minute)) ||
			-1 == mHours[hour] ||
			-1 == mMonths[month-1] ||
			-1 == getDates(month, year)[date-1])
		{
			hour++;
			if (-1 == (hour = getNextValidHour(hour)) ||
				-1 == mMonths[month-1] ||
				-1 == getDates(month, year)[date-1])
			{
				date++;
				hour = getFirstValidHour();
			}
			minute = getFirstValidMinute();
		}

		// got to next valid date
		while (year < MAX_YEAR)
		{
			if (-1 == (date = getNextValidDate(date, month, year)) ||
				-1 == mMonths[month-1])
			{
				month++;
				if (-1 == (month = getNextValidMonth(month)))
				{
					year++;
					month = getFirstValidMonth();
				}
				date = getFirstValidDate(month, year);
				if (-1 == date)
				{
					date = 1;
					continue;
				}
			}
	
			calendar.set(year, month-1, date, hour, minute);
			
			if (year == calendar.get(Calendar.YEAR) &&
				month == calendar.get(Calendar.MONTH)+1)
			{
				int weekday = calendar.get(Calendar.DAY_OF_WEEK)-2;
				if (-1 == weekday)
				{
					weekday = 6;
				}
				
				if (mWeekdays[weekday] != -1)
				{
					return calendar.getTimeInMillis();
				}
			}
			
			date++;
		}
		
		throw new FrequencyException("no valid next date available");
	}
	
	private int getFirstValidMinute()
	{
		return getNextValidMinute(0);
	}
	
	private int getNextValidMinute(int minute)
	{
		assert minute >= 0;
		
		for (int i = minute; i < mMinutes.length; i++)
		{
			if (mMinutes[i] != -1)
			{
				return mMinutes[i];
			}
		}
		
		return -1;
	}
	
	private int getFirstValidHour()
	{
		return getNextValidHour(0);
	}
	
	private int getNextValidHour(int hour)
	{
		assert hour >= 0;

		for (int i = hour; i < mHours.length; i++)
		{
			if (mHours[i] != -1)
			{
				return mHours[i];
			}
		}
		
		return -1;
	}
	
	private byte[] getDates(int month, int year)
	{
		assert month >= 1;
		assert year >= 0;

		Calendar	calendar = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale());
		calendar.set(year, month-1, 1);
		byte		maximum_date = (byte)calendar.getActualMaximum(Calendar.DATE);
		byte[]		dates = null;
		
		// only retain the dates that are valid for this month
		dates = new byte[ALL_DATES.length];
		Arrays.fill(dates, (byte)-1);
		System.arraycopy(mDates, 0, dates, 0, maximum_date);

		if (mDatesUnderflow != null &&
			mDatesOverflow != null)
		{
			// get the maximum date of the previous month
			calendar.roll(Calendar.MONTH, -1);
			byte maximum_date_previous = (byte)calendar.getActualMaximum(Calendar.DATE);
			
			// integrate overflowed dates
			byte end_value = ALL_DATES[ALL_DATES.length-1];
			byte difference = (byte)(end_value-maximum_date_previous);
			
			int start_position = ALL_DATES.length-1;
			int target_position = 0;
			for (int i = start_position; i >= 0; i--)
			{
				if (mDatesUnderflow[i] != 0)
				{
					// handle the possibility where due to the difference,
					// the underflow turns into an overflow
					if (i > maximum_date_previous-1)
					{
						target_position = i-maximum_date_previous;
						if (target_position < mDatesUnderflow[i] &&
							target_position < maximum_date)
						{
							dates[target_position] = ALL_DATES[target_position];
						}
					}
				}
					
				if (mDatesOverflow[i] != 0)
				{
					// handle the overflow of the end of the previous month
					target_position = i+difference;
					if (target_position < mDatesOverflow[i] &&
						target_position < maximum_date)
					{
						dates[target_position] = ALL_DATES[target_position];
					}
				}
			}
		}
		
		return dates;
	}
	
	private int getFirstValidDate(int month, int year)
	{
		return getNextValidDate(1, month, year);
	}
	
	private int getNextValidDate(int date, int month, int year)
	{
		assert date >= 1;
		assert month >= 1;
		assert year >= 0;

		byte[]	dates = getDates(month, year);
		
		for (int i = date-1; i < dates.length; i++)
		{
			if (dates[i] != -1)
			{
				return dates[i];
			}
		}
		
		return -1;
	}
	
	private int getFirstValidMonth()
	{
		return getNextValidMonth(1);
	}
	
	private int getNextValidMonth(int month)
	{
		assert month >= 1;
		
		for (int i = month-1; i < mMonths.length; i++)
		{
			if (mMonths[i] != -1)
			{
				return mMonths[i];
			}
		}
		
		return -1;
	}
	
	boolean isParsed()
	{
		return mParsed;
	}
	
	String getFrequency()
	{
		return mFrequency;
	}
	
	byte[] getMinutes()
	{
		return mMinutes;
	}
	
	byte[] getHours()
	{
		return mHours;
	}
	
	byte[] getDates()
	{
		return mDates;
	}
	
	byte[] getDatesUnderflow()
	{
		return mDatesUnderflow;
	}
	
	byte[] getDatesOverflow()
	{
		return mDatesOverflow;
	}
	
	byte[] getMonths()
	{
		return mMonths;
	}
	
	byte[] getWeekdays()
	{
		return mWeekdays;
	}
	
	void parse(String frequency)
	throws FrequencyException
	{
		if (null == frequency)			throw new IllegalArgumentException("frequency can't be null");
		if (0 == frequency.length())	throw new IllegalArgumentException("frequency can't be empty");

		mFrequency = frequency;
		mParsed = false;
		
		mMinutes = null;
		mHours = null;
		mDates = null;
		mDatesUnderflow = new byte[ALL_DATES.length];
		mDatesOverflow = new byte[ALL_DATES.length];
		mMonths = null;
		mWeekdays = null;
		
		List<String> frequency_parts = StringUtils.split(frequency, " ");
		if (frequency_parts.size() != 5)
		{
			throw new FrequencyException("invalid frequency, should be 5 fields seperated by a space");
		}
		
		String	minutes = frequency_parts.get(0);
		String	hours = frequency_parts.get(1);
		String	dates = frequency_parts.get(2);
		String	months = frequency_parts.get(3);
		String	weekdays = frequency_parts.get(4);
		
		mMinutes = processParts(StringUtils.split(minutes, ","), ALL_MINUTES, false, null, null);
		mHours = processParts(StringUtils.split(hours, ","), ALL_HOURS, false, null, null);
		mDates = processParts(StringUtils.split(dates, ","), ALL_DATES, true, mDatesUnderflow, mDatesOverflow);
		if (Arrays.equals(mDatesUnderflow, EMPTY_DATE_OVERFLOW))
		{
			mDatesUnderflow = null;
		}
		if (Arrays.equals(mDatesOverflow, EMPTY_DATE_OVERFLOW))
		{
			mDatesOverflow = null;
		}
		mMonths = processParts(StringUtils.split(months, ","), ALL_MONTHS, false, null, null);
		mWeekdays = processParts(StringUtils.split(weekdays, ","), ALL_WEEKDAYS, false, null, null);
		
		mParsed = true;
	}
	
	private byte[] processParts(List<String> parts, byte[] allValues, boolean deferOverflowProcessing, byte[] underflowStorage, byte[] overflowStorage)
	throws FrequencyException
	{
		assert parts != null;
		assert parts.size() > 0;
		assert allValues != null;
		assert allValues.length > 0;
		assert !deferOverflowProcessing || (deferOverflowProcessing && underflowStorage != null && overflowStorage != null);
		
		String	part = null;
		byte[]	result_values = null;
		
		// initialize the values to -1, the frequency syntax
		// will enable the specified array positions by copying them
		// from the reference array
		result_values = new byte[allValues.length];
		Arrays.fill(result_values, (byte)-1);
		if (underflowStorage != null)
		{
			Arrays.fill(underflowStorage, (byte)-1);
		}
		if (overflowStorage != null)
		{
			Arrays.fill(overflowStorage, (byte)-1);
		}
		
		byte	begin = allValues[0];
		byte	end = allValues[allValues.length-1];
		
		for (String current_part : parts)
		{
			part = current_part;
			
			// plain wildcard
			if (current_part.equals("*"))
			{
				result_values = allValues;
				return result_values;
			}
			
			try
			{
				int		seperator = -1;
				byte	divider = -1;
				
				// divider
				if ((seperator = current_part.indexOf("/")) != -1)
				{
					divider = Byte.parseByte(current_part.substring(seperator+1));
					current_part = current_part.substring(0, seperator);
				}

				// wildcard
				if (current_part.equals("*"))
				{
					if (-1 == divider)
					{
						throw new FrequencyException("invalid frequency part '"+part+"'");
					}

					for (byte i = 0; i < allValues.length; i += divider)
					{
						result_values[i] = allValues[i];
					}
					continue;
				}
				// range
				else if ((seperator = current_part.indexOf("-")) != -1)
				{
					byte left = Byte.parseByte(current_part.substring(0, seperator));
					byte right = Byte.parseByte(current_part.substring(seperator+1));
					
					if (left < begin ||
						left > end)
					{
						throw new FrequencyException("value out of range '"+left+"'");
					}
					if (right < begin ||
						right > end)
					{
						throw new FrequencyException("value out of range '"+right+"'");
					}
					
					if (left == right)
					{
						if (divider != -1)
						{
							throw new FrequencyException("invalid frequency part '"+part+"'");
						}
						result_values[left-begin] = allValues[left-begin];
						continue;
					}
					
					if (-1 == divider)
					{
						divider = 1;
					}
					
					if (right < left)
					{
						if (deferOverflowProcessing)
						{
							// the overflow processing should be done later
							
							// store the underflow both in the regular fashion and
							// preserve it seperately for later underflow processing
							// since it might bleed into overflow
							while (left <= end)
							{
								result_values[left-begin] = allValues[left-begin];
								// don't store the actual values after the overflow breakpoint
								// but store the value of the rightmost
								// limit of the corresponding range
								if (underflowStorage[left-begin] < right)
								{
									underflowStorage[left-begin] = right;
								}
								left += divider;
							}
							
							left = (byte)(begin+(left-end)-1);
							
							// store the positions at which entries are located
							// the positions contain the value of the rightmost
							// limit of the corresponding range
							// this is needed to be able to calculate the correct
							// transformations later
							while (left <= right)
							{
								// preserve a later right limit
								if (overflowStorage[left-begin] < right)
								{
									overflowStorage[left-begin] = right;
								}
								left += divider;
							}
							continue;
						}
						else
						{
							while (left <= end)
							{
								result_values[left-begin] = allValues[left-begin];
								left += divider;
							}
							
							left = (byte)(begin+(left-end)-1);
						}
					}
					
					while (left <= right)
					{
						result_values[left-begin] = allValues[left-begin];
						left += divider;
					}
					continue;
				}
				// one number
				else
				{
					if (divider != -1)
					{
						throw new FrequencyException("invalid frequency part '"+part+"'");
					}
					
					byte minute = Byte.parseByte(current_part);
					if (minute < begin ||
						minute > end)
					{
						throw new FrequencyException("value out of range '"+minute+"'");
					}
					result_values[minute-begin] = allValues[minute-begin];
				}
			}
			catch (NumberFormatException e)
			{
				throw new FrequencyException("invalid frequency part '"+part+"'", e);
			}
		}
	
		return result_values;
	}
}
