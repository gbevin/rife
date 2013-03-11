/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.engine.ElementSupport;

/**
 * A <code>ContentManager</code> manages content that is stored in a back-end
 * data store.
 * <p>Content is isolated in repositories that should have unique names. The
 * installation of a content manager creates an initial default repository. If
 * others are needed, they have to be created explicitly.
 * <p>All content is identified by a unique <code>location</code>. The
 * location is formatted like this:
 * <pre>repository:path</pre>
 * <p>If the <code>repository:</code> prefix is omitted, the content will be
 * stored in the default repository (see {@link
 * com.uwyn.rife.cmf.ContentRepository#DEFAULT ContentRepository.DEFAULT}).
 * <p>The path should start with a slash that makes it 'absolute', this is
 * completely analogue to file system paths.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)i
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface ContentManager
{
	/**
	 * Installs a content manager.
	 * 
	 * @return <code>true</code> if the installation was successful; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean install() throws ContentManagerException;

	/**
	 * Removes a content manager.
	 * 
	 * @return <code>true</code> if the removal was successful; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean remove() throws ContentManagerException;

	/**
	 * Creates a new repository.
	 * 
	 * @param name the name of the repository to create
	 * @return <code>true</code> if the creation was successful; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean createRepository(String name) throws ContentManagerException;
	
	/**
	 * Checks if the content manager contains a certain repository.
	 * 
	 * @param name the name of the repository to check
	 * @return <code>true</code> if the repository exists; or
	 * <p><code>false</code> if it doesn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.4
	 */
	public boolean containsRepository(String name) throws ContentManagerException;
	
	/**
	 * Store content at a certain location.
	 * <p>If content is already present at this location, the new content will
	 * become the current version and the old content remains available as an
	 * older version.
	 * 
	 * @param location the location where the content has to be stored.
	 * @param content the content that has to be stored
	 * @param transformer a transformer that will modify the content data; or
	 * <p><code>null</code> if the content data should stay intact
	 * @return <code>true</code> if the storing was successfully; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean storeContent(String location, Content content, ContentTransformer transformer) throws ContentManagerException;

	/**
	 * Delete the content at a certain location.
	 * <p>This will delete all versions of the content at that location.
	 * 
	 * @param location the location where the content has to be deleted
	 * @return <code>true</code> if the deletion was successfully; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean deleteContent(String location) throws ContentManagerException;

	/**
	 * Use the data of content at a certain location.
	 * <p>Some content data will only be available during this method call due
	 * to their volatile nature (certain streams for instance). Therefore, one
	 * has to be careful when trying to move the data that is provided to the
	 * content user outside this method. The behaviour is undefined.
	 * 
	 * @param location the location whose content will be used
	 * @param user the content user instance that will be called to use
	 * content data
	 * @return the data that the {@link ContentDataUser#useContentData(Object)}
	 * returns after its usage
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public <ResultType> ResultType useContentData(String location, ContentDataUser user) throws ContentManagerException;

	/**
	 * Checks whether content data is available at a certain location.
	 * 
	 * @param location the location that has to be checked
	 * @return <code>true</code> if content data is available; or
	 * <p><code>false</code> if it isn't.
	 * @exception ContentManagerException if an expected error occurred
	 * @since 1.0
	 */
	public boolean hasContentData(String location) throws ContentManagerException;

	/**
	 * Retrieves the content info from a certain location.
	 * 
	 * @param location the location whose content info has to be retrieved
	 * @return an instance of <code>ContentInfo</code>; or
	 * <p><code>null</code> if no content is present at the location
	 * @exception ContentManagerException if an expected error occurred
	 * @since 1.0
	 */
	public ContentInfo getContentInfo(String location) throws ContentManagerException;

	/**
	 * Serves content data from a certain location through the provided
	 * element.
	 * <p>This is intended to take over the complete handling of the request,
	 * so no other content should be output and no headers manipulated in the
	 * element if this method is called.
	 * 
	 * @param element an active element instance
	 * @param location the location whose content data has to be served
	 * @exception ContentManagerException if an expected error occurred
	 * @since 1.0
	 */
	public void serveContentData(ElementSupport element, String location) throws ContentManagerException;

	/**
	 * Retrieves a content representation for use in html.
	 * <p>This is mainly used to integrate content data inside a html
	 * document. For instance, html content will be displayed as-is, while
	 * image content will cause an image tag to be generated with the correct
	 * source URL to serve the image.
	 * 
	 * @param location the location whose content will be displayed
	 * @param element an active element instance
	 * @param serveContentExitName the exit namet that leads to a {@link
	 * com.uwyn.rife.cmf.elements.ServeContent ServeContent} element. This will
	 * be used to generate URLs for content that can't be directly displayed
	 * in-line.
	 * @exception ContentManagerException if an expected error occurred
	 * @since 1.0
	 */
	public String getContentForHtml(String location, ElementSupport element, String serveContentExitName) throws ContentManagerException;
}
