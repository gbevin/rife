/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FeedProvider.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed.elements;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.feed.Entry;
import com.uwyn.rife.feed.EntryProcessor;
import com.uwyn.rife.feed.EntryProvider;
import com.uwyn.rife.feed.Feed;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;

/**
 * An <code>Element</code> that uses an <code>EntryProvider</code> to print
 * out a feed.
 * <p>After being passed an IoC property of the <code>EntryProvider</code> and
 * the feed type one wishes to print out, <code>FeedProvider</code> will do
 * just that.
 * <p>The supported properties are:
 * <table border="1">
 * <tr>
 * <td><code>feedtype</code>
 * <td><code>rss_2.0</code> or <code>atom_0.3</code>
 * <tr>
 * <td><code>provider</code>
 * <td>an instance of <code>EntryProvider</code>
 * <tr>
 * <td><code>classname</code>
 * <td>the name of an <code>EntryProvider</code> class when the
 * <code>provider</code> property isn't set
 * </table>
 *
 * @author JR Boyens (jboyens[remove] at uwyn dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.engine.Element
 * @see com.uwyn.rife.feed.EntryProvider
 * @see com.uwyn.rife.feed.Entry
 * @see com.uwyn.rife.feed.Feed
 * @since 1.0
 */
public class FeedProvider extends Element implements EntryProcessor
{
	private static final HashSet<String>	VALID_FEED_TYPES = new HashSet<String>();

	private SimpleDateFormat	mIso8601DateFormat = null;
	private SimpleDateFormat	mRfc822DateFormat = null;

	private Template			mFeedTemplate = null;
	private SimpleDateFormat	mDateFormat = null;

	static
	{
		VALID_FEED_TYPES.add("rss_2_0");
		VALID_FEED_TYPES.add("atom_0_3");
	}
	
	public FeedProvider()
	{
		mIso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		mIso8601DateFormat.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		mRfc822DateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z");
		mRfc822DateFormat.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
	}

	public void processElement()
	{
		String feed_type = getPropertyString("feedtype").replace('.', '_');

		assert isValidFeedType(feed_type);

		mFeedTemplate = TemplateFactory.XML.get("feeds."+feed_type);
		if (feed_type.indexOf("atom") != -1)
		{
			mDateFormat = mIso8601DateFormat;
		}
		else
		{
			mDateFormat = mRfc822DateFormat;
		}

		// try and load using IoC first
		EntryProvider provider = getPropertyTyped("provider", EntryProvider.class);
		if (provider == null)
		{
			// IoC didn't work... try by classname
			String classname = getPropertyString("classname");
			try
			{
				provider = loadProvider(classname);
			}
			catch (ClassNotFoundException e)
			{
				String extendedClassname = "com.uwyn.rife.feed.entryproviders." + classname;

				try
				{
					provider = loadProvider(extendedClassname);
				}
				catch (ClassNotFoundException e1)
				{
					throw new UnsupportedFeedDataTypeException("Cannot find provider: "+classname+" or "+extendedClassname);
				}
				catch (Exception e1)
				{
					throw new EngineException(e1);
				}
			}
			catch (Exception e)
			{
				throw new EngineException(e);
			}
		}

		if (provider != null)
		{
			provider.provideEntries(this, this);

			Feed feed = provider.getFeedDescriptor(this);
			mFeedTemplate.setBean(feed, "feed_");
			mFeedTemplate.setValue("feed_publishedDate", mDateFormat.format(feed.getPublishedDate()));
			if (feed.getNamespaces() != null)
			{
				for (Map.Entry<String, String> entry : feed.getNamespaces().entrySet())
				{
					mFeedTemplate.setValue("namespace_key", encodeXml(entry.getKey()));
					mFeedTemplate.setValue("namespace_url", encodeXml(entry.getValue()));
					
					mFeedTemplate.appendBlock("namespaces", "namespace");
				}
			}
		}

		setContentType("application/xml");

		print(mFeedTemplate);
	}

	private EntryProvider loadProvider(String classname)
	throws SecurityException, NoSuchMethodException, IllegalArgumentException,
		InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException
	{
		if (classname == null)          return null;

		Class<EntryProvider>            providerClass = (Class<EntryProvider>)Class.forName(classname);
		Constructor<EntryProvider>      constructor = providerClass.getConstructor(new Class[] {});

		return constructor.newInstance(new Object[] {});
	}

	private boolean isValidFeedType(String feedType)
	{
		return VALID_FEED_TYPES.contains(feedType);
	}

	private class UnsupportedFeedDataTypeException extends RuntimeException
	{
		private static final long serialVersionUID = 8910041916874032181L;

		public UnsupportedFeedDataTypeException()
		{
			super();
		}

		public UnsupportedFeedDataTypeException(String message)
		{
			super(message);
		}

		public UnsupportedFeedDataTypeException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public UnsupportedFeedDataTypeException(Throwable cause)
		{
			super(cause);
		}
	}

	public void setEntry(Entry entry)
	{
		mFeedTemplate.setBean(entry, "entry_");
		if (entry.isEscaped() &&
			mFeedTemplate.hasValueId("entry_escaped_attribute"))
		{
			mFeedTemplate.setBlock("entry_escaped_attribute", "entry_escaped_attribute");
		}
		if (!entry.isEscaped())
		{
			mFeedTemplate.setValue("entry_content", entry.getContent());
		}
		mFeedTemplate.setValue("entry_publishedDate", mDateFormat.format(entry.getPublishedDate()));
		mFeedTemplate.appendBlock("entries", "entry");
	}
}
