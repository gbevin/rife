/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JBorderlessToggleButton.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;
	
/**
 * A toggle button that maintains its borderless look when
 * the look & feel changes.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class JBorderlessToggleButton extends JToggleButton
{
	private static final long serialVersionUID = 6408368895438315474L;

	/**
	 * Creates an initially unselected toggle button
	 * without setting the text or image.
	 * @since 1.0
	 */
    public JBorderlessToggleButton()
	{
		super();
		setStyle();
    }

	/**
	 * Creates an initially unselected toggle button
	 * with the specified image but no text.
	 *
	 * @param icon   the image that the button should display
	 * @since 1.0
	 */
    public JBorderlessToggleButton(Icon icon)
	{
		super(icon);
		setStyle();
    }

	/**
	 * Creates a toggle button with the specified image
	 * and selection state, but no text.
	 *
	 * @param icon     the image that the button should display
	 * @param selected if true, the button is initially selected;
	 *                 otherwise, the button is initially unselected
	 * @since 1.0
	 */
    public JBorderlessToggleButton(Icon icon, boolean selected)
	{
		super(icon, selected);
		setStyle();
    }

	/**
	 * Creates an unselected toggle button with the specified text.
	 *
	 * @param text   the string displayed on the toggle button
	 * @since 1.0
	 */
    public JBorderlessToggleButton(String text)
	{
		super(text);
		setStyle();
    }

	/**
	 * Creates a toggle button with the specified text
	 * and selection state.
	 *
	 * @param text     the string displayed on the toggle button
	 * @param selected if true, the button is initially selected;
	 *                 otherwise, the button is initially unselected
	 * @since 1.0
	 */
    public JBorderlessToggleButton(String text, boolean selected)
	{
		super(text, selected);
		setStyle();
    }

	/**
	 * Creates a toggle button where properties are taken from the
	 * Action supplied.
	 *
	 * @param a
	 * @since 1.0
	 */
    public JBorderlessToggleButton(Action a)
	{
		super(a);
		setStyle();
    }

	/**
	 * Creates a toggle button that has the specified text and image,
	 * and that is initially unselected.
	 *
	 * @param text   the string displayed on the button
	 * @param icon   the image that the button should display
	 * @since 1.0
	 */
    public JBorderlessToggleButton(String text, Icon icon)
	{
		super(text, icon);
		setStyle();
    }

	/**
	 * Creates a toggle button with the specified text, image, and
	 * selection state.
	 *
	 * @param text     the text of the toggle button
	 * @param icon     the image that the button should display
	 * @param selected if true, the button is initially selected;
	 *                 otherwise, the button is initially unselected
	 * @since 1.0
	 */
    public JBorderlessToggleButton(String text, Icon icon, boolean selected)
	{
		super(text, icon, selected);
		setStyle();
    }

	/**
	 * Notification from the UIFactory that the L&F
	 * has changed. Maintains the borderless look.
	 *
	 * @see JToggleButton#updateUI()
	 * @since 1.0
	 */
    public void updateUI()
	{
		super.updateUI();
		setStyle();
    }

	/**
	 * Makes this toggle button borderless.
	 * @since 1.0
	 */
	private void setStyle()
	{
		setBorder(null);
	}
}
