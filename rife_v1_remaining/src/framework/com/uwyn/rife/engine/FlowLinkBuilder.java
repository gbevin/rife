/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLinkBuilder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.FlowLinkAmbiguousTargetException;
import com.uwyn.rife.engine.exceptions.FlowLinkTargetRequiredException;

public class FlowLinkBuilder
{
	private ElementInfoBuilder	mElementInfoBuilder = null;

	private String	mSrcExit = null;
	private String	mDestId = null;
	private boolean	mSnapback = false;
	private boolean	mCancelInheritance = false;
	private boolean	mCancelEmbedding = false;
	private boolean	mRedirect = false;
	private boolean	mCancelContinuations = false;
	
	private FlowLinkDeclaration	mDeclaration = null;
	
	public FlowLinkBuilder(ElementInfoBuilder elementInfoBuilder, String srcExit)
	{
		mElementInfoBuilder = elementInfoBuilder;
		mSrcExit = srcExit;
	}
	
	public FlowLinkBuilder destId(String destId)
	{
		if (destId != null && 0 == destId.length()) destId = null;

		mDestId = destId;
		return this;
	}
	
	public FlowLinkBuilder snapback(boolean snapback)
	{
		mSnapback = snapback;
		return this;
	}
	
	public FlowLinkBuilder cancelInheritance(boolean cancelInheritance)
	{
		mCancelInheritance = cancelInheritance;
		return this;
	}
	
	public FlowLinkBuilder cancelEmbedding(boolean cancelEmbedding)
	{
		mCancelEmbedding = cancelEmbedding;
		return this;
	}
	
	public FlowLinkBuilder redirect(boolean redirect)
	{
		mRedirect = redirect;
		return this;
	}
	
	public FlowLinkBuilder cancelContinuations(boolean cancelContinuations)
	{
		mCancelContinuations = cancelContinuations;
		return this;
	}
	
	public FlowLinkBuilder addDataLink(String srcOutput, String destInput)
	throws EngineException
	{
		addDataLink(srcOutput, null, false, destInput, null);
		
		return this;
	}
	
	public FlowLinkBuilder addSnapbackDataLink(String srcOutput, String destInput)
	throws EngineException
	{
		addDataLink(srcOutput, null, true, destInput, null);
		
		return this;
	}
	
	public FlowLinkBuilder addDataLinkBean(String srcOutbean, String destInbean)
	throws EngineException
	{
		addDataLink(null, srcOutbean, false, null, destInbean);
		
		return this;
	}
	
	public FlowLinkBuilder addSnapbackDataLinkBean(String srcOutbean, String destInbean)
	throws EngineException
	{
		addDataLink(null, srcOutbean, true, null, destInbean);
		
		return this;
	}
	
	public FlowLinkBuilder addDataLink(String srcOutput, String srcOutbean, boolean snapback, String destInput, String destInbean)
	throws EngineException
	{
		String dest_id = null;
		if (!snapback)
		{
			dest_id = mDestId;
		}
		mElementInfoBuilder.addDataLink(srcOutput, srcOutbean, dest_id, snapback, destInput, destInbean, this);
		
		return this;
	}
	
	public ElementInfoBuilder leaveFlowLink()
	{
		mElementInfoBuilder.addFlowLinkDeclaration(getFlowLinkDeclaration());
		return mElementInfoBuilder;
	}
	
	String getSrcExit()
	{
		return mSrcExit;
	}
	
	FlowLinkDeclaration getFlowLinkDeclaration()
	throws EngineException
	{
		if (mDeclaration != null)
		{
			return mDeclaration;
		}
		
		if (null == mDestId &&
			!mSnapback)
		{
			throw new FlowLinkTargetRequiredException(mElementInfoBuilder.getSiteBuilder().getDeclarationName(), mElementInfoBuilder.getElementDeclaration().getId(), mSrcExit);
		}
		if (mDestId != null &&
			mSnapback)
		{
			throw new FlowLinkAmbiguousTargetException(mElementInfoBuilder.getSiteBuilder().getDeclarationName(), mElementInfoBuilder.getElementDeclaration().getId(), mSrcExit);
		}

		mDeclaration = new FlowLinkDeclaration(mElementInfoBuilder.getSiteBuilder(), mSrcExit, mDestId, mSnapback, mCancelInheritance, mCancelEmbedding, mRedirect, mCancelContinuations);
		
		return mDeclaration;
	}
	
	ElementInfoBuilder getElementInfoBuilder()
	{
		return mElementInfoBuilder;
	}
}
