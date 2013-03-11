/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutoLinkDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;
	
import java.util.Collection;

class AutoLinkDeclaration extends AbstractLogicLinkDeclaration
{
	private ElementInfoBuilder	mElementInfoBuilder = null;
	
	AutoLinkDeclaration(ElementInfoBuilder elementInfoBuilder, String srcExit, String destId, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect, boolean cancelContinuations)
	{
		super(elementInfoBuilder.getSiteBuilder(), srcExit, destId, false, cancelInheritance, cancelEmbedding, redirect, cancelContinuations);
		
		mElementInfoBuilder = elementInfoBuilder;
	}
	
	void registerFlowAndDataLinks()
	{
		FlowLinkBuilder flowlink_builder = new FlowLinkBuilder(mElementInfoBuilder, getSrcExit());
		flowlink_builder
			.destId(getDestId())
			.cancelInheritance(cancelInheritance())
			.cancelEmbedding(cancelEmbedding())
			.redirect(isRedirect());
		
		FlowLink flowlink = flowlink_builder.getFlowLinkDeclaration().getFlowLink();
		
		ElementInfo src_element_info = mElementInfoBuilder.getElementDeclaration().getElementInfo();
		
		// automatically add datalinks for outputs that have inputs with the same name in the target element
		Collection<String>	output_names = src_element_info.getOutputNames();
		Collection<String>	input_names = flowlink.getTarget().getInputNames();
		for (String output_name : output_names)
		{
			if (input_names.contains(output_name))
			{
				flowlink_builder.addDataLink(output_name, output_name);
			}
		}
		
		// automatically add datalinks for named outbeans that have named inbeans with the same name in the target element
		Collection<String>	outbean_names = src_element_info.getNamedOutbeanNames();
		Collection<String>	inbean_names = flowlink.getTarget().getNamedInbeanNames();
		for (String outbean_name : outbean_names)
		{
			if (inbean_names.contains(outbean_name))
			{
				flowlink_builder.addDataLinkBean(outbean_name, outbean_name);
			}
		}
		
		flowlink_builder.leaveFlowLink();
		
		mElementInfoBuilder.registerFlowAndDataLinksInSite(flowlink_builder.getFlowLinkDeclaration());
	}
	
	String getDestId()
	{
		String destid = super.getDestId();
		if (null == destid || 0 == destid.length())
		{
			return getSrcExit();
		}
		
		return destid;
	}
	
	public boolean equals(Object other)
	{
		if (!(other instanceof AutoLinkDeclaration))
		{
			return false;
		}
		
		return super.equals(other);
	}
}

