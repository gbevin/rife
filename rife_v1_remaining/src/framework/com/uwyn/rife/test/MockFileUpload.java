/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockFileUpload.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import com.uwyn.rife.config.RifeConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An instance of this class provides all the data that is needed to simulate
 * a file upload.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public class MockFileUpload
{
	private File        mFile;
	private InputStream mInputStream;
	private String      mFileName;
	private String      mContentType = "text/plain";
	
	/**
	 * Creates a new file upload simulation based on a <code>File</code>
	 * object.
	 * <p>The content type will be guessed from the file extension. The
	 * extension to mime-type mapping is retrieved from {@link
	 * com.uwyn.rife.config.RifeConfig.Mime}.
	 * 
	 * @param file the file that will be uploaded
	 * @since 1.1
	 */
	public MockFileUpload(File file)
	{
		if (null == file)   throw new IllegalArgumentException("file can't be null.");

		mFile = file;
		guessContentType();
	}
	
	/**
	 * Creates a new file upload simulation based on a <code>File</code>
	 * object.
	 * 
	 * @param file the file that will be uploaded
	 * @param contentType the content type of the file
	 * @since 1.1
	 */
	public MockFileUpload(File file, String contentType)
	{
		if (null == file)   throw new IllegalArgumentException("file can't be null.");
		
		mFile = file;
		if (null == contentType)
		{
			guessContentType();
		}
		else
		{
			mContentType = contentType;
		}
	}
	
	/**
	 * Creates a new file upload simulation based on an
	 * <code>InputStream</code>.
	 * 
	 * @param fileName the name of file that will be uploaded
	 * @param inputStream the input stream that will be read to provide the
	 * content of the uploaded file
	 * @param contentType the content type of the uploaded file
	 * @since 1.1
	 */
	public MockFileUpload(String fileName, InputStream inputStream, String contentType)
	{
		if (null == fileName)       throw new IllegalArgumentException("fileName can't be null.");
		if (null == inputStream)    throw new IllegalArgumentException("inputStream can't be null.");
		
		mFileName = fileName;
		mInputStream = inputStream;
		if (null == contentType)
		{
			guessContentType();
		}
		else
		{
			mContentType = contentType;
		}
	}
	
	InputStream getInputStream() throws IOException
	{
		if (null == mInputStream)
		{
			mInputStream = new FileInputStream(mFile);
		}
		return mInputStream;
	}
	
	String getFileName()
	{
		if (null == mFileName)
		{
			mFileName = mFile.getAbsolutePath();
		}
		return mFileName;
	}
	
	/**
	 * Returns the content type associated with this file upload simulation.
	 * <p>If no content type has been provided, and it could not be detected
	 * automatically, then it defaults to <code>text/plain</code>.
	 * 
	 * @return the content type
	 * @since 1.1
	 */
	public String getContentType()
	{
		return mContentType;
	}
	
	private void guessContentType()
	{
		String extension = getExtension(getFileName());
		if (null == extension)
		{
			return;
		}
		
		String content_type = RifeConfig.Mime.getMimeType(extension);
		if (content_type != null)
		{
			mContentType = content_type;
		}
	}
	
	private String getExtension(String fileName)
	{
		int last_dot_index = fileName.lastIndexOf('.');
		if (-1 == last_dot_index)
		{
			return null;
		}
		return fileName.substring(last_dot_index + 1);
	}
}
