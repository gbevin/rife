/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CharSequenceQueryUrl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

class CharSequenceQueryUrl extends CharSequenceDeferred<CharSequenceQueryUrl>
{
	private StateStore	mStateStore = null;
	private String 		mUrl = null;
	private FlowState	mState = null;
	private ElementInfo	mElementInfo = null;
	private String		mType = null;
	private String		mName = null;
	
	CharSequenceQueryUrl(StateStore stateStore, String url, FlowState state, ElementInfo elementInfo, String type, String name)
	{
		mStateStore = stateStore;
		mUrl = url;
		mState = state;
		mElementInfo = elementInfo;
		mType = type;
		mName = name;
	}
	
	protected void fillInContent()
	{
		mStateStore.exportQueryUrl(this, mUrl, mState, mElementInfo, mType, mName);
	}
}

