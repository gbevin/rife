/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
  * $Id: Groovy2Site.java 3918 2008-04-14 17:35:35Z gbevin $
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

class Groovy2Site implements SiteProcessor
{
	Groovy2Site()
	{
	}

	public void processSite(SiteBuilder builder, String declarationName, ResourceFinder resourceFinder)
	throws EngineException
	{
		GroovyProcessor processor = new GroovyProcessor(builder);
		
		String processed_path = null;
		try
		{
			// process the site declaration
			try
			{
				processed_path = declarationName;
				processor.processGroovy(processed_path, resourceFinder);
			}
			catch (NotFoundProcessingErrorException e)
			{
				processed_path = DEFAULT_SITES_PATH+declarationName;
				processor.processGroovy(processed_path, resourceFinder);
			}
		}
		catch (Exception e)
		{
			throw new ProcessingErrorException("site", declarationName, e);
		}
	
		// obtain the modification time
		if (RifeConfig.Engine.getSiteAutoReload())
		{
			URL resource = resourceFinder.getResource(processed_path);
			if (null == resource)
			{
				throw new NotFoundProcessingErrorException("site", processed_path, null);
			}
			
			try
			{
				builder.addResourceModificationTime(new UrlResource(resource, processed_path), resourceFinder.getModificationTime(resource));
			}
			catch (ResourceFinderErrorException e)
			{
				throw new ProcessingErrorException("site", declarationName, "Error while retrieving the modification time.", e);
			}
		}
	}
	
	private class GroovyProcessor extends BuilderSupport
	{
		private SiteBuilder			mSiteBuilder = null;
		private String				mGroovyPath = null;

		private ElementInfoBuilder	mCurrentElementInfoBuilder = null;
		private FlowLinkBuilder		mCurrentFlowLinkBuilder = null;
		
		private String				mCurrentGlobalVar = null;
		private ArrayList<String>	mCurrentGlobalVarDefaults = null;
		
		private String 				mCurrentGlobalCookie = null;
		private String 				mCurrentGlobalCookieDefault = null;
		
		private GroovyProcessor(SiteBuilder builder)
		{
			mSiteBuilder = builder;
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
								throw new NotFoundProcessingErrorException("site", groovyPath, null);
							}
							
							// parse the groovy script and create a class
							try
							{
								GroovyClassLoader	mLoader = new GroovyClassLoader(getClass().getClassLoader(), new CompilerConfiguration());
								GroovyCodeSource code_source = new GroovyCodeSource(stream, "sitebuilder.groovy", "/groovy/shell");
								try
								{
									return mLoader.parseClass(code_source);
								}
								catch (Throwable e)
								{
									throw new ParsingErrorException("site", groovyPath, e);
								}
							}
							finally
							{
								try
								{
									stream.close();
								}
								catch (IOException e)
								{
									// don't do anything
								}
							}
						}
					});
			}
			catch (ResourceFinderErrorException e)
			{
				throw new NotFoundProcessingErrorException("site", groovyPath, e);
			}
				
			// setup the script bindings and run it
			Binding binding = new Binding();
			binding.setVariable("processor", this);
			binding.setVariable("builder", mSiteBuilder);
			Script script = InvokerHelper.createScript(script_class, binding);
			script.run();
		}
		
		protected void setParent(Object parent, Object child)
		{
//			System.out.println("setParent "+parent+", "+child);
		}
		
		protected Object createNode(Object name)
		{
			return createNode(name, null, null);
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
			
			if (node.equals("site"))
			{
				mSiteBuilder.setFallback(Convert.toString(atts.get("fallbackid")));
			}
			else if (node.equals("subsite"))
			{
				mSiteBuilder
					.enterSubsiteDeclaration(Convert.toString(atts.get("file")))
						.setId(Convert.toString(atts.get("id")))
						.setUrlPrefix(Convert.toString(atts.get("urlprefix")))
						.enterSubsite()
							.setInherits(Convert.toString(atts.get("inherits")))
							.setPre(Convert.toString(atts.get("pre")))
						.leaveSubsite()
					.leaveSubsiteDeclaration();
			}
			else if (node.equals("group"))
			{
				String inherits = Convert.toString(atts.get("inherits"));
				String pre = Convert.toString(atts.get("pre"));
				
				mSiteBuilder.enterGroup()
					.setInherits(inherits)
					.setPre(pre);
			}
			else if (node.equals("globalvar"))
			{
				mCurrentGlobalVar = Convert.toString(atts.get("name"));
				mCurrentGlobalVarDefaults = new ArrayList<String>();
			}
			else if (node.equals("globalcookie"))
			{
				mCurrentGlobalCookie = Convert.toString(atts.get("name"));
				mCurrentGlobalCookieDefault = null;
			}
			else if (node.equals("globalbean"))
			{
				String classname = Convert.toString(atts.get("classname"));
				String prefix = Convert.toString(atts.get("prefix"));
				String name = Convert.toString(atts.get("name"));
				String group = Convert.toString(atts.get("group"));
				
				mSiteBuilder.addGlobalBean(classname, prefix, name, group);
			}
			else if (node.equals("globalexit"))
			{
				String name = Convert.toString(atts.get("name"));
				String destid = Convert.toString(atts.get("destid"));
				boolean reflective = Convert.toBoolean(atts.get("reflect"), false);
				boolean snapback = Convert.toBoolean(atts.get("snapback"), false);
				boolean redirect = Convert.toBoolean(atts.get("redirect"), false);
				boolean	cancel_inheritance = false;
				boolean	cancel_embedding = false;
				boolean	cancel_continuations = false;
				
				String	inheritance = Convert.toString(atts.get("inheritance"));
				if (inheritance != null &&
					inheritance.equals("cancel"))
				{
					cancel_inheritance = true;
				}
				
				String	embedding = Convert.toString(atts.get("embedding"));
				if (embedding != null &&
					embedding.equals("cancel"))
				{
					cancel_embedding = true;
				}
				
				String	continuations = Convert.toString(atts.get("continuations"));
				if (continuations != null &&
					continuations.equals("cancel"))
				{
					cancel_continuations = true;
				}
				
				mSiteBuilder.addGlobalExit(name, destid, reflective, snapback, cancel_inheritance, cancel_embedding, redirect, cancel_continuations);
			}
			else if (node.equals("arrival"))
			{
				boolean redirect = Convert.toBoolean(atts.get("redirect"), false);
				mSiteBuilder.setArrival(Convert.toString(atts.get("destid")), redirect);
			}
			else if (node.equals("departure"))
			{
				mSiteBuilder.addDeparture(Convert.toString(atts.get("srcid")));
			}
			else if (node.equals("state"))
			{
				mSiteBuilder.enterState(Convert.toString(atts.get("store")));
			}
			else if (node.equals("element"))
			{
				mCurrentElementInfoBuilder = mSiteBuilder.enterElement(Convert.toString(atts.get("file")))
					.setId(Convert.toString(atts.get("id")))
					.setUrl(Convert.toString(atts.get("url")))
					.setInherits(Convert.toString(atts.get("inherits")))
					.setPre(Convert.toString(atts.get("pre")));
			}
			else if (node.equals("datalink"))
			{
				String srcoutput = Convert.toString(atts.get("srcoutput"));
				String srcoutbean = Convert.toString(atts.get("srcoutbean"));
				
				String dest_id = Convert.toString(atts.get("destid"));
				boolean snapback = Convert.toBoolean(atts.get("snapback"), false);

				String destinput = Convert.toString(atts.get("destinput"));
				String destinbean = Convert.toString(atts.get("destinbean"));
				
				if (mCurrentFlowLinkBuilder != null)
				{
					mCurrentFlowLinkBuilder.addDataLink(srcoutput, srcoutbean, snapback, destinput, destinbean);
				}
				else
				{
					mCurrentElementInfoBuilder.addDataLink(srcoutput, srcoutbean, dest_id, snapback, destinput, destinbean);
				}
			}
			else if (node.equals("flowlink"))
			{
				String srcexit = Convert.toString(atts.get("srcexit"));
				String destid = Convert.toString(atts.get("destid"));
				boolean snapback = Convert.toBoolean(atts.get("snapback"), false);
				boolean redirect = Convert.toBoolean(atts.get("redirect"), false);
				boolean	cancel_inheritance = false;
				boolean	cancel_embedding = false;
				boolean	cancel_continuations = false;
				
				String	inheritance = Convert.toString(atts.get("inheritance"));
				if (inheritance != null &&
					inheritance.equals("cancel"))
				{
					cancel_inheritance = true;
				}
				
				String	embedding = Convert.toString(atts.get("embedding"));
				if (embedding != null &&
					embedding.equals("cancel"))
				{
					cancel_embedding = true;
				}
				
				String	continuations = Convert.toString(atts.get("continuations"));
				if (continuations != null &&
					continuations.equals("cancel"))
				{
					cancel_continuations = true;
				}
				
				mCurrentFlowLinkBuilder = mCurrentElementInfoBuilder.enterFlowLink(srcexit)
					.destId(destid)
					.snapback(snapback)
					.cancelInheritance(cancel_inheritance)
					.cancelEmbedding(cancel_embedding)
					.redirect(redirect)
					.cancelContinuations(cancel_continuations);
			}
			else if (node.equals("autolink"))
			{
				String srcexit = Convert.toString(atts.get("srcexit"));
				String destid = Convert.toString(atts.get("destid"));
				boolean redirect = Convert.toBoolean(atts.get("redirect"), false);
				boolean	cancel_inheritance = false;
				boolean	cancel_embedding = false;
				boolean	cancel_continuations = false;
				
				String	inheritance = Convert.toString(atts.get("inheritance"));
				if (inheritance != null &&
					inheritance.equals("cancel"))
				{
					cancel_inheritance = true;
				}
				
				String	embedding = Convert.toString(atts.get("embedding"));
				if (embedding != null &&
					embedding.equals("cancel"))
				{
					cancel_embedding = true;
				}
				
				String	continuations = Convert.toString(atts.get("continuations"));
				if (continuations != null &&
					continuations.equals("cancel"))
				{
					cancel_continuations = true;
				}
				
				mCurrentElementInfoBuilder.addAutoLink(srcexit, destid, cancel_inheritance, cancel_embedding, redirect, cancel_continuations);
			}
			else if (node.equals("property"))
			{
				mCurrentElementInfoBuilder.addProperty(Convert.toString(atts.get("name")), new PropertyValueObject(value));
			}
			else if (node.equals("defaultvalue"))
			{
				if (null != mCurrentGlobalCookie)
				{
					mCurrentGlobalCookieDefault = (String)value;
				}
				else if( null != mCurrentGlobalVar )
				{
					mCurrentGlobalVarDefaults.add((String)value);
				}
			}
			else
			{
				throw new ParsingErrorException("site", mGroovyPath, "Unsupport element name '"+node+"'.", null);
			}

			return node;
		}
		
		protected void nodeCompleted(Object parent, Object node)
		{
//			System.out.println("nodeCompleted "+parent+", "+node);
			
			if (node.equals("element"))
			{
				mCurrentElementInfoBuilder.leaveElement();
				mCurrentElementInfoBuilder = null;
			}
			else if (node.equals("flowlink"))
			{
				mCurrentFlowLinkBuilder.leaveFlowLink();
				mCurrentFlowLinkBuilder = null;
			}
			else if (node.equals("state"))
			{
				mSiteBuilder.leaveState();
			}
			else if (node.equals("group"))
			{
				mSiteBuilder.leaveGroup();
			}
			else if (node.equals("globalvar"))
			{
				String[]	defaults = null;
				if (mCurrentGlobalVarDefaults.size() > 0)
				{
					defaults = new String[mCurrentGlobalVarDefaults.size()];
					defaults = mCurrentGlobalVarDefaults.toArray(defaults);
				}
				mSiteBuilder.addGlobalVar(mCurrentGlobalVar, defaults);
				mCurrentGlobalVar = null;
				mCurrentGlobalVarDefaults = null;
			}
			else if (node.equals("globalcookie"))
			{
				mSiteBuilder.addGlobalCookie(mCurrentGlobalCookie, mCurrentGlobalCookieDefault);
				mCurrentGlobalCookie = null;
				mCurrentGlobalCookieDefault = null;
			}
		}
	}
}
