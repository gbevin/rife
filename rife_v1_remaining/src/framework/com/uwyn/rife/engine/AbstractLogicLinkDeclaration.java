/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractLogicLinkDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;
	
abstract class AbstractLogicLinkDeclaration
{
	private String	mSrcExit = null;
	private String	mDestId = null;
	private boolean	mSnapback = false;
	private boolean	mCancelInheritance = false;
	private boolean	mCancelEmbedding = false;
	private boolean	mRedirect = false;
	private boolean	mCancelContinuations = false;
	
	private SiteBuilder mSiteBuilder = null;
	
	AbstractLogicLinkDeclaration(SiteBuilder siteBuilder, String srcExit, String destId, boolean snapback, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect, boolean cancelContinuations)
	{
		assert siteBuilder != null;
		assert srcExit != null;
		assert srcExit.length() > 0;
		
		mSiteBuilder = siteBuilder;
		mSrcExit = srcExit;
		mDestId = destId;
		mSnapback = snapback;
		mCancelInheritance = cancelInheritance;
		mCancelEmbedding = cancelEmbedding;
		mRedirect = redirect;
		mCancelContinuations = cancelContinuations;
	}

	SiteBuilder getSiteBuilder()
	{
		return mSiteBuilder;
	}
	
	String getSrcExit()
	{
		return mSrcExit;
	}
	
	void makeAbsoluteDestId(SiteBuilder builder)
	{
		if (mDestId != null)
		{
			mDestId = builder.makeAbsoluteElementId(mDestId);
			mDestId = Site.getCanonicalId(mDestId);
		}
	}
	
	String getDestId()
	{
		return mDestId;
	}
	
	boolean isSnapback()
	{
		return mSnapback;
	}
	
	boolean cancelInheritance()
	{
		return mCancelInheritance;
	}
	
	boolean cancelEmbedding()
	{
		return mCancelEmbedding;
	}
	
	boolean isRedirect()
	{
		return mRedirect;
	}
	
	boolean cancelContinuations()
	{
		return mCancelContinuations;
	}
	
	public int hashCode()
	{
		int srcexit = 1;
		int destid = 1;
		int snapback = 1;
		int cancelinheritance = 1;
		int cancelembedded = 1;
		int redirect = 1;
		int cancelcontinuations = 1;
		
		if (mSrcExit != null)
		{
			srcexit = mSrcExit.hashCode();
		}
		if (mDestId != null)
		{
			destid = mDestId.hashCode();
		}
		if (mSnapback)
		{
			snapback = 2;
		}
		if (mCancelInheritance)
		{
			cancelinheritance = 2;
		}
		if (mCancelEmbedding)
		{
			cancelembedded = 2;
		}
		if (mRedirect)
		{
			redirect = 2;
		}
		if (mCancelContinuations)
		{
			cancelcontinuations = 2;
		}
		
		return srcexit*destid*snapback*cancelinheritance*cancelembedded*redirect*cancelcontinuations;
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
		
		if (!(other instanceof AbstractLogicLinkDeclaration))
		{
			return false;
		}
		
		AbstractLogicLinkDeclaration other_logiclink = (AbstractLogicLinkDeclaration)other;
		if (other_logiclink.getSrcExit() != null || getSrcExit() != null)
		{
			if (null == other_logiclink.getSrcExit() || null == getSrcExit())
			{
				return false;
			}
			else if (!other_logiclink.getSrcExit().equals(getSrcExit()))
			{
				return false;
			}
		}
		if (!other_logiclink.getDestId().equals(getDestId()))
		{
			return false;
		}
		if (other_logiclink.isSnapback() != isSnapback())
		{
			return false;
		}
		if (other_logiclink.cancelInheritance() != cancelInheritance())
		{
			return false;
		}
		if (other_logiclink.cancelEmbedding() != cancelEmbedding())
		{
			return false;
		}
		if (other_logiclink.isRedirect() != isRedirect())
		{
			return false;
		}
		
		return true;
	}
}

