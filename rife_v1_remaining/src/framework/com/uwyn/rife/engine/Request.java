/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Request.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This interface contains all the methods that the web engine needs to be
 * able to correctly handle incoming requests.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public interface Request
{
	/**
	 * Initialize the state of this request from a <code>StateStore</code>.
	 * <p>This method is only there to be used by the web engine, it should
	 * never be called manually.
	 * 
	 * @param stateStore the <code>StateStore</code> that will be used to
	 * initialize the request
	 * @since 1.1
	 */
	public void init(StateStore stateStore);
	
	/**
	 * See {@link HttpServletRequest#getMethod()}.
	 * 
	 * @since 1.1
	 */
	public RequestMethod getMethod();
	
	/**
	 * Retrieves the parameters that were sent in this request.
	 * 
	 * @return a <code>Map</code> with all the parameter names and values
	 * @since 1.1
	 */
	public Map<String, String[]> getParameters();
	
	/**
	 * Retrieves the files that were uploaded in this request.
	 * 
	 * @return a <code>Map</code> with all the uploaded files
	 * @see #hasFile(String)
	 * @see #getFile(String)
	 * @see #getFiles(String)
	 * @since 1.1
	 */
	public Map<String, UploadedFile[]> getFiles();

	/**
	 * Checks if a particular file has been uploaded in this request.
	 * 
	 * @param name the name of the file, as declared in the submission
	 * @return <code>true</code> if the file was uploaded; or
	 * <p><code>false</code> otherwise
	 * @see #getFiles()
	 * @see #getFile(String)
	 * @see #getFiles(String)
	 * @since 1.1
	 */
	public boolean hasFile(String name);

	/**
	 * Retrieves an uploaded file.
	 * 
	 * @param name the name of the file, as declared in the submission
	 * @return the uploaded file; or
	 * <p><code>null</code> if no file was uploaded
	 * @see #getFiles()
	 * @see #hasFile(String)
	 * @see #getFiles(String)
	 * @since 1.1
	 */
	public UploadedFile getFile(String name);

	/**
	 * Retrieves all files that have been uploaded for a particular name.
	 * 
	 * @param name the name of the file, as declared in the submission
	 * @return the uploaded files; or
	 * <p><code>null</code> if no files were uploaded for that name
	 * @see #getFiles()
	 * @see #hasFile(String)
	 * @see #getFile(String)
	 * @since 1.1
	 */
	public UploadedFile[] getFiles(String name);

	/**
	 * Returns the root URL of the server that is running this web
	 * applications.
	 * <p>This includes the protocol, the server name and the server port, for
	 * example: <code>http://www.somehost.com:8080</code>.
	 * 
	 * @return the server's root url
	 * @since 1.1
	 */
	public String getServerRootUrl(int port);

	/**
	 * Checks whether a cookie is present.
	 * 
	 * @param name the name of the cookie
	 * @return <code>true</code> if the cookie was present; or
	 * <p><code>false</code> otherwise
	 * @see #getCookie(String)
	 * @see #getCookies()
	 * @since 1.1
	 */
	public boolean hasCookie(String name);
	
	/**
	 * Retrieves a cookie.
	 * 
	 * @param name the name of the cookie.
	 * @return the instance of the cookie; or
	 * <p><code>null</code> if no such cookie is present
	 * @see #hasCookie(String)
	 * @see #getCookies()
	 * @since 1.1
	 */
	public Cookie getCookie(String name);
	
	/**
	 * See {@link HttpServletRequest#getCookies()}.
	 * 
	 * @since 1.1
	 */
	public Cookie[] getCookies();
	
	/**
	 * See {@link HttpServletRequest#getAttribute(String)}.
	 * 
	 * @since 1.1
	 */
	public Object getAttribute(String name);

	/**
	 * Checks if a request attribute exists.
	 * 
	 * @param name a <code>String</code> specifying the name of the attribute
	 * <p><code>false</code> otherwise
	 * @since 1.1
	 */
	public boolean hasAttribute(String name);

	/**
	 * See {@link HttpServletRequest#getAttributeNames()}.
	 * 
	 * @since 1.1
	 */
	public Enumeration getAttributeNames();

	/**
	 * See {@link HttpServletRequest#removeAttribute(String)}.
	 * 
	 * @since 1.1
	 */
	public void removeAttribute(String name);

	/**
	 * See {@link HttpServletRequest#setAttribute(String, Object)}.
	 * 
	 * @since 1.1
	 */
	public void setAttribute(String name, Object object);

	/**
	 * See {@link HttpServletRequest#getCharacterEncoding()}.
	 * 
	 * @since 1.1
	 */
	public String getCharacterEncoding();

	/**
	 * See {@link HttpServletRequest#getContentType()}.
	 * 
	 * @since 1.1
	 */
	public String getContentType();

	/**
	 * See {@link HttpServletRequest#getDateHeader(String)}.
	 * 
	 * @since 1.1
	 */
	public long getDateHeader(String name);

	/**
	 * See {@link HttpServletRequest#getHeader(String)}.
	 * 
	 * @since 1.1
	 */
	public String getHeader(String name);
	
	/**
	 * See {@link HttpServletRequest#getHeaderNames()}.
	 * 
	 * @since 1.1
	 */
	public Enumeration getHeaderNames();
	
	/**
	 * See {@link HttpServletRequest#getHeaders(String)}.
	 * 
	 * @since 1.1
	 */
	public Enumeration getHeaders(String name);
	
	/**
	 * See {@link HttpServletRequest#getIntHeader(String)}.
	 * 
	 * @since 1.1
	 */
	public int getIntHeader(String name);
	
	/**
	 * See {@link HttpServletRequest#getLocale()}.
	 * 
	 * @since 1.1
	 */
	public Locale getLocale();
	
	/**
	 * See {@link HttpServletRequest#getLocales()}.
	 * 
	 * @since 1.1
	 */
	public Enumeration getLocales();
	
	/**
	 * See {@link HttpServletRequest#getProtocol()}.
	 * 
	 * @since 1.1
	 */
	public String getProtocol();
	
	/**
	 * See {@link HttpServletRequest#getRemoteAddr()}.
	 * 
	 * @since 1.1
	 */
	public String getRemoteAddr();
	
	/**
	 * See {@link HttpServletRequest#getRemoteUser()}.
	 * 
	 * @since 1.1
	 */
	public String getRemoteUser();
	
	/**
	 * See {@link HttpServletRequest#getRemoteHost()}.
	 * 
	 * @since 1.1
	 */
	public String getRemoteHost();
	
	/**
	 * See {@link HttpServletRequest#getRequestDispatcher(String)}.
	 * 
	 * @since 1.1
	 */
	public RequestDispatcher getRequestDispatcher(String url);
	
	/**
	 * See {@link HttpServletRequest#getSession()}.
	 * 
	 * @since 1.1
	 */
	public HttpSession getSession();
	
	/**
	 * See {@link HttpServletRequest#getSession(boolean)}.
	 * 
	 * @since 1.1
	 */
	public HttpSession getSession(boolean create);
	
	/**
	 * See {@link HttpServletRequest#getServerPort()}.
	 * 
	 * @since 1.1
	 */
	public int getServerPort();
	
	/**
	 * See {@link HttpServletRequest#getScheme()}.
	 * 
	 * @since 1.1
	 */
	public String getScheme();
	
	/**
	 * See {@link HttpServletRequest#getServerName()}.
	 * 
	 * @since 1.1
	 */
	public String getServerName();
	
	/**
	 * See {@link HttpServletRequest#getContextPath()}.
	 * 
	 * @since 1.1
	 */
	public String getContextPath();
	
	/**
	 * See {@link HttpServletRequest#isSecure()}.
	 * 
	 * @since 1.1
	 */
	public boolean isSecure();

	/**
	 * Retrieves the underlying {@link HttpServletRequest}.
	 * 
	 * @return the underlying <code>HttpServletRequest</code> instance; or
	 * <p><code>null</code> if this request isn't backed by
	 * <code>HttpServletRequest</code>
	 * @since 1.1
	 */
	public HttpServletRequest getHttpServletRequest();
}
