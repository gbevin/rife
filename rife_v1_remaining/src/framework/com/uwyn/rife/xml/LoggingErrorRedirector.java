/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LoggingErrorRedirector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml;

import java.util.ArrayList;
import java.util.Collection;
import org.xml.sax.SAXParseException;

public class LoggingErrorRedirector extends XmlErrorRedirector
{
	private ArrayList<SAXParseException>	mWarnings = new ArrayList<SAXParseException>();
	private ArrayList<SAXParseException>	mErrors = new ArrayList<SAXParseException>();
	private ArrayList<SAXParseException>	mFatalErrors = new ArrayList<SAXParseException>();

	public LoggingErrorRedirector()
	{
	}
	
	public synchronized void warning(SAXParseException e)
	{
		if (null == mWarnings)
		{
			mWarnings = new ArrayList<SAXParseException>();
		}
		mWarnings.add(e);
	}

	public synchronized void error(SAXParseException e)
	{
		if (null == mErrors)
		{
			mErrors = new ArrayList<SAXParseException>();
		}
		mErrors.add(e);
	}

	public synchronized void fatalError(SAXParseException e)
	{
		if (null == mFatalErrors)
		{
			mFatalErrors = new ArrayList<SAXParseException>();
		}
		mFatalErrors.add(e);
	}
	
	public Collection<SAXParseException> getWarnings()
	{
		return mWarnings;
	}

	public Collection<SAXParseException> getErrors()
	{
		return mErrors;
	}

	public Collection<SAXParseException> getFatalErrors()
	{
		return mFatalErrors;
	}

	public boolean hasWarnings()
	{
		if (mWarnings != null &&
			!mWarnings.isEmpty())
		{
			return true;
		}
		
		return false;
	}

	public boolean hasErrors()
	{
		if (mErrors != null &&
			!mErrors.isEmpty())
		{
			return true;
		}
		
		return false;
	}

	public boolean hasFatalErrors()
	{
		if (mFatalErrors != null &&
			!mFatalErrors.isEmpty())
		{
			return true;
		}
		
		return false;
	}
}



