/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ManyToOneJoinColumnProcessor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

public interface ManyToOneJoinColumnProcessor
{
	boolean processJoinColumn(String columnName, String propertyName, ManyToOneDeclaration declaration);
}
