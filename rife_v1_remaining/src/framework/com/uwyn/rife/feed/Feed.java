/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Feed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed;

import java.util.Date;
import java.util.Map;

/**
 * A bean representing a feed, or rather a feed's metadata.
 * <p>A <code>Feed</code> is a set of metadata that helps to describe a feed
 * for the user and/or the engine processing the feed.
 *
 * @author JR Boyens (jboyens[remove] at uwyn dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.feed.Entry
 * @since 1.0
 */
public class Feed
{
	private String 				mTitle = null;
	private String 				mLink = null;
	private String 				mDescription = null;
	private String 				mLanguage = null;
	private String 				mCopyright = null;
	private Date 				mPublishedDate = null;
	private String 				mAuthor = null;
	private Map<String, String>	mNamespaces = null;
	
	public Feed author(String author)
	{ 
		setAuthor(author); 
		return this; 
	}
	
	public String getAuthor()
	{ 
		return mAuthor; 
	}
	
	public void setAuthor(String author)
	{ 
		mAuthor = author; 
	}
	
	public Feed copyright(String copyright)
	{ 
		setCopyright(copyright); 
		return this; 
	}
	
	public String getCopyright()
	{ 
		return mCopyright; 
	}
	
	public void setCopyright(String copyright)
	{ 
		mCopyright = copyright; 
	}
	
	public Feed description(String description)
	{ 
		setDescription(description); 
		return this; 
	}
	
	public String getDescription()
	{ 
		return mDescription; 
	}
	
	public void setDescription(String description)
	{ 
		mDescription = description; 
	}
	
	public Feed language(String language)
	{ 
		setLanguage(language); 
		return this; 
	}
	
	public String getLanguage()		
	{ 
		return mLanguage;
	}
	
	public void setLanguage(String language)
	{
		mLanguage = language;
	}
	
	public Feed link(String link)
	{ 
		setLink(link); 
		return this; 
	}
	
	public String getLink()	
	{ 
		return mLink; 
	}
	
	public void setLink(String link)
	{ 
		mLink = link;
	}
	
	public Feed publishedDate(Date publishedDate)
	{ 
		setPublishedDate(publishedDate); 
		return this;
	}
	
	public Date getPublishedDate()
	{ 
		return mPublishedDate;
	}
	
	public void setPublishedDate(Date publishedDate)
	{ 
		mPublishedDate = publishedDate; 
	}
	
	public Feed title(String title)
	{ 
		setTitle(title);
		return this; 
	}
	
	public String getTitle()
	{ 
		return mTitle;
	}
	
	public void setTitle(String title)
	{ 
		mTitle = title;
	}

	public Map<String, String> getNamespaces()
	{
		return mNamespaces;
	}

	public void setNamespaces(Map<String, String> namespaces)
	{
		mNamespaces = namespaces;
	}

	public Feed namespaces(Map<String, String> namespaces)
	{
		setNamespaces(namespaces);
		return this;
	}
}
