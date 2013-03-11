/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockHeaders.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import java.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

class MockHeaders
{
	private final static String[] DATE_FORMAT_SYNTAXES =
	{
		"EEE, dd MMM yyyy HH:mm:ss zzz",
		"EEE, dd-MMM-yy HH:mm:ss zzz",
		"EEE MMM dd HH:mm:ss yyyy",
		"EEE, dd MMM yyyy HH:mm:ss zzz",
		"EEE, dd-MMM-yy HH:mm:ss zzz",
		"dd MMM yyyy HH:mm:ss",
		"dd-MMM-yy HH:mm:ss",
	};
	
    private final static SimpleDateFormat[]	DATE_FORMATS;
    private final static TimeZone			TIMEZONE_GMT = TimeZone.getTimeZone("GMT");
    private final static ThreadLocal		DATE_PARSED_CACHED = new ThreadLocal();
	private final static String 			SET_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
	static
	{
		TIMEZONE_GMT.setID("GMT");
		DATE_FORMATS = new SimpleDateFormat[DATE_FORMAT_SYNTAXES.length];
		for (int i = 0; i < DATE_FORMATS.length; i++)
		{
			DATE_FORMATS[i] = new SimpleDateFormat(DATE_FORMAT_SYNTAXES[i], Locale.US);
			DATE_FORMATS[i].setTimeZone(TIMEZONE_GMT);
		}
	}
	
	private Map<String, List<String>>	mHeaders;
	private SimpleDateFormat[]			mDateFormats;
	
	public long getDateHeader(String name)
	{
        String header = getHeader(name);
        if (header == null)
		{
			return -1;
		}
		
		if (mDateFormats == null)
		{
			mDateFormats = (SimpleDateFormat[])DATE_PARSED_CACHED.get();
			if (mDateFormats == null)
			{
				mDateFormats = (SimpleDateFormat[])new SimpleDateFormat[DATE_FORMATS.length];
				DATE_PARSED_CACHED.set(mDateFormats);
			}
		}
		
        for (int i = 0; i < mDateFormats.length; i++)
        {
            // clone formatter for thread safety
            if (mDateFormats[i] == null)
			{
				mDateFormats[i] = (SimpleDateFormat)DATE_FORMATS[i].clone();
			}
            
			try
			{
				Date date = (Date)mDateFormats[i].parseObject(header);
                return date.getTime();
			}
			catch (ParseException e)
			{
				// IllegalArgumentException will thrown at the end of the method
			}
        }
		
        if (header.endsWith(" GMT"))
        {
            header = header.substring(0, header.length() - 4);
            for (int i = 0; i < mDateFormats.length; i++)
            {
                try
				{
                    Date date = (Date)mDateFormats[i].parseObject(header);
                    return date.getTime();
                }
				catch (ParseException e)
				{
					// IllegalArgumentException will thrown at the end of the method
				}
            }
        }
		
        throw new IllegalArgumentException(header);
	}
	
	public String getHeader(String name)
	{
		if (null == mHeaders)
		{
			return null;
		}
		
		List<String> headers = mHeaders.get(name);
		if (null == headers ||
			0 == headers.size())
		{
			return null;
		}
		
		return headers.get(0);
	}
	
	public Collection getHeaderNames()
	{
		if (null == mHeaders)
		{
			return Collections.EMPTY_LIST;
		}
		
		return mHeaders.keySet();
	}
	
	public Collection getHeaders(String name)
	{
		if (null == mHeaders)
		{
			return Collections.EMPTY_LIST;
		}
		
		List<String> headers = mHeaders.get(name);
		if (null == headers ||
			0 == headers.size())
		{
			return Collections.EMPTY_LIST;
		}
		
		return headers;
	}
	
	public int getIntHeader(String name)
	{
		String header = getHeader(name);
		if (null == header)
		{
			return -1;
		}
		
		try
		{
			return Integer.parseInt(header);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException(header);
		}
	}
	
	public void addHeader(String name, String value)
	{
		if (null == mHeaders)
		{
			mHeaders = new HashMap<String, List<String>>();
		}
		
		List<String> headers = mHeaders.get(name);
		if (null == headers)
		{
			headers = new ArrayList<String>();
			mHeaders.put(name, headers);
		}
		
		headers.add(value);
	}
	
	public void addDateHeader(String name, long date)
	{
		addHeader(name, formatDate(date));
	}
	
	public void addIntHeader(String name, int integer)
	{
		addHeader(name, String.valueOf(integer));
	}
	
	public boolean containsHeader(String name)
	{
		if (null == mHeaders)
		{
			return false;
		}
		
		return mHeaders.containsKey(name);
	}
	
	public void setDateHeader(String name, long date)
	{
		setHeader(name, formatDate(date));
	}
	
	private String formatDate(long date)
	{
		SimpleDateFormat format = new SimpleDateFormat(SET_DATE_FORMAT);
        HttpCal calendar = new HttpCal();
        calendar.setTimeInMillis(date);
		String formatted_date = format.format(calendar.getTime());
		return formatted_date;
	}
	
	public void setHeader(String name, final String value)
	{
		if (null == mHeaders)
		{
			mHeaders = new HashMap<String, List<String>>();
		}
		
		mHeaders.put(name, new ArrayList<String>() {{ add(value); }});
	}
	
	public void setIntHeader(String name, int value)
	{
		setHeader(name, String.valueOf(value));
	}
	
	public void removeHeader(String name)
	{
		if (null == mHeaders)
		{
			return;
		}
		
		mHeaders.remove(name);
	}
	
    private static class HttpCal extends GregorianCalendar
    {
        HttpCal()
        {
            super(TIMEZONE_GMT);
        }
		
        public void setTimeInMillis(long arg0)
        {
            super.setTimeInMillis(arg0);
        }
		
        public long getTimeInMillis()
        {
            return super.getTimeInMillis();
        }
    }
}
