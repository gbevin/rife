/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubsiteDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.SubsiteIdInvalidException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.tools.Localization;
import java.util.Stack;

public class SubsiteDeclaration
{
	private SiteBuilder				mSiteBuilder = null;
	private String					mId = null;
	private String					mDeclarationName = null;
	private String					mUrlPrefix = null;
	private GroupDeclaration		mGroup = null;
	private Stack<StateStore>		mStateStores = null;
	private HierarchicalProperties	mProperties = null;
	
	SubsiteDeclaration(String declarationName, GroupDeclaration group, Stack<StateStore> stateStores)
	{
		mDeclarationName = declarationName;
		mUrlPrefix = "";
		mGroup = group;
		mStateStores = (Stack<StateStore>)stateStores.clone();
		mProperties = new HierarchicalProperties();
	}
	
	public SubsiteDeclaration setId(String id)
	throws EngineException
	{
		if (id != null &&
			0 == id.length())
		{
			throw new SubsiteIdInvalidException(id);
		}
		
		if (null == id)
		{
			id = SiteBuilder.generateId(mDeclarationName);
		}
		
		if (id.indexOf(".") != -1 ||
			id.indexOf("^") != -1)
		{
			throw new SubsiteIdInvalidException(id);
		}
		
		mId = id;
		
		return this;
	}
	
	public SubsiteDeclaration setUrlPrefix(String urlPrefix)
	throws EngineException
	{
		if (null == urlPrefix)
		{
			mUrlPrefix = "";
		}
		else
		{
			mUrlPrefix = Localization.extractLocalizedUrl(urlPrefix);
		}
		
		return this;
	}
	
	public String getId()
	{
		if (null == mId)
		{
			setId(null);
		}
		
		return mId;
	}
	
	public SiteBuilder getSiteBuilder()
	{
		return mSiteBuilder;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getUrlPrefix()
	{
		return mUrlPrefix;
	}
	
	void setSiteBuilder(SiteBuilder siteBuilder)
	{
		mSiteBuilder = siteBuilder;
		
		if (null == siteBuilder)
		{
			mProperties.setParent(null);
		}
		else
		{
			mProperties.setParent(siteBuilder.getProperties());
		}
	}
	
	GroupDeclaration getGroupDeclaration()
	{
		return mGroup;
	}
	
	Stack<StateStore> getStateStores()
	{
		return mStateStores;
	}
	
	HierarchicalProperties getProperties()
	{
		return mProperties;
	}
	
	public SiteBuilder enterSubsite()
	throws EngineException
	{
		return mSiteBuilder;
	}
	
	public SiteBuilder leaveSubsiteDeclaration()
	{
		return mSiteBuilder.getParent();
	}
	
	public SubsiteDeclaration addProperty(String name, PropertyValue value)
	{
		mProperties.put(name, value);
		
		return this;
	}
	
	public boolean containsProperty(String name)
	{
		return mProperties.contains(name);
	}
}

