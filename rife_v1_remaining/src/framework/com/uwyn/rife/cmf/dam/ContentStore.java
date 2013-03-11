/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentStore.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.format.Formatter;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.engine.ElementSupport;
import java.util.Collection;

/**
 * A <code>ContentStore</code> stores the actual content data and is
 * responsible for managing it.
 * <p>The store doesn't work with paths, but with content ids. Each id
 * identifies a specific content instance at a certain location and with a
 * certain version number.
 * <p>Each store is only capable of storing content with certain mime types.
 * The store is optimized for a certain kind of content and will maybe not be
 * able to correctly handle other types.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)i
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface ContentStore
{
	/**
	 * Installs a content store.
	 * 
	 * @return <code>true</code> if the installation was successful; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean install() throws ContentManagerException;
	/**
	 * Removes a content store.
	 * 
	 * @return <code>true</code> if the removal was successful; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean remove() throws ContentManagerException;
	/**
	 * Returns the collection of mime types that the content store supports.
	 * 
	 * @return the collection of supported mime types
	 * @since 1.0
	 */
	public Collection<MimeType> getSupportedMimeTypes();
	/**
	 * Generates the HTTP content type that corresponds best to the
	 * information in the provided <code>ContentInfo</code>.
	 * 
	 * @param contentInfo the content info instance for which the content type
	 * has to be generated
	 * @return the generated content type
	 * @since 1.0
	 */
	public String getContentType(ContentInfo contentInfo);
	/**
	 * Returns a <code>Formatter</code> instance that will be used to load and
	 * to format the content data.
	 * 
	 * @param mimeType the mime type for which the formatter will be returned
	 * @param fragment <code>true</code> if the content that has to be
	 * formatter is a fragment; or
	 * <p><code>false</code> otherwise
	 * @return the corresponding formatter
	 * @since 1.0
	 */
	public Formatter getFormatter(MimeType mimeType, boolean fragment);
	/**
	 * Stores the content data for a certain content id.
	 * 
	 * @param id the id of the content whose data will be stored
	 * @param content the content whose data has to be stored
	 * @param transformer a transformer that will modify the content data; or
	 * <p><code>null</code> if the content data should stay intact
	 * @return <code>true</code> if the storing was successfully; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean storeContentData(int id, Content content, ContentTransformer transformer) throws ContentManagerException;
	/**
	 * Deletes the content data for a certain content id.
	 * 
	 * @param id the id of the content whose data will be deleted
	 * @return <code>true</code> if the deletion was successfully; or
	 * <p><code>false</code> if it wasn't.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public boolean deleteContentData(int id) throws ContentManagerException;
	/**
	 * Use the data of a certain content id.
	 * <p>Some content data will only be available during this method call due
	 * to their volatile nature (certain streams for instance). Therefore, one
	 * has to be careful when trying to move the data that is provided to the
	 * content user outside this method. The behaviour is undefined.
	 * 
	 * @param id the id of the content whose data will be used
	 * @param user the content user instance that will be called to use
	 * content data
	 * @return the data that the {@link ContentDataUser#useContentData(Object)}
	 * returns after its usage
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public <ResultType> ResultType useContentData(int id, ContentDataUser user) throws ContentManagerException;
	/**
	 * Checks whether content data is available for a certain content id.
	 * 
	 * @param id the id of the content whose data availability will be checked
	 * @return <code>true</code> if content data is available; or
	 * <p><code>false</code> if it isn't.
	 * @exception ContentManagerException if an expected error occurred
	 * @since 1.0
	 */
	public boolean hasContentData(int id) throws ContentManagerException;
	/**
	 * Retrieves the size of the content data for a certain content id.
	 * <p>Note that the result is specific to the data store. For instance,
	 * text data could return the number of characters, while image data could
	 * return the number of bytes.
	 * 
	 * @param id the id of the content whose data size will be returned
	 * @return <code>-1</code> if no data is available for the provided
	 * content id; or
	 * <p>the requested content data size.
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public int getSize(int id) throws ContentManagerException;
	/**
	 * Serves content data for a certain content id through the provided
	 * element.
	 * <p>This is intended to take over the complete handling of the request,
	 * so no other content should be output and no headers manipulated in the
	 * element if this method is called.
	 * 
	 * @param element an active element instance
	 * @param id the id of the content whose data will be served
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public void serveContentData(ElementSupport element, int id) throws ContentManagerException;
	/**
	 * Retrieves a content data representation for use in html.
	 * <p>This is mainly used to integrate content data inside a html
	 * document. For instance, html content will be displayed as-is, while
	 * image content will cause an image tag to be generated with the correct
	 * source URL to serve the image.
	 * 
	 * @param id the id of the content whose data will be displayed
	 * @param info the content info instance for which the html content
	 * has to be generated
	 * @param element an active element instance
	 * @param serveContentExitName the exit namet that leads to a {@link
	 * com.uwyn.rife.cmf.elements.ServeContent ServeContent} element. This will
	 * be used to generate URLs for content that can't be directly displayed
	 * in-line.
	 * @return the html content representation
	 * @exception ContentManagerException if an unexpected error occurred
	 * @since 1.0
	 */
	public String getContentForHtml(int id, ContentInfo info, ElementSupport element, String serveContentExitName) throws ContentManagerException;
}
