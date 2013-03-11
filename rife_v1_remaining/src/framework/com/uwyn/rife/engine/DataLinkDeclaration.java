/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

class DataLinkDeclaration
{
	private String	mSrcOutput = null;
	private String	mSrcOutbean = null;
	private String	mDestId = null;
	private boolean	mSnapback = false;
	private String	mDestInput = null;
	private String	mDestInbean = null;
	private FlowLinkBuilder	mFlowLinkBuilder = null;
	
	DataLinkDeclaration(String srcOutput, String srcOutbean, String destId, boolean snapback, String destInput, String destInbean, FlowLinkBuilder flowlink)
	{
		if (srcOutput != null && 0 == srcOutput.length()) srcOutput = null;
		if (srcOutbean != null && 0 == srcOutbean.length()) srcOutbean = null;
		if (destId != null && 0 == destId.length()) destId = null;
		if (destInput != null && 0 == destInput.length()) destInput = null;
		if (destInbean != null && 0 == destInbean.length()) destInbean = null;

		assert srcOutput != null || srcOutbean != null;
		assert (srcOutput != null && srcOutput.length() > 0) || (srcOutbean != null && srcOutbean.length() > 0);
		assert destId != null || snapback;
		assert null == destId || !snapback;
		assert destInput != null || destInbean != null;
		assert (destInput != null && destInput.length() > 0) || (destInbean != null && destInbean.length() > 0);
		
		mSrcOutput = srcOutput;
		mSrcOutbean = srcOutbean;
		mDestId = destId;
		mSnapback = snapback;
		mDestInput = destInput;
		mDestInbean = destInbean;
		mFlowLinkBuilder = flowlink;
	}
	
	String getSrcOutput()
	{
		return mSrcOutput;
	}
	
	String getSrcOutbean()
	{
		return mSrcOutbean;
	}
	
	String getDestId()
	{
		return mDestId;
	}
	
	void makeAbsoluteDestId(SiteBuilder builder)
	{
		if (mDestId != null)
		{
			mDestId = builder.makeAbsoluteElementId(mDestId);
			mDestId = Site.getCanonicalId(mDestId);
		}
	}
	
	String getDestInput()
	{
		return mDestInput;
	}
	
	String getDestInbean()
	{
		return mDestInbean;
	}
	
	boolean transfersBean()
	{
		return !(mSrcOutput != null && mDestInput != null);

	}
	
	boolean isSnapback()
	{
		return mSnapback;
	}
	
	FlowLink getFlowLink()
	{
		if (null == mFlowLinkBuilder)
		{
			return null;
		}
		return mFlowLinkBuilder.getFlowLinkDeclaration().getFlowLink();
	}
	
	FlowLinkDeclaration getFlowLinkDeclaration()
	{
		if (null == mFlowLinkBuilder)
		{
			return null;
		}
		return mFlowLinkBuilder.getFlowLinkDeclaration();
	}
	
	public int hashCode()
	{
		int srcoutput = 1;
		int srcoutbean = 1;
		int destid = 1;
		int snapback = 1;
		int destinput = 1;
		int destinbean = 1;
		int flowlink = 1;
		
		if (mSrcOutput != null)
		{
			srcoutput = mSrcOutput.hashCode();
		}
		if (mSrcOutbean != null)
		{
			srcoutbean = mSrcOutbean.hashCode();
		}
		if (mDestId != null)
		{
			destid = mDestId.hashCode();
		}
		if (mSnapback)
		{
			snapback = 2;
		}
		if (mDestInput != null)
		{
			destinput = mDestInput.hashCode();
		}
		if (mDestInbean != null)
		{
			destinbean = mDestInbean.hashCode();
		}
		if (mFlowLinkBuilder != null)
		{
			flowlink = mFlowLinkBuilder.hashCode();
		}
		
		return srcoutput*srcoutbean*destid*snapback*destinput*destinbean*flowlink;
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
		
		if (!(other instanceof DataLinkDeclaration))
		{
			return false;
		}
		
		DataLinkDeclaration other_datalink = (DataLinkDeclaration)other;
		if (other_datalink.getSrcOutput() != null || getSrcOutput() != null)
		{
			if (null == other_datalink.getSrcOutput() || null == getSrcOutput())
			{
				return false;
			}
			else if (!other_datalink.getSrcOutput().equals(getSrcOutput()))
			{
				return false;
			}
		}
		if (other_datalink.getSrcOutbean() != null || getSrcOutbean() != null)
		{
			if (null == other_datalink.getSrcOutbean() || null == getSrcOutbean())
			{
				return false;
			}
			else if (!other_datalink.getSrcOutbean().equals(getSrcOutbean()))
			{
				return false;
			}
		}
		if (!other_datalink.getDestId().equals(getDestId()))
		{
			return false;
		}
		if (other_datalink.isSnapback() != isSnapback())
		{
			return false;
		}
		if (other_datalink.getDestInput() != null || getDestInput() != null)
		{
			if (null == other_datalink.getDestInput() || null == getDestInput())
			{
				return false;
			}
			else if (!other_datalink.getDestInput().equals(getDestInput()))
			{
				return false;
			}
		}
		if (other_datalink.getDestInbean() != null || getDestInbean() != null)
		{
			if (null == other_datalink.getDestInbean() || null == getDestInbean())
			{
				return false;
			}
			else if (!other_datalink.getDestInbean().equals(getDestInbean()))
			{
				return false;
			}
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



