/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineClassLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.instrument.ElementDetector;
import com.uwyn.rife.instrument.ClassBytesProvider;
import com.uwyn.rife.instrument.exceptions.ClassBytesNotFoundException;
import com.uwyn.rife.resources.ModificationTimeClasspath;
import com.uwyn.rife.site.instrument.ConstrainedDetector;
import com.uwyn.rife.tools.ClassBytesLoader;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.InstrumentationUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;

public class EngineClassLoader extends URLClassLoader implements ClassBytesProvider
{
	public final static String	DEFAULT_IMPLEMENTATIONS_PATH = "implementations/";
	public final static String	META_DATA_SUFFIX = "MetaData";
	
	private static HashMap<String, Long>	sModificationTimes = new HashMap<String, Long>();
	
	private static List<String>	sRifeWebappPath = null;
	private static boolean		sRifeWebappPathProcessed = false;
	
	private Set<String>			mModifiedClasses = null;
	private boolean				mIsParentEngineclassloader = false;
	private EngineClassLoader	mChild = null;
	private ClassLoader			mInitiating = null;
	
	private Method	mFindLoadedClassMethod = null;
	
	private ClassBytesLoader	mByteLoader = null;
	
	private ElementDetector		mElementDetector = null;
	private ConstrainedDetector	mConstrainedDetector = null;
	
	private Class mMetaDataInstrumenterClass = null;
	private Method mMetaDataInstrumenterMethod = null;
	
	private Class	mLazyLoadClass = null;
	private Method	mLazyLoadMethod = null;
	
	private Object	mEngineContinuationConfigInstrument = null;

	private Class	mContinuationConfigInstrumentClass = null;
	
	private Class	mResumerClass = null;
	private Method	mResumerMethod = null;
	
	private String	mClassLoaderName = "RIFE:EngineClassLoader";
	
	public EngineClassLoader(ClassLoader parent)
	{
		super(new URL[0], parent);

		// check for the presence of Terracotta by loading its ClassProcessorHelper class
		try
		{
			Class classprocessorhelper_class = Class.forName("com.tc.object.bytecode.hook.impl.ClassProcessorHelper");
			Class namedclassloader_class = Class.forName("com.tc.object.loaders.NamedClassLoader");
			if (classprocessorhelper_class != null &&
				namedclassloader_class != null)
			{
				if (namedclassloader_class.isAssignableFrom(parent.getClass()))
				{
					try
					{
						Method getclassloadername_method = namedclassloader_class.getDeclaredMethod("__tc_getClassLoaderName", new Class[0]);
						mClassLoaderName = "Rife:Engine:" + getclassloadername_method.invoke(parent, new Object[0]);
						
						Method method = classprocessorhelper_class.getDeclaredMethod("registerGlobalLoader", new Class[] {namedclassloader_class});
						method.invoke(null, new Object[] {this});
					}
					catch (Exception e)
					{
						throw new RuntimeException("Unable to register the engine classloader '"+mClassLoaderName+"' with Terracotta.", e);
					}
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			// this is OK, Terracotta is not present in the classpath
		}
		
		mIsParentEngineclassloader = parent instanceof EngineClassLoader;
		if (mIsParentEngineclassloader)
		{
			mInitiating = ((EngineClassLoader)parent).mInitiating;
		}
		else
		{
			mInitiating = parent;
		}
		
		mByteLoader = new ClassBytesLoader(this);
		mByteLoader.setupSunByteLoading();
		if (mByteLoader.isUsingSunByteLoading())
		{
			addClassPathURLs();
		}
		
		mElementDetector = new ElementDetector(this);
		
		mConstrainedDetector = new ConstrainedDetector(mByteLoader);
	}
	
	public String __tc_getClassLoaderName() {
		return mClassLoaderName;
	}
	
	public void __tc_setClassLoaderName(String name) {
		throw new UnsupportedOperationException("class loader name can not be modified for loader with name: " + mClassLoaderName);
	}

	private void addClassPathURLs()
	{
		try
		{
			Class classpathutils_class = loadClass("com.uwyn.rife.tools.ClasspathUtils");
			Method classpathutils_method = classpathutils_class.getDeclaredMethod("getClassPathAsList", Class.class);
			List<String> classpath = (List<String>)classpathutils_method.invoke(null, getClass());
			if (classpath != null)
			{
				for (String classpath_element : classpath)
				{
					addURL(new URL("file", null, classpath_element));
				}
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private boolean containsModifiedClass(String classname)
	{
		if (null == mModifiedClasses)
		{
			return false;
		}
		
		if (mModifiedClasses.contains(classname))
		{
			return true;
		}
		
		int	innerclass_index = classname.indexOf("$");
		if (innerclass_index != -1)
		{
			String containing_classname = classname.substring(0, innerclass_index);
			if (mModifiedClasses.contains(containing_classname))
			{
				return true;
			}
		}
		
		return false;
	}
	
	protected Class loadClass(String classname, boolean resolve)
	throws ClassNotFoundException
	{
		return loadClass(classname, resolve, false);
	}

	synchronized public void markClassAsModified(String classname)
	{
		if (null == mModifiedClasses)
		{
			mModifiedClasses = new HashSet<String>();
		}

		if (mChild != null &&
			mModifiedClasses.contains(classname))
		{
			mChild.markClassAsModified(classname);
		}
		else
		{
			if (null == mChild)
			{
				mChild = new EngineClassLoader(this);
			}
			mModifiedClasses.add(classname);
		}
	}

	public Class loadClass(String classname, boolean resolve, boolean loadElement)
	throws ClassNotFoundException
	{
		assert classname != null;

//		System.out.println(">>> "+classname);

		// get the auto reload preferences
		boolean auto_reload = doAutoReload();

		// see if this classloader has cached the class with the provided name
		Class c = null;
		if (auto_reload &&
			containsModifiedClass(classname))
		{
			return mChild.loadClass(classname, resolve, loadElement);
		}
		if (null == c)
		{
			c = findLoadedClass(classname);
		}

		// if an already loaded version was found, check whether it's outdated or not
		if (c != null)
		{
			// if an already loaded version was found, check whether it's outdated or not
			// this can only be Element classes since those are the only ones that are
			// handled by this classloader
			if (auto_reload)
			{
				// if the element was modified, don't use the cached class
				// otherwise, just take the previous element class
				if (loadElement && isModified(classname))
				{
					synchronized (this)
					{
						markClassAsModified(classname);

						Class klass = mChild.loadClass(classname, resolve, loadElement);
						clearSiteCaches(classname);
						return klass;
					}
				}
			}
		}
		// try to obtain the class in another way
		else
		{
			// try to load the core system classes first, these are sure to be
			// handled by the system classloader
			if (null == c)
			{
				if (classname.startsWith("java."))
				{
					try
					{
						c = findSystemClass(classname);
						if (c != null)
						{
							if (resolve)
							{
								resolveClass(c);
							}

//							System.out.println("SYSTEM defined "+classname);

							return c;
						}
					}
					catch (ClassNotFoundException e)
					{
						c = null;
					}
				}
			}

			ClassLoader parent = getParent();
			classname = classname.intern();

			// Get classes already loaded by the servlet container's classloader,
			// to avoid loading them again. These will be classes loaded by other
			// filters and listeners inside the same web application, initialized
			// before RIFE. For RIFE these should only be the
			// EngineClassLoader-related classed and those of the continuations engine
			try
			{
				if (null == mFindLoadedClassMethod)
				{
					mFindLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] {java.lang.String.class});
					mFindLoadedClassMethod.setAccessible(true);
				}

				c = (Class)mFindLoadedClassMethod.invoke(mInitiating, (Object[])new String[] {classname});
//				if (c != null)
//				{
//					System.out.println("PARENT defined "+classname+" (already loaded)");
//				}
			}
			catch (Throwable e)
			{
				c = null;
			}

			// load the class through the initial EngineClassLoader, unless it
			// has been modified
			if (mIsParentEngineclassloader &&
				!((EngineClassLoader)parent).containsModifiedClass(classname))
			{
//				if (c != null)
//				{
//					System.out.println("PARENT loading "+classname);
//				}
				return ((EngineClassLoader)parent).loadClass(classname, resolve, loadElement);
			}

			// try to obtain the class in a RIFE-specific way
			if (null == c &&
				!classname.startsWith("com.uwyn.rife.asm.") &&
				!classname.startsWith("com.tc.object.loaders."))
			{
				boolean	webapp_class = false;
				boolean	rife_class = false;
				if (classname == "com.uwyn.rife.engine.EngineClassLoaderRifeWebappPath")
				{
					webapp_class = true;
				}
				else
				{
					// This is not a problem, if several simultaneous threads
					// will access it, it will just be retrieved several times.
					// The calls will be cached for all subsequent threads
					// though.
					if (!sRifeWebappPathProcessed)
					{
						try
						{
							Class webapp_path_class = loadClass("com.uwyn.rife.engine.EngineClassLoaderRifeWebappPath");
							Field  webapp_path_field = webapp_path_class.getField("RIFE_WEBAPP_PATH");
							sRifeWebappPath = (ArrayList<String>)webapp_path_field.get(null);
							sRifeWebappPathProcessed = true;
						}
						catch (Throwable e)
						{
							throw new ClassNotFoundException(classname, e);
						}
					}

					// check of the class is part of the web application, or if it should be loaded through
					// a parent classloader
					if (classname.startsWith("com.uwyn.rife."))	// classes are loaded by the EngineClassLoader
					{
						webapp_class = true;
						rife_class = true;
					}
					else
					{
						webapp_class = isWebAppClass(classname);
					}

					// only handle undefined classes, or check existing ones if
					// auto-reloading has been activated
					if (null == c || auto_reload)
					{
						String packagename = null;
						int packagename_index = classname.lastIndexOf('.');
						if (packagename_index != -1)
						{
							packagename = classname.substring(0, packagename_index);
						}

						// first try to find the compiled bytecode, take inner classes
						// into account
						byte raw_bytes[] = null;
						int innerclass_index = classname.indexOf("$");
						try
						{
							if (innerclass_index > -1)
							{
								raw_bytes = getClassBytes(classname.substring(0, innerclass_index), auto_reload, loadElement);
							}
							else
							{
								raw_bytes = getClassBytes(classname, auto_reload, loadElement);
							}
						}
						catch (ClassNotFoundException e)
						{
							raw_bytes = null;
						}

						if (raw_bytes != null)
						{
							// check if the bytes corresponds to an element class
							if (!isElement(classname, raw_bytes, auto_reload))
							{
								// If it's not an element and the class hasn't been
								// defined yet, define it. If it has been found in the
								// parent, use the already existing instance.
								// Auto-reloading isn't supported for non-elements
								if (null == c)
								{
									if (!webapp_class)
									{
										try
										{
//											System.out.println("PARENT defined "+classname+" (not webapp class)");
											if (!mIsParentEngineclassloader)
											{
												c = parent.loadClass(classname);
											}
										}
										catch (ClassNotFoundException e)
										{
											c = null;
										}
									}
									else
									{
//										System.out.println("ENGINE defined "+classname);

										if (packagename != null)
										{
											Package pkg = getPackage(packagename);
											if (null == pkg)
											{
												definePackage(packagename, null, null, null, null, null, null, null);
											}
										}

										// use the interned classname to get a synchronization lock monitor
										// that is specific for the current class that is loaded and
										// that will not lock up the classloading of all other classes
										// by for instance synchronizing on this classloader
										synchronized (classname)
										{
											// make sure that the class has not been defined in the
											// meantime, otherwise defining it again will trigger an
											// exception;
											// reuse the existing class if it has already been defined
											c = findLoadedClass(classname);
											if (null == c)
											{
												// if we are working with an inner class, get its
												// actual raw bytes, since we used the containing
												// class's bytes to determine if it's an element
												if (innerclass_index > -1)
												{
													raw_bytes = getClassBytes(classname, auto_reload, loadElement);
												}

												if (!rife_class)
												{
													// handle meta data sibling classes for non framework classes
													if (null == mMetaDataInstrumenterClass)
													{
														mMetaDataInstrumenterClass = loadClass("com.uwyn.rife.site.instrument.MetaDataInstrumenter");
													}

													try
													{
														if (null == mMetaDataInstrumenterMethod)
														{
															mMetaDataInstrumenterMethod = mMetaDataInstrumenterClass.getDeclaredMethod("instrument", new Class[] {ClassLoader.class, String.class, byte[].class});
															mMetaDataInstrumenterMethod.setAccessible(true);
														}
														raw_bytes = (byte[])mMetaDataInstrumenterMethod.invoke(null, new Object[] {this, classname, raw_bytes});
													}
													catch (Exception e)
													{
														throw new ClassNotFoundException(classname, e);
													}

													try
													{
														if (isConstrained(classname, raw_bytes))
														{
															// handle lazy loading for constrained non framework classes
															if (null == mLazyLoadClass)
															{
																mLazyLoadClass = loadClass("com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadAccessorsBytecodeTransformer");
															}

															if (null == mLazyLoadMethod)
															{
																mLazyLoadMethod = mLazyLoadClass.getDeclaredMethod("addLazyLoadToBytes", new Class[] {byte[].class});
																mLazyLoadMethod.setAccessible(true);
															}
															raw_bytes = (byte[])mLazyLoadMethod.invoke(null, new Object[] {raw_bytes});
														}
													}
													catch (Exception e)
													{
														throw new ClassNotFoundException(classname, e);
													}
												}
												
												// define the class from its raw bytes
												InstrumentationUtils.dumpClassBytes("adapted", classname, raw_bytes);
												c = defineClass(classname, raw_bytes, 0, raw_bytes.length);

												if (null == c)
												{
													throw new ClassNotFoundException(classname);
												}
											}
										}
									}
								}
							}
							else
							{
//								if (null == c)
//								{
//									System.out.println("ENGINE defined "+classname);
//								}
//								else
//								{
//									System.out.println("ENGINE redefined "+classname);
//								}

								// if we are working with an inner class, get its
								// actual raw bytes, since we used the containing
								// class's bytes to determine if it's an element
								if (innerclass_index > -1)
								{
									raw_bytes = getClassBytes(classname, auto_reload, loadElement);
								}

								if (auto_reload)
								{
									// register the modification time of the source file
									long	modification_time = getClassModificationTime(classname);
									sModificationTimes.put(classname, new Long(modification_time));
								}

								// get an instance of the engine continuations config
								if (null == mEngineContinuationConfigInstrument)
								{
									Class config_class = loadClass("com.uwyn.rife.engine.EngineContinuationConfigInstrument");
									try
									{
										Constructor config_constructor = config_class.getConstructor(new Class[0]);
										config_constructor.setAccessible(true);
										mEngineContinuationConfigInstrument = config_constructor.newInstance(new Object[0]);
									}
									catch (Exception e)
									{
										throw new ClassNotFoundException(classname, e);
									}
								}
								
								// get the continuation config instrument class
								if (null == mContinuationConfigInstrumentClass)
								{
									mContinuationConfigInstrumentClass = loadClass("com.uwyn.rife.continuations.ContinuationConfigInstrument");
								}
								
								// transform bytes on-the-fly into resumable bytes
								byte[] resumable_bytes;
								if (null == mResumerClass)
								{
									mResumerClass = loadClass("com.uwyn.rife.continuations.instrument.ContinuationsBytecodeTransformer");
								}
								try
								{
									if (null == mResumerMethod)
									{
										mResumerMethod = mResumerClass.getDeclaredMethod("transformIntoResumableBytes", new Class[] {mContinuationConfigInstrumentClass, byte[].class, String.class});
										mResumerMethod.setAccessible(true);
									}
									resumable_bytes = (byte[])mResumerMethod.invoke(null, new Object[] {mEngineContinuationConfigInstrument, raw_bytes, classname});
								}
								catch (Exception e)
								{
									throw new ClassNotFoundException(classname, e);
								}

								// define a package if needed
								if (packagename != null)
								{
									Package pkg = getPackage(packagename);
									if (null == pkg)
									{
										definePackage(packagename, null, null, null, null, null, null, null);
									}
								}

								// intern the classname to get a synchronization lock monitor
								// that is specific for the current class that is loaded and
								// that will not lock up the classloading of all other classes
								// by for instance synchronizing on this classloader
								classname = classname.intern();
								synchronized (classname)
								{
									// make sure that the class has not been defined in the
									// meantime, otherwise defining it again will trigger an
									// exception;
									// reuse the existing class if it has already been defined
									c = findLoadedClass(classname);
									if (null == c)
									{
										// get the raw bytes of the class and define it for this classloader
										if (null == resumable_bytes)
										{
											InstrumentationUtils.dumpClassBytes("adapted", classname, raw_bytes);
											c = defineClass(classname, raw_bytes, 0, raw_bytes.length);
										}
										else
										{
											InstrumentationUtils.dumpClassBytes("adapted", classname, resumable_bytes);
											c = defineClass(classname, resumable_bytes, 0, resumable_bytes.length);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// the ultimate fallback class loading
		if (null == c &&
			!mIsParentEngineclassloader)
		{
			try
			{
				c = getParent().loadClass(classname);
//				if (c != null)
//				{
//					System.out.println("FALLBACK PARENT defined "+classname+" (ultimate falback)");
//				}
			}
			catch (ClassNotFoundException e)
			{
				c = null;
			}
		}
		if (null == c)
		{
			try
			{
				c = findSystemClass(classname);
//				System.out.println("FALLBACK SYSTEM defined "+classname);
			}
			catch (ClassNotFoundException e)
			{
//				System.out.println("!!!!!! not found "+classname);
				throw e;
			}
		}

		// resolve the class if it's needed
		if (resolve)
		{
			resolveClass(c);
		}

		assert c != null;

		return c;
	}

	private void clearSiteCaches(String classname) throws ClassNotFoundException
	{
		try
		{
			Class site_class = loadClass("com.uwyn.rife.engine.Site");
			Method repinstance_method = site_class.getDeclaredMethod("getRepInstance", new Class[0]);
			Method clearcaches_method = site_class.getDeclaredMethod("clearCaches", new Class[0]);
			Object site = repinstance_method.invoke(null, new Object[0]);
			if (site != null)
			{
				clearcaches_method.invoke(site, new Object[0]);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ClassNotFoundException(classname, e);
		}
	}

	private boolean isWebAppClass(String classname)
	{
		try
		{
			String class_file = classname.replace('.', '/') + ".class";
			URL resource = getResource(class_file);
			if (resource != null)
			{
//				System.out.println("resource_path : " + resource.toExternalForm());
				String resource_path = resource.getPath();
				String resource_protocol = resource.getProtocol();
				// if the resource lies in a WEB-INF dir, it's part
				// of a webapp
				if (resource_path.indexOf("WEB-INF") != -1)
				{
					return true;
				}
				// handle Orion's classloader resource protocol
				else if (resource_protocol != null &&
						 resource_protocol.equals("classloader"))
				{
					return true;
				}
				// if a specific rife webapp path collection has been
				// given, check against each entry
				else if (sRifeWebappPath != null)
				{
					if (resource_path.startsWith("jar:file:"))
					{
						resource_path = resource_path.substring("jar:file:".length());
					}
					if (resource_path.startsWith("file:"))
					{
						resource_path = resource_path.substring("file:".length());
					}
					int exclamation_index = resource_path.indexOf("!");
					if (exclamation_index != -1)
					{
						resource_path = resource_path.substring(0, exclamation_index);
					}
					resource_path = URLDecoder.decode(resource_path, "ISO-8859-1");
					File resource_file = new File(resource_path);
					if (resource_file.exists())
					{
						resource_path = resource_file.getCanonicalPath();
						for (String path : sRifeWebappPath)
						{
							if (resource_path.startsWith(path))
							{
								return true;
							}
						}
					}
				}
			}
		}
		catch (Throwable e)
		{
			return false;
		}

		return false;
	}

	private boolean hasRepClass()
	{
		if (getParent() instanceof EngineClassLoader)
		{
			return ((EngineClassLoader)getParent()).hasRepClass();
		}

		try
		{
			return findLoadedClass("com.uwyn.rife.rep.Rep") != null;
		}
		// fix to make it work with the IBM jvm
		catch (ClassCircularityError e)
		{
			return true;
		}
	}

	private boolean doAutoReload()
	{
		Object value = System.getProperties().get("ELEMENT_AUTO_RELOAD");

		if (null == value)
		{
			return true;
		}

		if (value instanceof String)
		{
			String string_value = (String)value;
			if (string_value.equals("1") ||
				string_value.equalsIgnoreCase("t") ||
				string_value.equalsIgnoreCase("true") ||
				string_value.equalsIgnoreCase("y") ||
				string_value.equalsIgnoreCase("yes") ||
				string_value.equalsIgnoreCase("on"))
			{
				return true;
			}
		}

		return false;
	}

	public static String constructSourcePath(String classname)
	{
		String	source_location = null;
		int		innerclass_index = classname.indexOf("$");
		if (innerclass_index != -1)
		{
			String containing_classname = classname.substring(0, innerclass_index);
			source_location = containing_classname.replace('.', '/')+".java";
		}
		else
		{
			source_location = classname.replace('.', '/')+".java";
		}

		return source_location;
	}
	
	public byte[] getClassBytes(String className, boolean reloadAutomatically) throws ClassNotFoundException
	{
		return getClassBytes(className, reloadAutomatically, false);
	}

	public byte[] getClassBytes(String className, boolean doAutoReload, boolean performCompilation)
	throws ClassNotFoundException
	{
		byte[]	raw_bytes = null;
		String	class_filename = className.replace('.', '/')+".class";
		URL		class_resource = getResource(class_filename);
		try
		{
			// if it couldn't be found or if it's outdated when auto-reload is
			// activated, compile the element
			if ((null == class_resource ||
				(doAutoReload && performCompilation && isModified(className))) &&
				(-1 == className.indexOf('$') || !className.endsWith("BeanInfo"))) /// don't recompile for BeanInfo classes
			{
				try
				{
					if (!performCompilation &&
						!hasRepClass())
					{
						throw new ClassNotFoundException(className);
					}
					else
					{
						String	source_location = constructSourcePath(className);
						File	class_file = compileClass(className, source_location);
						raw_bytes = FileUtils.readBytes(class_file);
					}
				}
				catch (Throwable e)
				{
					Class elementcompilation_failed_class = loadClass("com.uwyn.rife.engine.exceptions.ElementCompilationFailedException");
					if (e.getClass() == elementcompilation_failed_class)
					{
						throw (RuntimeException)e;
					}
					if (e instanceof ClassNotFoundException)
					{
						throw (ClassNotFoundException)e;
					}

					throw new ClassNotFoundException(className, e);
				}
			}
			// otherwise just get the bytes of the classfile
			else
			{
				raw_bytes = mByteLoader.getClassBytes(class_filename, class_resource);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			if (e instanceof RuntimeException)
			{
				throw (RuntimeException)e;
			}

			throw new ClassNotFoundException("Error while reading the contents of the element class file '"+className+"'.", e);
		}

		InstrumentationUtils.dumpClassBytes("initial", className, raw_bytes);
		
		return raw_bytes;
	}

	File compileClass(String classname, String sourceLocation)
	throws ClassNotFoundException
	{
		assert classname != null;
		assert sourceLocation != null;
		assert sourceLocation.endsWith(".java");

		// try to find the script
		URL source_resource = getSourceResource(sourceLocation, true);
		if (null == source_resource)
		{
			throw new ClassNotFoundException("Couldn't find the source file '"+sourceLocation+"'.");
		}

		// get the package and the short classname of the element
		int		classname_index = classname.lastIndexOf(".");
		String	element_package = null;
		String	element_classname = null;
		if (classname_index != -1)
		{
			element_package = classname.substring(0, classname_index);
			element_classname = classname.substring(classname_index+1);
		}
		else
		{
			element_classname = classname;
		}

		// setup everything to perform the conversion of the element to java sources
		// and to compile it into a java class
		Object generation_path_object = null;
		try
		{
			Class tests_class = loadClass("com.uwyn.rife.config.RifeConfig$Engine");
			Method method = tests_class.getMethod("getElementGenerationPath", (Class[])null);
			generation_path_object = method.invoke(null, (Object[])null);
		}
		catch (Throwable e)
		{
			throw new ClassNotFoundException("Couldn't obtain the element bytecode generation path.", e);
		}

		String	generation_path = ((String)generation_path_object)+File.separatorChar;
		String	package_dir = "";
		if (element_package != null)
		{
			package_dir = generation_path+element_package.replace('.', File.separatorChar)+File.separator;
		}
		else
		{
			package_dir = generation_path;
		}
		String	filename_java = null;
		try
		{
			filename_java = URLDecoder.decode(source_resource.getPath(), "ISO-8859-1");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ClassNotFoundException("Couldn't decode the resource path '"+source_resource.getPath()+"'.", e);
		}
		String	filename_class = package_dir+element_classname+".class";
		File	file_packagedir = new File(package_dir);
		File	file_class = new File(filename_class);

		// prepare the package directory
		if (!file_packagedir.exists())
		{
			if (!file_packagedir.mkdirs())
			{
				throw new ClassNotFoundException("Couldn't create the package directory : '"+package_dir+"'.");
			}
		}
		else if (!file_packagedir.isDirectory())
		{
			throw new ClassNotFoundException("The package directory '"+package_dir+"' exists but is not a directory.");
		}
		else if (!file_packagedir.canWrite())
		{
			throw new ClassNotFoundException("The package directory '"+package_dir+"' is not writable.");
		}

		// check if the class wasn't already compiled due to other classes
		// in the same source file
		boolean	is_already_compiled = false;
		long	source_modification_time = -1L;
		if (file_class.exists())
		{
			long	class_modification_time = file_class.lastModified();
			source_modification_time = getSourceModificationTime(source_resource);
			if (class_modification_time >= source_modification_time)
			{
				is_already_compiled = true;
			}
		}

		if (!is_already_compiled)
		{
			try
			{
				Class classloader_classpath_class = loadClass("com.uwyn.rife.engine.EngineClassLoaderClasspath");
				Field  classloader_classpath_field = classloader_classpath_class.getField("CLASSPATH");
				String classloader_classpath = (String)classloader_classpath_field.get(null);

				Class tests_class = loadClass("com.uwyn.rife.tools.CompilationUtils");
				Method method = tests_class.getMethod("compile", new Class[] {String.class, File.class, String.class, String.class});
				generation_path_object = method.invoke(null, new Object[] {filename_java, file_class, generation_path, classloader_classpath});
			}
			catch (InvocationTargetException e)
			{
				Class compilation_failed_class = loadClass("com.uwyn.rife.tools.exceptions.CompilationFailedException");

				Throwable target_exception = e.getTargetException();
				if (compilation_failed_class == target_exception.getClass())
				{
					String sourcefilename = null;
					String errors = null;
					Throwable cause = null;

					try
					{
						Method sourcefilename_method = compilation_failed_class.getMethod("getSourceFilename", (Class[])null);
						sourcefilename = (String)sourcefilename_method.invoke(target_exception, (Object[])null);
						Method errors_method = compilation_failed_class.getMethod("getErrors", (Class[])null);
						errors = (String)errors_method.invoke(target_exception, (Object[])null);
						Method cause_method = compilation_failed_class.getMethod("getCause", (Class[])null);
						cause = (Throwable)cause_method.invoke(target_exception, (Object[])null);
					}
					catch (Throwable e2)
					{
						throw new ClassNotFoundException("Unexpected error while compiling the element source '"+filename_java+"'+.", e);
					}

					Object elementcompilation_failed_instance = null;

					try
					{
						Class elementcompilation_failed_class = loadClass("com.uwyn.rife.engine.exceptions.ElementCompilationFailedException");
						Constructor elementcompilation_failed_constructor = elementcompilation_failed_class.getConstructor(new Class[] {String.class, String.class, Throwable.class});
						elementcompilation_failed_instance = elementcompilation_failed_constructor.newInstance(new Object[] {sourcefilename, errors, cause});
					}
					catch (Throwable e2)
					{
						throw new ClassNotFoundException("Unexpected error while compiling the element source '"+filename_java+"'+.", e);
					}

					throw (RuntimeException)elementcompilation_failed_instance;
				}
				else
				{
					throw new ClassNotFoundException("Unexpected error while compiling the element source '"+filename_java+"'.", e);
				}
			}
			catch (Throwable e)
			{
				throw new ClassNotFoundException("Unexpected error while compiling the element source '"+filename_java+"'.", e);
			}
		}
		
		assert file_class != null;
		assert file_class.exists();
		assert file_class.canRead();
		
		return file_class;
	}

	private URL getSourceResource(String sourceLocation, boolean getElement)
	{
		URL	source_resource = getResource(sourceLocation);
		if (null == source_resource &&
		   getElement)
		{
			source_resource = getResource(DEFAULT_IMPLEMENTATIONS_PATH+sourceLocation);
		}
		return source_resource;
	}
	
	private boolean isElement(String internedClassname, byte[] bytes, boolean doAutoReload)
	throws ClassNotFoundException
	{
		return mElementDetector.detect(internedClassname, bytes, doAutoReload);
	}
	
	private boolean isConstrained(String internedClassname, byte[] bytes)
	throws ClassBytesNotFoundException
	{
		return mConstrainedDetector.isConstrained(internedClassname, bytes);
	}
	
	private long getClassModificationTime(String classname)
	{
		return getSourceModificationTime(getSourceResource(constructSourcePath(classname), true));
	}
	
	public static long getSourceModificationTime(URL sourceResource)
	{
		if (null == sourceResource)
		{
			return -1;
		}
		
		try
		{
			return ModificationTimeClasspath.getModificationTime(sourceResource);
		}
		catch (Throwable e)
		{
			return -1;
		}
	}
	
	private boolean isModified(String classname)
	{
		Long last_modification_time = sModificationTimes.get(classname);
		if (null == last_modification_time)
		{
			return false;
		}
		
		long current_modification_time = getClassModificationTime(classname);
		if (current_modification_time <= last_modification_time.longValue())
		{
			return false;
		}

		return true;
	}
}