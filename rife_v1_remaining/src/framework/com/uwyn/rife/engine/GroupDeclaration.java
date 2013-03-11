/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GroupDeclaration.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;

import java.util.*;

/**
 * GroupDeclaration
 *
 * The Merged HashMaps are a collection of vars/exits/cookies from the parent
 * and the local group
 */
class GroupDeclaration
{
	private IdSequence						mIdSequence = null;
	private int								mGroupId = -1;
	
	private SiteBuilder						mDeclaringSiteBuilder = null;
	private SiteBuilder						mActiveSiteBuilder = null;
	private String							mDeclarationName = null;
	private GroupDeclaration				mParent = null;
	private List<GroupDeclaration> 			mChildGroupDeclarations = null;
	private List<ElementDeclaration>		mElementDeclarations = null;
	private Map<String, GlobalVar>			mGlobalVarsLocal = null;
	private Map<String, GlobalVar>			mGlobalVarsMerged = null;
	private Map<String, GlobalExit>			mGlobalExitsLocal = null;
	private Map<String, GlobalExit>			mGlobalExitsMerged = null;
	private Map<String, String>				mGlobalCookiesLocal = null;
	private Map<String, String>				mGlobalCookiesMerged = null;
	private Map<String, BeanDeclaration>	mNamedGlobalBeansLocal = null;
	private Map<String, BeanDeclaration>	mNamedGlobalBeansMerged = null;
	private List<ErrorHandler>				mErrorHandlersLocal = null;
	private List<ErrorHandler>				mErrorHandlersMerged = null;
	private String							mInherits = null;
	private String							mPre = null;
	
	private class IdSequence
	{
		private int	mId = 0;
		
		synchronized int getNextId()
		{
			return mId++;
		}
	}
	
	GroupDeclaration(SiteBuilder declaringSiteBuilder, String declarationName)
	{
		this(declaringSiteBuilder, declarationName, null);
	}
	
	GroupDeclaration(SiteBuilder declaringSiteBuilder, String declarationName, GroupDeclaration parent)
	{
		mDeclaringSiteBuilder = declaringSiteBuilder;
		mActiveSiteBuilder = declaringSiteBuilder;
		mDeclarationName = declarationName;
		mParent = parent;
		mChildGroupDeclarations = new ArrayList<GroupDeclaration>();
		mElementDeclarations = new ArrayList<ElementDeclaration>();
		mGlobalVarsLocal = new LinkedHashMap<String, GlobalVar>();
		mGlobalCookiesLocal = new LinkedHashMap<String, String>();
		mGlobalExitsLocal = new LinkedHashMap<String, GlobalExit>();
		mNamedGlobalBeansLocal = new LinkedHashMap<String, BeanDeclaration>();
		mErrorHandlersLocal = new ArrayList<ErrorHandler>();
		
		if (null == parent)
		{
			mIdSequence = new IdSequence();
		}
		else
		{
			mIdSequence = parent.mIdSequence;
			parent.addChildGroupDeclaration(this);
		}
		
		mGroupId = mIdSequence.getNextId();
	}
	
	GroupDeclaration getParent()
	{
		return mParent;
	}
	
	void setInherits(String inherits)
	{
		mInherits = inherits;
	}
	
	void setPre(String pre)
	{
		mPre = pre;
	}
	
	String getInherits()
	{
		return mInherits;
	}
	
	String getPre()
	{
		return mPre;
	}
	
	int getGroupId()
	{
		return mGroupId;
	}
	
	SiteBuilder getDeclaringSiteBuilder()
	{
		return mDeclaringSiteBuilder;
	}
	
	void setActiveSiteBuilder(SiteBuilder activeSiteBuilder)
	{
		mActiveSiteBuilder = activeSiteBuilder;
	}
	
	SiteBuilder getActiveSiteBuilder()
	{
		return mActiveSiteBuilder;
	}
	
	String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	private void addChildGroupDeclaration(GroupDeclaration child)
	{
		mChildGroupDeclarations.add(child);
	}
	
	Collection<GroupDeclaration> getChildGroupDeclarations()
	{
		return mChildGroupDeclarations;
	}
	
	void addElementDeclaration(ElementDeclaration elementDeclaration)
	{
		mElementDeclarations.add(elementDeclaration);
	}
	
	boolean removeElementDeclaration(ElementDeclaration elementDeclaration)
	{
		return mElementDeclarations.remove(elementDeclaration);
	}
	
	Collection<ElementDeclaration> getElementDeclarations()
	{
		return mElementDeclarations;
	}
	
	void addGlobalVar(String name, GlobalVar globalVar)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert globalVar != null;
		
		if (mGlobalVarsMerged != null)
		{
			throw new GlobalVarsLockedException(getDeclarationName(), name);
		}
		
		if (ReservedParameters.RESERVED_NAMES_LIST.contains(name))
		{
			throw new ReservedGlobalVarNameException(getDeclarationName(), name);
		}
		
		if (mGlobalVarsLocal.containsKey(name))
		{
			throw new GlobalVarExistsException(getDeclarationName(), name);
		}

		globalVar.setGroupId(mGroupId);
		mGlobalVarsLocal.put(name, globalVar);
	}
	
	void addGlobalCookie(String name, String defaultVal)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		if (mGlobalCookiesMerged != null)
		{
			throw new GlobalCookiesLockedException(getDeclarationName(), name);
		}
		
		if (mGlobalVarsLocal.containsKey(name))
		{
			throw new GlobalCookieExistsException(getDeclarationName(), name);
		}

		mGlobalCookiesLocal.put(name, defaultVal);
	}
	
	void addGlobalExit(String name, GlobalExit globalExit)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert globalExit != null;

		if (mGlobalExitsMerged != null)
		{
			throw new GlobalExitsLockedException(getDeclarationName(), name);
		}
		
		if (mGlobalExitsLocal.containsKey(name))
		{
			throw new GlobalExitExistsException(getDeclarationName(), name);
		}

		globalExit.setGroupId(mGroupId);
		mGlobalExitsLocal.put(name, globalExit);
	}
	
	void addNamedGlobalBean(String name, BeanDeclaration bean)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert bean != null;
		
		if (mNamedGlobalBeansLocal.containsKey(name))
		{
			throw new NamedGlobalBeanExistsException(getDeclarationName(), name);
		}
		
		mNamedGlobalBeansLocal.put(name, bean);
	}

	void addErrorHandler(ErrorHandler handler)
	throws EngineException
	{
		assert handler != null;

		if (mErrorHandlersMerged != null)
		{
			throw new ErrorHandlersLockedException(getDeclarationName(), handler.getDestId());
		}

		handler.setGroupId(mGroupId);
		mErrorHandlersLocal.add(handler);
	}

	Map<String, GlobalVar> getGlobalVarsLocal()
	{
		return mGlobalVarsLocal;
	}
	
	Map<String, GlobalVar> getGlobalVarsMerged()
	{
		if (null == mGlobalVarsMerged)
		{
			Map<String, GlobalVar> merged = new LinkedHashMap<String, GlobalVar>();
			merged.putAll(mGlobalVarsLocal);
			
			if (getParent() != null)
			{
				// merge the group's global vars with the ones of its parent
				// should a global var be present in both, and they both have
				// default values, then the default values of the current group
				// override the ones of the parent
				Map<String, GlobalVar>	globalvars = getParent().getGlobalVarsMerged();
				String					globalvar_name = null;
				
				for (Map.Entry<String, GlobalVar> globalvar_entry : globalvars.entrySet())
				{
					globalvar_name = globalvar_entry.getKey();
					if (!merged.containsKey(globalvar_name) ||
						null == merged.get(globalvar_name))
					{
						merged.put(globalvar_name, globalvar_entry.getValue());
					}
				}
			}

			mGlobalVarsMerged = Collections.unmodifiableMap(merged);
		}
		
		return mGlobalVarsMerged;
	}
	
	Map<String, String> getGlobalCookiesMerged()
	{
		if (null == mGlobalCookiesMerged)
		{
			Map<String, String> merged = new LinkedHashMap<String, String>();
			merged.putAll(mGlobalCookiesLocal);
			
			if (getParent() != null)
			{
				// merge the group's global cookies with the ones of its parent
				Map<String, String> globalcookies = getParent().getGlobalCookiesMerged();
				String              globalcookie_name = null;
				
				for( Map.Entry<String, String> globalcookie_entry : globalcookies.entrySet() )
				{
					globalcookie_name = globalcookie_entry.getKey();
					if (!merged.containsKey(globalcookie_name) ||
					   null == merged.get(globalcookie_name))
					{
						merged.put(globalcookie_name, globalcookie_entry.getValue());
					}
				}
			}

			mGlobalCookiesMerged = Collections.unmodifiableMap(merged);
		}
		
		return mGlobalCookiesMerged;
	}
	
	Map<String, GlobalExit> getGlobalExitsMerged()
	{
		if (null == mGlobalExitsMerged)
		{
			Map<String, GlobalExit> merged = new LinkedHashMap<String, GlobalExit>();
			
			for (Map.Entry<String, GlobalExit> globalexit : mGlobalExitsLocal.entrySet())
			{
				globalexit.getValue().makeAbsoluteDestId(mActiveSiteBuilder);
				merged.put(globalexit.getKey(), globalexit.getValue());
			}
			
			if (getParent() != null)
			{
				// merge the group's global exits with the ones of its parent
				// should a global exit be present in both, then an exception
				// is thrown
				Map<String, GlobalExit>	globalexits = getParent().getGlobalExitsMerged();
				String					globalexit_name = null;
				
				for (Map.Entry<String, GlobalExit> globalexit_entry : globalexits.entrySet())
				{
					globalexit_name = globalexit_entry.getKey();
					if (!merged.containsKey(globalexit_name) ||
						null == merged.get(globalexit_name))
					{
						merged.put(globalexit_name, globalexit_entry.getValue());
					}
					else
					{
						throw new GlobalExitOverriddenException(getDeclarationName(), globalexit_name);
					}
				}
			}

			mGlobalExitsMerged = Collections.unmodifiableMap(merged);
		}
		
		return mGlobalExitsMerged;
	}
	
	Map<String, BeanDeclaration> getNamedGlobalBeansMerged()
	{
		if (null == mNamedGlobalBeansMerged)
		{
			Map<String, BeanDeclaration> merged = new LinkedHashMap<String, BeanDeclaration>();
			merged.putAll(mNamedGlobalBeansLocal);
			
			if (getParent() != null)
			{
				// merge the group's named global beans with the ones of its
				// parent
				// should a named global bean be present in both, then the one
				// of the current group override the one of the parent
				Map<String, BeanDeclaration>	globalbeans = getParent().getNamedGlobalBeansMerged();
				String							globalbean_name = null;
				
				for (Map.Entry<String, BeanDeclaration> globalbean_entry : globalbeans.entrySet())
				{
					globalbean_name = globalbean_entry.getKey();
					if (!merged.containsKey(globalbean_name) ||
						null == merged.get(globalbean_name))
					{
						merged.put(globalbean_name, globalbean_entry.getValue());
					}
				}
			}

			mNamedGlobalBeansMerged = Collections.unmodifiableMap(merged);
		}
		
		return mNamedGlobalBeansMerged;
	}

	List<ErrorHandler> getErrorHandlersMerged()
	{
		if (null == mErrorHandlersMerged)
		{
			List<ErrorHandler> merged = new ArrayList<ErrorHandler>();
			merged.addAll(mErrorHandlersLocal);

			if (getParent() != null)
			{
				merged.addAll(getParent().getErrorHandlersMerged());
			}

			mErrorHandlersMerged = Collections.unmodifiableList(merged);
		}

		return mErrorHandlersMerged;
	}
}

