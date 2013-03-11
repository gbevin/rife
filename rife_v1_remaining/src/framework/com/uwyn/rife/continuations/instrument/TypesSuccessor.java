/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TypesSuccessor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.Label;
import com.uwyn.rife.continuations.instrument.TypesSuccessor;

class TypesSuccessor
{
	private Label			mLabel = null;
	private TypesSuccessor	mNextSuccessor = null;
	
	void setNextSuccessor(TypesSuccessor nextSuccessor)
	{
		mNextSuccessor = nextSuccessor;
	}
	
	TypesSuccessor getNextSuccessor()
	{
		return mNextSuccessor;
	}
	
	void setLabel(Label successor)
	{
		mLabel = successor;
	}
	
	Label getLabel()
	{
		return mLabel;
	}
} 

