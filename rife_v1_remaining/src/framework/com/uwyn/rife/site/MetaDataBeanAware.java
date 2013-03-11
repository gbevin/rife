/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataBeanAware.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

/**
 * This interface can optionally be implemented by a class implementing the
 * <code>MetaDataMerged</code> interface.
 * <p>By implementing the methods here, each meta data instance will be made
 * aware of the bean that has been associated with.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see MetaDataMerged
 * @version $Revision: 3918 $
 * @since 1.4
 */
public interface MetaDataBeanAware
{
	/**
	 * <p>This method will be called by RIFE when a new instance of the meta
	 * data class has been created.
	 * 
	 * @param bean the bean instance that this particular meta data instance
	 * has been associated with
	 * @since 1.4
	 */
	public void setMetaDataBean(Object bean);
	
	/**
	 * Has to return the bean instance that has been associated with this
	 * meta data class instance.
	 * 
	 * @return this meta data's bean instance
	 * @since 1.6.2
	 */
	public Object retrieveMetaDataBean();
}
