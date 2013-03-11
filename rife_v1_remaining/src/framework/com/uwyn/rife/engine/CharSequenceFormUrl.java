/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CharSequenceFormUrl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

class CharSequenceFormUrl extends CharSequenceDeferred<CharSequenceQueryUrl>
{
	private StateStore	mStateStore = null;
	private String 		mUrl = null;
	
	CharSequenceFormUrl(StateStore stateStore, String url)
	{
		mStateStore = stateStore;
		mUrl = url;
	}
	
	protected void fillInContent()
	{
		mStateStore.exportFormUrl(this, mUrl);
	}
}

