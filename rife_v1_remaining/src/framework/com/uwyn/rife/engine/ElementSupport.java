/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementSupport.java 3959 2008-05-26 15:14:43Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.continuations.ContinuableObject;
import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.continuations.exceptions.ContinuationsNotActiveException;
import com.uwyn.rife.engine.exceptions.*;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.template.TemplateTransformer;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.*;
import com.uwyn.rife.tools.exceptions.ConversionException;
import com.uwyn.rife.tools.exceptions.SerializationUtilsErrorException;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * The {@code ElementSupport} class provides all the methods to
 * communicate from inside an element with the context in which it is being
 * executed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3959 $
 * @see ElementAware
 * @since 1.0
 */
public class ElementSupport implements ContinuableObject, Cloneable
{
	private ElementAware				mElementAware = null;
	private transient ElementInfo		mElementInfo = null;
	private transient ElementContext	mElementContext = null;
	private boolean						mRequestAccessEnabled = true;

	private ElementInitializer		mInitializer = null;
	private ElementChildTrigger		mChildTrigger = null;
	private Class                   mDeploymentClass = null;
	private boolean                 mProhibitRawAccess = true;
	private boolean                 mCloneContinuations = true;

	protected ElementSupport()
	{
	}

	/**
	 * Sets the {@code ElementInitializer} class that will be used to
	 * initialize the element.
	 * <p>Customizing the initialization can also simply be done by
	 * overloading the {@code initialize()} method if the element extends
	 * the {@link Element} class.
	 *
	 * @param initializer the initializer
	 * @see #initialize()
	 * @see ElementInitializer
	 * @since 1.0
	 */
	public void setInitializer(ElementInitializer initializer)
	{
		mInitializer = initializer;
	}

	/**
	 * Initializes the element, this method should never be called explicitly.
	 * <p>The initialization happens in a fully setup context and is the first
	 * method that will be called by the engine.
	 * <p>The default implementation executes the {@link ElementInitializer}
	 * that has been registered with {@link #setInitializer}.
	 *
	 * @see #setInitializer(ElementInitializer)
	 * @since 1.0
	 */
	public void initialize()
	{
		if (mInitializer != null)
		{
			mInitializer.initialize();
		}
	}

	/**
	 * Sets the {@code ElementChildTrigger} class that will be used to
	 * child trigger is executed.
	 * <p>Customizing the child trigger can also simply be done by overloading
	 * the {@code childTriggered()} method if the element extends the
	 * {@link Element} class.
	 *
	 * @param childTrigger the child trigger
	 * @see #childTriggered(String, String[])
	 * @see ElementChildTrigger
	 * @since 1.0
	 */
	public void setChildTrigger(ElementChildTrigger childTrigger)
	{
		mChildTrigger = childTrigger;
	}

	/**
	 * Called by the engine when a child trigger occurs, this method should
	 * never be called explicitly.
	 * <p>The default implementation executes the {@link ElementChildTrigger}
	 * that has been registered with {@link #setChildTrigger}.
	 *
	 * @param name the name of the variable that initiated the child trigger
	 * @param values the values of the variable that initiated the child
	 * trigger
	 * @return {@code true} if the execution should be interrupted and
	 * step down the inheritance stack (ie. when the activation of the child
	 * has been triggered); or
	 * <p>{@code false} if the execution should just continue
	 * @see #setChildTrigger(ElementChildTrigger)
	 * @since 1.0
	 */
	public boolean childTriggered(String name, String[] values)
	{
		if (null == mChildTrigger)
		{
			throw new ChildTriggerNotImplementedException(getClass().getName(), name);
		}

		return mChildTrigger.childTriggered(name, values);
	}

	/**
	 * Set the {@code ElementDeployer} class that will be used for
	 * deployment.
	 * <p>An instance of this class will be created when the element is
	 * deployed within a site. The instance's {@link ElementDeployer#deploy()}
	 * method will be called. This is handy if you need to setup
	 * element-specific resources for all its instances.
	 * <p>Customizing the element deployer can also simply be done by
	 * overloading the {@code getDeploymentClass()} method if the element
	 * extends the {@link Element} class.
	 *
	 * @param klass the {@code ElementDeployer} that will be used to
	 * deploy the element
	 * @see ElementDeployer#deploy()
	 * @see #getDeploymentClass()
	 * @see #getDeployer()
	 * @since 1.0
	 */
	public void setDeploymentClass(Class<? extends ElementDeployer> klass)
	{
		mDeploymentClass = klass;
	}

	/**
	 * Retrieves the class that will be used for the deployment of the
	 * element.
	 *
	 * @return an instance of {@code ElementDeployer}; or
	 * <p>null if no deployment class is used
	 * @see #setDeploymentClass(Class)
	 * @see #getDeployer()
	 * @since 1.0
	 */
	public Class getDeploymentClass()
	{
		return mDeploymentClass;
	}

	/**
	 * Changes the access permissions to raw servlet API methods.
	 * <p>By default, RIFE shields you away from raw access to the servlet API
	 * and controls all incoming and outgoing data. This makes it possible to
	 * offer the advanced engine features.
	 * <p>Sometimes it's useful however to still be able to access the raw
	 * servlet API features, for instance when integrating other libraries.
	 * The fact that a method needs to be called before being able to do so
	 * makes it easy to identify which elements are outside of the controlled
	 * context of the RIFE application.
	 *
	 * @param access {@code true} if the raw servlet API access is
	 * prohibited; or
	 * <p>{@code false} if it is allowed
	 * @see #prohibitRawAccess()
	 * @since 1.0
	 */
	public void setProhibitRawAccess(boolean access)
	{
		mProhibitRawAccess = access;
	}

	/**
	 * Indicates whether the access to raw servlet API methods is allowed.
	 * <p>Instead of using the {@link #setProhibitRawAccess(boolean)} method,
	 * one can also overload this method to allow raw access. By default, raw
	 * access is forbidden.
	 *
	 * @return {@code true} if the raw servlet API access is prohibited;
	 * or
	 * <p>{@code false} if it is allowed
	 * @see #setProhibitRawAccess(boolean)
	 * @since 1.0
	 */
	public boolean prohibitRawAccess()
	{
		return mProhibitRawAccess;
	}

	/**
	 * Changes the engine's behavior when new continuation steps are created.
	 * <p>By default, the active continuation is cloned when a new step needs
	 * to be created. This makes it possible to use the browser's back button
	 * and start a new continuation trail. Each previous step thus keeps it
	 * associated state. By disabling the cloning, performance will increase
	 * and memory usage will decrease since the active continuation will
	 * simply be migrated to the new continuation step. Note that none of the
	 * previous steps will be usable anymore though.
	 *
	 * @param clone {@code true} to make the engine clone continuations;
	 * or
	 * <p>{@code false} to disable the cloning
	 * @see #cloneContinuations()
	 * @since 1.0
	 */
	public void setCloneContinuations(boolean clone)
	{
		mCloneContinuations = clone;
	}

	/**
	 * Indicates whether continuations are cloned at each step.
	 * <p>Instead of using the {@link #setCloneContinuations(boolean)} method,
	 * one can also overload this method to configure the cloning. By default,
	 * cloning is active.
	 *
	 * @return {@code true} to make the engine clone continuations; or
	 * <p>{@code false} to disable the cloning
	 * @see #setCloneContinuations (boolean)
	 * @since 1.0
	 */
	public boolean cloneContinuations()
	{
		return mCloneContinuations;
	}

	/**
	 * Pauses the execution of the element and creates a new continuation.
	 * <p>The next request will resume exactly at the same location with a
	 * completely restored call stack and variable stack.
	 *
	 * @since 1.0
	 */
	public final void pause()
	{
		// this is deliberately empty since the continuation support
		// rewrites method calls to pause
		throw new ContinuationsNotActiveException();
	}
	
	/**
	 * Steps back to the start of the previous continuation.
	 * <p>If there is no previous continuation, the element will be executed
	 * from the beginning again.
	 *
	 * @see #duringStepBack
	 * @since 1.5
	 */
	public final void stepBack()
	{
		// this is deliberately empty since the continuation support
		// rewrites method calls to pause
		throw new ContinuationsNotActiveException();
	}
	
	/**
	 * Indicates whether the current element execution is a step back.
	 *
	 * @return {@code true} if a step back occurred in this request; or
	 * <p>{@code false} otherwise
	 * @see #stepBack
	 * @since 1.5
	 */
	public boolean duringStepBack()
	{
		return mElementContext.duringStepBack();
	}
	
	/**
	 * Pauses the execution of the element and creates a new continuation. The
	 * execution will immediately continue in the element that is the target
	 * of the called exit.
	 * <p>As soon as the called element returns or executes {@link #answer()},
	 * the execution will resume in the calling element with a completely
	 * restored call stack and variable stack.
	 *
	 * @param exit the name of the exit whose target element will be called
	 * @return the object that was provided through the {@link #answer(Object)}
	 * method in the called element; or
	 * <p>{@code null} if no answer was provided
	 * @see #answer()
	 * @see #answer(Object)
	 * @since 1.0
	 */
	public final Object call(String exit)
	{
		// this is deliberately empty since the continuation support
		// rewrites method calls to call
		throw new ContinuationsNotActiveException();
	}

	/**
	 * Resumes the execution in the calling element by providing no answer
	 * object.
	 * <p>The execution in the active element will be interrupted immediately
	 * and the call continuation will be resumed exactly where is was paused
	 * before.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException a runtime
	 * exception that is used to immediately interrupt the execution, don't
	 * catch this exception
	 * @see #call(String)
	 * @see #answer(Object)
	 * @since 1.0
	 */
	public final void answer()
	throws EngineException
	{
		// this is deliberately empty since the continuation support
		// rewrites method calls to answer
		throw new ContinuationsNotActiveException();
	}

	/**
	 * Resumes the execution in the calling element by providing an answer.
	 * <p>The execution in the active element will be interrupted immediately
	 * and the call continuation will be resumed exactly where is was paused
	 * before.
	 *
	 * @param answer the object that will be answered to the calling element
	 * @exception com.uwyn.rife.engine.exceptions.EngineException a runtime
	 * exception that is used to immediately interrupt the execution, don't
	 * catch this exception
	 * @see #call(String)
	 * @see #answer()
	 * @since 1.0
	 */
	public final void answer(Object answer)
	throws EngineException
	{
		// this is deliberately empty since the continuation support
		// rewrites method calls to answer
		throw new ContinuationsNotActiveException();
	}
	
	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEHTML enginehtml}
	 * type, using the current element's absolute ID as the template name.
	 * <p>If the element is an arrival, the absolute ID of the real element it
	 * points to will be used.
	 *
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getHtmlTemplate(String)
	 * @see #getHtmlTemplate(String, TemplateTransformer)
	 * @see #getHtmlTemplate(String, String)
	 * @see #getHtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.3
	 */
	public Template getHtmlTemplate()
	throws TemplateException, EngineException
	{
		return getHtmlTemplate(getElementInfo().getReferenceId().substring(1), null, null);
	}
	
	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEHTML enginehtml}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getHtmlTemplate()
	 * @see #getHtmlTemplate(String, TemplateTransformer)
	 * @see #getHtmlTemplate(String, String)
	 * @see #getHtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getHtmlTemplate(String name)
	throws TemplateException, EngineException
	{
		return getHtmlTemplate(name, null, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEHTML enginehtml}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getHtmlTemplate()
	 * @see #getHtmlTemplate(String)
	 * @see #getHtmlTemplate(String, String)
	 * @see #getHtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getHtmlTemplate(String name, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		return getHtmlTemplate(name, null, transformer);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEHTML enginehtml}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getHtmlTemplate()
	 * @see #getHtmlTemplate(String)
	 * @see #getHtmlTemplate(String, TemplateTransformer)
	 * @see #getHtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getHtmlTemplate(String name, String encoding)
	throws TemplateException, EngineException
	{
		return getHtmlTemplate(name, encoding, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEHTML enginehtml}
	 * type.
	 * <p>The special engine template types contain additional block and value
	 * filters to offer the following features:
	 * <ul>
	 * <li>embedded elements<br>(eg.: {@code &lt;!--V
	 * 'ELEMENT:my.elementid'/--&gt;})
	 * <li>role user context for scripted block assignment to values<br>(eg{@code .:
	 * &lt;!--V 'OGNL:ROLEUSER:rolecheck'--&gt;User is not in role
	 * "admin"&lt;!--/V--&gt;<br>&lt;!--B 'OGNL:ROLEUSER:rolecheck:[[
	 * isInRole("admin") ]]'--&gt;User is in role "admin"&lt;!--/B--&gt;})
	 * </ul>
	 * <p>Non-engine versions of the same template types are not able to
	 * provide these functionalities.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getHtmlTemplate()
	 * @see #getHtmlTemplate(String)
	 * @see #getHtmlTemplate(String, TemplateTransformer)
	 * @see #getHtmlTemplate(String, String)
	 * @since 1.0
	 */
	public Template getHtmlTemplate(String name, String encoding, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)			throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())		throw new IllegalArgumentException("name can't be empty.");

		return TemplateFactory.ENGINEHTML.get(name, encoding, transformer);
	}
	
	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXHTML
	 * enginexhtml} type, using the current element's absolute ID as the
	 * template name.
	 *
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXhtmlTemplate(String)
	 * @see #getXhtmlTemplate(String, TemplateTransformer)
	 * @see #getXhtmlTemplate(String, String)
	 * @see #getXhtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.3
	 */
	public Template getXhtmlTemplate()
	throws TemplateException, EngineException
	{
		return getXhtmlTemplate(getElementInfo().getReferenceId().substring(1), null, null);
	}
	
	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXHTML
	 * enginexhtml} type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXhtmlTemplate()
	 * @see #getXhtmlTemplate(String, TemplateTransformer)
	 * @see #getXhtmlTemplate(String, String)
	 * @see #getXhtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getXhtmlTemplate(String name)
	throws TemplateException, EngineException
	{
		return getXhtmlTemplate(name, null, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXHTML
	 * enginexhtml} type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXhtmlTemplate()
	 * @see #getXhtmlTemplate(String)
	 * @see #getXhtmlTemplate(String, String)
	 * @see #getXhtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getXhtmlTemplate(String name, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		return getXhtmlTemplate(name, null, transformer);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXHTML
	 * enginexhtml} type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXhtmlTemplate()
	 * @see #getXhtmlTemplate(String)
	 * @see #getXhtmlTemplate(String, TemplateTransformer)
	 * @see #getXhtmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getXhtmlTemplate(String name, String encoding)
	throws TemplateException, EngineException
	{
		return getXhtmlTemplate(name, encoding, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXHTML
	 * enginexhtml} type.
	 * <p>The special engine template types contain additional block and value
	 * filters to offer the following features:
	 * <ul>
	 * <li>embedded elements<br>(eg.: {@code &lt;!--V
	 * 'ELEMENT:my.elementid'/--&gt;})
	 * <li>role user context for scripted block assignment to values<br>(eg{@code .:
	 * &lt;!--V 'OGNL:ROLEUSER:rolecheck'--&gt;User is not in role
	 * "admin"&lt;!--/V--&gt;<br>&lt;!--B 'OGNL:ROLEUSER:rolecheck:[[
	 * isInRole("admin") ]]'--&gt;User is in role "admin"&lt;!--/B--&gt;})
	 * </ul>
	 * <p>Non-engine versions of the same template types are not able to
	 * provide these functionalities.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXhtmlTemplate()
	 * @see #getXhtmlTemplate(String)
	 * @see #getXhtmlTemplate(String, TemplateTransformer)
	 * @see #getXhtmlTemplate(String, String)
	 * @since 1.0
	 */
	public Template getXhtmlTemplate(String name, String encoding, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)			throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())		throw new IllegalArgumentException("name can't be empty.");

		return TemplateFactory.ENGINEXHTML.get(name, encoding, transformer);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXML enginexml}
	 * type, using the current element's absolute ID as the template name.
	 *
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXmlTemplate(String)
	 * @see #getXmlTemplate(String, TemplateTransformer)
	 * @see #getXmlTemplate(String, String)
	 * @see #getXmlTemplate(String, String, TemplateTransformer)
	 * @since 1.3
	 */
	public Template getXmlTemplate()
	throws TemplateException, EngineException
	{
		return getXmlTemplate(getElementInfo().getReferenceId().substring(1), null, null);
	}
	
	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXML enginexml}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXmlTemplate()
	 * @see #getXmlTemplate(String, TemplateTransformer)
	 * @see #getXmlTemplate(String, String)
	 * @see #getXmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getXmlTemplate(String name)
	throws TemplateException, EngineException
	{
		return getXmlTemplate(name, null, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXML enginexml}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXmlTemplate(String)
	 * @see #getXmlTemplate(String)
	 * @see #getXmlTemplate(String, String)
	 * @see #getXmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getXmlTemplate(String name, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		return getXmlTemplate(name, null, transformer);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXML enginexml}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXmlTemplate(String)
	 * @see #getXmlTemplate(String)
	 * @see #getXmlTemplate(String, TemplateTransformer)
	 * @see #getXmlTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getXmlTemplate(String name, String encoding)
	throws TemplateException, EngineException
	{
		return getXmlTemplate(name, encoding, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINEXML enginexml}
	 * type.
	 * <p>The special engine template types contain additional block and value
	 * filters to offer the following features:
	 * <ul>
	 * <li>embedded elements<br>(eg.: {@code &lt;!--V
	 * 'ELEMENT:my.elementid'/--&gt;})
	 * <li>role user context for scripted block assignment to values<br>(eg{@code .:
	 * &lt;!--V 'OGNL:ROLEUSER:rolecheck'--&gt;User is not in role
	 * "admin"&lt;!--/V--&gt;<br>&lt;!--B 'OGNL:ROLEUSER:rolecheck:[[
	 * isInRole("admin") ]]'--&gt;User is in role "admin"&lt;!--/B--&gt;})
	 * </ul>
	 * <p>Non-engine versions of the same template types are not able to
	 * provide these functionalities.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getXmlTemplate(String)
	 * @see #getXmlTemplate(String)
	 * @see #getXmlTemplate(String, TemplateTransformer)
	 * @see #getXmlTemplate(String, String)
	 * @since 1.0
	 */
	public Template getXmlTemplate(String name, String encoding, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)			throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())		throw new IllegalArgumentException("name can't be empty.");

		return TemplateFactory.ENGINEXML.get(name, encoding, transformer);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINETXT enginetxt}
	 * type, using the current element's absolute ID as the template name.
	 *
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getTxtTemplate(String)
	 * @see #getTxtTemplate(String, TemplateTransformer)
	 * @see #getTxtTemplate(String, String)
	 * @see #getTxtTemplate(String, String, TemplateTransformer)
	 * @since 1.3
	 */
	public Template getTxtTemplate()
	throws TemplateException, EngineException
	{
		return getTxtTemplate(getElementInfo().getReferenceId().substring(1), null, null);
	}
	
	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINETXT enginetxt}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getTxtTemplate()
	 * @see #getTxtTemplate(String, TemplateTransformer)
	 * @see #getTxtTemplate(String, String)
	 * @see #getTxtTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getTxtTemplate(String name)
	throws TemplateException, EngineException
	{
		return getTxtTemplate(name, null, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINETXT enginetxt}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getTxtTemplate()
	 * @see #getTxtTemplate(String)
	 * @see #getTxtTemplate(String, String)
	 * @see #getTxtTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getTxtTemplate(String name, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		return getTxtTemplate(name, null, transformer);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINETXT enginetxt}
	 * type.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getTxtTemplate()
	 * @see #getTxtTemplate(String)
	 * @see #getTxtTemplate(String, TemplateTransformer)
	 * @see #getTxtTemplate(String, String, TemplateTransformer)
	 * @since 1.0
	 */
	public Template getTxtTemplate(String name, String encoding)
	throws TemplateException, EngineException
	{
		return getTxtTemplate(name, encoding, null);
	}

	/**
	 * Creates a new template instance of the {@link
	 * com.uwyn.rife.template.TemplateFactoryEngineTypes#ENGINETXT enginetxt}
	 * type.
	 * <p>The special engine template types contain additional block and value
	 * filters to offer the following features:
	 * <ul>
	 * <li>embedded elements<br>(eg.: {@code [!V 'ELEMENT:my.elementid'/]})
	 * <li>role user context for scripted block assignment to values<br>(eg{@code .:
	 * [!V 'OGNL:ROLEUSER:rolecheck']User is not in role "admin"[!/V]<br>[!B
	 * 'OGNL:ROLEUSER:rolecheck:[[ isInRole("admin") ]]']User is in role
	 * "admin"[!/B]})
	 * </ul>
	 * <p>Non-engine versions of the same template types are not able to
	 * provide these functionalities.
	 *
	 * @param name the name of the template. Note that this follows the Java
	 * naming conventions for classes and packages. Directories correspond to
	 * package names and file separators correspond to dots. Any non-valid
	 * class name character will be replaced by an underscore.
	 * @param encoding the encoding of the template's source
	 * @param transformer the template transformer that will be used to modify
	 * the template's source before it's parsed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurred during the retrieval, parsing or compilation of the
	 * template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the initialization of the template in the element
	 * context; or if you don't have access to the request data (eg. you're
	 * inside a child trigger); or if there's no active element context (eg.
	 * you're using this method inside the constructor instead of inside the
	 * {@link #initialize()} method)
	 * @return a new instance of the template
	 * @see #getTxtTemplate()
	 * @see #getTxtTemplate(String)
	 * @see #getTxtTemplate(String, TemplateTransformer)
	 * @see #getTxtTemplate(String, String)
	 * @since 1.0
	 */
	public Template getTxtTemplate(String name, String encoding, TemplateTransformer transformer)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)			throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())		throw new IllegalArgumentException("name can't be empty.");

		return TemplateFactory.ENGINETXT.get(name, encoding, transformer);
	}

	/**
	 * Transforms a provided {@code String} object into a new string,
	 * containing only valid HTML characters.
	 *
	 * @param source The string that has to be transformed into a valid HTML
	 * string.
	 * @return The encoded {@code String} object.
	 * @see #encodeXml(String)
	 * @since 1.0
	 */
	public String encodeHtml(String source)
	{
		return StringUtils.encodeHtml(source);
	}

	/**
	 * Transforms a provided {@code String} object into a new string,
	 * containing only valid XML characters.
	 *
	 * @param source The string that has to be transformed into a valid XML
	 * string.
	 * @return The encoded {@code String} object.
	 * @see #encodeHtml(String)
	 * @since 1.0
	 */
	public String encodeXml(String source)
	{
		return StringUtils.encodeXml(source);
	}

	/**
	 * Enables or disables the response text buffer. By default, it is
	 * enabled.
	 * <p>Disabling an enabled text buffer, flushes the already buffered
	 * content first.
	 * <p>If the text buffer is disabled, text content will be send
	 * immediately to the client, this can decrease performance. Unless you
	 * need to stream content in real time, it's best to leave the text buffer
	 * enabled. It will be flushed and sent in one go at the end of the
	 * request.
	 * <p>Exits that cancel embedding rely on the fact that the text buffer is
	 * active to be able to discard the partial content of the embedding
	 * element.
	 *
	 * @param enabled {@code true} to enable the text buffer; or
	 * <p>{@code false} to disable it
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the modification of the text buffer presence; or if you
	 * don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #isTextBufferEnabled()
	 * @see #flush()
	 * @see #clearBuffer()
	 * @since 1.0
	 */
	public void enableTextBuffer(boolean enabled)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().enableTextBuffer(enabled);
	}

	/**
	 * Indicates whether the response text buffer is enabled or disabled.
	 *
	 * @return {@code true} if the text buffer is enabled; or
	 * <p>{@code false} if it is disabled
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #enableTextBuffer(boolean)
	 * @see #flush()
	 * @see #clearBuffer()
	 * @since 1.0
	 */
	public boolean isTextBufferEnabled()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getResponse().isTextBufferEnabled();
	}

	/**
	 * Prints the content of a template to the request text output. The
	 * template is first processed in the active element context by the {@link
	 * #processTemplate(Template)} method.
	 *
	 * @param template the template that will be printed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurs during the retrieval of the template content
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the output of the template content; or if you don't have
	 * access to the request data (eg. you're inside a child trigger); or if
	 * there's no active element context (eg. you're using this method inside
	 * the constructor instead of inside the {@link #initialize()} method)
	 * @see #print(Object)
	 * @see #processTemplate(Template)
	 * @since 1.0
	 */
	public void print(Template template)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");

		mElementContext.print(template);
	}

	/**
	 * Processes a template in the active element context.
	 * <p>This performs the following value replacements if they haven't
	 * already been set.
	 * <p>Each template type can have a value encoder attached to it (for
	 * instance a HTML encoder to replace non-ascii characters with the
	 * appropriate entities). All variable content that is handled in this
	 * method will be encoded before being set in the template.
	 * <table border="0">
	 * <tr>
	 * <td>{@code OGNL:ROLEUSER:valueid<br>{@code GROOVY:ROLEUSER:valueid<br>}{@code JANINO:ROLEUSER:valueid<br>}}
	 * <td>These scripted block value tags will be processed according to the
	 * active element context.
	 * <p>For example: {@code &lt;!--V 'OGNL:ROLEUSER:rolecheck'--&gt;User is
	 * no admin&lt;!--/V--&gt;<br>&lt;!--B 'OGNL:ROLEUSER:rolecheck:[[
	 * isInRole("admin") ]]'--&gt;User is admin&lt;!--/B--&gt;}
	 * <p>Will display '{@code User is admin}' if the user has the admin
	 * role, and otherwise '{@code User is no admin}'.
	 * <tr>
	 * <td>{@code EXIT:QUERY:exitname}
	 * <td>Will be replaced with the URL that links to the target of the named
	 * exit. The state will be carried around according to the currently set
	 * outputs.
	 * <tr>
	 * <td>{@code EXIT:FORM:exitname}
	 * <td>Will be replaced with the URL that links to the target of the named
	 * exit. No state information will be added to the URL. The
	 * {@code EXIT:PARAMS} value tag should be put at the location where
	 * hidden form parameters are allowed.
	 * <tr>
	 * <td>{@code EXIT:PARAMS:exitname}
	 * <td>Will be replaced by the hidden form parameters that are need to
	 * carry the state around according to the currently set outputs. This tag
	 * goes hand-in-hand with the {@code EXIT:FORM} tag.
	 * <tr>
	 * <td>{@code SUBMISSION:QUERY:submissionname}
	 * <td>Will be replaced with the URL that sends the named submission to
	 * currently the active element. The state will be carried around
	 * according to the currently set inputs.
	 * <tr>
	 * <td>{@code SUBMISSION:FORM:submissionname}
	 * <td>Will be replaced with the URL that sends the named submission to
	 * currently the active element. No state information will be added to the
	 * URL. The {@code SUBMISSION:PARAMS} value tag should be put at the
	 * location where hidden form parameters are allowed.
	 * <tr>
	 * <td>{@code SUBMISSION:PARAMS:submissionname}
	 * <td>Will be replaced by the hidden form parameters that are need to
	 * carry the state around according to the currently set inputs. This tag
	 * goes hand-in-hand with the {@code SUBMISSION:FORM} tag.
	 * <tr>
	 * <td>{@code PARAM:name}
	 * <td>Will be replaced with the encoded content of the named submission
	 * parameter.
	 * <tr>
	 * <td>{@code INPUT:name}
	 * <td>Will be replaced with the encoded content of the named input.
	 * <tr>
	 * <td>{@code OUTPUT:name}
	 * <td>Will be replaced with the encoded content of the named output.
	 * <tr>
	 * <td>{@code INCOOKIE:name}
	 * <td>Will be replaced with the encoded content of the named incookie.
	 * <tr>
	 * <td>{@code OUTCOOKIE:nam}
	 * <td>Will be replaced with the encoded content of the named outcookie.
	 * <tr>
	 * <td>{@code WEBAPP:ROOTURL}
	 * <td>Will be replaced with the absolute root URL of the web application.
	 * This is typically used in a &lt;base href=""&gt; tag. All URLs can then
	 * be relative according to this root URL and the application can be used
	 * anywhere and with any web application name.
	 * <tr>
	 * <td>automated form building for submission beans
	 * <td>see {@link com.uwyn.rife.site.FormBuilder}
	 * </table>
	 *
	 * @param template the template instance that needs to be processed
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if an
	 * error occurs during the manipulation of the template
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the retrieval of the values from the current element
	 * context, or during the output of the template content; or if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a list with the ids of all the template values that have been
	 * set
	 * @since 1.0
	 */
	public List<String> processTemplate(Template template)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");

		return new EngineTemplateProcessor(mElementContext, template).processTemplate();
	}

	/**
	 * Prints the string representation of an object to the request text
	 * output. The string representation will be created through a
	 * {@code String.valueOf(value)} call.
	 *
	 * @param value the object that will be output
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the output of the content; or if you don't have access to
	 * the request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @since 1.0
	 */
	public void print(Object value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().print(value);
	}

	/**
	 * Retrieves an output stream to send binary data through the response.
	 * <p>Note that the text output is written to the same output stream. Of
	 * course, when the text buffer is active this only happen at the end of
	 * the request.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the creation of the output stream; or if you don't have
	 * access to the request data (eg. you're inside a child trigger); or if
	 * there's no active element context (eg. you're using this method inside
	 * the constructor instead of inside the {@link #initialize()} method)
	 * @return an instance of the response output stream
	 * @since 1.0
	 */
	public OutputStream getOutputStream()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getResponse().getOutputStream();
	}

	/**
	 * Clears the request text output buffer, all buffered text will be
	 * discarded.
	 * <p>If no text buffer is active, this method doesn't do anything.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #isTextBufferEnabled()
	 * @see #enableTextBuffer(boolean)
	 * @see #flush()
	 * @since 1.0
	 */
	public void clearBuffer()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().clearBuffer();
	}

	/**
	 * Flushes the request text output buffer and the request output stream.
	 * This sends any buffered data immediately to the client.
	 * <p>All text in the active buffer will be sent to the client and the
	 * buffer will be empty again, if no text buffer is enabled only the
	 * output stream will be flushed.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurs during the output of the content; or if you don't have access to
	 * the request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #isTextBufferEnabled()
	 * @see #enableTextBuffer(boolean)
	 * @see #clearBuffer()
	 * @since 1.0
	 */
	public void flush()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().flush();
	}

	/**
	 * Retrieves the site in which this element is declared.
	 *
	 * @return a site instance
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @since 1.0
	 */
	public Site getSite()
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getSite();
	}

	/**
	 * Retrieves the deployer of this element.
	 *
	 * @return the instance of the deployer that was used to deploy the
	 * element; or
	 * <p>{@code null} if no deployment class has been declared
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getDeploymentClass()
	 * @see #setDeploymentClass(Class)
	 * @since 1.0
	 */
	public ElementDeployer getDeployer()
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getDeployer();
	}

	/**
	 * Retrieves the declaration information of this element.
	 *
	 * @return the declaration information of this element
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @since 1.0
	 */
	public ElementInfo getElementInfo()
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();
		return _getElementInfo();
	}
	
	ElementInfo _getElementInfo()
	throws EngineException
	{
		return mElementInfo;
	}
	
	/**
	 * Retrieves the source implementation name of this element.
	 *
	 * @return the source implementation name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @since 1.0
	 */
	public String getSourceName()
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getImplementation();
	}

	/**
	 * Retrieves the declaration name of this element.
	 *
	 * @return the declaration name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @since 1.0
	 */
	public String getDeclarationName()
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getDeclarationName();
	}

	/**
	 * Retrieves the information of the target element of the active request.
	 * <p>This can be different from the current element due to precedence,
	 * behavioural inheritance, child triggers, element embedding, ...
	 *
	 * @return the request's target element information
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @since 1.0
	 */
	public ElementInfo getTarget()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getTarget();
	}

	/**
	 * Retrieves the element that caused an exception to be caught by an
	 * error handler.
	 *
	 * @return the erroneous element; or
	 * <p>{@code null} if no error handler was activated
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getErrorException()
	 * @see #isErrorTarget()
	 * @since 1.6.2
	 */
	public ElementSupport getErrorElement()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getErrorElement();
	}

	/**
	 * Retrieves the exception that was caught by an error handler.
	 *
	 * @return the exception that was caught by the error handler; or
	 * <p>{@code null} if no error handler was activated
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getErrorElement()
	 * @see #isErrorTarget()
	 * @since 1.6.2
	 */
	public Throwable getErrorException()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getErrorException();
	}

	/**
	 * Indicates whether this element is executing as the target of an error
	 * handler.
	 *
	 * @return {@code true} if this element is the target of an error
	 * handler; or
	 * <p>{@code false} otherwise
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getErrorElement()
	 * @see #getErrorException()
	 * @since 1.6.2
	 */
	public boolean isErrorTarget()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getErrorException() != null;
	}

	/**
	 * Retrieves the element that is embedding the current element.
	 *
	 * @return the embedding element; or
	 * <p>{@code null} if this element is not embedded
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbedDifferentiator()
	 * @see #hasEmbedValue()
	 * @see #getEmbedValue()
	 * @see #hasEmbedData()
	 * @see #getEmbedData()
	 * @see #getEmbedProperties()
	 * @see #isEmbedded()
	 * @since 1.0
	 */
	public ElementSupport getEmbeddingElement()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		EmbeddingContext embedding_context = mElementContext.getRequestState().getEmbeddingContext();
		if (null == embedding_context)
		{
			return null;
		}

		return embedding_context.getEmbeddingElement();
	}

	/**
	 * Retrieves the template that is embedding the current element.
	 *
	 * @return the embedding template; or
	 * <p>{@code null} if this element is not embedded
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingElement()
	 * @see #getEmbedDifferentiator()
	 * @see #hasEmbedValue()
	 * @see #getEmbedValue()
	 * @see #hasEmbedData()
	 * @see #getEmbedData()
	 * @see #getEmbedProperties()
	 * @see #isEmbedded()
	 * @since 1.0
	 */
	public Template getEmbeddingTemplate()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		EmbeddingContext embedding_context = mElementContext.getRequestState().getEmbeddingContext();
		if (null == embedding_context)
		{
			return null;
		}

		return embedding_context.getTemplate();
	}

	/**
	 * Retrieves the differentiator that was used to set this embedded element apart.
	 *
	 * @return this embedded element's differentiator; or
	 * <p>{@code null} if this embedded element didn't have a differentiator
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbeddingElement()
	 * @see #hasEmbedValue()
	 * @see #getEmbedValue()
	 * @see #hasEmbedData()
	 * @see #getEmbedData()
	 * @see #getEmbedProperties()
	 * @see #isEmbedded()
	 * @since 1.0
	 */
	public String getEmbedDifferentiator()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getEmbedDifferentiator();
	}

	/**
	 * Indicates whether the embedded element's template value has content.
	 *
	 * @return {@code true} if the value has content; or
	 * <p>{@code false} otherwise
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbeddingElement()
	 * @see #getEmbedDifferentiator()
	 * @see #getEmbedValue()
	 * @see #hasEmbedData()
	 * @see #getEmbedData()
	 * @see #getEmbedProperties()
	 * @see #isEmbedded()
	 * @since 1.0
	 */
	public boolean hasEmbedValue()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getEmbedValue() != null;
	}

	/**
	 * Retrieves the current content of the value tag of this embedded element.
	 * <p>For example:
	 * <pre>&lt;!--V 'ELEMENT:my.element'--&gt;this is the embed value&lt;!--/V--&gt;</pre>
	 * <p>Will return:
	 * <pre>this is the embed value</pre>
	 * <p>Note that when you have several embedded elements in the same
	 * template with the same element id, you have to use embedded element
	 * differentiators if you want to provide different embed values. For
	 * example:
	 * <pre>&lt;!--V 'ELEMENT:my.element:differentiator1'--&gt;this is the first embed value&lt;!--/V--&gt;
	 *&lt;!--V 'ELEMENT:my.element:differentiator2'--&gt;this is the second embed value&lt;!--/V--&gt;</pre>
	 *
	 * @return the value from the embedded template; or
	 * <p>null if no default value was provided or if the current element is
	 * not embedded
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbeddingElement()
	 * @see #getEmbedDifferentiator()
	 * @see #hasEmbedValue()
	 * @see #getEmbedData()
	 * @see #hasEmbedData()
	 * @see #getEmbedProperties()
	 * @see #isEmbedded()
	 * @since 1.0
	 */
	public String getEmbedValue()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getEmbedValue();
	}

	/**
	 * Indicates whether data was passed on during the processing of this embedded element.
	 *
	 * @return {@code true} if data was passed on; or
	 * <p>{@code false} otherwise
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbeddingElement()
	 * @see #getEmbedDifferentiator()
	 * @see #hasEmbedValue()
	 * @see #getEmbedValue()
	 * @see #getEmbedData()
	 * @see #getEmbedProperties()
	 * @see #isEmbedded()
	 * @since 1.0
	 */
	public boolean hasEmbedData()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getEmbedValue() != null;
	}

	/**
	 * Retrieves the data that was passed on for the processing of this embedded element.
	 * @return the value from the embedded template; or
	 * <p>null if no data was provided or if the current element is
	 * not embedded
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbeddingElement()
	 * @see #getEmbedDifferentiator()
	 * @see #getEmbedValue()
	 * @see #hasEmbedValue()
	 * @see #hasEmbedData()
	 * @see #getEmbedProperties()
	 * @see #isEmbedded()
	 * @since 1.5
	 */
	public Object getEmbedData()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getEmbedData();
	}

	/**
	 * Retrieves the embed value as a property list (see {@link
	 * #getEmbedValue()} for more information about embed values).
	 * <p>The content of the embed value will be parsed as a property list
	 * according to the format described in {@link
	 * java.util.Properties#load(java.io.InputStream)}.
	 * <p>For instance:
	 * <pre>&lt;!--V 'ELEMENT:my.element'--&gt;
	 *key1 = value1
	 *key2 = value1
	 *&lt;!--/V--&gt;</pre>
	 * <p>Will return a property list where the key '{@code key1}' is
	 * associated to '{@code value1}' and '{@code key2}' to '{@code value2}'.
	 *
	 * @return the embed value parsed as a property list; or
	 * <p>null if no default value was provided, if the current element is not
	 * embedded
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbeddingElement()
	 * @see #getEmbedDifferentiator()
	 * @see #hasEmbedValue()
	 * @see #getEmbedValue()
	 * @see #hasEmbedData()
	 * @see #isEmbedded()
	 * @since 1.0
	 */
	public Properties getEmbedProperties()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getEmbedProperties();
	}
	
	/**
	 * Indicates whether this element is running embedded inside another
	 * element's template.
	 *
	 * @return {@code true} if this element is embedded; or
	 * <p>{@code false} otherwise
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getEmbeddingTemplate()
	 * @see #getEmbeddingElement()
	 * @see #getEmbedDifferentiator()
	 * @see #hasEmbedValue()
	 * @see #getEmbedValue()
	 * @see #hasEmbedData()
	 * @see #getEmbedProperties()
	 * @since 1.4
	 */
	public boolean isEmbedded()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();
		
		return mElementContext.getRequestState().isEmbedded();
	}
	
	/**
	 * Indicates whether this element has a certain injected named property.
	 *
	 * @param name the name of the property
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return {@code true} if the element contains the property; or
	 * <p>{@code false} otherwise
	 * @see #getProperty(String)
 	 * @see #getProperty(String, Object)
 	 * @see #getPropertyTyped(String, Class)
 	 * @see #getPropertyTyped(String, Class, Object)
	 * @see #getPropertyString(String)
	 * @see #getPropertyString(String, String)
	 * @see #isPropertyEmpty(String)
	 * @since 1.0
	 */
	public boolean hasProperty(String name)
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.containsProperty(name);
	}

	/**
	 * Retrieves the value of an injected named property.
	 * <p>Note that there are two types of properties, fixed value properties
	 * ({@link com.uwyn.rife.ioc.PropertyValueObject string literals}) and dynamic value
	 * properties ({@link com.uwyn.rife.ioc.PropertyValueParticipant participant objects},
	 * {@link com.uwyn.rife.ioc.PropertyValueTemplate template instances}, ...). The fixed value
	 * is set during the declaration of the property and the dynamic value is
	 * retrieved or instantiated each time the property value is obtained.
	 * <p>Property values can be of any type and class. If the resulting value
	 * needs to be a certain standard type or primitive value, use the {@link
	 * com.uwyn.rife.tools.Convert} helper class to perform the conversion
	 * in-line. Since properties are very often used as string literals,
	 * there's {@link #getPropertyString(String)} method to make this more
	 * convenient.
	 *
	 * @param name the name of the property
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the property value; or
	 * <p>{@code null} if no such property exists
	 * @see #hasProperty(String)
 	 * @see #getProperty(String, Object)
 	 * @see #getPropertyTyped(String, Class)
 	 * @see #getPropertyTyped(String, Class, Object)
	 * @see #getPropertyString(String)
	 * @see #getPropertyString(String, String)
	 * @see #isPropertyEmpty(String)
	 * @since 1.0
	 */
	public Object getProperty(String name)
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getProperty(name);
	}

	/**
	 * Retrieves the value of an injected named property, using a default value
	 * as fallback.
	 *
	 * @param name the name of the property
	 * @param defaultValue the value that should be used if the
	 * property can't be found
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the property value; or
	 * <p>the default value if no such property exists
	 * @see #hasProperty(String)
 	 * @see #getProperty(String)
 	 * @see #getPropertyTyped(String, Class)
 	 * @see #getPropertyTyped(String, Class, Object)
	 * @see #getPropertyString(String)
	 * @see #getPropertyString(String, String)
	 * @see #isPropertyEmpty(String)
	 * @since 1.0
	 */
	public Object getProperty(String name, Object defaultValue)
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getProperty(name, defaultValue);
	}

	/**
	 * Retrieves the value of an injected named property and converts it to the
	 * specified type.
	 * <p>This method has advantages over a regular cast, since it throws a
	 * meaningful exception to the user in case the type of the property value
	 * is not compatible.
	 *
	 * @param name the name of the property
	 * @param type the class you want the property to be converted to
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @exception com.uwyn.rife.ioc.exceptions.IncompatiblePropertyValueTypeException
	 * if the type of the property value wasn't compatible with the requested type
	 * @return the property value, casted to the requested type; or
	 * <p>{@code null} if no such property exists
	 * @see #hasProperty(String)
	 * @see #getProperty(String)
 	 * @see #getProperty(String, Object)
 	 * @see #getPropertyTyped(String, Class, Object)
	 * @see #getPropertyString(String)
	 * @see #getPropertyString(String, String)
	 * @see #isPropertyEmpty(String)
	 * @since 1.3
	 */
	public <T> T getPropertyTyped(String name, Class<T> type)
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return (T)mElementInfo.getPropertyTyped(name, type);
	}

	/**
	 * Retrieves the value of an injected named property and converts it to the
	 * specified type, using a default value as fallback.
	 * <p>This method has advantages over a regular cast, since it throws a
	 * meaningful exception to the user in case the type of the property value
	 * is not compatible.
	 *
	 * @param name the name of the property
	 * @param type the class you want the property to be converted to
	 * @param defaultValue the object that should be used if the
	 * property can't be found
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @exception com.uwyn.rife.ioc.exceptions.IncompatiblePropertyValueTypeException
	 * if the type of the property value wasn't compatible with the requested type
	 * @return the property value, casted to the requested type; or
	 * <p>the default value if no such property exists
	 * @see #hasProperty(String)
	 * @see #getProperty(String)
 	 * @see #getProperty(String, Object)
 	 * @see #getPropertyTyped(String, Class)
	 * @see #getPropertyString(String)
	 * @see #getPropertyString(String, String)
	 * @see #isPropertyEmpty(String)
	 * @since 1.3
	 */
	public <T> T getPropertyTyped(String name, Class<T> type, T defaultValue)
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return (T)mElementInfo.getPropertyTyped(name, type, defaultValue);
	}

	/**
	 * Retrieves the value of an injected named property and converts it to a
	 * string.
	 *
	 * @param name the name of the property
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the string representation of the property value; or
	 * <p>{@code null} if no such property exists
	 * @see #hasProperty(String)
	 * @see #getProperty(String)
 	 * @see #getProperty(String, Object)
 	 * @see #getPropertyTyped(String, Class)
 	 * @see #getPropertyTyped(String, Class, Object)
	 * @see #getPropertyString(String, String)
	 * @see #isPropertyEmpty(String)
	 * @since 1.0
	 */
	public String getPropertyString(String name)
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getPropertyString(name);
	}

	/**
	 * Retrieves the value of an injected named property and converts it to a
	 * string, using a default value as fallback.
	 *
	 * @param name the name of the property
	 * @param defaultValue the string literal that should be used if the
	 * property can't be found
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the string representation of the property value; or
	 * <p>the default value if no such property exists or when the resulting
	 * string is empty
	 * @see #hasProperty(String)
	 * @see #getProperty(String)
 	 * @see #getProperty(String, Object)
 	 * @see #getPropertyTyped(String, Class)
 	 * @see #getPropertyTyped(String, Class, Object)
	 * @see #getPropertyString(String)
	 * @see #isPropertyEmpty(String)
	 * @since 1.0
	 */
	public String getPropertyString(String name, String defaultValue)
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.getPropertyString(name, defaultValue);
	}

	/**
	 * Checks if a property is not available or if the string presentation is
	 * empty.
	 *
	 * @param name the name of the property
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if the
	 * element information hasn't been provided yet (eg. you're using this
	 * method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return {@code true} if the property is empty; or
	 * <p>{@code false} otherwise
	 * @see #hasProperty(String)
	 * @see #getProperty(String)
 	 * @see #getProperty(String, Object)
 	 * @see #getPropertyTyped(String, Class)
 	 * @see #getPropertyTyped(String, Class, Object)
	 * @see #getPropertyString(String)
	 * @see #getPropertyString(String, String)
	 * @since 1.0
	 */
	public boolean isPropertyEmpty(String name)
	throws EngineException
	{
		if (null == mElementInfo)       throw new ElementInfoMissingException();

		return mElementInfo.isPropertyEmpty(name);
	}

	/**
	 * Retrieves an instance of a named input bean and populates the
	 * properties with the input values. The class of the bean is looked up
	 * through its name, as is the property prefix.
	 * <p>This bean is not serialized or deserialized, each property
	 * corresponds to an input and is individually sent by the client.
	 *
	 * @param name the name of the input bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * bean is known with this name; or if an error occurred during the
	 * instantiation of the bean; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the populated input bean instance
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getNamedInputBean(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return (BeanType)mElementContext.getNamedInputBean(name);
	}

	/**
	 * Retrieves an instance of an input bean and populates the properties
	 * with the input values.
	 * <p>This bean is not serialized or de-serialized, each property
	 * corresponds to an input and is individually sent by the client.
	 *
	 * @param beanClass the class of the input bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the instantiation of the bean; or if you don't have
	 * access to the request data (eg. you're inside a child trigger); or if
	 * there's no active element context (eg. you're using this method inside
	 * the constructor instead of inside the {@link #initialize()} method)
	 * @return the populated input bean instance
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getInputBean(Class<BeanType> beanClass)
	throws EngineException
	{
		return getInputBean(beanClass, null);
	}

	/**
	 * Retrieves an instance of an input bean and populates the properties
	 * with the input values, taking the provided prefix into account.
	 * <p>This bean is not serialized or de-serialized, each property
	 * corresponds to an input and is individually sent by the client.
	 *
	 * @param beanClass the class of the input bean
	 * @param prefix the prefix that will be put in front of each property
	 * name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the instantiation of the bean; or if you don't have
	 * access to the request data (eg. you're inside a child trigger); or if
	 * there's no active element context (eg. you're using this method inside
	 * the constructor instead of inside the {@link #initialize()} method)
	 * @return the populated input bean instance
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getInputBean(Class<BeanType> beanClass, String prefix)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == beanClass)              throw new IllegalArgumentException("beanClass can't be null.");

		return mElementContext.getInputBean(beanClass, prefix);
	}

	/**
	 * Checks whether a value has been provided to an input.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the input has a value; or
	 * <p>{@code false} otherwise
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public boolean hasInputValue(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.hasInputValue(name);
	}

	/**
	 * Checks whether an input has no value or whether the value is empty.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the input has no value or when the value
	 * is empty; or
	 * <p>{@code false} otherwise
	 * @see #hasInputValue(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public boolean isInputEmpty(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.isInputEmpty(name);
	}

	/**
	 * Retrieves the value of an input.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the value of the input; or
	 * <p>{@code null} if no value is present for this input
	 * @since 1.0
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 */
	public String getInput(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getInput(name);
	}

	/**
	 * Retrieves the value of an input and returns a default value if no input
	 * value is present
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the input value; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public String getInput(String name, String defaultValue)
	throws EngineException
	{
		String input = getInput(name);
		if (input == null)
		{
			return defaultValue;
		}
		return input;
	}
	
	/**
	 * Retrieves the value of a serialized input.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the deserialized input
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInputValues(String)
	 * @since 1.3.1
	 */
	public <TargetType extends Serializable> TargetType getInputSerializable(String name)
	throws EngineException
	{
		return (TargetType)getInputSerializable(name, null);
	}
	
	/**
	 * Retrieves the value of a serialized input and returns a default value if no input
	 * value is present
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the deserialized input; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInputValues(String)
	 * @since 1.3.1
	 */
	public <TargetType extends Serializable> TargetType getInputSerializable(String name, TargetType defaultValue)
	throws EngineException
	{
		String input = getInput(name);
		if (input == null)
		{
			return defaultValue;
		}
		
		try
		{
			return (TargetType)SerializationUtils.deserializeFromString(input);
		}
		catch (SerializationUtilsErrorException e)
		{
			throw new InputsDeserializationException(getDeclarationName(), name, e);
		}
	}
	
	/**
	 * Retrieves the value of an input and converts it to a Date.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>{@code null} if the input didn't have a value
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.6.1
	 */
	public Date getInputDate(String name)
	throws EngineException
	{
		return getInputDate(name, null);
	}
	
	/**
	 * Retrieves the value of an input and converts it to a Date, using a
	 * default value if no input value is present.
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.6.1
	 */
	public Date getInputDate(String name, Date defaultValue)
	throws EngineException
	{
		String input = getInput(name);
		if (input == null)
		{
			return defaultValue;
		}
		
		try
		{
			return Convert.toDate(input);
		}
		catch (ConversionException e)
		{
			throw new EngineException(e);
		}
	}
	
	/**
	 * Retrieves the values of an input.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return a string array with all the input values; or
	 * <p>{@code null} if no input values are present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @since 1.0
	 */
	public String[] getInputValues(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getInputValues(name);
	}

	/**
	 * Retrieves the value of an input and converts it to a boolean.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>{@code false} if no input value is present or if the input
	 * value is not a valid boolean
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public boolean getInputBoolean(String name)
	throws EngineException
	{
		return getInputBoolean(name, ElementInfo.DEFAULT_BOOLEAN);
	}

	/**
	 * Retrieves the value of an input and converts it to a boolean, using a
	 * default value if no input value is present.
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public boolean getInputBoolean(String name, boolean defaultValue)
	throws EngineException
	{
		String value = getInput(name);
		if (value == null)
		{
			return defaultValue;
		}

		return StringUtils.convertToBoolean(value);
	}

	/**
	 * Retrieves the value of an input and converts it to an integer.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>{@code 0} if no input value is present or if the input value is
	 * not a valid integer
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public int getInputInt(String name)
	throws EngineException
	{
		return getInputInt(name, ElementInfo.DEFAULT_INTEGER);
	}

	/**
	 * Retrieves the value of an input and converts it to an integer, using a
	 * default value if no input value is present.
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public int getInputInt(String name, int defaultValue)
	throws EngineException
	{
		String value = getInput(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of an input and converts it to a long.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>{@code 0L} if no input value is present or if the input value
	 * is not a valid long
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public long getInputLong(String name)
	throws EngineException
	{
		return getInputLong(name, ElementInfo.DEFAULT_LONG);
	}

	/**
	 * Retrieves the value of an input and converts it to a long, using a
	 * default value if no input value is present.
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public long getInputLong(String name, long defaultValue)
	throws EngineException
	{
		String value = getInput(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of an input and converts it to a double.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>{@code 0.0d} if no input value is present or if the input value
	 * is not a valid double
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public double getInputDouble(String name)
	throws EngineException
	{
		return getInputDouble(name, ElementInfo.DEFAULT_DOUBLE);
	}

	/**
	 * Retrieves the value of an input and converts it to a double, using a
	 * default value if no input value is present.
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public double getInputDouble(String name, double defaultValue)
	throws EngineException
	{
		String value = getInput(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of an input and converts it to a float.
	 *
	 * @param name the name of the input
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>{@code 0.0f} if no input value is present or if the input value
	 * is not a valid float
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public float getInputFloat(String name)
	throws EngineException
	{
		return getInputFloat(name, ElementInfo.DEFAULT_FLOAT);
	}

	/**
	 * Retrieves the value of an input and converts it to a float, using a
	 * default value if no input value is present.
	 *
	 * @param name the name of the input
	 * @param defaultValue the default value that will be used when no input
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no input
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the converted input value; or
	 * <p>the default value if no input value is present
	 * @see #hasInputValue(String)
	 * @see #isInputEmpty(String)
	 * @see #getNamedInputBean(String)
	 * @see #getInputBean(Class)
	 * @see #getInputBean(Class, String)
	 * @see #getInput(String)
	 * @see #getInput(String, String)
	 * @see #getInputValues(String)
	 * @since 1.0
	 */
	public float getInputFloat(String name, float defaultValue)
	throws EngineException
	{
		String value = getInput(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Sets a select box option, a radio button or a checkbox to selected or
	 * checked according to input values.
	 * <p>The actual logic is performed by the {@link
	 * #selectParameter(Template, String, String[])} method. This method only
	 * prefixes the parameter name with the {@code INPUT:} literal, which
	 * is the syntax that is used to be able to handle automatic population
	 * correctly for each value type (inputs or submission parameters).
	 * <p>This method is automatically called during the {@link
	 * #print(Template)} for all the inputs and values that this element
	 * received. You should thus only call it explicitly if you need it to be
	 * executed with custom values.
	 *
	 * @param template the template instance where the selection should happen
	 * @param name the name of the input
	 * @param values the values that should selected or checked
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a list with the identifiers of the template values that have
	 * been set, this is never {@code null}, when no values are set an
	 * empty list is returned
	 * @see #selectParameter(Template, String, String[])
	 * @see #selectSubmissionParameter(Template, String, String[])
	 * @since 1.0
	 */
	public Collection<String> selectInputParameter(Template template, String name, String[] values)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");
		if (null == name)       throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty.");

		return EngineTemplateHelper.selectInputParameter(template, name, values);
	}

	/**
	 * Sets the value of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, String value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null == value)                  throw new IllegalArgumentException("value can't be null.");

		mElementContext.setOutput(name, value);
	}

	/**
	 * Set the values of an output.
	 *
	 * @param name the name of the output
	 * @param values the values that have to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, String[] values)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null == values)                 throw new IllegalArgumentException("values can't be null.");
		if (0 == values.length)             throw new IllegalArgumentException("values can't be empty.");

		mElementContext.setOutput(name, values);
	}

	/**
	 * Sets the value of an output from a {@code boolean}.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, boolean value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, String.valueOf(value));
	}

	/**
	 * Sets the value of an output from a {@code char}.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, char value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, String.valueOf(value));
	}

	/**
	 * Set the value of an output from an array of {@code char}s that
	 * will be concatenated to a {@code String}.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, char[] value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, String.valueOf(value));
	}

	/**
	 * Sets the value of an output from a {@code double}.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, double value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, String.valueOf(value));
	}

	/**
	 * Sets the value of an output from a {@code float}.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, float value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, String.valueOf(value));
	}

	/**
	 * Sets the value of an output from an {@code int}.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, int value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, String.valueOf(value));
	}

	/**
	 * Sets the value of an output from a {@code long}.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, long value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, String.valueOf(value));
	}

	/**
	 * Sets the value of an output from a generic {@code object}. The object
	 * will be converted to its {@code String} representation.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutput(String name, Object value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.setOutput(name, value, null);
	}

	/**
	 * Adds a value to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, String value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null == value)                  throw new IllegalArgumentException("value can't be null.");

		mElementContext.addOutputValue(name, value);
	}

	/**
	 * Adds values to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param values the values that have to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValues(String name, String[] values)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null == values)                 throw new IllegalArgumentException("values can't be null.");
		if (0 == values.length)             throw new IllegalArgumentException("values can't be empty.");

		mElementContext.addOutputValues(name, values);
	}

	/**
	 * Adds a {@code boolean} value to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, boolean value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, String.valueOf(value));
	}

	/**
	 * Adds a {@code char} value to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, char value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, String.valueOf(value));
	}

	/**
	 * Adds values from an array of {@code char}s to the current values
	 * of an output.
	 *
	 * @param name the name of the output
	 * @param value the values that have to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, char[] value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, String.valueOf(value));
	}

	/**
	 * Adds a {@code double} value to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, double value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, String.valueOf(value));
	}

	/**
	 * Adds a {@code float} value to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, float value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, String.valueOf(value));
	}

	/**
	 * Adds a {@code int} value to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, int value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, String.valueOf(value));
	}

	/**
	 * Adds a {@code long} value to the current values of an output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, long value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, String.valueOf(value));
	}

	/**
	 * Adds a generic {@code object} value to the current values of an
	 * output.
	 *
	 * @param name the name of the output
	 * @param value the value that has to be added
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void addOutputValue(String name, Object value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.addOutputValue(name, value);
	}

	/**
	 * Sets an instance of a named output bean and populates the output values
	 * from the property values. The class of the bean is looked up through
	 * its name, as is the property prefix.
	 * <p>This bean is not serialized or deserialized, each output corresponds
	 * to a property and is individually sent to the client.
	 *
	 * @param name the name of the output bean
	 * @param bean the bean instance that should be used to set the outputs
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * bean is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setNamedOutputBean(String name, Object bean)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null == bean)                   throw new IllegalArgumentException("bean can't be null.");

		mElementContext.setNamedOutputBean(name, bean);
	}

	/**
	 * Sets an instance of a named input bean and populates the output values
	 * from the property values.
	 * <p>This bean is not serialized or deserialized, each output corresponds
	 * to a property and is individually sent to the client.
	 *
	 * @param bean the bean instance that should be used to set the outputs
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutputBean(Object bean)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == bean)                   throw new IllegalArgumentException("bean can't be null.");

		mElementContext.setOutputBean(bean, null);
	}

	/**
	 * Sets an instance of a named input bean and populates the output values
	 * from the property values.
	 * <p>This bean is not serialized or deserialized, each output corresponds
	 * to a property and is individually sent to the client.
	 *
	 * @param bean the bean instance that should be used to set the outputs
	 * @param prefix the prefix that will be put in front of each property
	 * name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void setOutputBean(Object bean, String prefix)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == bean)                   throw new IllegalArgumentException("bean can't be null.");

		mElementContext.setOutputBean(bean, prefix);
	}

	/**
	 * Clears the output value of an output.
	 *
	 * @param name the name of the output
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if no output is known with this name; or if
	 * you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void clearOutput(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.clearOutput(name);
	}

	/**
	 * Clears the outputs that correspond to the properties of a named output
	 * bean. The class of the bean is looked up through its name, as is the
	 * property prefix.
	 *
	 * @param name the name of the output bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * bean is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void clearNamedOutputBean(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		mElementContext.clearNamedOutputBean(name);
	}

	/**
	 * Clears the outputs that correspond to the properties of an output bean.
	 *
	 * @param beanClass the class of the output bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * bean is known with this name; if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class, String)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void clearOutputBean(Class beanClass)
	throws EngineException
	{
		clearOutputBean(beanClass, null);
	}

	/**
	 * Clears the outputs that correspond to the properties of an output bean,
	 * taking the provided prefix into account.
	 *
	 * @param beanClass the class of the output bean
	 * @param prefix the prefix that will be put in front of each property
	 * name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #getOutput(String)
	 * @since 1.0
	 */
	public void clearOutputBean(Class beanClass, String prefix)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == beanClass)      throw new IllegalArgumentException("beanClass can't be null.");

		mElementContext.clearOutputBean(beanClass, prefix);
	}
	
	/**
	 * Retrieves the value of the ouput.
	 *
	 * @param name the name of the output
	 * @return the textual value of the output as it's used by framework; or
	 * {@code null} if the output couldn't be found.
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no output
	 * is known with this name; if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @see #setNamedOutputBean(String, Object)
	 * @see #setOutputBean(Object)
	 * @see #setOutputBean(Object, String)
	 * @see #setOutput(String, String)
	 * @see #setOutput(String, String[])
	 * @see #addOutputValue(String, String)
	 * @see #addOutputValues(String, String[])
	 * @see #clearOutput(String)
	 * @see #clearNamedOutputBean(String)
	 * @see #clearOutputBean(Class)
	 * @see #clearOutputBean(Class, String)
	 * @since 1.6
	 */
	public String[] getOutput(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();
		
		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		
		return mElementContext.getOutput(name);
	}
	
	/**
	 * Checks whether a cookie is present.
	 *
	 * @param name the name of the cookie
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the cookie was present; or
	 * <p>{@code false} otherwise
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public boolean hasCookie(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.hasCookie(name);
	}

	/**
	 * Retrieves a cookie.
	 *
	 * @param name the name of the cookie.
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the instance of the cookie; or
	 * <p>{@code null} if no such cookie is present
	 * @see #hasCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public Cookie getCookie(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getCookie(name);
	}

	/**
	 * Retrieves the value of a cookie.
	 *
	 * @param name the name of the cookie
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the value of the cookie; or
	 * <p>{@code null} if no such cookie is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public String getCookieValue(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getCookieValue(name);
	}

	/**
	 * Retrieves all current cookies names with their values.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a new map of all the current cookies names with their values
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public Map<String, String> getCookieValues()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.collectCookieValues();
	}

	/**
	 * Retrieves the value of a named cookie, using a default value as
	 * fallback.
	 *
	 * @param name the name of the cookie
	 * @param defaultValue the default value that will be used when no cookie
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the cookie value; or
	 * <p>the default value if no cookie value is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public String getCookieValue(String name, String defaultValue)
	throws EngineException
	{
		String value = getCookieValue(name);
		if (value == null)
		{
			return defaultValue;
		}
		return value;
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a boolean.
	 *
	 * @param name the name of the cookie
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>{@code false} if no cookie value is present or if the cookie
	 * value is not a valid boolean
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public boolean getCookieValueBoolean(String name)
	throws EngineException
	{
		return getCookieValueBoolean(name, ElementInfo.DEFAULT_BOOLEAN);
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a boolean,
	 * using a default value if no input value is present.
	 *
	 * @param name the name of the cookie
	 * @param defaultValue the default value that will be used when no cookie
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>the default value if no cookie value is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public boolean getCookieValueBoolean(String name, boolean defaultValue)
	throws EngineException
	{
		String value = getCookieValue(name);
		if (value == null)
		{
			return defaultValue;
		}

		return StringUtils.convertToBoolean(value);
	}

	/**
	 * Retrieves the value of a named cookie and converts it to an integer.
	 *
	 * @param name the name of the cookie
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>{@code 0} if no cookie value is present or if the cookie value
	 * is not a valid integer
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public int getCookieValueInt(String name)
	throws EngineException
	{
		return getCookieValueInt(name, ElementInfo.DEFAULT_INTEGER);
	}

	/**
	 * Retrieves the value of a named cookie and converts it to an integer,
	 * using a default value if no input value is present.
	 *
	 * @param name the name of the cookie
	 * @param defaultValue the default value that will be used when no cookie
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>the default value if no cookie value is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public int getCookieValueInt(String name, int defaultValue)
	throws EngineException
	{
		String value = getCookieValue(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a long.
	 *
	 * @param name the name of the cookie
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>{@code 0L} if no cookie value is present or if the cookie value
	 * is not a valid long
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public long getCookieValueLong(String name)
	throws EngineException
	{
		return getCookieValueLong(name, ElementInfo.DEFAULT_LONG);
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a long, using
	 * a default value if no input value is present.
	 *
	 * @param name the name of the cookie
	 * @param defaultValue the default value that will be used when no cookie
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>the default value if no cookie value is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public long getCookieValueLong(String name, long defaultValue)
	throws EngineException
	{
		String value = getCookieValue(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a double.
	 *
	 * @param name the name of the cookie
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>{@code 0.0d} if no cookie value is present or if the cookie
	 * value is not a valid double
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public double getCookieValueDouble(String name)
	throws EngineException
	{
		return getCookieValueDouble(name, ElementInfo.DEFAULT_DOUBLE);
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a double,
	 * using a default value if no input value is present.
	 *
	 * @param name the name of the cookie
	 * @param defaultValue the default value that will be used when no cookie
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>the default value if no cookie value is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public double getCookieValueDouble(String name, double defaultValue)
	throws EngineException
	{
		String value = getCookieValue(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a float.
	 *
	 * @param name the name of the cookie
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>{@code 0.0}f if no cookie value is present or if the cookie
	 * value is not a valid float
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public float getCookieValueFloat(String name)
	throws EngineException
	{
		return getCookieValueFloat(name, ElementInfo.DEFAULT_FLOAT);
	}

	/**
	 * Retrieves the value of a named cookie and converts it to a float, using
	 * a default value if no input value is present.
	 *
	 * @param name the name of the cookie
	 * @param defaultValue the default value that will be used when no cookie
	 * value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * incookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted cookie value; or
	 * <p>the default value if no cookie value is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public float getCookieValueFloat(String name, float defaultValue)
	throws EngineException
	{
		String value = getCookieValue(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Sets a cookie which will be sent to the browser.
	 * <p>Cookies are handles outside of the web engine's data flow
	 * management. They are preserved by the browser and are automatically
	 * provided at each request.
	 *
	 * @param cookie the cookie instance that will be set
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * outcookie is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookieValue(String, String)
	 * @see #getCookieValues()
	 * @see #setCookie(Cookie)
	 * @since 1.0
	 */
	public void setCookie(Cookie cookie)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == cookie)                 throw new IllegalArgumentException("cookie can't be null.");
		if (null == cookie.getName())       throw new IllegalArgumentException("cookie name can't be empty.");

		mElementContext.setCookie(cookie);
	}

	/**
	 * Returns the unique identifier of the current continuation.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger)
	 * @return the unique identifier of the current continuations; or
	 * <p>{@code null} if no continuation is active
	 * @since 1.0
	 */
	public String getContinuationId()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();

		return ContinuationContext.getActiveContextId();
	}

	/**
	 * Generates a query URL for an exit.
	 *
	 * @param name the name of the exit
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitQueryUrl(String name)
	throws EngineException
	{
		return getExitQueryUrl(name, null, null);
	}

	/**
	 * Generates a query URL for an exit and appends a pathinfo to the URL of
	 * the destination element.
	 *
	 * @param name the name of the exit
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitQueryUrl(String name, String pathinfo)
	throws EngineException
	{
		return getExitQueryUrl(name, pathinfo, null);
	}

	/**
	 * Generates a query URL for an exit and overrides the current output
	 * values only for this method.
	 *
	 * @param name the name of the exit
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitQueryUrl(String name, String[] outputValues)
	throws EngineException
	{
		return getExitQueryUrl(name, null, outputValues);
	}

	/**
	 * Generates a query URL for an exit and appends a pathinfo to the URL of
	 * the destination element. The current output values can be overridden
	 * for this method alone.
	 * <p>This will take the current element context into account with the
	 * available outputs, global variables, ... and generate an URL that
	 * persists the data state according to the declared site structure.
	 * <p>The output values are provided as an array of strings that should be
	 * structured in pairs. For example, if these output values should be
	 * used: {@code output1}:{@code value1} and {@code output2}:{@code value2},
	 * you should define the following string array:
	 * <pre>new String[] {"output1", "value1", "output2", "value2"}</pre>
	 * <p>The generated URL with not contain a scheme, host or port. It will
	 * begin with the path part and be absolute, starting with the web
	 * application's root URL.
	 *
	 * @param name the name of the exit
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitQueryUrl(String name, String pathinfo, String[] outputValues)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != outputValues &&
			outputValues.length % 2 > 0)    throw new IllegalArgumentException("outputValues should be a series of key/value pairs.");

		return ElementContextFlowGeneration.generateExitQueryUrl(mElementContext,
																 mElementContext.getElementInfo().validateAndRetrieveFlowLink(name),
																 pathinfo, mElementContext.getOutputs().aggregateValues(), outputValues);
	}

	/**
	 * Generates a form action URL for an exit.
	 *
	 * @param name the name of the exit
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitFormUrl(String name)
	throws EngineException
	{
		return getExitFormUrl(name, null);
	}

	/**
	 * Generates a form action URL for an exit and appends a pathinfo to the
	 * URL of the destination element.
	 * <p>This will take the current element context into account with the
	 * available outputs, global variables, ... and generate an URL that
	 * persists the data state according to the declared site structure.
	 * <p>The generated URL with not contain a scheme, host or port. It will
	 * begin with the path part and be absolute, starting with the web
	 * application's root URL.
	 * <p>This method goes together with the {@link
	 * #getExitFormParameters(String, String[])} method since the state is
	 * tranferred as hidden form parameters that are part of the form.
	 *
	 * @param name the name of the exit
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitFormUrl(String name, String pathinfo)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return ElementContextFlowGeneration.generateExitFormUrl(mElementContext,
																mElementContext.getElementInfo().validateAndRetrieveFlowLink(name),
																pathinfo, mElementContext.getOutputs().aggregateValues());
	}

	/**
	 * Generates the XHTML hidden form parameters for an exit.
	 *
	 * @param name the name of the exit
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated parameters as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitFormParameters(String name)
	throws EngineException
	{
		return getExitFormParameters(name, null);
	}

	/**
	 * Generates the hidden XHTML form parameters for an exit and overrides
	 * the current output values only for this method.
	 * <p>This will take the current element context into account with the
	 * available outputs, global variables, ... and generate hidden XHTML form
	 * parameters that persist the data state according to the declared site
	 * structure.
	 * <p>The output values are provided as an array of strings that should be
	 * structured in pairs. For example, if these output values should be
	 * used: {@code output1}:{@code value1} and {@code output2}:{@code value2},
	 * you should define the following string array:
	 * <pre>new String[] {"output1", "value1", "output2", "value2"}</pre>
	 * <p>This method goes together with the {@link
	 * #getExitFormUrl(String, String)} method since the URL needs to be
	 * provided in the action attribute of the form.
	 *
	 * @param name the name of the exit
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated parameters as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParametersJavascript(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getExitFormParameters(String name, String[] outputValues)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != outputValues &&
			outputValues.length % 2 > 0)    throw new IllegalArgumentException("outputValues should be a series of key/value pairs.");

		return ElementContextFlowGeneration.generateExitFormParameters(mElementContext,
																	   mElementContext.getElementInfo().validateAndRetrieveFlowLink(name),
																	   mElementContext.getOutputs().aggregateValues(), outputValues);
	}

	/**
	 * Generates Javascript that will generate hidden XHTML form parameters for
	 * an exit and overrides the current output values only for this method.
	 * <p>This will take the current element context into account with the
	 * available outputs, global variables, ... and generate hidden XHTML form
	 * parameters that persist the data state according to the declared site
	 * structure.
	 * <p>The output values are provided as an array of strings that should be
	 * structured in pairs. For example, if these output values should be
	 * used: {@code output1}:{@code value1} and {@code output2}:{@code value2},
	 * you should define the following string array:
	 * <pre>new String[] {"output1", "value1", "output2", "value2"}</pre>
	 * <p>This method goes together with the {@link
	 * #getExitFormUrl(String, String)} method since the URL needs to be
	 * provided in the action attribute of the form.
	 *
	 * @param name the name of the exit
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the generated parameters as a character sequence
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.6
	 */
	public CharSequence getExitFormParametersJavascript(String name, String[] outputValues)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != outputValues &&
			outputValues.length % 2 > 0)    throw new IllegalArgumentException("outputValues should be a series of key/value pairs.");

		return ElementContextFlowGeneration.generateExitFormParametersJavascript(mElementContext,
																				 mElementContext.getElementInfo().validateAndRetrieveFlowLink(name),
																				 mElementContext.getOutputs().aggregateValues(), outputValues);
	}

	/**
	 * Generates a query URL for an exit and sets it as the content of a
	 * template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code EXIT:QUERY:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public void setExitQuery(Template template, String name)
	throws TemplateException, EngineException
	{
		setExitQuery(template, name, null, null);
	}

	/**
	 * Generates a query URL with a pathinfo for an exit and sets it as the
	 * content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code EXIT:QUERY:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public void setExitQuery(Template template, String name, String pathinfo)
	throws TemplateException, EngineException
	{
		setExitQuery(template, name, pathinfo, null);
	}

	/**
	 * Generates a query URL for an exit with overridden outputs and sets it
	 * as the content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code EXIT:QUERY:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public void setExitQuery(Template template, String name, String[] outputValues)
	throws TemplateException, EngineException
	{
		setExitQuery(template, name, null, outputValues);
	}

	/**
	 * Generates a query URL for an exit with a pathinfo and overridden
	 * outputs and sets it as the content of a template value.
	 * <p>The URL will be generated by calling the {@link
	 * #getExitQueryUrl(String, String, String[])} method and it will be set
	 * to the value identifier with the syntax
	 * {@code EXIT:QUERY:exitname}.
	 * <p>Template content that is outputted with the
	 * {@code #print(Template)} method will automatically be scanned for
	 * value identifiers with this syntax and the exit query URLs will
	 * generated. You should only use this method if you need a query URL to
	 * be generated in a certain context.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code EXIT:QUERY:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public void setExitQuery(Template template, String name, String pathinfo, String[] outputValues)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)               throw new IllegalArgumentException("template can't be null.");
		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != outputValues &&
			outputValues.length % 2 > 0)    throw new IllegalArgumentException("outputValues should be a series of key/value pairs.");

		EngineTemplateHelper.setExitQuery(mElementContext, template, name, pathinfo, outputValues);
	}

	/**
	 * Generates a form action URL for an exit and sets it as the content of a
	 * template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code EXIT:FORM:exitname} and {@code EXIT:PARAMS:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public void setExitForm(Template template, String name)
	throws TemplateException, EngineException
	{
		setExitForm(template, name, null, null);
	}

	/**
	 * Generates a form action URL for an exit with a pathinfo and sets it as
	 * the content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code EXIT:FORM:exitname} and {@code EXIT:PARAMS:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public void setExitForm(Template template, String name, String pathinfo)
	throws TemplateException, EngineException
	{
		setExitForm(template, name, pathinfo, null);
	}

	/**
	 * Generates a form action URL for an exit with overridden outputs and
	 * sets it as the content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code EXIT:FORM:exitname} and {@code EXIT:PARAMS:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @see #setExitForm(Template, String, String)
	 * @since 1.0
	 */
	public void setExitForm(Template template, String name, String[] outputValues)
	throws TemplateException, EngineException
	{
		setExitForm(template, name, null, outputValues);
	}

	/**
	 * Generates a form action URL for an exit with a pathinfo and overridden
	 * outputs and sets it as the content of a template value.
	 * <p>The URL will be generated by calling the {@link
	 * #getExitFormUrl(String, String)} and {@link
	 * #getExitFormParameters(String, String[])} methods and it will be set
	 * the results to the value identifiers with the syntax
	 * {@code EXIT:FORM:exitname} and {@code EXIT:PARAMS:exitname}.
	 * <p>Template content that is outputted with the
	 * {@code #print(Template)} method will automatically be scanned for
	 * value identifiers with this syntax and the exit forms URLs and
	 * parameters will generated. You should only use this method if you need
	 * these to be generated in a certain context.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the exit
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @param outputValues an array of string pairs that will be used to
	 * override the current output values; or {@code null} if no output
	 * values should be overridden
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code EXIT:FORM:exitname} and {@code EXIT:PARAMS:exitname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getExitQueryUrl(String, String, String[])
	 * @see #getExitFormUrl(String, String)
	 * @see #getExitFormParameters(String, String[])
	 * @see #setExitQuery(Template, String, String, String[])
	 * @since 1.0
	 */
	public void setExitForm(Template template, String name, String pathinfo, String[] outputValues)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)               throw new IllegalArgumentException("template can't be null.");
		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != outputValues &&
			outputValues.length % 2 > 0)    throw new IllegalArgumentException("outputValues should be a series of key/value pairs.");

		EngineTemplateHelper.setExitForm(mElementContext, template, name, pathinfo, outputValues);
	}

	/**
	 * Sets a select box option, a radio button or a checkbox to selected or
	 * checked.
	 * <p>This method will check the template for certain value tags and set
	 * them to the correct attributes according to the name and the provided
	 * values in this method. This is dependent on the template type and
	 * currently only makes sense for {@code enginehtml},
	 * {@code enginexhtml}, {@code html} and {@code xhtml}
	 * templates.
	 * <p>For example for select boxes, consider the name '{@code colors}',
	 * the values '{@code blue}' and '{@code red}', and the
	 * following XHTML template excerpt:
	 * <pre>&lt;select name="colors"&gt;
	 *&lt;option value="blue"[!V 'colors:blue:SELECTED'][!/V]&gt;Blue&lt;/option&gt;
	 *&lt;option value="orange"[!V 'colors:orange:SELECTED'][!/V]&gt;Orange&lt;/option&gt;
	 *&lt;option value="red"[!V 'colors:red:SELECTED'][!/V]&gt;Red&lt;/option&gt;
	 *&lt;option value="green"[!V colors:green:SELECTED''][!/V]&gt;Green&lt;/option&gt;
	 *&lt;/select&gt;</pre>
	 * <p>the result will then be:
	 * <pre>&lt;select name="colors"&gt;
	 *&lt;option value="blue" selected="selected"&gt;Blue&lt;/option&gt;
	 *&lt;option value="orange"&gt;Orange&lt;/option&gt;
	 *&lt;option value="red" selected="selected"&gt;Red&lt;/option&gt;
	 *&lt;option value="green"&gt;Green&lt;/option&gt;
	 *&lt;/select&gt;</pre>
	 * <p>For example for radio buttons, consider the name '{@code sex}',
	 * the value '{@code male}' and the following XHTML template excerpt:
	 * <pre>&lt;input type="radio" name="sex" value="male"[!V 'sex:male:CHECKED'][!/V] /&gt;
	 *&lt;input type="radio" name="sex" value="female"[!V 'sex:female:CHECKED'][!/V] /&gt;</pre>
	 * <p>the result will then be:
	 * <pre>&lt;input type="radio" name="sex" value="male" checked="checked" /&gt;
	 *&lt;input type="radio" name="sex" value="female" /&gt;</pre>
	 * <p>For example for checkboxes, consider the name '{@code active}',
	 * the value '{@code true}' and the following XHTML template excerpt:
	 * <pre>&lt;input type="checkbox" name="active"[!V 'active:CHECKED'][!/V] /&gt;
	 *&lt;input type="checkbox" name="senditnow"[!V 'senditnow:CHECKED'][!/V] /&gt;</pre>
	 * <p>the result will then be:
	 * <pre>&lt;input type="checkbox" name="active" checked="checked" /&gt;
	 *&lt;input type="checkbox" name="senditnow" /&gt;</pre>
	 *
	 * @param template the template instance where the selection should happen
	 * @param name the name of the parameter
	 * @param values the values that should selected or checked
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a list with the identifiers of the template values that have
	 * been set, this is never {@code null}, when no values are set an
	 * empty list is returned
	 * @see #selectInputParameter(Template, String, String[])
	 * @see #selectSubmissionParameter(Template, String, String[])
	 * @since 1.0
	 */
	public Collection<String> selectParameter(Template template, String name, String[] values)
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");
		if (null == name)       throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty.");

		return EngineTemplateHelper.selectParameter(template, name, values);
	}

	/**
	 * Generates a form that corresponds to a bean instance.
	 *
	 * @param template the template instance where the generation should
	 * happen
	 * @param beanInstance the instance of the bean that should be used to
	 * generate the form
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if errors
	 * occurred during the introspection of the bean instance; or if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see com.uwyn.rife.site.FormBuilder
	 * @see #generateForm(Template, Object, String)
	 * @see #generateEmptyForm(Template, Class, String)
	 * @see #removeForm(Template, Class)
	 * @since 1.0
	 */
	public void generateForm(Template template, Object beanInstance)
	throws EngineException
	{
		generateForm(template, beanInstance, null);
	}

	/**
	 * Generates a form that corresponds to a bean instance.
	 * <p>This method delegates all logic to the {@link
	 * com.uwyn.rife.site.FormBuilder#generateForm(Template, Object, Map, String)}
	 * method of the provided template instance.
	 *
	 * @param template the template instance where the generation should
	 * happen
	 * @param beanInstance the instance of the bean that should be used to
	 * generate the form
	 * @param prefix the prefix that will be prepended to all bean property
	 * names
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if errors
	 * occurred during the introspection of the bean instance; or if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see com.uwyn.rife.site.FormBuilder
	 * @see #generateEmptyForm(Template, Class, String)
	 * @see #removeForm(Template, Class)
	 * @since 1.0
	 */
	public void generateForm(Template template, Object beanInstance, String prefix)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)       throw new IllegalArgumentException("template can't be null.");
		if (null == beanInstance)   throw new IllegalArgumentException("beanInstance can't be null.");

		EngineTemplateHelper.generateForm(template, beanInstance, prefix);
	}

	/**
	 * Generates a form that corresponds to an empty instance of a bean class.
	 *
	 * @param template the template instance where the generation should
	 * happen
	 * @param beanClass the class of the bean that should be used to generate
	 * the form
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if errors
	 * occurred during the introspection of the bean; or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @see com.uwyn.rife.site.FormBuilder
	 * @see #generateForm(Template, Object, String)
	 * @see #generateEmptyForm(Template, Class, String)
	 * @see #removeForm(Template, Class)
	 * @since 1.0
	 */
	public void generateEmptyForm(Template template, Class beanClass)
	throws EngineException
	{
		generateEmptyForm(template, beanClass, null);
	}

	/**
	 * Generates a form that corresponds to an empty instance of a bean class.
	 * <p>An '<em>empty</em>' instance is an object that has been created by
	 * calling the default constructor of the bean class, without making any
	 * additional changes to it afterwards.
	 * <p>This method delegates all logic to the {@link
	 * com.uwyn.rife.site.FormBuilder#generateForm(Template, Class, Map, String)}
	 * method of the provided template instance.
	 *
	 * @param template the template instance where the generation should
	 * happen
	 * @param beanClass the class of the bean that should be used to generate
	 * the form
	 * @param prefix the prefix that will be prepended to all bean property
	 * names
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if errors
	 * occurred during the introspection of the bean; or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @see com.uwyn.rife.site.FormBuilder
	 * @see #generateForm(Template, Object, String)
	 * @see #removeForm(Template, Class)
	 * @since 1.0
	 */
	public void generateEmptyForm(Template template, Class beanClass, String prefix)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");
		if (null == beanClass)  throw new IllegalArgumentException("beanClass can't be null.");

		EngineTemplateHelper.generateEmptyForm(template, beanClass, prefix);
	}

	/**
	 * Removes a generated form, leaving the builder value tags empty again as
	 * if this form never had been generated.
	 *
	 * @param template the template instance where the form should be removed
	 * from
	 * @param beanClass the class of the bean that should be used to remove
	 * the form
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if errors
	 * occurred during the introspection of the bean; or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @see com.uwyn.rife.site.FormBuilder
	 * @see #generateForm(Template, Object, String)
	 * @see #generateEmptyForm(Template, Class, String)
	 * @see #removeForm(Template, Class)
	 * @since 1.0
	 */
	public void removeForm(Template template, Class beanClass)
	throws EngineException
	{
		removeForm(template, beanClass, null);
	}

	/**
	 * Removes a generated form, leaving the builder value tags empty again as
	 * if this form never had been generated.
	 * <p>This method delegates all logic to the {@link
	 * com.uwyn.rife.site.FormBuilder#removeForm(Template, Class, String)}
	 * method of the provided template instance.
	 *
	 * @param template the template instance where the form should be removed
	 * from
	 * @param beanClass the class of the bean that should be used to remove
	 * the form
	 * @param prefix the prefix that will be prepended to all bean property
	 * names
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if errors
	 * occurred during the introspection of the bean; or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @see com.uwyn.rife.site.FormBuilder
	 * @see #generateForm(Template, Object, String)
	 * @see #generateEmptyForm(Template, Class, String)
	 * @since 1.0
	 */
	public void removeForm(Template template, Class beanClass, String prefix)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");
		if (null == beanClass)  throw new IllegalArgumentException("beanClass can't be null.");

		EngineTemplateHelper.removeForm(template, beanClass, prefix);
	}

	/**
	 * Sets a select box option, a radio button or a checkbox to selected or
	 * checked according to submission parameter values.
	 * <p>The actual logic is performed by the {@link
	 * #selectParameter(Template, String, String[])} method. This method only
	 * prefixes the parameter name with the {@code PARAM:} literal, which
	 * is the syntax that is used to be able to handle automatic population
	 * correctly for each value type (inputs or submission parameters).
	 * <p>This method is automatically called during the {@link
	 * #print(Template)} for all the inputs and values that this element
	 * received. You should thus only call it explicitly if you need it to be
	 * executed with custom values.
	 *
	 * @param template the template instance where the selection should happen
	 * @param name the name of the parameter
	 * @param values the values that should selected or checked
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a list with the identifiers of the template values that have
	 * been set, this is never {@code null}, when no values are set an
	 * empty list is returned
	 * @see #selectParameter(Template, String, String[])
	 * @see #selectInputParameter(Template, String, String[])
	 * @since 1.0
	 */
	public Collection<String> selectSubmissionParameter(Template template, String name, String[] values)
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");
		if (null == name)       throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty.");

		return EngineTemplateHelper.selectSubmissionParameter(template, name, values);
	}

	/**
	 * Sets a select box option, a radio button or a checkbox to selected or
	 * checked according to a submission parameter value.
	 * <p>This is simply a convenience method that calls {@code #selectSubmissionParameter(Template,
	 * String, String[])} with a single value string array.
	 *
	 * @param template the template instance where the selection should happen
	 * @param name the name of the parameter
	 * @param value the value that should selected or checked
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a list with the identifiers of the template values that have
	 * been set, this is never {@code null}, when no values are set an
	 * empty list is returned
	 * @see #selectParameter(Template, String, String[])
	 * @see #selectInputParameter(Template, String, String[])
	 * @see #selectSubmissionParameter(Template, String, String[])
	 * @since 1.0
	 */
	public Collection selectSubmissionParameter(Template template, String name, String value)
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");
		if (null == name)       throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty.");

		return EngineTemplateHelper.selectSubmissionParameter(template, name, new String[] {value});
	}

	/**
	 * Sets the content of all values that correspond to bean property names
	 * to the data of the bean properties.
	 * <p>The data will be converted to strings and the template's encoder
	 * will be used to encode the string representations (for example, for
	 * HTML non-ascii characters will be replaced with HTML entities).
	 * <p>The identifiers of the values that will be filled in should have the
	 * following syntax:
	 * <pre>PARAM:propertyName</pre>
	 *
	 * @param template the template instance that contains the values that
	 * will be filled in
	 * @param beanInstance the bean instance whose property values will be set
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when
	 * errors occurred during the introspection of the bean instance
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #setSubmissionBean(Template, Object, boolean)
	 * @see #setSubmissionBean(Template, Object, String)
	 * @see #setSubmissionBean(Template, Object, String, boolean)
	 * @since 1.0
	 */
	public void setSubmissionBean(Template template, Object beanInstance)
	throws TemplateException, EngineException
	{
		setSubmissionBean(template, beanInstance, null, true);
	}

	/**
	 * Sets the content of all values that correspond to bean property names
	 * to the data of the bean properties by prepending all the property names
	 * with a prefix.
	 * <p>The data will be converted to strings and the template's encoder
	 * will be used to encode the string representations (for example, for
	 * HTML non-ascii characters will be replaced with HTML entities).
	 * <p>The identifiers of the values that will be filled in should have the
	 * following syntax:
	 * <pre>PARAM:prefixpropertyName</pre>
	 *
	 * @param template the template instance that contains the values that
	 * will be filled in
	 * @param beanInstance the bean instance whose property values will be set
	 * @param prefix the string that will be prefixed to each property name
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when
	 * errors occurred during the introspection of the bean instance
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #setSubmissionBean(Template, Object)
	 * @see #setSubmissionBean(Template, Object, boolean)
	 * @see #setSubmissionBean(Template, Object, String, boolean)
	 * @since 1.6.2
	 */
	public void setSubmissionBean(Template template, Object beanInstance, String prefix)
	throws TemplateException, EngineException
	{
		setSubmissionBean(template, beanInstance, prefix, true);
	}

	/**
	 * Sets the content of all values that correspond to bean property names
	 * to the data of the bean properties.
	 * <p>The identifiers of the values that will be filled in should have the
	 * following syntax:
	 * <pre>PARAM:propertyName</pre>
	 *
	 * @param template the template instance that contains the values that
	 * will be filled in
	 * @param beanInstance the bean instance whose property values will be set
	 * @param encode {@code true} when the property values should be
	 * encoded according to the template type; or
	 * <p>{@code false} otherwise
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when
	 * errors occurred during the introspection of the bean instance
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #setSubmissionBean(Template, Object)
	 * @see #setSubmissionBean(Template, Object, String)
	 * @see #setSubmissionBean(Template, Object, String, boolean)
	 * @since 1.0
	 */
	public void setSubmissionBean(Template template, Object beanInstance, boolean encode)
	throws TemplateException, EngineException
	{
		setSubmissionBean(template, beanInstance, null, encode);
	}

	/**
	 * Sets the content of all values that correspond to bean property names
	 * to the data of the bean properties by prepending all the property names
	 * with a prefix.
	 * <p>The identifiers of the values that will be filled in should have the
	 * following syntax:
	 * <pre>PARAM:propertyName</pre>
	 *
	 * @param template the template instance that contains the values that
	 * will be filled in
	 * @param beanInstance the bean instance whose property values will be set
	 * @param prefix the string that will be prefixed to each property name
	 * @param encode {@code true} when the property values should be
	 * encoded according to the template type; or
	 * <p>{@code false} otherwise
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when
	 * errors occurred during the introspection of the bean instance
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #setSubmissionBean(Template, Object)
	 * @see #setSubmissionBean(Template, Object, String)
	 * @see #setSubmissionBean(Template, Object, boolean)
	 * @since 1.6.2
	 */
	public void setSubmissionBean(Template template, Object beanInstance, String prefix, boolean encode)
	throws TemplateException, EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)       throw new IllegalArgumentException("template can't be null.");
		if (null == beanInstance)   throw new IllegalArgumentException("beanInstance can't be null.");

		EngineTemplateHelper.setSubmissionBean(template, beanInstance, prefix, encode);
	}

	/**
	 * Retrieves an instance of a named submission bean for the current
	 * submission and populates the properties with the parameter values. The
	 * class of the bean is looked up through its name, as is the property
	 * prefix.
	 * <p>This bean is not serialized or deserialized, each property
	 * corresponds to a parameter and is individually sent by the client.
	 *
	 * @param beanName the name of the submission bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission bean is known with this name; or if an error occurred during
	 * the instantiation of the bean; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the populated submission bean instance; or
	 * <p>{@code null} if no submission has been sent
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getNamedSubmissionBean(String beanName)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == beanName)               throw new IllegalArgumentException("beanName can't be null.");
		if (0 == beanName.length())         throw new IllegalArgumentException("beanName can't be empty.");

		String submission_name = mElementContext.getSubmission();
		if (null == submission_name)
		{
			return null;
		}

		return (BeanType)mElementContext.getNamedSubmissionBean(submission_name, beanName);
	}

	/**
	 * Retrieves an instance of a named submission bean and populates the
	 * properties with the parameter values. The class of the bean is looked
	 * up through its name, as is the property prefix.
	 * <p>This bean is not serialized or deserialized, each property
	 * corresponds to a parameter and is individually sent by the client.
	 *
	 * @param submissionName the name of the submission bean
	 * @param beanName the name of the submission that contains the bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission bean is known with this name; or if no submission is know
	 * with the name; or if an error occurred during the instantiation or the
	 * population of the bean; or if you don't have access to the request data
	 * (eg. you're inside a child trigger); or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @return the populated submission bean instance; or
	 * <p>{@code null} if the submission name doesn't correspond to the
	 * sent submission
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getNamedSubmissionBean(String submissionName, String beanName)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == submissionName)         throw new IllegalArgumentException("submissionName can't be null.");
		if (0 == submissionName.length())   throw new IllegalArgumentException("submissionName can't be empty.");
		if (null == beanName)               throw new IllegalArgumentException("beanName can't be null.");
		if (0 == beanName.length())         throw new IllegalArgumentException("beanName can't be empty.");

		return (BeanType)mElementContext.getNamedSubmissionBean(submissionName, beanName);
	}

	/**
	 * Retrieves an instance of a submission bean and populates the properties
	 * with the parameter values.
	 * <p>This bean is not serialized or de-serialized, each property
	 * corresponds to a parameter and is individually sent by the client.
	 *
	 * @param beanClass the class of the submission bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the instantiation or the population of the bean; or if
	 * you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the populated submission bean instance; or
	 * <p>{@code null} if no submission has been sent
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getSubmissionBean(Class<BeanType> beanClass)
	throws EngineException
	{
		String submission_name = mElementContext.getSubmission();
		if (null == submission_name)
		{
			return null;
		}

		return getSubmissionBean(submission_name, beanClass, null);
	}

	/**
	 * Retrieves an instance of a submission bean and populates the properties
	 * with the parameter values, taking the provided prefix into account.
	 * <p>This bean is not serialized or de-serialized, each property
	 * corresponds to a parameter and is individually sent by the client.
	 *
	 * @param beanClass the class of the submission bean
	 * @param prefix the prefix that will be put in front of each property
	 * name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the instantiation or the population of the bean; or if
	 * you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the populated submission bean instance; or
	 * <p>{@code null} if no submission has been sent
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getSubmissionBean(Class<BeanType> beanClass, String prefix)
	throws EngineException
	{
		String submission_name = mElementContext.getSubmission();
		if (null == submission_name)
		{
			return null;
		}

		return getSubmissionBean(submission_name, beanClass, prefix);
	}

	/**
	 * Retrieves an instance of a submission bean and populates the properties
	 * with the parameter values.
	 * <p>This bean is not serialized or de-serialized, each property
	 * corresponds to a parameter and is individually sent by the client.
	 *
	 * @param submissionName the name of the submission
	 * @param beanClass the class of the submission bean
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the instantiation or the population of the bean; or if
	 * you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the populated submission bean instance; or
	 * <p>{@code null} if the submission name doesn't correspond to the
	 * sent submission
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getSubmissionBean(String submissionName, Class<BeanType> beanClass)
	throws EngineException
	{
		return getSubmissionBean(submissionName, beanClass, null);
	}

	/**
	 * Retrieves an instance of a submission bean and populates the properties
	 * with the parameter values, taking the provided prefix into account.
	 * <p>This bean is not serialized or de-serialized, each property
	 * corresponds to a parameter and is individually sent by the client.
	 *
	 * @param submissionName the name of the submission
	 * @param beanClass the class of the submission bean
	 * @param prefix the prefix that will be put in front of each property
	 * name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the instantiation or the population of the bean; or if
	 * you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @return the populated submission bean instance; or
	 * <p>{@code null} if the submission name doesn't correspond to the
	 * sent submission
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public <BeanType> BeanType getSubmissionBean(String submissionName, Class<BeanType> beanClass, String prefix)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == submissionName)         throw new IllegalArgumentException("submissionName can't be null.");
		if (0 == submissionName.length())   throw new IllegalArgumentException("submissionName can't be empty.");
		if (null == beanClass)              throw new IllegalArgumentException("beanClass can't be null.");

		return mElementContext.getSubmissionBean(submissionName, beanClass, prefix);
	}

	/**
	 * Fills the properties of an existing bean with the parameter values of
	 * the submission that was sent.
	 *
	 * @param bean the submission bean instance that will be filled
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the population of the bean; or if you don't have access
	 * to the request data (eg. you're inside a child trigger); or if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public void fillSubmissionBean(Object bean)
	throws EngineException
	{
		String submission_name = mElementContext.getSubmission();
		if (null == submission_name)
		{
			return;
		}

		fillSubmissionBean(submission_name, bean, null);
	}

	/**
	 * Fills the properties of an existing bean with the parameter values of a
	 * submission.
	 *
	 * @param submissionName the name of the submission
	 * @param bean the submission bean instance that will be filled
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the population of the bean; or if you don't have access
	 * to the request data (eg. you're inside a child trigger); or if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public void fillSubmissionBean(String submissionName, Object bean)
	throws EngineException
	{
		fillSubmissionBean(submissionName, bean, null);
	}

	/**
	 * Fills the properties of an existing bean with the parameter values of
	 * the submission that was sent, taking the provided prefix into account.
	 *
	 * @param bean the submission bean instance that will be filled
	 * @param prefix the prefix that will be put in front of each property
	 * name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the population of the bean; or if you don't have access
	 * to the request data (eg. you're inside a child trigger); or if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public void fillSubmissionBean(Object bean, String prefix)
	throws EngineException
	{
		String submission_name = mElementContext.getSubmission();
		if (null == submission_name)
		{
			return;
		}

		fillSubmissionBean(submission_name, bean, prefix);
	}

	/**
	 * Fills the properties of an existing bean with the parameter values of a
	 * submission, taking the provided prefix into account.
	 *
	 * @param submissionName the name of the submission
	 * @param bean the submission bean instance that will be filled
	 * @param prefix the prefix that will be put in front of each property
	 * name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if an error
	 * occurred during the population of the bean; or if you don't have access
	 * to the request data (eg. you're inside a child trigger); or if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public void fillSubmissionBean(String submissionName, Object bean, String prefix)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == submissionName)         throw new IllegalArgumentException("submissionName can't be null.");
		if (0 == submissionName.length())   throw new IllegalArgumentException("submissionName can't be empty.");

		mElementContext.fillSubmissionBean(submissionName, bean, prefix);
	}

	/**
	 * Indicates whether this element received a submission.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return {@code true} if a submission was sent to this element; and
	 * <p>{@code false} otherwise
	 * @see #hasSubmission(String)
	 * @see #getSubmission()
	 * @since 1.0
	 */
	public boolean hasSubmission()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.hasSubmission();
	}

	/**
	 * Indicates whether this element received a certain submission.
	 *
	 * @param submissionName the name of the submission
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return {@code true} if the submission was sent to this element;
	 * and
	 * <p>{@code false} otherwise
	 * @see #hasSubmission()
	 * @see #getSubmission()
	 * @since 1.0
	 */
	public boolean hasSubmission(String submissionName)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == submissionName)         throw new IllegalArgumentException("submissionName can't be null.");
		if (0 == submissionName.length())   throw new IllegalArgumentException("submissionName can't be empty.");

		return mElementContext.hasSubmission(submissionName);
	}

	/**
	 * Retrieves the name of the submission that was sent to this element
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the name of the submission; or
	 * <p>{@code null} if no submission was sent
	 * @see #hasSubmission()
	 * @see #hasSubmission(String)
	 * @since 1.0
	 */
	public String getSubmission()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getSubmission();
	}

	/**
	 * Checks whether a value has been provided to an parameter.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the parameter has a value; or
	 * <p>{@code false} otherwise
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public boolean hasParameterValue(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.hasParameterValue(name);
	}

	/**
	 * Checks whether a parameter is empty.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the parameter is empty; or
	 * <p>{@code false} otherwise
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public boolean isParameterEmpty(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.isParameterEmpty(name);
	}

	/**
	 * Retrieves the value of a parameter.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the value of the parameter; or
	 * <p>{@code null} if no value is present for this parameter
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public String getParameter(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getParameter(name);
	}

	/**
	 * Retrieves the value of a parameter and returns a default value if no
	 * parameter value is present
	 *
	 * @param name the name of the parameter
	 * @param defaultValue the default value that will be used when no
	 * parameter value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the parameter value; or
	 * <p>the default value if no parameter value is present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public String getParameter(String name, String defaultValue)
	throws EngineException
	{
		String value = getParameter(name);
		if (value == null)
		{
			return defaultValue;
		}

		return value;
	}

	/**
	 * Retrieves the names of all the parameters that are present.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the list with the parameter names
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public ArrayList<String> getParameterNames()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getParameterNames(null);
	}

	/**
	 * Retrieves the names of all the parameters that are present and that
	 * match a regular expression.
	 *
	 * @param regexp the regular expression that will be used to filter the
	 * parameter names
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the list with the parameter names
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterNames()
	 * @since 1.0
	 */
	public ArrayList<String> getParameterNames(String regexp)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getParameterNames(regexp);
	}


	/**
	 * Retrieves the values of a parameter.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a string array with all the parameter values; or
	 * <p>{@code null} if no parameter values are present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public String[] getParameterValues(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getParameterValues(name);
	}

	/**
	 * Retrieves the value of a parameter and converts it to a boolean.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>{@code false} if no parameter value is present or if the
	 * parameter value is not a valid boolean
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public boolean getParameterBoolean(String name)
	throws EngineException
	{
		return getParameterBoolean(name, ElementInfo.DEFAULT_BOOLEAN);
	}

	/**
	 * Retrieves the value of a parameter and converts it to a boolean, using
	 * a default value if no parameter value is present.
	 *
	 * @param name the name of the parameter
	 * @param defaultValue the default value that will be used when no
	 * parameter value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>the default value if no parameter value is present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public boolean getParameterBoolean(String name, boolean defaultValue)
	throws EngineException
	{
		String value = getParameter(name);
		if (value == null)
		{
			return defaultValue;
		}

		return StringUtils.convertToBoolean(value);
	}

	/**
	 * Retrieves the value of a parameter and converts it to an integer.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>{@code 0} if no parameter value is present or if the parameter
	 * value is not a valid integer
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public int getParameterInt(String name)
	throws EngineException
	{
		return getParameterInt(name, ElementInfo.DEFAULT_INTEGER);
	}

	/**
	 * Retrieves the value of a parameter and converts it to an integer, using
	 * a default value if no parameter value is present.
	 *
	 * @param name the name of the parameter
	 * @param defaultValue the default value that will be used when no
	 * parameter value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>the default value if no parameter value is present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public int getParameterInt(String name, int defaultValue)
	throws EngineException
	{
		String value = getParameter(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of a parameter and converts it to a long.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>{@code 0L} if no parameter value is present or if the parameter
	 * value is not a valid long
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public long getParameterLong(String name)
	throws EngineException
	{
		return getParameterLong(name, ElementInfo.DEFAULT_LONG);
	}

	/**
	 * Retrieves the value of a parameter and converts it to a long, using a
	 * default value if no parameter value is present.
	 *
	 * @param name the name of the parameter
	 * @param defaultValue the default value that will be used when no
	 * parameter value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>the default value if no parameter value is present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public long getParameterLong(String name, long defaultValue)
	throws EngineException
	{
		String value = getParameter(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of a parameter and converts it to a double.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>{@code 0.0d} if no parameter value is present or if the
	 * parameter value is not a valid double
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public double getParameterDouble(String name)
	throws EngineException
	{
		return getParameterDouble(name, ElementInfo.DEFAULT_DOUBLE);
	}

	/**
	 * Retrieves the value of a parameter and converts it to a double, using a
	 * default value if no parameter value is present.
	 *
	 * @param name the name of the parameter
	 * @param defaultValue the default value that will be used when no
	 * parameter value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>the default value if no parameter value is present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public double getParameterDouble(String name, double defaultValue)
	throws EngineException
	{
		String value = getParameter(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the value of a parameter and converts it to a float.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>{@code 0.0f} if no parameter value is present or if the
	 * parameter value is not a valid float
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public float getParameterFloat(String name)
	throws EngineException
	{
		return getParameterFloat(name, ElementInfo.DEFAULT_FLOAT);
	}

	/**
	 * Retrieves the value of a parameter and converts it to a float, using a
	 * default value if no parameter value is present.
	 *
	 * @param name the name of the parameter
	 * @param defaultValue the default value that will be used when no
	 * parameter value is present
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the converted parameter value; or
	 * <p>the default value if no parameter value is present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public float getParameterFloat(String name, float defaultValue)
	throws EngineException
	{
		String value = getParameter(name);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves the values of a parameter as an array of integers.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a integer array with all the parameter values; or
	 * <p>{@code null} if no parameter values are present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public int[] getParameterIntValues(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return ArrayUtils.createIntArray(mElementContext.getParameterValues(name));
	}

	/**
	 * Retrieves the values of a parameter as an array of longs.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a long array with all the parameter values; or
	 * <p>{@code null} if no parameter values are present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public long[] getParameterLongValues(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return ArrayUtils.createLongArray(mElementContext.getParameterValues(name));
	}

	/**
	 * Retrieves the values of a parameter as an array of floats.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a float array with all the parameter values; or
	 * <p>{@code null} if no parameter values are present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public float[] getParameterFloatValues(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return ArrayUtils.createFloatArray(mElementContext.getParameterValues(name));
	}

	/**
	 * Retrieves the values of a parameter as an array of doubles.
	 *
	 * @param name the name of the parameter
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * parameter is known with this name; or if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a double array with all the parameter values; or
	 * <p>{@code null} if no parameter values are present
	 * @see #getNamedSubmissionBean(String, String)
	 * @see #getSubmissionBean(String, Class, String)
	 * @see #fillSubmissionBean(String, Object, String)
	 * @see #hasParameterValue(String)
	 * @see #isParameterEmpty(String)
	 * @see #getParameter(String)
	 * @see #getParameter(String, String)
	 * @see #getParameterValues(String)
	 * @see #getParameterNames()
	 * @see #getParameterNames(String)
	 * @since 1.0
	 */
	public double[] getParameterDoubleValues(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return ArrayUtils.createDoubleArray(mElementContext.getParameterValues(name));
	}

	/**
	 * Retrieves the names of all the files that are present and that
	 * match a regular expression.
	 *
	 * @param regexp the regular expression that will be used to filter the
	 * file names
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the list with the file names
	 * @see #getUploadedFileNames()
	 * @see #hasUploadedFile(String)
	 * @see #isFileEmpty(String)
	 * @see #getUploadedFile(String)
	 * @see #getUploadedFiles(String)
	 * @since 1.1
	 */
	public ArrayList<String> getUploadedFileNames(String regexp)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getUploadedFileNames(regexp);
	}

	/**
	 * Retrieves the list of uploaded file names.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the list of uploaded file names
	 * @see #getUploadedFileNames(String)
	 * @see #hasUploadedFile(String)
	 * @see #isFileEmpty(String)
	 * @see #getUploadedFile(String)
	 * @see #getUploadedFiles(String)
	 * @since 1.0
	 */
	public ArrayList<String> getUploadedFileNames()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getUploadedFileNames();
	}

	/**
	 * Checks if a particular file has been uploaded during the last
	 * submission.
	 *
	 * @param name the name of the file, as declared in the submission
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no file
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the file was uploaded; or
	 * <p>{@code false} otherwise
	 * @see #getUploadedFileNames(String)
	 * @see #getUploadedFileNames()
	 * @see #isFileEmpty(String)
	 * @see #getUploadedFile(String)
	 * @see #getUploadedFiles(String)
	 * @since 1.0
	 */
	public boolean hasUploadedFile(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.hasUploadedFile(name);
	}

	/**
	 * Checks if an uploaded file wasn't sent or if it is empty.
	 *
	 * @param name the name of the file, as declared in the submission
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no file
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the file wasn't uploaded or empty; or
	 * <p>false otherwise
	 * @see #getUploadedFileNames(String)
	 * @see #getUploadedFileNames()
	 * @see #hasUploadedFile(String)
	 * @see #getUploadedFile(String)
	 * @see #getUploadedFiles(String)
	 * @since 1.0
	 */
	public boolean isFileEmpty(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.isFileEmpty(name);
	}

	/**
	 * Retrieves an uploaded file.
	 *
	 * @param name the name of the file, as declared in the submission
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no file
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the uploaded file; or
	 * <p>{@code null} if no file was uploaded
	 * @see #getUploadedFileNames(String)
	 * @see #getUploadedFileNames()
	 * @see #hasUploadedFile(String)
	 * @see #isFileEmpty(String)
	 * @see #getUploadedFiles(String)
	 * @since 1.0
	 */
	public UploadedFile getUploadedFile(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getUploadedFile(name);
	}

	/**
	 * Retrieves all files that have been uploaded for a particular name.
	 *
	 * @param name the name of the file, as declared in the submission
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no file
	 * is known with this name; or if you don't have access to the request
	 * data (eg. you're inside a child trigger); or if there's no active
	 * element context (eg. you're using this method inside the constructor
	 * instead of inside the {@link #initialize()} method)
	 * @return the uploaded files; or
	 * <p>{@code null} if no files were uploaded for that name
	 * @see #getUploadedFileNames(String)
	 * @see #getUploadedFileNames()
	 * @see #hasUploadedFile(String)
	 * @see #isFileEmpty(String)
	 * @see #getUploadedFile(String)
	 * @since 1.0
	 */
	public UploadedFile[] getUploadedFiles(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");

		return mElementContext.getUploadedFiles(name);
	}

	/**
	 * Generates a query URL for a submission.
	 *
	 * @param name the name of the submission
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionQueryUrl(String name)
	throws EngineException
	{
		return getSubmissionQueryUrl(name, null, null);
	}

	/**
	 * Generates a query URL for a submission and appends a pathinfo to the
	 * URL of the element.
	 *
	 * @param name the name of the submission
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionQueryUrl(String name, String pathinfo)
	throws EngineException
	{
		return getSubmissionQueryUrl(name, pathinfo, null);
	}

	/**
	 * Generates a query URL for a submission with default parameter values.
	 *
	 * @param name the name of the submission
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionQueryUrl(String name, String[] parameterValues)
	throws EngineException
	{
		return getSubmissionQueryUrl(name, null, parameterValues);
	}

	/**
	 * Generates a query URL for a submission and appends a pathinfo to the
	 * URL of the element, default parameter values can also be added.
	 * <p>This will take the current element context into account with the
	 * available inputs, global variables, ... and generate an URL that
	 * persists the data state according to the declared site structure.
	 * <p>The default parameter values are provided as an array of strings
	 * that should be structured in pairs. For example, if these output values
	 * should be used: {@code param1}:{@code value1} and
	 * {@code param2}:{@code value2}, you should define the
	 * following string array:
	 * <pre>new String[] {"param1", "value1", "param2", "value2"}</pre>
	 * <p>The generated URL with not contain a scheme, host or port. It will
	 * begin with the path part and be absolute, starting with the web
	 * application's root URL.
	 *
	 * @param name the name of the submission
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the generated URL as a character sequence
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionQueryUrl(String name, String pathinfo, String[] parameterValues)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != parameterValues &&
			parameterValues.length % 2 > 0) throw new IllegalArgumentException("parameterValues should be a series of key/value pairs.");

		mElementContext.getElementInfo().validateSubmissionName(name);

		return ElementContextFlowGeneration.generateSubmissionQueryUrl(mElementContext, name, pathinfo, parameterValues, mElementContext.getOutputs().aggregateValues().entrySet());
	}

	/**
	 * Generates a form action URL for a submission.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the generated URL as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionFormUrl()
	throws EngineException
	{
		return getSubmissionFormUrl(null);
	}

	/**
	 * Generates a form action URL for a submission and appends a pathinfo to
	 * the URL of the element.
	 * <p>This will take the current element context into account with the
	 * available inputs, global variables, ... and generate an URL that
	 * persists the data state according to the declared site structure.
	 * <p>The generated URL with not contain a scheme, host or port. It will
	 * begin with the path part and be absolute, starting with the web
	 * application's root URL.
	 * <p>This method goes together with the {@link
	 * #getSubmissionFormParameters(String, String[])} method since the state
	 * is tranferred as hidden form parameters that are part of the form.
	 *
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the generated URL as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionFormUrl(String pathinfo)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return ElementContextFlowGeneration.generateSubmissionFormUrl(mElementContext, pathinfo);
	}

	/**
	 * Generates the hidden XHTML form parameters for a submission.
	 *
	 * @param name the name of the submission
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the generated parameters as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionFormParameters(String name)
	throws EngineException
	{
		return getSubmissionFormParameters(name, null);
	}

	/**
	 * Generates the hidden XHTML form parameters for a submission and
	 * overrides the current output values only for this method.
	 * <p>This will take the current element context into account with the
	 * available inputs, global variables, ... and generate hidden XHTML form
	 * parameters that persist the data state according to the declared site
	 * structure.
	 * <p>The default parameter values are provided as an array of strings
	 * that should be structured in pairs. For example, if these output values
	 * should be used: {@code param1}:{@code value1} and
	 * {@code param2}:{@code value2}, you should define the
	 * following string array:
	 * <pre>new String[] {"param1", "value1", "param2", "value2"}</pre>
	 * <p>This method goes together with the {@link
	 * #getSubmissionFormUrl(String)} method since the URL needs to be
	 * provided in the action attribute of the form.
	 *
	 * @param name the name of the submission
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the generated parameters as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParametersJavascript(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public CharSequence getSubmissionFormParameters(String name, String[] parameterValues)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != parameterValues &&
			parameterValues.length % 2 > 0) throw new IllegalArgumentException("parameterValues should be a series of key/value pairs.");

		mElementContext.getElementInfo().validateSubmissionName(name);

		return ElementContextFlowGeneration.generateSubmissionFormParameters(mElementContext, name, parameterValues, mElementContext.getOutputs().aggregateValues().entrySet());
	}

	/**
	 * Generates Javascript that will generate hidden XHTML form parameters for
	 * a submission and overrides the current output values only for this method.
	 * <p>This will take the current element context into account with the
	 * available inputs, global variables, ... and generate hidden XHTML form
	 * parameters that persist the data state according to the declared site
	 * structure.
	 * <p>The default parameter values are provided as an array of strings
	 * that should be structured in pairs. For example, if these output values
	 * should be used: {@code param1}:{@code value1} and
	 * {@code param2}:{@code value2}, you should define the
	 * following string array:
	 * <pre>new String[] {"param1", "value1", "param2", "value2"}</pre>
	 * <p>This method goes together with the {@link
	 * #getSubmissionFormUrl(String)} method since the URL needs to be
	 * provided in the action attribute of the form.
	 *
	 * @param name the name of the submission
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the generated parameters as a character sequence
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.6
	 */
	public CharSequence getSubmissionFormParametersJavascript(String name, String[] parameterValues)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != parameterValues &&
			parameterValues.length % 2 > 0) throw new IllegalArgumentException("parameterValues should be a series of key/value pairs.");

		mElementContext.getElementInfo().validateSubmissionName(name);

		return ElementContextFlowGeneration.generateSubmissionFormParametersJavascript(mElementContext, name, parameterValues, mElementContext.getOutputs().aggregateValues().entrySet());
	}

	/**
	 * Generates a query URL for a submission sets it as the content of a
	 * template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code SUBMISSION:QUERY:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public void setSubmissionQuery(Template template, String name)
	throws TemplateException, EngineException
	{
		setSubmissionQuery(template, name, null, null);
	}

	/**
	 * Generates a query URL for a submission with a pathinfo and sets it as
	 * the content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code SUBMISSION:QUERY:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public void setSubmissionQuery(Template template, String name, String pathinfo)
	throws TemplateException, EngineException
	{
		setSubmissionQuery(template, name, pathinfo, null);
	}

	/**
	 * Generates a query URL for a submission with default parameter values
	 * and sets it as the content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code SUBMISSION:QUERY:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public void setSubmissionQuery(Template template, String name, String[] parameterValues)
	throws TemplateException, EngineException
	{
		setSubmissionQuery(template, name, null, parameterValues);
	}

	/**
	 * Generates a query URL for a submission with pathinfo and default
	 * parameter values and sets it as the content of a template value.
	 * <p>The URL will be generated by calling the {@link
	 * #getSubmissionQueryUrl(String, String, String[])} method and it will be
	 * set to the value identifier with the syntax
	 * {@code SUBMISSION:QUERY:submissionname}.
	 * <p>Template content that is outputted with the
	 * {@code #print(Template)} method will automatically be scanned for
	 * value identifiers with this syntax and the submission query URLs will
	 * generated. You should only use this method if you need a submission URL
	 * to be generated in a certain context.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifier
	 * {@code SUBMISSION:QUERY:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * submission is known with this name; if you don't have access to the
	 * request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public void setSubmissionQuery(Template template, String name, String pathinfo, String[] parameterValues)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)               throw new IllegalArgumentException("template can't be null.");
		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != parameterValues &&
			parameterValues.length % 2 > 0) throw new IllegalArgumentException("parameterValues should be a series of key/value pairs.");

		EngineTemplateHelper.setSubmissionQuery(mElementContext, template, name, pathinfo, parameterValues);
	}

	/**
	 * Generates a form action URL for an submission and sets it as the
	 * content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code SUBMISSION:FORM:submissionname} and
	 * {@code SUBMISSION:PARAMS:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public void setSubmissionForm(Template template, String name)
	throws TemplateException, EngineException
	{
		setSubmissionForm(template, name, null, null);
	}

	/**
	 * Generates a form action URL for an submission with a pathinfo and sets
	 * it as the content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code SUBMISSION:FORM:submissionname} and
	 * {@code SUBMISSION:PARAMS:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public void setSubmissionForm(Template template, String name, String pathinfo)
	throws TemplateException, EngineException
	{
		setSubmissionForm(template, name, pathinfo, null);
	}

	/**
	 * Generates a form action URL for an submission with default parameter
	 * values and sets it as the content of a template value.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code SUBMISSION:FORM:submissionname} and
	 * {@code SUBMISSION:PARAMS:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @see #setSubmissionForm(Template, String, String)
	 * @since 1.0
	 */
	public void setSubmissionForm(Template template, String name, String[] parameterValues)
	throws TemplateException, EngineException
	{
		setSubmissionForm(template, name, null, parameterValues);
	}

	/**
	 * Generates a form action URL for an submission with a pathinfo and
	 * default parameter values and sets it as the content of a template
	 * value.
	 * <p>The URL will be generated by calling the {@link
	 * #getSubmissionFormUrl(String)} and {@link
	 * #getSubmissionFormParameters(String, String[])} methods and it will be
	 * set the results to the value identifiers with the syntax
	 * {@code SUBMISSION:FORM:submissionname} and
	 * {@code SUBMISSION:PARAMS:submissionname}.
	 * <p>Template content that is outputted with the
	 * {@code #print(Template)} method will automatically be scanned for
	 * value identifiers with this syntax and the submission forms URLs and
	 * parameters will generated. You should only use this method if you need
	 * these to be generated in a certain context.
	 *
	 * @param template the template that will be used to set the value
	 * @param name the name of the submission
	 * @param pathinfo the pathinfo that will be appended; or
	 * {@code null} if no pathinfo should be appended
	 * @param parameterValues an array of string pairs that will be used to
	 * set default parameter values; or {@code null} if no default
	 * parameter values should be used
	 * @exception com.uwyn.rife.template.exceptions.TemplateException if the
	 * template doesn't contain the value identifiers
	 * {@code SUBMISSION:FORM:submissionname} and
	 * {@code SUBMISSION:PARAMS:submissionname}
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no exit
	 * is known with this name; if the exit hasn't got a destination element;
	 * if you don't have access to the request data (eg. you're inside a child
	 * trigger); or if there's no active element context (eg. you're using
	 * this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @see #getSubmissionQueryUrl(String, String, String[])
	 * @see #getSubmissionFormUrl(String)
	 * @see #getSubmissionFormParameters(String, String[])
	 * @see #setSubmissionQuery(Template, String, String, String[])
	 * @since 1.0
	 */
	public void setSubmissionForm(Template template, String name, String pathinfo, String[] parameterValues)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)               throw new IllegalArgumentException("template can't be null.");
		if (null == name)                   throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())             throw new IllegalArgumentException("name can't be empty.");
		if (null != parameterValues &&
			parameterValues.length % 2 > 0) throw new IllegalArgumentException("parameterValues should be a series of key/value pairs.");

		EngineTemplateHelper.setSubmissionForm(mElementContext, template, name, pathinfo, parameterValues);
	}

	/**
	 * Processes an embedded element without a differentiator in a template.
	 * <p>Embedded elements are evaluated when value identifiers have the
	 * following syntax: {@code ELEMENT:elementId}.
	 * <p>All embedded elements are automatically processed when the template
	 * is instantiated, so this method should only be called if you need to
	 * re-process an embedded element in a particular context.
	 *
	 * @param template the template that will be used to process the embedded
	 * template
	 * @param elementId the identifier of the element
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when an
	 * error occurs during the template processing
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * element is known with that identifier; or if you don't have access to
	 * the request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #processEmbeddedElement(Template, String, Object)
	 * @see #processEmbeddedElement(Template, String, String)
	 * @see #processEmbeddedElement(Template, String, String, Object)
	 * @since 1.0
	 */
	public void processEmbeddedElement(Template template, String elementId)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)           throw new IllegalArgumentException("template can't be null.");
		if (null == elementId)          throw new IllegalArgumentException("elementId can't be null.");
		if (0 == elementId.length())    throw new IllegalArgumentException("elementId can't be empty.");

		mElementContext.processEmbeddedElement(template, this, elementId, null, null);
	}

	/**
	 * Processes an embedded element without a differentiator in a template
	 * and pass on data to the processed embedded element.
	 * <p>See {@link #processEmbeddedElement(Template, String)} for more
	 * information.
	 *
	 * @param template the template that will be used to process the embedded
	 * template
	 * @param elementId the identifier of the element
	 * @param data the data that will be available from within the embedded
	 * element
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when an
	 * error occurs during the template processing
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * element is known with that identifier; or if you don't have access to
	 * the request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #processEmbeddedElement(Template, String)
	 * @see #processEmbeddedElement(Template, String, String)
	 * @see #processEmbeddedElement(Template, String, String, Object)
	 * @since 1.5
	 */
	public void processEmbeddedElement(Template template, String elementId, Object data)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)           throw new IllegalArgumentException("template can't be null.");
		if (null == elementId)          throw new IllegalArgumentException("elementId can't be null.");
		if (0 == elementId.length())    throw new IllegalArgumentException("elementId can't be empty.");

		mElementContext.processEmbeddedElement(template, this, elementId, null, data);
	}

	/**
	 * Processes an embedded element with a differentiator in a template.
	 * <p>Embedded elements are evaluated when value identifiers have the
	 * following syntax: {@code ELEMENT:elementId:differentiator}.
	 * <p>All embedded elements are automatically processed when the template
	 * is instantiated, so this method should only be called if you need to
	 * re-process an embedded element in a particular context.
	 *
	 * @param template the template that will be used to process the embedded
	 * template
	 * @param elementId the identifier of the element
	 * @param differentiator the differentiator that will be used; or
	 * {@code null} if no differentiator should be used
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when an
	 * error occurs during the template processing
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * element is known with that identifier; or if you don't have access to
	 * the request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #processEmbeddedElement(Template, String)
	 * @see #processEmbeddedElement(Template, String, Object)
	 * @see #processEmbeddedElement(Template, String, String, Object)
	 * @since 1.0
	 */
	public void processEmbeddedElement(Template template, String elementId, String differentiator)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)           throw new IllegalArgumentException("template can't be null.");
		if (null == elementId)          throw new IllegalArgumentException("elementId can't be null.");
		if (0 == elementId.length())    throw new IllegalArgumentException("elementId can't be empty.");

		mElementContext.processEmbeddedElement(template, this, elementId, differentiator, null);
	}

	/**
	 * Processes an embedded element with a differentiator in a template
	 * and pass on data to the processed embedded element.
	 * <p>See {@link #processEmbeddedElement(Template, String, String)} for more
	 * information.
	 *
	 * @param template the template that will be used to process the embedded
	 * template
	 * @param elementId the identifier of the element
	 * @param differentiator the differentiator that will be used; or
	 * @param data the data that will be available from within the embedded
	 * element
	 * {@code null} if no differentiator should be used
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when an
	 * error occurs during the template processing
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if no
	 * element is known with that identifier; or if you don't have access to
	 * the request data (eg. you're inside a child trigger); or if there's no
	 * active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #processEmbeddedElement(Template, String)
	 * @see #processEmbeddedElement(Template, String, Object)
	 * @see #processEmbeddedElement(Template, String, String)
	 * @since 1.5
	 */
	public void processEmbeddedElement(Template template, String elementId, String differentiator, Object data)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)           throw new IllegalArgumentException("template can't be null.");
		if (null == elementId)          throw new IllegalArgumentException("elementId can't be null.");
		if (0 == elementId.length())    throw new IllegalArgumentException("elementId can't be empty.");

		mElementContext.processEmbeddedElement(template, this, elementId, differentiator, data);
	}

	/**
	 * Evaluate the {@code ROLEUSER} expression tags in a template.
	 * <p>The {@link #print(Template)} method automatically evaluates all role
	 * user expression tags. This method should thus only be called when you
	 * need them to be evaluated in a specific context.
	 * <p>This block and value expression tag is evaluated through a method
	 * that's not part of the {@link com.uwyn.rife.template.Template} class
	 * since it only makes sense in an element context where a users can be
	 * identified.
	 * <p>The value tags have the following syntax
	 * {@code LANGUAGE:ROLEUSER:identifier} and the block tags have the
	 * following syntax
	 * {@code LANGUAGE:ROLEUSER:identifier:[[&nbsp;boolean_expression&nbsp;]]}.
	 * <p>Below is an example of roleuser expression tags in use:
	 * <pre>&lt;!--V 'OGNL:ROLEUSER:role1'--&gt;User is not in role "admin"&lt;!--/V--&gt;
	 *&lt;!--B 'OGNL:ROLEUSER:role1:[[ isInRole("admin") ]]'--&gt;User is in role "admin"&lt;!--/B--&gt;</pre>
	 *
	 * @param template the template instance where the evaluation should
	 * happen
	 * @param id the block and the value identifier
	 * @return the list of names of the template values that were generated
	 * @exception com.uwyn.rife.template.exceptions.TemplateException when an
	 * error occurs during the expression tags evaluation
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @since 1.0
	 */
	public List<String> evaluateExpressionRoleUserTags(Template template, String id)
	throws TemplateException, EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == template)   throw new IllegalArgumentException("template can't be null.");

		if (null == id)
		{
			return Collections.emptyList();
		}

		List<String> set_values = new ArrayList<String>();
		EngineTemplateHelper.evaluateExpressionRoleUserTags(mElementContext, set_values, template, id);
		return set_values;
	}

	/**
	 * Activates an exit.
	 * <p>This immediately breaks out of the element and notifies the engine
	 * that the next step of the flow must be looked up and executed.
	 *
	 * @param name the name of the exit
	 * @exception com.uwyn.rife.engine.exceptions.EngineException a runtime
	 * exception that is used to immediately interrupt the execution, don't
	 * catch this exception; or there's no active element context (eg. you're
	 * using this method inside the constructor instead of inside the {@link
	 * #initialize()} method)
	 * @since 1.0
	 */
	public void exit(String name)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		if (null == name)       throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty.");

		mElementContext.exit(name);
	}

	/**
	 * Interrupts the execution in this element and transfers the execution to
	 * the child element.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException a runtime
	 * exception that is used to immediately interrupt the execution, don't
	 * catch this exception; or you don't have access to the request data (eg.
	 * you're inside a child trigger); or there's no active element context
	 * (eg. you're using this method inside the constructor instead of inside
	 * the {@link #initialize()} method)
	 * @since 1.0
	 */
	public void child()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		throw new ChildTriggeredException(null, null);
	}

	/**
	 * Interrupts the execution in RIFE completely and defers it to the
	 * servlet container.
	 * <p>If RIFE is being run as a filter, it will execute the next filter in
	 * the chain.
	 * <p>If RIFE is being run as a servlet, the status code {@code 404: Not
	 * Found} will be sent to the client.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException a runtime
	 * exception that is used to immediately interrupt the execution, don't
	 * catch this exception; or you don't have access to the request data (eg.
	 * you're inside a child trigger); or there's no active element context
	 * (eg. you're using this method inside the constructor instead of inside
	 * the {@link #initialize()} method)
	 * @since 1.0
	 */
	public void defer()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		throw new DeferException();
	}

	/**
	 * Interrupts the execution in this element and forwards the entire
	 * request to another URL.
	 * <p>The response of the forwarded request will be sent the to original
	 * client, as if the request was sent directly to the forwarded URL.
	 *
	 * @param url the URL to which the request will be forwarded
	 * @exception com.uwyn.rife.engine.exceptions.EngineException a runtime
	 * exception that is used to immediately interrupt the execution, don't
	 * catch this exception; or you don't have access to the request data (eg.
	 * you're inside a child trigger); or there's no active element context
	 * (eg. you're using this method inside the constructor instead of inside
	 * the {@link #initialize()} method)
	 * @since 1.0
	 */
	public void forward(String url)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		throw new ForwardException(url);
	}

	/**
	 * Interrupts the execution in this element and redirects the client to
	 * another URL.
	 *
	 * @param url the URL to which the request will be redirected
	 * @exception com.uwyn.rife.engine.exceptions.EngineException a runtime
	 * exception that is used to immediately interrupt the execution, don't
	 * catch this exception; or you don't have access to the request data (eg.
	 * you're inside a child trigger); or there's no active element context
	 * (eg. you're using this method inside the constructor instead of inside
	 * the {@link #initialize()} method)
	 * @since 1.0
	 */
	public void redirect(String url)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		throw new RedirectException(url);
	}

	/**
	 * Returns the name of the character encoding (MIME charset) used for the
	 * body sent in this response. The character encoding may have been
	 * specified explicitly using the {@link #setContentType} method, or
	 * implicitly using the {@link #setResponseLocale} method. Explicit
	 * specifications take precedence over implicit specifications. If no
	 * character encoding has been specified, {@code ISO-8859-1} is
	 * returned.
	 * <p>See RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt) for more
	 * information about character encoding and MIME.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String} specifying the name of the character
	 * encoding, for example, {@code UTF-8}
	 * @see #setContentType #setResponseLocale
	 * @since 1.0
	 */
	public String getResponseCharacterEncoding()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getResponse().getCharacterEncoding();
	}

	/**
	 * Sets the length of the content body in the response In HTTP servlets,
	 * this method sets the HTTP Content-Length header.
	 *
	 * @param length an integer specifying the length of the content being
	 * returned to the client; sets the Content-Length header
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @since 1.0
	 */
	public void setContentLength(int length)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().setContentLength(length);
	}

	/**
	 * Sets the content type of the response being sent to the client, if the
	 * response has not been committed yet. The given content type may include
	 * a character encoding specification, for example,
	 * {@code text/html;charset=UTF-8}. The response's character encoding
	 * is only set from the given content type if this method is called before
	 * {@code getWriter} is called.
	 * <p>This method may be called repeatedly to change content type and
	 * character encoding. This method has no effect if called after the
	 * response has been committed. It does not set the response's character
	 * encoding if it is called after {@code getWriter} has been called
	 * or after the response has been committed.
	 * <p>Containers must communicate the content type and the character
	 * encoding used for the servlet response's writer to the client if the
	 * protocol provides a way for doing so. In the case of HTTP, the
	 * {@code Content-Type} header is used.
	 *
	 * @param type a {@code String} specifying the MIME type of the
	 * content
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #setResponseLocale
	 * @see #getOutputStream
	 * @since 1.0
	 */
	public void setContentType(String type)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().setContentType(type);
	}

	/**
	 * Adds a response header with the given name and value. This method
	 * allows response headers to have multiple values.
	 *
	 * @param name the name of the header
	 * @param value the additional header value If it contains octet string,
	 * it should be encoded according to RFC 2047
	 * (http://www.ietf.org/rfc/rfc2047.txt)
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #setHeader
	 * @since 1.0
	 */
	public void addHeader(String name, String value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().addHeader(name, value);
	}

	/**
	 * Sets a response header with the given name and date-value. The date is
	 * specified in terms of milliseconds since the epoch. If the header had
	 * already been set, the new value overwrites the previous one. The
	 * {@code containsHeader} method can be used to test for the presence
	 * of a header before setting its value.
	 *
	 * @param name the name of the header to set
	 * @param date the assigned date value
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #setDateHeader
	 * @since 1.0
	 */
	public void addDateHeader(String name, long date)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().addDateHeader(name, date);
	}

	/**
	 * Adds a response header with the given name and integer value. This
	 * method allows response headers to have multiple values.
	 *
	 * @param name the name of the header
	 * @param value the assigned integer value
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #setIntHeader
	 * @since 1.0
	 */
	public void addIntHeader(String name, int value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().addIntHeader(name, value);
	}

	/**
	 * Returns a boolean indicating whether the named response header has
	 * already been set.
	 *
	 * @param name the header name
	 * @return {@code true} if the named response header has already been
	 * set; or
	 * <p>{@code false} otherwise
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @since 1.0
	 */
	public boolean containsHeader(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getResponse().containsHeader(name);
	}

	/**
	 * Sends an error response to the client using the specified status code
	 * and clearing the buffer.
	 * <p>If the response has already been committed, this method throws an
	 * IllegalStateException. After using this method, the response should be
	 * considered to be committed and should not be written to.
	 *
	 * @param statusCode the error status code
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #sendError(int, String)
	 * @since 1.0
	 */
	public void sendError(int statusCode)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().sendError(statusCode);
	}

	/**
	 * Sends an error response to the client using the specified status. The
	 * server defaults to creating the response to look like an HTML-formatted
	 * server error page containing the specified message, setting the content
	 * type to "text/html", leaving cookies and other headers unmodified. If
	 * an error-page declaration has been made for the web application
	 * corresponding to the status code passed in, it will be served back in
	 * preference to the suggested msg parameter.
	 * <p>If the response has already been committed, this method throws an
	 * IllegalStateException. After using this method, the response should be
	 * considered to be committed and should not be written to.
	 *
	 * @param statusCode the error status code
	 * @param message the descriptive message
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #sendError(int)
	 * @since 1.0
	 */
	public void sendError(int statusCode, String message)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().sendError(statusCode, message);
	}

	/**
	 * Sends a temporary redirect response to the client using the specified
	 * redirect location URL. This method can accept relative URLs; the
	 * servlet container must convert the relative URL to an absolute URL
	 * before sending the response to the client. If the location is relative
	 * without a leading '/' the container interprets it as relative to the
	 * current request URI. If the location is relative with a leading '/' the
	 * container interprets it as relative to the servlet container root.
	 * <p>If the response has already been committed, this method throws an
	 * IllegalStateException. After using this method, the response should be
	 * considered to be committed and should not be written to.
	 *
	 * @param location the redirect location URL
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @since 1.0
	 */
	public void sendRedirect(String location)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().sendRedirect(location);
	}

	/**
	 * Sets up the current request to prevent all caching of the response by
	 * the client.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @since 1.0
	 */
	public void preventCaching()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		ServletUtils.preventCaching(mElementContext.getResponse());
	}

	/**
	 * Sets a response header with the given name and date-value. The date is
	 * specified in terms of milliseconds since the epoch. If the header had
	 * already been set, the new value overwrites the previous one. The
	 * {@code containsHeader} method can be used to test for the presence
	 * of a header before setting its value.
	 *
	 * @param name the name of the header to set
	 * @param date the assigned date value
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #containsHeader
	 * @see #addDateHeader
	 * @since 1.0
	 */
	public void setDateHeader(String name, long date)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().setDateHeader(name, date);
	}

	/**
	 * Sets a response header with the given name and value. If the header had
	 * already been set, the new value overwrites the previous one. The
	 * {@code containsHeader} method can be used to test for the presence
	 * of a header before setting its value.
	 *
	 * @param name the name of the header
	 * @param value the header value If it contains octet string, it should be
	 * encoded according to RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt)
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #containsHeader
	 * @see #addHeader
	 * @since 1.0
	 */
	public void setHeader(String name, String value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().setHeader(name, value);
	}

	/**
	 * Sets a response header with the given name and integer value. If the
	 * header had already been set, the new value overwrites the previous one.
	 * The {@code containsHeader} method can be used to test for the
	 * presence of a header before setting its value.
	 *
	 * @param name the name of the header
	 * @param value the assigned integer value
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #containsHeader
	 * @see #addIntHeader
	 * @since 1.0
	 */
	public void setIntHeader(String name, int value)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().setIntHeader(name, value);
	}

	/**
	 * Sets the status code for this response. This method is used to set the
	 * return status code when there is no error (for example, for the status
	 * codes SC_OK or SC_MOVED_TEMPORARILY). If there is an error, and the
	 * caller wishes to invoke an error-page defined in the web application,
	 * the {@code sendError} method should be used instead.
	 * <p>The container clears the buffer and sets the Location header,
	 * preserving cookies and other headers.
	 *
	 * @param statusCode the status code
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #sendError
	 * @since 1.0
	 */
	public void setStatus(int statusCode)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().setStatus(statusCode);
	}

	/**
	 * Sets the locale of the response, if the response has not been committed
	 * yet. It also sets the response's character encoding appropriately for
	 * the locale, if the character encoding has not been explicitly set using
	 * {@link #setContentType} and the response hasn't been committed yet. If
	 * the deployment descriptor contains a
	 * {@code locale-encoding-mapping-list} element, and that element
	 * provides a mapping for the given locale, that mapping is used.
	 * Otherwise, the mapping from locale to character encoding is container
	 * dependent.
	 * <p>This method may be called repeatedly to change locale and character
	 * encoding. The method has no effect if called after the response has
	 * been committed. It does not set the response's character encoding if it
	 * is called after {@link #setContentType} has been called with a charset
	 * specification, or after the response has been committed.
	 * <p>Containers must communicate the locale and the character encoding
	 * used for the servlet response's writer to the client if the protocol
	 * provides a way for doing so. In the case of HTTP, the locale is
	 * communicated via the {@code Content-Language} header, the
	 * character encoding as part of the {@code Content-Type} header for
	 * text media types. Note that the character encoding cannot be
	 * communicated via HTTP headers if the servlet does not specify a content
	 * type; however, it is still used to encode text written via the servlet
	 * response's writer.
	 *
	 * @param locale the locale of the response
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @see #getResponseLocale
	 * @see #setContentType
	 * @since 1.0
	 */
	public void setResponseLocale(Locale locale)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getResponse().setLocale(locale);
	}

	/**
	 * Returns the locale specified for this response using the {@link
	 * #setResponseLocale} method. Calls made to
	 * {@code setResponseLocale} after the response is committed have no
	 * effect. If no locale has been specified, the container's default locale
	 * is returned.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the locale of the response
	 * @see #setResponseLocale
	 * @since 1.0
	 */
	public Locale getResponseLocale()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getResponse().getLocale();
	}

	/**
	 * Returns the root URL of the server that is running this web
	 * applications.
	 * <p>This includes the protocol, the server name and the server port, for
	 * example: {@code http://www.somehost.com:8080}.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the server's root url
	 * @since 1.0
	 */
	public String getServerRootUrl()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getServerRootUrl(-1);
	}

	/**
	 * Returns the root URL of this web applications.
	 * <p>This includes the protocol, the server name, the server port and the
	 * URL of RIFE's gateway, for example:
	 * {@code http://www.somehost.com:8080/my/webapp/}.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the web application's root url
	 * @since 1.0
	 */
	public String getWebappRootUrl()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getWebappRootUrl(-1);
	}

	/**
	 * Returns the name of the character encoding used in the body of this
	 * request. This method returns {@code null} if the request does not
	 * specify a character encoding
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String} containing the name of the character
	 * encoding; or
	 * <p>{@code null} if the request does not specify a character
	 * encoding
	 * @since 1.0
	 */
	public String getRequestCharacterEncoding()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getCharacterEncoding();
	}

	/**
	 * Returns the MIME type of the body of the request, or {@code null}
	 * if the type is not known. For HTTP servlets, same as the value of the
	 * CGI variable CONTENT_TYPE.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String} containing the name of the MIME type of
	 * the request; or
	 * <p>{@code null} if the type is not known
	 * @since 1.0
	 */
	public String getContentType()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getContentType();
	}

	/**
	 * Returns the value of the specified request header as a
	 * {@code long} value that represents a {@code Date} object. Use
	 * this method with headers that contain dates, such as
	 * {@code If-Modified-Since}.
	 * <p>The date is returned as the number of milliseconds since January 1,
	 * 1970 GMT. The header name is case insensitive.
	 * <p>If the request did not have a header of the specified name, this
	 * method returns -1. If the header can't be converted to a date, the
	 * method throws an {@code IllegalArgumentException}.
	 *
	 * @param name a {@code String} specifying the name of the header
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code long} value representing the date specified in
	 * the header expressed as the number of milliseconds since January 1,
	 * 1970 GMT, or -1 if the named header was not included with the request
	 * @since 1.0
	 */
	public long getDateHeader(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getDateHeader(name);
	}

	/**
	 * Returns the value of the specified request header as a
	 * {@code String}. If the request did not include a header of the
	 * specified name, this method returns {@code null}. If there are
	 * multiple headers with the same name, this method returns the first head
	 * in the request. The header name is case insensitive. You can use this
	 * method with any request header.
	 *
	 * @param name a {@code String} specifying the header name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String} containing the value of the requested
	 * header; or
	 * <p>{@code null} if the request does not have a header of that name
	 * @since 1.0
	 */
	public String getHeader(String name)
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getHeader(name);
	}

	/**
	 * Returns an enumeration of all the header names this request contains.
	 * If the request has no headers, this method returns an empty
	 * enumeration.
	 * <p>Some servlet containers do not allow servlets to access headers
	 * using this method, in which case this method returns {@code null}
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return an enumeration of all the header names sent with this request;
	 * if the request has no headers, an empty enumeration; if the servlet
	 * container does not allow servlets to use this method, {@code null}
	 * @since 1.0
	 */
	public Enumeration getHeaderNames()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getHeaderNames();
	}

	/**
	 * Returns all the values of the specified request header as an
	 * {@code Enumeration} of {@code String} objects.
	 * <p>Some headers, such as {@code Accept-Language} can be sent by
	 * clients as several headers each with a different value rather than
	 * sending the header as a comma separated list.
	 * <p>If the request did not include any headers of the specified name,
	 * this method returns an empty {@code Enumeration}. The header name
	 * is case insensitive. You can use this method with any request header.
	 *
	 * @param name a {@code String} specifying the header name
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return an {@code Enumeration} containing the values of the
	 * requested header. If the request does not have any headers of that name
	 * return an empty enumeration. If the container does not allow access to
	 * header information, it returns {@code null}.
	 * @since 1.0
	 */
	public Enumeration getHeaders(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getHeaders(name);
	}

	/**
	 * Retrieves initialization configuration of this web application.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the initialization configuration instance of this web
	 * application
	 * @see InitConfig
	 * @since 1.0
	 */
	public InitConfig getInitConfig()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getInitConfig();
	}

	/**
	 * Returns the value of the specified request header as an
	 * {@code int}. If the request does not have a header of the
	 * specified name, this method returns -1. If the header cannot be
	 * converted to an integer, this method throws a
	 * {@code NumberFormatException}.
	 * <p>The header name is case insensitive.
	 *
	 * @param name a {@code String} specifying the name of a request
	 * header
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return an integer expressing the value of the request header; or
	 * <p>{@code -1} if the request doesn't have a header of this name
	 * @since 1.0
	 */
	public int getIntHeader(String name)
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getIntHeader(name);
	}

	/**
	 * Returns the preferred {@code Locale} that the client will accept
	 * content in, based on the Accept-Language header. If the client request
	 * doesn't provide an Accept-Language header, this method returns the
	 * default locale for the server.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the preferred {@code Locale} for the client
	 * @since 1.0
	 */
	public Locale getRequestLocale()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getLocale();
	}

	/**
	 * Returns an {@code Enumeration} of {@code Locale} objects
	 * indicating, in decreasing order starting with the preferred locale, the
	 * locales that are acceptable to the client based on the Accept-Language
	 * header. If the client request doesn't provide an Accept-Language
	 * header, this method returns an {@code Enumeration} containing one
	 * {@code Locale}, the default locale for the server.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return an {@code Enumeration} of preferred {@code Locale}
	 * objects for the client
	 * @since 1.0
	 */
	public Enumeration getRequestLocales()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getLocales();
	}

	/**
	 * Returns any extra path information associated with the URL the client
	 * sent when it made this request. The extra path information follows the
	 * element URL but precedes the query string and will start with a "/"
	 * character.
	 * <p>The URL of an element that should support pathinfo, has to end with
	 * an asterisk (for example: {@code /my/url/*}).
	 * <p>This method returns an empty string if there was no extra path
	 * information.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String}, decoded by the web engine, specifying
	 * extra path information that comes after the element URL but before the
	 * query string in the request URL; or
	 * <p>or an empty string if the URL does not have any extra path
	 * information
	 * @since 1.0
	 */
	public String getPathInfo()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getElementState().getPathInfo();
	}

	/**
	 * Returns the method of this request.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return the method of this request
	 * @since 1.0
	 */
	public RequestMethod getMethod()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getElementState().getMethod();
	}

	/**
	 * Returns the name and version of the protocol the request uses in the
	 * form <i>protocol/majorVersion.minorVersion</i>, for example, HTTP/1.1.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String} containing the protocol name and version
	 * number
	 * @since 1.0
	 */
	public String getProtocol()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getProtocol();
	}

	/**
	 * Returns the Internet Protocol (IP) address of the client or last proxy
	 * that sent the request.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a {@code String} containing the IP address of the client
	 * that sent the request
	 * @since 1.0
	 */
	public String getRemoteAddr()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getRemoteAddr();
	}

	/**
	 * Returns the login of the user making this request, if the user has been
	 * authenticated, or {@code null} if the user has not been
	 * authenticated. Whether the user name is sent with each subsequent
	 * request depends on the browser and type of authentication.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a {@code String} specifying the login of the user making
	 * this request; or
	 * <p>{@code null} if the user login is not known
	 * @since 1.0
	 */
	public String getRemoteUser()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getRemoteUser();
	}

	/**
	 * Returns the fully qualified name of the client or the last proxy that
	 * sent the request. If the engine cannot or chooses not to resolve the
	 * hostname (to improve performance), this method returns the
	 * dotted-string form of the IP address.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return a {@code String} containing the fully qualified name of
	 * the client
	 * @since 1.0
	 */
	public String getRemoteHost()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getRemoteHost();
	}

	/**
	 * Returns the port number to which the request was sent. It is the value
	 * of the part after ":" in the {@code Host} header value, if any, or
	 * the server port where the client connection was accepted on.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return an integer specifying the port number
	 * @since 1.0
	 */
	public int getServerPort()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getServerPort();
	}

	/**
	 * Returns the name of the scheme used to make this request, for example,
	 * {@code http}, {@code https}, or {@code ftp}. Different
	 * schemes have different rules for constructing URLs, as noted in RFC
	 * 1738.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String} containing the name of the scheme used to
	 * make this request
	 * @since 1.0
	 */
	public String getScheme()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getScheme();
	}

	/**
	 * Returns the host name of the server to which the request was sent. It
	 * is the value of the part before ":" in the {@code Host} header
	 * value, if any, or the resolved server name, or the server IP address.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a {@code String} containing the name of the server
	 * @since 1.0
	 */
	public String getServerName()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getServerName();
	}

	/**
	 * Returns a boolean indicating whether this request was made using a
	 * secure channel, such as HTTPS.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have access to the request data (eg. you're inside a child trigger); or
	 * if there's no active element context (eg. you're using this method
	 * inside the constructor instead of inside the {@link #initialize()}
	 * method)
	 * @return a boolean indicating if the request was made using a secure
	 * channel
	 * @since 1.0
	 */
	public boolean isSecure()
	throws EngineException
	{
		if (!mRequestAccessEnabled)     throw new RequestAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().isSecure();
	}

	/**
	 * Returns the value of the named attribute as an {@code Object}, or
	 * {@code null} if no attribute of the given name exists.
	 * <p>Attributes can be set two ways. The servlet container may set
	 * attributes to make available custom information about a request. For
	 * example, for requests made using HTTPS, the attribute
	 * {@code javax.servlet.request.X509Certificate} can be used to
	 * retrieve information on the certificate of the client. Attributes can
	 * also be set programatically using {@link #setRequestAttribute}. This allows
	 * information to be embedded into a request an communicate amongst
	 * elements.
	 * <p>Attribute names should follow the same conventions as package names.
	 * This specification reserves names matching {@code java.*},
	 * {@code javax.*}, and {@code sun.*}.
	 *
	 * @param name a {@code String} specifying the name of the attribute
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return an {@code Object} containing the value of the attribute,
	 * or {@code null} if the attribute does not exist
	 * @see #hasRequestAttribute
	 * @see #getRequestAttributeNames
	 * @see #removeRequestAttribute
	 * @see #setRequestAttribute
	 * @since 1.0
	 */
	public Object getRequestAttribute(String name)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getRequestAttribute(name);
	}

	/**
	 * Checks if a request attribute exists.
	 *
	 * @param name a {@code String} specifying the name of the attribute
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return {@code true} if the attribute exists; or
	 * <p>{@code false} otherwise
	 * @see #getRequestAttribute
	 * @see #getRequestAttributeNames
	 * @see #removeRequestAttribute
	 * @see #setRequestAttribute
	 * @since 1.0
	 */
	public boolean hasRequestAttribute(String name)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().hasRequestAttribute(name);
	}

	/**
	 * Returns an {@code Enumeration} containing the names of the
	 * attributes available to this request. This method returns an empty
	 * {@code Enumeration} if the request has no attributes available to
	 * it.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return an {@code Enumeration} of strings containing the names of
	 * the request's attributes
	 * @see #getRequestAttribute
	 * @see #hasRequestAttribute
	 * @see #removeRequestAttribute
	 * @see #setRequestAttribute
	 * @since 1.0
	 */
	public Enumeration getRequestAttributeNames()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getRequestAttributeNames();
	}

	/**
	 * Removes an attribute from this request. This method is not generally
	 * needed as attributes only persist as long as the request is being
	 * handled.
	 * <p>Attribute names should follow the same conventions as package names.
	 * Names beginning with {@code java.*}, {@code javax.*}, and
	 * {@code com.sun.*}, are reserved for use by Sun Microsystems.
	 *
	 * @param name a {@code String} specifying the name of the attribute
	 * to remove
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getRequestAttribute
	 * @see #hasRequestAttribute
	 * @see #getRequestAttributeNames
	 * @see #setRequestAttribute
	 * @since 1.0
	 */
	public void removeRequestAttribute(String name)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getRequestState().removeRequestAttribute(name);
	}

	/**
	 * Stores an attribute in this request. Attributes are reset between
	 * requests.
	 * <p>Attribute names should follow the same conventions as package names.
	 * Names beginning with {@code java.*}, {@code javax.*}, and
	 * {@code com.sun.*}, are reserved for use by Sun Microsystems. <br>
	 * If the object passed in is null, the effect is the same as calling
	 * {@link #removeRequestAttribute}.
	 *
	 * @param name a {@code String} specifying the name of the attribute
	 * @param object the {@code Object} to be stored
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @see #getRequestAttribute
	 * @see #hasRequestAttribute
	 * @see #getRequestAttributeNames
	 * @see #removeRequestAttribute
	 * @since 1.0
	 */
	public void setRequestAttribute(String name, Object object)
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		mElementContext.getRequestState().setRequestAttribute(name, object);
	}

	/**
	 * Retrieves the context of this element.
	 * <p>By default, this method will throw an exception since it gives raw
	 * access to web engine features that aren't managed. See {@link
	 * #setProhibitRawAccess} for more information about activating it.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have raw access to the web engine; or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @return the context that belongs to this element instance
	 * @see ElementContext
	 * @see #setProhibitRawAccess
	 * @since 1.0
	 */
	public ElementContext getElementContext()
	throws EngineException
	{
		if (prohibitRawAccess())        throw new RawAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext;
	}

	/**
	 * Returns the current {@code HttpServletRequest}.
	 * <p>By default, this method will throw an exception since it gives raw
	 * access to web engine features that aren't managed. See {@link
	 * #setProhibitRawAccess} for more information about activating it.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have raw access to the web engine; or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @return the current {@code HttpServletRequest}
	 * @see #setProhibitRawAccess
	 * @since 1.0
	 */
	public HttpServletRequest getHttpServletRequest()
	throws EngineException
	{
		if (prohibitRawAccess())        throw new RawAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getRequest().getHttpServletRequest();
	}

	/**
	 * Returns the current {@code HttpServletResponse}.
	 * <p>By default, this method will throw an exception since it gives raw
	 * access to web engine features that aren't managed. See {@link
	 * #setProhibitRawAccess} for more information about activating it.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if you don't
	 * have raw access to the web engine; or if there's no active element
	 * context (eg. you're using this method inside the constructor instead of
	 * inside the {@link #initialize()} method)
	 * @return the current {@code HttpServletResponse}
	 * @see #setProhibitRawAccess
	 * @since 1.0
	 */
	public HttpServletResponse getHttpServletResponse()
	throws EngineException
	{
		if (prohibitRawAccess())        throw new RawAccessDeniedException();
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getResponse().getHttpServletResponse();
	}

	/**
	 * Returns the {@code ServletContext} of this web application.
	 * <p>By default, this method will throw an exception since it gives raw
	 * access to web engine features that aren't managed. See {@link
	 * #setProhibitRawAccess} for more information about activating it.
	 *
	 * @exception com.uwyn.rife.engine.exceptions.EngineException if there's
	 * no active element context (eg. you're using this method inside the
	 * constructor instead of inside the {@link #initialize()} method)
	 * @return the {@code ServletContext}
	 * @see #setProhibitRawAccess
	 * @since 1.0
	 */
	public ServletContext getServletContext()
	throws EngineException
	{
		if (null == mElementContext)    throw new ElementContextMissingException();

		return mElementContext.getRequestState().getInitConfig().getServletContext();
	}

	void setElementAware(ElementAware elementAware)
	{
		mElementAware = elementAware;
		elementAware.noticeElement(this);
	}

	ElementAware getElementAware()
	{
		return mElementAware;
	}

	ElementContext _getElementContext()
	{
		return mElementContext;
	}

	public void setElementContext(ElementContext elementContext)
	{
		mElementContext = elementContext;
	}

	void setElementInfo(ElementInfo elementInfo)
	{
		assert elementInfo != null;

		mElementInfo = elementInfo;
	}

	void enableRequestAccess(boolean enabled)
	{
		mRequestAccessEnabled = enabled;
	}

	public Object clone()
	throws CloneNotSupportedException
	{
		ElementSupport new_elementsupport = (ElementSupport)super.clone();

		new_elementsupport.mElementContext = null;

		if (mElementAware != null)
		{
			// prevent a self-referencing clone
			if (this == mElementAware)
			{
				new_elementsupport.mElementAware = (ElementAware)new_elementsupport;
			}
			// clone it since the ElementAware instance is not the same as
			// the ElementSupport instance
			else
			{
				new_elementsupport.mElementAware = ObjectUtils.genericClone(mElementAware);
				if (null == new_elementsupport.mElementAware)
				{
					throw new CloneNotSupportedException();
				}
			}
			new_elementsupport.mElementAware.noticeElement(new_elementsupport);
		}

		return new_elementsupport;
	}
}


