/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementAware.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;

/**
 * This interface contains all the methods that a class must implement to
 * become an element for the web engine.
 * <p>For convenience, you can also extend the abstract {@link Element} class
 * which gives you the benefit of having local access to all its methods and
 * having no abstract methods to implement.
 * <p>Elements are the smallest logical building blocks of a RIFE web
 * application. They are declared in the site structure and when a request
 * arrives that maps to an element declaration, a new instance of the
 * implementation is created. Element instances are thus never shared amongst
 * requests, unless you are into a continuation tree that is set up to not
 * clone its variable stack. This makes the logic inside elements fully
 * thread-safe.
 * <p>The {@link #processElement} method is the default entry point and will
 * be called when a request arrives.
 * <p>You're free to add any other method to this class. RIFE provides a
 * convention syntax for methods that are supposed to handle submissions. The
 * name of the submission is capitalized and the "<code>do</code>" literal is
 * prepended. RIFE then looks for a method with that name, the <code>public
 * void</code> modifiers and no arguments. When such a method is found, it is
 * executed instead of <code>processElement()</code>. Nothing prevents you
 * however from handling submissions conditionally in the
 * <code>processElement()</code> method though, without isolating the logic in
 * a separate method. For example, when a submission arrives with the name "<code>storeUser</code>",
 * RIFE will look for the method:
 * <pre>public void doStoreUser()</pre>
 * If it's present, it will be called instead of <code>processElement()</code>.
 * <p>Often you want to initialize common data structures, both for regular
 * requests as for submissions. The {@link ElementSupport#initialize()} method
 * can be used for that. It will be the first element's method that is called
 * in a fully setup element context. When extending {@link Element}, the
 * easiest is to simply overload the {@link ElementSupport#initialize()}
 * method, otherwise an {@link ElementInitializer} has to be registered in the
 * {@link #noticeElement} method.
 * <p>RIFE also supports setter-based dependency injection for element
 * properties, inputs, global variables and submission parameters. If setter
 * methods are present that correspond to declared variable names, they will
 * be automatically invoked with the available values. Of course, you can
 * always retrieve values through the dedicated ElementSupport methods for
 * {@link ElementSupport#getProperty properties}, {@link
 * ElementSupport#getInput inputs} and {@link ElementSupport#getParameter
 * submission parameters}.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface ElementAware
{
	/**
	 * This method is called immediately after the instantiation of the
	 * element to provide the support object that allows the element to
	 * function in the current context. Note that the context is not setup
	 * yet, the bridge object is merely provided at this stage.
	 * <p>It's good practice to store the <code>elementSupport</code>
	 * parameter in a member variable of the element, making it possible to
	 * use it from any method in the element.
	 * <p>This method should also be used to provide the elementSupport
	 * instance with an {@link ElementDeployer}, an {@link ElementInitializer}
	 * and an {@link ElementChildTrigger}, if they are needed.
	 * 
	 * @param elementSupport the <code>ElementSupport</code> instance for this
	 * request and this element
	 * @see ElementSupport
	 * @since 1.0
	 */
	public void noticeElement(ElementSupport elementSupport);
	/**
	 * The default entry point that will be called when a request arrives.
	 * 
	 * @since 1.0
	 */
	public void processElement() throws EngineException;
}

