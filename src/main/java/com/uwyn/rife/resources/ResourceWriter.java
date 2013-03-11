/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.resources.exceptions.ResourceWriterErrorException;

/**
 * This interface defines the methods that classes with
 * <code>ResourceWriter</code> functionalities have to implement.
 * <p/>
 * A <code>ResourceWriter</code> provides an abstract way of modifying
 * resources. According to a name, a resource and its content can be added,
 * updated or removed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see com.uwyn.rife.resources.ResourceFinder
 * @since 1.0
 */
public interface ResourceWriter
{
    /**
     * Adds a resource with the provided name and content.
     *
     * @param name    the name of the resource
     * @param content the content of the resource
     * @throws ResourceWriterErrorException if an error occurred during the
     *                                      resource addition.
     * @since 1.0
     */
    public void addResource(String name, String content) throws ResourceWriterErrorException;

    /**
     * Updates the content of the resource with the provided name.
     *
     * @param name    the name of the resource
     * @param content the content of the resource
     * @throws ResourceWriterErrorException if an error occurred during the
     *                                      resource update.
     * @since 1.0
     */
    public boolean updateResource(String name, String content) throws ResourceWriterErrorException;

    /**
     * Removes the resource with the provided name.
     *
     * @param name the name of the resource
     * @throws ResourceWriterErrorException if an error occurred during the
     *                                      resource removal.
     * @since 1.0
     */
    public boolean removeResource(String name) throws ResourceWriterErrorException;
}
