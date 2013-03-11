/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DwrRifePageNormalizer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.ElementToService;
import com.uwyn.rife.engine.Site;
import org.directwebremoting.extend.PageNormalizer;

/**
 * Returns an element's reference ID according to the URL it was accessed with.
 * This is needed for DWR reverse Ajax to work.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 */
class DwrRifePageNormalizer implements PageNormalizer
{
	private Site mSite;
	
	public DwrRifePageNormalizer(Site site)
	{
		mSite = site;
	}
	
	public String normalizePage(String unnormalized)
	{
		ElementToService element_match = mSite.findElementForRequest(unnormalized);
		if (null == element_match)
		{
			return unnormalized;
		}
		
		return element_match.getElementInfo().getReferenceId();
	}
}


