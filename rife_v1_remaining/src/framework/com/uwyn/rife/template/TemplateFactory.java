/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.datastructures.EnumClass;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.site.ValidationBuilder;
import com.uwyn.rife.template.exceptions.InvalidBlockFilterException;
import com.uwyn.rife.template.exceptions.InvalidValueFilterException;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.template.exceptions.TemplateNotFoundException;
import com.uwyn.rife.tools.Localization;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TemplateFactory extends EnumClass<String>
{
	private static final Parser.Config	CONFIG_COMPACT_STANDARD = new Parser.Config("[!", "]", "[!/", "/]", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C");

	public static final Parser.Config	CONFIG_INVISIBLE_XML = new Parser.Config("<!--", "-->", "<!--/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C");
	public static final Parser.Config	CONFIG_XML_TAGS = new Parser.Config("<r:", ">", "</r:", "/>", new Parser.MandatoryConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "name=\""), new Parser.MandatoryConfigPart(Parser.PartType.STRING_DELIMITER_END, "\""), "v", "b", "bv", "ba", "i", "c");
	public static final Parser.Config	CONFIG_VELOCITY = new Parser.Config("${", "}", "${/", "/}", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "v", "b", "bv", "ba", "i", "c");
	public static final Parser.Config	CONFIG_INVISIBLE_TXT = new Parser.Config("<!", ">", "<!/", "/>", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C");
	public static final Parser.Config	CONFIG_INVISIBLE_SQL = new Parser.Config("/*", "*/", "/*-", "-*/", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C");
	public static final Parser.Config	CONFIG_INVISIBLE_JAVA = new Parser.Config("/*", "*/", "/*-", "-*/", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C");

	public static final Parser.Config[]	CONFIGS_XML = new Parser.Config[] {CONFIG_XML_TAGS, CONFIG_INVISIBLE_XML, CONFIG_VELOCITY, CONFIG_COMPACT_STANDARD};
	public static final Parser.Config[]	CONFIGS_TXT = new Parser.Config[] {CONFIG_INVISIBLE_TXT, CONFIG_COMPACT_STANDARD};
	public static final Parser.Config[]	CONFIGS_SQL = new Parser.Config[] {CONFIG_INVISIBLE_SQL, CONFIG_COMPACT_STANDARD};
	public static final Parser.Config[]	CONFIGS_JAVA = new Parser.Config[] {CONFIG_INVISIBLE_JAVA, CONFIG_COMPACT_STANDARD};

	public static final String		PREFIX_CONFIG = "CONFIG:";
	public static final String		PREFIX_L10N = "L10N:";
	public static final String		PREFIX_LANG = "LANG:";
	public static final String		PREFIX_OGNL_CONFIG = "OGNL:CONFIG:";
	public static final String		PREFIX_OGNL = "OGNL:";
	public static final String		PREFIX_MVEL_CONFIG = "MVEL:CONFIG:";
	public static final String		PREFIX_MVEL = "MVEL:";
	public static final String		PREFIX_GROOVY_CONFIG = "GROOVY:CONFIG:";
	public static final String		PREFIX_GROOVY = "GROOVY:";
	public static final String		PREFIX_JANINO_CONFIG = "JANINO:CONFIG:";
	public static final String		PREFIX_JANINO = "JANINO:";
	public static final String		PREFIX_RENDER = "RENDER:";

	public static final String		TAG_CONFIG = "^"+PREFIX_CONFIG+"\\s*(.*)\\s*$";
	public static final String		TAG_L10N = "^"+PREFIX_L10N+"\\s*([^:]*)(?::([^:]*))?\\s*$";
	public static final String		TAG_LANG = "(?s)^("+PREFIX_LANG+".*):\\s*(\\w*)\\s*$";
	public static final String		TAG_OGNL_CONFIG = "(?s)^("+PREFIX_OGNL_CONFIG+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_OGNL = "(?s)^("+PREFIX_OGNL+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_MVEL_CONFIG = "(?s)^("+PREFIX_MVEL_CONFIG+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_MVEL = "(?s)^("+PREFIX_MVEL+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_GROOVY_CONFIG = "(?s)^("+PREFIX_GROOVY_CONFIG+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_GROOVY = "(?s)^("+PREFIX_GROOVY+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_JANINO_CONFIG = "(?s)^("+PREFIX_JANINO_CONFIG+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_JANINO = "(?s)^("+PREFIX_JANINO+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String		TAG_RENDER = "^"+PREFIX_RENDER+"\\s*(.*?)\\s*(:[^:]*)?$";

	public static	TemplateFactory	HTML = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"html", CONFIGS_XML, "text/html", ".html",
		new String[]
		{
			ValidationBuilder.TAG_ERRORS,
			ValidationBuilder.TAG_ERRORMESSAGE,
			TAG_LANG,
			TAG_OGNL_CONFIG,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
		    TAG_MVEL,
		    TAG_GROOVY_CONFIG,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			TAG_JANINO
		},
		new String[]
		{
			ValidationBuilder.TAG_MARK,
			ValidationBuilder.TAG_ERRORS,
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerXhtml.getInstance(), EncoderHtmlSingleton.INSTANCE,
		null);
	
	public static	TemplateFactory	XHTML = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"xhtml", CONFIGS_XML, "text/html", ".xhtml",
		new String[]
		{
			ValidationBuilder.TAG_ERRORS,
			ValidationBuilder.TAG_ERRORMESSAGE,
			TAG_LANG,
			TAG_OGNL_CONFIG,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			TAG_JANINO
		},
		new String[]
		{
			ValidationBuilder.TAG_MARK,
			ValidationBuilder.TAG_ERRORS,
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerXhtml.getInstance(), EncoderHtmlSingleton.INSTANCE,
		null);
	
	public static	TemplateFactory	XML = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"xml", CONFIGS_XML, "application/xml", ".xml",
		new String[]
		{
			ValidationBuilder.TAG_ERRORS,
			ValidationBuilder.TAG_ERRORMESSAGE,
			TAG_LANG,
			TAG_OGNL_CONFIG,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			TAG_JANINO
		},
		new String[]
		{
			ValidationBuilder.TAG_MARK,
			ValidationBuilder.TAG_ERRORS,
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerXml.getInstance(), EncoderXmlSingleton.INSTANCE,
		null);
	
	public static	TemplateFactory	TXT = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"txt", CONFIGS_TXT, "text/plain", ".txt",
		new String[]
		{
			TAG_LANG,
			TAG_OGNL_CONFIG,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			TAG_JANINO
		},
		new String[]
		{
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerPlain.getInstance(), null,
		null);
	
	public static	TemplateFactory	SQL = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"sql", CONFIGS_SQL, "text/plain", ".sql",
		new String[]
		{
			TAG_LANG,
			TAG_OGNL_CONFIG,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			TAG_JANINO
		},
		new String[]
		{
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerPlain.getInstance(), EncoderHtmlSingleton.INSTANCE,
		null);
	
	public static	TemplateFactory	JAVA = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"java", CONFIGS_JAVA, "text/x-java-source", ".java",
		new String[]
		{
			TAG_LANG,
			TAG_OGNL_CONFIG,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			TAG_JANINO
		},
		new String[]
		{
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerPlain.getInstance(), null,
		null);


	public static  TemplateFactory	ENGINEHTML;
	public static  TemplateFactory	ENGINEXHTML;
	public static  TemplateFactory	ENGINEXML;
	public static  TemplateFactory	ENGINETXT;

	static
	{
		Class engine_types = null;
		try
		{
			engine_types = Class.forName("com.uwyn.rife.template.TemplateFactoryEngineTypes");
		}
		catch (ClassNotFoundException e)
		{
			engine_types = null;
		}

		if (engine_types != null)
		{
			TemplateFactory factory = null;
			try
			{
				factory = (TemplateFactory)engine_types.getField("ENGINEHTML").get(null);

			}
			catch (Throwable e)
			{
				factory = null;
			}

			ENGINEHTML = factory;

			try
			{
				factory = (TemplateFactory)engine_types.getField("ENGINEXHTML").get(null);
			}
			catch (Throwable e)
			{
				factory = null;
			}

			ENGINEXHTML = factory;

			try
			{
				factory = (TemplateFactory)engine_types.getField("ENGINEXML").get(null);
			}
			catch (Throwable e)
			{
				factory = null;
			}

			ENGINEXML = factory;

			try
			{
				factory = (TemplateFactory)engine_types.getField("ENGINETXT").get(null);
			}
			catch (Throwable e)
			{
				factory = null;
			}

			ENGINETXT = factory;
		}
		else
		{
			ENGINEHTML = null;
			ENGINEXHTML = null;
			ENGINEXML = null;
			ENGINETXT = null;
		}
	}

	private TemplateClassLoader	mLastClassloader = null;
	private Parser				mParser = null;
	private BeanHandler			mBeanHandler = null;
	private TemplateEncoder		mEncoder = null;
	private ResourceFinder		mResourceFinder = null;
	private TemplateInitializer	mInitializer = null;
	private String				mIdentifierUppercase = null;
	protected String			mDefaultContentType = null;

	public TemplateFactory(ResourceFinder resourceFinder, String identifier,
			Parser.Config[] configs, String defaultContentType,
			String extension, String[] blockFilters, String[] valueFilters,
			BeanHandler beanHandler, TemplateEncoder encoder,
			TemplateInitializer initializer)
	{
		super(TemplateFactory.class, identifier);

		Pattern[] block_filter_patterns = null;
		try
		{
			block_filter_patterns = compileFilters(blockFilters);
		}
		catch (PatternSyntaxException e)
		{
			throw new InvalidBlockFilterException(e.getPattern());
		}

		Pattern[] value_filter_patterns = null;
		try
		{
			value_filter_patterns = compileFilters(valueFilters);
		}
		catch (PatternSyntaxException e)
		{
			throw new InvalidValueFilterException(e.getPattern());
		}

		mIdentifierUppercase = identifier.toUpperCase();
		mParser = new Parser(this, identifier, configs, extension, block_filter_patterns, value_filter_patterns);
		mResourceFinder = resourceFinder;
		mBeanHandler = beanHandler;
		mEncoder = encoder;
		mInitializer = initializer;
		mDefaultContentType = defaultContentType;

		assert mParser != null;
	}

	private Pattern[] compileFilters(String[] filters)
	throws PatternSyntaxException
	{
		if (filters != null)
		{
			Pattern[] patterns = new Pattern[filters.length];

			for (int i = 0; i < filters.length; i++)
			{
				patterns[i] = Pattern.compile(filters[i]);
			}

			return patterns;
		}

		return null;
	}

	public TemplateFactory(String identifier, TemplateFactory base)
	{
		super(TemplateFactory.class, identifier);

		mIdentifierUppercase = identifier.toUpperCase();
		mParser = new Parser(this, identifier, base.getParser().getConfigs(), base.getParser().getExtension(), base.getParser().getBlockFilters(), base.getParser().getValueFilters());
		mResourceFinder = base.getResourceFinder();
		mBeanHandler = base.getBeanHandler();
		mEncoder = base.getEncoder();
		mInitializer = base.getInitializer();
		mDefaultContentType = base.getDefaultContentType();

		assert mParser != null;
	}

	public String getIdentifierUppercase()
	{
		return mIdentifierUppercase;
	}

	public String getDefaultContentType()
	{
		return mDefaultContentType;
	}

	public static Collection<String> getFactoryTypes()
	{
		return (Collection<String>)getIdentifiers(TemplateFactory.class);
	}

	public static TemplateFactory getFactory(String identifier)
	{
		return getMember(TemplateFactory.class, identifier);
	}

	public Template get(String name)
	throws TemplateException
	{
		return get(name, null, null);
	}

	public Template get(String name, TemplateTransformer transformer)
	throws TemplateException
	{
		return get(name, null, transformer);
	}

	public Template get(String name, String encoding)
	throws TemplateException
	{
		return get(name, encoding, null);
	}

	public Template get(String name, String encoding, TemplateTransformer transformer)
	throws TemplateException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		try
		{
			AbstractTemplate template = (AbstractTemplate)parse(name, encoding, transformer).newInstance();
			template.setBeanHandler(mBeanHandler);
			template.setEncoder(mEncoder);
			template.setInitializer(mInitializer);
			template.setDefaultContentType(mDefaultContentType);

			assert template != null;

			Collection<String> default_resourcebundles = RifeConfig.Template.getDefaultResourcebundles(this);
			if (default_resourcebundles != null)
			{
				ArrayList<ResourceBundle> default_bundles = new ArrayList<ResourceBundle>();
				for (String bundle_name : default_resourcebundles)
				{
					// try to look it up as a filename in the classpath
					ResourceBundle bundle = Localization.getResourceBundle(bundle_name);
					if (bundle != null)
					{
						default_bundles.add(bundle);
						continue;
					}
				}
				template.setDefaultResourceBundles(default_bundles);
			}

			template.initialize();

			return template;
		}
		catch (IllegalAccessException e)
		{
			// this should not happen
			throw new TemplateException("TemplateFactory.get() : '"+name+"' IllegalAccessException : "+e.getMessage(), e);
		}
		catch (InstantiationException e)
		{
			// this should not happen
			throw new TemplateException("TemplateFactory.get() : '"+name+"' InstantiationException : "+e.getMessage(), e);
		}
	}
	
	public Class parse(String name, String encoding, TemplateTransformer transformer)
	throws TemplateException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		
		try
		{
			return getClassLoader().loadClass(mParser.getPackage()+mParser.escapeClassname(name), false, encoding, transformer);
		}
		catch (ClassNotFoundException e)
		{
			throw new TemplateNotFoundException(name, e);
		}
	}
	
	public TemplateFactory setResourceFinder(ResourceFinder resourceFinder)
	{
		if (null == resourceFinder)	throw new IllegalArgumentException("resourceFinder can't be null.");

		mResourceFinder = resourceFinder;

		return this;
	}

	public ResourceFinder getResourceFinder()
	{
		assert mResourceFinder != null;

		return mResourceFinder;
	}

	Parser getParser()
	{
		assert mParser != null;

		return mParser;
	}

	public TemplateFactory setBeanHandler(BeanHandler beanHandler)
	{
		mBeanHandler = beanHandler;

		return this;
	}

	public BeanHandler getBeanHandler()
	{
		return mBeanHandler;
	}

	public TemplateFactory setEncoder(TemplateEncoder encoder)
	{
		mEncoder = encoder;

		return this;
	}

	public TemplateEncoder getEncoder()
	{
		return mEncoder;
	}

	public TemplateFactory setInitializer(TemplateInitializer initializer)
	{
		mInitializer = initializer;

		return this;
	}

	public TemplateInitializer getInitializer()
	{
		return mInitializer;
	}

	private TemplateClassLoader getClassLoader()
	{
		if (null == mLastClassloader)
		{
			setClassLoader(new TemplateClassLoader(this, getClass().getClassLoader()));
		}

		assert mLastClassloader != null;

		return mLastClassloader;
	}

	synchronized void setClassLoader(TemplateClassLoader classloader)
	{
		mLastClassloader = classloader;
	}
}
