/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EditorPane.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui;

import com.uwyn.rife.swing.Images;
import com.uwyn.rife.swing.JBorderlessButton;
import com.uwyn.rife.tools.Localization;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;

public abstract class EditorPane extends JPanel
{
    private EditorToolBar		mToolBar = null;
	private JScrollPane			mScrollPane = null;
    private EditorView			mView = null;

	private JBorderlessButton	mPanButton = null;
//	private PanWindow			mPanWindow = null;

	protected EditorPane()
	{
		super();

		mScrollPane = new JScrollPane();
		mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mPanButton = new JBorderlessButton(Images.getRepInstance().getImageIcon("pan.gif"));
		mPanButton.setDefaultCapable(false);
		mPanButton.setBorder(null);
//		mPanButton.addMouseListener(this);
		mPanButton.setToolTipText(Localization.getString("rife.tooltip.tool.pan"));
		mScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, mPanButton);
		mScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);

		setLayout(new BorderLayout());
	}

	protected void constructLayout()
	{
		mScrollPane.setViewportView(mView);
		if(mToolBar.getOrientation() == JToolBar.HORIZONTAL)
		{
			add(mToolBar, BorderLayout.NORTH);
		}
		else
		{
			add(mToolBar, BorderLayout.EAST);
		}
		add(mScrollPane, BorderLayout.CENTER);

		mToolBar.setVisible(true);
		mScrollPane.setVisible(true);
		mView.setVisible(true);
	}

	protected void setView(EditorView view)
    {
		mView = view;
    }

	protected void setToolBar(EditorToolBar toolbar)
    {
		mToolBar = toolbar;
    }

	public EditorToolBar getToolBar()
	{
		return mToolBar;
	}

	public JScrollPane getScrollPane()
	{
		return mScrollPane;
	}

	public EditorView getView()
	{
		return mView;
	}
}
