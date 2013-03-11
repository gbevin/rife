/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.ioc.HierarchicalProperties;

import java.util.Collection;

/**
 * The <code>Repository</code> provides a collection of application-wide data
 * structures and services that typically setup the whole application
 * structure in a modular fashion.
 * <p>These modules are called <code>Participant</code>s and they are
 * registered through a name so make it possible to retrieve them. The name is
 * not necessarily a unique identifier, but can also identify a specific
 * <code>Participant</code> type.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Participant
 * @since 1.0
 */
public interface Repository
{
    /**
     * Verifies if a participant with a certain name is present in the
     * repository.
     *
     * @param name The name of the participant that you wish to look up in the
     *             repository.
     * @return <code>true</code> if the participant could be found, or
     *         <p><code>false</code> otherwise
     * @since 1.0
     */
    public boolean hasParticipant(String name);

    /**
     * Looks for the participant that corresponds to a given name and returns
     * it when found.
     *
     * @return A <code>Participant</code> instance if the provided name could
     *         be found; or
     *         <p><code>null</code> if the participant couldn't be found
     * @see Participant
     * @since 1.0
     */
    public Participant getParticipant(String name);

    /**
     * Returns all the participants that correspond to a given name.
     *
     * @param name The name of the participants that you wish to retrieve from
     *             the repository.
     * @return A <code>Collection</code> of <code>Participant</code>s that
     *         correspond to the name; or
     *         <p><code>null</code> if no participants with the provided name could be
     *         found
     * @see Participant
     * @since 1.0
     */
    public Collection<? extends Participant> getParticipants(String name);

    /**
     * Retrieves the repository's properties. This is meant to be similar
     * <code>System.getProperties</code>, but then not for the whole
     * system, but just for this application.
     * <p/>
     * Also, instead of just have a map of <code>String</code> keys and values,
     * the property values are of the {@link com.uwyn.rife.ioc.PropertyValue} type and are looked
     * up at run-time in a hierachical manner. This provides them with IoC
     * capabilities.
     * <p/>
     * Since Java allows the configuration of an application through the use of
     * properties, many other sub-system have adopted a similar approach (for
     * example servlet init parameters). Most of the time an application runs
     * through several barriers of configuration that often function
     * independently. These properties make it possible for each sub-system to
     * add their properties to the same pool. This makes it much more convenient
     * to retrieve a property value.
     *
     * @return the repository's properties
     * @since 1.0
     */
    public HierarchicalProperties getProperties();

    /**
     * Retrieves the context in which the repository was initialized.
     *
     * @return a reference to the context in which the repository was
     *         initialized; or
     *         <p><code>null</code> if the context isn't accessible
     * @since 1.0
     */
    public Object getContext();

    /**
     * Obtains the finished status of the initialization.
     *
     * @return <code>false</code> if the initialization is still busy; or
     *         <p><code>true</code> if the initialization is finished
     * @since 1.0
     */
    public boolean isFinished();

    /**
     * Cleans up the repository, typically done at application shutdown.
     *
     * @since 1.0
     */
    public void cleanup();
}


