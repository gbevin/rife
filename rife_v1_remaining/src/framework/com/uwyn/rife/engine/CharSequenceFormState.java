/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CharSequenceFormState.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

class CharSequenceFormState extends CharSequenceDeferred<CharSequenceFormState>
{
	private StateStore		mStateStore = null;
	private FlowState		mState = null;
	private FormStateType	mStateType = null;
	
	CharSequenceFormState(StateStore stateStore, FlowState state, FormStateType stateType)
	{
		mStateStore = stateStore;
		mState = state;
		mStateType = stateType;
	}
	
	protected void fillInContent()
	{
		mStateStore.exportFormState(this, mState, mStateType);
	}
}

