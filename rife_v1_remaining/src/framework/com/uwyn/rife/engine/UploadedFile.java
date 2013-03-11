/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UploadedFile.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.tools.ExceptionUtils;
import java.io.File;
import java.util.logging.Logger;

/**
 * An <code>UploadedFile</code> instance is created by the web engine when
 * files are uploaded through a multi-part request.
 * <p>The uploaded files can be retrieved through the
 * <code>ElementSupport#getUploadedFile</code> method and its siblings. The
 * web engine does its best to dispose of the temporary file at a convenient
 * time, but the file is not guaranteed to persist after the request. If you
 * want to make sure that the file is deleted, you should call {@link
 * File#delete} yourself when you're finished with the uploaded file.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class UploadedFile implements Cloneable
{
	private File    mTempFile = null;
	private String  mFilename = null;
	private String  mType = null;
	private boolean mSizeExceeded = false;
	
	public UploadedFile(String filename, String type)
	{
		mFilename = filename;
		mType = type;
	}
	
	protected void finalize()
	throws Throwable
	{
		if (mTempFile != null)
		{
			mTempFile.delete();
		}
		
		super.finalize();
	}
	
	public void setTempFile(File tempFile)
	{
		assert tempFile != null;
		assert tempFile.exists();
		assert tempFile.isFile();
		assert tempFile.canRead();
		
		mTempFile = tempFile;
		mTempFile.deleteOnExit();
	}
	
	public void setSizeExceeded(boolean exceeded)
	{
		mSizeExceeded = exceeded;
	}
	
	/**
	 * Retrieves the content type of the file.
	 * 
	 * @return the content type of the uploaded file
	 * @since 1.0
	 */
	public String getType()
	{
		return mType;
	}
	
	/**
	 * Retrieves the name of the file that was selected on the client when
	 * uploading.
	 * 
	 * @return the name of the original file that was uploaded
	 * @since 1.0
	 */
	public String getName()
	{
		return mFilename;
	}
	
	/**
	 * Retrieves the temporary file on the server that was created for the
	 * upload.
	 * 
	 * @return the temporary uploaded file
	 * @since 1.0
	 */
	public File getFile()
	{
		return mTempFile;
	}
	
	/**
	 * Indicates whether the uploaded file exceeded the file {@link
	 * com.uwyn.rife.config.RifeConfig.Engine#getFileuploadSizeLimit upload
	 * size limit}.
	 * <p>If the limit was exceeded, the temporary file will be
	 * <code>null</code> and deleted from the server.
	 * 
	 * @return <code>true</code> if the upload file size limit was exceeded;
	 * or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean wasSizeExceeded()
	{
		return mSizeExceeded;
	}
	
	public UploadedFile clone()
	{
		try
		{
			return (UploadedFile)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.site").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
			///CLOVER:ON
		}
	}
}

