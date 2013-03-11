/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractResponse.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.EmbeddedElementCantSetContentLengthException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.ResponseOutputStreamRetrievalErrorException;
import com.uwyn.rife.template.InternalString;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.HttpUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;

/**
 * This abstract class implements parts of the {@link Response} interface to
 * provide behaviour that is specific to RIFE.
 * <p>Additional abstract methods have been provided to integrate with the
 * concrete back-end classes that extend <code>AbstractResponse</code>.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public abstract class AbstractResponse implements Response
{
	private Request                 mRequest = null;
	private boolean                 mEmbedded = false;
	private ElementSupport          mLastElement = null;
	private String                  mContentType = null;
	private boolean                 mTextBufferEnabled = true;
	private ArrayList<CharSequence> mTextBuffer = null;
	private OutputStream            mResponseOutputStream = null;
	private ByteArrayOutputStream   mGzipByteOutputStream = null;
	private GZIPOutputStream        mGzipOutputStream = null;
	private OutputStream            mOutputStream = null;
	
	/**
	 * This method needs to be implemented by the extending back-end class and
	 * will be called by <code>AbstractResponse</code> during the
	 * RIFE-specific additional behaviour. It behaves exactly like its {@link
	 * Response#setContentType(String) counter-part in the Response interface}.
	 *
	 * @see Response#setContentType(String)
	 * @since 1.1
	 */
	protected abstract void _setContentType(String contentType);

	/**
	 * This method needs to be implemented by the extending back-end class and
	 * will be called by <code>AbstractResponse</code> during the
	 * RIFE-specific additional behaviour. It behaves exactly like its {@link
	 * Response#getCharacterEncoding() counter-part in the Response interface}.
	 *
	 * @see Response#getCharacterEncoding()
	 * @since 1.1
	 */
	protected abstract String _getCharacterEncoding();

	/**
	 * This method needs to be implemented by the extending back-end class and
	 * will be called by <code>AbstractResponse</code> during the
	 * RIFE-specific additional behaviour. It behaves exactly like its {@link
	 * Response#setContentLength(int) counter-part in the Response interface}.
	 *
	 * @see Response#setContentLength(int)
	 * @since 1.1
	 */
	protected abstract void _setContentLength(int length);

	/**
	 * This method needs to be implemented by the extending back-end class and
	 * will be called by <code>AbstractResponse</code> during the
	 * RIFE-specific additional behaviour. It behaves exactly like its {@link
	 * Response#sendRedirect(String) counter-part in the Response interface}.
	 *
	 * @see Response#sendRedirect(String)
	 * @since 1.1
	 */
	protected abstract void _sendRedirect(String location);

	/**
	 * This method needs to be implemented by the extending back-end class and
	 * will be called by <code>AbstractResponse</code> during the
	 * RIFE-specific additional behaviour. It behaves exactly like its {@link
	 * Response#getOutputStream() counter-part in the Request interface}.
	 *
	 * @see Response#getOutputStream()
	 * @since 1.1
	 */
	protected abstract OutputStream _getOutputStream() throws IOException;
	
	/**
	 * Constructor that needs to be called by all the constructors of the
	 * extending classes.
	 *
	 * @param request the {@link Request} that is associated with this
	 * response
	 * @param embedded <code>true</code> if the response is embedded; or
	 * <p><code>false</code> otherwise
	 * @since 1.1
	 */
	protected AbstractResponse(Request request, boolean embedded)
	{
		mRequest = request;
		mEmbedded = embedded;
	}
	
	/**
	 * Retrieves the request that is associated with this response.
	 *
	 * @return the associated request
	 * @since 1.1
	 */
	public Request getRequest()
	{
		return mRequest;
	}
	
	/**
	 * Retrieves the last element that has been processed with this response.
	 *
	 * @return the last processed element
	 * @since 1.1
	 */
	public ElementSupport getLastElement()
	{
		return mLastElement;
	}
	
	public void setLastElement(ElementSupport element)
	{
		mLastElement = element;
	}
	
	public ArrayList<CharSequence> getEmbeddedContent()
	{
		if (!mEmbedded)
		{
			return null;
		}
		
		if (null == mOutputStream)
		{
			return mTextBuffer;
		}
		
		flush();
		
		return ((EmbeddedStream)mOutputStream).getEmbeddedContent();
	}
	
	public boolean isEmbedded()
	{
		return mEmbedded;
	}
	
	public boolean isContentTypeSet()
	{
		return mContentType != null;
	}
	
	public String getContentType()
	{
		return mContentType;
	}

	public void setContentType(String contentType)
	{
		if (mEmbedded)
		{
			return;
		}
		
		if (null == contentType)
		{
			return;
		}
		
		if (-1 == contentType.indexOf(HttpUtils.CHARSET))
		{
			contentType = contentType+"; charset=UTF-8";
		}
		
		mContentType = contentType;
		_setContentType(contentType);
	}
	
	public void enableTextBuffer(boolean enabled)
	{
		if (mTextBufferEnabled != enabled)
		{
			flush();
		}
		
		mTextBufferEnabled = enabled;
	}
	
	public boolean isTextBufferEnabled()
	{
		return mTextBufferEnabled;
	}
	
	public void print(Template template)
	throws EngineException
	{
		if (null == template) return;
		
		print(template.getDeferredContent());
	}
	
	public void print(Collection<CharSequence> deferredContent)
	throws EngineException
	{
		if (!isContentTypeSet())
		{
			setContentType(RifeConfig.Engine.getDefaultContentType());
		}
		
		if (null == deferredContent ||
			0 == deferredContent.size())
		{
			return;
		}
		
		if (mTextBufferEnabled)
		{
			if (mOutputStream != null)
			{
				try
				{
					mOutputStream.flush();
				}
				catch (IOException e)
				{
					throw new EngineException(e);
				}
			}
			
			if (null == mTextBuffer)
			{
				mTextBuffer = new ArrayList<CharSequence>();
			}
			mTextBuffer.addAll(deferredContent);
		}
		else
		{
			writeDeferredContent(deferredContent);
		}
	}
	
	public void print(Object value)
	throws EngineException
	{
		if (!isContentTypeSet())
		{
			setContentType(RifeConfig.Engine.getDefaultContentType());
		}
		
		if (null == value)
		{
			return;
		}
		
		String text = String.valueOf(value);
		
		if (mTextBufferEnabled)
		{
			if (mOutputStream != null)
			{
				try
				{
					mOutputStream.flush();
				}
				catch (IOException e)
				{
					throw new EngineException(e);
				}
			}
			
			if (null == mTextBuffer)
			{
				mTextBuffer = new ArrayList<CharSequence>();
			}
			mTextBuffer.add(text);
		}
		else
		{
			ensureOutputStream();
			
			try
			{
				mOutputStream.write(text.getBytes(getCharacterEncoding()));
				mOutputStream.flush();
			}
			catch (IOException e)
			{
				throw new EngineException(e);
			}
		}
	}
	
	public String getCharacterEncoding()
	{
		if (mEmbedded)
		{
			return RifeConfig.Engine.getResponseEncoding();
		}
		
		String encoding = _getCharacterEncoding();
		if (encoding == null)
		{
			encoding = RifeConfig.Engine.getResponseEncoding();
		}
		
		return encoding;
	}
	
	private void writeDeferredContent(Collection<CharSequence> deferredContent)
	throws EngineException
	{
		if (!mEmbedded)
		{
			// create a string version of each char sequence so that any state operation happens
			// before any content is actually being written
			for (CharSequence charsequence : deferredContent)
			{
				charsequence.toString();
			}
		}
		
		ensureOutputStream();
		
		String encoding = getCharacterEncoding();
		try
		{
			mOutputStream.flush();

			if (mEmbedded)
			{
				EmbeddedStream embedded_stream = (EmbeddedStream)mOutputStream;
				for (CharSequence charsequence : deferredContent)
				{
					embedded_stream.write(charsequence);
				}
			}
			else
			{
				// write the content to the output stream
				for (CharSequence charsequence : deferredContent)
				{
					if (charsequence instanceof com.uwyn.rife.template.InternalString)
					{
						mOutputStream.write(((InternalString)charsequence).getBytes(encoding));
					}
					else if (charsequence instanceof java.lang.String)
					{
						mOutputStream.write(((String)charsequence).getBytes(encoding));
					}
				}
			}
			
			mOutputStream.flush();
		}
		catch (IOException e)
		{
			// don't do anything since this exception is merely caused by someone that
			// stopped or closed his browsing request
		}
	}
	
	public void clearBuffer()
	{
		if (mTextBuffer != null &&
			mTextBuffer.size() > 0)
		{
			mTextBuffer.clear();
		}
	}
	
	public void flush()
	throws EngineException
	{
		if (mTextBuffer != null &&
			mTextBuffer.size() > 0)
		{
			writeDeferredContent(mTextBuffer);
			
			mTextBuffer.clear();
		}

		if (mOutputStream != null)
		{
			try
			{
				mOutputStream.flush();
			}
			catch (IOException e)
			{
				// don't do anything, the response stream has probably been
				// closed or reset
			}
		}
	}
	
	public void close()
	throws EngineException
	{
		flush();

		if (!mEmbedded &&
			mOutputStream != null)
		{
			try
			{
				if (mGzipOutputStream != null)
				{
					mGzipOutputStream.flush();
					mGzipOutputStream.finish();
					
					byte[] bytes = mGzipByteOutputStream.toByteArray();
					
					mGzipOutputStream = null;
					mGzipByteOutputStream = null;
					
					setContentLength(bytes.length);
					addHeader("Content-Encoding", "gzip");
					mResponseOutputStream.write(bytes);
					mOutputStream = mResponseOutputStream;
				}
			
				try
				{
					mOutputStream.flush();
					mOutputStream.close();
				}
				catch (IOException e)
				{
					// don't do anything, the response stream has probably been
					// closed or reset
				}
				
				mOutputStream = null;
			}
			catch (IOException e)
			{
				// don't do anything, the response stream has probably been
				// closed or reset
			}
		}
	}
	
	public OutputStream getOutputStream()
	throws EngineException
	{
		ensureOutputStream();
		
		return mOutputStream;
	}
	
	public void setContentLength(int length)
	throws EngineException
	{
		assert length >= 0;
		
		if (mEmbedded)
		{
			throw new EmbeddedElementCantSetContentLengthException();
		}
		
		_setContentLength(length);
	}
	
	public void sendRedirect(String location)
	throws EngineException
	{
		String user_agent = mRequest.getHeader("User-Agent");
		// dirty hack around an IE bug that incorrectly handles anchors in redirect headers
		if (user_agent != null &&
			location.indexOf("#") != -1 &&
			user_agent.indexOf("MSIE") != -1)
		{
			setHeader("Refresh", "0; URL="+location);
		}
		else
		{
			_sendRedirect(location);
		}
	}
	
	private void ensureOutputStream()
	throws EngineException
	{
		if (null == mOutputStream)
		{
			if (mEmbedded)
			{
				mOutputStream = new EmbeddedStream();
			}
			else
			{
				if (null == mResponseOutputStream)
				{
					try
					{
						mResponseOutputStream = _getOutputStream();
						
						if (mContentType != null)
						{
							String content_type = HttpUtils.extractMimeTypeFromContentType(mContentType);
							
							// check if the content type should be gzip encoded
							if (RifeConfig.Engine.getGzipCompression() &&
								RifeConfig.Engine.getGzipCompressionTypes().contains(content_type))
							{
								String accept_encoding = mRequest.getHeader("Accept-Encoding");
								if (accept_encoding != null &&
									accept_encoding.indexOf("gzip") != -1)
								{
									mGzipByteOutputStream = new ByteArrayOutputStream();
									mGzipOutputStream = new GZIPOutputStream(mGzipByteOutputStream);
								}
							}
						}
					}
					catch (IOException e)
					{
						throw new ResponseOutputStreamRetrievalErrorException(e);
					}
				}
				
				if (mGzipOutputStream != null)
				{
					mOutputStream = mGzipOutputStream;
				}
				else
				{
					mOutputStream = mResponseOutputStream;
				}
			}
		}
	}
}
