/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EditorToolBar.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui;

import javax.swing.*;

public abstract class EditorToolBar extends JToolBar
{
    private EditorPane	mPane = null;

	private EditorToolBar()
	{
	}

	protected EditorToolBar(EditorPane pane)
	{
        this.mPane = pane;
	}

	public EditorPane getPane()
	{
		return mPane;
	}
}


