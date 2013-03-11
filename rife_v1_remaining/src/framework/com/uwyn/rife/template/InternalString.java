/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InternalString.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.tools.StringUtils;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;

public class InternalString implements CharSequence
{
	private CharSequence	mStringValue = null;
	
	private transient SoftReference<byte[]>	mBytesValue_US_ASCII = null;
	private transient SoftReference<byte[]>	mBytesValue_ISO_8859_1 = null;
	private transient SoftReference<byte[]>	mBytesValue_UTF_8 = null;
	private transient SoftReference<byte[]>	mBytesValue_UTF_16 = null;
	private transient SoftReference<byte[]>	mBytesValue_UTF_16BE = null;
	private transient SoftReference<byte[]>	mBytesValue_UTF_16LE = null;
	
	public InternalString(String value)
	{
		mStringValue = value;
	}
	
	public InternalString(CharSequence value)
	{
		mStringValue = value;
	}
	
	public String toString()
	{
		return mStringValue.toString();
	}
	
	public byte[] getBytes(String charsetName)
	throws UnsupportedEncodingException
	{
		byte[] bytes = null;
		
		if (StringUtils.ENCODING_ISO_8859_1.equals(charsetName))
		{
			if (mBytesValue_ISO_8859_1 != null)
			{
				bytes = mBytesValue_ISO_8859_1.get();
			}
			if (null == bytes)
			{
				bytes = toString().getBytes(charsetName);
				if (null == mBytesValue_ISO_8859_1)
				{
					mBytesValue_ISO_8859_1 = new SoftReference<byte[]>(bytes);
				}
			}
		}
		else if (StringUtils.ENCODING_UTF_8.equals(charsetName))
		{
			if (mBytesValue_UTF_8 != null)
			{
				bytes = mBytesValue_UTF_8.get();
			}
			if (null == bytes)
			{
				bytes = toString().getBytes(charsetName);
				if (null == mBytesValue_UTF_8)
				{
					mBytesValue_UTF_8 = new SoftReference<byte[]>(bytes);
				}
			}
		}
		else if (StringUtils.ENCODING_US_ASCII.equals(charsetName))
		{
			if (mBytesValue_US_ASCII != null)
			{
				bytes = mBytesValue_US_ASCII.get();
			}
			if (null == bytes)
			{
				bytes = toString().getBytes(charsetName);
				if (null == mBytesValue_US_ASCII)
				{
					mBytesValue_US_ASCII = new SoftReference<byte[]>(bytes);
				}
			}
		}
		else if (StringUtils.ENCODING_UTF_16.equals(charsetName))
		{
			if (mBytesValue_UTF_16 != null)
			{
				bytes = mBytesValue_UTF_16.get();
			}
			if (null == bytes)
			{
				bytes = toString().getBytes(charsetName);
				if (null == mBytesValue_UTF_16)
				{
					mBytesValue_UTF_16 = new SoftReference<byte[]>(bytes);
				}
			}
		}
		else if (StringUtils.ENCODING_UTF_16BE.equals(charsetName))
		{
			if (mBytesValue_UTF_16BE != null)
			{
				bytes = mBytesValue_UTF_16BE.get();
			}
			if (null == bytes)
			{
				bytes = toString().getBytes(charsetName);
				if (null == mBytesValue_UTF_16BE)
				{
					mBytesValue_UTF_16BE = new SoftReference<byte[]>(bytes);
				}
			}
		}
		else if (StringUtils.ENCODING_UTF_16LE.equals(charsetName))
		{
			if (mBytesValue_UTF_16LE != null)
			{
				bytes = mBytesValue_UTF_16LE.get();
			}
			if (null == bytes)
			{
				bytes = toString().getBytes(charsetName);
				if (null == mBytesValue_UTF_16LE)
				{
					mBytesValue_UTF_16LE = new SoftReference<byte[]>(bytes);
				}
			}
		}
		else
		{
			bytes = toString().getBytes(charsetName);
		}
		
		return bytes;
	}

	public int length()
	{
		return mStringValue.length();
	}
	
	public void append(String value)
	{
		mStringValue = mStringValue+value;
		if (mBytesValue_ISO_8859_1 != null)
		{
			SoftReference<byte[]> reference = mBytesValue_ISO_8859_1;
			mBytesValue_ISO_8859_1 = null;
			reference.clear();
		}
		if (mBytesValue_UTF_8 != null)
		{
			SoftReference<byte[]> reference = mBytesValue_UTF_8;
			mBytesValue_UTF_8 = null;
			reference.clear();
		}
		if (mBytesValue_UTF_16 != null)
		{
			SoftReference<byte[]> reference = mBytesValue_UTF_16;
			mBytesValue_UTF_16 = null;
			reference.clear();
		}
		if (mBytesValue_UTF_16BE != null)
		{
			SoftReference<byte[]> reference = mBytesValue_UTF_16BE;
			mBytesValue_UTF_16BE = null;
			reference.clear();
		}
		if (mBytesValue_UTF_16LE != null)
		{
			SoftReference<byte[]> reference = mBytesValue_UTF_16LE;
			mBytesValue_UTF_16LE = null;
			reference.clear();
		}
	}
	
	public CharSequence subSequence(int beginIndex, int endIndex)
	{
		return mStringValue.subSequence(beginIndex, endIndex);
	}
	
	public char charAt(int index)
	{
		return mStringValue.charAt(index);
	}
}

