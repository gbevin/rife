/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementResultState.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.Map;

public interface ElementResultState
{
	public void populate(ElementResultState other);
	public String getContextId();
	public void setContinuationId(String continuationId);
	public String getContinuationId();
	public void setPreservedInputs(Map<String, String[]> preservedInputs);
	public Map<String, String[]> getPreservedInputs();
	public String getBase64EncodedState();
}
