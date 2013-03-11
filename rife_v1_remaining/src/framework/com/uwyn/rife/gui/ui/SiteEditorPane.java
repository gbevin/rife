/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteEditorPane.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui;

import com.uwyn.rife.gui.model.SiteModel;

public class SiteEditorPane extends EditorPane
{
	private SiteModel mSiteModel = null;

	public SiteEditorPane(SiteModel site)
    {
        super();

		mSiteModel = site;

        this.setView(new SiteEditorView(this));
        this.setToolBar(new SiteEditorToolBar(this));
    }

	public SiteModel getSiteModel()
	{
		return mSiteModel;
	}
}


