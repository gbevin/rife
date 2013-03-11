/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Site.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.continuations.ContinuationManager;
import com.uwyn.rife.engine.exceptions.DuplicateElementIdException;
import com.uwyn.rife.engine.exceptions.ElementIdNotFoundException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.FallbackUrlExistsException;
import com.uwyn.rife.engine.exceptions.UrlExistsException;
import com.uwyn.rife.rep.Participant;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.TerracottaUtils;
import java.lang.reflect.Method;
import java.util.regex.Matcher;

/**
 * A <code>Site</code> contains all the elements that will be used to handle
 * web requests
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class Site
{
	public final static String	DEFAULT_PARTICIPANT_NAME = "ParticipantSite";
	
	private String				mDeclarationName = null;
	private SiteData			mData = new SiteData();
	private Set<SiteListener>	mListeners = null;
	private volatile Long		mLastModificationCheck = 0L;
	
	protected Site()
	{
	}
	
	void setDeclarationName(String declarationName)
	{
		assert declarationName != null;
		
		mDeclarationName = declarationName;
	}
	
	void setResourceFinder(ResourceFinder resourceFinder)
	{
		assert resourceFinder != null;
		
		mData.mResourceFinder = resourceFinder;
	}
	
	void addResourceModificationTime(UrlResource location, long modificationTime)
	{
		if (RifeConfig.Engine.getSiteAutoReload())
		{
			if (null == mData.mResourceModificationTimes)
			{
				mData.mResourceModificationTimes = new HashMap<UrlResource, Long>();
			}
			
			mData.mResourceModificationTimes.put(location, new Long(modificationTime));
		}
	}
	
	/**
	 * Retrieves a map of all the resources that were used to construct the site
	 * and their last modification time.
	 *
	 * @return the map of resources with their modification times; or
	 * <p><code>null</code> if the site was totally built manually or if the
	 * <code>SITE_AUTO_RELOAD</code> configuration parameter was not set to
	 * <code>true</code> at the time of construction.
	 * @since 1.0
	 */
	public Map<UrlResource, Long> getResourceModificationTimes()
	{
		if (null == mData.mResourceModificationTimes)
		{
			return Collections.EMPTY_MAP;
		}
		return mData.mResourceModificationTimes;
	}
	
	/**
	 * Resets the last modification check so that the next request will always
	 * check for modifications.
	 * 
	 * @since 1.5.1
	 */
	public void resetLastModificationCheck()
	{
		synchronized (mLastModificationCheck)
		{
			mLastModificationCheck = 0L;
		}
	}
	
	private boolean isModified()
	throws EngineException
	{
		if (null != mData.mResourceModificationTimes)
		{
			long	current_modification_time = 0;
			
			for (Map.Entry<UrlResource, Long> resource_entry : mData.mResourceModificationTimes.entrySet())
			{
				try
				{
					current_modification_time = mData.mResourceFinder.getModificationTime(resource_entry.getKey().getUrl());
				}
				catch (ResourceFinderErrorException e)
				{
					// resource couldn't be found, consider it as not modified
					return false;
				}
				
				if (resource_entry.getValue().longValue() != current_modification_time)
				{
					if (getClass().getClassLoader() instanceof EngineClassLoader &&
						resource_entry.getKey().getUrl().getFile().endsWith(".class"))
					{
						((EngineClassLoader)getClass().getClassLoader())
							.markClassAsModified(resource_entry.getKey().getSourceName());
					}
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void checkModification()
	throws EngineException
	{
		if (null != mData.mResourceModificationTimes)
		{
			synchronized (mLastModificationCheck)
			{
				if (System.currentTimeMillis() - mLastModificationCheck <= RifeConfig.Global.getAutoReloadDelay())
				{
					return;
				}
				mLastModificationCheck = System.currentTimeMillis();
			}

			synchronized (this)
			{
				if (isModified())
				{
					SiteBuilder	builder = new SiteBuilder(mDeclarationName, mData.mResourceFinder);
					Site		new_site = null;
					
					new_site = builder.getSite();
					
					// only replace the site data when the root site builder was not manually processed
					if (new_site != null &&
						!SiteProcessorFactory.MANUAL_IDENTIFIER.equals(builder.getSiteProcessorIdentifier()))
					{
						populateFromOther(new_site);
					}
					
					fireModified();
				}
			}
		}
	}
	
	/**
	 * Clears the cached data
	 * 
	 * @since 1.5.1
	 */
	public synchronized void clearCaches()
	{
        this.mData.clearCaches();
	}
	
	/**
	 * Populates this site instance from another site instance.
	 * <p>This method is typically used during the implementation of a {@link SiteListener#modified}
	 * method. Doing this, will ensure that the active request as well as
	 * subsequent requests will be executed against the data of the other site
	 * instance.
	 *
	 * @param otherSite the other site that will be used to replace this site's
	 * data with
	 * @since 1.5
	 */
	public synchronized void populateFromOther(Site otherSite)
	{
		this.mData = otherSite.mData;
        this.mData.clearCaches();

        for (ElementInfo element_info : otherSite.getElementInfos())
		{
			element_info.setSite(this);
		}
	}

	/**
	 * Indicates whether the default repository has a participant named
	 * "<code>ParticipantSite</code>".
	 *
	 * @return <code>true</code> if that participant was present; or
	 * <p><code>false</code> otherwise
	 * @see Rep#getDefaultRepository
	 * @see Participant
	 * @since 1.0
	 */
    public static boolean hasRepInstance()
    {
        return Rep.hasParticipant(DEFAULT_PARTICIPANT_NAME);
    }

	/**
	 * Retrieves the participant named "<code>ParticipantSite</code>" from the
	 * default repository.
	 *
	 * @return the participant; or
	 * <p><code>null</code> if no such participant was present
	 * @see Rep#getDefaultRepository
	 * @see Participant
	 * @since 1.0
	 */
    public static Participant getRepParticipant()
    {
		return Rep.getParticipant(DEFAULT_PARTICIPANT_NAME);
    }
	
	/**
	 * Retrieves the default object as a <code>Site</code> from the participant
	 * that was returned by {@link #getRepParticipant}.
	 *
	 * @return the site instance; or
	 * <p><code>null</code> if no "<code>ParticipantSite</code>" participant was
	 * present in the default repository
	 * @see #getRepParticipant
	 * @see Participant#getObject
	 * @since 1.0
	 */
    public static Site getRepInstance()
    {
		Participant	participant = getRepParticipant();
		if (null == participant)
		{
			return null;
		}
		
        return (Site)participant.getObject();
    }
	
	/**
	 * Retrieves the resource finder that was used to populate this site.
	 *
	 * @return this site's resource finder
	 * @since 1.4
	 */
    public ResourceFinder getResourceFinder()
    {
		return mData.mResourceFinder;
    }
	
	/**
	 * Retrieves the continuation manager that is used by this site.
	 *
	 * @return the site's contiuation manager
	 * @since 1.5
	 */
	public ContinuationManager getContinuationManager()
	{
		return mData.mContinuationManager;
	}
	
	/**
	 * Retrieves the collection of all the element IDs that are present in
	 * this site.
	 *
	 * @return the collection of all the element IDs in this site
	 * @since 1.0
	 */
	public Collection<String> getIds()
	{
		return mData.mIdMapping.keySet();
	}
	
	Collection<ElementInfo> getElementInfos()
	{
		return mData.mIdMapping.values();
	}
	
	void addElementInfo(String id, ElementInfo elementInfo, String url)
	throws EngineException
	{
		assert id != null;
		assert id.length() > 0;
		assert elementInfo != null;
		
		if (mData.mIdMapping.containsKey(id))
		{
			throw new DuplicateElementIdException(id);
		}
		elementInfo.setId(id);
		mData.mIdMapping.put(id, elementInfo);
		
		elementInfo.setSite(this);
		
		if (url != null)
		{
			// ensure that the root url always is '/' and not ''
			if (0 == url.length())
			{
				url = "/";
			}
			
			elementInfo.setUrl(url);
			
			mapElementId(id, url);
		}
	}
	
	void mapElementId(String id, String url)
	throws EngineException
	{
		assert id != null;
		assert id.length() > 0;
		assert url != null;
		
		ElementInfo element_info = mData.mIdMapping.get(id);
		if (null == element_info)
		{
			throw new ElementIdNotFoundException(id);
		}
		
		// ensure that the root url always is '/' and not ''
		if (0 == url.length())
		{
			url = "/";
		}
		
		mData.mUrls = null;
		if (element_info.isPathInfoUsed())
		{
			List<ElementInfo> elements = mData.mPathinfoUrlMapping.get(url);
			if (null == elements)
			{
				elements = new ArrayList<ElementInfo>();
				mData.mPathinfoUrlMapping.put(url, elements);
			}
			
			elements.add(element_info);
		}
		else
		{
			if (mData.mUrlMapping.containsKey(url))
			{
				throw new UrlExistsException(id, url, mData.mUrlMapping.get(url).getId());
			}
			
			mData.mUrlMapping.put(url, element_info);
		}
	}
	
	void addFallback(ElementInfo elementInfo, String url)
	throws EngineException
	{
		assert elementInfo != null;
		
		if (url != null)
		{
			if (mData.mFallbackUrlMapping.containsKey(url))
			{
				throw new FallbackUrlExistsException(url);
			}
			mData.mFallbackUrlMapping.put(url, elementInfo);
		}
	}
	
	/**
	 * Searches which element would be used as a fallback for a particilar URL.
	 *
	 * @param url the URL for which a fallback should be found
	 * @return the fallback element; or
	 * <p><code>null</code> if no fallback is available for that URL
	 * @since 1.0
	 */
	public ElementInfo searchFallback(String url)
	throws EngineException
	{
		if (null == url)	throw new IllegalArgumentException("url can't be null;");
		
		checkModification();
		
		String best_match = null;
		if (0 == url.length())
		{
			url = "/";
		}
		
		for (String fallback_url : mData.mFallbackUrlMapping.keySet())
		{
			if (url.startsWith(fallback_url) &&
				(null == best_match || fallback_url.length() > best_match.length()))
			{
				best_match = fallback_url;
			}
		}
		
		if (best_match != null)
		{
			return mData.mFallbackUrlMapping.get(best_match);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Indicates whether a certain URL has a mapping in this site.
	 *
	 * @param url the URL that should be looked up
	 * @return <code>true</code> if the URL corresponds to and element; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean containsUrl(String url)
	{
		if (null == url)	throw new IllegalArgumentException("url can't be null;");
		
		checkModification();
 
		if (0 == url.length())
		{
			url = "/";
		}
		
		if (mData.mUrlMapping.containsKey(url))
		{
			return true;
		}
		
		if (url.length() > 0 &&
			'/' == url.charAt(url.length()-1))
		{
			String stripped_url = url.substring(0, url.length()-1);
			// if the url contains a dot in the last part, it shouldn't be
			// seen as simulating a directory
			if (stripped_url.lastIndexOf('.') <= stripped_url.lastIndexOf('/') &&
				mData.mUrlMapping.containsKey(stripped_url))
			{
				return true;
			}
		}
		
		if (mData.mPathinfoUrlMapping.containsKey(url))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Looks up the information of the element that is responsible for handling
	 * a certain URL and pathinfo.
	 *
	 * @param url the URL that should be looked up
	 * @param pathinfo the pathinfo that should be taken into account
	 * @return the corresponding element information; or
	 * <p><code>null</code> if the URL and pathinfo aren't registered in this site
	 * @since 1.4
	 */
	public ElementInfo resolveUrl(String url, String pathinfo)
	throws EngineException
	{
		if (null == url)	throw new IllegalArgumentException("url can't be null;");
		
		checkModification();
		
		if (0 == url.length())
		{
			url = "/";
		}

		ElementInfo result;
		
		if (null == pathinfo)
		{
			result = mData.mUrlMapping.get(url);
			if (result != null)
			{
				return result;
			}
			
			if (url.length() > 0 &&
				'/' == url.charAt(url.length()-1))
			{
				String stripped_url = url.substring(0, url.length()-1);
				// if the url contains a dot in the last part, it shouldn't be
				// seen as simulating a directory
				if (stripped_url.lastIndexOf('.') <= stripped_url.lastIndexOf('/'))
				{
					result = mData.mUrlMapping.get(stripped_url);
					if (result != null)
					{
						return result;
					}
				}
			}
		}
		
		result = resolvePathInfoUrl(url, pathinfo);
		
		return result;
	}

	/**
	 * Looks for an element that corresponds to a particular request URL.
	 * <p>
	 * This method will determine the best element match by stepping up the path
	 * segments. It will also look for fallback elements, cater for trailing
	 * slashes, and figure out the correct pathinfo.
	 * <p>
	 * Basically, this is the method that is used by the <code>Gate</code> to
	 * figure out which element to service when a request arrives.
	 *
	 * @param elementUrl the URL that will be used to search for the element
	 *
	 * @return an instance of <code>ElementToService</code> when an element match
	 * was found; or
	 * <p><code>null</code> if no suitable element could be found.
	 * 
	 * @since 1.6
	 */
	public ElementToService findElementForRequest(String elementUrl)
	{
		// obtain the element info that mapped to the requested path info
		ElementInfo		element_info = null;
		StringBuilder	element_url_buffer = new StringBuilder(elementUrl);
		int				element_url_location = -1;
		String			element_path_info = "";
		String			pathinfo = null;
		do
		{
			// if a slash was found in the url, it was stripped away
			// and thus the only urls that should match then are path info
			// urls
			if (element_url_location > -1)
			{
				pathinfo = elementUrl.substring(element_url_location);
			}
			element_info = resolveUrl(element_url_buffer.toString(), pathinfo);
			
			if (element_info != null)
			{
				break;
			}

			element_url_location = element_url_buffer.lastIndexOf("/");
			if (-1 == element_url_location)
			{
				break;
			}
			element_url_buffer.setLength(element_url_location);
		}
		while (element_url_location != -1);

		// no target element, get the fallback element
		if (null == element_info)
		{
			element_info = searchFallback(elementUrl);
			if (null == element_info)
			{
				return null;
			}
		}
		// otherwise get the target element's path info
		else
		{
			// only accept pathinfo if the element accepts it
			if (!element_info.isPathInfoUsed() &&
				elementUrl.length() != element_url_buffer.length())
			{
				// check for a fallback element
				element_info = searchFallback(elementUrl);
				if (null == element_info)
				{
					return null;
				}
			}
			else if (element_info.isPathInfoUsed())
			{
				// construct the element path info
				element_path_info = elementUrl.substring(element_url_buffer.length());
				// always ensure that the path info starts with a slash
				// this can not be present if the concerned element is
				// an arrival for instance
				if (!element_path_info.startsWith("/"))
				{
					element_path_info = "/"+element_path_info;
				}
			}
		}
		
		// if no element info was found, don't return an ElementToService match
		if (null == element_info)
		{
			return null;
		}
		
		return new ElementToService(element_info, element_path_info);
	}
	
	private ElementInfo resolvePathInfoUrl(String url, String pathinfo)
	throws EngineException
	{
		List<ElementInfo> elements = mData.mPathinfoUrlMapping.get(url);
		if (null == elements ||
			0 == elements.size())
		{
			return null;
		}
		
		// if a pathinfo was provided, check the pathinfo mappings
		// for the first that matches
		if (pathinfo != null)
		{
			for (ElementInfo element : elements)
			{
				if (element.hasPathInfoMappings())
				{
					for (PathInfoMapping mapping : element.getPathInfoMappings())
					{
						Matcher matcher = mapping.getRegexp().matcher(pathinfo);
						if (matcher.matches())
						{
							return element;
						}
					}
				}
			}
		}
		
		// return the first element that handles the url and doesn't have
		// any pathinfo mappings
		for (ElementInfo element : elements)
		{
			if (!element.hasPathInfoMappings() ||
				PathInfoMode.LOOSE.equals(element.getPathInfoMode()))
			{
				return element;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the collection of all the URLs that are present in this site.
	 *
	 * @return the collection of all the URLs in this site
	 * @since 1.0
	 */
	public Collection<String> getUrls()
	{
		if (null == mData.mUrls)
		{
			ArrayList urls = new ArrayList<String>(mData.mUrlMapping.keySet());
			urls.addAll(mData.mPathinfoUrlMapping.keySet());
			mData.mUrls = urls;
		}
		
		return mData.mUrls;
	}
	
	/**
	 * Indicates whether an absolute element ID is present in the site.
	 *
	 * @param id the absolute element ID that should be looked up
	 * @return <code>true</code> if the element ID could be found; or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean containsId(String id)
	{
		if (null == id)			throw new IllegalArgumentException("id can't be null.");
		if (0 == id.length())	throw new IllegalArgumentException("id can't be empty.");
		
		return mData.mIdMapping.containsKey(id);
	}
	
	/**
	 * Retrieves the element information in this site that corresponds to
	 * provided absolute element ID.
	 *
	 * @param id the absolute element ID that should be looked up
	 * @return the corresponding element information; or
	 * <p><code>null</code> if the absolute element ID couldn't be found
	 * @since 1.0
	 */
	public ElementInfo resolveId(String id)
	throws EngineException
	{
		return resolveId(id, null);
	}
	
	/**
	 * Retrieves the element information in this site that corresponds to
	 * provided element ID.
	 *
	 * @param id the element ID that should be looked up
	 * @param reference the element information that should be used as a
	 * reference to look up the element information from when a relative ID
	 * is provided
	 * @return the corresponding element information; or
	 * <p><code>null</code> if the element ID couldn't be found
	 * @since 1.0
	 */
	public ElementInfo resolveId(String id, ElementInfo reference)
	throws EngineException
	{
		if (null == id)			throw new IllegalArgumentException("id can't be null.");
		if (0 == id.length())	throw new IllegalArgumentException("id can't be empty.");
		
		checkModification();
		
		String absolute_id = getCanonicalId(getAbsoluteId(id, reference));
		
		return mData.mIdMapping.get(absolute_id);
	}
	
	/**
	 * Transforms the provided element ID into an absolute element ID.
	 *
	 * @param id the element ID that should be transformed
	 * @param reference the element information that should be used as a
	 * reference to look up the element information from when a relative ID
	 * is provided
	 * @return the absolute element ID that corresponds to the provided
	 * element ID
	 * @since 1.0
	 */
	public static String getAbsoluteId(String id, ElementInfo reference)
	{
		if (null == id)			throw new IllegalArgumentException("id can't be null.");
		if (0 == id.length())	throw new IllegalArgumentException("id can't be empty.");

		// resolve a relative element id
		if (!id.startsWith("."))
		{
			if (null == reference)	throw new IllegalArgumentException("reference can't be null for a relative element id.");
			
			String			path_id = reference.getReferenceId().substring(0, reference.getReferenceId().lastIndexOf(".")+1);
			StringBuilder	absolute_id = new StringBuilder(path_id);
			absolute_id.append(id);
			id = absolute_id.toString();
		}
		
		return id;
	}
	
	/**
	 * Transforms the provided element ID into a canonical ID without any
	 * parent indicators.
	 *
	 * @param id the element ID that should be transformed
	 * @return the canonical element ID that corresponds to the provided
	 * element ID
	 * @since 1.0
	 */
	public static String getCanonicalId(String id)
	{
		if (null == id)
		{
			return null;
		}
		
		StringBuilder	canonical_id = new StringBuilder();
		
		StringTokenizer	id_tok = new StringTokenizer(id, ".^", true);
		String			token = null;
		int				seperator_index = -1;
		while (id_tok.hasMoreTokens())
		{
			token = id_tok.nextToken();
			if (token.equals("."))
			{
				// do nothing
			}
			else if (token.equals("^"))
			{
				// fold back to the previous element path part
				// and don't go further than an empty string
				seperator_index = canonical_id.lastIndexOf(".");
				if (seperator_index != -1)
				{
					canonical_id.setLength(seperator_index);
				}
			}
			else
			{
				canonical_id.append(".");
				canonical_id.append(token);
			}
		}
		
		// handle arrival elements
		if (id.endsWith("."))
		{
			canonical_id.append(".");
		}
		
		return canonical_id.toString();
	}
	
	/**
	 * Adds the specified listener to receive site-related events.
	 * If <code>listener</code> is null, no exception is thrown and no action
	 * is performed.
	 *
	 * @param listener The site listener that will be added.
	 * @see SiteListener
	 * @see #removeListener(SiteListener)
	 * @since 1.5
	 */
	public void addListener(SiteListener listener)
	{
		if (null == listener)	return;
		
		if (null == mListeners)
		{
			mListeners = new HashSet<SiteListener>();
		}
		mListeners.add(listener);
	}
	
	/**
	 * Removes the site listener so that it no longer receives any events. This
	 * method performs no function, nor does it throw an exception if the listener
	 * specified by the argument was not previously added to this component or is
	 * <code>null</code>.
	 *
	 * @param listener The site listener that will be removed.
	 * @see SiteListener
	 * @see #addListener(SiteListener)
	 * @since 1.5
	 */
	public void removeListener(SiteListener listener)
	{
		if (mListeners != null)
		{
			mListeners.remove(listener);
		}
	}
	
	/**
	 * Notifies the registered listeners that one of the site's resources has
	 * been detected as being modified.
	 *
	 * @see SiteListener
	 * @since 1.5
	 */
	public void fireModified()
	{
		if (mListeners != null &&
			mListeners.size() > 0)
		{
			for (SiteListener listener : mListeners)
			{
				listener.modified(this);
			}
		}
	}
	
	Map<String, Method> getCachedOutputGetters(String elementId)
	{
		return mData.mOutputGettersCache.get(elementId);
	}
	
	void putCachedOutputGetters(String elementId, Map<String, Method> outputGetters)
	{
		synchronized (mData)
		{
			mData.mOutputGettersCache.put(elementId, outputGetters);
		}
	}
	
	Map<String, Method> getCachedOutbeanGetters(String elementId)
	{
		return mData.mOutbeanGettersCache.get(elementId);
	}
	
	void putCachedOutbeanGetters(String elementId, Map<String, Method> outbeanGetters)
	{
		synchronized (mData)
		{
			mData.mOutbeanGettersCache.put(elementId, outbeanGetters);
		}
	}
	
	Map<String, Method> getCachedOutcookieGetters(String elementId)
	{
		return mData.mOutcookieGettersCache.get(elementId);
	}
	
	void putCachedOutcookieGetters(String elementId, Map<String, Method> outcookieGetters)
	{
		synchronized (mData)
		{
			mData.mOutcookieGettersCache.put(elementId, outcookieGetters);
		}
	}
	
	Map<String, Method> getCachedPropertySetters(String elementId)
	{
		return mData.mPropertySettersCache.get(elementId);
	}
	
	void putCachedPropertySetters(String elementId, Map<String, Method> propertySetters)
	{
		synchronized (mData)
		{
			mData.mPropertySettersCache.put(elementId, propertySetters);
		}
	}
	
	Map<String, Method> getCachedIncookieSetters(String elementId)
	{
		return mData.mIncookieSettersCache.get(elementId);
	}
	
	void putCachedIncookieSetters(String elementId, Map<String, Method> incookieSetters)
	{
		synchronized (mData)
		{
			mData.mIncookieSettersCache.put(elementId, incookieSetters);
		}
	}
	
	Map<String, Method> getCachedInputSetters(String elementId)
	{
		return mData.mInputSettersCache.get(elementId);
	}
	
	void putCachedInputSetters(String elementId, Map<String, Method> inputSetters)
	{
		synchronized (mData)
		{
			mData.mInputSettersCache.put(elementId, inputSetters);
		}
	}
	
	Map<String, Method> getCachedInbeanSetters(String elementId)
	{
		return mData.mInbeanSettersCache.get(elementId);
	}
	
	void putCachedInbeanSetters(String elementId, Map<String, Method> inbeanSetters)
	{
		synchronized (mData)
		{
			mData.mInbeanSettersCache.put(elementId, inbeanSetters);
		}
	}
	
	SubmissionSettersCache getSubmissionSettersCache(String elementId)
	{
		return mData.mSubmissionSettersCache.get(elementId);
	}
	
	void putSubmissionSettersCache(String elementId, SubmissionSettersCache submissionSettersCache)
	{
		synchronized (mData)
		{
			mData.mSubmissionSettersCache.put(elementId, submissionSettersCache);
		}
	}
	
	private class SiteData
	{
		private final Map<String, ElementInfo>				mUrlMapping = new LinkedHashMap<String, ElementInfo>();
		private final Map<String, List<ElementInfo>>		mPathinfoUrlMapping = new LinkedHashMap<String, List<ElementInfo>>();
		private final Map<String, ElementInfo>				mFallbackUrlMapping = new HashMap<String, ElementInfo>();
		private final Map<String, ElementInfo>				mIdMapping = new LinkedHashMap<String, ElementInfo>();
		private final ContinuationManager<ElementSupport>	mContinuationManager = new ContinuationManager<ElementSupport>(EngineContinuationConfigRuntimeSingleton.INSTANCE);

		private List<String>						mUrls = null;
		private ResourceFinder						mResourceFinder = null;
		private HashMap<UrlResource, Long>			mResourceModificationTimes = null;
		
		private Map<String, Map<String, Method>>	mOutputGettersCache;
		private Map<String, Map<String, Method>>	mOutbeanGettersCache;
		private Map<String, Map<String, Method>>	mOutcookieGettersCache;
		
		private Map<String, Map<String, Method>>	mPropertySettersCache;
		private Map<String, Map<String, Method>>	mIncookieSettersCache;
		private Map<String, Map<String, Method>>	mInputSettersCache;
		private Map<String, Map<String, Method>>	mInbeanSettersCache;
		private Map<String, SubmissionSettersCache>	mSubmissionSettersCache;

		SiteData()
		{		
			clearCaches();
		}
		
        void clearCaches()
        {
			if (TerracottaUtils.isTcPresent())
			{

				mOutputGettersCache = new HashMap<String, Map<String, Method>>();
				mOutbeanGettersCache = new HashMap<String, Map<String, Method>>();
				mOutcookieGettersCache = new HashMap<String, Map<String, Method>>();

				mPropertySettersCache = new HashMap<String, Map<String, Method>>();
				mIncookieSettersCache = new HashMap<String, Map<String, Method>>();
				mInputSettersCache = new HashMap<String, Map<String, Method>>();
				mInbeanSettersCache = new HashMap<String, Map<String, Method>>();
				mSubmissionSettersCache = new HashMap<String, SubmissionSettersCache>();
			}
			else
			{
				mOutputGettersCache = new WeakHashMap<String, Map<String, Method>>();
				mOutbeanGettersCache = new WeakHashMap<String, Map<String, Method>>();
				mOutcookieGettersCache = new WeakHashMap<String, Map<String, Method>>();
	
				mPropertySettersCache = new WeakHashMap<String, Map<String, Method>>();
				mIncookieSettersCache = new WeakHashMap<String, Map<String, Method>>();
				mInputSettersCache = new WeakHashMap<String, Map<String, Method>>();
				mInbeanSettersCache = new WeakHashMap<String, Map<String, Method>>();
				mSubmissionSettersCache = new WeakHashMap<String, SubmissionSettersCache>();
			}
        }
    }
}

class SubmissionSettersCache
{
	private Map<String, Map<String, Method>>	mSubmissionparamSettersCache = new HashMap<String, Map<String, Method>>();
	private Map<String, Map<String, Method>>	mSubmissionbeanSettersCache = new HashMap<String, Map<String, Method>>();
	private Map<String, Map<String, Method>>	mUploadedfileSettersCache = new HashMap<String, Map<String, Method>>();
	
	Map<String, Method> getCachedSubmissionparamSetters(String submissionName)
	{
		return mSubmissionparamSettersCache.get(submissionName);
	}
	
	synchronized void putCachedSubmissionparamSetters(String submissionName, Map<String, Method> submissionparamSetters)
	{
		mSubmissionparamSettersCache.put(submissionName, submissionparamSetters);
	}
	
	Map<String, Method> getCachedSubmissionbeanSetters(String submissionName)
	{
		return mSubmissionbeanSettersCache.get(submissionName);
	}
	
	synchronized void putCachedSubmissionbeanSetters(String submissionName, Map<String, Method> submissionbeanSetters)
	{
		mSubmissionbeanSettersCache.put(submissionName, submissionbeanSetters);
	}
	
	Map<String, Method> getCachedUploadedfileSetters(String submissionName)
	{
		return mUploadedfileSettersCache.get(submissionName);
	}
	
	synchronized void putCachedUploadedfileSetters(String submissionName, Map<String, Method> uploadedfileSetters)
	{
		mUploadedfileSettersCache.put(submissionName, uploadedfileSetters);
	}
}
