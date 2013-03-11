/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractManyToOneAssociationCollection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import java.util.Collection;
import java.util.List;

import static com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerRelationalUtils.generateManyToOneJoinColumnName;

abstract class AbstractManyToOneAssociationCollection<E> implements Collection<E>
{
	private AbstractGenericQueryManager		mQueryManager;
	private int								mObjectId;
	private ManyToOneAssociationDeclaration	mDeclaration;
	
	AbstractManyToOneAssociationCollection(AbstractGenericQueryManager manager, int objectId, ManyToOneAssociationDeclaration declaration)
	{
		mQueryManager = manager;
		mObjectId = objectId;
		mDeclaration = declaration;
	}
	
	protected List restoreManyToOneAssociations()
	{
		GenericQueryManager association_manager = mQueryManager.createNewManager(mDeclaration.getMainType());
		
		RestoreQuery restore_mappings = association_manager.getRestoreQuery()
			.fields(mDeclaration.getMainType())
			.where(generateManyToOneJoinColumnName(mDeclaration.getMainProperty(), mDeclaration.getMainDeclaration()), "=", mObjectId);
		
		// restore the many to one associations
		return association_manager.restore(restore_mappings);
	}
}
