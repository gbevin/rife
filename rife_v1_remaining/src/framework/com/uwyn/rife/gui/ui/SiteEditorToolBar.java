/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteEditorToolBar.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui;

import javax.swing.*;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.gui.ui.actions.*;
import com.uwyn.rife.swing.JBorderlessToggleButton;

public class SiteEditorToolBar extends EditorToolBar
{
	private ButtonGroup					mToolsButtonGroup = null;
	private JBorderlessToggleButton		mSelectionButton = null;
	private JBorderlessToggleButton		mZoomButton = null;
	private JBorderlessToggleButton		mElementButton = null;
	private JBorderlessToggleButton		mConnectorButton = null;
	private JBorderlessToggleButton		mGridShowButton = null;
	private JBorderlessToggleButton		mGridSnapButton = null;

	public SiteEditorToolBar(EditorPane pane)
	{
        super(pane);

		mToolsButtonGroup = new ButtonGroup();
        EditorPaneToolAction action = null;

		mSelectionButton = new JBorderlessToggleButton();
        action = new SitestructurePaneSelectionToolAction();
        action.setEditorPane(pane);
		mSelectionButton.setAction(action);
		mSelectionButton.setSelected(true);
		mToolsButtonGroup.add(mSelectionButton);
		mZoomButton = new JBorderlessToggleButton();
        action = new SitestructurePaneZoomToolAction();
        action.setEditorPane(pane);
		mZoomButton.setAction(action);
//		mZoomButton.addMouseListener(this);
		mToolsButtonGroup.add(mZoomButton);
		mElementButton = new JBorderlessToggleButton();
        action = new SitestructurePaneElementToolAction();
        action.setEditorPane(pane);
		mElementButton.setAction(action);
		mToolsButtonGroup.add(mElementButton);
		mConnectorButton = new JBorderlessToggleButton();
        action = new SitestructurePaneConnectorToolAction();
        action.setEditorPane(pane);
		mConnectorButton.setAction(action);
		mToolsButtonGroup.add(mConnectorButton);
		mGridShowButton = new JBorderlessToggleButton();
        action = new SitestructurePaneGridShowToolAction();
        action.setEditorPane(pane);
		mGridShowButton.setAction(action);
		mGridShowButton.setSelected(Config.getRepInstance().getBool("GRID_SHOW"));
		mGridSnapButton = new JBorderlessToggleButton();
        action = new SitestructurePaneGridSnapToolAction();
        action.setEditorPane(pane);
		mGridSnapButton.setAction(action);
		mGridSnapButton.setSelected(Config.getRepInstance().getBool("GRID_SNAP"));

		this.add(mSelectionButton);
		this.add(new JToolBar.Separator());
		this.add(mZoomButton);
		this.add(new JToolBar.Separator());
		this.add(mElementButton);
		this.add(mConnectorButton);
		this.add(new JToolBar.Separator());
		this.add(mGridShowButton);
		this.add(mGridSnapButton);
	}
}


