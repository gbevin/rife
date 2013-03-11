/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineContinuationsTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.instrument;

import com.uwyn.rife.continuations.instrument.ContinuationsTransformer;
import com.uwyn.rife.engine.EngineContinuationConfigInstrument;

/**
 * This is a bytecode transformer that will modify classes so that they
 * receive the functionalities that are required to support the continuations
 * functionalities as they are provided by RIFE's web engine.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class EngineContinuationsTransformer extends ContinuationsTransformer
{
	public EngineContinuationsTransformer()
	{
		super(new EngineContinuationConfigInstrument());
	}
}
