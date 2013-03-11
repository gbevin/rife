/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StructureEditor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import javax.swing.*;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.swing.Images;
import com.uwyn.rife.swing.JBorderlessButton;
import com.uwyn.rife.swing.JBorderlessToggleButton;
import com.uwyn.rife.tools.Localization;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class StructureEditor extends JPanel implements ActionListener, MouseListener
{
	private JScrollPane					mScrollPane = null;

	private JBorderlessButton			mStructurePanelPanButton = null;
	private StructurePanelPanWindow		mStructurePanelPanWindow = null;

	private JPanel						mStructureToolbar = null;
	private ButtonGroup					mStructureToolbarButtonGroup = null;
	private JBorderlessToggleButton		mStructureToolbarSelectionButton = null;
	private JBorderlessToggleButton		mStructureToolbarZoomButton = null;
	private JBorderlessToggleButton		mStructureToolbarElementButton = null;
	private JBorderlessToggleButton		mStructureToolbarConnectorButton = null;
	private JBorderlessToggleButton		mStructureToolbarGridShowButton = null;
	private JBorderlessToggleButton		mStructureToolbarGridSnapButton = null;
	private StructurePanel				mStructurePanel = null;

	public StructureEditor()
	{
		super();

		mStructureToolbarButtonGroup = new ButtonGroup();
		mStructureToolbarSelectionButton = new JBorderlessToggleButton(Images.getRepInstance().getImageIcon("buttons/selection.gif"));
		mStructureToolbarSelectionButton.setBorder(null);
		mStructureToolbarSelectionButton.setSelected(true);
		mStructureToolbarSelectionButton.addActionListener(this);
		mStructureToolbarSelectionButton.setToolTipText(Localization.getString("rife.tooltip.tool.selection"));
		mStructureToolbarButtonGroup.add(mStructureToolbarSelectionButton);
		mStructureToolbarZoomButton = new JBorderlessToggleButton(Images.getRepInstance().getImageIcon("buttons/zoom.gif"));
		mStructureToolbarZoomButton.setBorder(null);
		mStructureToolbarZoomButton.addActionListener(this);
		mStructureToolbarZoomButton.addMouseListener(this);
		mStructureToolbarZoomButton.setToolTipText(Localization.getString("rife.tooltip.tool.zoom"));
		mStructureToolbarButtonGroup.add(mStructureToolbarZoomButton);
		mStructureToolbarElementButton = new JBorderlessToggleButton(Images.getRepInstance().getImageIcon("buttons/element.gif"));
		mStructureToolbarElementButton.setBorder(null);
		mStructureToolbarElementButton.addActionListener(this);
		mStructureToolbarElementButton.setToolTipText(Localization.getString("rife.tooltip.tool.element"));
		mStructureToolbarButtonGroup.add(mStructureToolbarElementButton);
		mStructureToolbarConnectorButton = new JBorderlessToggleButton(Images.getRepInstance().getImageIcon("buttons/connector.gif"));
		mStructureToolbarConnectorButton.setBorder(null);
		mStructureToolbarConnectorButton.addActionListener(this);
		mStructureToolbarConnectorButton.setToolTipText(Localization.getString("rife.tooltip.tool.connector"));
		mStructureToolbarButtonGroup.add(mStructureToolbarConnectorButton);
		mStructureToolbarGridShowButton = new JBorderlessToggleButton(Images.getRepInstance().getImageIcon("buttons/gridshow.gif"));
		mStructureToolbarGridShowButton.setBorder(null);
		mStructureToolbarGridShowButton.addActionListener(this);
		mStructureToolbarGridShowButton.setToolTipText(Localization.getString("rife.tooltip.tool.gridshow"));
		mStructureToolbarGridShowButton.setSelected(Config.getRepInstance().getBool("GRID_SHOW"));
		mStructureToolbarGridSnapButton = new JBorderlessToggleButton(Images.getRepInstance().getImageIcon("buttons/gridsnap.gif"));
		mStructureToolbarGridSnapButton.setBorder(null);
		mStructureToolbarGridSnapButton.addActionListener(this);
		mStructureToolbarGridSnapButton.setToolTipText(Localization.getString("rife.tooltip.tool.gridsnap"));
		mStructureToolbarGridSnapButton.setSelected(Config.getRepInstance().getBool("GRID_SNAP"));

		mStructureToolbar = new JPanel();
		mStructureToolbar.setLayout(new GridBagLayout());
		mStructureToolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 0, 0, 0);
		mStructureToolbar.add(mStructureToolbarSelectionButton, constraints);
        constraints.gridy = 1;
		mStructureToolbar.add(new JSeparator(SwingConstants.HORIZONTAL), constraints);
        constraints.gridy = 2;
		mStructureToolbar.add(mStructureToolbarZoomButton, constraints);
        constraints.gridy = 3;
		mStructureToolbar.add(new JSeparator(SwingConstants.HORIZONTAL), constraints);
        constraints.gridy = 4;
		mStructureToolbar.add(mStructureToolbarElementButton, constraints);
        constraints.gridy = 5;
		mStructureToolbar.add(mStructureToolbarConnectorButton, constraints);
        constraints.gridy = 6;
		mStructureToolbar.add(new JSeparator(SwingConstants.HORIZONTAL), constraints);
        constraints.gridy = 7;
		mStructureToolbar.add(mStructureToolbarGridShowButton, constraints);
        constraints.gridy = 8;
        constraints.insets = new Insets(1, 0, 0, 0);
		mStructureToolbar.add(mStructureToolbarGridSnapButton, constraints);
        constraints.gridy = 9;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
		mStructureToolbar.add(new JPanel(), constraints);

		mScrollPane = new JScrollPane();
		mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mStructurePanelPanButton = new JBorderlessButton(Images.getRepInstance().getImageIcon("buttons/pan.gif"));
		mStructurePanelPanButton.setDefaultCapable(false);
		mStructurePanelPanButton.setBorder(null);
		mStructurePanelPanButton.addMouseListener(this);
		mStructurePanelPanButton.setToolTipText(Localization.getString("rife.tooltip.tool.pan"));
		mScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, mStructurePanelPanButton);

		mStructurePanel = new StructurePanel(mScrollPane);
		mStructurePanel.setVisible(true);

		mScrollPane.setViewportView(mStructurePanel);
		mScrollPane.setVisible(true);
		mScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);

		mStructureToolbar.setVisible(true);

		setLayout(new BorderLayout());
		add(mStructureToolbar, BorderLayout.WEST);
		add(mScrollPane, BorderLayout.CENTER);


		for(int i = 1; i <= 5; i++)
		{
			mStructurePanel.addElement("test_element_"+i);
		}
	}

	public StructurePanel getStructurePanel()
	{
		return mStructurePanel;
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if(source == mStructureToolbarSelectionButton)
		{
			mStructurePanel.setActiveTool(StructurePanel.SELECTION_TOOL);
		}
		else if(source == mStructureToolbarZoomButton)
		{
			mStructurePanel.setActiveTool(StructurePanel.ZOOMIN_TOOL);
		}
		else if(source == mStructureToolbarElementButton)
		{
			mStructurePanel.setActiveTool(StructurePanel.ELEMENT_TOOL);
		}
		else if(source == mStructureToolbarConnectorButton)
		{
			mStructurePanel.setActiveTool(StructurePanel.CONNECTOR_TOOL);
		}
		else if(source == mStructureToolbarGridShowButton)
		{
			Config.getRepInstance().setParameter("GRID_SHOW", ""+mStructureToolbarGridShowButton.isSelected());
			mStructurePanel.repaint();
		}
		else if(source == mStructureToolbarGridSnapButton)
		{
			Config.getRepInstance().setParameter("GRID_SNAP", ""+mStructureToolbarGridSnapButton.isSelected());
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		Object source = e.getSource();

		if(source == mStructureToolbarZoomButton)
		{
			if(e.getClickCount() == 2)
			{
				mStructurePanel.changeZoom(1/mStructurePanel.getScaleFactor());
			}
		}
	}

	public void mousePressed(MouseEvent e)
	{
		Object source = e.getSource();

		if(source == mStructurePanelPanButton &&
		   ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0))
		{
			mStructurePanel.setScrollActive(true);
			mStructurePanel.repaint();

			mStructurePanelPanWindow = new StructurePanelPanWindow(mStructurePanel, mStructurePanelPanButton, e.getPoint());
			mStructurePanelPanButton.addMouseMotionListener(mStructurePanelPanWindow);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		Object source = e.getSource();

		if(source == mStructurePanelPanButton &&
		   ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0))
		{
			mStructurePanelPanButton.removeMouseMotionListener(mStructurePanelPanWindow);

			mStructurePanelPanWindow.setVisible(false);
			mStructurePanelPanWindow.dispose();
			mStructurePanelPanWindow = null;

			mStructurePanel.setScrollActive(false);
			mStructurePanel.repaint();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}
}
