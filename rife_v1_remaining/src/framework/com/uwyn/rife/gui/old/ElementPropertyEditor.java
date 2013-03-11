/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementPropertyEditor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.swing.JDialogError;
import com.uwyn.rife.swing.MouseMotionEventTranslator;
import com.uwyn.rife.swing.documents.CharFilterDocument;
import com.uwyn.rife.tools.Localization;

class ElementPropertyEditor extends JTextField implements KeyListener, ActionListener
{
	private ElementProperty	mElementProperty = null;
	private Rectangle		mEditRectangle = null;

	ElementPropertyEditor(ElementProperty elementProperty, Point referencePoint)
	{
		super(new CharFilterDocument(ElementProperty.VALID_CHARS, CharFilterDocument.VALID, ElementStyle.MAX_PROPERTY_STRING_LENGTH), elementProperty.getName(), 0);

		mElementProperty = elementProperty;
		mEditRectangle = mElementProperty.getEditRectangle(referencePoint);

		mElementProperty.getElement().add(ElementPropertyEditor.this);
		mElementProperty.getElement().getStructurePanel().elementPropertyHighlighted(mElementProperty);
		setBorder(null);
		setFont(mElementProperty.getEditFont());
		setHorizontalAlignment(mElementProperty.getEditAlignment());

		setBounds((int)mEditRectangle.getX(), (int)mEditRectangle.getY(), (int)mEditRectangle.getWidth(), (int)mEditRectangle.getHeight());

		setVisible(true);
		requestFocus();

		addActionListener(this);
		addKeyListener(this);
		addMouseMotionListener(new MouseMotionEventTranslator(getElementProperty().getElement()));
	}

	ElementProperty getElementProperty()
	{
		return mElementProperty;
	}

	void destroy()
	{
		removeKeyListener(this);
		removeActionListener(this);
		Element element = mElementProperty.getElement();
		element.remove(this);
		if(mElementProperty.getName().length() == 0)
		{
			element.removeProperty(mElementProperty);
		}
		element.repaint();
	}

	public void keyTyped(KeyEvent e)
	{
	}
	
	public void keyPressed(KeyEvent e)
	{
	}
	
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			Element element = mElementProperty.getElement();
			element.getStructurePanel().removeElementPropertyEditor();
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if(source == this)
		{
			if(getText().length() == 0)
			{
				JDialogError dialog = new JDialogError(Rife.getMainFrame(), "rife.dialog.emptyelementproperty.title", Localization.getString("rife.dialog.emptyelementproperty.message"));
				dialog.setVisible(true);
			}
			else
			{
				Element element = mElementProperty.getElement();
				if(element.renameProperty(mElementProperty, getText()))
				{
					element.getStructurePanel().removeElementPropertyEditor();
					element.resetPrecalculatedAreas();
				}
				else
				{
					JDialogError dialog = mElementProperty.getUnicityErrorDialog();
					dialog.setVisible(true);
				}
			}
		}
    }
}
