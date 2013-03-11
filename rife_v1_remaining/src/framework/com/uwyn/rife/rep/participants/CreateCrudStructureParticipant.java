/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CreateCrudStructureParticipant.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentFactory;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.InstallContentErrorException;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.crud.BeanClassFactory;
import com.uwyn.rife.crud.dam.CrudContentQueryManager;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.tools.ExceptionUtils;

import java.util.Stack;
import java.util.logging.Logger;

public class CreateCrudStructureParticipant extends ActivateCrudSiteProcessorParticipant
{
	public static final String CRUD_IDENTIFIER = "crud";
	
	protected void initialize()
	{
		Datasource datasource = (Datasource)getRepository().getProperties().getValue("datasource", Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("CRUD_DATASOURCE", Config.getRepInstance().getString("DATASOURCE"))));
		
		try
		{
			DatabaseContentFactory.getInstance(datasource).install();
			Logger.getLogger("com.uwyn.rife.crud").info("The RIFE CMF structure has been installed.");
		}
		catch (InstallContentErrorException e)
		{
			Logger.getLogger("com.uwyn.rife.crud").warning("The RIFE CMF structure couldn't be installed, it probably already exists.");
		}
		
		String[] implementations = getImplementationNames();
		if (implementations != null)
		{
			for (String implementation : implementations)
			{
				String implementation_trimmed = implementation.trim();
				if (0 == implementation_trimmed.length())
				{
					continue;
				}
				
				try
				{
					Class klass = BeanClassFactory.INSTANCE.getClassInstance(implementation_trimmed);
					
					installCrudStructure(new Stack<Class>(), datasource, klass);
				}
				catch (ClassNotFoundException e)
				{
					Logger.getLogger("com.uwyn.rife.crud").severe("The crud structure for "+implementation_trimmed+" couldn't be installed, since the class couldn't be found.\n"+ExceptionUtils.getExceptionStackTrace(e));
				}
			}
		}
	}

	protected String[] getImplementationNames()
	{
		String[] implementations = null;
		String parameter = getParameter();
		if (parameter != null)
		{
			implementations = parameter.split("[\\s\\n]");
		}
		return implementations;
	}

	protected boolean installCrudStructure(Stack<Class> previousClasses, Datasource datasource, Class klass)
	{
		boolean result = false;
		
		if (previousClasses.contains(klass))
		{
			return false;
		}
		
		previousClasses.push(klass);
		
		try
		{
			new CrudContentQueryManager(datasource, klass).install();
			result = true;
			
			Logger.getLogger("com.uwyn.rife.crud").info("The crud structure for " + klass.getName() + " has been installed.");
		}
		catch (DatabaseException e)
		{
			Logger.getLogger("com.uwyn.rife.crud").warning("The crud structure for " + getName() + " couldn't be installed, it probably already exists : "+ExceptionUtils.getExceptionStackTraceMessages(e));
		}
		
		// handle associations
		installAssociations(previousClasses, datasource, klass);
		
		previousClasses.pop();
		
		return result;
	}

	protected void installAssociations(Stack<Class> previousClasses, Datasource datasource, Class klass)
	{
		Constrained constrained = ConstrainedUtils.getConstrainedInstance(klass);
		if (constrained != null)
		{
			if (constrained.getConstrainedBean() != null &&
				constrained.getConstrainedBean().hasAssociations())
			{
				for (Class klass_association : constrained.getConstrainedBean().getAssociations())
				{
					installCrudStructure(previousClasses, datasource, klass_association);
				}
			}
		}
	}
}
