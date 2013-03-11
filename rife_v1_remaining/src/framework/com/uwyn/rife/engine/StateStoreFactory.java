/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StateStoreFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.StateStore;
import java.util.HashMap;
import java.util.Map;

public abstract class StateStoreFactory
{
	private static Map<String, StateStore>	sStateStores = new HashMap<String, StateStore>() {{
			put(StateStoreQuery.IDENTIFIER, new StateStoreQuery());
			put(StateStoreSession.IDENTIFIER, new StateStoreSession());
		}};
	
	public static StateStore getInstance(String identifier)
	{
		return sStateStores.get(identifier);
	}
	
	public static void put(String identifier, StateStore stateStore)
	{
		sStateStores.put(identifier, stateStore);
	}
}
