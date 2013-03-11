/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Response.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.template.Template;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface contains all the methods that the web engine needs to be
 * able to send a response to the client.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public interface Response
{
	/**
	 * Creates a new <code>Response</code> instance that will be used for
	 * embedded elements.
	 *
	 * @param valueId the template value in which the embedded element will
	 * be processed
	 * @param differentiator the embedded element differentiator
	 * @return the new <code>Response</code> instance for embedded use
	 * @since 1.5
	 */
	public Response createEmbeddedResponse(String valueId, String differentiator);

	/**
	 * Retrieves the content of the response for embedded usage and
	 * integration into another response.
	 *
	 * @return the embedded content; or
	 * <p><code>null</code> if the response is not embedded.
	 * @since 1.1
	 */
	public ArrayList<CharSequence> getEmbeddedContent();

	/**
	 * Indicates whether this response is embedded into another response.
	 *
	 * @return <code>true</code> if the response is embedded; or
	 * <p><code>false</code> otherwise.
	 * @since 1.1
	 */
	public boolean isEmbedded();

	/**
	 * Sets the latest target element of this response. This method is called
	 * repeatedly by the engine to make it possible to trace which elements
	 * have been processed.
	 *
	 * @param element an element that has been executed in the context of this
	 * response
	 * @since 1.1
	 */
	public void setLastElement(ElementSupport element);

	/**
	 * Enables or disabled the text output buffer.
	 * <p>The buffer is enabled by default and its buffered contents will be
	 * flushed when the buffer is disabled.
	 *
	 * @param enabled <code>true</code> to enable the text buffer; or
	 * <p><code>false</code> to disable it
	 * @since 1.1
	 */
	public void enableTextBuffer(boolean enabled);

	/**
	 * Indicates whether the text output buffer is enabled.
	 *
	 * @return <code>true</code> when the text buffer is enabled; or
	 * <p><code>false</code> when it is disabled.
	 * @since 1.1
	 */
	public boolean isTextBufferEnabled();

	/**
	 * Prints the content of a template to the request text output.
	 *
	 * @param template the template that will be printed
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the output of the template content
	 * @see #print(Collection)
	 * @see #print(Object)
	 * @since 1.1
	 */
	public void print(Template template) throws EngineException;

	/**
	 * Prints a list of text snippets to the request text output.
	 *
	 * @param deferredContent the text snippets that will be printed
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the output of the text snippets
	 * @see #print(Template)
	 * @see #print(Object)
	 * @since 1.1
	 */
	public void print(Collection<CharSequence> deferredContent) throws EngineException;

	/**
	 * Prints the string representation of an object to the request text
	 * output. The string representation will be created through a
	 * <code>String.valueOf(value)</code> call.
	 *
	 * @param value the object that will be output
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the output of the content
	 * @see #print(Template)
	 * @see #print(Collection)
	 * @since 1.1
	 */
	public void print(Object value) throws EngineException;

	/**
	 * Clears the text buffer is it's enabled.
	 * <p>If the text buffer is disabled, this method does nothing.
	 *
	 * @since 1.1
	 */
	public void clearBuffer();

	/**
	 * Forces all the streamed content to be output to the client.
	 * <p>If the text buffer is enabled, this will flush its content to the
	 * output stream first.
	 *
	 * @since 1.1
	 */
	public void flush() throws EngineException;

	/**
	 * Closed the content output stream.
	 * <p>All content is {@link #flush flushed} first.
	 *
	 * @since 1.1
	 */
	public void close() throws EngineException;

	/**
	 * See {@link HttpServletResponse#getOutputStream()}.
	 *
	 * @since 1.1
	 */
	public OutputStream getOutputStream() throws EngineException;

	/**
	 * See {@link HttpServletResponse#getWriter()}.
	 *
	 * @since 1.1
	 */
	public PrintWriter getWriter() throws IOException;

	/**
	 * See {@link HttpServletResponse#setContentType(String)}.
	 *
	 * @since 1.1
	 */
	public void setContentType(String contentType);

	/**
	 * Indicates whether this response's content type has been explicitly
	 * set.
	 *
	 * @return <code>true</code> if it has been set; or
	 * <p><code>false</code> otherwise
	 *
	 * @see #setContentType
	 * @since 1.3
	 */
	public boolean isContentTypeSet();
	
	/**
	 * Retrieves the content type that was explicitly set for this response.
	 *
	 * @return the content type as a String; or
	 * <p><code>null</code> if the content type wasn't set
	 *
	 * @see #setContentType
	 * @since 1.5.1
	 */
	public String getContentType();

	/**
	 * See {@link HttpServletResponse#setLocale(Locale)}.
	 *
	 * @since 1.1
	 */
	public void setLocale(Locale locale);

	/**
	 * See {@link HttpServletResponse#getLocale()}.
	 *
	 * @since 1.1
	 */
	public Locale getLocale();

	/**
	 * See {@link HttpServletResponse#getCharacterEncoding()}.
	 *
	 * @since 1.1
	 */
	public String getCharacterEncoding();

	/**
	 * See {@link HttpServletResponse#setContentLength(int)}.
	 *
	 * @since 1.1
	 */
	public void setContentLength(int length) throws EngineException;

	/**
	 * See {@link HttpServletResponse#addCookie(Cookie)}.
	 *
	 * @since 1.1
	 */
	public void addCookie(Cookie cookie);

	/**
	 * See {@link HttpServletResponse#addHeader(String, String)}.
	 *
	 * @since 1.1
	 */
	public void addHeader(String name, String value);

	/**
	 * See {@link HttpServletResponse#addDateHeader(String, long)}.
	 *
	 * @since 1.1
	 */
	public void addDateHeader(String name, long date);

	/**
	 * See {@link HttpServletResponse#addIntHeader(String, int)}.
	 *
	 * @since 1.1
	 */
	public void addIntHeader(String name, int integer);

	/**
	 * See {@link HttpServletResponse#containsHeader(String)}.
	 *
	 * @since 1.1
	 */
	public boolean containsHeader(String name);

	/**
	 * See {@link HttpServletResponse#sendError(int)}.
	 *
	 * @since 1.1
	 */
	public void sendError(int statusCode) throws EngineException;

	/**
	 * See {@link HttpServletResponse#sendError(int, String)}.
	 *
	 * @since 1.1
	 */
	public void sendError(int statusCode, String message) throws EngineException;

	/**
	 * See {@link HttpServletResponse#sendRedirect(String)}.
	 *
	 * @since 1.1
	 */
	public void sendRedirect(String location) throws EngineException;

	/**
	 * See {@link HttpServletResponse#setDateHeader(String, long)}.
	 *
	 * @since 1.1
	 */
	public void setDateHeader(String name, long date);

	/**
	 * See {@link HttpServletResponse#setHeader(String, String)}.
	 *
	 * @since 1.1
	 */
	public void setHeader(String name, String value);

	/**
	 * See {@link HttpServletResponse#setIntHeader(String, int)}.
	 *
	 * @since 1.1
	 */
	public void setIntHeader(String name, int value);

	/**
	 * See {@link HttpServletResponse#setStatus(int)}.
	 *
	 * @since 1.1
	 */
	public void setStatus(int statusCode);

	/**
	 * See {@link HttpServletResponse#encodeURL(String)}.
	 *
	 * @since 1.1
	 */
	public String encodeURL(String url);

	/**
	 * Retrieves the underlying {@link HttpServletResponse}.
	 *
	 * @return the underlying <code>HttpServletResponse</code> instance; or
	 * <p><code>null</code> if this response isn't backed by
	 * <code>HttpServletResponse</code>
	 * @since 1.1
	 */
	public HttpServletResponse getHttpServletResponse();
}
