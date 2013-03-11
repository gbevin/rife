/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Entry.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed;

import java.util.Date;

/**
 * A bean representing an entry in a feed.
 * <p>An <code>Entry</code> is a single piece of content, (forum message, news article,
 * blog post), with it's own title, link to permanent content,
 * published date, content and author. Has a many-to-one relationship
 * with <code>Feed</code>.
 *
 * @author JR Boyens (jboyens[remove] at uwyn dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.feed.Feed
 * @since 1.0
 */
public class Entry
{
	private String 	mId = null;
	private String 	mTitle = null;
	private String 	mLink = null;
	private Date 	mPublishedDate = null;
	private String 	mContent = null;
	private String 	mAuthor = null;
	private String 	mType = "text/html";
	private boolean	mEscaped = true;
	
	public Entry author(String author)
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
	
	public Entry content(String content)
	{ 
		setContent(content); 
		return this; 
	}
	
	public String getContent()
	{ 
		return mContent; 
	}
	
	public void setContent(String content)
	{ 
		mContent = content; 
	}
	
	public Entry id(String id)
	{ 
		setId(id); 
		return this; 
	}
	
	public String getId()
	{
		if (null == mId)
		{
			return getLink();
		}
		
		return mId; 
	}
	
	public void setId(String id)
	{ 
		mId = id; 
	}
	
	public Entry link(String link)
	{ 
		setLink(link); return this; 
	}
	
	public String getLink()
	{ 
		return mLink; 
	}
	
	public void setLink(String link)
	{ 
		mLink = link; 
	}
	
	public Entry publishedDate(Date publishedDate)
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
	
	public Entry title(String title)
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

	public String getType()
	{
		return mType;
	}

	public void setType(String type)
	{
		mType = type;
	}

	public Entry type(String type)
	{
		setType(type);
		
		return this;
	}

	public boolean isEscaped()
	{
		return mEscaped;
	}

	public void setEscaped(boolean escaped)
	{
		mEscaped = escaped;
	}

	public Entry escaped(boolean escaped)
	{
		setEscaped(escaped);
		return this;
	}
}
