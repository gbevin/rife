/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractManyToManyCollection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import static com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerRelationalUtils.generateManyToManyJoinColumnName;
import static com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerRelationalUtils.generateManyToManyJoinTableName;

import java.util.Collection;
import java.util.List;

import com.uwyn.rife.database.queries.Select;

abstract class AbstractManyToManyCollection<E> implements Collection<E>
{
	private AbstractGenericQueryManager	mQueryManager;
	private String						mColumnName1;
	private int							mObjectId;
	private ManyToManyDeclaration		mDeclaration;
	
	AbstractManyToManyCollection(AbstractGenericQueryManager manager, String columnName1, int objectId, ManyToManyDeclaration declaration)
	{
		mQueryManager = manager;
		mColumnName1 = columnName1;
		mObjectId = objectId;
		mDeclaration = declaration;
	}
	
	protected List restoreManyToManyMappings()
	{
		GenericQueryManager association_manager = mQueryManager.createNewManager(mDeclaration.getAssociationType());
		String join_table = generateManyToManyJoinTableName(mDeclaration, mQueryManager, association_manager);
		final String column2_name = generateManyToManyJoinColumnName(association_manager);
		
		RestoreQuery restore_mappings = association_manager.getRestoreQuery()
			.fields(mDeclaration.getAssociationType())
			.joinInner(join_table, Select.ON, association_manager.getTable()+"."+association_manager.getIdentifierName()+" = "+join_table+"."+column2_name)
			.where(join_table+"."+mColumnName1, "=", mObjectId);
		
		// restore the many to many associations
		return association_manager.restore(restore_mappings);
	}
}
