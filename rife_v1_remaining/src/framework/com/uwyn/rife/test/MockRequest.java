/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockRequest.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import java.util.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Request;
import com.uwyn.rife.engine.RequestMethod;
import com.uwyn.rife.engine.StateStore;
import com.uwyn.rife.engine.UploadedFile;
import com.uwyn.rife.engine.exceptions.MultipartFileTooBigException;
import com.uwyn.rife.engine.exceptions.MultipartInvalidUploadDirectoryException;
import com.uwyn.rife.engine.exceptions.MultipartRequestException;
import com.uwyn.rife.test.MockHeaders;
import com.uwyn.rife.tools.StringUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Provides a {@link Request} implementation that is suitable for testing a
 * web application outside of a servlet container.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public class MockRequest implements Request
{
	private static final String
		SESSIONID_NOT_CHECKED = "not checked",
		SESSIONID_URL = "url",
		SESSIONID_COOKIE = "cookie",
		SESSIONID_NONE = "none";

	private RequestMethod               mRequestMethod = RequestMethod.GET;
	private Map<String, String[]>       mParameters = new HashMap<String, String[]>();
	private Map<String, UploadedFile[]> mFiles;
	private Map<String, Object>         mAttributes;
	private String                      mCharacterEncoding;
	private String                      mContentType;
	private MockHeaders                 mHeaders = new MockHeaders();
	private List<Locale>                mLocales;
	private File                        mUploadDirectory;
	
	private MockConversation            mMockConversation;
	private MockResponse                mMockResponse;
	private MockSession                 mSession;
	private String                      mRequestedSessionId;
	private String                      mSessionIdState = SESSIONID_NOT_CHECKED;
	
	private String                      mProtocol = "HTTP/1.1";
	private String                      mRemoteAddr = "127.0.0.1";
	private String                      mRemoteUser;
	private String                      mRemoteHost = "localhost";
	private boolean                     mSecure = false;
	
	public void init(StateStore stateStore)
	{
		String      parameter_name = null;
		String[]    parameter_values = null;
		for (Map.Entry<String, String[]> entry : mParameters.entrySet())
		{
			parameter_name = entry.getKey();
			if (StringUtils.doesUrlValueNeedDecoding(parameter_name))
			{
				parameter_name = StringUtils.decodeUrlValue(parameter_name);
			}
			
			parameter_values = entry.getValue();
			for (int i = 0; i < parameter_values.length; i++)
			{
				if (StringUtils.doesUrlValueNeedDecoding(parameter_values[i]))
				{
					parameter_values[i] = StringUtils.decodeUrlValue(parameter_values[i]);
				}
			}
			
			mParameters.put(parameter_name, parameter_values);
		}
		
		Map<String, String[]> parameters = stateStore.restoreParameters(this);
		if (parameters != null)
		{
			mParameters = parameters;
		}
	}
	
	void setMockConversation(MockConversation conversation)
	{
		mMockConversation = conversation;
	}
	
	void setMockResponse(MockResponse response)
	{
		mMockResponse = response;
	}
	
	public RequestMethod getMethod()
	{
		return mRequestMethod;
	}
	
	/**
	 * Sets the method of this request.
	 * <p>The method defaults to {@link RequestMethod#GET}.
	 *
	 * @param method the method that will be used by this request
	 * @see #getMethod
	 * @see #method
	 * @since 1.1
	 */
	public void setMethod(RequestMethod method)
	{
		if (null == method) throw new IllegalArgumentException("method can't be null");

		mRequestMethod = method;
	}
	
	/**
	 * Sets the method of this request.
	 *
	 * @param method the method that will be used by this request
	 * @return this <code>MockRequest</code> instance
	 * @see #getMethod
	 * @see #setMethod
	 * @since 1.1
	 */
	public MockRequest method(RequestMethod method)
	{
		setMethod(method);
		
		return this;
	}
	
	/**
	 * Checks whether a named parameter is present in this request.
	 *
	 * @param name the name of the parameter to check
	 * @return <code>true</code> if the parameter is present; or
	 * <p><code>false</code> otherwise
	 * @see #getParameters
	 * @see #setParameters
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public boolean hasParameter(String name)
	{
		return mParameters.containsKey(name);
	}
	
	/**
	 * Retrieves all the parameters of this request.
	 *
	 * @return a <code>Map</code> of the parameters with the names as the keys
	 * and their value arrays as the values
	 * @see #hasParameter
	 * @see #setParameters
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public Map<String, String[]> getParameters()
	{
		return mParameters;
	}
	
	/**
	 * Sets a map of parameters in this request.
	 *
	 * @param parameters a <code>Map</code> of the parameters that will be set
	 * with the names as the keys and their value arrays as the values
	 * @see #hasParameter
	 * @see #getParameters
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public void setParameters(Map<String, String[]> parameters)
	{
		if (null == parameters)
		{
			return;
		}

		for (Map.Entry<String, String[]> parameter : parameters.entrySet())
		{
			setParameter(parameter.getKey(), parameter.getValue());
		}
	}
	
	/**
	 * Sets a map of parameters in this request.
	 *
	 * @param parameters a <code>Map</code> of the parameters that will be set
	 * with the names as the keys and their value arrays as the values
	 * @return this <code>MockRequest</code> instance
	 * @see #hasParameter
	 * @see #getParameters
	 * @see #setParameters
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public MockRequest parameters(Map<String, String[]> parameters)
	{
		setParameters(parameters);
		
		return this;
	}
	
	/**
	 * Sets a parameter in this request.
	 *
	 * @param name the name of the parameter
	 * @param values the value array of the parameter
	 * @see #hasParameter
	 * @see #getParameters
	 * @see #setParameters
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public void setParameter(String name, String[] values)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		if (null == values)     throw new IllegalArgumentException("values can't be null");
		
		mParameters.put(name, values);
		if (mFiles != null)
		{
			mFiles.remove(name);
		}
	}
	
	/**
	 * Sets a parameter in this request.
	 *
	 * @param name the name of the parameter
	 * @param values the value array of the parameter
	 * @return this <code>MockRequest</code> instance
	 * @see #hasParameter
	 * @see #getParameters
	 * @see #setParameters
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public MockRequest parameter(String name, String[] values)
	{
		setParameter(name, values);
		
		return this;
	}
	
	/**
	 * Sets a parameter in this request.
	 *
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @see #hasParameter
	 * @see #getParameters
	 * @see #setParameters
	 * @see #setParameter(String, String[])
	 * @since 1.1
	 */
	public void setParameter(String name, String value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		if (null == value)      throw new IllegalArgumentException("value can't be null");
		
		setParameter(name, new String[] {value});
}
	
	/**
	 * Sets a parameter in this request.
	 *
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @return this <code>MockRequest</code> instance
	 * @see #hasParameter
	 * @see #getParameters
	 * @see #setParameters
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public MockRequest parameter(String name, String value)
	{
		setParameter(name, value);
		
		return this;
	}
	
	public Map<String, UploadedFile[]> getFiles()
	{
		return mFiles;
	}
	
	public boolean hasFile(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		
		if (null == getFiles())
		{
			return false;
		}
		
		if (!getFiles().containsKey(name))
		{
			return false;
		}
		
		UploadedFile[] uploaded_files = getFiles().get(name);
		
		if (0 == uploaded_files.length)
		{
			return false;
		}
		
		for (UploadedFile uploaded_file : uploaded_files)
		{
			if (uploaded_file != null &&
				uploaded_file.getName() != null)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public UploadedFile getFile(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		if (null == getFiles())
		{
			return null;
		}
		
		UploadedFile[] files = getFiles().get(name);
		if (null == files)
		{
			return null;
		}
		
		return files[0];
	}
	
	public UploadedFile[] getFiles(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		
		if (null == getFiles())
		{
			return null;
		}
		
		return getFiles().get(name);
	}
	
	private void checkUploadDirectory()
	{
		mUploadDirectory = new File(RifeConfig.Engine.getFileUploadPath());
		mUploadDirectory.mkdirs();
		
		if(!mUploadDirectory.exists() ||
		   !mUploadDirectory.isDirectory() ||
		   !mUploadDirectory.canWrite())
		{
			throw new MultipartInvalidUploadDirectoryException(mUploadDirectory);
		}
	}
	
	/**
	 * Sets a file in this request.
	 *
	 * @param name the parameter name of the file
	 * @param file the file specification that will be uploaded
	 * @see #hasFile
	 * @see #getFile
	 * @see #getFiles
	 * @see #setFiles(Map)
	 * @see #setFiles(String, MockFileUpload[])
	 * @since 1.1
	 */
	public void setFile(String name, MockFileUpload file)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		if (null == file)       throw new IllegalArgumentException("file can't be null");
		
		setFiles(name, new MockFileUpload[] {file});
	}
	
	/**
	 * Sets a map of files in this request.
	 *
	 * @param files a <code>Map</code> of the files that will be set with the
	 * names as the keys and their file upload specifications as the values
	 * @see #hasFile
	 * @see #getFile
	 * @see #getFiles
	 * @see #setFile(String, MockFileUpload)
	 * @see #setFiles(String, MockFileUpload[])
	 * @since 1.1
	 */
	public void setFiles(Map<String, MockFileUpload[]> files)
	{
		if (null == files ||
			0 == files.size())
		{
			return;
		}
		
		for (Map.Entry<String, MockFileUpload[]> file : files.entrySet())
		{
			setFiles(file.getKey(), file.getValue());
		}
	}
	
	/**
	 * Sets files in this request.
	 *
	 * @param name the parameter name of the file
	 * @param files the file specifications that will be uploaded
	 * @see #hasFile
	 * @see #getFile
	 * @see #getFiles
	 * @see #setFile(String, MockFileUpload)
	 * @see #setFiles(Map)
	 * @since 1.1
	 */
	public void setFiles(String name, MockFileUpload[] files)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		if (null == files)      throw new IllegalArgumentException("files can't be null");
		
		checkUploadDirectory();
		
		if (null == mFiles)
		{
			mFiles = new HashMap<String, UploadedFile[]>();
		}
		
		UploadedFile[] uploaded_files = new UploadedFile[files.length];
		for (int i = 0; i < files.length; i++)
		{
			UploadedFile uploaded_file = new UploadedFile(files[i].getFileName(), files[i].getContentType());

			try
			{
				File                    tmp_file = File.createTempFile("upl", ".tmp", mUploadDirectory);
				FileOutputStream        output_stream = new FileOutputStream(tmp_file);
				BufferedOutputStream    output = new BufferedOutputStream(output_stream, 8 * 1024); // 8K
				
				InputStream input_stream = files[i].getInputStream();
				long        downloaded_size = 0;
				
				byte[]  buffer = new byte[1024];
				int     return_value = -1;
				
				return_value = input_stream.read(buffer);
				while (-1 != return_value)
				{
					output.write(buffer, 0, return_value);
					
					// increase size count
					if (output != null &&
						RifeConfig.Engine.getFileUploadSizeCheck())
					{
						downloaded_size += return_value;
						
						if (downloaded_size > RifeConfig.Engine.getFileuploadSizeLimit())
						{
							uploaded_file.setSizeExceeded(true);
							output.close();
							output = null;
							tmp_file.delete();
							tmp_file = null;
							if (RifeConfig.Engine.getFileUploadSizeException())
							{
								throw new MultipartFileTooBigException(name, RifeConfig.Engine.getFileuploadSizeLimit());
							}
						}
					}
					
					return_value = input_stream.read(buffer);
				}
				
				if (output != null)
				{
					output.flush();
					output.close();
					output_stream.close();
				}
				
				if (tmp_file != null)
				{
					uploaded_file.setTempFile(tmp_file);
				}
				
				uploaded_files[i] = uploaded_file;
			}
			catch (IOException e)
			{
				throw new MultipartRequestException(e);
			}
		}
		
		mFiles.put(name, uploaded_files);
		mParameters.remove(name);
	}
	
	/**
	 * Sets a file in this request.
	 *
	 * @param name the parameter name of the file
	 * @param file the file specification that will be uploaded
	 * @return this <code>MockRequest</code> instance
	 * @see #hasFile
	 * @see #getFile
	 * @see #getFiles
	 * @see #setFile(String, MockFileUpload)
	 * @see #setFiles(Map)
	 * @see #setFiles(String, MockFileUpload[])
	 * @since 1.1
	 */
	public MockRequest file(String name, MockFileUpload file)
	{
		setFile(name, file);
		
		return this;
	}
	
	/**
	 * Sets a map of files in this request.
	 *
	 * @param files a <code>Map</code> of the files that will be set with the
	 * names as the keys and their file upload specifications as the values
	 * @return this <code>MockRequest</code> instance
	 * @see #hasFile
	 * @see #getFile
	 * @see #getFiles
	 * @see #setFile(String, MockFileUpload)
	 * @see #setFiles(Map)
	 * @see #setFiles(String, MockFileUpload[])
	 * @since 1.1
	 */
	public MockRequest files(Map<String, MockFileUpload[]> files)
	{
		setFiles(files);
		
		return this;
	}
	
	/**
	 * Sets files in this request.
	 *
	 * @param name the parameter name of the file
	 * @param files the file specifications that will be uploaded
	 * @return this <code>MockRequest</code> instance
	 * @see #hasFile
	 * @see #getFile
	 * @see #getFiles
	 * @see #setFile(String, MockFileUpload)
	 * @see #setFiles(Map)
	 * @see #setFiles(String, MockFileUpload[])
	 * @since 1.1
	 */
	public MockRequest files(String name, MockFileUpload[] files)
	{
		setFiles(name, files);
		
		return this;
	}
	
	public String getServerRootUrl(int port)
	{
		StringBuilder server_root = new StringBuilder();
		server_root.append(getScheme());
		server_root.append("://");
		server_root.append(getServerName());
		if (port <= -1)
		{
			port = getServerPort();
		}
		if (port != 80)
		{
			server_root.append(":");
			server_root.append(port);
		}
		return server_root.toString();
	}
	
	public boolean hasCookie(String name)
	{
		return mMockConversation.hasCookie(name);
	}
	
	public Cookie getCookie(String name)
	{
		return mMockConversation.getCookie(name);
	}
	
	public Cookie[] getCookies()
	{
		return mMockConversation.getCookies();
	}
	
	public Object getAttribute(String name)
	{
		if (null == mAttributes)
		{
			return null;
		}
		
		return mAttributes.get(name);
	}
	
	public boolean hasAttribute(String name)
	{
		if (null == mAttributes)
		{
			return false;
		}
		
		return mAttributes.containsKey(name);
	}
	
	public Enumeration getAttributeNames()
	{
		if (null == mAttributes)
		{
			return Collections.enumeration(new ArrayList<String>());
		}
		
		return Collections.enumeration(mAttributes.keySet());
	}
	
	public void removeAttribute(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		if (null == mAttributes)
		{
			return;
		}
		
		mAttributes.remove(name);
	}
	
	public void setAttribute(String name, Object object)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		if (null == mAttributes)
		{
			mAttributes = new HashMap<String, Object>();
		}
		
		mAttributes.put(name, object);
	}
	
	public String getCharacterEncoding()
	{
		return mCharacterEncoding;
	}
	
	/**
	 * Set the character encoding of this request.
	 *
	 * @param encoding the name of the character encoding
	 * @since 1.1
	 */
	public void setCharacterEncoding(String encoding)
	{
		if (null == encoding)       throw new IllegalArgumentException("encoding can't be null");
		if (0 == encoding.length()) throw new IllegalArgumentException("encoding can't be empty");
		
		mCharacterEncoding = encoding;
	}
	
	/**
	 * Set the character encoding of this request.
	 *
	 * @param encoding the name of the character encoding
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest characterEncoding(String encoding)
	{
		setCharacterEncoding(encoding);
		
		return this;
	}
	
	public String getContentType()
	{
		return mContentType;
	}
	
	/**
	 * Set the content type of this request.
	 *
	 * @param type the content type
	 * @since 1.1
	 */
	public void setContentType(String type)
	{
		if (null == type)       throw new IllegalArgumentException("type can't be null");
		if (0 == type.length()) throw new IllegalArgumentException("type can't be empty");
		
		mContentType = type;
	}
	
	/**
	 * Set the content type of this request.
	 *
	 * @param type the content type
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest contentType(String type)
	{
		setContentType(type);
		
		return this;
	}
	
	public long getDateHeader(String name)
	{
		return mHeaders.getDateHeader(name);
	}
	
	public String getHeader(String name)
	{
		return mHeaders.getHeader(name);
	}
	
	public Enumeration getHeaderNames()
	{
		return Collections.enumeration(mHeaders.getHeaderNames());
	}
	
	public Enumeration getHeaders(String name)
	{
		return Collections.enumeration(mHeaders.getHeaders(name));
	}
	
	public int getIntHeader(String name)
	{
		return mHeaders.getIntHeader(name);
	}
	
	/**
	 * Adds a request header with the given name and value. This method allows
	 * request headers to have multiple values.
	 *
	 * @param name the name of the header to set
	 * @param value the additional header value
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest addHeader(String name, String value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		if (null == value)      throw new IllegalArgumentException("value can't be null");
		
		mHeaders.addHeader(name, value);
		
		return this;
	}
	
	/**
	 * Adds a request header with the given name and date-value. The date is
	 * specified in terms of milliseconds since the epoch. This method allows
	 * request headers to have multiple values.
	 *
	 * @param name the name of the header to set
	 * @param value the additional date value
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest addDateHeader(String name, long value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		mHeaders.addDateHeader(name, value);
		
		return this;
	}
	
	/**
	 * Adds a request header with the given name and integer value. This
	 * method allows request headers to have multiple values.
	 *
	 * @param name the name of the header to set
	 * @param value the additional integer value
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest addIntHeader(String name, int value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		mHeaders.addIntHeader(name, value);
		
		return this;
	}
	
	/**
	 * Checks whether a certain request header is present.
	 *
	 * @param name the name of the header to check
	 * @return <code>true</code> if the header was present; or
	 * <p><code>false</code> otherwise
	 * @since 1.1
	 */
	public boolean containsHeader(String name)
	{
		return mHeaders.containsHeader(name);
	}
	
	/**
	 * Sets a request header with the given name and date-value. The date is
	 * specified in terms of milliseconds since the epoch. If the header had
	 * already been set, the new value overwrites the previous one. The {@link
	 * #containsHeader} method can be used to test for the presence of a
	 * header before setting its value.
	 *
	 * @param name the name of the header to set
	 * @param value the assigned date value
	 * @since 1.1
	 */
	public void setDateHeader(String name, long value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		mHeaders.setDateHeader(name, value);
	}
	
	/**
	 * Sets a request header with the given name and date-value.
	 *
	 * @param name the name of the header to set
	 * @param value the assigned date value
	 * @see #setDateHeader
	 * @since 1.1
	 */
	public MockRequest dateHeader(String name, long value)
	{
		setDateHeader(name, value);
		
		return this;
	}
	
	/**
	 * Sets a request header with the given name and value. If the header had
	 * already been set, the new value overwrites the previous one. The {@link
	 * #containsHeader} method can be used to test for the presence of a
	 * header before setting its value.
	 *
	 * @param name the name of the header to set
	 * @param value the header value
	 * @since 1.1
	 */
	public void setHeader(String name, String value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		if (null == value)      throw new IllegalArgumentException("value can't be null");

		mHeaders.setHeader(name, value);
	}
	
	/**
	 * Sets a request header with the given name and value.
	 *
	 * @param name the name of the header to set
	 * @param value the header value
	 * @see #setDateHeader
	 * @since 1.1
	 */
	public MockRequest header(String name, String value)
	{
		setHeader(name, value);
		
		return this;
	}
	
	/**
	 * Sets a request header with the given name and integer value. If the
	 * header had already been set, the new value overwrites the previous one.
	 * The containsHeader method can be used to test for the presence of a
	 * header before setting its value.
	 *
	 * @param name the name of the header to set
	 * @param value the assigned integer value
	 * @since 1.1
	 */
	public void setIntHeader(String name, int value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		mHeaders.setIntHeader(name, value);
	}
	
	/**
	 * Sets a request header with the given name and integer value.
	 *
	 * @param name the name of the header to set
	 * @param value the assigned integer value
	 * @see #setDateHeader
	 * @since 1.1
	 */
	public MockRequest intHeader(String name, int value)
	{
		setIntHeader(name, value);
		
		return this;
	}
	
	/**
	 * Removes a request header with the given name.
	 *
	 * @param name the name of the header to remove
	 * @since 1.1
	 */
	public void removeHeader(String name)
	{
		mHeaders.removeHeader(name);
	}
	
	public Locale getLocale()
	{
		if (null == mLocales ||
			0 == mLocales.size())
		{
			return Locale.getDefault();
		}
		
		return mLocales.get(0);
	}
	
	public Enumeration getLocales()
	{
		if (null == mLocales)
		{
			return Collections.enumeration(new ArrayList() {{ add(Locale.getDefault()); }});
		}
		
		return Collections.enumeration(mLocales);
	}
	
	/**
	 * Adds a {@link Locale} to this request.
	 *
	 * @param locale the locale to add
	 * @since 1.1
	 */
	public void addLocale(Locale locale)
	{
		if (null == locale)
		{
			return;
		}
		
		if (null == mLocales)
		{
			mLocales = new ArrayList<Locale>();
		}
		
		mLocales.add(locale);
	}
	
	/**
	 * Adds a {@link Locale} to this request.
	 *
	 * @param locale the locale to add
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest locale(Locale locale)
	{
		addLocale(locale);
		
		return this;
	}
	
	public String getProtocol()
	{
		return mProtocol;
	}
	
	/**
	 * Set the protocol of this request.
	 * <p>The default protocol is <code>"HTTP/1.1"</code>.
	 *
	 * @param protocol the protocol to set
	 * @since 1.1
	 */
	public void setProtocol(String protocol)
	{
		if (null == protocol)       throw new IllegalArgumentException("protocol can't be null");
		if (0 == protocol.length()) throw new IllegalArgumentException("protocol can't be empty");

		mProtocol = protocol;
	}
	
	/**
	 * Set the protocol of this request.
	 *
	 * @param protocol the protocol to set
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest protocol(String protocol)
	{
		setProtocol(protocol);
		
		return this;
	}
	
	public String getRemoteAddr()
	{
		return mRemoteAddr;
	}
	
	/**
	 * Set the remote address of this request.
	 * <p>The default remote address is "<code>127.0.0.1"</code>.
	 *
	 * @param remoteAddr the remote address to set
	 * @since 1.1
	 */
	public void setRemoteAddr(String remoteAddr)
	{
		if (null == remoteAddr)       throw new IllegalArgumentException("remoteAddr can't be null");
		if (0 == remoteAddr.length()) throw new IllegalArgumentException("remoteAddr can't be empty");

		mRemoteAddr = remoteAddr;
	}
	
	/**
	 * Set the remote address of this request.
	 *
	 * @param remoteAddr the remote address to set
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest remoteAddr(String remoteAddr)
	{
		setRemoteAddr(remoteAddr);
		
		return this;
	}
	
	public String getRemoteUser()
	{
		return mRemoteUser;
	}
	
	/**
	 * Set the remote user of this request.
	 * <p>The default remote user is <code>null</code>.
	 *
	 * @param remoteUser the remote user to set
	 * @since 1.1
	 */
	public void setRemoteUser(String remoteUser)
	{
		if (null == remoteUser)       throw new IllegalArgumentException("remoteUser can't be null");
		if (0 == remoteUser.length()) throw new IllegalArgumentException("remoteUser can't be empty");

		mRemoteUser = remoteUser;
	}
	
	/**
	 * Set the remote user of this request.
	 *
	 * @param remoteUser the remote user to set
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest remoteUser(String remoteUser)
	{
		setRemoteUser(remoteUser);
		
		return this;
	}
	
	public String getRemoteHost()
	{
		return mRemoteHost;
	}
	
	/**
	 * Set the remote host of this request.
	 * <p>The default remote host is "<code>localhost</code>".
	 *
	 * @param remoteHost the remote host to set
	 * @since 1.1
	 */
	public void setRemoteHost(String remoteHost)
	{
		if (null == remoteHost)       throw new IllegalArgumentException("remoteHost can't be null");
		if (0 == remoteHost.length()) throw new IllegalArgumentException("remoteHost can't be empty");
		
		mRemoteHost = remoteHost;
	}
	
	/**
	 * Set the remote host of this request.
	 *
	 * @param remoteHost the remote host to set
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest remoteHost(String remoteHost)
	{
		setRemoteHost(remoteHost);
		
		return this;
	}
	
	public String getScheme()
	{
		return mMockConversation.getScheme();
	}
	
	public String getServerName()
	{
		return mMockConversation.getServerName();
	}
	
	public int getServerPort()
	{
		return mMockConversation.getServerPort();
	}
	
	public String getContextPath()
	{
		return mMockConversation.getContextPath();
	}
	
	public boolean isSecure()
	{
		return mSecure;
	}
	
	/**
	 * Set whether this request is secure.
	 * <p>A request is not secure by default.
	 *
	 * @param secure <code>true</code> if this request is secure; or
	 * <p><code>false</code> otherwise
	 * @since 1.1
	 */
	public void setSecure(boolean secure)
	{
		mSecure = secure;
	}
	
	/**
	 * Set whether this request is secure.
	 *
	 * @param secure <code>true</code> if this request is secure; or
	 * <p><code>false</code> otherwise
	 * @return this <code>MockRequest</code> instance
	 * @since 1.1
	 */
	public MockRequest secure(boolean secure)
	{
		setSecure(secure);
		
		return this;
	}
	
	public HttpSession getSession(boolean create)
	{
		if (mSession != null && mSession.isValid())
		{
			return mSession;
		}
		
		mSession = null;
		
		String id = getRequestedSessionId();
		
		if (id != null)
		{
			mSession = mMockConversation.getSession(id);
			if (null == mSession && !create)
			{
				return null;
			}
		}
		
		if (mSession == null && create)
		{
			mSession = newSession();
		}
		
		return mSession;
	}
	
	public HttpSession getSession()
	{
		HttpSession session = getSession(true);
		return session;
	}
	
	void setRequestedSessionId(String pathParams)
	{
		mRequestedSessionId = null;
		
		// try cookies first
		Cookie[] cookies = getCookies();
		if (cookies != null && cookies.length > 0)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				if (MockConversation.SESSION_ID_COOKIE.equalsIgnoreCase(cookies[i].getName()))
				{
					if (mRequestedSessionId != null)
					{
						// Multiple jsessionid cookies. Probably due to
						// multiple paths and/or domains. Pick the first
						// known session or the last defined cookie.
						if (mMockConversation.getSession(mRequestedSessionId) != null)
						{
							break;
						}
					}
					
					mRequestedSessionId = cookies[i].getValue();
					mSessionIdState = SESSIONID_COOKIE;
				}
			}
		}
		
		// check if there is a url encoded session param.
		if (pathParams != null && pathParams.startsWith(MockConversation.SESSION_ID_URL))
		{
			String id = pathParams.substring(MockConversation.SESSION_ID_URL.length()+1);
			
			if (null == mRequestedSessionId)
			{
				mRequestedSessionId = id;
				mSessionIdState = SESSIONID_URL;
			}
		}
		
		if (null == mRequestedSessionId)
		{
			mSessionIdState = SESSIONID_NONE;
		}
	}
	
	String getRequestedSessionId()
	{
		return mRequestedSessionId;
	}
	
	MockSession newSession()
	{
		MockSession session = mMockConversation.newHttpSession();
		Cookie cookie = new Cookie(MockConversation.SESSION_ID_COOKIE, session.getId());
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		
		mMockResponse.addCookie(cookie);
		
		return session;
	}
	
	boolean isRequestedSessionIdFromCookie()
	{
		return SESSIONID_COOKIE.equals(mSessionIdState);
	}
	
	public RequestDispatcher getRequestDispatcher(String url)
	{
		return null;
	}
	
	public HttpServletRequest getHttpServletRequest()
	{
		return null;
	}
}
