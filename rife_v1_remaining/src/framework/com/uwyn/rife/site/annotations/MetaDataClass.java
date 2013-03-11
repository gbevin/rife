/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.uwyn.rife.site.annotations;

import java.lang.annotation.*;

/**
 * Declares which class should be used for meta data merging.
 * <p>
 * This allows classes to not have to rely on the {@code MetaData} suffix when
 * they want to have another class merged.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3908 $
 * @since 1.6.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface MetaDataClass
{
	/**
	 * The class that will be merged during meta data merging.
	 * @return the class
	 * @since 1.6.2
	 */
	Class value();
}
