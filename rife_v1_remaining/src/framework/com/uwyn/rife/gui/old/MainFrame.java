/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MainFrame.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import bsh.EvalError;
import bsh.Interpreter;
import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.swing.JDialogSystemError;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.Localization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class MainFrame extends JFrame implements WindowListener, ActionListener
{
	private StructureEditor		mStructureEditor = null;

	public MainFrame()
	{
		super(Localization.getString("rife.application.title"));
		addWindowListener(this);

		createMenuBar();
		mStructureEditor = new StructureEditor();
		addKeyListener(mStructureEditor.getStructurePanel());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mStructureEditor, BorderLayout.CENTER);
		pack();
		setSize(800, 600);
	}

	public StructureEditor getStructureEditor()
	{
		return mStructureEditor;
	}

	public void updateMenuBar()
	{
		JMenuBar menu_bar = getJMenuBar();
		menu_bar.removeAll();
		populateMenuBar(menu_bar);
		menu_bar.revalidate();
		menu_bar.repaint();
	}

	private void createMenuBar()
	{
		JMenuBar menu_bar = new JMenuBar();
		populateMenuBar(menu_bar);
		setJMenuBar(menu_bar);
	}

	private void populateMenuBar(JMenuBar menuBar)
	{
		DynamicMenuBuilder menu_builder = new DynamicMenuBuilder();
		
		JMenu menu_file = menu_builder.addMenu(menuBar, Localization.getString("rife.menu.file"), Localization.getChar("rife.menu.file.mnemonic"));
		menu_builder.addMenuItem(menu_file, Localization.getString("rife.menu.file.exit"), new Exit(), Localization.getChar("rife.menu.file.exit.mnemonic"), KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK, false));
		
		JMenu menu_view = menu_builder.addMenu(menuBar, Localization.getString("rife.menu.view"), Localization.getChar("rife.menu.view.mnemonic"));
		JMenu menu_lookandfeel = menu_builder.addMenu(menu_view, Localization.getString("rife.menu.view.lookandfeel"), Localization.getChar("rife.menu.view.lookandfeel.mnemonic"));
		DynamicMenuAction lookandfeel_action = new ChangeLookAndFeel();
		ButtonGroup lookandfeel_group = new ButtonGroup();
		LookAndFeel current_lookandfeel = UIManager.getLookAndFeel();
		UIManager.LookAndFeelInfo[] look_and_feels = UIManager.getInstalledLookAndFeels();
		JRadioButtonMenuItem menu_item = null;
		String name = null;
        String classname = null;
        Class<LookAndFeel> testclass = null;
        LookAndFeel lookandfeel = null;
		for(int counter = 0; counter < look_and_feels.length; counter++)
		{
			name = look_and_feels[counter].getName();
			classname = look_and_feels[counter].getClassName();
			try
			{
				testclass = (Class<LookAndFeel>)Class.forName(classname);
				lookandfeel = testclass.newInstance();
				if(lookandfeel.isSupportedLookAndFeel())
				{
					menu_item = menu_builder.addRadioButtonMenuItem(menu_lookandfeel, name, lookandfeel_action);
					menu_item.putClientProperty("LOOKANDFEEL_CLASSNAME", classname);
					if(current_lookandfeel.getName().equals(name))
					{
						menu_item.setSelected(true);
					}
					lookandfeel_group.add(menu_item);
				}
			}
			catch (Throwable e)
			{
				(new JDialogSystemError(MainFrame.this, "MainFrame.populateMenuBar() : Error while determining the look & feel to '"+classname+"' : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
			}
		}
		
		JMenu menu_tools = menu_builder.addMenu(menuBar, Localization.getString("rife.menu.tools"), Localization.getChar("rife.menu.tools.mnemonic"));
		JMenu menu_beanshell = menu_builder.addMenu(menu_tools, Localization.getString("rife.menu.tools.beanshell"), Localization.getChar("rife.menu.tools.beanshell.mnemonic"));
		menu_builder.addMenuItem(menu_beanshell, Localization.getString("rife.menu.tools.beanshell.opendesktop"), new OpenBeanshellDesktop(), Localization.getChar("rife.menu.tools.beanshell.opendesktop.mnemonic"), KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.SHIFT_MASK|InputEvent.CTRL_MASK, false));
		
		menuBar.add(Box.createHorizontalGlue());

		JMenu menu_help = menu_builder.addMenu(menuBar, Localization.getString("rife.menu.help"), Localization.getChar("rife.menu.help.mnemonic"));
		menu_builder.addMenuItem(menu_help, Localization.getString("rife.menu.help.context"), null, Localization.getChar("rife.menu.help.context.mnemonic"), KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false));
		menu_builder.addMenuItem(menu_help, Localization.getString("rife.menu.help.about"), null, Localization.getChar("rife.menu.help.about.mnemonic"));
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		e.getWindow().dispose();
	}

	public void windowClosed(WindowEvent e)
	{
		Rife.quit();
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void actionPerformed(ActionEvent e)
	{
	}
	
	protected class Exit implements DynamicMenuAction
	{
		public void execute(JMenuItem menuItem)
		{
			Rife.quit();
		}
	}
	
	protected class ChangeLookAndFeel implements DynamicMenuAction
	{
		public void execute(JMenuItem menuItem)
		{
			String classname = (String)menuItem.getClientProperty("LOOKANDFEEL_CLASSNAME");
			Rife.setLookAndFeel(classname);
		}
	}
	
	protected class OpenBeanshellDesktop implements DynamicMenuAction
	{
		public void execute(JMenuItem menuItem)
		{
			(new RealExecute()).start();
		}

		private class RealExecute extends Thread
		{
			public void run()
			{
				String				beanshell_desktop_path = "/scripts/rife_desktop.bsh";
				InputStream			input_stream = null;
				InputStreamReader	input_stream_reader = null;
				BufferedReader		buffered_reader = null;

				input_stream = this.getClass().getResourceAsStream(beanshell_desktop_path);

				if(input_stream != null)
				{
					try
					{
						input_stream_reader = new InputStreamReader(input_stream, "ISO8859_1");

						if(input_stream_reader != null)
						{
							buffered_reader = new BufferedReader(input_stream_reader);

							if(buffered_reader != null)
							{
								try
								{
									new Interpreter().eval(buffered_reader);
									return;
								}
								catch (EvalError e)
								{
									(new JDialogSystemError(MainFrame.this, "MainFrame.OpenBeanshellDesktop.RealExecute.run() : Error while evaluating the beanshell desktop script : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
									return;
								}
							}
						}
						else
						{
							(new JDialogSystemError(MainFrame.this, "MainFrame.OpenBeanshellDesktop.RealExecute.run() : Couldn't create the buffered reader for the beanshell desktop script resource at '"+beanshell_desktop_path+"'.")).setVisible(true);
							return;
						}
					}
					catch (UnsupportedEncodingException e)
					{
						(new JDialogSystemError(MainFrame.this, "MainFrame.OpenBeanshellDesktop.RealExecute.run() : Error while creating the inputstream reader for the beanshell desktop script resource at '"+beanshell_desktop_path+"' : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
						return;
					}
				}
				else
				{
					(new JDialogSystemError(MainFrame.this, "MainFrame.OpenBeanshellDesktop.RealExecute.run() : Couldn't open the beanshell desktop script resource at '"+beanshell_desktop_path+"'.")).setVisible(true);
					return;
				}
				(new JDialogSystemError(MainFrame.this, "MainFrame.OpenBeanshellDesktop.RealExecute.run() : Couldn't open the beanshell desktop.")).setVisible(true);
				return;
			}
		}
	}
}
