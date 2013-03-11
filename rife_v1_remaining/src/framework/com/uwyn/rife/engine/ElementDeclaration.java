/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.ElementIdInvalidException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.resources.ResourceFinder;
import java.util.Stack;

class ElementDeclaration implements Cloneable
{
	private SiteBuilder					mSiteBuilder = null;
	private ElementInfoBuilder			mElementInfoBuilder = null;
	private GroupDeclaration			mGroup = null;
	private StateStore					mStateStore = null;

	private String						mDeclarationName = null;
	private String						mId = null;
	private String						mUrl = null;
	private String						mInherits = null;
	private String						mPre = null;
	private ElementInfo					mElementInfo = null;
	private HierarchicalProperties		mProperties = null;
	private Stack<ElementDeclaration>	mParentStack = null;
	private Stack<ElementDeclaration>	mPreStack = null;
	
	ElementDeclaration(SiteBuilder siteBuilder, ResourceFinder resourceFinder, GroupDeclaration group, StateStore stateStore, String declarationName)
	{
		mSiteBuilder = siteBuilder;
		mElementInfoBuilder = new ElementInfoBuilder(siteBuilder, resourceFinder, this);
		setGroup(group);
		mStateStore = stateStore;
		mDeclarationName = declarationName;
		mProperties = new HierarchicalProperties();
		mParentStack = new Stack<ElementDeclaration>();
		mPreStack = new Stack<ElementDeclaration>();
	}

	void setId(String id)
	throws EngineException
	{
		if (null == id)
		{
			id = SiteBuilder.generateId(mDeclarationName);
		}

		if (null == id)
		{
			mId = null;
			return;
		}

		if (id.startsWith(".") ||
			id.endsWith(".") ||
			id.indexOf("..") != -1 ||
			id.indexOf("^") != -1 ||
			id.indexOf(":") != -1)
		{
			throw new ElementIdInvalidException(id);
		}

		mId = id;
	}

	void setDeclarationName(String declarationName)
	{
		mDeclarationName = declarationName;
	}

	void setUrl(String url)
	{
		mUrl = url;
	}

	void setInherits(String inherits)
	{
		mInherits = inherits;
	}

	void setPre(String pre)
	{
		mPre = pre;
	}

	SiteBuilder getSiteBuilder()
	{
		return mSiteBuilder;
	}

	ElementInfoBuilder getElementInfoBuilder()
	{
		return mElementInfoBuilder;
	}

	void setGroup(GroupDeclaration group)
	{
		if (mGroup != null)
		{
			mGroup.removeElementDeclaration(this);
		}

		if (group != null)
		{
			group.addElementDeclaration(this);
		}

		mGroup = group;
	}

	GroupDeclaration getGroup()
	{
		return mGroup;
	}

	String getId()
	{
		if (null == mId)
		{
			setId(null);
		}

		return mId;
	}
	
	boolean hasDeclaredId()
	{
		return mId != null;
	}

	void setStateStore(StateStore stateStore)
	{
		mStateStore = stateStore;
	}

	StateStore getStateStore()
	{
		return mStateStore;
	}

	String getUrl()
	{
		return mUrl;
	}
	
	boolean hasDeclaredUrl()
	{
		return mUrl != null;
	}

	String getDeclarationName()
	{
		if (null == mDeclarationName)
		{
			// Try to generate a declaration name if the element id has been provided.
			// A missing declaration name can mean two things:
			// * the element is totally declared manually, or
			// * the declaration is included in the element implementation as annotations
			if (mId != null)
			{
				if (AnnotationsElementDetector.hasElementAnnotation(getElementInfoBuilder().getImplementation()))
				{
					mDeclarationName = ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+getElementInfoBuilder().getImplementation();
				}
				else
				{
					mDeclarationName = ElementInfoProcessorFactory.MANUAL_IDENTIFIER+":"+mId;
				}
			}
		}
		
		return mDeclarationName;
	}

	String getInherits()
	{
		return mInherits;
	}

	String getPre()
	{
		return mPre;
	}

	void setElementInfo(ElementInfo elementInfo)
	{
		mElementInfo = elementInfo;
	}

	ElementInfo getElementInfo()
	{
		return mElementInfo;
	}

	void addProperty(String name, PropertyValue value)
	{
		mProperties.put(name, value);
	}

	public boolean hasProperty(String name)
	{
		if (null == mProperties)
		{
			return false;
		}

		return mProperties.contains(name);
	}

	HierarchicalProperties getProperties()
	{
		return mProperties;
	}

	Stack<ElementDeclaration> getParentStack()
	{
		return mParentStack;
	}

	Stack<ElementDeclaration> getPreStack()
	{
		return mPreStack;
	}

	public synchronized ElementDeclaration clone()
	{
        ElementDeclaration new_elementdeclaration = null;
		try
		{
			new_elementdeclaration = (ElementDeclaration)super.clone();
			if (mElementInfo != null)
			{
				new_elementdeclaration.mElementInfo = mElementInfo.clone();
				if (mParentStack != null)
				{
					new_elementdeclaration.mParentStack = new Stack<ElementDeclaration>();
					new_elementdeclaration.mParentStack.addAll(mParentStack);
				}
				if (mPreStack != null)
				{
					new_elementdeclaration.mPreStack = new Stack<ElementDeclaration>();
					new_elementdeclaration.mPreStack.addAll(mPreStack);
				}
				if (mProperties != null)
				{
					new_elementdeclaration.mProperties = new HierarchicalProperties();
					new_elementdeclaration.mProperties.putAll(mProperties);
				}
			}
		}
		catch (CloneNotSupportedException e)
		{
			new_elementdeclaration = null;
		}

		return new_elementdeclaration;
	}
}
