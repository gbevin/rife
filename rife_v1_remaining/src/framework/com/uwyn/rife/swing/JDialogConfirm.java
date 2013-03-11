/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JDialogConfirm.java 3918 2008-04-14 17:35:35Z gbevin $
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

public class JDialogConfirm extends JDialog implements ActionListener, DefaultFocused
{
	private static final long serialVersionUID = -947283805874742381L;
	
	public static final int	OK = 0;
	public static final int	CANCEL = 1;
	
	protected GridBagConstraints	mConstraints = null;
	
	protected JFrame		mParentFrame = null;
	protected JPanel		mContentPane = null;
	protected JLabel		mConfirmationIcon = null;
	protected JPanel		mConfirmationIconPanel = null;
	protected JComponent	mConfirmationMessage = null;
	protected JButton		mOkButton = null;
	protected JButton		mCancelButton = null;
	protected JPanel		mButtonsPanel = null;
	protected int			mPerformedAction = CANCEL;

	public JDialogConfirm(JFrame frame, String text)
	{
		this(frame, Localization.getString("rife.dialog.confirm.title"), text);
	}

	public JDialogConfirm(JFrame frame, String title, String text)
	{
		this(frame, title, new JLabel(text));
	}

	public JDialogConfirm(JFrame frame, String title, JComponent messageComponent)
	{
		super(frame, title, true);

        mParentFrame = frame;

		ImageIcon icon = null;
		if (Images.hasRepInstance())
		{
			icon = Images.getRepInstance().getImageIcon(RifeConfig.Swing.getIconConfirmPath());
		}
		if (null == icon)
		{
			icon = new ImageIcon(JDialogError.class.getClassLoader().getResource(RifeConfig.Swing.getIconConfirmPath()));
		}
		mConfirmationIcon = new JLabel(icon);
        mConfirmationIcon.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, (Color)null));
        mConfirmationIcon.setBackground(Colors.CONFIRM_BACKGROUND);
        mConfirmationIcon.setOpaque(true);
		mConfirmationIconPanel = new JPanel();
        mConfirmationIconPanel.setBackground(Colors.CONFIRM_BACKGROUND);
        mConfirmationIconPanel.add(mConfirmationIcon);
        mConfirmationMessage = messageComponent;
        mConfirmationMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mOkButton = new JButton();
        mOkButton.addActionListener(this);
		mCancelButton = new JButton();
        mCancelButton.addActionListener(this);
		mButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		setButtonLabels();
        mButtonsPanel.add(mOkButton);
        mButtonsPanel.add(mCancelButton);

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
        mContentPane.add(mConfirmationIconPanel, mConstraints);
        mConstraints.gridx = 1;
        mConstraints.weightx = 1;
        mConstraints.anchor = GridBagConstraints.NORTHWEST;
        mConstraints.fill = GridBagConstraints.HORIZONTAL;
        mContentPane.add(mConfirmationMessage, mConstraints);
		
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

	protected void setButtonLabels()
	{
		mOkButton.setText(Localization.getString("rife.dialog.confirm.okbutton"));
		mOkButton.setMnemonic(Localization.getChar("rife.dialog.confirm.okbutton.mnemonic"));
		mCancelButton.setText(Localization.getString("rife.dialog.confirm.cancelbutton"));
		mCancelButton.setMnemonic(Localization.getChar("rife.dialog.confirm.cancelbutton.mnemonic"));
	}

	public int getPerformedAction()
	{
		return mPerformedAction;
	}

	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();

		if (mOkButton == source)
		{
            mPerformedAction = OK;
			dispose();
		}
		else if (mCancelButton == source)
		{
            mPerformedAction = CANCEL;
			dispose();
		}
	}
}

