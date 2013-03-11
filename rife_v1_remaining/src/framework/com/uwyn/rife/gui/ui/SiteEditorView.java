/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteEditorView.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui;

import java.awt.*;

import com.uwyn.rife.config.Config;

public class SiteEditorView extends EditorView
{
	private float	mScaleFactor = 1f;

	public SiteEditorView(EditorPane pane)
	{
        super(pane);

        this.setBackground(Color.white);
	}

	protected Dimension calculateDimensionReal()
    {
        return new Dimension(800,600);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(Config.getRepInstance().getBool("GRID_SHOW"))
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.gray);
			Rectangle clip_bounds = g2d.getClipBounds();
			int grid_size = Config.getRepInstance().getInt("GRID_SIZE");
			double grid_size_scaled = grid_size*mScaleFactor;
			if(grid_size_scaled > 0)
			{
				while(grid_size_scaled < 5)
				{
					grid_size_scaled = grid_size_scaled*2;
				}
				double offset_x = clip_bounds.x-(clip_bounds.x%grid_size_scaled);
				double offset_y = clip_bounds.y-(clip_bounds.y%grid_size_scaled);
				double new_clip_width = clip_bounds.width+(clip_bounds.x%grid_size_scaled);
				double new_clip_height = clip_bounds.height+(clip_bounds.y%grid_size_scaled);
				int real_x = 0;
				int real_y = 0;
				for(double x = 0; x <= new_clip_width; x += grid_size_scaled)
				{
					for(double y = 0; y <= new_clip_height; y += grid_size_scaled)
					{
						real_x = (int)(offset_x+x);
						real_y = (int)(offset_y+y);
						g2d.drawLine(real_x, real_y, real_x, real_y);
					}
				}
			}
		}
	}
}
