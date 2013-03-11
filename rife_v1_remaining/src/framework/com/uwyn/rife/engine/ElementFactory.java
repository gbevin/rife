/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.logging.Logger;

import com.uwyn.rife.engine.exceptions.*;
import com.uwyn.rife.instrument.RifeAgent;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.*;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.ConversionException;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

class ElementFactory
{
	static final	ElementFactory	INSTANCE = new ElementFactory();
	
	private static final String		SCRIPT_EXT_JAVASCRIPT = ".js";
	private static final String		SCRIPT_EXT_JACL = ".jacl";
	private static final String		SCRIPT_EXT_JYTHON = ".py";
	private static final String		SCRIPT_EXT_PNUTS = ".pnut";
	private static final String		SCRIPT_EXT_RUBY = ".rb";
	private static final String		SCRIPT_EXT_BEANSHELL = ".bsh";
	private static final String		SCRIPT_EXT_GROOVY = ".groovy";
	private static final String		SCRIPT_EXT_JANINO = ".janino";
	private static final String[]	SCRIPT_EXTENSIONS = new String[] {
		SCRIPT_EXT_JAVASCRIPT, SCRIPT_EXT_JACL, SCRIPT_EXT_JYTHON,
		SCRIPT_EXT_PNUTS, SCRIPT_EXT_RUBY, SCRIPT_EXT_BEANSHELL,
		SCRIPT_EXT_GROOVY, SCRIPT_EXT_JANINO};

	private static String				mJythonClassPath = "";
	private static ScriptLoaderGroovy	mScriptLoaderGroovy = null;
	private static ScriptLoaderJanino	mScriptLoaderJanino = null;

	private ResourceFinder	mResourceFinder = ResourceFinderClasspath.getInstance();
		
	private ElementFactory()
	{
	}
	
	ResourceFinder getResourceFinder()
	{
		return mResourceFinder;
	}
	
	static ElementType detectElementType(String implementation)
	{
		if (null == implementation)	return null;
		
		// if there are path seperators, it's a script
		if (implementation.indexOf("/") != -1)
		{
			return ElementType.SCRIPT;
		}
		
		// check if the implementation name ends with a script file extension
		for (String extension : SCRIPT_EXTENSIONS)
		{
			if (implementation.endsWith(extension))
			{
				return ElementType.SCRIPT;
			}
		}
		
		// it's thus a java class name
		return ElementType.JAVA_CLASS;
	}
	
	Class getJavaClass(String declarationName, String implementation)
	throws EngineException
	{
		try
		{
			// try to look it up as a java element and compile it if needed
			Class	element_class = null;
			if (getClass().getClassLoader() instanceof EngineClassLoader)
			{
				element_class = ((EngineClassLoader)getClass().getClassLoader()).loadClass(implementation, true, true);
			}
			else
			{
				HierarchicalProperties properties = Rep.getProperties();
				if (!properties.contains(RifeAgent.AGENT_ACTIVE_PROPERTY) &&
					(!properties.contains("engineclassloader.enabled") ||
					 StringUtils.convertToBoolean(properties.get("engineclassloader.enabled").getValueString())))
				{
					Logger.getLogger(getClass().getPackage().getName()).warning("The element implementation class "+implementation +" is not being loaded by EngineClassLoader which means that continuations will not work. You should be executing your application with the com.uwyn.rife.test.RunWithEngineClassLoader class.");
				}
				element_class = getClass().getClassLoader().loadClass(implementation);
			}

			return element_class;
		}
		catch (ClassNotFoundException e2)
		{
			throw new ElementImplementationNotFoundException(declarationName, implementation, e2);
		}
	}

	ElementAware getJavaInstance(String declarationName, String implementation)
	throws EngineException
	{
		ElementAware element = null;

		try
		{
			// try to look it up as a java element and compile it if needed
			Class	element_class = getJavaClass(declarationName, implementation);
			element = (ElementAware) element_class.newInstance();
		}
		catch (IllegalAccessException e2)
		{
			// this should not happen
			throw new ElementImplementationInstantiationException(declarationName, implementation, e2);
		}
		catch (InstantiationException e2)
		{
			// this should not happen
			throw new ElementImplementationInstantiationException(declarationName, implementation, e2);
		}

		return element;
	}

	ElementSupport getInstance(final ElementInfo elementInfo, final boolean injectProperties)
	throws EngineException
	{
		if (null == elementInfo)	throw new IllegalArgumentException("elementInfo can't be null.");
		
		final ElementAware element_aware;

		if (ElementType.JAVA_CLASS == elementInfo.getType())
		{
			try
			{
				// try to obtain the element as a bytecode class from the classpath
				element_aware = getJavaInstance(elementInfo.getDeclarationName(), elementInfo.getImplementation());
			}
			catch (ElementCompilationFailedException e)
			{
				// this should not happen
				throw e;
			}
			catch (Throwable e)
			{
				throw new ElementImplementationInstantiationException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), e);
			}
		}
		else if (ElementType.JAVA_INSTANCE == elementInfo.getType())
		{
			try
			{
				element_aware = (Element)elementInfo.getImplementationBlueprint().clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new ElementImplementationInstantiationException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), e);
			}
		}
		// handle bean scripting framework scripts
		else if (ElementType.SCRIPT == elementInfo.getType())
		{
			if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_GROOVY))
			{
				if (null == mScriptLoaderGroovy)
				{
					mScriptLoaderGroovy = new ScriptLoaderGroovy(mResourceFinder);
				}
				
				element_aware = mScriptLoaderGroovy.getInstance(elementInfo);
			}
			else if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_JANINO))
			{
				if (null == mScriptLoaderJanino)
				{
					mScriptLoaderJanino = new ScriptLoaderJanino(mResourceFinder);
				}
				
				element_aware = mScriptLoaderJanino.getInstance(elementInfo);
			}
			else if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_JAVASCRIPT))
			{
				String code = getScriptCode(mResourceFinder, elementInfo);
				ScriptedEngine engine = new ScriptedEngineRhino(code);
				
				element_aware = new ElementScripted(engine);
			}
			else
			{
				String	language = null;
				
				if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_JACL))
				{
					language = "jacl";
				}
				else if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_JYTHON))
				{
					language = "jython";
					addRifeJarsToClasspathProperty();
				}
				else if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_PNUTS))
				{
					language = "pnuts";
				}
				else if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_RUBY))
				{
					language = "ruby";
				}
				else if (elementInfo.getImplementation().endsWith(SCRIPT_EXT_BEANSHELL))
				{
					language = "beanshell";
				}
				
				String code = getScriptCode(mResourceFinder, elementInfo);
				ScriptedEngine engine = new ScriptedEngineBSF(language, code);
				
				element_aware = new ElementScripted(engine);
			}
		}
		else
		{
			throw new ElementImplementationInstantiationException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), null);
		}
		
		ElementSupport element = null;
		if (element_aware instanceof ElementSupport)
		{
			element = (ElementSupport)element_aware;
		}
		else
		{
			element = new ElementSupport();
		}
		element.setElementAware(element_aware);
		element.setElementInfo(elementInfo);
		
		// handle IoC setter injection
		if (injectProperties)
		{
			injectProperties(elementInfo, element_aware);
		}
		
		return element;
	}

	static void injectProperties(final ElementInfo elementInfo, final ElementAware elementAware)
	throws PropertiesInjectionException
	{
		Collection<String> property_names = elementInfo.getInjectablePropertyNames();
		if (elementInfo.getInjectablePropertyNames().size() > 0)
		{
			String[] property_names_array = new String[property_names.size()];
			property_names.toArray(property_names_array);
			try
			{
				BeanUtils.processProperties(BeanUtils.SETTERS, elementAware.getClass(), property_names_array, null, null, new BeanPropertyProcessor() {
						public boolean gotProperty(String name, PropertyDescriptor descriptor)
						throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
						{
							Method write = descriptor.getWriteMethod();
							Class type = write.getParameterTypes()[0];
							try
							{
								write.invoke(elementAware, Convert.toType(elementInfo.getProperty(name), type));
							}
							catch (ConversionException e)
							{
								throw new PropertyInjectionException(elementInfo.getDeclarationName(), elementAware.getClass(), name, e);
							}
							
							return true;
						}
					});
			}
			catch (BeanUtilsException e)
			{
				throw new PropertiesInjectionException(elementInfo.getDeclarationName(), elementAware.getClass(), e);
			}
		}
	}

	static URL getScriptUrl(ResourceFinder resourceFinder, ElementInfo elementInfo)
	throws EngineException
	{
		URL	sourcename_url = resourceFinder.getResource(elementInfo.getImplementation());

		if (null == sourcename_url)
		{
			sourcename_url = resourceFinder.getResource(EngineClassLoader.DEFAULT_IMPLEMENTATIONS_PATH+elementInfo.getImplementation());
		}

		if (null == sourcename_url)
		{
			throw new ElementImplementationNotFoundException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), null);
		}
		
		return sourcename_url;
	}

	static String getScriptCode(ResourceFinder resourceFinder, ElementInfo elementInfo)
	throws EngineException
	{
		URL sourcename_url = getScriptUrl(resourceFinder, elementInfo);
		
		try
		{
			return FileUtils.readString(sourcename_url);
		}
		catch (FileUtilsErrorException e)
		{
			throw new ElementImplementationUnreadableException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), e);
		}
	}
 	
	private void addRifeJarsToClasspathProperty()
	{
		// only set the jython class path property when it's not null
		if (mJythonClassPath != null)
		{
			// try to generate the class path additions when the class path var is empty
			if (0 == mJythonClassPath.length())
			{
				URL	resource = this.getClass().getClassLoader().getResource("com/uwyn/rife/engine/ElementFactory.class");
				if (resource != null &&
					resource.getProtocol().equals("jar"))
				{
					String	resource_path = null;
					try
					{
						resource_path = URLDecoder.decode(resource.getPath(), "ISO-8859-1");
						String	prefix = "file:";
						String	jar_filename = resource_path.substring(prefix.length(), resource_path.indexOf('!'));
						File	jar_file = new File(jar_filename);
						File	jar_directory = jar_file.getParentFile();
						if (jar_directory != null &&
							jar_directory.isDirectory())
						{
							StringBuilder	java_class_path = new StringBuilder(System.getProperty("java.class.path"));
							String			jar_directory_path = URLDecoder.decode(jar_directory.getPath(), "ISO-8859-1");
							// check if the class path doesn't already contain the jars in the WEB-INF/lib directory
							if (-1 == java_class_path.indexOf(jar_directory_path))
							{
								// add the jars in the WEB-INF/lib directory to the class path property
								String[]	jar_filenames = jar_directory.list();
								for (String jar_filenames_entry : jar_filenames)
								{
									if (jar_filenames_entry.endsWith(".jar"))
									{
										java_class_path.append(File.pathSeparator);
										java_class_path.append(jar_directory_path);
										java_class_path.append(File.separator);
										java_class_path.append(jar_filenames_entry);
									}
								}

								// check if the WEB-INF/classes directory exists and should be added too
								if (jar_directory_path.endsWith("WEB-INF/lib"))
								{
									StringBuilder classes_directory_path = new StringBuilder(jar_directory.getParent());
									classes_directory_path.append(File.separator);
									classes_directory_path.append("classes");
									File classes_directory = new File(classes_directory_path.toString());
									if (classes_directory.exists() &&
										classes_directory.isDirectory())
									{
										java_class_path.append(File.pathSeparator);
										java_class_path.append(classes_directory_path);
									}
								}

								// store the constructed class path for quick reuse at later invocations
								mJythonClassPath = java_class_path.toString();
							}

							// set the new class path property
							System.setProperty("java.class.path", mJythonClassPath);
							return;
						}
					}
					catch (UnsupportedEncodingException e)
					{
						// should never fail, it's a standard encoding
					}
				}
			}
			else
			{
				System.setProperty("java.class.path", mJythonClassPath);
				return;
			}
			
			// this means that the class path additions couldn't be find
			// and thus the class path property should remain unchanged
			mJythonClassPath = null;
		}
	}
}
