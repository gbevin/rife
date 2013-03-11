/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InheritanceGlobalvarEmbedding.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;

public class InheritanceGlobalvarEmbedding extends Element
{
	public void processElement()
	{
		print(getHtmlTemplate("engine_embedding_inheritance_globalvar_embedding"));
	}
}

