/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CharFilterDocument.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing.documents;

import java.awt.Toolkit;
import java.text.StringCharacterIterator;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class CharFilterDocument extends PlainDocument
{
	private static final long serialVersionUID = 3113190474991139229L;
	
	public static final int	VALID = 0;
	public static final int	INVALID = 1;

	private String	mChars = null;
	private int		mFilterType = -1;
	private int		mMaxChars = 0;

	public CharFilterDocument(String chars, int filterType)
	{
		this(chars, filterType, 0);
	}

	public CharFilterDocument(String chars, int filterType, int maxChars)
	{
		super();

		mChars = chars;
		mFilterType = filterType;
		mMaxChars = maxChars;
	}

	public void insertString(int offset, String origString, AttributeSet attributes)
	throws BadLocationException
	{
		StringBuilder filtered_string = new StringBuilder(filterString(origString));
		if (mMaxChars > 0 &&
			getLength()+filtered_string.length() > mMaxChars)
		{
			filtered_string.setLength(mMaxChars-getLength());
		}
		if (origString.length() != filtered_string.length())
		{
			Toolkit.getDefaultToolkit().beep();
		}

		super.insertString(offset, filtered_string.toString(), attributes);
	}

	private String filterString(String origString)
	{
		StringBuilder			filtered_string = new StringBuilder();
		StringCharacterIterator	it = new StringCharacterIterator(origString);
		while (StringCharacterIterator.DONE != it.current())
		{
			if (VALID == mFilterType)
			{
				if (-1 != mChars.indexOf(it.current()))
				{
					filtered_string.append(it.current());
				}
			}
			else if (INVALID == mFilterType)
			{
				if (-1 == mChars.indexOf(it.current()))
				{
					filtered_string.append(it.current());
				}
			}
			it.next();
		}

		return filtered_string.toString();
	}
}

