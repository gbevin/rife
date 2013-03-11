/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.elements.Redirect;
import com.uwyn.rife.engine.exceptions.*;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.site.ValidatedConstrained;
import com.uwyn.rife.site.ValidationGroup;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

import java.util.*;

public class SiteBuilder
{
	private ResourceFinder										mResourceFinder = null;
	private SiteBuilder											mParent = null;
	private	SubsiteDeclaration									mSubsiteDeclaration = null;
	
	private boolean												mProcessed = false;
	private String												mIdentifier = null;
	private String												mDeclarationName = null;
	private String												mSiteId = null;
	private String												mUrlPrefix = null;
	private Stack<StateStore>									mStateStores = null;
	private HashSet<String>										mSubsiteHistory = null;
	
	private Site												mSite = null;
	
	private	Stack<GroupDeclaration>								mGroupDeclarationsStack = null;
	private	ArrayList<GroupDeclaration>							mGroupDeclarations = null;
	
	private String												mFallbackElementId = null;
	private String												mArrivalElementId = null;
	private boolean												mArrivalRedirect = false;
	private ElementDeclaration									mArrivalElementDeclaration = null;
	private ArrayList<String>									mDepartureIds = null;
	
	private LinkedHashMap<String, ElementDeclaration>			mElementIdMapping = null;
	private ArrayList<ElementDeclaration>						mElementDeclarations = null;
	
	private LinkedHashMap<String, SubsiteDeclaration>			mChildSubsiteIdMapping = null;
	private ArrayList<SubsiteDeclaration>						mChildSubsiteDeclarations = null;
	
	private LinkedHashMap<String, HashSet<DataLinkDeclaration>>	mDataLinkMapping = null;
	private LinkedHashMap<String, HashSet<FlowLinkDeclaration>>	mFlowLinkMapping = null;
	private LinkedHashMap<String, HashSet<AutoLinkDeclaration>>	mAutoLinkMapping = null;
	
	private HierarchicalProperties								mProperties = null;
	
	public SiteBuilder(String declarationName)
	{
		this(declarationName, ResourceFinderClasspath.getInstance());
	}
	
	public SiteBuilder(String declarationName, ResourceFinder resourceFinder)
	{
		if (null == declarationName)	throw new IllegalArgumentException("declarationName can't be null.");
		if (null == resourceFinder)		throw new IllegalArgumentException("resourceFinder can't be null.");
		
		mResourceFinder = resourceFinder;
		
		initialize(declarationName);
	}
	
	private SiteBuilder(String declarationName, ResourceFinder resourceFinder, SiteBuilder parent, SubsiteDeclaration subsite)
	{
		assert declarationName != null;
		assert resourceFinder != null;
		assert parent != null;
		assert parent != this;
		assert subsite != null;
		
		mResourceFinder = resourceFinder;
		mParent = parent;
		mSubsiteDeclaration = subsite;
		
		initialize(declarationName);
	}
	
	private SiteBuilder initialize(String declarationName)
	{
		mDeclarationName = declarationName;
		
		if (null == getParent())
		{
			mSite = new Site();
			mSite.setDeclarationName(mDeclarationName);
			
			// if the site structure must be automatically reloaded
			// ensure that the same resource finder is used as the one
			// that found the element and site xml files
			if (RifeConfig.Engine.getSiteAutoReload())
			{
				mSite.setResourceFinder(mResourceFinder);
			}
			
			mStateStores = new Stack<StateStore>();
			mStateStores.push(StateStoreFactory.getInstance(StateStoreQuery.IDENTIFIER));
			mGroupDeclarationsStack = new Stack<GroupDeclaration>();
			mGroupDeclarations = new ArrayList<GroupDeclaration>();
			mSubsiteHistory = new HashSet<String>();
			
			addGroupDeclaration(new GroupDeclaration(this, declarationName));
		}
		else
		{
			mSite = mParent.mSite;
			
			mStateStores = mSubsiteDeclaration.getStateStores();
			mSubsiteHistory = new HashSet<String>(getParent().mSubsiteHistory);
			
			mGroupDeclarationsStack = new Stack<GroupDeclaration>();
			mGroupDeclarations = new ArrayList<GroupDeclaration>();
			
			addGroupDeclaration(mSubsiteDeclaration.getGroupDeclaration());
		}
		
		mFallbackElementId = null;
		mArrivalElementId = null;
		mArrivalElementDeclaration = null;
		mDepartureIds = new ArrayList<String>();
		mElementIdMapping = new LinkedHashMap<String, ElementDeclaration>();
		mElementDeclarations = new ArrayList<ElementDeclaration>();
		mChildSubsiteIdMapping = new LinkedHashMap<String, SubsiteDeclaration>();
		mChildSubsiteDeclarations = new ArrayList<SubsiteDeclaration>();
		mDataLinkMapping = new LinkedHashMap<String, HashSet<DataLinkDeclaration>>();
		mFlowLinkMapping = new LinkedHashMap<String, HashSet<FlowLinkDeclaration>>();
		mAutoLinkMapping = new LinkedHashMap<String, HashSet<AutoLinkDeclaration>>();
		mProperties = new HierarchicalProperties();
		// if this is the root site, the parent properties are those
		// of the repository
		if (null == mParent)
		{
			mProperties.setParent(Rep.getProperties());
		}
		else
		{
			// the root site hasn't got a subsite declaration, so link
			// to its properties directly
			if (null == mParent.getSubsiteDeclaration())
			{
				mProperties.setParent(mParent.getProperties());
			}
			// for the real subsite, the parent should be the declaration properties
			// since they have precedence over the subsite properties themselves
			else
			{
				mProperties.setParent(mParent.getSubsiteDeclaration().getProperties());
			}
		}
		
		return this;
	}
	
	public synchronized Site getSite()
	{
		SiteBuilder root = getRoot();
		if (!mProcessed)
		{
			root.process();
		}
		
		return root.mSite;
	}
	
	public String getSiteProcessorIdentifier()
	{
		return mIdentifier;
	}
	
	private synchronized void process()
	throws EngineException
	{
		String					declaration = mDeclarationName;
		SiteProcessorFactory	processor_factory = null;
		
		String identifier = SiteProcessorFactory.MANUAL_IDENTIFIER;
		int identifier_index = mDeclarationName.indexOf(":");
		int extension_index = mDeclarationName.lastIndexOf(".");
		if (identifier_index != -1)
		{
			identifier = mDeclarationName.substring(0, identifier_index);
			declaration = mDeclarationName.substring(identifier_index+1);
			
			processor_factory = SiteProcessorFactory.getSiteProcessorFactory(identifier);
		}
		else if (extension_index != -1)
		{
			String extension = mDeclarationName.substring(extension_index+1);
			Collection<SiteProcessorFactory> factories = SiteProcessorFactory.getSiteProcessorFactories();
			for (SiteProcessorFactory factory : factories)
			{
				if (factory.getExtension() != null &&
					factory.getExtension().equals(extension))
				{
					processor_factory = factory;
					break;
				}
			}
			
			if (null == processor_factory)
			{
				throw new SiteProcessorExtensionUnsupportedException(mDeclarationName, extension);
			}
		}
		else
		{
			processor_factory = SiteProcessorFactory.getSiteProcessorFactory(identifier);
		}
		
		if (null == processor_factory)
		{
			throw new SiteProcessorIdentifierUnsupportedException(mDeclarationName, identifier);
		}
		
		// check for circular subsites
		if (!identifier.equals(SiteProcessorFactory.MANUAL_IDENTIFIER))
		{
			if (mSubsiteHistory.contains(mDeclarationName))
			{
				throw new CircularSubsitesException(mDeclarationName);
			}
			mSubsiteHistory.add(mDeclarationName);
		}
		
		// process the site definition
		SiteProcessor	processor = processor_factory.getProcessor();
		if (processor != null)
		{
			processor.processSite(this, declaration, mResourceFinder);
		}
		
		mIdentifier = processor_factory.getIdentifier();
		finish();
		
		mProcessed = true;
	}
	
	private synchronized void finish()
	throws EngineException
	{
		// first gather all the data about the elements and the subsites
		setupData();
		
		if (null == getParent())
		{
			// process the collected data from the root upwards to ensure
			// that all elements have been declared before the links to them
			// are processed
			processData();
			
			// create the inheritance stacks from the root site upwards
			// otherwise they would be created before all the subsites are
			// completely defined
			createInheritanceStacks();
			
			// create the precedence stacks from the root site upwards
			// otherwise they would be created before all the subsites are
			// completely defined
			createPrecedenceStacks();
		}
	}
	
	public SiteBuilder setFallback(String fallbackElementId)
	{
		mFallbackElementId = ensureLocalElementId(fallbackElementId);
		
		return this;
	}
	
	public SubsiteDeclaration enterSubsiteDeclaration(String declarationName)
	throws EngineException
	{
		// auto-generate empty declaration names
		if (null == declarationName)
		{
			declarationName = SiteProcessorFactory.MANUAL_IDENTIFIER+":"+getDeclarationName()+":subsite"+(mChildSubsiteDeclarations.size()+1);
		}
		
		GroupDeclaration	group_declaration = new GroupDeclaration(this, declarationName, getCurrentGroupDeclaration());
		SubsiteDeclaration	subsite_declaration = new SubsiteDeclaration(declarationName, group_declaration, mStateStores);
		mChildSubsiteDeclarations.add(subsite_declaration);
		SiteBuilder builder = new SiteBuilder(subsite_declaration.getDeclarationName(), mResourceFinder, this, subsite_declaration);
		subsite_declaration.setSiteBuilder(builder);
		group_declaration.setActiveSiteBuilder(builder);
		
		return subsite_declaration;
	}
	
	public SubsiteDeclaration leaveSubsite()
	{
		if (null == mParent)
		{
			return null;
		}
		
		return mSubsiteDeclaration;
	}
	
	public SiteBuilder enterGroup()
	throws EngineException
	{
		addGroupDeclaration(new GroupDeclaration(this, mDeclarationName, getCurrentGroupDeclaration()));
		
		return this;
	}
	
	public SiteBuilder leaveGroup()
	throws EngineException
	{
		mGroupDeclarationsStack.pop();
		
		return this;
	}
	
	public SiteBuilder setInherits(String inherits)
	throws EngineException
	{
		getCurrentGroupDeclaration().setInherits(inherits);
		
		return this;
	}
	
	public SiteBuilder setPre(String pre)
	throws EngineException
	{
		getCurrentGroupDeclaration().setPre(pre);
		
		return this;
	}
	
	public SiteBuilder addGlobalBean(String classname)
	{
		return addGlobalBean(classname, null, null, null);
	}
	
	public SiteBuilder addGlobalBean(String classname, String prefix)
	{
		return addGlobalBean(classname, prefix, null, null);
	}
	
	public SiteBuilder addGlobalBean(String classname, String prefix, String name)
	{
		return addGlobalBean(classname, prefix, name, null);
	}
	
	public SiteBuilder addGlobalBean(String classname, String prefix, String name, String groupName)
	throws EngineException
	{
		BeanDeclaration bean_declaration = new BeanDeclaration(classname, prefix, groupName);
		
		return addGlobalBean(bean_declaration, name);
	}
	
	public SiteBuilder addGlobalBean(Class klass)
	{
		return addGlobalBean(klass, null, null, null);
	}
	
	public SiteBuilder addGlobalBean(Class klass, String prefix)
	{
		return addGlobalBean(klass, prefix, null, null);
	}
	
	public SiteBuilder addGlobalBean(Class klass, String prefix, String name)
	{
		return addGlobalBean(klass, prefix, name, null);
	}
	
	public SiteBuilder addGlobalBean(Class klass, String prefix, String name, String groupName)
	throws EngineException
	{
		BeanDeclaration bean_declaration = new BeanDeclaration(klass, prefix, groupName);
		
		return addGlobalBean(bean_declaration, name);
	}
	
	private SiteBuilder addGlobalBean(BeanDeclaration beanDeclaration, String name)
	throws EngineException
	{
		GroupDeclaration site_group = getCurrentGroupDeclaration();
		try
		{
			Class bean_class = beanDeclaration.getBeanClass();
			
			if (name != null)
			{
				site_group.addNamedGlobalBean(name, beanDeclaration);
			}
			
			try
			{
				Object		instance = bean_class.newInstance();
				Constrained	constrained = ConstrainedUtils.makeConstrainedInstance(instance);
				Set<String>	properties;
				if (beanDeclaration.getGroupName() != null)
				{
					if (!(instance instanceof ValidatedConstrained))
					{
						throw new GlobalBeanGroupRequiresValidatedConstrainedException(mDeclarationName, beanDeclaration.getClassname(), beanDeclaration.getGroupName());
					}
					
					ValidatedConstrained validation = (ValidatedConstrained)instance;
					ValidationGroup group = validation.getGroup(beanDeclaration.getGroupName());
					if (null == group)
					{
						throw new GlobalBeanGroupNotFoundException(mDeclarationName, beanDeclaration.getClassname(), beanDeclaration.getGroupName());
					}
					properties = new TreeSet<String>();
					if (null == beanDeclaration.getPrefix())
					{
						properties.addAll(group.getPropertyNames());
					}
					else
					{
						for (String property_name : (List<String>)group.getPropertyNames())
						{
							properties.add(beanDeclaration.getPrefix()+property_name);
						}
					}
				}
				else
				{
					properties = BeanUtils.getPropertyNames(bean_class, null, null, beanDeclaration.getPrefix());
				}
				
				for (String property : properties)
				{
					if (ConstrainedUtils.editConstrainedProperty(constrained, property, beanDeclaration.getPrefix()))
					{
						site_group.addGlobalVar(property, new GlobalVar(null));
					}
				}
			}
			catch (IllegalAccessException e)
			{
				throw new GlobalBeanErrorException(mDeclarationName, beanDeclaration.getClassname(), e);
			}
			catch (InstantiationException e)
			{
				throw new GlobalBeanErrorException(mDeclarationName, beanDeclaration.getClassname(), e);
			}
			catch (BeanUtilsException e)
			{
				throw new GlobalBeanErrorException(mDeclarationName, beanDeclaration.getClassname(), e);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new GlobalBeanNotFoundException(mDeclarationName, beanDeclaration.getClassname(), e);
		}
		
		return this;
	}
	
	public SiteBuilder addGlobalExit(String name, String destId)
	throws EngineException
	{
		return addGlobalExit(name, destId, false, false, false, false, false, false);
	}
	
	public SiteBuilder addReflectiveGlobalExit(String name)
	throws EngineException
	{
		return addGlobalExit(name, null, true, false, false, false, false, false);
	}
	
	public SiteBuilder addSnapbackGlobalExit(String name)
	throws EngineException
	{
		return addGlobalExit(name, null, false, true, false, false, false, false);
	}
	
	public SiteBuilder addRedirectGlobalExit(String name)
	throws EngineException
	{
		return addGlobalExit(name, null, false, false, false, false, true, false);
	}
	
	public SiteBuilder addGlobalExit(String name, String destId, boolean reflective, boolean snapback, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect, boolean cancelContinuations)
	throws EngineException
	{
		if (null == destId &&
			!reflective &&
			!snapback)
		{
			throw new GlobalExitTargetRequiredException(mDeclarationName, name);
		}
		if (null == destId &&
			reflective &&
			snapback)
		{
			throw new GlobalExitAmbiguousTargetException(mDeclarationName, name);
		}
		if (destId != null &&
			(reflective || snapback))
		{
			throw new GlobalExitAmbiguousTargetException(mDeclarationName, name);
		}
		
		GlobalExit globalexit = new GlobalExit(destId, reflective, snapback, cancelInheritance, cancelEmbedding, redirect, cancelContinuations);
		getCurrentGroupDeclaration().addGlobalExit(name, globalexit);
		
		return this;
	}
	
	public SiteBuilder setArrival(String destId)
	throws EngineException
	{
		return setArrival(destId, false);
	}
	
	public SiteBuilder setArrival(String destId, boolean redirect)
	throws EngineException
	{
		mArrivalElementId = ensureLocalElementId(destId);
		mArrivalRedirect = redirect;
		
		return this;
	}
	
	public SiteBuilder addDeparture(String srcId)
	throws EngineException
	{
		mDepartureIds.add(ensureLocalElementId(srcId));
		
		return this;
	}
	
	public SiteBuilder enterState(String store)
	throws EngineException
	{
		StateStore state_store = StateStoreFactory.getInstance(store);
		if (null == state_store)
		{
			throw new StateStoreUnknownException(mDeclarationName, store);
		}
		
		mStateStores.push(state_store);
		
		return this;
	}
	
	public SiteBuilder leaveState()
	throws EngineException
	{
		mStateStores.pop();
		
		return this;
	}

	public SiteBuilder addElement(Class klass)
	throws EngineException
	{
		return enterElement(null).setImplementation(klass).leaveElement();
	}

	public ElementInfoBuilder enterElement()
	throws EngineException
	{
		return enterElement(null);
	}
	
	public ElementInfoBuilder enterElement(String declarationName)
	throws EngineException
	{
		ElementDeclaration declaration = new ElementDeclaration(this, mResourceFinder, getCurrentGroupDeclaration(), mStateStores.peek(), declarationName);
		addElementDeclaration(declaration);
		
		return declaration.getElementInfoBuilder();
	}
	
	public SiteBuilder addGlobalVar(String name)
	throws EngineException
	{
		return addGlobalVar(name, null);
	}
	
	public SiteBuilder addGlobalVar(String name, String[] defaultValues)
	throws EngineException
	{
		getCurrentGroupDeclaration().addGlobalVar(name, new GlobalVar(defaultValues));
		
		return this;
	}
	
	public SiteBuilder addGlobalCookie(String name)
	throws EngineException
	{
		return addGlobalCookie(name, null);
	}
	
	public SiteBuilder addGlobalCookie(String name, String defaultValue)
	throws EngineException
	{
		getCurrentGroupDeclaration().addGlobalCookie(name, defaultValue);
		
		return this;
	}

	public SiteBuilder addErrorHandler(String destId)
	throws EngineException
	{
		return addErrorHandler(destId, null);
	}

	public SiteBuilder addErrorHandler(String destId, Collection<Class> exceptionTypes)
	throws EngineException
	{
		if (null == destId)
		{
			throw new ErrorHandlerTargetRequiredException(getDeclarationName());
		}

		getCurrentGroupDeclaration().addErrorHandler(new ErrorHandler(makeAbsoluteElementId(destId), exceptionTypes));
		return this;
	}

	public SiteBuilder addProperty(String name, Object value)
	throws EngineException
	{
		return addProperty(name, new PropertyValueObject(value));
	}
	
	public SiteBuilder addProperty(String name, PropertyValue value)
	{
		mProperties.put(name, value);
		
		return this;
	}
	
	public HierarchicalProperties getProperties()
	{
		return mProperties;
	}
	
	public boolean containsProperty(String name)
	{
		if (null == mSubsiteDeclaration)
		{
			return mProperties.contains(name);
		}
		else
		{
			return mSubsiteDeclaration.getProperties().contains(name);
		}
	}
	
	public Object getProperty(String name)
	{
		return getProperty(name, null);
	}
	
	public Object getProperty(String name, Object defaultValue)
	{
		PropertyValue property = _getProperty(name);
		
		Object result = null;
		
		if (property != null)
		{
			try
			{
				result = property.getValue();
			}
			catch (PropertyValueException e)
			{
				throw new PropertyValueRetrievalException(getDeclarationName(), name, e);
			}
		}
		
		if (null == result)
		{
			return defaultValue;
		}
		
		return result;
	}

	private PropertyValue _getProperty(String name)
	{
		if (null == mSubsiteDeclaration)
		{
			return mProperties.get(name);
		}
		else
		{
			return mSubsiteDeclaration.getProperties().get(name);
		}
	}
	
	public String getPropertyString(String name)
	{
		return getPropertyString(name, null);
	}
	
	public String getPropertyString(String name, String defaultValue)
	{
		PropertyValue property = _getProperty(name);
		
		String result = null;

		if (property != null)
		{
			try
			{
				result = property.getValueString();
			}
			catch (PropertyValueException e)
			{
				throw new PropertyValueRetrievalException(getDeclarationName(), name, e);
			}
		}
		
		if (null == result ||
			0 == result.length())
		{
			return defaultValue;
		}
		
		return result;
	}
	
	public boolean isPropertyEmpty(String name)
	throws EngineException
	{
		if (!containsProperty(name))
		{
			return true;
		}
		
		if (0 == getPropertyString(name, "").length())
		{
			return true;
		}
		
		return false;
	}
	
	private SiteBuilder addGroupDeclaration(GroupDeclaration groupDeclaration)
	{
		mGroupDeclarationsStack.push(groupDeclaration);
		mGroupDeclarations.add(groupDeclaration);
		
		return this;
	}
	
	private void addElementDeclaration(ElementDeclaration elementDeclaration)
	{
		mElementDeclarations.add(elementDeclaration);
	}
	
	private GroupDeclaration getCurrentGroupDeclaration()
	{
		return mGroupDeclarationsStack.peek();
	}
	
	private String getId()
	{
		if (mSiteId != null)
		{
			return mSiteId;
		}
		
		if (null == mParent)
		{
			mSiteId = ".";
		}
		else
		{
			mSiteId = getParent().getId()+mSubsiteDeclaration.getId()+".";
		}
		
		return mSiteId;
	}
	
	private SiteBuilder getRoot()
	{
		SiteBuilder current = this;
		while (current.getParent() != null)
		{
			current = current.getParent();
		}
		
		return current;
	}
	
	SiteBuilder getParent()
	{
		return mParent;
	}
	
	private Collection<ElementDeclaration> getElementDeclarations()
	{
		return mElementDeclarations;
	}
	
	public SubsiteDeclaration getSubsiteDeclaration()
	{
		return mSubsiteDeclaration;
	}
	
	private ElementDeclaration getArrival()
	{
		return mArrivalElementDeclaration;
	}
	
	ElementDeclaration getGlobalElementDeclaration(String id)
	{
		assert id != null;
		
		// relative element id
		if (!id.startsWith("."))
		{
			id = getId()+id;
		}
		
		id = Site.getCanonicalId(id);
		
		// obtain the different parts of the id string
		List<String>	id_parts = StringUtils.split(id, ".");
		int				id_part_counter = 0;
		SiteBuilder		current_site = getRoot();
		
		id_parts.remove(0);	// remove the first entry since it is empty (id starts with a dot)
		for (String id_part : id_parts)
		{
			id_part_counter++;
			
			// check if this is the last id part, only handle elements
			// in the last id part, the rest must be subsite ids
			// it's possible though that elements are injected in subsites
			// from a parent subsite by specifying the dot-seperate path
			// directly
			if (id_part_counter < id_parts.size())
			{
				String element_injection = id.substring(current_site.getId().length());
				if (current_site.mElementIdMapping.containsKey(element_injection))
				{
					return current_site.mElementIdMapping.get(element_injection);
				}
				
				// check if the next path matches a subsite
				if (current_site.mChildSubsiteIdMapping.containsKey(id_part))
				{
					current_site = current_site.mChildSubsiteIdMapping.get(id_part).getSiteBuilder();
				}
				else
				{
					return null;
				}
			}
			// last id part, thus also look for elements
			else
			{
				if (current_site.mElementIdMapping.containsKey(id_part))
				{
					return current_site.mElementIdMapping.get(id_part);
				}
				
				if (current_site.mChildSubsiteIdMapping.containsKey(id_part))
				{
					return current_site.mChildSubsiteIdMapping.get(id_part).getSiteBuilder().getArrival();
				}
			}
		}
		
		return null;
	}
	
	private ElementDeclaration getElementDeclaration(String id)
	{
		assert id != null;
		
		if (mElementIdMapping.containsKey(id))
		{
			return mElementIdMapping.get(id);
		}
		
		if (mChildSubsiteIdMapping.containsKey(id))
		{
			return mChildSubsiteIdMapping.get(id).getSiteBuilder().getArrival();
		}
		
		return null;
	}
	
	private ElementInfo getElementInfo(String id)
	{
		assert id != null;
		
		ElementDeclaration	element_declaration = getElementDeclaration(id);
		
		if (element_declaration != null)
		{
			return element_declaration.getElementInfo();
		}
		
		return null;
	}
	
	private void setupElements()
	{
		ElementInfo	element_info = null;
		
		// iterate over the element declarations
		for (ElementDeclaration element_declaration : getElementDeclarations())
		{
			// ensure the unique element id
			if (mElementIdMapping.containsKey(element_declaration.getId()))
			{
				throw new ElementIdAlreadyRegisteredToElementException(mDeclarationName, element_declaration.getId(), mElementIdMapping.get(element_declaration.getId()).getDeclarationName(), element_declaration.getDeclarationName());
			}
			if (mChildSubsiteIdMapping.containsKey(element_declaration.getId()))
			{
				throw new ElementIdAlreadyRegisteredToSubsiteException(mDeclarationName, element_declaration.getId(), mChildSubsiteIdMapping.get(element_declaration.getId()).getDeclarationName(), element_declaration.getDeclarationName());
			}
			
			// process the element directives
			ElementInfoBuilder builder = element_declaration.getElementInfoBuilder();
			builder.process();
			GroupDeclaration group = element_declaration.getGroup();
			element_info = builder.createElementInfo(
				group.getGlobalExitsMerged(),
				group.getGlobalVarsMerged(),
				group.getGlobalCookiesMerged(),
				group.getNamedGlobalBeansMerged(),
				group.getErrorHandlersMerged());
			element_info.setGroupId(group.getGroupId());
			
			// check if the element declation url ends with a wildcard to add
			// the support for path info
			String element_url = element_declaration.getUrl();
			
			if (element_url != null)
			{
				if (element_url.endsWith("/") ||
					0 == element_url.length())
				{
					throw new ElementUrlInvalidException(element_declaration.getId(), element_url);
				}
				
				// ensure an initial slash
				if (element_url.length() > 0 &&
					!element_url.startsWith("/"))
				{
					element_url = "/"+element_url;
				}
				
				// prevent double slashes
				String url_prefix = getUrlPrefix();
				if (url_prefix.endsWith("/") &&
					element_url.startsWith("/"))
				{
					url_prefix = StringUtils.stripFromEnd(url_prefix, "/");
					element_url = StringUtils.stripFromFront(element_url, "/");
					StringBuilder buffer = new StringBuilder(url_prefix);
					buffer.append("/");
					buffer.append(element_url);
					element_url = buffer.toString();
				}
				else
				{
					element_url = url_prefix+element_url;
				}
				
				// handle path info
				if (element_url.endsWith("/*"))
				{
					element_url = element_url.substring(0, element_url.length()-2);
					element_info.setPathInfoUsed(true);
				}
			}
			
			// check if the element has an implementation
			if (null == element_info.getImplementation())
			{
				throw new MissingImplementationException(element_declaration.getDeclarationName());
			}

			// link the element properties with the site properties
			HierarchicalProperties site_properties;
			if (null == mSubsiteDeclaration)
			{
				site_properties = mProperties;
			}
			else
			{
				site_properties = mSubsiteDeclaration.getProperties();
			}
			HierarchicalProperties site_properties_shadow = site_properties.createShadow(Rep.getProperties());
			
			// deploy and register the constructed element info
			element_info.setProperties(element_declaration.getProperties(), site_properties_shadow);
			element_info.setStateStore(element_declaration.getStateStore());
			element_declaration.setElementInfo(element_info);
			mSite.addElementInfo(getId()+element_declaration.getId(), element_info, element_url);
			mElementIdMapping.put(element_declaration.getId(), element_declaration);
			element_info.deploy();
		}
	}
	
	private void setupSubsites()
	{
		for (SubsiteDeclaration subsite : mChildSubsiteDeclarations)
		{
			SiteBuilder subsite_builder = subsite.getSiteBuilder();
			
			subsite_builder.process();
			
			// register the subsite id
			if (mElementIdMapping.containsKey(subsite.getId()))
			{
				throw new SubsiteIdAlreadyRegisteredToElementException(mDeclarationName, subsite.getId(), mElementIdMapping.get(subsite.getId()).getDeclarationName(), subsite.getDeclarationName());
			}
			if (mChildSubsiteIdMapping.containsKey(subsite.getId()))
			{
				throw new SubsiteIdAlreadyRegisteredToSubsiteException(mDeclarationName, subsite.getId(), mChildSubsiteIdMapping.get(subsite.getId()).getDeclarationName(), subsite.getDeclarationName());
			}
			
			// register the subsite id
			mChildSubsiteIdMapping.put(subsite.getId(), subsite);
		}
	}
	
	private void setupArrivalElement()
	{
		if (mArrivalElementId != null)
		{
			mArrivalElementDeclaration = getElementDeclaration(mArrivalElementId);
			if (null == mArrivalElementDeclaration)
			{
				elementIdNotFound(mArrivalElementId);
			}
			
			// clone the element declaration and store it in the collection of the
			// site's element declarations
			mArrivalElementDeclaration = mArrivalElementDeclaration.clone();
			mArrivalElementDeclaration.setId("");
			addElementDeclaration(mArrivalElementDeclaration);
			mElementIdMapping.put("", mArrivalElementDeclaration);
		}
	}
	
	private void processSubsites()
	{
		for (SubsiteDeclaration subsite : mChildSubsiteDeclarations)
		{
			subsite.getSiteBuilder().processData();
		}
	}
	
	private void processGlobalExits()
	{
		ElementInfo	element_info = null;
		
		// iterate over all the element declarations in the site
		for (ElementDeclaration element_declaration : getElementDeclarations())
		{
			element_info = element_declaration.getElementInfo();
			
			// iterate over all global exits of the element
			Map<String, GlobalExit>	global_exits = element_declaration.getGroup().getGlobalExitsMerged();
			String					global_exit_name = null;
			GlobalExit				global_exit = null;
			FlowLink				flowlink = null;
			for (Map.Entry<String, GlobalExit> global_exit_entry : global_exits.entrySet())
			{
				global_exit_name = global_exit_entry.getKey();
				global_exit = global_exit_entry.getValue();
				
				// add the exit to every element info
				element_info.addExit(global_exit_name);
				
				// ensure a flow link from each elementinfo to the target element id
				HashSet<FlowLinkDeclaration> flowlinks = mFlowLinkMapping.get(element_declaration.getId());
				if (null == flowlinks)
				{
					flowlinks = new HashSet<FlowLinkDeclaration>();
					mFlowLinkMapping.put(element_declaration.getId(), flowlinks);
				}
				
				// check if this points to a specific element or if it's reflective
				if (global_exit.isReflective())
				{
					flowlink = new FlowLink(global_exit_name, element_info, global_exit.isSnapback(), global_exit.cancelInheritance(), global_exit.cancelEmbedding(), global_exit.isRedirect(), global_exit.cancelContinuations());
					element_info.setFlowLink(flowlink);
					
					// check if this element has identical inputs and outputs
					// these will be automatically linked due to the reflective exit
					Collection<String>	input_names = element_info.getInputNames();
					Collection<String>	output_names = element_info.getOutputNames();
					DataLinkDeclaration	datalink = null;
					for (String input_name : input_names)
					{
						if (output_names.contains(input_name))
						{
							datalink = new DataLinkDeclaration(input_name, null, element_declaration.getId(), false, input_name, null, null);
							addDataLink(element_declaration.getId(), datalink, false);
						}
					}
				}
				else
				{
					ElementDeclaration	target_elementdeclaration = null;
					
					String	globalexit_destid = global_exit.getDestId();
					if (globalexit_destid != null)
					{
						// get the element declaration that corresponds to the destination id
						target_elementdeclaration = getGlobalElementDeclaration(globalexit_destid);
						
						// if the target element couldn't be found, throw an exception
						if (null == target_elementdeclaration)
						{
							elementIdNotFound(globalexit_destid);
						}
						
						global_exit.setTarget(target_elementdeclaration.getElementInfo());
					}
					
					flowlinks.add(new FlowLinkDeclaration(this, global_exit_name, globalexit_destid, global_exit.isSnapback(), global_exit.cancelInheritance(), global_exit.cancelEmbedding(), global_exit.isRedirect(), global_exit.cancelContinuations()));
				}
			}
		}
	}
	
	void elementIdNotFound(String elementId)
	{
		if (mChildSubsiteIdMapping.containsKey(elementId))
		{
			throw new ElementIdNotFoundSiteIdExistsException(mSiteId, elementId);
		}
		
		throw new ElementIdNotFoundInSiteException(mSiteId, elementId);
	}
	
	private void processAutoLinks()
	{
		// process the auto links
		HashSet<AutoLinkDeclaration>	autolinks = null;
		for (String current_elementid : mAutoLinkMapping.keySet())
		{
			autolinks = mAutoLinkMapping.get(current_elementid);
			for (AutoLinkDeclaration autolink_declaration : autolinks)
			{
				autolink_declaration.registerFlowAndDataLinks();
			}
		}
	}
	
	private void processFlowLinks()
	{
		ElementDeclaration	current_elementdeclaration = null;
		
		// process the flow links
		HashSet<FlowLinkDeclaration>	flowlinks = null;
		for (String current_elementid : mFlowLinkMapping.keySet())
		{
			current_elementdeclaration = getElementDeclaration(current_elementid);
			
			flowlinks = mFlowLinkMapping.get(current_elementid);
			for (FlowLinkDeclaration flowlink_declaration : flowlinks)
			{
				// add the flowlink to the element info
				current_elementdeclaration.getElementInfo().setFlowLink(flowlink_declaration.getFlowLink());
			}
		}
	}
	
	private void processDataLinks()
	{
		ElementDeclaration	current_elementdeclaration = null;
		ElementDeclaration	target_elementdeclaration = null;
		
		// process the data links
		HashSet<DataLinkDeclaration>	datalinks = null;
		String							datalink_destid = null;
		DataLink						datalink = null;
		for (String current_elementid : mDataLinkMapping.keySet())
		{
			current_elementdeclaration = getElementDeclaration(current_elementid);
			
			datalinks = mDataLinkMapping.get(current_elementid);
			for (DataLinkDeclaration datalink_declaration : datalinks)
			{
				// resolve target element declaration
				if (datalink_declaration.isSnapback())
				{
					target_elementdeclaration = current_elementdeclaration;
				}
				else
				{
					datalink_destid = datalink_declaration.getDestId();
					
					// get the element declaration that corresponds to the destination id
					target_elementdeclaration = getGlobalElementDeclaration(datalink_destid);
					
					// if the target element couldn't be found, throw an exception
					if (null == target_elementdeclaration)
					{
						elementIdNotFound(datalink_declaration.getDestId());
					}
				}
				
				// handle bean data transfers
				if (datalink_declaration.transfersBean())
				{
					if (!current_elementdeclaration.getElementInfo().containsNamedOutbean(datalink_declaration.getSrcOutbean()))
					{
						throw new DataLinkUnknownSrcOutbeanException(mDeclarationName, datalink_declaration.getSrcOutbean(), current_elementid, datalink_declaration.getDestId(), datalink_declaration.isSnapback());
					}
					if (!target_elementdeclaration.getElementInfo().containsNamedInbean(datalink_declaration.getDestInbean()))
					{
						throw new DataLinkUnknownDestInbeanException(mDeclarationName, datalink_declaration.getDestInbean(), current_elementid, datalink_declaration.getDestId(), datalink_declaration.isSnapback());
					}
					
					BeanDeclaration	srcoutbean = current_elementdeclaration.getElementInfo().getNamedOutbeanInfo(datalink_declaration.getSrcOutbean());
					BeanDeclaration	destinbean = target_elementdeclaration.getElementInfo().getNamedInbeanInfo(datalink_declaration.getDestInbean());
					
					Class	srcoutbean_class = null;
					Class	destinbean_class = null;
					try
					{
						srcoutbean_class = Class.forName(srcoutbean.getClassname());
						destinbean_class = Class.forName(destinbean.getClassname());
					}
					catch (ClassNotFoundException e)
					{
						throw new DataLinkBeanErrorException(mDeclarationName, datalink_declaration.getSrcOutbean(), current_elementid, datalink_declaration.getDestId(), datalink_declaration.isSnapback(), datalink_declaration.getDestInbean(), e);
					}
					
					Constrained constrained_destinbean = ConstrainedUtils.getConstrainedInstance(destinbean_class);
					
					Set<String>	srcoutbean_properties = null;
					Set<String>	destinbean_properties = null;
					try
					{
						srcoutbean_properties = BeanUtils.getPropertyNames(srcoutbean_class, null, null, srcoutbean.getPrefix());
						destinbean_properties = BeanUtils.getPropertyNames(destinbean_class, null, null, destinbean.getPrefix());
					}
					catch (BeanUtilsException e)
					{
						throw new DataLinkBeanErrorException(mDeclarationName, datalink_declaration.getSrcOutbean(), current_elementid, datalink_declaration.getDestId(), datalink_declaration.isSnapback(), datalink_declaration.getDestInbean(), e);
					}
					
					for (String srcoutbean_property : srcoutbean_properties)
					{
						datalink = null;
						
						if (destinbean_properties.contains(srcoutbean_property) &&
							ConstrainedUtils.editConstrainedProperty(constrained_destinbean, srcoutbean_property, srcoutbean.getPrefix()) &&
							current_elementdeclaration.getElementInfo().containsOutputPossibility(srcoutbean_property))
						{
							if (datalink_declaration.isSnapback())
							{
								datalink = new DataLink(srcoutbean_property, null, datalink_declaration.isSnapback(), srcoutbean_property, datalink_declaration.getFlowLink());
								current_elementdeclaration.getElementInfo().addDataLink(datalink);
							}
							else if (target_elementdeclaration.getElementInfo().containsInputPossibility(srcoutbean_property))
							{
								datalink = new DataLink(srcoutbean_property, target_elementdeclaration.getElementInfo(), datalink_declaration.isSnapback(), srcoutbean_property, datalink_declaration.getFlowLink());
								current_elementdeclaration.getElementInfo().addDataLink(datalink);
							}
						}
					}
				}
				// handle regular values transfer
				else
				{
					if (datalink_declaration.isSnapback())
					{
						datalink = new DataLink(datalink_declaration.getSrcOutput(), null, datalink_declaration.isSnapback(), datalink_declaration.getDestInput(), datalink_declaration.getFlowLink());
					}
					else
					{
						datalink = new DataLink(datalink_declaration.getSrcOutput(), target_elementdeclaration.getElementInfo(), false, datalink_declaration.getDestInput(), datalink_declaration.getFlowLink());
					}
					current_elementdeclaration.getElementInfo().addDataLink(datalink);
				}
			}
		}
	}
	
	private void processFallbackElement()
	{
		if (mFallbackElementId != null)
		{
			ElementInfo	fallback_elementinfo = getElementInfo(mFallbackElementId);
			if (null == fallback_elementinfo)
			{
				elementIdNotFound(mFallbackElementId);
			}
			mSite.addFallback(fallback_elementinfo, getUrlPrefix());
		}
	}
	
	private void processArrivalElement()
	{
		if (mArrivalElementDeclaration != null)
		{
			// this second pass ensures that the arrival element is linked to and from
			// the correct locations
			ElementDeclaration target_element_declaration = getElementDeclaration(mArrivalElementId);
			mArrivalElementDeclaration.getElementInfo().populateFrom(target_element_declaration.getElementInfo());
			mArrivalElementDeclaration.setGroup(target_element_declaration.getGroup());
			
			// ensure & valid arrival id
			String arrival_id = mSiteId;
			
			// only use the site's url prefix if it's the root site or a subsite
			// that has its own url prefix
			String arrival_url = null;
			if (null == mParent ||
				target_element_declaration.getUrl() != null ||
				!getUrlPrefix().equals(mParent.getUrlPrefix()))
			{
				arrival_url = getUrlPrefix();
				// an arrival should never end with a final slash since that's
				// taken care of by the engine gate
				if (arrival_url.endsWith("/"))
				{
					arrival_url = StringUtils.stripFromEnd(arrival_url, "/");
				}
			}
			
			// handle arrival redirects
			if (mArrivalRedirect)
			{
				mArrivalElementDeclaration.getElementInfo().setImplementation(Redirect.class.getName());
				HierarchicalProperties properties = mArrivalElementDeclaration.getElementInfo().getProperties();
				properties.put("redirectInputs", true);
				properties.put("redirectInputs", true);
				
				// handle redirects when the target element has no URL
				if (null == target_element_declaration.getUrl())
				{
					properties.put("to", arrival_url+"/");
					properties.put("type", "url");
				}
				else
				{
					properties.put("to", makeAbsoluteElementId(mArrivalElementId));
					properties.put("type", "element");
				}
			}
			
			// register the arrival element
			ElementInfo	arrival_elementinfo = mArrivalElementDeclaration.getElementInfo();
			arrival_elementinfo.setId(null);
			arrival_elementinfo.setUrl(null);
			mSite.addElementInfo(arrival_id, arrival_elementinfo, arrival_url);
			
			// ensure that the empty root url isn't mapped twice
			if (arrival_url != null &&
				arrival_url.length() > 0)
			{
				// if the arrival redirects, and the target element has got no URL,
				// ensure that the URL with a final slash doesn't redirect to itself,
				// just map the arrival target element to the URL instead
				if (mArrivalRedirect &&
					null == target_element_declaration.getUrl())
				{
					mSite.mapElementId(makeAbsoluteElementId(mArrivalElementId), arrival_url+"/");
				}
				// map the URL with a final slash to the same arrival element
				else
				{
					mSite.mapElementId(arrival_id, arrival_url+"/");
				}
			}
		}
	}
	
	private void processDepartureElements()
	{
		if (mDepartureIds.size() > 0)
		{
			ElementDeclaration	departure_elementdeclaration = null;
			for (String departure_id : mDepartureIds)
			{
				departure_elementdeclaration = getElementDeclaration(departure_id);
				if (null == departure_elementdeclaration)
				{
					elementIdNotFound(departure_id);
				}
				
				ArrayList<String>	departure_vars = new ArrayList<String>(departure_elementdeclaration.getGroup().getGlobalVarsLocal().keySet());
				
				departure_elementdeclaration.getElementInfo().setDepartureVars(departure_vars);
			}
		}
	}
	
	private void processInheritsStack()
	{
		String				parent_id = null;
		ElementDeclaration	parent_elementdeclaration = null;
		
		// get the parent elementdeclaration of the elements that inherit from another element
		for (ElementDeclaration element_declaration : getElementDeclarations())
		{
			// obtain the parent elementdeclaration
			parent_id = element_declaration.getInherits();
			if (parent_id != null)
			{
				parent_elementdeclaration = element_declaration.getSiteBuilder().getGlobalElementDeclaration(parent_id);
				if (null == parent_elementdeclaration)
				{
					elementIdNotFound(parent_id);
				}
				
				// add it to the parent stack
				element_declaration.getParentStack().push(parent_elementdeclaration);
			}
		}

		// add the global parents of groups to the elements contained within
		if (mGroupDeclarations.size() > 0)
		{
			Stack<ArrayList<GroupDeclaration>>	groupdeclarations_stack = null;
			GroupDeclaration					child_groupdeclaration = null;
			ArrayList<GroupDeclaration>			child_groupdeclarations = null;
			ListIterator<GroupDeclaration>		groupdeclarations_it = mGroupDeclarations.listIterator(mGroupDeclarations.size());
			while (groupdeclarations_it.hasPrevious())
			{
				GroupDeclaration groupdeclaration = groupdeclarations_it.previous();
				
				// obtain the parent elementdeclaration
				parent_id = groupdeclaration.getInherits();
				if (parent_id != null)
				{
					parent_elementdeclaration = groupdeclaration.getDeclaringSiteBuilder().getGlobalElementDeclaration(parent_id);
					if (null == parent_elementdeclaration)
					{
						elementIdNotFound(parent_id);
					}
					
					// add it to the stack of all the elements in the group and its groups
					groupdeclarations_stack = new Stack<ArrayList<GroupDeclaration>>();
					child_groupdeclarations = new ArrayList<GroupDeclaration>();
					child_groupdeclarations.add(groupdeclaration);
					groupdeclarations_stack.push(child_groupdeclarations);
					
					// continue until the stack is empty
					while (groupdeclarations_stack.size() > 0)
					{
						// get the top of the stack and process all its groups
						child_groupdeclarations = groupdeclarations_stack.pop();
						while (child_groupdeclarations.size() > 0)
						{
							// add the global parent to all the elements within the group
							child_groupdeclaration = child_groupdeclarations.remove(0);
							for (ElementDeclaration child_elementdeclaration : child_groupdeclaration.getElementDeclarations())
							{
								child_elementdeclaration.getParentStack().push(parent_elementdeclaration);
							}
							
							// if the group contain other groups, add the collection to the stack
							if (child_groupdeclaration.getChildGroupDeclarations().size() > 0)
							{
								groupdeclarations_stack.push(new ArrayList<GroupDeclaration>(child_groupdeclaration.getChildGroupDeclarations()));
							}
						}
					}
				}
			}
		}
	}
	
	private void processPreStack()
	{
		String				pre_id = null;
		ElementDeclaration	pre_elementdeclaration = null;
		
		// get the pre elementdeclaration of the elements that inherit from another element
		for (ElementDeclaration element_declaration : getElementDeclarations())
		{
			// obtain the pre elementdeclaration
			pre_id = element_declaration.getPre();
			if (pre_id != null)
			{
				pre_elementdeclaration = element_declaration.getSiteBuilder().getGlobalElementDeclaration(pre_id);
				if (null == pre_elementdeclaration)
				{
					elementIdNotFound(pre_id);
				}
				
				// add it to the pre stack
				element_declaration.getPreStack().push(pre_elementdeclaration);
			}
		}
		
		// add the global pres of groups to the elements contained within
		if (mGroupDeclarations.size() > 0)
		{
			Stack<ArrayList<GroupDeclaration>>	groupdeclarations_stack = null;
			GroupDeclaration					child_groupdeclaration = null;
			ArrayList<GroupDeclaration>			child_groupdeclarations = null;
			ListIterator<GroupDeclaration>		groupdeclarations_it = mGroupDeclarations.listIterator(mGroupDeclarations.size());
			while (groupdeclarations_it.hasPrevious())
			{
				GroupDeclaration groupdeclaration = groupdeclarations_it.previous();
				
				// obtain the pre elementdeclaration
				pre_id = groupdeclaration.getPre();
				if (pre_id != null)
				{
					pre_elementdeclaration = groupdeclaration.getDeclaringSiteBuilder().getGlobalElementDeclaration(pre_id);
					if (null == pre_elementdeclaration)
					{
						elementIdNotFound(pre_id);
					}
					
					// add it to the stack of all the elements in the group and its groups
					groupdeclarations_stack = new Stack<ArrayList<GroupDeclaration>>();
					child_groupdeclarations = new ArrayList<GroupDeclaration>();
					child_groupdeclarations.add(groupdeclaration);
					groupdeclarations_stack.push(child_groupdeclarations);
					
					// continue until the stack is empty
					while (groupdeclarations_stack.size() > 0)
					{
						// get the top of the stack and process all its groups
						child_groupdeclarations = groupdeclarations_stack.pop();
						while (child_groupdeclarations.size() > 0)
						{
							// add the global pre to all the elements within the group
							child_groupdeclaration = child_groupdeclarations.remove(0);
							for (ElementDeclaration child_elementdeclaration : child_groupdeclaration.getElementDeclarations())
							{
								child_elementdeclaration.getPreStack().push(pre_elementdeclaration);
							}
							
							// if the group contain other groups, add the collection to the stack
							if (child_groupdeclaration.getChildGroupDeclarations().size() > 0)
							{
								groupdeclarations_stack.push(new ArrayList<GroupDeclaration>(child_groupdeclaration.getChildGroupDeclarations()));
							}
						}
					}
				}
			}
		}
	}
	
	private void createInheritanceStacks()
	{
		// create the inheritance stack of the elements of subsites first
		for (SubsiteDeclaration subsitedeclaration : mChildSubsiteDeclarations)
		{
			subsitedeclaration.getSiteBuilder().createInheritanceStacks();
		}
		
		// create the inheritance stack of all the elements in this subsite
		// construct each element's inheritance stack
		Stack<ElementInfo>				inheritance_stack = null;
		String							parent_id = null;
		
		for (ElementDeclaration current_elementdeclaration : getElementDeclarations())
		{
			if (current_elementdeclaration.getParentStack().size() > 0)
			{
				inheritance_stack = new Stack<ElementInfo>();
				
				// the element itself should be at the bottom of inheritance stack
				inheritance_stack.push(current_elementdeclaration.getElementInfo());
				
				// iterate over all the elements in the parent stack
				for (ElementDeclaration parent_elementdeclaration : current_elementdeclaration.getParentStack())
				{
					// lookup and resolve the parent hierarchy of an element in the parent stack
					while (parent_elementdeclaration != null)
					{
						inheritance_stack.push(parent_elementdeclaration.getElementInfo());
						parent_id = parent_elementdeclaration.getInherits();
						if (null == parent_id)
						{
							parent_elementdeclaration = null;
						}
						else
						{
							parent_elementdeclaration = parent_elementdeclaration.getSiteBuilder().getGlobalElementDeclaration(parent_id);
						}
					}
				}
				
				// store the inheritance stack
				current_elementdeclaration.getElementInfo().setInheritanceStack(inheritance_stack);
			}
		}
	}
	
	private void createPrecedenceStacks()
	{
		// create the precedence stack of the elements of subsites first
		for (SubsiteDeclaration subsitedeclaration : mChildSubsiteDeclarations)
		{
			subsitedeclaration.getSiteBuilder().createPrecedenceStacks();
		}
		
		// create the precedence stack of all the elements in this subsite
		// construct each element's precedence stack
		Stack<ElementInfo>	precedence_stack = null;
		
		for (ElementDeclaration current_elementdeclaration : getElementDeclarations())
		{
			if (current_elementdeclaration.getPreStack().size() > 0)
			{
				precedence_stack = new Stack<ElementInfo>();
				
				// the element itself should be at the bottom of precedence stack
				precedence_stack.push(current_elementdeclaration.getElementInfo());
				
				// iterate over all the elements in the pre stack and resolve them to element infos
				for (ElementDeclaration pre_elementdeclaration : current_elementdeclaration.getPreStack())
				{
					precedence_stack.push(pre_elementdeclaration.getElementInfo());
				}
				
				// store the precedence stack
				current_elementdeclaration.getElementInfo().setPrecedenceStack(precedence_stack);
			}
		}
	}

	private void processErrorHandlers()
	{
		// iterate over all the element declarations in the site
		for (ElementDeclaration element_declaration : getElementDeclarations())
		{
			// iterate over all error handlers of the element
			List<ErrorHandler>	error_handlers = element_declaration.getGroup().getErrorHandlersMerged();

			for (ErrorHandler error_handler : error_handlers)
			{
				ElementDeclaration	target_elementdeclaration = null;

				String	errorhandler_destid = error_handler.getDestId();

				// get the element declaration that corresponds to the destination id
				target_elementdeclaration = getGlobalElementDeclaration(errorhandler_destid);

				// if the target element couldn't be found, throw an exception
				if (null == target_elementdeclaration)
				{
					elementIdNotFound(errorhandler_destid);
				}

				error_handler.setTarget(target_elementdeclaration.getElementInfo());
			}
		}
	}

	private void setupData()
	{
		setupElements();
		setupSubsites();
		setupArrivalElement();
	}
	
	private void processData()
	{
		processSubsites();
		processGlobalExits();
		processAutoLinks();
		processFlowLinks();
		processDataLinks();
		processFallbackElement();
		processArrivalElement();
		processDepartureElements();
		processInheritsStack();
		processPreStack();
		processErrorHandlers();

		// clean the temporary data
		mArrivalElementId = null;
		mDepartureIds = null;
		mDataLinkMapping = null;
		mFlowLinkMapping = null;
		mAutoLinkMapping = null;
	}
	
	private String getUrlPrefix()
	{
		if (mUrlPrefix != null)
		{
			return mUrlPrefix;
		}
		
		if (null == mParent)
		{
			mUrlPrefix = "/";
		}
		else
		{
			String url_prefix = getParent().getUrlPrefix();
			if (mSubsiteDeclaration.getUrlPrefix() != null)
			{
				// prevent double slashes
				String subsite_url_prefix = mSubsiteDeclaration.getUrlPrefix();
				if (url_prefix.endsWith("/") &&
					subsite_url_prefix.startsWith("/"))
				{
					url_prefix = StringUtils.stripFromEnd(url_prefix, "/");
					subsite_url_prefix = StringUtils.stripFromFront(subsite_url_prefix, "/");
					StringBuilder buffer = new StringBuilder(url_prefix);
					buffer.append("/");
					buffer.append(subsite_url_prefix);
					url_prefix = buffer.toString();
				}
				else if (url_prefix.endsWith("/") || subsite_url_prefix.startsWith("/"))
				{
					url_prefix = url_prefix+subsite_url_prefix;
				}
				else
				{
					StringBuilder buffer = new StringBuilder(url_prefix);
					buffer.append("/");
					buffer.append(subsite_url_prefix);
					url_prefix = buffer.toString();
				}
			}
			
			mUrlPrefix = url_prefix;
		}
		
		return mUrlPrefix;
	}
	
	String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	void addDataLink(String srcId, DataLinkDeclaration dataLink, boolean overrideExisting)
	{
		HashSet<DataLinkDeclaration> datalinks = mDataLinkMapping.get(srcId);
		
		if (null == datalinks)
		{
			datalinks = new HashSet<DataLinkDeclaration>();
			mDataLinkMapping.put(srcId, datalinks);
		}
		
		if (overrideExisting || !datalinks.contains(dataLink))
		{
			datalinks.add(dataLink);
		}
	}
	
	void addFlowLink(String srcId, FlowLinkDeclaration flowLink)
	{
		HashSet<FlowLinkDeclaration> flowlinks = mFlowLinkMapping.get(srcId);
		
		if (null == flowlinks)
		{
			flowlinks = new HashSet<FlowLinkDeclaration>();
			mFlowLinkMapping.put(srcId, flowlinks);
		}
		
		flowlinks.add(flowLink);
	}
	
	void addAutoLink(String srcId, AutoLinkDeclaration autoLink)
	{
		HashSet<AutoLinkDeclaration> autolinks = mAutoLinkMapping.get(srcId);
		
		if (null == autolinks)
		{
			autolinks = new HashSet<AutoLinkDeclaration>();
			mAutoLinkMapping.put(srcId, autolinks);
		}
		
		autolinks.add(autoLink);
	}
	
	String ensureLocalElementId(String id)
	throws EngineException
	{
		if (id != null)
		{
			if (id.indexOf(".") != -1 ||
				id.indexOf("^") != -1)
			{
				throw new LocalElementIdRequiredException(id);
			}
		}
		
		return id;
	}
	
	String makeAbsoluteElementId(String id)
	{
		if (null == id)
		{
			return null;
		}
		
		if (!id.startsWith("."))
		{
			id = getId()+id;
		}
		
		return id;
	}
	
	static String generateId(String declarationName)
	{
		if (null == declarationName)
		{
			return null;
		}
		
		String id = FileUtils.getBaseName(declarationName);
		if (null == id)
		{
			id = declarationName;
		}
		int index = id.lastIndexOf("/");
		if (-1 == index)
		{
			index = id.lastIndexOf(":");
		}
		if (index != -1)
		{
			if (index == id.length()-1)
			{
				id = null;
			}
			else
			{
				id = id.substring(index+1);
			}
		}
		
		return id;
	}
	
	public SiteBuilder addResourceModificationTime(UrlResource resource, long modificationTime)
	{
		if (RifeConfig.Engine.getSiteAutoReload())
		{
			mSite.addResourceModificationTime(resource, modificationTime);
		}
		
		return this;
	}
}


