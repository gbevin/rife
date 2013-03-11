/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EditorView.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui;

import java.awt.*;
import javax.swing.*;

public abstract class EditorView extends JPanel
{
    private EditorPane	mPane = null;
	private int 		mWidth = 0;
	private int 		mHeight = 0;
	private boolean		mScrollActive = false;

	private EditorView()
	{
	}

	protected EditorView(EditorPane pane)
	{
        this.mPane = pane;
		this.setDoubleBuffered(true);
		this.setOpaque(true);

		this.calculateDimension();
	}

	public EditorPane getPane()
	{
		return mPane;
	}

	protected abstract Dimension calculateDimensionReal();

	protected void calculateDimension()
    {
        Dimension dimension = this.calculateDimensionReal();
        this.mWidth = (int)dimension.getWidth();
        this.mHeight = (int)dimension.getHeight();
	}

	public int getCalculatedWidth()
	{
		return mWidth;
	}

	public int getCalculatedHeight()
	{
		return mHeight;
	}

	public Dimension getMinimumSize()
	{
		return new Dimension(mWidth, mHeight);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(mWidth, mHeight);
	}

	public boolean isScrollActive()
	{
		return mScrollActive;
	}

	public void setScrollActive(boolean active)
	{
		mScrollActive = active;
	}
}


