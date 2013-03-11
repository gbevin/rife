/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TypesNode.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.Label;
import com.uwyn.rife.continuations.instrument.TypesContext;
import com.uwyn.rife.continuations.instrument.TypesInstruction;
import com.uwyn.rife.continuations.instrument.TypesNode;
import com.uwyn.rife.continuations.instrument.TypesSuccessor;
import java.util.ArrayList;
import java.util.Collection;
	
class TypesNode
{
	final static int	REGULAR = 0;
	final static int	EXCEPTION = 1;
	
	private ArrayList<TypesInstruction>	mInstructions = new ArrayList<TypesInstruction>();
	
	private TypesNode			mFollowingNode = null;
	private boolean				mIsSuccessor = false;
	private TypesSuccessor		mSuccessors = null;

	private int					mLevel = 0;
	private TypesContext		mContext = null;
	private boolean				mProcessed = false;
	private TypesNode			mNextToProcess = null;
	private TypesNode			mPreceeder = null;
	
	private int					mSort = REGULAR;
	
	void addInstruction(TypesInstruction instruction)
	{
		mInstructions.add(instruction);
	}
	
	Collection<TypesInstruction> getInstructions()
	{
		return mInstructions;
	}
	
	void setSort(int type)
	{
		mSort = type;
	}
	
	int getSort()
	{
		return mSort;
	}
	
	void setFollowingNode(TypesNode followingNode)
	{
		mFollowingNode = followingNode;
	}
	
	TypesNode getFollowingNode()
	{
		return mFollowingNode;
	}
	
	void addSuccessor(Label label)
	{
		TypesSuccessor successor = new TypesSuccessor();
		
		successor.setLabel(label);
		successor.setNextSuccessor(getSuccessors());
		
		setSuccessors(successor);
	}
	
	void setSuccessors(TypesSuccessor successors)
	{
		mSuccessors = successors;
	}
	
	TypesSuccessor getSuccessors()
	{
		return mSuccessors;
	}
	
	void setNextToProcess(TypesNode nextNode)
	{
		mNextToProcess = nextNode;
	}
	
	TypesNode getNextToProcess()
	{
		return mNextToProcess;
	}
	
	void setPreceeder(boolean isSuccessor, TypesNode preceeder)
	{
		mIsSuccessor = isSuccessor;
		mPreceeder = preceeder;
		
		if (mIsSuccessor)
		{
			mLevel = mPreceeder.getLevel()+1;
		}
		else
		{
			mLevel = mPreceeder.getLevel();
		}
	}
	
	TypesNode getPreceeder()
	{
		return mPreceeder;
	}
	
	boolean getIsSuccessor()
	{
		return mIsSuccessor;
	}
	
	void setProcessed(boolean processed)
	{
		mProcessed = processed;
	}
	
	boolean isProcessed()
	{
		return mProcessed;
	}
	
	void setContext(TypesContext previousContext)
	{
		mContext = previousContext;
	}
	
	TypesContext getContext()
	{
		return mContext;
	}
	
	int getLevel()
	{
		return mLevel;
	}
}

