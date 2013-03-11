/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementInfo.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.*;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.ExceptionUtils;

import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElementInfo implements Cloneable
{
	public static final boolean	DEFAULT_BOOLEAN = false;
	public static final int		DEFAULT_INTEGER = 0;
	public static final long	DEFAULT_LONG = 0l;
	public static final double	DEFAULT_DOUBLE = 0.0d;
	public static final float	DEFAULT_FLOAT = 0.0f;

	private Site								mSite = null;
	private String								mUrl = null;
	private String								mProcessorIdentifier = null;
	private String								mId = null;
	private String								mReferenceId = null;
	private int									mGroupId = -1;
	private StateStore							mStateStore = null;
	private	boolean								mPathInfoUsed = false;
	private String								mDeclarationName = null;
	private String								mImplementation = null;
	private Element								mImplementationBlueprint = null;
	private ElementType							mType = null;
	private String								mContentType = null;
	private HierarchicalProperties				mStaticProperties = null;
	private HierarchicalProperties				mProperties = null;
	private Map<String, Submission>				mSubmissions = null;
	private Map<String, String[]>				mInputs = null;
	private Map<String, String[]>				mOutputs = null;
	private Map<String, String>					mIncookies = null;
	private Map<String, String>					mOutcookies = null;
	private Map<String, BeanDeclaration>		mNamedInbeans = null;
	private Map<String, BeanDeclaration>		mNamedOutbeans = null;
	private boolean								mHasSubmissionDefaults = false;
	private boolean								mHasGlobalVarDefaults = false;
	private boolean 							mHasGlobalCookieDefaults = false;
	private boolean								mHasInputDefaults = false;
	private boolean								mHasOutputDefaults = false;
	private boolean								mHasIncookieDefaults = false;
	private boolean								mHasOutcookieDefaults = false;
	private boolean								mHasSnapbackFlowLinks = false;
	private boolean								mHasSnapbackDataLinks = false;
	private List<String>						mChildTriggers = null;
	private Map<String, FlowLink>				mExits = null;
	private Map<ElementInfo, List<DataLink>>	mDatalinks = null;
	private List<DataLink>						mSnapbackDatalinks = null;
	private Stack<ElementInfo>					mInheritanceStack = null;
	private Stack<ElementInfo>					mPrecedenceStack = null;
	private Map<String, GlobalExit>				mGlobalExits = null;
	private Map<String, GlobalVar>				mGlobalVars = null;
	private Map<String, String>					mGlobalCookies = null;
	private Map<String, BeanDeclaration>		mNamedGlobalBeans = null;
	private List<String>						mDepartureVars = null;
	private boolean								mHasDepartureVars = false;
	private ElementDeployer						mDeployer = null;
	private	PathInfoMode						mPathInfoMode = PathInfoMode.LOOSE;
	private List<PathInfoMapping>				mPathInfoMappings = null;
	private List<ErrorHandler>					mErrorHandlers = null;
	
	private static NumberFormat	MEMORY_FORMAT = null;

	static
	{
		MEMORY_FORMAT = NumberFormat.getIntegerInstance();
		MEMORY_FORMAT.setMinimumIntegerDigits(8);
		MEMORY_FORMAT.setMaximumIntegerDigits(8);
		MEMORY_FORMAT.setGroupingUsed(false);
	}

	ElementInfo()
	throws EngineException
	{
		mStaticProperties = new HierarchicalProperties().parent(Rep.getProperties());
		mSubmissions = new LinkedHashMap<String, Submission>();
		mInputs = new LinkedHashMap<String, String[]>();
		mOutputs = new LinkedHashMap<String, String[]>();
		mIncookies = new LinkedHashMap<String, String>();
		mOutcookies = new LinkedHashMap<String, String>();
		mNamedInbeans = new LinkedHashMap<String, BeanDeclaration>();
		mNamedOutbeans = new LinkedHashMap<String, BeanDeclaration>();
		mChildTriggers = new ArrayList<String>();
		mExits = new LinkedHashMap<String, FlowLink>();
		mDatalinks = new LinkedHashMap<ElementInfo, List<DataLink>>();
		mSnapbackDatalinks = new ArrayList<DataLink>();
		mPathInfoMappings = new ArrayList<PathInfoMapping>();
	}

	ElementInfo(String declarationName, String contentType, String implementation, ElementType type)
	throws EngineException
	{
		this();

		setDeclarationName(declarationName);
		setContentType(contentType);
		setImplementation(implementation);
		setType(type);
	}

	void extendFrom(ElementInfo extendedElement)
	{
		mProcessorIdentifier = extendedElement.mProcessorIdentifier;
		mImplementation = extendedElement.mImplementation;
		mType = extendedElement.mType;
		mContentType = extendedElement.mContentType;
		mStaticProperties = new HierarchicalProperties().putAll(extendedElement.mStaticProperties).parent(Rep.getProperties());
		mSubmissions = new LinkedHashMap<String, Submission>(extendedElement.mSubmissions);
		mInputs = new LinkedHashMap<String, String[]>(extendedElement.mInputs);
		mOutputs = new LinkedHashMap<String, String[]>(extendedElement.mOutputs);
		mIncookies = new LinkedHashMap<String, String>(extendedElement.mIncookies);
		mOutcookies = new LinkedHashMap<String, String>(extendedElement.mOutcookies);
		mNamedInbeans = new LinkedHashMap<String, BeanDeclaration>(extendedElement.mNamedInbeans);
		mNamedOutbeans = new LinkedHashMap<String, BeanDeclaration>(extendedElement.mNamedOutbeans);
		mHasSubmissionDefaults = extendedElement.mHasSubmissionDefaults;
		mHasInputDefaults = extendedElement.mHasInputDefaults;
		mHasOutputDefaults = extendedElement.mHasOutputDefaults;
		mHasIncookieDefaults = extendedElement.mHasIncookieDefaults;
		mHasOutcookieDefaults = extendedElement.mHasOutcookieDefaults;
		mHasSnapbackFlowLinks = false;
		mHasSnapbackDataLinks = false;
		mChildTriggers = new ArrayList<String>(extendedElement.mChildTriggers);
		mExits = new LinkedHashMap<String, FlowLink>(extendedElement.mExits);
		mDatalinks = new LinkedHashMap<ElementInfo, List<DataLink>>();
		mSnapbackDatalinks = new ArrayList<DataLink>();
		mPathInfoMode = extendedElement.mPathInfoMode;
		mPathInfoMappings = new ArrayList<PathInfoMapping>(extendedElement.mPathInfoMappings);

		// clear flowlinks
		for (String exit : mExits.keySet())
		{
			mExits.put(exit, null);
		}
	}

	void populateFrom(ElementInfo referenceElement)
	{
		mSite = referenceElement.mSite;
		mDeclarationName = referenceElement.mDeclarationName;
		mProcessorIdentifier = referenceElement.mProcessorIdentifier;
		mImplementation = referenceElement.mImplementation;
		mType = referenceElement.mType;
		mUrl = referenceElement.mUrl;
		mId = referenceElement.mId;
		mReferenceId = referenceElement.getReferenceId();
		mGroupId = referenceElement.mGroupId;
		mStateStore = referenceElement.mStateStore;
		mPathInfoUsed = referenceElement.mPathInfoUsed;
		mContentType = referenceElement.mContentType;
		mStaticProperties = referenceElement.mStaticProperties;
		mSubmissions = referenceElement.mSubmissions;
		mInputs = referenceElement.mInputs;
		mOutputs = referenceElement.mOutputs;
		mIncookies = referenceElement.mIncookies;
		mOutcookies = referenceElement.mOutcookies;
		mNamedInbeans = referenceElement.mNamedInbeans;
		mNamedOutbeans = referenceElement.mNamedOutbeans;
		mHasSubmissionDefaults = referenceElement.mHasSubmissionDefaults;
		mHasGlobalVarDefaults = referenceElement.mHasGlobalVarDefaults;
		mHasGlobalCookieDefaults = referenceElement.mHasGlobalCookieDefaults;
		mHasInputDefaults = referenceElement.mHasInputDefaults;
		mHasOutputDefaults = referenceElement.mHasOutputDefaults;
		mHasIncookieDefaults = referenceElement.mHasIncookieDefaults;
		mHasOutcookieDefaults = referenceElement.mHasOutcookieDefaults;
		mHasSnapbackFlowLinks = referenceElement.mHasSnapbackFlowLinks;
		mHasSnapbackDataLinks = referenceElement.mHasSnapbackDataLinks;
		mChildTriggers = referenceElement.mChildTriggers;
		mSnapbackDatalinks = referenceElement.mSnapbackDatalinks;
		mInheritanceStack = referenceElement.mInheritanceStack;
		mGlobalExits = referenceElement.mGlobalExits;
		mGlobalVars = referenceElement.mGlobalVars;
		mGlobalCookies = referenceElement.mGlobalCookies;
		mNamedGlobalBeans = referenceElement.mNamedGlobalBeans;
		mDepartureVars = referenceElement.mDepartureVars;
		mHasDepartureVars = referenceElement.mHasDepartureVars;
		mDeployer = referenceElement.mDeployer;
		mPathInfoMode = referenceElement.mPathInfoMode;
		mPathInfoMappings = referenceElement.mPathInfoMappings;
		mErrorHandlers = referenceElement.mErrorHandlers;

		// Go over all the datalinks and flowlinks and check if their targets
		// don't point to the reference element itself.
		// If that is the case, they have to be adapted to point to the one
		// that's being populated instead.
		mExits = new LinkedHashMap<String, FlowLink>(referenceElement.mExits);
		Map<FlowLink, FlowLink> replaced_flowlinks = new HashMap<FlowLink, FlowLink>();
		for (Map.Entry<String, FlowLink> exits_entry : mExits.entrySet())
		{
			FlowLink flowlink = exits_entry.getValue();
			if (flowlink != null &&
				flowlink.getTarget() == referenceElement)
			{
				FlowLink new_flowlink = new FlowLink(flowlink.getExitName(), this, flowlink.isSnapback(), flowlink.cancelInheritance(), flowlink.cancelEmbedding(), flowlink.isRedirect(), flowlink.cancelContinuations());
				mExits.put(exits_entry.getKey(), new_flowlink);
				replaced_flowlinks.put(flowlink, new_flowlink);
			}
		}
		
		mDatalinks = new LinkedHashMap<ElementInfo, List<DataLink>>(referenceElement.mDatalinks);
		List<DataLink> reflective_datalinks = mDatalinks.remove(referenceElement);
		if (reflective_datalinks != null)
		{
			List<DataLink> datalinks = new ArrayList<DataLink>();
			for (DataLink datalink : reflective_datalinks)
			{
				DataLink new_datalink = new DataLink(datalink.getOutput(), this, datalink.isSnapback(), datalink.getInput(), replaced_flowlinks.get(datalink.getFlowLink()));
				datalinks.add(new_datalink);
			}
			mDatalinks.put(this, datalinks);
		}
	}

	void deploy()
	throws EngineException
	{
		ElementSupport		element = null;
		Class				deployment_class = null;
		ElementDeployer		deployer = null;

		// iterate over all the registered element infos
		element = ElementFactory.INSTANCE.getInstance(this, true);
		
		// try to instantiate each element for the first time
		if (element != null)
		{
			if (ElementType.JAVA_CLASS == getType())
			{
				// obtain the modification time
				if (RifeConfig.Engine.getSiteAutoReload())
				{
					ResourceFinder resource_finder = ElementFactory.INSTANCE.getResourceFinder();
					URL resource = resource_finder.getResource(mImplementation.replace('.', '/')+".class");
					if (resource != null)
					{
						try
						{
							mSite.addResourceModificationTime(new UrlResource(resource, mImplementation), resource_finder.getModificationTime(resource));
						}
						catch (ResourceFinderErrorException e)
						{
							throw new ProcessingErrorException("element", mImplementation, "Error while retrieving the modification time.", e);
						}
					}
				}
			}
			
			deployment_class = element.getDeploymentClass();

			// check if a deployer is available
			if (deployment_class != null)
			{
				try
				{
					// try to instatiate the deployer
					deployer = (ElementDeployer)deployment_class.newInstance();

					// perform the deployment and register it
					deployer.setElementInfo(this);
					deployer.deploy();
					setDeployer(deployer);
				}
				catch (InstantiationException e)
				{
					throw new DeployerInstantiationException(getDeclarationName(), e);
				}
				catch (IllegalAccessException e)
				{
					throw new DeployerInstantiationException(getDeclarationName(), e);
				}
				catch (ClassCastException e)
				{
					throw new DeployerInstantiationException(getDeclarationName(), e);
				}
			}
		}
	}

	void setSite(Site site)
	{
		assert site != null;

		mSite = site;
	}

	public Site getSite()
	{
		return mSite;
	}

	void setProcessorIdentifier(String identifier)
	{
		assert identifier != null;

		mProcessorIdentifier = identifier;
	}

	public String getProcessorIdentifier()
	{
		return mProcessorIdentifier;
	}

	void setGroupId(int groupId)
	{
		mGroupId = groupId;
	}

	public int getGroupId()
	{
		return mGroupId;
	}

	HierarchicalProperties getStaticProperties()
	{
		return mStaticProperties;
	}

	void setProperties(HierarchicalProperties properties, HierarchicalProperties parent)
	{
		mProperties = properties.parent(parent);
		if (parent != null &&
			parent != mStaticProperties)
		{
			parent.getRoot().parent(mStaticProperties);
		}
	}

	void setInheritanceStack(Stack<ElementInfo> inheritanceStack)
	{
		assert inheritanceStack != null;
		assert null == mInheritanceStack;

		mInheritanceStack = inheritanceStack;
	}

	public Stack<ElementInfo> getInheritanceStack()
	{
		return mInheritanceStack;
	}

	void setPrecedenceStack(Stack<ElementInfo> precedenceStack)
	{
		assert precedenceStack != null;
		assert null == mPrecedenceStack;

		mPrecedenceStack = precedenceStack;
	}

	public Stack<ElementInfo> getPrecedenceStack()
	{
		return mPrecedenceStack;
	}

	void setGlobalExits(Map<String, GlobalExit> globalExits)
	throws EngineException
	{
		assert globalExits != null;
		assert null == mGlobalExits;

		mGlobalExits = globalExits;
	}

	void setGlobalVars(Map<String, GlobalVar> globalVars)
	throws EngineException
	{
		assert globalVars != null;
		assert null == mGlobalVars;

		String	globalvar_name = null;
		for (Map.Entry<String, GlobalVar> globalvar_entry : globalVars.entrySet())
		{
			globalvar_name = globalvar_entry.getKey();

			// check if there's no conflicting input
			if (containsInput(globalvar_name))
			{
				throw new GlobalVarInputConflictException(getDeclarationName(), globalvar_name);
			}

			// check if there's no conflicting output
			if (containsOutput(globalvar_name))
			{
				throw new GlobalVarOutputConflictException(getDeclarationName(), globalvar_name);
			}

			// check if there's no conflicting submission parameter or file
			for (Submission submission : mSubmissions.values())
			{
				if (submission.containsParameter(globalvar_name))
				{
					throw new GlobalVarParameterConflictException(getDeclarationName(), globalvar_name, submission.getName());
				}

				if (submission.containsFile(globalvar_name))
				{
					throw new GlobalVarFileConflictException(getDeclarationName(), globalvar_name, submission.getName());
				}
			}

			if (globalvar_name != null)
			{
				mHasGlobalVarDefaults = true;
			}
		}

		mGlobalVars = globalVars;
	}

	void setGlobalCookies(Map<String, String> globalCookies)
	throws EngineException
	{
		assert globalCookies != null;
		assert null == mGlobalCookies;

		String	globalcookie_name = null;
		for (Map.Entry<String, String> globalcookie_entry : globalCookies.entrySet())
		{
			globalcookie_name = globalcookie_entry.getKey();

			// check if there's no conflicting incookie
			if (containsIncookie(globalcookie_name))
			{
				throw new GlobalCookieIncookieConflictException(getDeclarationName(), globalcookie_name);
			}

			// check if there's no conflicting outcookie
			if (containsOutcookie(globalcookie_name))
			{
				throw new GlobalCookieOutcookieConflictException(getDeclarationName(), globalcookie_name);
			}

			// check if there's no conflicting submission parameter or file
			for (Submission submission : mSubmissions.values())
			{
				if (submission.containsParameter(globalcookie_name))
				{
					throw new GlobalCookieParameterConflictException(getDeclarationName(), globalcookie_name, submission.getName());
				}

				if (submission.containsFile(globalcookie_name))
				{
					throw new GlobalCookieFileConflictException(getDeclarationName(), globalcookie_name, submission.getName());
				}
			}

			if (globalcookie_name != null)
			{
				mHasGlobalCookieDefaults = true;
			}
		}

		mGlobalCookies = globalCookies;
	}

	void setNamedGlobalBeans(Map<String, BeanDeclaration> globalBeans)
	throws EngineException
	{
		assert globalBeans != null;
		assert null == mNamedGlobalBeans;

		String	globalbean_name = null;
		for (Map.Entry<String, BeanDeclaration> globalbean_entry : globalBeans.entrySet())
		{
			globalbean_name = globalbean_entry.getKey();

			if (containsNamedInbean(globalbean_name))
			{
				throw new NamedInbeanGlobalBeanConflictException(getDeclarationName(), globalbean_name);
			}

			if (containsNamedOutbean(globalbean_name))
			{
				throw new NamedOutbeanGlobalBeanConflictException(getDeclarationName(), globalbean_name);
			}
		}

		mNamedGlobalBeans = globalBeans;
	}

	public Collection<String> getGlobalExitNames()
	{
		if (null == mGlobalExits)
		{
			return null;
		}

		return mGlobalExits.keySet();
	}

	public Collection<String> getGlobalVarNames()
	{
		if (null == mGlobalVars)
		{
			return null;
		}

		return mGlobalVars.keySet();
	}

	public Collection<String> getGlobalCookieNames()
	{
		if (null == mGlobalCookies)
		{
			return null;
		}

		return mGlobalCookies.keySet();
	}

	public Collection<String> getNamedGlobalBeanNames()
	{
		if (null == mNamedGlobalBeans)
		{
			return null;
		}

		return mNamedGlobalBeans.keySet();
	}

	public boolean hasGlobalExits()
	{
		return !(null == mGlobalExits || mGlobalExits.isEmpty());
	}

	public boolean hasGlobalVars()
	{
		return !(null == mGlobalVars || mGlobalVars.isEmpty());
	}

	public boolean hasGlobalCookies()
	{
		return !(null == mGlobalCookies || mGlobalCookies.isEmpty());
	}

	public boolean hasNamedGlobalBeans()
	{
		return !(null == mNamedGlobalBeans || mNamedGlobalBeans.isEmpty());
	}

	public boolean containsGlobalExit(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mGlobalExits)
		{
			return false;
		}

		return mGlobalExits.containsKey(name);
	}

	public boolean containsGlobalVar(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mGlobalVars)
		{
			return false;
		}

		return mGlobalVars.containsKey(name);
	}

	public boolean containsGlobalCookie(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mGlobalCookies)
		{
			return false;
		}

		return mGlobalCookies.containsKey(name);
	}

	public boolean containsNamedGlobalBean(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mNamedGlobalBeans)
		{
			return false;
		}

		return mNamedGlobalBeans.containsKey(name);
	}

	public GlobalExit getGlobalExitInfo(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mGlobalExits)
		{
			return null;
		}

		return mGlobalExits.get(name);
	}

	public GlobalVar getGlobalVarInfo(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mGlobalVars)
		{
			return null;
		}

		return mGlobalVars.get(name);
	}

	public String getGlobalCookieInfo(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mGlobalCookies)
		{
			return null;
		}

		return mGlobalCookies.get(name);
	}

	public BeanDeclaration getNamedGlobalBeanInfo(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mNamedGlobalBeans)
		{
			return null;
		}

		return mNamedGlobalBeans.get(name);
	}

	void setDepartureVars(ArrayList<String> departureVars)
	throws EngineException
	{
		assert departureVars != null;
		assert null == mDepartureVars;

		for (String departure_var : departureVars)
		{
			if (!containsGlobalVar(departure_var))
			{
				throw new GlobalVarUnknownException(departure_var);
			}
		}

		mDepartureVars = departureVars;
		mHasDepartureVars = true;
	}

	List<String> getDepartureVars()
	{
		return mDepartureVars;
	}

	boolean hasDepartureVars()
	{
		return mHasDepartureVars;
	}

	boolean containsDepartureVar(String name)
	{
		assert name != null;
		assert name.length() > 0;

		if (!mHasDepartureVars ||
			null == mDepartureVars)
		{
			return false;
		}

		return mDepartureVars.contains(name);
	}

	void setUrl(String url)
	throws EngineException
	{
		// ensure that the root url is '/' and not ''
		if (url != null)
		{
			if (mUrl != null)
			{
				throw new ElementAlreadyMappedException(getDeclarationName(), mUrl);
			}
			if (0 == url.length())
			{
				mUrl = "/";
				return;
			}
		}

		mUrl = url;
	}

	void setId(String id)
	throws EngineException
	{
		if (null != mId &&
			id != null)
		{
			throw new ElementAlreadyHasIdException(getDeclarationName(), mId);
		}
		mId = id;
	}

	void setStateStore(StateStore stateStore)
	{
		assert stateStore != null;

		mStateStore = stateStore;
	}

	StateStore getStateStore()
	{
		return mStateStore;
	}

	void setDeclarationName(String declarationName)
	{
		mDeclarationName = declarationName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	void setImplementation(String implementation)
	throws EngineException
	{
		mImplementation = implementation;
		mImplementationBlueprint = null;
		
		setType(ElementFactory.detectElementType(implementation));
		if (null == mType)
		{
			throw new ElementImplementationUnsupportedException(mDeclarationName, mImplementation, null);
		}
	}

	void setImplementation(String implementation, Element blueprint)
	throws EngineException
	{
		if (null == blueprint)
		{
			setImplementation(implementation);
		}
		else
		{
			mImplementation = implementation;
			mImplementationBlueprint = blueprint;
			
			setType(ElementType.JAVA_INSTANCE);
		}
	}
	
	void setType(ElementType type)
	{
		mType = type;
	}

	public String getImplementation()
	{
		return mImplementation;
	}
	
	public Element getImplementationBlueprint()
	{
		return mImplementationBlueprint;
	}
	
	public String getAbsoluteUrl(Element currentElement)
	{
		if (null == currentElement)	throw new IllegalArgumentException("currentElement can't be null.");

		if (null == mUrl)
		{
			return null;
		}

		String			gateservlet_path = currentElement.getElementContext().getRequestState().getGateUrl();
		StringBuilder	absolute_url = new StringBuilder(gateservlet_path);
		if (!gateservlet_path.endsWith("/") &&
			!mUrl.startsWith("/"))
		{
			absolute_url.append("/");
		}

		absolute_url.append(mUrl);

		return absolute_url.toString();
	}

	public String getUrl()
	{
		return mUrl;
	}

	public String getId()
	{
		return mId;
	}
	
	public String getReferenceId()
	{
		if (null == mReferenceId)
		{
			return mId;
		}
		
		return mReferenceId;
	}
	
	void setPathInfoUsed(boolean state)
	{
		mPathInfoUsed = state;
	}

	public boolean isPathInfoUsed()
	{
		return mPathInfoUsed;
	}
	
	void setPathInfoMode(PathInfoMode mode)
	{
		if (null == mode)
		{
			mode = PathInfoMode.LOOSE;
		}
		
		mPathInfoMode = mode;
	}
	
	public PathInfoMode getPathInfoMode()
	{
		return mPathInfoMode;
	}
	
	void setPathInfoMappings(List<PathInfoMapping> mappings)
	{
		mPathInfoMappings = mappings;
	}
	
	public List<PathInfoMapping> getPathInfoMappings()
	{
		return mPathInfoMappings;
	}
	
	public boolean hasPathInfoMappings()
	{
		return mPathInfoMappings != null && mPathInfoMappings.size() > 0;
	}

	void setErrorHandlers(List<ErrorHandler> handlers)
	{
		mErrorHandlers = handlers;
	}

	public List<ErrorHandler> getErrorHandlers()
	{
		return mErrorHandlers;
	}

	public boolean hasErrorHandlers()
	{
		return mErrorHandlers != null && mErrorHandlers.size() > 0;
	}

	void setContentType(String contentType)
	{
		if (contentType != null &&
			0 == contentType.length())
		{
			contentType = null;
		}
		mContentType = contentType;
	}

	public String getContentType()
	{
		return mContentType;
	}

	public ElementType getType()
	{
		return mType;
	}

	ElementSupport getElement()
	throws EngineException
	{
		return ElementFactory.INSTANCE.getInstance(this, true);
	}

	long startTrace()
	throws EngineException
	{
		if (Logger.getLogger("com.uwyn.rife.engine").isLoggable(Level.INFO))
		{
			if (RifeConfig.Engine.getElementDebugTrace())
			{
				return System.currentTimeMillis();
			}
		}

		return 0;
	}

	void outputTrace(long start, RequestState state)
	throws EngineException
	{
		if (start != 0)
		{
			StringBuilder output = new StringBuilder();
			if (RifeConfig.Engine.getElementDebugMemory())
			{
				Runtime runtime = java.lang.Runtime.getRuntime();
				long total_memory = runtime.totalMemory();
				long free_memory = runtime.freeMemory();
				output.append("total (");
				output.append(MEMORY_FORMAT.format(total_memory/1024));
				output.append("kb) free (");
				output.append(MEMORY_FORMAT.format(free_memory/1024));
				output.append("kb) used (");
				output.append(MEMORY_FORMAT.format((total_memory-free_memory)/1024));
				output.append("kb)");

				output.append(" ");
			}

			output.append(System.currentTimeMillis()-start);
			output.append("ms : ");
			output.append(state.getServerName());
			output.append(":");
			output.append(state.getServerPort());
			output.append(" ");

			output.append(getId());
			output.append(" ");
			if (state.getRequest() != null &&
				state.getRequest().getHttpServletRequest() != null)
			{
				output.append(state.getRequest().getHttpServletRequest().getRequestURI());
				String query_string = state.getRequest().getHttpServletRequest().getQueryString();
				if (query_string != null &&
					query_string.length() > 0)
				{
					output.append("?");
					output.append(query_string);
				}
			}

			Logger.getLogger("com.uwyn.rife.engine").info(output.toString());
		}
	}
	
	public HierarchicalProperties getProperties()
	{
		return mProperties;
	}
	
	public Collection<String> getPropertyNames()
	{
		if (null == mProperties)
		{
			return Collections.EMPTY_LIST;
		}

		return mProperties.getNames();
	}

	public Collection<String> getInjectablePropertyNames()
	{
		if (null == mProperties)
		{
			return Collections.EMPTY_LIST;
		}

		return mProperties.getInjectableNames();
	}

	public Collection<String> getInputNames()
	{
		return mInputs.keySet();
	}

	public Collection<String> getOutputNames()
	{
		return mOutputs.keySet();
	}

	public Set<Map.Entry<String, GlobalExit>> getGlobalExitEntries()
	{
		return mGlobalExits.entrySet();
	}

	public Set<Map.Entry<String, GlobalVar>> getGlobalVarEntries()
	{
		return mGlobalVars.entrySet();
	}

	public Set<Map.Entry<String, String[]>> getOutputEntries()
	{
		return mOutputs.entrySet();
	}

	public Collection<String> getIncookieNames()
	{
		return mIncookies.keySet();
	}

	public Collection<String> getOutcookieNames()
	{
		return mOutcookies.keySet();
	}

	public Set<Map.Entry<String, String>> getGlobalCookieEntries()
	{
		return mGlobalCookies.entrySet();
	}

	public Set<Map.Entry<String, String>> getOutcookieEntries()
	{
		return mOutcookies.entrySet();
	}

	public boolean hasIncookies()
	{
		return mIncookies != null && mIncookies.size() > 0;
	}

	public boolean hasGlobalcookies()
	{
		return mGlobalCookies != null && mGlobalCookies.size() > 0;
	}

	public boolean hasOutcookies()
	{
		return mOutcookies != null && mOutcookies.size() > 0;
	}

	public boolean hasNamedInbeans()
	{
		return !(null == mNamedInbeans || mNamedInbeans.isEmpty());
	}

	public Collection<String> getNamedInbeanNames()
	{
		return mNamedInbeans.keySet();
	}

	public Collection<String> getNamedOutbeanNames()
	{
		return mNamedOutbeans.keySet();
	}

	public BeanDeclaration getNamedInbeanInfo(String name)
	throws EngineException
	{
		if (null == name ||
			0 == name.length() ||
			null == mNamedInbeans)
		{
			return null;
		}

		return mNamedInbeans.get(name);
	}

	public BeanDeclaration getNamedOutbeanInfo(String name)
	throws EngineException
	{	
		if (null == name ||
			0 == name.length() ||
			null == mNamedOutbeans)
		{
			return null;
		}

		return mNamedOutbeans.get(name);
	}

	public Collection<String> getChildTriggerNames()
	{
		return mChildTriggers;
	}

	public Collection<String> getExitNames()
	{
		return mExits.keySet();
	}

	public boolean hasProperties()
	{
		if (null == mProperties)
		{
			return false;
		}

		return mProperties.size() != 0;
	}

	public Object getProperty(String name)
	{
		return getProperty(name, null);
	}

	public Object getProperty(String name, Object defaultValue)
	{
		if (null == name ||
			0 == name.length() ||
			null == mProperties)
		{
			return null;
		}

		Object result = null;

		PropertyValue property = mProperties.get(name);
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

	public <T> T getPropertyTyped(String name, Class<T> type)
	{
		return (T)getPropertyTyped(name, type, null);
	}

	public <T> T getPropertyTyped(String name, Class<T> type, T defaultValue)
	{
		if (null == mProperties)
		{
			return null;
		}

		try
		{
			return (T)mProperties.getValueTyped(name, type, defaultValue);
		}
		catch (PropertyValueException e)
		{
			throw new PropertyValueRetrievalException(getDeclarationName(), name, e);
		}
	}

	public String getPropertyString(String name)
	{
		return getPropertyString(name, null);
	}

	public String getPropertyString(String name, String defaultValue)
	{
		if (null == name ||
			0 == name.length() ||
			null == mProperties)
		{
			return null;
		}

		try
		{
			return mProperties.getValueString(name, defaultValue);
		}
		catch (PropertyValueException e)
		{
			throw new PropertyValueRetrievalException(getDeclarationName(), name, e);
		}
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

	public Submission getSubmission(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return null;
		}
		
		return mSubmissions.get(name);
	}
	
	public String[] getGlobalVarDefaultValues(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return null;
		}
		
		GlobalVar data = mGlobalVars.get(name);
		if (null == data)
		{
			return null;
		}

		return data.getDefaultValues();
	}

	public String[] getInputDefaultValues(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return null;
		}
		
		return mInputs.get(name);
	}

	public String[] getOutputDefaultValues(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return null;
		}
		
		return mOutputs.get(name);
	}

	public String getIncookieDefaultValue(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return null;
		}
		
		return mIncookies.get(name);
	}

	public String getGlobalCookieDefaultValue(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return null;
		}
		
		return mGlobalCookies.get(name);
	}

	public String getOutcookieDefaultValue(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return null;
		}
		
		return mOutcookies.get(name);
	}

	public Map<String, String> getDefaultIncookies()
	{
		Map<String, String>	default_incookies = new LinkedHashMap<String, String>();

		for (Map.Entry<String, String> incookie_entry : mIncookies.entrySet())
		{
			if (incookie_entry.getValue() != null)
			{
				default_incookies.put(incookie_entry.getKey(), incookie_entry.getValue());
			}
		}

		return default_incookies;
	}

	public Map<String, String> getDefaultOutcookies()
	{
		Map<String, String>	default_outcookies = new LinkedHashMap<String, String>();

		for (Map.Entry<String, String> outcookie_entry : mOutcookies.entrySet())
		{
			if (outcookie_entry.getValue() != null)
			{
				default_outcookies.put(outcookie_entry.getKey(), outcookie_entry.getValue());
			}
		}

		return default_outcookies;
	}

	public Map<String, String> getDefaultGlobalCookies()
	{
		Map<String, String>	default_globalcookies = new LinkedHashMap<String, String>();

		for (Map.Entry<String, String> globalcookie_entry : mGlobalCookies.entrySet())
		{
			if (globalcookie_entry.getValue() != null)
			{
				default_globalcookies.put(globalcookie_entry.getKey(), globalcookie_entry.getValue());
			}
		}

		return default_globalcookies;
	}

	public String[] getParameterDefaultValues(String submissionName, String parameterName)
	{
		if (null == submissionName ||
			0 == submissionName.length() ||
			null == parameterName ||
			0 == parameterName.length())
		{
			return null;
		}

		return mSubmissions.get(submissionName).getParameterDefaultValues(parameterName);
	}

	public Set<Map.Entry<String, FlowLink>> getExitEntries()
	{
		return mExits.entrySet();
	}

	public Collection<String> getSubmissionNames()
	{
		return mSubmissions.keySet();
	}

	public Collection<Submission> getSubmissions()
	{
		return mSubmissions.values();
	}

	public FlowLink getFlowLink(String name)
	{
		if (null == name ||
			0 == name.length() ||
			!mExits.containsKey(name))
		{
			return null;
		}

		return mExits.get(name);
	}

	public boolean containsProperty(String name)
	{
		if (null == name ||
			0 == name.length() ||
			null == mProperties)
		{
			return false;
		}

		return mProperties.contains(name);
	}

	public boolean containsInput(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mInputs.containsKey(name);
	}
	
	public boolean containsInputPossibility(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mInputs.containsKey(name) || (mGlobalVars != null && mGlobalVars.containsKey(name));
	}

	public boolean containsOutput(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mOutputs.containsKey(name);
	}

	public boolean containsOutputPossibility(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mOutputs.containsKey(name) || (mGlobalVars != null && mGlobalVars.containsKey(name));
	}

	public boolean containsIncookie(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mIncookies.containsKey(name);
	}

	public boolean containsIncookiePossibility(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mIncookies.containsKey(name) || (mGlobalCookies != null && mGlobalCookies.containsKey(name));
	}

	public boolean containsOutcookie(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mOutcookies.containsKey(name);
	}

	public boolean containsOutcookiePossibility(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mOutcookies.containsKey(name) || (mGlobalCookies != null && mGlobalCookies.containsKey(name));
	}

	public boolean containsNamedInbean(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mNamedInbeans.containsKey(name);
	}

	public boolean containsNamedOutbean(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mNamedOutbeans.containsKey(name);
	}

	public boolean containsChildTrigger(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mChildTriggers.contains(name);
	}

	public boolean containsExit(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mExits.containsKey(name);
	}

	public boolean containsSubmission(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mSubmissions.containsKey(name);
	}

	public boolean hasSubmission(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mSubmissions.containsKey(name);
	}

	public boolean hasGlobalVarDefaults()
	{
		return mHasGlobalVarDefaults;
	}

	public boolean hasGlobalCookieDefaults()
	{
		return mHasGlobalCookieDefaults;
	}

	public boolean hasInputDefaults()
	{
		return mHasInputDefaults;
	}

	public boolean hasOutputDefaults()
	{
		return mHasOutputDefaults;
	}

	public boolean hasIncookieDefaults()
	{
		return mHasIncookieDefaults;
	}

	public boolean hasOutcookieDefaults()
	{
		return mHasOutcookieDefaults;
	}

	public boolean hasParameterDefaults(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		return mHasSubmissionDefaults && mSubmissions.get(name).hasParameterDefaults();
	}

	public boolean hasGlobalVarDefaultValues(String name)
	{
		if (null == name ||
			0 == name.length() ||
			!mHasGlobalVarDefaults)
		{
			return false;
		}

		GlobalVar data = mGlobalVars.get(name);
		if (null == data)
		{
			return false;
		}
		if (data.getDefaultValues() != null)
		{
			return true;
		}

		return false;
	}

	public boolean hasGlobalCookieDefaultValue(String name)
	{
		if (null == name ||
			0 == name.length() ||
			!mHasGlobalCookieDefaults)
		{
			return false;
		}

		String data = mGlobalCookies.get(name);

		if (data != null && data.length() != 0)
		{
			return true;
		}

		return false;
	}

	public boolean hasInputs()
	{
		return mInputs != null && mInputs.size() > 0;
	}

	public boolean hasInputDefaultValues(String name)
	{
		if (null == name ||
			0 == name.length() ||
			!mHasInputDefaults)
		{
			return false;
		}

		if (mInputs.get(name) != null)
		{
			return true;
		}

		return false;
	}

	public boolean hasOutputDefaultValues(String name)
	{
		if (null == name ||
			0 == name.length() ||
			!mHasOutputDefaults)
		{
			return false;
		}

		if (mOutputs.get(name) != null)
		{
			return true;
		}

		return false;
	}

	public boolean hasIncookieDefaultValue(String name)
	{
		if (null == name ||
			0 == name.length() ||
			!mHasIncookieDefaults)
		{
			return false;
		}

		if (mIncookies.get(name) != null)
		{
			return true;
		}

		return false;
	}

	public boolean hasOutcookieDefaultValue(String name)
	{
		if (null == name ||
			0 == name.length() ||
			!mHasOutcookieDefaults)
		{
			return false;
		}

		if (mOutcookies.get(name) != null)
		{
			return true;
		}

		return false;
	}

	public boolean hasParameterDefaultValues(String submissionName, String parameterName)
	{
		if (null == submissionName ||
			0 == submissionName.length() ||
			null == parameterName ||
			0 == parameterName.length() ||
			!mHasSubmissionDefaults)
		{
			return false;
		}

		return mSubmissions.get(submissionName).hasParameterDefaultValues(parameterName);
	}

	public boolean hasDataLink(ElementInfo target)
	{
		assert target != null;

		return mDatalinks.containsKey(target);
	}

	public boolean hasSnapbackDataLinks()
	{
		return mHasSnapbackDataLinks;
	}

	public boolean hasFlowLink(ElementInfo target)
	{
		assert target != null;

		if (0 == mExits.size())
		{
			return false;
		}

		for (FlowLink flowlink : mExits.values())
		{
			if (flowlink != null &&
				flowlink.getTarget() != null &&
				flowlink.getTarget().equals(target))
			{
				return true;
			}
		}

		return false;
	}

	public boolean hasSnapbackFlowLinks()
	{
		return mHasSnapbackFlowLinks;
	}

	void validatePropertyName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsProperty(name))
		{
			throw new PropertyUnknownException(getDeclarationName(), name);
		}
	}

	void validateInputName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsInput(name) &&
			!containsGlobalVar(name))
		{
			throw new InputUnknownException(getDeclarationName(), name);
		}
	}

	void validateOutputName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsOutput(name) &&
			!containsGlobalVar(name))
		{
			throw new OutputUnknownException(getDeclarationName(), name);
		}
	}

	void validateIncookieName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsIncookie(name) &&
			!containsGlobalCookie(name))
		{
			throw new IncookieUnknownException(getDeclarationName(), name);
		}
	}

	void validateOutcookieName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsOutcookie(name) &&
			!containsGlobalCookie(name))
		{
			throw new OutcookieUnknownException(getDeclarationName(), name);
		}
	}

	void validateInbeanName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsNamedInbean(name) &&
			!containsNamedGlobalBean(name))
		{
			throw new NamedInbeanUnknownException(getDeclarationName(), name);
		}
	}

	void validateOutbeanName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsNamedOutbean(name) &&
			!containsNamedGlobalBean(name))
		{
			throw new NamedOutbeanUnknownException(getDeclarationName(), name);
		}
	}

	void validateExitName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsExit(name))
		{
			throw new ExitUnknownException(getDeclarationName(), name);
		}
	}

	FlowLink validateAndRetrieveFlowLink(String exitName)
	throws EngineException
	{
		validateExitName(exitName);

		FlowLink	flowlink = null;
		flowlink = getFlowLink(exitName);
		if (null == flowlink)
		{
			throw new ExitNotAttachedException(getDeclarationName(), exitName);
		}

		return flowlink;
	}

	void validateSubmissionName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsSubmission(name))
		{
			throw new SubmissionUnknownException(getDeclarationName(), name);
		}
	}

	void addDataLink(DataLink dataLink)
	throws EngineException
	{
		assert dataLink != null;

		// detect in-lined element declarations and auto-add exits for the flowlinks if they are missing
		ElementInfoProcessorFactory factory = ElementInfoProcessorFactory.getElementInfoProcessorFactory(getProcessorIdentifier());
		if (factory != null &&
			factory.generateOutputsFromDatalinks() &&
			!containsOutputPossibility(dataLink.getOutput()))
		{
			addOutput(dataLink.getOutput(), null);
		}

		// ensure the valid output
		validateOutputName(dataLink.getOutput());

		if (dataLink.isSnapback())
		{
			if (!hasSnapbackFlowLinks())
			{
				throw new SnapbackFlowLinkMissingException(getDeclarationName());
			}
			if (!mSnapbackDatalinks.contains(dataLink))
			{
				mSnapbackDatalinks.add(dataLink);
			}
			mHasSnapbackDataLinks = true;
		}
		else
		{
			ElementInfo target = dataLink.getTarget();
			target.validateInputName(dataLink.getInput());
			if (this.equals(target))
			{
				if (!hasFlowLink(target) &&
					0 == mSubmissions.size())
				{
					throw new FlowLinkOrSubmissionMissingException(getDeclarationName());
				}
			}
			else if (!hasFlowLink(target))
			{
				throw new FlowLinkMissingException(getDeclarationName(), target.getDeclarationName());
			}

			List<DataLink> datalinks = mDatalinks.get(target);
			if (null == datalinks)
			{
				datalinks = new ArrayList<DataLink>();
				mDatalinks.put(target, datalinks);
			}
			if (!datalinks.contains(dataLink))
			{
				datalinks.add(dataLink);
			}
		}
	}

	Collection<String> getDataLinkInputs(String outputName, ElementInfo target, boolean snapback, FlowLink flowLink)
	{
		assert outputName != null;
		assert outputName.length() > 0;
		assert target != null;

		// retrieve the target specific datalinks
		List<DataLink>	datalinks = mDatalinks.get(target);
		if (!snapback && null == datalinks)
		{
			return null;
		}

		HashSet<String>	inputnames_list = new HashSet<String>();

		// process the target specific datalinks
		if (datalinks != null)
		{
			for (DataLink datalink : datalinks)
			{
				if (datalink.getOutput().equals(outputName) &&
					(null == datalink.getFlowLink() ||
					 flowLink != null && datalink.getFlowLink().equals(flowLink)))
				{
					inputnames_list.add(datalink.getInput());
				}
			}
		}

		// process the snapback datalinks
		if (snapback)
		{
			for (DataLink datalink : mSnapbackDatalinks)
			{
				if (datalink.getOutput().equals(outputName) &&
					(null == datalink.getFlowLink() ||
					 flowLink != null && datalink.getFlowLink().equals(flowLink)))
				{
					inputnames_list.add(datalink.getInput());
				}
			}
		}

		if (0 == inputnames_list.size())
		{
			return null;
		}

		return inputnames_list;
	}

	void setFlowLink(FlowLink flowLink)
	throws EngineException
	{
		assert flowLink != null;

		// detect in-lined element declarations and auto-add exits for the flowlinks if they are missing
		ElementInfoProcessorFactory factory = ElementInfoProcessorFactory.getElementInfoProcessorFactory(getProcessorIdentifier());
		if (factory != null &&
			factory.generateExitsFromFlowlinks() &&
			!containsExit(flowLink.getExitName()))
		{
			addExit(flowLink.getExitName());
		}

		// ensure the valid exit
		validateExitName(flowLink.getExitName());

		mExits.put(flowLink.getExitName(), flowLink);
		if (flowLink.isSnapback())
		{
			mHasSnapbackFlowLinks = true;
		}
	}

	void addStaticProperty(String name, Object value)
	throws EngineException
	{
		addStaticProperty(name, new PropertyValueObject(value));
	}

	void addStaticProperty(String name, PropertyValue value)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		mStaticProperties.put(name, value);
		if (null == mProperties)
		{
			setProperties(new HierarchicalProperties(), mStaticProperties);
		}
	}

	void addInput(String name, String defaultValues[])
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (defaultValues != null && 0 == defaultValues.length)
		{
			defaultValues = null;
		}

		// check that the name isn't reserved
		if (ReservedParameters.RESERVED_NAMES_LIST.contains(name))
		{
			throw new ReservedInputNameException(getDeclarationName(), name);
		}

		// check if the input doesn't exist already
		if (mInputs.containsKey(name))
		{
			throw new InputExistsException(getDeclarationName(), name);
		}

		// check if there's no conflicting submission parameter or file
		for (Submission submission : mSubmissions.values())
		{
			if (submission.containsParameter(name))
			{
				throw new InputParameterConflictException(getDeclarationName(), name, submission.getName());
			}

			if (submission.containsFile(name))
			{
				throw new InputFileConflictException(getDeclarationName(), name, submission.getName());
			}
		}

		// check if there's no conflicting global var
		if (containsGlobalVar(name))
		{
			throw new InputGlobalVarConflictException(getDeclarationName(), name);
		}

		if (defaultValues != null)
		{
			mHasInputDefaults = true;
		}

		mInputs.put(name, defaultValues);
	}

	void addOutput(String name, String[] defaultValues)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (defaultValues != null && 0 == defaultValues.length)
		{
			defaultValues = null;
		}

		// check that the name isn't reserved
		if (ReservedParameters.RESERVED_NAMES_LIST.contains(name))
		{
			throw new ReservedOutputNameException(getDeclarationName(), name);
		}

		// check if the output doesn't exist already
		if (mOutputs.containsKey(name))
		{
			throw new OutputExistsException(getDeclarationName(), name);
		}

		// check if there's no conflicting global variable
		if (containsGlobalVar(name))
		{
			throw new OutputGlobalVarConflictException(getDeclarationName(), name);
		}

		if (defaultValues != null)
		{
			mHasOutputDefaults = true;
		}

		mOutputs.put(name, defaultValues);
	}

	void addIncookie(String name, String defaultValue)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (defaultValue != null && 0 == defaultValue.length())
		{
			defaultValue = null;
		}

		// check if the incookie doesn't exist already
		if (mIncookies.containsKey(name))
		{
			throw new IncookieExistsException(getDeclarationName(), name);
		}

		// check if there's no conflicting submission parameter or file
		for (Submission submission : mSubmissions.values())
		{
			if (submission.containsParameter(name))
			{
				throw new IncookieParameterConflictException(getDeclarationName(), name, submission.getName());
			}

			if (submission.containsFile(name))
			{
				throw new IncookieFileConflictException(getDeclarationName(), name, submission.getName());
			}
		}

		// check if there's no conflicting global cookie
		if (containsGlobalCookie(name))
		{
			throw new IncookieGlobalCookieConflictException(getDeclarationName(), name);
		}

		if (defaultValue != null)
		{
			mHasIncookieDefaults = true;
		}

		mIncookies.put(name, defaultValue);
	}

	void addOutcookie(String name, String defaultValue)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (defaultValue != null && 0 == defaultValue.length())
		{
			defaultValue = null;
		}

		// check if the outcookie doesn't exist already
		if (mOutcookies.containsKey(name))
		{
			throw new OutcookieExistsException(getDeclarationName(), name);
		}

		// check if there's no conflicting global cookie
		if (containsGlobalCookie(name))
		{
			throw new OutcookieGlobalCookieConflictException(getDeclarationName(), name);
		}

		if (defaultValue != null)
		{
			mHasOutcookieDefaults = true;
		}

		mOutcookies.put(name, defaultValue);
	}

	void addNamedInbean(String name, BeanDeclaration bean)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert bean != null;

		if (containsNamedGlobalBean(name))
		{
			throw new NamedInbeanGlobalBeanConflictException(getDeclarationName(), name);
		}

		if (mNamedInbeans.containsKey(name))
		{
			throw new NamedInbeanExistsException(getDeclarationName(), name);
		}

		mNamedInbeans.put(name, bean);
	}

	void addNamedOutbean(String name, BeanDeclaration bean)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert bean != null;

		if (containsNamedGlobalBean(name))
		{
			throw new NamedOutbeanGlobalBeanConflictException(getDeclarationName(), name);
		}

		if (mNamedOutbeans.containsKey(name))
		{
			throw new NamedOutbeanExistsException(getDeclarationName(), name);
		}

		mNamedOutbeans.put(name, bean);
	}

	void addChildTrigger(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (!containsInput(name) &&
			!containsIncookie(name) &&
			!containsOutput(name) &&
			!containsOutcookie(name) &&
			!containsGlobalVar(name) &&
			!containsGlobalCookie(name))
		{
			throw new ChildTriggerVariableUnknownException(getDeclarationName(), name);
		}

		if (mChildTriggers.contains(name))
		{
			throw new ChildTriggerExistsException(getDeclarationName(), name);
		}

		mChildTriggers.add(name);
	}

	void addExit(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		if (mExits.containsKey(name))
		{
			throw new ExitExistsException(getDeclarationName(), name);
		}

		mExits.put(name, null);
	}

	void addSubmission(String name, Submission submission)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert submission != null;

		if (mSubmissions.containsKey(name))
		{
			throw new SubmissionExistsException(getDeclarationName(), name);
		}

		mSubmissions.put(name, submission);
		submission.setName(name);
		submission.setElementInfo(this);

		if (submission.hasParameterDefaults())
		{
			setHasSubmissionDefaults(true);
		}
	}

	void setHasSubmissionDefaults(boolean hasSubmissionDefaults)
	{
		mHasSubmissionDefaults = hasSubmissionDefaults;
	}

	void setDeployer(ElementDeployer deployer)
	{
		mDeployer = deployer;
	}

	public ElementDeployer getDeployer()
	{
		return mDeployer;
	}

	public synchronized ElementInfo clone()
	{
        ElementInfo new_elementinfo = null;
		try
		{
			new_elementinfo = (ElementInfo)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// this should never happen
			Logger.getLogger("com.uwyn.rife.engine").severe(ExceptionUtils.getExceptionStackTrace(e));
		}

		if (mStaticProperties != null)
		{
			new_elementinfo.mStaticProperties = new HierarchicalProperties()
				.putAll(mStaticProperties)
				.parent(Rep.getProperties());
		}

		if (mProperties != null)
		{
			HierarchicalProperties shadow = mProperties.createShadow(mStaticProperties);
			new_elementinfo.mProperties = new HierarchicalProperties().putAll(mProperties);
			if (null == shadow.getParent())
			{
				new_elementinfo.mProperties = new HierarchicalProperties().putAll(mProperties).parent(new_elementinfo.mStaticProperties);
			}
			else
			{
				new_elementinfo.setProperties(new HierarchicalProperties().putAll(mProperties), shadow);
			}
		}

		if (mSubmissions != null)
		{
			new_elementinfo.mSubmissions = new LinkedHashMap<String, Submission>();

			Submission	submission = null;
			for (String id : mSubmissions.keySet())
			{
				submission = mSubmissions.get(id);
				if (submission != null)
				{
					submission = submission.clone();
				}
				submission.setElementInfo(this);
				new_elementinfo.mSubmissions.put(id, submission);
			}
		}

		if (mInputs != null)
		{
			new_elementinfo.mInputs = new LinkedHashMap<String, String[]>(mInputs);
		}

		if (mOutputs != null)
		{
			new_elementinfo.mOutputs = new LinkedHashMap<String, String[]>(mOutputs);
		}

		if (mIncookies != null)
		{
			new_elementinfo.mIncookies = new LinkedHashMap<String, String>(mIncookies);
		}

		if (mOutcookies != null)
		{
			new_elementinfo.mOutcookies = new LinkedHashMap<String, String>(mOutcookies);
		}

		if (mNamedInbeans != null)
		{
			new_elementinfo.mNamedInbeans = new LinkedHashMap<String, BeanDeclaration>(mNamedInbeans);
		}

		if (mNamedOutbeans != null)
		{
			new_elementinfo.mNamedOutbeans = new LinkedHashMap<String, BeanDeclaration>(mNamedOutbeans);
		}

		if (mChildTriggers != null)
		{
			new_elementinfo.mChildTriggers = new ArrayList<String>(mChildTriggers);
		}

		if (mExits != null)
		{
			new_elementinfo.mExits = new LinkedHashMap<String, FlowLink>(mExits);
		}

		if (mDatalinks != null)
		{
			new_elementinfo.mDatalinks = new LinkedHashMap<ElementInfo, List<DataLink>>();

			List<DataLink>		data_links = null;
			for (ElementInfo element_info : mDatalinks.keySet())
			{
				data_links = mDatalinks.get(element_info);
				if (data_links != null)
				{
					data_links = new ArrayList<DataLink>(data_links);
				}
				new_elementinfo.mDatalinks.put(element_info, data_links);
			}
		}

		if (mInheritanceStack != null)
		{
			new_elementinfo.mInheritanceStack = new Stack<ElementInfo>();
			new_elementinfo.mInheritanceStack.addAll(mInheritanceStack);
		}

		if (mPrecedenceStack != null)
		{
			new_elementinfo.mPrecedenceStack = new Stack<ElementInfo>();
			new_elementinfo.mPrecedenceStack.addAll(mPrecedenceStack);
		}

		if (mGlobalExits != null)
		{
			new_elementinfo.mGlobalExits = new LinkedHashMap<String, GlobalExit>(mGlobalExits);
		}

		if (mGlobalVars != null)
		{
			new_elementinfo.mGlobalVars = new LinkedHashMap<String, GlobalVar>(mGlobalVars);
		}

		if (mGlobalCookies != null)
		{
			new_elementinfo.mGlobalCookies = new LinkedHashMap<String, String>(mGlobalCookies);
		}

		if (mNamedGlobalBeans != null)
		{
			new_elementinfo.mNamedGlobalBeans = new LinkedHashMap<String, BeanDeclaration>(mNamedGlobalBeans);
		}

		if (mDepartureVars != null)
		{
			new_elementinfo.mDepartureVars = new ArrayList<String>(mDepartureVars);
		}

		if (mPathInfoMappings != null)
		{
			new_elementinfo.mPathInfoMappings = new ArrayList<PathInfoMapping>(mPathInfoMappings);
		}

		if (mErrorHandlers != null)
		{
			new_elementinfo.mErrorHandlers = new ArrayList<ErrorHandler>(mErrorHandlers);
		}

		return new_elementinfo;
	}
}



