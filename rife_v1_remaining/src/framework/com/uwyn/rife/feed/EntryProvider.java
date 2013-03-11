/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EntryProvider.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed;

import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.feed.Feed;

/**
 * An <code>EntryProvider</code> is a way to get entries for a feed.
 *
 * @author JR Boyens (jboyens[remove] at uwyn dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see Feed
 * @since 1.0
 */
public interface EntryProvider
{
	/**
	 * Get a bean describing the <code>Feed</code> being outputted.
	 *
	 * @param element the context for this EntryProvider
	 * @return Feed a bean describing the feed currently being outputted
	 * @see Feed
	 * @since 1.0
	 */
	public Feed getFeedDescriptor(ElementSupport element);
	
	/**
	 * Provide entries using {@link EntryProcessor#setEntry(Entry)} to set
	 * each entry to the feed
	 *
	 * @param element the context for this EntryProvider
	 * @param processor the processor creating this feed
	 * @see EntryProcessor
	 * @since 1.0
	 */
	public void provideEntries(ElementSupport element, EntryProcessor processor);
}
