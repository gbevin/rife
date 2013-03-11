/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateDeployer.java 3957 2008-05-26 07:57:51Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.ResourceFinderDirectories;
import com.uwyn.rife.resources.ResourceFinderGroup;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.SortListComparables;
import com.uwyn.rife.tools.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TemplateDeployer
{
	private boolean			mVerbose = false;
	private File[]			mDirectories = null;
	private TemplateFactory	mTemplateFactory = null;
	private Pattern			mInclude = null;
	private Pattern			mExclude = null;
	
	private TemplateDeployer(boolean verbose, ArrayList<String> directoryPaths, TemplateFactory templateFactory, Pattern include, Pattern exclude)
	{
		assert directoryPaths != null;
		assert directoryPaths.size() > 0;
		
		mVerbose = verbose;
		mTemplateFactory = templateFactory;
		mInclude = include;
		mExclude = exclude;
		ArrayList<File> directories = new ArrayList<File>();

		File directory_file = null;
		
		for (String directory_path : directoryPaths)
		{
			directory_file = new File(directory_path);
			if (!directory_file.exists())
			{
				System.err.println("The path '"+directory_path+"' doesn't exist.");
				System.exit(1);
			}
			if (!directory_file.isDirectory())
			{
				System.err.println("The path '"+directory_path+"' is not a directory.");
				System.exit(1);
			}
			if (!directory_file.canRead())
			{
				System.err.println("The directory '"+directory_path+"' is not readable.");
				System.exit(1);
			}
			
			directories.add(directory_file);
		}
		
		mDirectories = new File[directories.size()];
		mDirectories = directories.toArray(mDirectories);
	}
	
	private void execute()
	throws TemplateException
	{
 		ArrayList<String>	files = null;
		String				classname = null;
		
		for (File directory : mDirectories)
		{
			ResourceFinderGroup group = new ResourceFinderGroup()
				.add(new ResourceFinderDirectories(new File[] {directory}))
				.add(ResourceFinderClasspath.getInstance());
 			mTemplateFactory.setResourceFinder(group);
			files = FileUtils.getFileList(directory, Pattern.compile(".*\\"+mTemplateFactory.getParser().getExtension()+"$"), Pattern.compile(".*(SCCS|CVS|\\.svn).*"));
			
			for (String file : files)
			{
				if (!StringUtils.filter(file, mInclude, mExclude))
				{
					continue;
				}
				
				if (mVerbose)
				{
					System.out.print(directory.getPath()+" : "+file+" ... ");
				}
				classname = file.replace(File.separatorChar, '.');
				classname = classname.substring(0, classname.length()-mTemplateFactory.getParser().getExtension().length());
				mTemplateFactory.parse(classname, null, null);
				if (mVerbose)
				{
					System.out.println("done.");
				}
			}
		}
	}
	
	private static void listTemplateTypes()
	{
		ArrayList<String>	types = new ArrayList<String>(TemplateFactory.getFactoryTypes());
		SortListComparables	sort = new SortListComparables();
		
		sort.sort(types);
		for (Object type : types)
		{
			System.err.println("  "+type);
		}
	}

	public TemplateDeployer()
	{
		super();
	}

	public static void main(String[] arguments)
	{
		boolean				valid_arguments = true;
		boolean				verbose = false;
		ArrayList<String>	directory_paths = new ArrayList<String>();
		String				template_type = "enginehtml";
		Pattern				include = null;
		Pattern				exclude = null;
		
		if (arguments.length < 1)
		{
			valid_arguments = false;
		}
		else
		{
			for (int i = 0; i < arguments.length; i++)
			{
				if (arguments[i].startsWith("-"))
				{
					if (arguments[i].equals("-t"))
					{
						i++;
						if (arguments[i].startsWith("-"))
						{
							valid_arguments = false;
						}
						else
						{
							template_type = arguments[i];
						}
					}
					else if (arguments[i].equals("-l"))
					{
						System.err.println("The supported template types are:");
						listTemplateTypes();
						System.exit(0);
					}
					else if (arguments[i].equals("-verbose"))
					{
						verbose = true;
					}
					else if (arguments[i].equals("-d"))
					{
						i++;
						if (arguments[i].startsWith("-"))
						{
							valid_arguments = false;
						}
						else
						{
							RifeConfig.Template.setGenerationPath(arguments[i]);
						}
					}
					else if (arguments[i].equals("-encoding"))
					{
						i++;
						if (arguments[i].startsWith("-"))
						{
							valid_arguments = false;
						}
						else
						{
							RifeConfig.Template.setDefaultEncoding(arguments[i]);
						}
					}
					else if (arguments[i].equals("-preload"))
					{
						i++;
						if (arguments[i].startsWith("-"))
						{
							valid_arguments = false;
						}
						else
						{
							List<String> class_names = StringUtils.split(arguments[i], ":");
							for (String class_name : class_names)
							{
								try
								{
									Class.forName(class_name);
								}
								catch (ClassNotFoundException e)
								{
									throw new RuntimeException(e);
								}
							}
						}
					}
					else if (arguments[i].equals("-i"))
					{
						i++;
						if (arguments[i].startsWith("-"))
						{
							valid_arguments = false;
						}
						else
						{
							include = Pattern.compile(arguments[i]);
						}
					}
					else if (arguments[i].equals("-e"))
					{
						i++;
						if (arguments[i].startsWith("-"))
						{
							valid_arguments = false;
						}
						else
						{
							exclude = Pattern.compile(arguments[i]);
						}
					}
					else
					{
						valid_arguments = false;
					}
				}
				else
				{
					directory_paths.add(arguments[i]);
				}
				
				if (!valid_arguments)
				{
					break;
				}
			}
		}
		
		if (0 == directory_paths.size())
		{
			valid_arguments = false;
		}
		
		if (!valid_arguments)
		{
			System.err.println("Usage : java "+TemplateDeployer.class.getName()+" <options> <directories>");
			System.err.println("Compiles RIFE templates to class files.");
			System.err.println("All the files of the active template type that are found in the provided");
			System.err.println("directories will be parsed and compiled to java bytecode into the");
			System.err.println("destination directory.");
			System.err.println("  -t <type>             Specify which template type to use (default enginehtml)");
			System.err.println("  -l                    List the known template types");
			System.err.println("  -verbose              Output messages about what the parser is doing");
			System.err.println("  -d <directory>        Specify where to place generated class files");
			System.err.println("  -encoding <encoding>  Specify character encoding used by template files");
			System.err.println("  -preload <classes>    Colon seperated list of classes to preload");
			System.err.println("  -i <regexp>           Regexp to include certain files");
			System.err.println("  -e <regexp>           Regexp to exclude certain files");
			System.err.println("  -help                 Print a synopsis of standard options");
			System.exit(1);
		}
		
		TemplateFactory		factory = null;
		factory = TemplateFactory.getFactory(template_type);
		if (null == factory)
		{
			System.err.println("The template type '"+template_type+"' is not supported.");
			System.err.println("The list of valid types is:");
			listTemplateTypes();
			System.exit(1);
		}
		
		RifeConfig.Template.setGenerateClasses(true);
		TemplateDeployer	deployer = new TemplateDeployer(verbose, directory_paths, factory, include, exclude);
		deployer.execute();
	}
}

