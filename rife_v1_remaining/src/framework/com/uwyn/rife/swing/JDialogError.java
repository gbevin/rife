/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JDialogError.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import javax.swing.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.tools.Localization;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JDialogError extends JDialog implements ActionListener, DefaultFocused
{
	private static final long serialVersionUID = -8662064050538600090L;
	
	private GridBagConstraints	mConstraints = null;
	
	private JPanel		mContentPane = null;
	private JLabel		mErrorIcon = null;
	private JPanel		mErrorIconPanel = null;
	private JComponent	mErrorMessage = null;
	private JButton		mOkButton = null;
	private JPanel		mButtonsPanel = null;
	
	public JDialogError(JFrame frame, String text)
	{
		this(frame, Localization.getString("rife.dialog.error.title"), text);
	}

	public JDialogError(JFrame frame, String title, String text)
	{
		this(frame, title, new JLabel(text));
	}

	public JDialogError(JFrame frame, String title, JComponent messageComponent)
	{
		super(frame, title, true);
		ImageIcon icon = null;
		if (Images.hasRepInstance())
		{
			icon = Images.getRepInstance().getImageIcon(RifeConfig.Swing.getIconErrorPath());
		}
		if (null == icon)
		{
			icon = new ImageIcon(JDialogError.class.getClassLoader().getResource(RifeConfig.Swing.getIconErrorPath()));
		}
		mErrorIcon = new JLabel(icon);
        mErrorIcon.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, (Color)null));
        mErrorIcon.setBackground(Colors.ERROR_BACKGROUND);
        mErrorIcon.setOpaque(true);
		mErrorIconPanel = new JPanel();
        mErrorIconPanel.setBackground(Colors.ERROR_BACKGROUND);
        mErrorIconPanel.add(mErrorIcon);
		mErrorMessage = messageComponent;
        mErrorMessage.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, (Color)null));
		mOkButton = new JButton(Localization.getString("rife.dialog.error.okbutton"));
		mOkButton.setMnemonic(Localization.getChar("rife.dialog.error.okbutton.mnemonic"));
        mOkButton.addActionListener(this);
		mButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mButtonsPanel.add(mOkButton);

		mContentPane = new JPanel(new GridBagLayout());
        mContentPane.setBackground(Color.white);
		setContentPane(mContentPane);
        mConstraints = new GridBagConstraints();
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;
        mConstraints.gridwidth = 1;
        mConstraints.gridheight = 1;
        mConstraints.weightx = 0;
        mConstraints.weighty = 1;
        mConstraints.anchor = GridBagConstraints.CENTER;
        mConstraints.fill = GridBagConstraints.BOTH;
        mContentPane.add(mErrorIconPanel, mConstraints);
        mConstraints.gridx = 1;
        mConstraints.weightx = 1;
        mConstraints.anchor = GridBagConstraints.NORTHWEST;
        mConstraints.fill = GridBagConstraints.HORIZONTAL;
        mContentPane.add(mErrorMessage, mConstraints);

		mConstraints.gridx = 0;
		mConstraints.gridy = 1;
        mConstraints.weightx = 1;
        mConstraints.weighty = 0;
        mConstraints.gridwidth = 2;
		mContentPane.add(new JSeparator(JSeparator.HORIZONTAL), mConstraints);

        mConstraints.gridy = 2;
        mContentPane.add(mButtonsPanel, mConstraints);
		
        mContentPane.setVisible(true);
        pack();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (null != frame)
		{
	        setLocationRelativeTo(frame);
		}

		new DefaultFocusSetter(this);
	}

	public JComponent getDefaultFocus()
	{
		return mOkButton;
	}

	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();

		if (mOkButton == source)
		{
			dispose();
		}
	}
}

