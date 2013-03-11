/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CmfValidation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

/**
 * This class extends <code>Validation</code> to create additional
 * <code>ValidationRule</code>s that correspond to CMF constraints.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @deprecated As of RIFE 1.3.2, all the methods moved to the base class
 *              {@link Validation}
 */
public class CmfValidation extends Validation<ConstrainedBean, ConstrainedProperty>
{
}
