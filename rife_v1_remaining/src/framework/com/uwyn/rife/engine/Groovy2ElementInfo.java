/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Groovy2ElementInfo.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.NotFoundProcessingErrorException;
import com.uwyn.rife.engine.exceptions.ParsingErrorException;
import com.uwyn.rife.engine.exceptions.ProcessingErrorException;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;
import groovy.util.BuilderSupport;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

class Groovy2ElementInfo implements ElementInfoProcessor
{
	Groovy2ElementInfo()
	{
	}

	public void processElementInfo(ElementInfoBuilder builder, String declarationName, ResourceFinder resourceFinder)
	throws EngineException
	{
		GroovyProcessor processor = new GroovyProcessor(builder);
		
		String processed_path = null;
		try
		{
			// process the element declaration
			try
			{
				processed_path = declarationName;
				processor.processGroovy(processed_path, resourceFinder);
			}
			catch (NotFoundProcessingErrorException e)
			{
				processed_path = DEFAULT_ELEMENTS_PATH+declarationName;
				processor.processGroovy(processed_path, resourceFinder);
			}
		}
		catch (Exception e)
		{
			throw new ProcessingErrorException("element", declarationName, e);
		}
	
		// obtain the modification time
		if (RifeConfig.Engine.getSiteAutoReload())
		{
			URL resource = resourceFinder.getResource(processed_path);
			if (null == resource)
			{
				throw new NotFoundProcessingErrorException("element", processed_path, null);
			}
			
			try
			{
				builder.addResourceModificationTime(new UrlResource(resource, processed_path), resourceFinder.getModificationTime(resource));
			}
			catch (ResourceFinderErrorException e)
			{
				throw new ProcessingErrorException("element", declarationName, "Error while retrieving the modification time.", e);
			}
		}
	}
	
	private class GroovyProcessor extends BuilderSupport
	{
		private ElementInfoBuilder		mElementInfoBuilder = null;
		private String					mGroovyPath = null;

		private	String					mCurrentInput = null;
		private	String					mCurrentOutput = null;
		private	String					mCurrentIncookie = null;
		private	String					mCurrentOutcookie = null;
		private	String					mCurrentParameter = null;
		private ArrayList<String>		mDefaults = null;
		private	SubmissionBuilder		mSubmissionBuilder = null;
		
		private GroovyProcessor(ElementInfoBuilder builder)
		{
			mElementInfoBuilder = builder;
		}
		
		public synchronized void processGroovy(final String groovyPath, ResourceFinder resourceFinder)
		{
			if (null == groovyPath)			throw new IllegalArgumentException("groovyPath can't be null.");
			if (groovyPath.length() == 0)	throw new IllegalArgumentException("groovyPath can't be empty.");
			if (null == resourceFinder)		throw new IllegalArgumentException("resourceFinder can't be null.");
			
			mGroovyPath = groovyPath;

			// retrieve a stream towards the groovy script
			Class script_class = null;
			try
			{
				script_class = resourceFinder.useStream(groovyPath, new InputStreamUser() {
						public Class useInputStream(InputStream stream)
						throws InnerClassException
						{
							if (null == stream)
							{
								throw new NotFoundProcessingErrorException("element", groovyPath, null);
							}
							
							// parse the groovy script and create a class
							GroovyClassLoader mLoader = new GroovyClassLoader(getClass().getClassLoader(), new CompilerConfiguration());
							GroovyCodeSource code_source = new GroovyCodeSource(stream, "elementinfobuilder.groovy", "/groovy/shell");
							try
							{
								return mLoader.parseClass(code_source);
							}
							catch (Throwable e)
							{
								throw new ParsingErrorException("element", groovyPath, e);
							}
						}
					});
			}
			catch (ResourceFinderErrorException e)
			{
				throw new NotFoundProcessingErrorException("element", groovyPath, e);
			}
			
			// setup the script bindings and run it
			Binding binding = new Binding();
			binding.setVariable("processor", this);
			binding.setVariable("builder", mElementInfoBuilder);
			Script script = InvokerHelper.createScript(script_class, binding);
			script.run();
		}
		
		protected void setParent(Object parent, Object child)
		{
//			System.out.println("setParent "+parent+", "+child);
		}
		
		protected Object createNode(Object node)
		{
			return createNode(node, null, null);
		}
		
		protected Object createNode(Object node, Object value)
		{
			return createNode(node, null, value);
		}
		
		protected Object createNode(Object node, Map attributes)
		{
			return createNode(node, attributes, null);
		}
		
		protected Object createNode(Object node, Map atts, Object value)
		{
//			System.out.println("createNode "+qName+", "+atts+", "+value);
			
			if (null == atts)
			{
				atts = new HashMap();
			}

			if (node.equals("element"))
			{
				String	content_type = Convert.toString(atts.get("contenttype"));
				String	extending = Convert.toString(atts.get("extends"));
				String	implementation = Convert.toString(atts.get("implementation"));
				
				mElementInfoBuilder.setContentType(content_type);
				
				// process the class it extends from
				if (extending != null)
				{
					mElementInfoBuilder.extendsFrom(extending);
				}
				
				if (implementation != null)
				{
					mElementInfoBuilder.setImplementation(implementation);
				}
			}
			else if (node.equals("property"))
			{
				mElementInfoBuilder.addStaticProperty(Convert.toString(atts.get("name")), new PropertyValueObject(value));
			}
			else if (node.equals("input"))
			{
				mCurrentInput = Convert.toString(atts.get("name"));
				mDefaults = new ArrayList<String>();
			}
			else if (node.equals("inbean"))
			{
				String classname = Convert.toString(atts.get("classname"));
				String prefix = Convert.toString(atts.get("prefix"));
				String name = Convert.toString(atts.get("name"));
				String group = Convert.toString(atts.get("group"));
				
				mElementInfoBuilder.addInBean(classname, prefix, name, group);
			}
			else if (node.equals("output"))
			{
				mCurrentOutput = Convert.toString(atts.get("name"));
				mDefaults = new ArrayList<String>();
			}
			else if (node.equals("outbean"))
			{
				String classname = Convert.toString(atts.get("classname"));
				String prefix = Convert.toString(atts.get("prefix"));
				String name = Convert.toString(atts.get("name"));
				String group = Convert.toString(atts.get("group"));

				mElementInfoBuilder.addOutBean(classname, prefix, name, group);
			}
			else if (node.equals("incookie"))
			{
				mCurrentIncookie = Convert.toString(atts.get("name"));
				mDefaults = new ArrayList<String>();
			}
			else if (node.equals("outcookie"))
			{
				mCurrentOutcookie = Convert.toString(atts.get("name"));
				mDefaults = new ArrayList<String>();
			}
			else if (node.equals("childtrigger"))
			{
				mElementInfoBuilder.addChildTrigger(Convert.toString(atts.get("name")));
			}
			else if (node.equals("submission"))
			{
				mSubmissionBuilder = mElementInfoBuilder.enterSubmission(Convert.toString(atts.get("name")));
				
				boolean	cancel_continuations = false;				
				String	continuations = Convert.toString(atts.get("continuations"));
				if (continuations != null &&
					continuations.equals("cancel"))
				{
					cancel_continuations = true;
				}
				
				mSubmissionBuilder.cancelContinuations(cancel_continuations);
			}
			else if (node.equals("param"))
			{
				if (Convert.toString(atts.get("name")) != null &&
					Convert.toString(atts.get("regexp")) != null)
				{
					throw new ParsingErrorException("element", mGroovyPath, "A submission parameter can't have both a name and a regexp attribute.", null);
				}
				
				if (Convert.toString(atts.get("name")) != null)
				{
					mCurrentParameter = Convert.toString(atts.get("name"));
					mDefaults = new ArrayList<String>();
				}
				else if (Convert.toString(atts.get("regexp")) != null)
				{
					mSubmissionBuilder.addParameterRegexp(Convert.toString(atts.get("regexp")));
					mDefaults = null;
				}
				else
				{
					throw new ParsingErrorException("element", mGroovyPath, "A submission parameter needs either a name or a regexp attribute.", null);
				}
			}
			else if (node.equals("bean"))
			{
				String classname = Convert.toString(atts.get("classname"));
				String prefix = Convert.toString(atts.get("prefix"));
				String name = Convert.toString(atts.get("name"));
				String group = Convert.toString(atts.get("group"));
				
				mSubmissionBuilder.addBean(classname, prefix, name, group);
			}
			else if (node.equals("file"))
			{
				mSubmissionBuilder.addFile(Convert.toString(atts.get("name")));
			}
			else if (node.equals("defaultvalue"))
			{
				if (null == mDefaults)
				{
					throw new ParsingErrorException("element", mGroovyPath, "A submission parameter that's defined by a regular expression can't have default values.", null);
				}
				
				mDefaults.add((String)value);
			}
			else if (node.equals("exit"))
			{
				mElementInfoBuilder.addExit(Convert.toString(atts.get("name")));
			}
			else
			{
				throw new ParsingErrorException("element", mGroovyPath, "Unsupport element name '"+node+"'.", null);
			}
			
			return node;
		}
		
		protected void nodeCompleted(Object parent, Object node)
		{
//			System.out.println("nodeCompleted "+parent+", "+node);
			
			if (node.equals("input"))
			{
				String[]	defaults = null;
				if (mDefaults.size() > 0)
				{
					defaults = new String[mDefaults.size()];
					defaults = mDefaults.toArray(defaults);
				}
				mElementInfoBuilder.addInput(mCurrentInput, defaults);
				mCurrentInput = null;
				mDefaults = null;
			}
			else if (node.equals("output"))
			{
				String[]	defaults = null;
				if (mDefaults.size() > 0)
				{
					defaults = new String[mDefaults.size()];
					defaults = mDefaults.toArray(defaults);
				}
				mElementInfoBuilder.addOutput(mCurrentOutput, defaults);
				mCurrentOutput = null;
				mDefaults = null;
			}
			else if (node.equals("incookie"))
			{
				String defaultValue = null;
				if (mDefaults.size() > 0)
				{
					defaultValue = mDefaults.get(0);
				}
				mElementInfoBuilder.addIncookie(mCurrentIncookie, defaultValue);
				mCurrentIncookie = null;
				mDefaults = null;
			}
			else if (node.equals("outcookie"))
			{
				String defaultValue = null;
				if (mDefaults.size() > 0)
				{
					defaultValue = mDefaults.get(0);
				}
				mElementInfoBuilder.addOutcookie(mCurrentOutcookie, defaultValue);
				mCurrentOutcookie = null;
				mDefaults = null;
			}
			else if (node.equals("submission"))
			{
				mSubmissionBuilder.leaveSubmission();
			}
			else if (node.equals("param"))
			{
				if (mDefaults != null)
				{
					String[]	defaults = null;
					if (mDefaults.size() > 0)
					{
						defaults = new String[mDefaults.size()];
						defaults = mDefaults.toArray(defaults);
					}
					mSubmissionBuilder.addParameter(mCurrentParameter, defaults);
				}
				mCurrentParameter = null;
				mDefaults = null;
			}
		}
	}
}

