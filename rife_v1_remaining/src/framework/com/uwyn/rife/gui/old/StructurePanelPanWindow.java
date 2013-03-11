/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StructurePanelPanWindow.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import com.uwyn.rife.swing.BorderEtched;

public class StructurePanelPanWindow extends JWindow implements MouseMotionListener
{
	private StructurePanel			mStructurePanel = null;
	private JComponent				mRelativeComponent = null;
	private Thumbnail				mThumbnail = null;
	private int						mViewWidth = 0;
	private int						mViewHeight = 0;
	private ViewRectangle			mViewRectangle = null;
	private Point					mInitialViewPosition = null;
	private Point					mStartPoint = null;

	public StructurePanelPanWindow(StructurePanel structurePanel, JComponent relativeComponent, Point startPoint)
	{
		mStructurePanel = structurePanel;
		mRelativeComponent = relativeComponent;
		mStartPoint = startPoint;

		mViewWidth = (int)mStructurePanel.getScrollPane().getViewport().getViewRect().getWidth();
		mViewHeight = (int)mStructurePanel.getScrollPane().getViewport().getViewRect().getHeight();
		mInitialViewPosition = mStructurePanel.getScrollPane().getViewport().getViewPosition();

		mThumbnail = new Thumbnail();
		mViewRectangle = new ViewRectangle();

		getContentPane().setLayout(null);
		getContentPane().add(mViewRectangle);
		getContentPane().add(mThumbnail);
		mViewRectangle.updateLocation();

		setSize(mThumbnail.getSize());

		updateLocation();

		setVisible(true);
	}

	private void updateLocation()
	{
		Point relative_component_location = mRelativeComponent.getLocationOnScreen();

		int base_offset_x = relative_component_location.x+(mRelativeComponent.getWidth()/2);
		int base_offset_y = relative_component_location.y+(mRelativeComponent.getHeight()/2);

		Point view_rectangle_center = mViewRectangle.getCenterPoint();

		base_offset_x -= view_rectangle_center.x;
		base_offset_y -= view_rectangle_center.y;

		Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
		if(base_offset_x+getWidth() > screen_size.width)
		{
			int new_offset_x = screen_size.width-getWidth();
			mStartPoint.x += new_offset_x-base_offset_x;
			base_offset_x = new_offset_x;
		}
		if(base_offset_y+getHeight() > screen_size.height)
		{
			int new_offset_y = screen_size.height-getHeight();
			mStartPoint.y += new_offset_y-base_offset_y;
			base_offset_y = new_offset_y;
		}
		
		setLocation(base_offset_x, base_offset_y);
	}

	private class ViewRectangle extends JPanel
	{
		private int	mWidth = 0;
		private int	mHeight = 0;
		private int mX = 0;
		private int mY = 0;

		public ViewRectangle()
		{
			super();
			setOpaque(false);
			mWidth = (int)(mViewWidth*mThumbnail.getScaleFactor());
			mHeight = (int)(mViewHeight*mThumbnail.getScaleFactor());
			ViewRectangle.this.setSize(mWidth, mHeight);
		}

		public Point getCenterPoint()
		{
			return new Point(mX+(mWidth/2), mY+(mHeight/2));
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			g.setColor(Color.black);
			g.drawRect(0, 0, mWidth-1, mHeight-1);
		}

		public void updateLocation()
		{
			Point view_position = mStructurePanel.getScrollPane().getViewport().getViewPosition();
			mX = (int)(view_position.x*mThumbnail.getScaleFactor()+mThumbnail.getInsets().left);
			mY = (int)(view_position.y*mThumbnail.getScaleFactor()+mThumbnail.getInsets().top);
			ViewRectangle.this.setLocation(mX, mY);
		}
	}

	private class Thumbnail extends JLabel
	{
		private int				mWidth = 200;
		private int				mHeight = 200;
		private float			mScaleFactor = 0;
		private BufferedImage	mThumbnailImage = null;
		private BorderEtched	mBorder = null;
		
		public Thumbnail()
		{
			float width_ratio = 200f/(float)mStructurePanel.getWidth();
			float height_ratio = 200f/(float)mStructurePanel.getHeight();

			if(width_ratio < height_ratio)
			{
				mHeight = (int)(200*(width_ratio/height_ratio));
				mScaleFactor = width_ratio;
			}
			else
			{
				mWidth = (int)(200*(height_ratio/width_ratio));
				mScaleFactor = height_ratio;
			}

			mThumbnailImage = new BufferedImage(mWidth, mHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = mThumbnailImage.createGraphics();
			ElementStyle.setRenderingHints(g2d, mScaleFactor);
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, mWidth, mHeight);
			g2d.scale(mScaleFactor, mScaleFactor);

			Component[] elements = mStructurePanel.getComponents();
			Element element = null;
			for(int i = elements.length-1; i >= 0; i--)
			{
				element = (Element)elements[i];
				g2d.translate(element.getX(), element.getY());
				element.drawElement(g2d);
				g2d.translate(-1*element.getX(), -1*element.getY());
			}

			setIcon(new ImageIcon(mThumbnailImage));
			mBorder = new BorderEtched(BorderEtched.RAISED);
			setBorder(mBorder);
			Insets insets = Thumbnail.this.getInsets();
			Thumbnail.this.setSize(mWidth+insets.left+insets.right, mHeight+insets.top+insets.bottom);
		}

		public float getScaleFactor()
		{
			return mScaleFactor;
		}
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseDragged(MouseEvent e)
	{
		Point current_point = e.getPoint();
		int offset_x = (int)((current_point.x-mStartPoint.x)/mThumbnail.getScaleFactor());
		int offset_y = (int)((current_point.y-mStartPoint.y)/mThumbnail.getScaleFactor());
		int new_view_position_x = 0;
		int new_view_position_y = 0;

		if(mViewWidth < mStructurePanel.getWidth())
		{
			new_view_position_x = mInitialViewPosition.x+offset_x;
			if(new_view_position_x+mViewWidth > mStructurePanel.getWidth())
			{
				new_view_position_x = mStructurePanel.getWidth()-mViewWidth;
			}
			if(new_view_position_x < 0)
			{
				new_view_position_x = 0;
			}
		}

		if(mViewHeight < mStructurePanel.getHeight())
		{
			new_view_position_y = mInitialViewPosition.y+offset_y;
			if(new_view_position_y+mViewHeight > mStructurePanel.getHeight())
			{
				new_view_position_y = mStructurePanel.getHeight()-mViewHeight;
			}
			if(new_view_position_y < 0)
			{
				new_view_position_y = 0;
			}
		}

		mStructurePanel.getScrollPane().getViewport().setViewPosition(new Point(new_view_position_x, new_view_position_y));
		mViewRectangle.updateLocation();
	}

	public void mouseMoved(MouseEvent e)
	{
	}
}
