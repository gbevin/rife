/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StatefulExplicitProcessed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InputProperty;
import com.uwyn.rife.engine.annotations.OutputProperty;
import com.uwyn.rife.engine.annotations.SubmissionHandler;
import com.uwyn.rife.template.Template;

@Elem(
	url=""
)
public class StatefulExplicitProcessed extends Element {
	private boolean enabled = true;
	@InputProperty	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	@OutputProperty	public boolean getEnabled() { return enabled; }
	
	@SubmissionHandler
	public void doDisable() {
		enabled = false;
		processElement();
	}
	
	public void processElement() {
		Template t = getHtmlTemplate("engine_embedding_statefulexplicitprocessed");
		Object embed_data = getEmbedData();
		t.setValue("text", encodeHtml(String.valueOf(embed_data)));
		if (!enabled) {
			t.setBlock("content", "disabled");
		}
		print(t);
	}
}
