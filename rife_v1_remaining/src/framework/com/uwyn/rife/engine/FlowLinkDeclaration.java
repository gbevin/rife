/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLinkDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;
	
class FlowLinkDeclaration extends AbstractLogicLinkDeclaration
{
	private FlowLink	mFlowLink = null;

	FlowLinkDeclaration(SiteBuilder siteBuilder, String srcExit, String destId, boolean snapback, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect, boolean cancelContinuations)
	{
		super(siteBuilder, srcExit, destId, snapback, cancelInheritance, cancelEmbedding, redirect, cancelContinuations);
		
		assert destId != null || snapback;
		assert null == destId || !snapback;
	}
	
	FlowLink getFlowLink()
	{
		if (mFlowLink != null)
		{
			return mFlowLink;
		}
		
		ElementDeclaration	target_elementdeclaration = null;
		ElementInfo			flowlink_target_elementinfo = null;
		
		flowlink_target_elementinfo = null;
		
		if (getDestId() != null)
		{
			// get the element declaration that corresponds to the destination id
			target_elementdeclaration = getSiteBuilder().getGlobalElementDeclaration(getDestId());
			
			// if the target element couldn't be found, throw an exception
			if (null == target_elementdeclaration)
			{
				getSiteBuilder().elementIdNotFound(getDestId());
			}
			
			flowlink_target_elementinfo = target_elementdeclaration.getElementInfo();
		}
		
		mFlowLink = new FlowLink(getSrcExit(), flowlink_target_elementinfo, isSnapback(), cancelInheritance(), cancelEmbedding(), isRedirect(), cancelContinuations());
		
		return mFlowLink;
	}
	
	public boolean equals(Object other)
	{
		if (!(other instanceof FlowLinkDeclaration))
		{
			return false;
		}
		
		return super.equals(other);
	}
}
