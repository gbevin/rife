/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SimpleEntryProvider.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.ElementSupport;
import java.util.Calendar;

public class SimpleEntryProvider implements EntryProvider
{
	private Calendar mCalendar = null;
	
	public SimpleEntryProvider()
	{
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		mCalendar.set(2005, Calendar.JANUARY, 1, 0, 0, 0);
		mCalendar.set(Calendar.AM_PM, Calendar.AM);
	}
	
	public Feed getFeedDescriptor(ElementSupport element)
	{
		Feed feed = new Feed();
		feed
			.title("feed_title")
			.author("feed_author")
			.copyright("feed_copyright")
			.description("feed_description")
			.language("feed_language")
			.link("feed_link")
			.publishedDate(mCalendar.getTime());
		
		return feed;
	}
	
	public void provideEntries(ElementSupport element, EntryProcessor processor)
	{
		for (int i = 0; i < 2; i++)
		{
			mCalendar.set(Calendar.HOUR, i+1);
			Entry entry = new Entry();
			entry
				.author("entry_author"+(i+1))
				.content("entry_content"+(i+1))
				.link("entry_link"+(i+1))
				.publishedDate(mCalendar.getTime())
				.title("entry_title"+(i+1));
			
			processor.setEntry(entry);
		}
	}
}
