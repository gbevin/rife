/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StateStore.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import java.util.Map;

public interface StateStore
{
	public void init(Request request) throws EngineException;
	public Map<String, String[]> restoreParameters(Request request) throws EngineException;
	public ElementResultState createNewResultState(String contextId) throws EngineException;
	public Class getResultStateType() throws EngineException;
	public ResultStates restoreResultStates(Request request) throws EngineException;

	public void exportQueryUrl(CharSequenceDeferred deferred, String url, FlowState state, ElementInfo source, String type, String name) throws EngineException;
	public void exportFormState(CharSequenceDeferred deferred, FlowState state, FormStateType stateType) throws EngineException;
	public void exportFormUrl(CharSequenceDeferred deferred, String url) throws EngineException;
}
