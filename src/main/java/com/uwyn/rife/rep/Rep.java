/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.resources.ResourceFinder;

import java.util.Collection;

/**
 * This static abstract class provides easy application-wide access to the
 * default <code>Repository</code>.
 * <p>It's main purpose is to be able to quickle retrieve its
 * <code>Participant</code>s.
 * <p>It's possible to retrieve and replace the default
 * <code>Repository</code> with the {@link #setDefaultRepository(Repository)
 * setDefaultRepository} and {@link #getDefaultRepository()
 * getDefaultRepository} methods.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Participant
 * @see Repository
 * @since 1.0
 */
public abstract class Rep
{
    private static Repository defaultRepository = null;
    private static HierarchicalProperties dummyProperties = null;

    /**
     * Initializes the default repository by creating an instance of
     * <code>BlockingRepository</code> and initializing it with the provided
     * XML path, a default resource finder and no context.
     *
     * @see #initialize(String, ResourceFinder, Object)
     * @since 1.0
     */
    public static void initialize(String repXmlPath)
    {
        initialize(repXmlPath, null, null);
    }

    /**
     * Initializes the default repository by creating an instance of
     * <code>BlockingRepository</code> and initializing it with the provided
     * properties and context.
     *
     * @see BlockingRepository
     * @see BlockingRepository#initialize(String, ResourceFinder)
     * @since 1.0
     */
    public static void initialize(String repXmlPath, ResourceFinder resourcefinder, Object context)
    {
        BlockingRepository repository = new BlockingRepository(context);
        defaultRepository = repository;
        repository.initialize(repXmlPath, resourcefinder);
    }

    /**
     * Retrieves the current default repository.
     *
     * @return An instance of <code>Repository</code> that is currently the
     *         application-wide default repository.
     * @see Repository
     * @since 1.0
     */
    public static Repository getDefaultRepository()
    {
        return defaultRepository;
    }

    /**
     * Replaces the default repository.
     *
     * @param repository An instance of <code>Repository</code> that will
     *                   afterwards become the application-wide default repository.
     * @see Repository
     * @since 1.0
     */
    public static void setDefaultRepository(Repository repository)
    {
        defaultRepository = repository;
    }

    /**
     * Convenience method to quickly check if a participant with a certain
     * name is available in the default repository.
     *
     * @see Repository#hasParticipant(String)
     * @since 1.0
     */
    public static boolean hasParticipant(String name)
    {
        return null != defaultRepository && defaultRepository.hasParticipant(name);

    }

    /**
     * Convenience method to quickly retrieve the first participant with a
     * certain name from the default repository.
     *
     * @see Repository#getParticipant(String)
     * @since 1.0
     */
    public static Participant getParticipant(String name)
    {
        if (null == defaultRepository)
        {
            return null;
        }

        return defaultRepository.getParticipant(name);
    }

    /**
     * Convenience method to quickly retrieve all the participants with a
     * certain name from the default repository.
     *
     * @see Repository#getParticipants(String)
     * @since 1.0
     */
    public static Collection<? extends Participant> getParticipants(String name)
    {
        if (null == defaultRepository)
        {
            return null;
        }

        return defaultRepository.getParticipants(name);
    }

    /**
     * Convenience method to quickly retrieve the properties from the default
     * repository. If no default repository has been configured, an empty instance
     * of <code>HierarchicalProperties</code> is returned.
     *
     * @see Repository#getProperties()
     * @since 1.1
     */
    public static HierarchicalProperties getProperties()
    {
        if (null == defaultRepository)
        {
            if (null == dummyProperties)
            {
                HierarchicalProperties system_properties = new HierarchicalProperties().putAll(System.getProperties());
                dummyProperties = new HierarchicalProperties().parent(system_properties);
            }
            return dummyProperties;
        }

        return defaultRepository.getProperties();
    }

    /**
     * Cleans up the default repository if it exists. This is typically done at
     * application shutdown.
     *
     * @since 1.0
     */
    public static void cleanup()
    {
        if (null == defaultRepository)
        {
            return;
        }

        defaultRepository.cleanup();
    }
}

