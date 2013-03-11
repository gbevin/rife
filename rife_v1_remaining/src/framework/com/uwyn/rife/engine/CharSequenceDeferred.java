/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CharSequenceDeferred.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.template.TemplateEncoder;

public abstract class CharSequenceDeferred<T extends CharSequenceDeferred> implements CharSequence
{
	private	CharSequence	mContent = null;
	private	String			mResult = null;
	private	TemplateEncoder	mEncoder = null;
	private long			mPreservedInputsModified = -1;
	
	public synchronized void setContent(CharSequence content)
	{
		mContent = content;
	}
	
	synchronized T encoder(TemplateEncoder encoder)
	{
		mEncoder = encoder;
		mResult = null;
		
		return (T)this;
	}
	
	public synchronized String encode(String value)
	{
		if (null == mEncoder)
		{
			return value;
		}
		
		return mEncoder.encode(value);
	}
	
	protected abstract void fillInContent();
	
	public synchronized final String toString()
	{
		long modified = RequestState.getActiveRequestState().getElementResultStatesObtained().getModified();
		if (mPreservedInputsModified != modified)
		{
			mResult = null;
			mPreservedInputsModified = modified;
			fillInContent();
		}
		
		if (null == mContent)
		{
			return null;
		}
		
		if (null == mResult)
		{
			mResult = mContent.toString();
		}
		
		return mResult;
	}
	
	public int length()
	{
		return 0;
	}
	
	public char charAt(int index)
	{
		return toString().charAt(index);
	}
	
	public CharSequence subSequence(int start, int end)
	{
		return toString().subSequence(start, end);
	}
}

