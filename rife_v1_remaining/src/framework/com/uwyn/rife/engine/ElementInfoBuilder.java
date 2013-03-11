/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementInfoBuilder.java 3961 2008-07-11 11:35:59Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;
import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.site.ValidatedConstrained;
import com.uwyn.rife.site.ValidationGroup;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.JavaSpecificationUtils;
import com.uwyn.rife.tools.Localization;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

import java.util.*;

public class ElementInfoBuilder
{
	private SiteBuilder								mSiteBuilder = null;
	private ResourceFinder							mResourceFinder = null;
	
	private ElementDeclaration						mDeclaration = null;
	
	private String									mProcessorIdentifier = null;
	private String									mExtendsFrom = null;
	private String									mContentType = null;
	private String									mImplementation = null;
	private Element									mImplementationBlueprint = null;
	private String									mInherits = null;
	private String									mPre = null;
	private ArrayList<DataLinkDeclaration>			mDataLinks = null;
	private ArrayList<FlowLinkDeclaration>			mFlowLinks = null;
	private ArrayList<AutoLinkDeclaration>			mAutoLinks = null;
	private LinkedHashMap<BeanDeclaration, String>	mInbeans = null;
	private LinkedHashMap<BeanDeclaration, String>	mOutbeans = null;
	private LinkedHashMap<String, PropertyValue>	mStaticProperties = null;
	private LinkedHashMap<String, String[]>			mInputs = null;
	private LinkedHashMap<String, String[]>			mOutputs = null;
	private LinkedHashMap<String, String>			mIncookies = null;
	private LinkedHashMap<String, String>			mOutcookies = null;
	private ArrayList<String>						mExits = null;
	private ArrayList<String>						mChildTriggers = null;
	private ArrayList<SubmissionBuilder>			mSubmissionBuilders = null;
	private	PathInfoMode							mPathInfoMode = null;
	private List<PathInfoMapping>					mPathInfoMappings = null;
	
	ElementInfoBuilder(SiteBuilder siteBuilder, ResourceFinder resourceFinder, ElementDeclaration declaration)
	{
		assert siteBuilder != null;
		assert resourceFinder != null;
		
		mSiteBuilder = siteBuilder;
		mResourceFinder = resourceFinder;
		mDeclaration = declaration;

		return;
	}
	
	void process()
	{
		if (JavaSpecificationUtils.isAtLeastJdk15() && null == mExtendsFrom)
		{
			// handle auto declaration generation for annotation element info processors
			if (null == mDeclaration.getDeclarationName())
			{
				if (AnnotationsElementDetector.hasElementAnnotation(mImplementation))
				{
					mDeclaration.setDeclarationName(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+mImplementation);
				}
			}
			// handle auto implementation generation for annotation element info processors
			else if (null == mImplementation &&
					 mDeclaration.getDeclarationName() != null)
			{
				String declaration = mDeclaration.getDeclarationName();
				int identifier_index = declaration.indexOf(":");
				if (identifier_index != -1)
				{
					mImplementation = declaration.substring(identifier_index+1);
				}
			}
		}

		process(mDeclaration.getDeclarationName());
	}
	
	void process(String declarationName)
	{
		if (null == declarationName)
		{
			throw new ElementDeclarationNameMissingException(mSiteBuilder.getDeclarationName(), mDeclaration.getId(), mDeclaration.getUrl(), mImplementation, null);
		}
		
		String	declaration_name_part = declarationName;
		ElementInfoProcessorFactory	processor_factory = null;
		
		String identifier = ElementInfoProcessorFactory.MANUAL_IDENTIFIER;
		int identifier_index = declarationName.indexOf(":");
		int extension_index = declarationName.lastIndexOf(".");
		if (identifier_index != -1)
		{
			identifier = declarationName.substring(0, identifier_index);
			declaration_name_part = declarationName.substring(identifier_index+1);
			
			processor_factory = ElementInfoProcessorFactory.getElementInfoProcessorFactory(identifier);
		}
		else if (extension_index != -1)
		{
			String extension = declarationName.substring(extension_index+1);
			Collection<ElementInfoProcessorFactory> factories = ElementInfoProcessorFactory.getElementInfoProcessorFactories();
			for (ElementInfoProcessorFactory factory : factories)
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
				throw new ElementInfoProcessorExtensionUnsupportedException(declarationName, extension);
			}
		}
		else
		{
			processor_factory = ElementInfoProcessorFactory.getElementInfoProcessorFactory(identifier);
		}
		
		if (null == processor_factory)
		{
			throw new ElementInfoProcessorIdentifierUnsupportedException(declarationName, identifier);
		}
		
		mProcessorIdentifier = processor_factory.getIdentifier();
		
		// process the element definition
		ElementInfoProcessor	processor = processor_factory.getProcessor();
		if (processor != null)
		{
			processor.processElementInfo(this, declaration_name_part, mResourceFinder);
		}
	}
	
	ElementInfo createElementInfo(Map<String, GlobalExit> globalExits, Map<String, GlobalVar> globalVars, Map<String, String> globalCookies, Map<String, BeanDeclaration> namedGlobalBeans, List<ErrorHandler> errorsHandlers)
	{
		ElementInfo	element_info = new ElementInfo();
		
		element_info.setProcessorIdentifier(mProcessorIdentifier);

		if (mDeclaration != null)
		{
			element_info.setDeclarationName(mDeclaration.getDeclarationName());
		}
		
		element_info.setGlobalExits(globalExits);
		element_info.setGlobalVars(globalVars);
		element_info.setGlobalCookies(globalCookies);
		element_info.setNamedGlobalBeans(namedGlobalBeans);
		element_info.setErrorHandlers(errorsHandlers);
		
		if (mExtendsFrom != null)
		{
			ElementInfo extended_element_info = null;

			// try to create an absolute element ID from the extension source
			String extends_id = mSiteBuilder.makeAbsoluteElementId(mExtendsFrom);
			ElementDeclaration extends_declaration = mSiteBuilder.getGlobalElementDeclaration(extends_id);

			// handle extension from an existing element declaration
			if (extends_declaration != null)
			{
				mDeclaration.getProperties().putAllWithoutReplacing(extends_declaration.getProperties());
				extended_element_info = extends_declaration.getElementInfo();
			}
			// handle extension from declared elements in the site
			else
			{
				ElementInfoBuilder builder = new ElementInfoBuilder(mSiteBuilder, mResourceFinder, null);
				builder.process(mExtendsFrom);
				extended_element_info = builder.createElementInfo(globalExits, globalVars, globalCookies, namedGlobalBeans, errorsHandlers);
			}

			// instantiate the current element info by taking all the properties from the class it extends from
			element_info.extendFrom(extended_element_info);
		}
		
		element_info.setContentType(mContentType);
		
		if (mImplementation != null)
		{
			element_info.setImplementation(mImplementation, mImplementationBlueprint);
		}
		
		if (mInherits != null)
		{
			mInherits = mSiteBuilder.makeAbsoluteElementId(mInherits);
			mInherits = Site.getCanonicalId(mInherits);
			
			mDeclaration.setInherits(mInherits);
		}
		
		if (mPre != null)
		{
			mPre = mSiteBuilder.makeAbsoluteElementId(mPre);
			mPre = Site.getCanonicalId(mPre);
			
			mDeclaration.setPre(mPre);
		}
		
		registerFlowAndDataLinksInSite(null);
		
		if (mAutoLinks != null)
		{
			for (AutoLinkDeclaration autolink : mAutoLinks)
			{
				autolink.makeAbsoluteDestId(mSiteBuilder);
				mSiteBuilder.addAutoLink(mDeclaration.getId(), autolink);
			}
		}
		
		if (mInbeans != null)
		{
			String name = null;
			BeanDeclaration declaration = null;
			for (Map.Entry<BeanDeclaration, String> inbean : mInbeans.entrySet())
			{
				name = inbean.getValue();
				declaration = inbean.getKey();
				try
				{
					Class bean_class = declaration.getBeanClass();
					
					if (name != null)
					{
						element_info.addNamedInbean(name, declaration);
					}
					
					try
					{
						Object		instance = bean_class.newInstance();
						Constrained	constrained = ConstrainedUtils.makeConstrainedInstance(instance);
						Set<String>	properties;
						if (declaration.getGroupName() != null)
						{
							if (!(instance instanceof ValidatedConstrained))
							{
								throw new InbeanGroupRequiresValidatedConstrainedException(mDeclaration.getDeclarationName(), declaration.getClassname(), declaration.getGroupName());
							}
							
							ValidatedConstrained validation = (ValidatedConstrained)instance;
							ValidationGroup group = validation.getGroup(declaration.getGroupName());
							if (null == group)
							{
								throw new InbeanGroupNotFoundException(mDeclaration.getDeclarationName(), declaration.getClassname(), declaration.getGroupName());
							}
							properties = new TreeSet<String>();
							if (null == declaration.getPrefix())
							{
								properties.addAll(group.getPropertyNames());
							}
							else
							{
								for (String property_name : (List<String>)group.getPropertyNames())
								{
									properties.add(declaration.getPrefix()+property_name);
								}
							}
						}
						else
						{
							properties = BeanUtils.getPropertyNames(bean_class, null, null, declaration.getPrefix());
						}

						for (String property : properties)
						{
							if (ConstrainedUtils.editConstrainedProperty(constrained, property, declaration.getPrefix()) &&
								!element_info.containsInput(property))
							{
								element_info.addInput(property, null);
							}
						}
					}
					catch (IllegalAccessException e)
					{
						throw new InbeanPropertiesCouldntBeRetrievedException(mDeclaration.getDeclarationName(), declaration.getClassname(), e);
					}
					catch (InstantiationException e)
					{
						throw new InbeanPropertiesCouldntBeRetrievedException(mDeclaration.getDeclarationName(), declaration.getClassname(), e);
					}
					catch (BeanUtilsException e)
					{
						throw new InbeanPropertiesCouldntBeRetrievedException(mDeclaration.getDeclarationName(), declaration.getClassname(), e);
					}
				}
				catch (ClassNotFoundException e)
				{
					throw new InbeanClassNotFoundException(mDeclaration.getDeclarationName(), declaration.getClassname());
				}
			}
		}
		
		if (mOutbeans != null)
		{
			String name = null;
			BeanDeclaration declaration = null;
			for (Map.Entry<BeanDeclaration, String> outbean : mOutbeans.entrySet())
			{
				name = outbean.getValue();
				declaration = outbean.getKey();
				try
				{
					Class bean_class = declaration.getBeanClass();
					
					if (name != null)
					{
						element_info.addNamedOutbean(name, declaration);
					}
					
					try
					{
						Object		instance = bean_class.newInstance();
						Constrained	constrained = ConstrainedUtils.makeConstrainedInstance(instance);
						Set<String>	properties;
						if (declaration.getGroupName() != null)
						{
							if (!(instance instanceof ValidatedConstrained))
							{
								throw new OutbeanGroupRequiresValidatedConstrainedException(mDeclaration.getDeclarationName(), declaration.getClassname(), declaration.getGroupName());
							}
							
							ValidatedConstrained validation = (ValidatedConstrained)instance;
							ValidationGroup group = validation.getGroup(declaration.getGroupName());
							if (null == group)
							{
								throw new OutbeanGroupNotFoundException(mDeclaration.getDeclarationName(), declaration.getClassname(), declaration.getGroupName());
							}
							properties = new TreeSet<String>();
							if (null == declaration.getPrefix())
							{
								properties.addAll(group.getPropertyNames());
							}
							else
							{
								for (String property_name : (List<String>)group.getPropertyNames())
								{
									properties.add(declaration.getPrefix()+property_name);
								}
							}
						}
						else
						{
							properties = BeanUtils.getPropertyNames(bean_class, null, null, declaration.getPrefix());
						}
						
						for (String property : properties)
						{
							if (ConstrainedUtils.editConstrainedProperty(constrained, property, declaration.getPrefix()) &&
								!element_info.containsOutput(property))
							{
								element_info.addOutput(property, null);
							}
						}
					}
					catch (IllegalAccessException e)
					{
						throw new OutbeanPropertiesCouldntBeRetrievedException(mDeclaration.getDeclarationName(), declaration.getClassname(), e);
					}
					catch (InstantiationException e)
					{
						throw new OutbeanPropertiesCouldntBeRetrievedException(mDeclaration.getDeclarationName(), declaration.getClassname(), e);
					}
					catch (BeanUtilsException e)
					{
						throw new OutbeanPropertiesCouldntBeRetrievedException(mDeclaration.getDeclarationName(), declaration.getClassname(), e);
					}
				}
				catch (ClassNotFoundException e)
				{
					throw new OutbeanClassNotFoundException(mDeclaration.getDeclarationName(), declaration.getClassname());
				}
			}
		}
		
		if (mStaticProperties != null)
		{
			for (Map.Entry<String, PropertyValue> property : mStaticProperties.entrySet())
			{
				element_info.addStaticProperty(property.getKey(), property.getValue());
			}
		}
		
		if (mInputs != null)
		{
			for (Map.Entry<String, String[]> input : mInputs.entrySet())
			{
				element_info.addInput(input.getKey(), input.getValue());
			}
		}
		
		if (mOutputs != null)
		{
			for (Map.Entry<String, String[]> output : mOutputs.entrySet())
			{
				element_info.addOutput(output.getKey(), output.getValue());
			}
		}
		
		if (mIncookies != null)
		{
			for (Map.Entry<String, String> incookie : mIncookies.entrySet())
			{
				element_info.addIncookie(incookie.getKey(), incookie.getValue());
			}
		}
		
		if (mOutcookies != null)
		{
			for (Map.Entry<String, String> outcookie : mOutcookies.entrySet())
			{
				element_info.addOutcookie(outcookie.getKey(), outcookie.getValue());
			}
		}
		
		if (mExits != null)
		{
			for (String exit : mExits)
			{
				element_info.addExit(exit);
			}
		}
		
		if (mChildTriggers != null)
		{
			for (String name : mChildTriggers)
			{
				element_info.addChildTrigger(name);
			}
		}
		
		if (mSubmissionBuilders != null)
		{
			for (SubmissionBuilder builder : mSubmissionBuilders)
			{
				builder.getSubmission(element_info);
			}
		}
		
		element_info.setPathInfoMode(mPathInfoMode);
		if (mPathInfoMappings != null)
		{
			element_info.setPathInfoMappings(mPathInfoMappings);
			
			for (PathInfoMapping mapping : mPathInfoMappings)
			{
				for (String input : mapping.getInputs())
				{
					if (!element_info.containsInputPossibility(input))
					{
						element_info.addInput(input, null);
					}
				}
			}
		}
		
		return element_info;
	}

	void registerFlowAndDataLinksInSite(FlowLinkDeclaration focusedFlowlinkDeclaration)
	{
		if (mDataLinks != null)
		{
			for (DataLinkDeclaration datalink_declaration : mDataLinks)
			{
				if (focusedFlowlinkDeclaration != null && datalink_declaration.getFlowLinkDeclaration() != focusedFlowlinkDeclaration)
				{
					continue;
				}
				
				datalink_declaration.makeAbsoluteDestId(mSiteBuilder);
				mSiteBuilder.addDataLink(mDeclaration.getId(), datalink_declaration, true);
			}
		}
		
		if (mFlowLinks != null)
		{
			for (FlowLinkDeclaration flowlink_declaration : mFlowLinks)
			{
				if (focusedFlowlinkDeclaration != null && flowlink_declaration != focusedFlowlinkDeclaration)
				{
					continue;
				}
				flowlink_declaration.makeAbsoluteDestId(mSiteBuilder);
				mSiteBuilder.addFlowLink(mDeclaration.getId(), flowlink_declaration);
			}
		}
	}
	
	public ElementInfoBuilder setId(String id)
	throws EngineException
	{
		if (id != null &&
			0 == id.length())
		{
			throw new ElementIdInvalidException(id);
		}
		
		mDeclaration.setId(id);
		
		return this;
	}
	
	public ElementInfoBuilder extendsFrom(String extendsFrom)
	{
		mExtendsFrom = extendsFrom;
		
		return this;
	}
	
	public ElementInfoBuilder setContentType(String contentType)
	{
		if (null == contentType)
		{
			return this;
		}
		
		mContentType = contentType;
		
		return this;
	}
	
	public ElementInfoBuilder setImplementation(Class klass)
	{
		if (null == klass)
		{
			return setImplementation((String)null);
		}
				
		return setImplementation(klass.getName());
	}
		
	public ElementInfoBuilder setImplementation(String implementation)
	{
		mImplementation = implementation;
		
		return this;
	}
	
	public ElementInfoBuilder setImplementation(Element blueprint)
	{
		if (null == blueprint)
		{
			mImplementation = null;
			mImplementationBlueprint = null;
		}
		else
		{
			mImplementation = blueprint.getClass().getName();
			mImplementationBlueprint = blueprint;
		}
		
		return this;
	}
	
	public String getImplementation()
	{
		return mImplementation;
	}
		
	public ElementInfoBuilder setUrl(String url)
	{
		if (url != null)
		{
			mDeclaration.setUrl(Localization.extractLocalizedUrl(url));
		}
		
		return this;
	}
		
	public ElementInfoBuilder setInherits(String inherits)
	{
		mInherits = inherits;
		
		return this;
	}
		
	public ElementInfoBuilder setPre(String pre)
	{
		mPre = pre;
		
		return this;
	}
	
	public ElementInfoBuilder addProperty(String name, PropertyValue value)
	throws EngineException
	{
		mDeclaration.addProperty(name, value);

		return this;
	}
	
	public ElementInfoBuilder addProperty(String name, Object value)
	throws EngineException
	{
		return addProperty(name, new PropertyValueObject(value));
	}
	
	public ElementInfoBuilder addDataLink(String srcOutput, String destId, String destInput)
	throws EngineException
	{
		addDataLink(srcOutput, null, destId, false, destInput, null);

		return this;
	}
	
	public ElementInfoBuilder addSnapbackDataLink(String srcOutput, String destInput)
	throws EngineException
	{
		addDataLink(srcOutput, null, null, true, destInput, null);

		return this;
	}
	
	public ElementInfoBuilder addDataLinkBean(String srcOutbean, String destId, String destInbean)
	throws EngineException
	{
		addDataLink(null, srcOutbean, destId, false, null, destInbean);

		return this;
	}
	
	public ElementInfoBuilder addSnapbackDataLinkBean(String srcOutbean, String destInbean)
	throws EngineException
	{
		addDataLink(null, srcOutbean, null, true, null, destInbean);

		return this;
	}
	
	public ElementInfoBuilder addDataLink(String srcOutput, String srcOutbean, String destId, boolean snapback, String destInput, String destInbean)
	throws EngineException
	{
		addDataLink(srcOutput, srcOutbean, destId, snapback, destInput, destInbean, null);
		
		return this;
	}
	
	void addDataLink(String srcOutput, String srcOutbean, String destId, boolean snapback, String destInput, String destInbean, FlowLinkBuilder flowlink)
	throws EngineException
	{
		if (srcOutput != null && 0 == srcOutput.length()) srcOutput = null;
		if (srcOutbean != null && 0 == srcOutbean.length()) srcOutbean = null;
		if (destId != null && 0 == destId.length()) destId = null;
		if (destInput != null && 0 == destInput.length()) destInput = null;
		if (destInbean != null && 0 == destInbean.length()) destInbean = null;

		if (null == srcOutput &&
			null == srcOutbean)
		{
			throw new DataLinkOutputRequiredException(mSiteBuilder.getDeclarationName(), mDeclaration.getId());
		}
		if (srcOutput != null &&
			srcOutbean != null)
		{
			throw new DataLinkAmbiguousOutputException(mSiteBuilder.getDeclarationName(), mDeclaration.getId());
		}
		
		if (null == destId &&
			!snapback)
		{
			throw new DataLinkTargetRequiredException(mSiteBuilder.getDeclarationName(), mDeclaration.getId(), srcOutput, srcOutbean, destInput, destInbean);
		}
		if (destId != null &&
			snapback)
		{
			throw new DataLinkAmbiguousTargetException(mSiteBuilder.getDeclarationName(), mDeclaration.getId(), srcOutput, srcOutbean, destInput, destInbean);
		}

		if (null == destInput &&
			null == destInbean)
		{
			throw new DataLinkInputRequiredException(mSiteBuilder.getDeclarationName(), mDeclaration.getId(), destId, snapback);
		}
		if (destInput != null &&
			destInbean != null)
		{
			throw new DataLinkAmbiguousInputException(mSiteBuilder.getDeclarationName(), mDeclaration.getId(), destId, snapback);
		}
		
		if ((srcOutput != null && destInbean != null) ||
			(srcOutbean != null && destInput != null))
		{
			throw new DataLinkIncompatibleInputOutputException(mSiteBuilder.getDeclarationName(), mDeclaration.getId(), destId, snapback);
		}
		
		if (null == mDataLinks)
		{
			mDataLinks = new ArrayList<DataLinkDeclaration>();
		}
		mDataLinks.add(new DataLinkDeclaration(srcOutput, srcOutbean, destId, snapback, destInput, destInbean, flowlink));
	}
	
	public FlowLinkBuilder enterFlowLink(String srcExit)
	throws EngineException
	{
		FlowLinkBuilder builder = new FlowLinkBuilder(this, srcExit);
		
		return builder;
	}
	
	public ElementInfoBuilder addFlowLink(String srcExit, String destId)
	throws EngineException
	{
		addFlowLink(srcExit, destId, false, false, false, false);

		return this;
	}
	
	public ElementInfoBuilder addFlowLink(String srcExit, String destId, boolean cancelInheritance)
	throws EngineException
	{
		addFlowLink(srcExit, destId, false, cancelInheritance, false, false);

		return this;
	}
	
	public ElementInfoBuilder addFlowLink(String srcExit, String destId, boolean cancelInheritance, boolean cancelEmbedding)
	throws EngineException
	{
		addFlowLink(srcExit, destId, false, cancelInheritance, cancelEmbedding, false);

		return this;
	}
	
	public ElementInfoBuilder addSnapbackFlowLink(String srcExit, boolean cancelInheritance)
	throws EngineException
	{
		addFlowLink(srcExit, null, true, cancelInheritance, false, false);

		return this;
	}
	
	public ElementInfoBuilder addRedirectFlowLink(String srcExit, String destId)
	throws EngineException
	{
		addFlowLink(srcExit, destId, false, false, false, true);

		return this;
	}
	
	public ElementInfoBuilder addFlowLink(String srcExit, String destId, boolean snapback, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect)
	throws EngineException
	{
		return enterFlowLink(srcExit)
				.destId(destId)
				.snapback(snapback)
				.cancelInheritance(cancelInheritance)
				.cancelEmbedding(cancelEmbedding)
				.redirect(redirect)
			.leaveFlowLink();
	}
	
	public ElementInfoBuilder addAutoLink(String srcExit)
	throws EngineException
	{
		addAutoLink(srcExit, null, false, false, false, false);
		
		return this;
	}
	
	public ElementInfoBuilder addAutoLink(String srcExit, String destId)
	throws EngineException
	{
		addAutoLink(srcExit, destId, false, false, false, false);
		
		return this;
	}
	
	public ElementInfoBuilder addRedirectAutoLink(String srcExit)
	throws EngineException
	{
		addAutoLink(srcExit, null, false, false, true, false);
		
		return this;
	}
	
	public ElementInfoBuilder addRedirectAutoLink(String srcExit, String destId)
	throws EngineException
	{
		addAutoLink(srcExit, destId, false, false, true, false);
		
		return this;
	}
	
	public ElementInfoBuilder addAutoLink(String srcExit, String destId, boolean cancelInheritance, boolean cancelEmbedding, boolean redirect, boolean cancelContinuations)
	throws EngineException
	{
		addAutoLinkDeclaration(new AutoLinkDeclaration(this, srcExit, destId, cancelInheritance, cancelEmbedding, redirect, cancelContinuations));
		return this;
	}
	
	void addFlowLinkDeclaration(FlowLinkDeclaration declaration)
	{
		if (null == mFlowLinks)
		{
			mFlowLinks = new ArrayList<FlowLinkDeclaration>();
		}
		mFlowLinks.add(declaration);
	}
	
	void addAutoLinkDeclaration(AutoLinkDeclaration declaration)
	{
		if (null == mAutoLinks)
		{
			mAutoLinks = new ArrayList<AutoLinkDeclaration>();
		}
		mAutoLinks.add(declaration);
	}
	
	public ElementInfoBuilder addInBean(String classname)
	{
		return addInBean(classname, null, null, null);
	}
	
	public ElementInfoBuilder addInBean(String classname, String prefix)
	{
		return addInBean(classname, prefix, null, null);
	}
	
	public ElementInfoBuilder addInBean(String classname, String prefix, String name)
	{
		return addInBean(classname, prefix, name, null);
	}
	
	public ElementInfoBuilder addInBean(String classname, String prefix, String name, String groupName)
	{
		BeanDeclaration bean_declaration = new BeanDeclaration(classname, prefix, groupName);
		
		return addInBean(bean_declaration, name);
	}
	
	public ElementInfoBuilder addInBean(Class klass)
	{
		return addInBean(klass, null, null, null);
	}
	
	public ElementInfoBuilder addInBean(Class klass, String prefix)
	{
		return addInBean(klass, prefix, null, null);
	}
	
	public ElementInfoBuilder addInBean(Class klass, String prefix, String name)
	{
		return addInBean(klass, prefix, name, null);
	}
	
	public ElementInfoBuilder addInBean(Class klass, String prefix, String name, String groupName)
	{
		if (prefix != null && 0 == prefix.length())			prefix = null;
		if (name != null && 0 == name.length())				name = null;
		if (groupName != null && 0 == groupName.length())	groupName = null;

		BeanDeclaration bean_declaration = new BeanDeclaration(klass, prefix, groupName);
		
		return addInBean(bean_declaration, name);
	}
	
	private ElementInfoBuilder addInBean(BeanDeclaration beanDeclaration, String name)
	{
		if (null == mInbeans)
		{
			mInbeans = new LinkedHashMap<BeanDeclaration, String>();
		}
		
		mInbeans.put(beanDeclaration, name);
		
		return this;
	}
	
	public ElementInfoBuilder addOutBean(String classname)
	{
		return addOutBean(classname, null, null, null);
	}
	
	public ElementInfoBuilder addOutBean(String classname, String prefix)
	{
		return addOutBean(classname, prefix, null, null);
	}
	
	public ElementInfoBuilder addOutBean(String classname, String prefix, String name)
	{
		return addOutBean(classname, prefix, name, null);
	}
	
	public ElementInfoBuilder addOutBean(String classname, String prefix, String name, String groupName)
	{
		BeanDeclaration bean_declaration = new BeanDeclaration(classname, prefix, groupName);
		
		return addOutBean(bean_declaration, name);
	}
	
	public ElementInfoBuilder addOutBean(Class klass)
	{
		return addOutBean(klass, null, null, null);
	}
	
	public ElementInfoBuilder addOutBean(Class klass, String prefix)
	{
		return addOutBean(klass, prefix, null, null);
	}
	
	public ElementInfoBuilder addOutBean(Class klass, String prefix, String name)
	{
		return addOutBean(klass, prefix, name, null);
	}
	
	public ElementInfoBuilder addOutBean(Class klass, String prefix, String name, String groupName)
	{
		if (prefix != null && 0 == prefix.length())			prefix = null;
		if (name != null && 0 == name.length())				name = null;
		if (groupName != null && 0 == groupName.length())	groupName = null;

		BeanDeclaration bean_declaration = new BeanDeclaration(klass, prefix, groupName);
		
		return addOutBean(bean_declaration, name);
	}
	
	private ElementInfoBuilder addOutBean(BeanDeclaration beanDeclaration, String name)
	{
		if (null == mOutbeans)
		{
			mOutbeans = new LinkedHashMap<BeanDeclaration, String>();
		}
		
		mOutbeans.put(beanDeclaration, name);
		
		return this;
	}
	
	public ElementInfoBuilder addStaticProperty(String name, Object value)
	throws EngineException
	{
		return addStaticProperty(name, new PropertyValueObject(value));
	}
	
	public ElementInfoBuilder addStaticProperty(String name, PropertyValue value)
	{
		if (null == mStaticProperties)
		{
			mStaticProperties = new LinkedHashMap<String, PropertyValue>();
		}
		
		mStaticProperties.put(name, value);
		
		return this;
	}
	
	public ElementInfoBuilder addInput(String name)
	{
		return addInput(name, null);
	}
	
	public ElementInfoBuilder addInput(String name, String[] defaultValues)
	{
		if (null == mInputs)
		{
			mInputs = new LinkedHashMap<String, String[]>();
		}

		mInputs.put(name, defaultValues);
		
		return this;
	}
	
	public ElementInfoBuilder addOutput(String name)
	{
		return addOutput(name, null);
	}
	
	public ElementInfoBuilder addOutput(String name, String[] defaultValues)
	{
		if (null == mOutputs)
		{
			mOutputs = new LinkedHashMap<String, String[]>();
		}

		mOutputs.put(name, defaultValues);
		
		return this;
	}
	
	public ElementInfoBuilder addIncookie(String name)
	{
		return addIncookie(name, null);
	}
	
	public ElementInfoBuilder addIncookie(String name, String defaultValue)
	{
		if (null == mIncookies)
		{
			mIncookies = new LinkedHashMap<String, String>();
		}

		mIncookies.put(name, defaultValue);
		
		return this;
	}
	
	public ElementInfoBuilder addOutcookie(String name)
	{
		return addOutcookie(name, null);
	}
	
	public ElementInfoBuilder addOutcookie(String name, String defaultValue)
	{
		if (null == mOutcookies)
		{
			mOutcookies = new LinkedHashMap<String, String>();
		}
		
		mOutcookies.put(name, defaultValue);
		
		return this;
	}
	
	public ElementInfoBuilder addExit(String name)
	{
		if (null == mExits)
		{
			mExits = new ArrayList<String>();
		}
		
		mExits.add(name);
		
		return this;
	}
	
	public ElementInfoBuilder addChildTrigger(String name)
	{
		if (null == mChildTriggers)
		{
			mChildTriggers = new ArrayList<String>();
		}
		
		mChildTriggers.add(name);
		
		return this;
	}
	
	public SubmissionBuilder enterSubmission(String name)
	{
		if (null == mSubmissionBuilders)
		{
			mSubmissionBuilders = new ArrayList<SubmissionBuilder>();
		}

		SubmissionBuilder builder = new SubmissionBuilder(this, name);
		mSubmissionBuilders.add(builder);
		
		return builder;
	}
	
	public ElementInfoBuilder addResourceModificationTime(UrlResource resource, long modificationTime)
	{
		mSiteBuilder.addResourceModificationTime(resource, modificationTime);
		
		return this;
	}
	
	public SiteBuilder leaveElement()
	{
		return mSiteBuilder;
	}
	
	PathInfoMode getPathInfoMode() {
		return mPathInfoMode;
	}
	
	public ElementInfoBuilder setPathInfoMode(PathInfoMode mode)
	{
		mPathInfoMode = mode;
		
		return this;
	}
	
	public ElementInfoBuilder addPathInfoMapping(String specification)
	{
		PathInfoMapping mapping = PathInfoMapping.create(specification);
		if (null == mapping)
		{
			return this;
		}
		
		if (null == mPathInfoMappings)
		{
			mPathInfoMappings = new ArrayList<PathInfoMapping>();
		}
		
		mPathInfoMappings.add(mapping);

		return this;
	}
	
	SiteBuilder getSiteBuilder()
	{
		return mSiteBuilder;
	}
	
	ElementDeclaration getElementDeclaration()
	{
		return mDeclaration;
	}
}
