/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalExit.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

public class GlobalExit
{
	private int			mGroupId = -1;

	private String		mDestId = null;
	private ElementInfo	mTarget = null;
	private boolean		mReflective = false;
	private boolean		mSnapback = false;
	private boolean		mCancelInheritance = false;
	private boolean		mCancelEmbedding = false;
	private boolean		mRedirect = false;
	private boolean		mCancelContinuations = false;
	
	GlobalExit(String destId, boolean reflective, boolean snapback, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect, boolean cancelContinuations)
	{
		assert (destId != null && !reflective && !snapback) ||
			   (null == destId && reflective && !snapback) ||
			   (null == destId && !reflective && snapback);
		
		mDestId = destId;
		mReflective = reflective;
		mSnapback = snapback;
		mCancelInheritance = cancelInheritance;
		mCancelEmbedding = cancelEmbedding;
		mRedirect = redirect;
		mCancelContinuations = cancelContinuations;
	}
	
	GlobalExit setGroupId(int groupId)
	{
		assert groupId > -1;
		
		mGroupId = groupId;
		
		return this;
	}
	
	void makeAbsoluteDestId(SiteBuilder siteBuilder)
	{
		if (mDestId != null)
		{
			mDestId = siteBuilder.makeAbsoluteElementId(mDestId);
			mDestId = Site.getCanonicalId(mDestId);
		}
	}
	
	String getDestId()
	{
		return mDestId;
	}
	
	void setTarget(ElementInfo target)
	{
		mTarget = target;
	}
	
	public ElementInfo getTarget()
	{
		return mTarget;
	}
	
	public int getGroupId()
	{
		return mGroupId;
	}
	
	public boolean isReflective()
	{
		return mReflective;
	}
	
	public boolean isSnapback()
	{
		return mSnapback;
	}
	
	public boolean cancelInheritance()
	{
		return mCancelInheritance;
	}
	
	public boolean cancelEmbedding()
	{
		return mCancelEmbedding;
	}
	
	public boolean isRedirect()
	{
		return mRedirect;
	}
	
	public boolean cancelContinuations()
	{
		return mCancelContinuations;
	}
}
