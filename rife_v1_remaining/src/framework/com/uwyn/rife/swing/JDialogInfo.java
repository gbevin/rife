/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JDialogInfo.java 3918 2008-04-14 17:35:35Z gbevin $
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

public class JDialogInfo extends JDialog implements ActionListener, DefaultFocused
{
	private static final long serialVersionUID = 1114295526679645482L;
	
	protected GridBagConstraints	mConstraints = null;
	
	protected JPanel		mContentPane = null;
	protected JLabel		mInfoIcon = null;
	protected JPanel		mInfoIconPanel = null;
	protected JComponent	mInfoMessage = null;
	protected JButton		mOkButton = null;
	protected JPanel		mButtonsPanel = null;

	public JDialogInfo(JFrame frame, String text)
	{
		this(frame, Localization.getString("rife.dialog.info.title"), text);
	}

	public JDialogInfo(JFrame frame, String title, String text)
	{
		this(frame, title, new JLabel(text));
	}

	public JDialogInfo(JFrame frame, String title, JComponent messageComponent)
	{
		super(frame, title, true);
		
		ImageIcon icon = null;
		if (Images.hasRepInstance())
		{
			icon = Images.getRepInstance().getImageIcon(RifeConfig.Swing.getIconInfoPath());
		}
		if (null == icon)
		{
			icon = new ImageIcon(JDialogError.class.getClassLoader().getResource(RifeConfig.Swing.getIconInfoPath()));
		}
		mInfoIcon = new JLabel(icon);
        mInfoIcon.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, (Color)null));
        mInfoIcon.setBackground(Colors.INFO_BACKGROUND);
        mInfoIcon.setOpaque(true);
		mInfoIconPanel = new JPanel();
        mInfoIconPanel.setBackground(Colors.INFO_BACKGROUND);
        mInfoIconPanel.add(mInfoIcon);
		mInfoMessage = messageComponent;
        mInfoMessage.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, (Color)null));
		
		mOkButton = new JButton(Localization.getString("rife.dialog.info.okbutton"));
		mOkButton.setMnemonic(Localization.getChar("rife.dialog.info.okbutton.mnemonic"));
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
        mContentPane.add(mInfoIconPanel, mConstraints);
        mConstraints.gridx = 1;
        mConstraints.weightx = 1;
        mConstraints.anchor = GridBagConstraints.NORTHWEST;
        mConstraints.fill = GridBagConstraints.HORIZONTAL;
        mContentPane.add(mInfoMessage, mConstraints);
		
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

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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

	public void safeShow()
	{
		SwingUtilities.invokeLater(new DialogShowThread(this));
	}

	public class DialogShowThread extends Thread
	{
		JDialog mDialog = null;

		public DialogShowThread(JDialog dialog)
		{
			mDialog = dialog;
		}

		public void run()
		{
			mDialog.setVisible(true);
		}
	}
}

