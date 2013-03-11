/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLink.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;
	
class DataLink
{
	private String		mOutput = null;
	private ElementInfo	mTarget = null;
	private boolean		mSnapback = false;
	private String		mInput = null;
	private FlowLink	mFlowLink = null;
	
	DataLink(String output, ElementInfo target, boolean snapback, String input, FlowLink flowlink)
	{
		assert output != null;
		assert output.length() > 0;
		assert target != null || snapback;
		assert null == target || !snapback;
		assert input != null;
		assert input.length() > 0;
		
		mOutput = output;
		mTarget = target;
		mSnapback = snapback;
		mInput = input;
		mFlowLink = flowlink;
	}
	
	String getOutput()
	{
		return mOutput;
	}
	
	ElementInfo getTarget()
	{
		return mTarget;
	}
	
	boolean isSnapback()
	{
		return mSnapback;
	}
	
	String getInput()
	{
		return mInput;
	}
	
	FlowLink getFlowLink()
	{
		return mFlowLink;
	}
	
	public int hashCode()
	{
		int target = 1;
		int snapback = 1;
		int flowlink = 1;
		
		if (mTarget != null)
		{
			target = mTarget.hashCode();
		}
		if (mSnapback)
		{
			snapback = 2;
		}
		if (mFlowLink != null)
		{
			flowlink = mFlowLink.hashCode();
		}
		return mOutput.hashCode()*target*snapback*mInput.hashCode()*flowlink;
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
		
		if (!(other instanceof DataLink))
		{
			return false;
		}
		
		DataLink other_datalink = (DataLink)other;
		if (!other_datalink.getOutput().equals(getOutput()))
		{
			return false;
		}
		if (other_datalink.getTarget() != null || getTarget() != null)
		{
			if (null == other_datalink.getTarget() && getTarget() != null)
			{
				return false;
			}
			if (other_datalink.getTarget() != null && null == getTarget())
			{
				return false;
			}
			if (!other_datalink.getTarget().equals(getTarget()))
			{
				return false;
			}
		}
		if (other_datalink.isSnapback() != isSnapback())
		{
			return false;
		}
		if (!other_datalink.getInput().equals(getInput()))
		{
			return false;
		}
		if (other_datalink.getFlowLink() != null || getFlowLink() != null)
		{
			if (null == other_datalink.getFlowLink() && getFlowLink() != null)
			{
				return false;
			}
			if (other_datalink.getFlowLink() != null && null == getFlowLink())
			{
				return false;
			}
			if (!other_datalink.getFlowLink().equals(getFlowLink()))
			{
				return false;
			}
		}
		
		return true;
	}
}
