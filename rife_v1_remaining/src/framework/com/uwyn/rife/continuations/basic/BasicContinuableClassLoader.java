/*
 * Copyright 2001-2008 Patrick Lightbody and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BasicContinuableClassLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.basic;

import java.lang.reflect.Method;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.continuations.instrument.ContinuableDetector;
import com.uwyn.rife.continuations.instrument.ContinuationsAgent;
import com.uwyn.rife.continuations.instrument.ContinuationsBytecodeTransformer;
import com.uwyn.rife.instrument.ClassBytesProvider;
import com.uwyn.rife.tools.ClassBytesLoader;
import com.uwyn.rife.tools.TerracottaUtils;

/**
 * Classloader implementation that will transform bytecode for classes that
 * should receive the continuations functionalities.
 * <p>Note that this is a basic classloader implementation. For your own
 * application you should probably create your own or at least read over
 * the source code of this one. It's even better to not use a custom
 * classloader and only rely on the {@link ContinuationsAgent}.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class BasicContinuableClassLoader extends ClassLoader implements ClassBytesProvider
{
	private ContinuationConfigInstrument	mConfig;
	private ClassBytesLoader				mBytesLoader;
	private ContinuableDetector				mContinuableDetector;
	private Method							mMethodFindloadedclass;
	
	public String __tc_getClassLoaderName() {
		return "RIFE:BasicContinuableClassLoader";
	}

	/**
	 * Creates a new classloader instance with the context classloader as
	 * the parent classloader.
	 *
	 * @param config the instance of the instrumentation configuration that
	 * will be used for the transformation
	 * @since 1.6
	 */
	public BasicContinuableClassLoader(ContinuationConfigInstrument config)
	{
		this(Thread.currentThread().getContextClassLoader(), config);
		
		if (TerracottaUtils.isTcPresent()) {
			try {
				Class classprocessor_helper_class = Class.forName("com.tc.object.bytecode.hook.impl.ClassProcessorHelper");
				Class namedclassloader_class = Class.forName("com.tc.object.loaders.NamedClassLoader");
				Method method = classprocessor_helper_class.getDeclaredMethod("registerGlobalLoader", new Class[] {namedclassloader_class});
				method.invoke(null, new Object[] {this});
			} catch (Exception e) {
				throw new RuntimeException("Unable to register the engine classloader '"+__tc_getClassLoaderName()+"' with Terracotta.", e);
			}
		}
	}
	
	/**
	 * Creates a new classloader instance.
	 *
	 * @param parent the parent classloader
	 * @param config the instance of the instrumentation configuration that
	 * will be used for the transformation
	 * @since 1.6
	 */
    public BasicContinuableClassLoader(ClassLoader parent, ContinuationConfigInstrument config)
	{
        super(parent);
		
		mConfig = config;
		mBytesLoader = new ClassBytesLoader(getParent());
		mContinuableDetector = new ContinuableDetector(mConfig, this);
		try
		{
			mMethodFindloadedclass = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] {String.class});
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		mMethodFindloadedclass.setAccessible(true);
    }
	
	public byte[] getClassBytes(String className, boolean reloadAutomatically) throws ClassNotFoundException
	{                                                                
		return mBytesLoader.getClassBytes(className.replace('.', '/') + ".class");
	}
	
    public Class loadClass(String name) throws ClassNotFoundException
	{
		// disable this classloader and delegate to the parent if the continuations
		// agent is active
		if (Boolean.getBoolean(ContinuationsAgent.AGENT_ACTIVE_PROPERTY))
		{
			return getParent().loadClass(name);
		}
		
		// check if the class wasn't already loaded by the parent classloader and in
		// that case, don't instrument it, yes I know this is ugly and a hack
		Class parentclassloader_class = null;
		try
		{
			parentclassloader_class = (Class)mMethodFindloadedclass.invoke(getParent(), new Object[] {name});
			if (parentclassloader_class != null)
			{
				return parentclassloader_class;
			}
		}
		catch (Exception e)
		{
			throw new ClassNotFoundException(name, e);
		}
		
		// if the class couldn't be obtained from the parent classloader and it
		// wasn't previously loaded by this one, perform the instrumentation
		synchronized (name.intern())
		{
			if (null == findLoadedClass(name))
			{
				byte[] bytes = getClassBytes(name, false);
				if (bytes == null)
				{
					return super.loadClass(name);
				}
			
				if (mContinuableDetector.detect(bytes, false))
				{
					byte[] resume_bytes = ContinuationsBytecodeTransformer.transformIntoResumableBytes(mConfig, bytes, name);
					
					if (resume_bytes != null)
					{
						bytes = resume_bytes;
					}
					
					return defineClass(name, bytes, 0, bytes.length);
				}
			}
		}	
		
		return super.loadClass(name);
	}
}
