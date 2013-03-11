/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLink.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;
	
public class FlowLink
{
	private String		mExit = null;
	private ElementInfo	mTarget = null;
	private boolean		mSnapback = false;
	private boolean		mCancelInheritance = false;
	private boolean		mCancelEmbedding = false;
	private boolean		mRedirect = false;
	private boolean		mCancelContinuations = false;
	
	FlowLink(String exit, ElementInfo target, boolean snapback, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect, boolean cancelContinuations)
	{
		assert exit != null;
		assert exit.length() > 0;
		assert target != null || snapback;
		assert null == target || !snapback;
		
		mExit = exit;
		mTarget = target;
		mSnapback = snapback;
		mCancelInheritance = cancelInheritance;
		mCancelEmbedding = cancelEmbedding;
		mRedirect = redirect;
		mCancelContinuations = cancelContinuations;
	}
	
	public String getExitName()
	{
		return mExit;
	}
	
	public ElementInfo getExitTarget(RequestState state)
	{
		if (mSnapback)
		{
			return state.getSnapback();
		}
		
		return mTarget;
	}
	
	public ElementInfo getTarget()
	{
		return mTarget;
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
	
	public int hashCode()
	{
		int target = 1;
		int snapback = 1;
		int cancel_inheritance = 1;
		int cancel_embedding = 1;
		int redirect = 1;
		int cancel_continuations = 1;
		
		if (mTarget != null)
		{
			target = mTarget.hashCode();
		}
		if (mSnapback)
		{
			snapback = 2;
		}
		if (mCancelInheritance)
		{
			cancel_inheritance = 2;
		}
		if (mCancelEmbedding)
		{
			cancel_embedding = 2;
		}
		if (mRedirect)
		{
			redirect = 2;
		}
		if (mCancelContinuations)
		{
			cancel_continuations = 2;
		}
		return mExit.hashCode()*target*snapback*cancel_inheritance*cancel_embedding*redirect*cancel_continuations;
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
		
		if (!(other instanceof FlowLink))
		{
			return false;
		}
		
		FlowLink other_flowlink = (FlowLink)other;
		if (!other_flowlink.getExitName().equals(getExitName()))
		{
			return false;
		}
		if (!other_flowlink.getTarget().equals(getTarget()))
		{
			return false;
		}
		if (other_flowlink.isSnapback() != isSnapback())
		{
			return false;
		}
		if (other_flowlink.cancelInheritance() != cancelInheritance())
		{
			return false;
		}
		if (other_flowlink.cancelEmbedding() != cancelEmbedding())
		{
			return false;
		}
		if (other_flowlink.isRedirect() != isRedirect())
		{
			return false;
		}
		
		return true;
	}
}
