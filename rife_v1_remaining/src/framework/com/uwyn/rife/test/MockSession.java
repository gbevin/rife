/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockSession.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

class MockSession implements HttpSession
{
    final static public String SESSION_COOKIE_NAME = "JSESSION";

    private static int sNextId = 1;
	
	private MockConversation	mMockConversation;
    private String				mId = Integer.toString(sNextId++);
	private long				mCreationTime = System.currentTimeMillis();
	private long				mLastAccessTime = System.currentTimeMillis();
    private int					mMaxInactiveInterval;
	private Map<String, Object>	mAttributes = new HashMap<String, Object>();
	private boolean				mIsNew = true;
	private boolean				mInvalid = false;
	
	MockSession(MockConversation conversation)
	{
		mMockConversation = conversation;
	}
	
	public long getCreationTime()
	{
        if (mInvalid) throw new IllegalStateException();

		return mCreationTime;
	}
	
	public String getId()
	{
        if (mInvalid) throw new IllegalStateException();

		return mId;
	}
	
	public long getLastAccessedTime()
	{
        if (mInvalid) throw new IllegalStateException();

		return mLastAccessTime;
	}
	
	public void setMaxInactiveInterval(int interval)
	{
        if (mInvalid) throw new IllegalStateException();

		mMaxInactiveInterval = interval;
	}
	
	public int getMaxInactiveInterval()
	{
        if (mInvalid) throw new IllegalStateException();

		return mMaxInactiveInterval;
	}
	
	public Object getAttribute(String name)
	{
        if (mInvalid) throw new IllegalStateException();
		
		return mAttributes.get(name);
	}
	
	public Enumeration getAttributeNames()
	{
        if (mInvalid) throw new IllegalStateException();
		
		return Collections.enumeration(mAttributes.keySet());
	}
	
	public void setAttribute(String name, Object value)
	{
        if (mInvalid) throw new IllegalStateException();
		
		mAttributes.put(name, value);
	}
	
	public void removeAttribute(String name)
	{
        if (mInvalid) throw new IllegalStateException();
		
		mAttributes.remove(name);
	}
	
	public void invalidate()
	{
		mMockConversation.removeSession(mId);
		mInvalid = true;
		mAttributes.clear();
		mId = null;
	}
	
	public boolean isNew()
	{
		return mIsNew;
	}

	public Object getValue(String name)
	{
        if (mInvalid) throw new IllegalStateException();
		
		return getAttribute(name);
	}

	public String[] getValueNames()
	{
        if (mInvalid) throw new IllegalStateException();
		
		String[] names_array = new String[mAttributes.size()];
		mAttributes.keySet().toArray(names_array);
		return names_array;
	}

	public void putValue(String name, Object value)
	{
        if (mInvalid) throw new IllegalStateException();
		
		setAttribute(name, value);
	}

	public void removeValue(String name)
	{
        if (mInvalid) throw new IllegalStateException();
		
		removeAttribute(name);
	}
	
	public ServletContext getServletContext()
	{
		return null;
	}

    public HttpSessionContext getSessionContext()
	{
		return null;
	}
	
	void access()
	{
		mIsNew = false;
		mLastAccessTime = System.currentTimeMillis();
	}
	
	boolean isValid()
	{
		return !mInvalid;
	}
}
