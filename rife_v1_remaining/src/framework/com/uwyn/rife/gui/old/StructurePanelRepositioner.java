/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StructurePanelRepositioner.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import javax.swing.*;

class StructurePanelRepositioner extends Thread
{
	private StructurePanel	mStructurePanel = null;
	private Point			mCenterPoint = null;
	private Cursor			mCursor = null;

	public StructurePanelRepositioner(StructurePanel structurePanel, Point centerPoint)
	{
		this(structurePanel, centerPoint, null);
	}

	public StructurePanelRepositioner(StructurePanel structurePanel, Point centerPoint, Cursor cursor)
	{
		mStructurePanel = structurePanel;
		mCenterPoint = centerPoint;
		mCursor = cursor;
	}
	
	public void run()
	{
		JScrollPane scroll_pane = mStructurePanel.getScrollPane();
		scroll_pane.getViewport().setView(mStructurePanel);
		if(mCenterPoint != null)
		{
			int view_width = scroll_pane.getWidth()-scroll_pane.getVerticalScrollBar().getWidth();
			int view_height = scroll_pane.getHeight()-scroll_pane.getHorizontalScrollBar().getHeight();
			int panel_width = mStructurePanel.getCalculatedWidth();
			int panel_height = mStructurePanel.getCalculatedHeight();

			int topleft_x = 0;
			
			if(view_width < panel_width)
			{
				topleft_x = mCenterPoint.x-view_width/2;
				if(topleft_x+view_width > panel_width)
				{
					topleft_x = panel_width-view_width;
				}
				if(topleft_x < 0)
				{
					topleft_x = 0;
				}
			}

			int topleft_y = 0;
			
			if(view_height < panel_height)
			{
				topleft_y = mCenterPoint.y-view_height/2;
				if(topleft_y+view_height > panel_height)
				{
					topleft_y = panel_height-view_height;
				}
				if(topleft_y < 0)
				{
					topleft_y = 0;
				}
			}

			scroll_pane.getViewport().setViewPosition(new Point(topleft_x, topleft_y));
		}
		if(mCursor != null)
		{
			mStructurePanel.setCursor(mCursor);
			mStructurePanel.getToolkit().sync();
		}
	}
}
