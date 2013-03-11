/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CrudSiteProcessor.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud;

import com.uwyn.rife.crud.elements.admin.*;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerFactory;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerRelationalUtils;
import com.uwyn.rife.database.querymanagers.generic.ManyToOneDeclaration;
import com.uwyn.rife.engine.ElementInfoBuilder;
import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.engine.SiteProcessor;
import com.uwyn.rife.engine.SiteProcessorFactory;
import com.uwyn.rife.engine.UrlResource;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.site.PagedNavigation;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.ClassUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.uwyn.rife.crud.CrudPropertyNames.*;

public class CrudSiteProcessor implements SiteProcessor
{
	public final static String	CRUD_PREFIX = "crud-";
	
	public final static String	IDENTIFIER_MANYTOONE_PROPERTYNAME;
	public final static String	IDENTIFIER_ASSOCIATED_CLASSNAME;
	public final static String	IDENTIFIER_IMPLEMENTATION;
	public final static String	IDENTIFIER_CLASS;
	public final static String	IDENTIFIER_HIGHLIGHT;
	public final static String	IDENTIFIER_ELEMENTID_HOME;
	public final static String	IDENTIFIER_ELEMENTID_MENU;
	public final static String	IDENTIFIER_ELEMENTID_BROWSE;
	public final static String	IDENTIFIER_ELEMENTID_ADD;
	public final static String	IDENTIFIER_ELEMENTID_SERVECONTENT;
	public final static String	IDENTIFIER_ELEMENTID_EDIT;
	public final static String	IDENTIFIER_ELEMENTID_DELETE;
	public final static String	IDENTIFIER_ELEMENTID_MOVE;
	public final static String	IDENTIFIER_SUBSITEID_MODIFICATION;
	public final static String	IDENTIFIER_SUBSITEELEMENTID_EDIT;
	public final static String	IDENTIFIER_SUBSITEELEMENTID_DELETE;
	public final static String	IDENTIFIER_SUBSITEELEMENTID_MOVE;

	public final static String	SUFFIX_EXIT_HOME = "-home";
	public final static String	SUFFIX_EXIT_BROWSE = "-browse";
	public final static String	SUFFIX_EXIT_ADD = "-add";
	public final static String	SUFFIX_EXIT_EDIT = "-edit";
	public final static String	SUFFIX_EXIT_DELETE = "-delete";
	public final static String	SUFFIX_EXIT_SERVECONTENT = "-servecontent";
	
	static
	{
		IDENTIFIER_MANYTOONE_PROPERTYNAME = CRUD_PREFIX+"manytoone_propertyname";
		IDENTIFIER_ASSOCIATED_CLASSNAME = CRUD_PREFIX+"associated_classname";
		IDENTIFIER_IMPLEMENTATION = CRUD_PREFIX+"implementation";
		IDENTIFIER_CLASS = CRUD_PREFIX+"class";
		IDENTIFIER_HIGHLIGHT = CRUD_PREFIX+"highlight";
		IDENTIFIER_ELEMENTID_HOME = "HOME";
		IDENTIFIER_ELEMENTID_MENU = "MENU";
		IDENTIFIER_ELEMENTID_BROWSE = "BROWSE";
		IDENTIFIER_ELEMENTID_ADD = "ADD";
		IDENTIFIER_ELEMENTID_SERVECONTENT = "SERVECONTENT";
		IDENTIFIER_ELEMENTID_EDIT = "EDIT";
		IDENTIFIER_ELEMENTID_DELETE = "DELETE";
		IDENTIFIER_ELEMENTID_MOVE = "MOVE";
		IDENTIFIER_SUBSITEID_MODIFICATION = "MODIFICATION";
		IDENTIFIER_SUBSITEELEMENTID_EDIT = IDENTIFIER_SUBSITEID_MODIFICATION+"."+IDENTIFIER_ELEMENTID_EDIT;
		IDENTIFIER_SUBSITEELEMENTID_DELETE = IDENTIFIER_SUBSITEID_MODIFICATION+"."+IDENTIFIER_ELEMENTID_DELETE;
		IDENTIFIER_SUBSITEELEMENTID_MOVE = IDENTIFIER_SUBSITEID_MODIFICATION+"."+IDENTIFIER_ELEMENTID_MOVE;
	}
	
	public void processSite(SiteBuilder builder, String implementation, ResourceFinder resourceFinder)
	{
		try
		{
			Class klass = BeanClassFactory.INSTANCE.getClassInstance(implementation);
			if (RifeConfig.Engine.getSiteAutoReload())
			{
				URL resource = BeanClassFactory.INSTANCE.getClassResource(implementation);
				builder.addResourceModificationTime(new UrlResource(resource, implementation), resourceFinder.getModificationTime(resource));
			}
			buildAdminSubsite(builder, implementation, klass, null, null);
		}
		catch (ClassNotFoundException e)
		{
			throw new EngineException(e);
		}
		catch (ResourceFinderErrorException e)
		{
			throw new EngineException(e);
		}
	}

	public void buildAdminSubsite(SiteBuilder builder, String implementation, Class klass, String manytooneProperty, String associatedClassname)
	throws EngineException
	{
		String	classname = klass.getName();
		String	short_classname = ClassUtils.shortenClassName(klass);
		String	submission_name = short_classname+"data";
		String	identity_property = ConstrainedUtils.getIdentityProperty(klass);
		String	identity_property_prefixed = short_classname+identity_property;

		String					ordinal_property_name = null;
		HashMap<Class, String>	associations_columns = null;

		// analyze the class to check for special features such as ordinal properties
		Constrained constrained = ConstrainedUtils.getConstrainedInstance(klass);
		Collection<ConstrainedProperty> properties = null;
		if (constrained != null)
		{
			properties = constrained.getConstrainedProperties();
			
			// detect ordinal properties
			for (ConstrainedProperty property : properties)
			{
				if (null == ordinal_property_name &&
					property.isOrdinal())
				{
					ordinal_property_name = property.getPropertyName();
				}
			}
			
			// detect associations
			if (constrained.getConstrainedBean() != null &&
				constrained.getConstrainedBean().hasAssociations())
			{
				Datasource datasource = (Datasource)builder.getProperty("datasource", Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("CRUD_DATASOURCE", Config.getRepInstance().getString("DATASOURCE"))));
				GenericQueryManager gqm = GenericQueryManagerFactory.getInstance(datasource, klass);
				String gqm_table_name = gqm.getTable();

				for (Class association : constrained.getConstrainedBean().getAssociations())
				{
					try
					{
						Map<String, Class> property_types = BeanUtils.getPropertyTypes(association, null, null, null);
						Constrained<ConstrainedBean, ConstrainedProperty> constrained_association = ConstrainedUtils.getConstrainedInstance(association);
						if (constrained_association != null)
						{
							for (ConstrainedProperty property : constrained_association.getConstrainedProperties())
							{
								ManyToOneDeclaration declaration = GenericQueryManagerRelationalUtils.createManyToOneDeclaration(gqm, property, property_types.get(property.getName()));
								if (declaration != null &&
									gqm_table_name.equals(declaration.getAssociationTable()) &&
									identity_property.equals(declaration.getAssociationColumn()))
								{
									if (null == associations_columns)
									{
										associations_columns = new LinkedHashMap<Class, String>();
									}
									
									associations_columns.put(association, property.getPropertyName());
									break;
								}
							}
						}
					}
					catch (BeanUtilsException e)
					{
						throw new EngineException(e);
					}
				}
			}
		}
		
		// generate the class-specific crud prefix
		String crud_prefix = CRUD_PREFIX+classname;
		
		// generate the class-specific global exit names
		String	exit_home = crud_prefix+SUFFIX_EXIT_HOME;
		String	exit_browse = crud_prefix+SUFFIX_EXIT_BROWSE;
		String	exit_add = crud_prefix+SUFFIX_EXIT_ADD;
		String	exit_edit = crud_prefix+SUFFIX_EXIT_EDIT;
		String	exit_delete = crud_prefix+SUFFIX_EXIT_DELETE;
		String	exit_servecontent = crud_prefix+SUFFIX_EXIT_SERVECONTENT;
		
		builder
			.setArrival(IDENTIFIER_ELEMENTID_BROWSE);
		if (null == associatedClassname)
		{
			builder
				.addGlobalExit(crud_prefix, "");
		}
		
		builder
			.addGlobalExit(exit_home, IDENTIFIER_ELEMENTID_HOME)
			.addGlobalExit(exit_browse, IDENTIFIER_ELEMENTID_BROWSE)
			.addGlobalExit(exit_add, IDENTIFIER_ELEMENTID_ADD)
			.addGlobalExit(exit_edit, IDENTIFIER_SUBSITEELEMENTID_EDIT)
			.addGlobalExit(exit_delete, IDENTIFIER_SUBSITEELEMENTID_DELETE)
			.addGlobalExit(exit_servecontent, IDENTIFIER_ELEMENTID_SERVECONTENT)
			
			.addGlobalVar(PagedNavigation.DEFAULT_OUTPUT)
			
			.addProperty(IDENTIFIER_IMPLEMENTATION, new PropertyValueObject(implementation))
			.addProperty(IDENTIFIER_CLASS, new PropertyValueObject(klass))
		
			.enterElement(IDENTIFIER_ELEMENTID_MENU)
				.setImplementation(builder.getPropertyString(IMPLEMENTATION_MENU, Menu.class.getName()))
			.leaveElement()
		
			.enterElement("rife/cmf/serve_content.xml")
				.setId(IDENTIFIER_ELEMENTID_SERVECONTENT)
				.setUrl("/content/*")
			.leaveElement();
		
		ElementInfoBuilder home_element= builder
			.enterElement(IDENTIFIER_ELEMENTID_HOME)
				.setImplementation(builder.getPropertyString(IMPLEMENTATION_HOME, Home.class.getName()))
				.setUrl("/home")
				.addProperty(IDENTIFIER_HIGHLIGHT, new PropertyValueObject(crud_prefix));
			
		ElementInfoBuilder add_element = builder
			.enterElement(IDENTIFIER_ELEMENTID_ADD)
				.setImplementation(builder.getPropertyString(IMPLEMENTATION_ADD, Add.class.getName()))
				.setUrl("/add")
				.addProperty(IDENTIFIER_HIGHLIGHT, new PropertyValueObject(crud_prefix))
				.enterSubmission(submission_name)
					.addBean(klass)
				.leaveSubmission();
		
		ElementInfoBuilder browse_element = builder
			.enterElement(IDENTIFIER_ELEMENTID_BROWSE)
				.setImplementation(builder.getPropertyString(IMPLEMENTATION_BROWSE, Browse.class.getName()))
				.setUrl("/browse")
				.addProperty(IDENTIFIER_HIGHLIGHT, new PropertyValueObject(crud_prefix))
				.addExit(PagedNavigation.DEFAULT_EXIT)
				.addFlowLink(PagedNavigation.DEFAULT_EXIT, IDENTIFIER_ELEMENTID_BROWSE)
				.addOutput(identity_property_prefixed)
				.addDataLink(identity_property_prefixed, IDENTIFIER_SUBSITEELEMENTID_EDIT, identity_property_prefixed)
				.addDataLink(identity_property_prefixed, IDENTIFIER_SUBSITEELEMENTID_DELETE, identity_property_prefixed);
		
		SiteBuilder modification_subsite_builder = builder
			.enterSubsiteDeclaration(SiteProcessorFactory.MANUAL_IDENTIFIER+":"+IDENTIFIER_SUBSITEID_MODIFICATION)
				.setId(IDENTIFIER_SUBSITEID_MODIFICATION)
				.enterSubsite()
					.addGlobalVar(identity_property_prefixed);
						
		ElementInfoBuilder edit_element = modification_subsite_builder
					.enterElement(IDENTIFIER_ELEMENTID_EDIT)
						.setImplementation(builder.getPropertyString(IMPLEMENTATION_EDIT, Edit.class.getName()))
						.setUrl("/edit")
						.addProperty(IDENTIFIER_HIGHLIGHT, new PropertyValueObject(exit_browse))
						.enterSubmission(submission_name)
							.addBean(klass)
						.leaveSubmission();
							
		ElementInfoBuilder delete_element = modification_subsite_builder
					.enterElement(IDENTIFIER_ELEMENTID_DELETE)
						.setImplementation(builder.getPropertyString(IMPLEMENTATION_DELETE, Delete.class.getName()))
						.setUrl("/delete")
						.addProperty(IDENTIFIER_HIGHLIGHT, new PropertyValueObject(exit_browse))
						.enterSubmission("confirm")
						.leaveSubmission();
		
		// store the manytoone property name
		if (manytooneProperty != null)
		{
			// add the required element properties
			home_element
				.addProperty(IDENTIFIER_MANYTOONE_PROPERTYNAME, new PropertyValueObject(manytooneProperty))
				.addProperty(IDENTIFIER_ASSOCIATED_CLASSNAME, new PropertyValueObject(associatedClassname));
			browse_element
				.addProperty(IDENTIFIER_MANYTOONE_PROPERTYNAME, new PropertyValueObject(manytooneProperty))
				.addProperty(IDENTIFIER_ASSOCIATED_CLASSNAME, new PropertyValueObject(associatedClassname));
			add_element
				.addProperty(IDENTIFIER_MANYTOONE_PROPERTYNAME, new PropertyValueObject(manytooneProperty))
				.addProperty(IDENTIFIER_ASSOCIATED_CLASSNAME, new PropertyValueObject(associatedClassname));
			edit_element
				.addProperty(IDENTIFIER_MANYTOONE_PROPERTYNAME, new PropertyValueObject(manytooneProperty))
				.addProperty(IDENTIFIER_ASSOCIATED_CLASSNAME, new PropertyValueObject(associatedClassname));
			delete_element
				.addProperty(IDENTIFIER_MANYTOONE_PROPERTYNAME, new PropertyValueObject(manytooneProperty))
				.addProperty(IDENTIFIER_ASSOCIATED_CLASSNAME, new PropertyValueObject(associatedClassname));
		}
		
		// build association subsites
		if (associations_columns != null)
		{
			for (Class assocation : associations_columns.keySet())
			{
				String name = ClassUtils.shortenClassName(assocation);
				modification_subsite_builder.addGlobalExit(CRUD_PREFIX+assocation.getName(), name);
			}
				
			for (Class assocation : associations_columns.keySet())
			{
				// setup the subsite declaration
				String name = ClassUtils.shortenClassName(assocation);
				String declaration = SiteProcessorFactory.MANUAL_IDENTIFIER+":"+ClassUtils.shortenClassName(assocation);
				SiteBuilder subsite = modification_subsite_builder
					.enterSubsiteDeclaration(declaration)
						.setId(name)
						.setUrlPrefix(name)
						.enterSubsite();
				// build the actual subsite recursively
				buildAdminSubsite(subsite, assocation.getName(), assocation, associations_columns.get(assocation), klass.getName());

				// setup links from the browse element of the associated class
				builder
					.addProperty(crud_prefix+"-associations_columns", new PropertyValueObject(associations_columns));
				
				String association_crud_prefix = CRUD_PREFIX+assocation.getName();
				String association_exit_browse = association_crud_prefix+"-browse";
				String association_exit_add = association_crud_prefix+"-add";
				String association_target_browse = IDENTIFIER_SUBSITEID_MODIFICATION+"."+name+"."+IDENTIFIER_ELEMENTID_BROWSE;
				String association_target_add = IDENTIFIER_SUBSITEID_MODIFICATION+"."+name+"."+IDENTIFIER_ELEMENTID_ADD;
				browse_element
					.addExit(association_exit_browse)
					.addExit(association_exit_add)
					.addFlowLink(association_exit_browse, association_target_browse)
					.addFlowLink(association_exit_add, association_target_add)
					.addDataLink(identity_property_prefixed, association_target_browse, identity_property_prefixed)
					.addDataLink(identity_property_prefixed, association_target_add, identity_property_prefixed);
				}
		}
		
		// add the ordinal property name if it exists and create the appropriate additional elements
		if (ordinal_property_name != null)
		{
			builder
				.addProperty(crud_prefix+"-ordinal_property_name", new PropertyValueObject(ordinal_property_name));

			browse_element
				.addOutput("direction")
				.addExit("up")
				.addExit("down")
				.addFlowLink("up", IDENTIFIER_SUBSITEELEMENTID_MOVE)
				.addFlowLink("down", IDENTIFIER_SUBSITEELEMENTID_MOVE)
				.addDataLink(identity_property_prefixed, IDENTIFIER_SUBSITEELEMENTID_MOVE, identity_property_prefixed)
				.addDataLink("direction", IDENTIFIER_SUBSITEELEMENTID_MOVE, "direction");

			modification_subsite_builder
				
				.enterElement(IDENTIFIER_ELEMENTID_MOVE)
						.setImplementation(builder.getPropertyString(IMPLEMENTATION_MOVE, Move.class.getName()))
						.setUrl("/move")
						.addInput("direction")
						.addExit("moved")
						.addFlowLink("moved", "^"+IDENTIFIER_ELEMENTID_BROWSE)
					.leaveElement();
		}
	}
}
