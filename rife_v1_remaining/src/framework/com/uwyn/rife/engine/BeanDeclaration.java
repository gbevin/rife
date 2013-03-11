/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

public class BeanDeclaration
{
	private String	mClassname = null;
	private Class	mClass = null;
	private String	mPrefix = null;
	private String	mGroupName = null;
	
	BeanDeclaration(String classname, String prefix, String groupName)
	{
		assert classname != null;
		assert classname.length() > 0;
		assert null == prefix || prefix.length() > 0;

		if (prefix != null && 0 == prefix.length())			prefix = null;
		if (groupName != null && 0 == groupName.length())	groupName = null;

		mClassname = classname;
		mPrefix = prefix;
		mGroupName = groupName;
	}
	
	BeanDeclaration(Class klass, String prefix, String groupName)
	{
		this(klass.getName(), prefix, groupName);
		
		assert klass != null;

		mClass = klass;
	}
	
	public Class getBeanClass()
	throws ClassNotFoundException
	{
		if (null == mClass)
		{
			mClass = Class.forName(mClassname);
		}
		
		return mClass;
	}
	
	public String getClassname()
	{
		return mClassname;
	}
	
	public String getPrefix()
	{
		return mPrefix;
	}
	
	public String getGroupName()
	{
		return mGroupName;
	}
	
	public int hashCode()
	{
		int classname = 1;
		int prefix = 1;
		int groupname = 1;
		
		if (mClassname != null)
		{
			classname = mClassname.hashCode();
		}
		if (mPrefix != null)
		{
			prefix = mPrefix.hashCode();
		}
		if (mGroupName != null)
		{
			groupname = mGroupName.hashCode();
		}
		
		return classname*prefix*groupname;
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
		
		if (!(other instanceof BeanDeclaration))
		{
			return false;
		}
		
		BeanDeclaration other_bean = (BeanDeclaration)other;
		if (!other_bean.getClassname().equals(getClassname()))
		{
			return false;
		}
		if (other_bean.getPrefix() != null || getPrefix() != null)
		{
			if (null == other_bean.getPrefix() && getPrefix() != null)
			{
				return false;
			}
			if (other_bean.getPrefix() != null && null == getPrefix())
			{
				return false;
			}
			if (!other_bean.getPrefix().equals(getPrefix()))
			{
				return false;
			}
		}
		if (other_bean.getGroupName() != null || getGroupName() != null)
		{
			if (null == other_bean.getGroupName() && getGroupName() != null)
			{
				return false;
			}
			if (other_bean.getGroupName() != null && null == getGroupName())
			{
				return false;
			}
			if (!other_bean.getGroupName().equals(getGroupName()))
			{
				return false;
			}
		}
		
		return true;
	}
}


