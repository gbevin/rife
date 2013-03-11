/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.rep.exceptions.BlockingParticipantExpectedException;
import com.uwyn.rife.rep.exceptions.RepException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;

import java.util.*;
import java.util.logging.Logger;

/**
 * The <code>BlockingRepository</code> class provides a
 * <code>Repository</code> implementation that loads the participants from an
 * XML file.
 * <p>This file defaults to <code>rep/participants.xml</code>, but it can be
 * overridden by providing another filename to the <code>{@link
 * #initialize(String, ResourceFinder) initialize}</code> method. The
 * participants are initialized according to their listed order.
 * <p>Following is an example of such an XML file :
 * <pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;!DOCTYPE rep SYSTEM "/dtd/rep.dtd"&gt;
 * &lt;rep&gt;
 * &lt;participant blocking="true" parameter="rep/config.xml"&gt;ParticipantConfig&lt;/participant&gt;
 * &lt;participant blocking="false" parameter="graphics/buttons/"&gt;ParticipantImages&lt;/participant&gt;
 * &lt;participant name="my cursors" blocking="false" parameter="graphics/cursors/"&gt;ParticipantCursors&lt;/participant&gt;
 * &lt;/rep&gt;</pre>
 * <p>Each participant has a <code>blocking</code> attribute that determines
 * whether the repository should wait for the end of the participant's
 * initialization before progressing to the next participant or not. Using
 * this intelligently, it's possible to dramatically increase the perceived
 * startup time of an application.
 * <p>Optionally a participant can have a <code>name</code> attribute which
 * makes it possible to declare multiple participants of the same class. If no
 * name is provided, the participant's class name will be used to identify the
 * declared participant.
 * <p>Optionally a participant can also have a <code>parameter</code>
 * attribute which is merely a <code>String</code> that is provided to the
 * participant object for configuration purposes.
 * <p>Listeners can be added to the repository to receive notifications about
 * the initialization advancement of the participants and to know when the
 * initialization has completely finished. These notifications can, for
 * example, be used to display a progress bar in a splash window and to switch
 * to the real application window when the initialization has finished.
 * <p>The JDK's logging facility is used to output informative text during the
 * advancement of the initialization. Each participant has to provide an
 * initialization message that will be output.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see RepositoryListener
 * @see BlockingParticipant
 * @since 1.0
 */
public class BlockingRepository extends ThreadGroup implements Repository
{
// TODO
//    /**
//     * The default path of the repository participant XML file is
//     * <code>rep/participants.xml</code>. It can be overridden by setting the
//     * <code>rep.path</code> system property.
//     */
//    private static final String DEFAULT_REP_PATH = "rep/participants.xml";
    /**
     * Object that is used for thread synchronization of the finish.
     */
    private final Object finishedThreadMonitor = new Object();
    /**
     * Object that is used for thread synchronization of the cleanup.
     */
    private final Object cleanupThreadMonitor = new Object();
    /**
     * A map of the registered participants, indexed according to their name.
     */
    private Map<String, BlockingParticipant> repParticipants = new HashMap<>();
    /**
     * A map of the registered participants class name with all the registered
     * participants.
     */
    private Map<String, ArrayList<BlockingParticipant>> repParticipantClassnames = new HashMap<>();
    /**
     * A list of the participant names, ordered according to their
     * registration moment. This will be used as the order of execution.
     */
    private List<String> repParticipantsOrder = new ArrayList<>();
    /**
     * The list of participant names that will be use to determine which
     * participant's initialization end the repository has to wait for.
     */
    private List<String> repParticipantsToWaitFor = new ArrayList<>();
    /**
     * The list of all registered <code>RepListener</code> objects.
     */
    private Set<RepositoryListener> repListeners = new HashSet<>();
    /**
     * Indicates whether the initialization is finished.
     */
    private boolean finished = false;
    /**
     * Indicates whether the repository has been cleaned up.
     */
    private boolean cleanedUp = false;
    /**
     * Exception that is thrown by a participant.
     */
    private Throwable participantException = null;
    /**
     * The repository's properties.
     */
    private HierarchicalProperties properties;
    /**
     * The repository's context.
     */
    private Object context;

    /**
     * Default constructor without a repository context.
     */
    public BlockingRepository()
    {
        this(null);
    }

    /**
     * Constructor which sets up a the context in which the repository is initialized.
     */
    public BlockingRepository(Object context)
    {
        super("BlockingRepository");

        HierarchicalProperties system_properties = new HierarchicalProperties().putAll(System.getProperties());
        properties = new HierarchicalProperties().parent(system_properties);
        this.context = context;
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name for the name of the participant.
     * <p>The participant will not be blocking and have no parameter.
     *
     * @param className The fully resolved name of the participant's class, or
     *                  only the class name if the participant resides in the
     *                  <code>com.uwyn.rife.rep.participants</code> package.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(String className)
            throws RepException
    {
        return addParticipant(className, null, false, null);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name for the name of the participant.
     * <p>The participant will have no parameter.
     *
     * @param className The fully resolved name of the participant's class, or
     *                  only the class name if the participant resides in the
     *                  <code>com.uwyn.rife.rep.participants</code> package.
     * @param blocking  Indicates if this a blocking participant or not.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(String className, boolean blocking)
            throws RepException
    {
        return addParticipant(className, null, blocking, null);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name for the name of the participant.
     * <p>The participant will not be blocking.
     *
     * @param className The fully resolved name of the participant's class, or
     *                  only the class name if the participant resides in the
     *                  <code>com.uwyn.rife.rep.participants</code> package.
     * @param parameter An optional string that contains the parameter for
     *                  this participant.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(String className, String parameter)
            throws RepException
    {
        return addParticipant(className, null, false, parameter);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name for the name of the participant.
     *
     * @param className The fully resolved name of the participant's class, or
     *                  only the class name if the participant resides in the
     *                  <code>com.uwyn.rife.rep.participants</code> package.
     * @param blocking  Indicates if this a blocking participant or not.
     * @param parameter An optional string that contains the parameter for
     *                  this participant.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(String className, boolean blocking, String parameter)
            throws RepException
    {
        return addParticipant(className, null, blocking, parameter);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository.
     *
     * @param className The fully resolved name of the participant's class, or
     *                  only the class name if the participant resides in the
     *                  <code>com.uwyn.rife.rep.participants</code> package.
     * @param name      The name under which the participant will be registered, if
     *                  the name is <code>null</code> the class name will be used
     * @param blocking  Indicates if this a blocking participant or not.
     * @param parameter An optional string that contains the parameter for
     *                  this participant.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.0
     */
    public boolean addParticipant(String className, String name, boolean blocking, String parameter)
            throws RepException
    {
        // Try to resolve the participant's classname to ensure that an
        // object can be instantiated.
        Class klass;
        try
        {
            klass = Class.forName(className);
        }
        catch (ClassNotFoundException e1)
        {
            className = "com.uwyn.rife.rep.participants." + className;
            try
            {
                klass = Class.forName(className);
            }
            catch (ClassNotFoundException e2)
            {
                return false;
            }
        }

        return addParticipant(klass, name, blocking, parameter);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name as the name of the participant.
     *
     * @param klass     The class of the participant.
     * @param blocking  Indicates if this a blocking participant or not.
     * @param parameter An optional string that contains the parameter for
     *                  this participant.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(Class klass, boolean blocking, String parameter)
            throws RepException
    {
        return addParticipant(klass, null, blocking, parameter);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name as the name of the participant.
     * <p>The participant will not be blocking and have no parameter.
     *
     * @param klass The class of the participant.
     *              this participant.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(Class klass)
            throws RepException
    {
        return addParticipant(klass, null, false, null);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name as the name of the participant.
     * <p>The participant will have no parameter.
     *
     * @param klass    The class of the participant.
     * @param blocking Indicates if this a blocking participant or not.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(Class klass, boolean blocking)
            throws RepException
    {
        return addParticipant(klass, null, blocking, null);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository, using the
     * class name as the name of the participant.
     * <p>The participant will not be blocking.
     *
     * @param klass     The class of the participant.
     * @param parameter An optional string that contains the parameter for
     *                  this participant.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @see #addParticipant(Class, String, boolean, String)
     * @since 1.5
     */
    public boolean addParticipant(Class klass, String parameter)
            throws RepException
    {
        return addParticipant(klass, null, false, parameter);
    }

    /**
     * Adds a <code>BlockingParticipant</code> to the repository.
     *
     * @param klass     The class of the participant.
     * @param name      The name under which the participant will be registered, if
     *                  the name is <code>null</code> the class name will be used
     * @param blocking  Indicates if this a blocking participant or not.
     * @param parameter An optional string that contains the parameter for
     *                  this participant.
     * @return <code>true</code> if the participants was added successfully;
     *         or
     *         <p><code>false</code> if errors occurred
     * @see BlockingParticipant
     * @see Participant
     * @since 1.0
     */
    public boolean addParticipant(Class klass, String name, boolean blocking, String parameter)
            throws RepException
    {
        boolean generated_name = false;

        if (null == name ||
                0 == name.length())
        {
            name = klass.getName();
            generated_name = true;
        }

        // Check if the participant isn't already present in the repository.
        if (repParticipants.containsKey(name))
        {
            if (!generated_name)
            {
                return false;
            }

            String new_name;
            int counter = 2;
            do
            {
                new_name = name + counter;
                counter++;
            }
            while (repParticipants.containsKey(new_name));

            name = new_name;
        }

        try
        {
            // Try to create an instance of the participant.
            BlockingParticipant participant_instance;
            if (!BlockingParticipant.class.isAssignableFrom(klass))
            {
                if (Participant.class.isAssignableFrom(klass))
                {
                    participant_instance = new BlockingParticipantDelegate(klass);
                }
                else
                {
                    throw new BlockingParticipantExpectedException(klass.getName());
                }
            }
            else
            {
                participant_instance = (BlockingParticipant)klass.newInstance();
            }

            // Sets the name of the blocking participant thread to the one
            // it will be registered with
            participant_instance.setName(name);

            // Setup the thread
            Thread thread = new Thread(this, participant_instance);
            participant_instance.setThread(thread);

            // Store the participant and its name, remember it's execution order
            // according to other participants and remember whether this
            // participant's initialization should be finished before
            // continuing the repository initialization.
            // Regardless of their names, all the participants are already
            // registered with their class name.
            // Store its optional parameter.
            participant_instance.setRepository(this);
            repParticipants.put(name, participant_instance);
            ArrayList<BlockingParticipant> participants = repParticipantClassnames.get(klass.getName());
            if (null == participants)
            {
                participants = new ArrayList<>();
                repParticipantClassnames.put(klass.getName(), participants);
            }
            participants.add(participant_instance);
            repParticipantsOrder.add(name);
            if (blocking)
            {
                repParticipantsToWaitFor.add(name);
            }
            if (null != parameter)
            {
                participant_instance.setParameter(parameter);
            }
            return true;
        }
        catch (IllegalAccessException e)
        {
            return false;
        }
        catch (InstantiationException e)
        {
            return false;
        }
    }

    /**
     * Verifies if a participant that corresponds to a given name is present.
     *
     * @param name The name of the participant object that you wish to
     *             retrieve from the repository. See the {@link #getParticipant(String)
     *             getParticipant} method for detailed information about how the
     *             participant's name is resolved.
     * @return <code>true</code> if the provided class name could be found, or
     *         <p><code>false</code> otherwise
     * @see BlockingParticipant
     * @see #getParticipant(String)
     * @since 1.0
     */
    public boolean hasParticipant(String name)
    {
        BlockingParticipant participant = getParticipant(name);
        return null != participant && !participant.hadInitializationError();

    }

    /**
     * Looks for the participant that corresponds to a given name and returns
     * it when found.
     *
     * @param name The name of the participant instance that you wish to
     *             retrieve from the repository.
     *             <p>If no name was provided during the XML specification, the
     *             participant will have been registered with its class name. If the
     *             participant's class is not part of the
     *             <code>com.uwyn.rife.rep.participants</code> package, its full class
     *             name has to be provided, otherwise just the name of the class itself is
     *             sufficient.
     *             <p>Also, even though a participant has been registered with a name,
     *             it'll still be known under its class name. When a class name is
     *             provided as the argument, the first known participant of that class
     *             will be returned. This can be seen as the default participant for the
     *             specified type.
     * @return A <code>BlockingParticipant</code> instance if the provided
     *         name could be found amongst the registered participants in the
     *         repository; or
     *         <p><code>null</code> if the participant couldn't be found
     * @see BlockingParticipant
     * @see #hasParticipant(String)
     * @since 1.0
     */
    public BlockingParticipant getParticipant(String name)
    {
        // check if the provided participant name is present in the repository
        if (repParticipants.containsKey(name))
        {
            return repParticipants.get(name);
        }

        // check if the provided participant name can be expanded to a name that
        // is known by the repository
        String prefixed_name = "com.uwyn.rife.rep.participants." + name;
        if (repParticipants.containsKey(prefixed_name))
        {
            return repParticipants.get(prefixed_name);
        }

        // check if the provided participant name has been remembered as the
        // first participant of a certain class
        if (repParticipantClassnames.containsKey(name))
        {
            ArrayList<BlockingParticipant> participants = repParticipantClassnames.get(name);
            if (participants.size() > 0)
            {
                return participants.get(0);
            }
        }
        if (repParticipantClassnames.containsKey(prefixed_name))
        {
            ArrayList<BlockingParticipant> participants = repParticipantClassnames.get(prefixed_name);
            if (participants.size() > 0)
            {
                return participants.get(0);
            }
        }

        return null;
    }

    /**
     * Returns all the participants with a given class name
     *
     * @param className The class name of the participants that you wish to
     *                  retrieve from the repository.
     *                  <p>If the participant's class is not part of the
     *                  <code>com.uwyn.rife.rep.participants</code> package, its full class
     *                  name has to be provided, otherwise just the name of the class itself is
     *                  sufficient.
     * @return A <code>Collection</code> of <code>BlockingParticipant</code>
     *         instances of the provided class name; or
     *         <p><code>null</code> if no participants with the provided class name
     *         could be found
     * @see BlockingParticipant
     * @see #getParticipant(String)
     * @since 1.0
     */
    public Collection<BlockingParticipant> getParticipants(String className)
    {
        // check if the provided participant name has been remembered as the
        // first participant of a certain class
        if (repParticipantClassnames.containsKey(className))
        {
            ArrayList<BlockingParticipant> participants = repParticipantClassnames.get(className);
            if (participants.size() > 0)
            {
                return participants;
            }
        }
        // check if the provided participant class name can be expanded to a
        // name that is known by the repository
        String prefixed_name = "com.uwyn.rife.rep.participants." + className;
        if (repParticipantClassnames.containsKey(prefixed_name))
        {
            ArrayList<BlockingParticipant> participants = repParticipantClassnames.get(prefixed_name);
            if (participants.size() > 0)
            {
                return participants;
            }
        }

        return null;
    }

    /**
     * Sequentially execute the participants according to their registration
     * order. If the participant has already been run or is still running, it
     * is not executed anymore. The repository waits for the participant's
     * execution to finish if this has been indicated by registering with the
     * <code>blocking</code> attribute.
     * <p>The resource finder that will be used is an instance of {@link ResourceFinderClasspath}.
     *
     * @see ResourceFinderClasspath
     * @see #runParticipants(ResourceFinder)
     * @since 1.5
     */
    public void runParticipants()
    {
        runParticipants(ResourceFinderClasspath.getInstance());
    }

    /**
     * Sequentially execute the participants according to their registration
     * order. If the participant has already been run or is still running, it
     * is not executed anymore. The repository waits for the participant's
     * execution to finish if this has been indicated by registering with the
     * <code>blocking</code> attribute.
     *
     * @param resourceFinder The resource finder that is used during the
     *                       initialization.
     * @since 1.0
     */
    public void runParticipants(ResourceFinder resourceFinder)
    {
        // Iterate over the participants according to their registration order.
        for (String name : repParticipantsOrder)
        {
            BlockingParticipant participant = repParticipants.get(name);
            try
            {
                // Only start the participant's initialization if it hasn't run
                // before.
                if (!participant.getThread().isAlive() &&
                        !participant.isFinished())
                {
                    Logger.getLogger("com.uwyn.rife.rep").info("INITIALIZATION : " + participant.getInitializationMessage());
                    // Initialize the participant
                    participant.setResourceFinder(resourceFinder);
                    participant.getThread().start();
                    // Wait for the initialization to finish if this has been
                    // specified.
                    if (repParticipantsToWaitFor.contains(name))
                    {
                        participant.waitUntilFinished();
                    }
                }
            }
            finally
            {
                detectParticipantException();
            }
        }

        synchronized (finishedThreadMonitor)
        {
            if (!finished)
            {
                BlockingRepositoryCleanup cleanupShutdownHook = new BlockingRepositoryCleanup(this);
                Runtime.getRuntime().addShutdownHook(cleanupShutdownHook);
                finished = true;
                fireInitFinished();
            }
        }
    }

    private void detectParticipantException()
    {
        if (participantException != null)
        {
            if (participantException instanceof RuntimeException)
            {
                throw (RuntimeException)participantException;
            }
            throw new RuntimeException(participantException);
        }
    }

    /**
     * If participants call an exception, clean up correctly and rethrow the
     * exception afterwards.
     *
     * @since 1.0
     */
    public void uncaughtException(Thread thread, Throwable e)
    {
        if (e instanceof ThreadDeath)
        {
            super.uncaughtException(thread, e);
        }

        if (null == participantException)
        {
            participantException = e;
        }

        synchronized (finishedThreadMonitor)
        {
            if (!finished)
            {
                finished = true;
                fireInitFinished();
                cleanup();
            }
            // the repository has finished so log the exception
            else
            {
                if (participantException != null)
                {
                    Logger.getLogger("com.uwyn.rife.rep").severe(ExceptionUtils.getExceptionStackTrace(participantException));
                }
            }
        }
    }

    /**
     * Parses the XML file to determine what the participants are. Then, one
     * by one, initializes each participant with the {@link
     * #runParticipants(ResourceFinder) runParticipants} method, waiting for
     * it to finish if its <code>blocking</code> attribute was set to
     * <code>true</code>.
     *
     * @param repXmlPath     The path of the XML file.
     *                       <p>If this is <code>null</code>, <code>rep/participants.xml</code> will
     *                       be used.
     * @param resourcefinder The resource finder that will be used to look up
     *                       resources such as XML files and DTDs. It will also be used by other
     *                       classes after initialization through the
     *                       <code>getResourceFinder()</code> method.
     *                       <p>If this is <code>null</code>, an instance of <code>{@link
     *                       ResourceFinderClasspath}</code> will be used.
     * @throws RepException when an error occurs during the initialization.
     * @since 1.0
     */
    public void initialize(String repXmlPath, ResourceFinder resourcefinder)
            throws RepException
    {
        finished = false;

        if (null == resourcefinder)
        {
            resourcefinder = ResourceFinderClasspath.getInstance();
        }
// TODO
//        if (null == repXmlPath)
//        {
//            repXmlPath = DEFAULT_REP_PATH;
//        }
//
//		// Parse the repository configuration file.
//		Xml2BlockingRepository xml2rep = new Xml2BlockingRepository(this);
//		try
//		{
//			// Add the participants that have been specified in the
//			// configuration file to the repository.
//			xml2rep.addRepParticipants(repXmlPath, resourcefinder);
//		}
//		// If errors occured during the parsing of the repository configuration
//		// file, output a message and throw a runtime error.
//		catch (XmlErrorException e)
//		{
//			throw new InitializationErrorException(e);
//		}

        // Initialize the participants.
        runParticipants(resourcefinder);
    }

    /**
     * Obtains the finished status of the initialization.
     *
     * @return <code>false</code> if the initialization is still busy; or
     *         <p><code>true</code> if the initialization is finished
     * @since 1.0
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Cleans up the participants in the order in which they have been
     * declared. Every participant's <code>cleanup()</code> method is
     * successively called.
     *
     * @throws RepException when an error occurs during the cleanup.
     * @since 1.0
     */
    public void cleanup()
            throws RepException
    {
        if (!finished)
        {
            return;
        }

        synchronized (cleanupThreadMonitor)
        {
            if (cleanedUp)
            {
                return;
            }

            // iterate over all the registered participants in their reverse
            // registration order
            String name;
            BlockingParticipant participant;
            for (int i = repParticipantsOrder.size() - 1; i >= 0; i--)
            {
                name = repParticipantsOrder.get(i);

                // obtain each participant by its name and clean it up
                participant = getParticipant(name);
                if (participant != null)
                {
                    Logger.getLogger("com.uwyn.rife.rep").info("CLEANUP : " + participant.getCleanupMessage());
                    participant.cleanup();
                }
            }

            cleanedUp = true;
        }
    }

    public HierarchicalProperties getProperties()
    {
        return properties;
    }

    public Object getContext()
    {
        return context;
    }

    /**
     * Adds the specified repository listener to receive repository
     * initialization events. If <code>repListener</code> is null, no
     * exception is thrown and no action is performed.
     *
     * @param repListener The repository listener that will be added.
     * @see RepositoryListener
     * @see #removeRepListener(RepositoryListener)
     * @since 1.0
     */
    public void addRepListener(RepositoryListener repListener)
    {
        if (null == repListener)
        {
            return;
        }

        repListeners.add(repListener);
    }

    /**
     * Removes the repository listener so that it no longer receives
     * repository initialization events. This method performs no function, nor
     * does it throw an exception, if the listener specified by the argument
     * was not previously added to this component. If <code>repListener</code>
     * is <code>null</code>, no exception is thrown and no action is
     * performed.
     *
     * @param repListener The repository listener that will be removed.
     * @see RepositoryListener
     * @see #addRepListener(RepositoryListener)
     * @since 1.0
     */
    public void removeRepListener(RepositoryListener repListener)
    {
        repListeners.remove(repListener);
    }

    /**
     * Notifies the registered listeners that a new initialization action has
     * been performed.
     * <p>This is always triggered when a participant's initialization has
     * finished. Each participant however has the possibility to call this
     * method directly, allowed for finer-grained notification of the
     * advancement of the initialization.
     *
     * @param participant The participant that triggered the action.
     * @see RepositoryListener
     * @since 1.0
     */
    public void fireInitActionPerformed(BlockingParticipant participant)
    {
        if (repListeners.size() > 0)
        {
            for (RepositoryListener listener : repListeners)
            {
                listener.initActionPerformed(participant);
            }
        }
    }

    /**
     * Notifies the registered listeners that the repository initialization
     * has finished.
     *
     * @see RepositoryListener
     * @since 1.0
     */
    public void fireInitFinished()
    {
        if (repListeners.size() > 0)
        {
            for (RepositoryListener listener : repListeners)
            {
                listener.initFinished();
            }
        }
    }
}


