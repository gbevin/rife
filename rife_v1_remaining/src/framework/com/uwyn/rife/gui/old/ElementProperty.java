/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.uwyn.rife.swing.JDialogError;

abstract class ElementProperty
{
	public static final int	TITLE = 0;
	public static final int EXIT = 1;
	public static final int CONSUMED_PARAMETER = 2;
	public static final int USED_PARAMETER = 3;
	public static final int ADDED_PARAMETER = 4;

	public static final String	VALID_CHARS = "abcdefghjiklmnopqrstuvwxyzABCDEFGHJIKLMNOPQRSTUVWXYZ0123456789*?_";

	private Element			mElement = null;
	private String			mName = null;

	private float			mScaleFactor = 1f;

	private Rectangle2D		mNameBoundsOrig = null;
	private Rectangle2D		mNameBoundsScaled = null;
	private Area			mHotSpotOrig = null;
	private Area			mHotSpotScaled = null;

	public ElementProperty(Element element, String name)
	{
		setElement(element);
		setName(name);
	}

	public void drawActive(Graphics2D g2d)
	{
		Rectangle clip = g2d.getClipBounds();
		if(clip == null ||
		   clip.intersects(getNameBounds()))
		{
			Composite previous_composite = g2d.getComposite();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.setColor(Color.white);
			g2d.fill(getHotSpot());
			g2d.setComposite(previous_composite);
		}
	}

	public boolean equals(Object object)
	{
		if(object instanceof ElementProperty)
		{
			ElementProperty property = (ElementProperty)object;
			if(property.getElement() == getElement() &&
			   property.getClass() == getClass() &&
			   property.getName().equals(getName()))
			{
				return true;
			}
		}

		return false;
	}

	public void setElement(Element element)
	{
		mElement = element;
	}

	public Element getElement()
	{
		return mElement;
	}

	public void setName(String name)
	{
		mName = name;
		clearNameBounds();
		clearHotSpot();
	}

	public String getName()
	{
		return mName;
	}

	public boolean isValidName(String name)
	{
		ElementProperty temp_property = getElement().getProperty(getClass(), name);
		if(temp_property == this ||
		   temp_property == null)
		{

			return true;
		}
		else
		{
			return false;
		}
	}

	public void clearNameBounds()
	{
		setNameBounds(null);
	}

	public void setNameBounds(Rectangle2D nameBounds)
	{
		mNameBoundsOrig = nameBounds;
		mNameBoundsScaled = null;
	}

	public Rectangle2D getNameBounds()
	{
		if(mNameBoundsOrig == null)
		{
			return null;
		}

		if(mNameBoundsScaled == null)
		{
			scaleNameBounds();
		}
		return mNameBoundsScaled;
	}

	public void clearHotSpot()
	{
		setHotSpot(null);
	}

	public void setHotSpot(Area hotSpot)
	{
		mHotSpotOrig = hotSpot;
		mHotSpotScaled = null;
	}

	public void setHotSpot(Shape hotSpot)
	{
		mHotSpotOrig = new Area(hotSpot);
		mHotSpotScaled = null;
	}

	public Area getHotSpot()
	{
		if(mHotSpotOrig == null)
		{
			return null;
		}

		if(mHotSpotScaled == null)
		{
			scaleHotSpot();
		}
		return mHotSpotScaled;
	}

	private void resetScaledMembers()
	{
		mNameBoundsScaled = null;
		mHotSpotScaled = null;
	}

	public synchronized void scale(float multiplier)
	{
		mScaleFactor *= multiplier;

		resetScaledMembers();
	}

	private void scaleHotSpot()
	{
		AffineTransform scale_transform = AffineTransform.getScaleInstance(mScaleFactor, mScaleFactor);
		mHotSpotScaled = mHotSpotOrig.createTransformedArea(scale_transform);
	}

	private void scaleNameBounds()
	{
		mNameBoundsScaled = new Rectangle2D.Float((float)(mNameBoundsOrig.getX()*mScaleFactor), (float)(mNameBoundsOrig.getY()*mScaleFactor),
												  (float)(mNameBoundsOrig.getWidth()*mScaleFactor), (float)(mNameBoundsOrig.getHeight()*mScaleFactor));
	}

	public void showPopupMenu(Point clickedPoint)
	{
		JPopupMenu popup = new JPopupMenu();
		popup.addPopupMenuListener(getElement().getStructurePanel());
		showPopupMenuReal(popup, clickedPoint);
		popup.show(getElement(), clickedPoint.x, clickedPoint.y);
	}

	protected class Delete implements DynamicMenuAction
	{
		public void execute(JMenuItem menuItem)
		{
			getElement().removeProperty(ElementProperty.this);
			getElement().repaint();
		}
	}

	protected class Edit implements DynamicMenuAction
	{
		private Point	mClickedPoint = null;

		public Edit(Point clickedPoint)
		{
			mClickedPoint = clickedPoint;
		}

		public void execute(JMenuItem menuItem)
		{
			getElement().getStructurePanel().editElementProperty(ElementProperty.this, mClickedPoint);
			getElement().repaint();
		}
	}

	public abstract void draw(Graphics2D g2d);
	public abstract Color getColor();
	public abstract Font getDrawFont();
	public abstract int getEditAlignment();
	public abstract Font getEditFont();
	public abstract Rectangle getEditRectangle(Point clickedPoint);
	public abstract JDialogError getUnicityErrorDialog();
	protected abstract void showPopupMenuReal(JPopupMenu popupMenu, Point clickedPoint);
}
