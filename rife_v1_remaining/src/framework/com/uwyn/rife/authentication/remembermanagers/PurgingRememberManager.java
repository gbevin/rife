/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PurgingRememberManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.config.RifeConfig;
import java.util.Random;

public class PurgingRememberManager implements RememberManager
{
	private int		mRememberPurgeFrequency = RifeConfig.Authentication.getRememberPurgeFrequency();
	private int		mRememberPurgeScale = RifeConfig.Authentication.getRememberPurgeScale();

	private final Random	mRandom = new Random();
	
	private RememberManager	mRememberManager = null;

    public PurgingRememberManager(RememberManager rememberManager)
    {
		if (null == rememberManager)	throw new IllegalArgumentException("rememberManager can't be null");

		mRememberManager = rememberManager;
    }
	
	public RememberManager getRememberManager()
	{
		return mRememberManager;
	}
	
	public int getRememberPurgeFrequency()
	{
		return mRememberPurgeFrequency;
	}
	
	public void setRememberPurgeFrequency(int frequency)
	{
		mRememberPurgeFrequency = frequency;
	}
	
	public int getRememberPurgeScale()
	{
		return mRememberPurgeScale;
	}
	
	public void setRememberPurgeScale(int scale)
	{
		mRememberPurgeScale = scale;
	}
	
	public long getRememberDuration()
	{
		return mRememberManager.getRememberDuration();
	}
	
	public void setRememberDuration(long milliseconds)
	{
		mRememberManager.setRememberDuration(milliseconds);
	}
	
	public String createRememberId(long userId, String hostIp)
	throws RememberManagerException
	{
		int purge_decision = -1; 
		synchronized (mRandom)
		{
			purge_decision = mRandom.nextInt(mRememberPurgeScale);
		}
		if (purge_decision <= mRememberPurgeFrequency)
		{
			purgeRememberIds();
		}
		
		return mRememberManager.createRememberId(userId, hostIp);
	}
	
	public boolean eraseRememberId(String rememberId)
	throws RememberManagerException
	{
		return mRememberManager.eraseRememberId(rememberId);
	}
	
	public boolean eraseUserRememberIds(long userId)
	throws RememberManagerException
	{
		return mRememberManager.eraseUserRememberIds(userId);
	}
	
	public void eraseAllRememberIds()
	throws RememberManagerException
	{
		mRememberManager.eraseAllRememberIds();
	}
	
	public long getRememberedUserId(String rememberId)
	throws RememberManagerException
	{
		return mRememberManager.getRememberedUserId(rememberId);
	}
	
	public void purgeRememberIds()
	throws RememberManagerException
	{
		mRememberManager.purgeRememberIds();
	}
}

