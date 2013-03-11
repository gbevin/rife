/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MainFrame.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.gui.model.ElementModel;
import com.uwyn.rife.gui.model.SiteModel;
import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import com.uwyn.rife.gui.ui.actions.ExitAction;
import com.uwyn.rife.gui.ui.actions.OpenBeanshellAction;
import com.uwyn.rife.gui.ui.commands.ChangeLookAndFeel;
import com.uwyn.rife.swing.JAction;
import com.uwyn.rife.swing.JDialogSystemError;
import com.uwyn.rife.swing.JMenuBuilder;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.Localization;

public class MainFrame extends JFrame implements WindowListener, ActionListener
{
	private EditorPane		mEditorPane = null;

	public MainFrame()
	{
		super(Localization.getString("rife.application.title"));
		this.addWindowListener(this);

		this.createMenuBar();
		this.getContentPane().setLayout(new BorderLayout());

        SiteModel site = new SiteModel();
		ElementModel element = null;
        for(int j = 0; j < 3; j++)
        {
            try
            {
				element = new ElementModel("element "+j);
				site.addElement(element);
            }
            catch (GuiModelException e)
            {
            }

			int exits = (int)(Math.random()*3);
			int consumeds = (int)(Math.random()*3);
			int useds = (int)(Math.random()*3);
            try
            {
				for(int i = 0; i < exits; i++)
				{
					element.addExit("exit"+i);
				}
				for(int i = 0; i < consumeds; i++)
				{
					element.addInput("input"+i);
				}
				for(int i = 0; i < useds; i++)
				{
					element.addOutput("output"+i);
				}
            }
            catch (GuiModelException e)
            {
            }
//			Color body_color = new Color(155+(int)(Math.random()*100), 155+(int)(Math.random()*100), 155+(int)(Math.random()*100));
//			element.setElementColor(body_color);
//			element.addElementListener(this);
//			mElements.add(element);

//			this.add(element);
//			element.setBounds((int)(Math.random()*800), (int)(Math.random()*600), element.getWidth(), element.getHeight());

//			calculateDimension();
        }

		System.out.println(site.toString());

		//		addKeyListener(mStructureEditor.getStructurePanel());

        this.setEditorPane(new SiteEditorPane(site));
		this.pack();
		this.setSize(800, 600);
	}

	public EditorPane getEditorPane()
	{
		return this.mEditorPane;
	}

	protected void setEditorPane(EditorPane pane)
	{
		this.mEditorPane = pane;
        this.mEditorPane.constructLayout();
		this.getContentPane().add(this.mEditorPane, BorderLayout.CENTER);
	}

	public void updateMenuBar()
	{
		JMenuBar menu_bar = getJMenuBar();
		menu_bar.removeAll();
		this.populateMenuBar(menu_bar);
		menu_bar.revalidate();
		menu_bar.repaint();
	}

	private void createMenuBar()
	{
		JMenuBar menu_bar = new JMenuBar();
		this.populateMenuBar(menu_bar);
		this.setJMenuBar(menu_bar);
	}

	private void populateMenuBar(JMenuBar menuBar)
	{
		// build the file menu
		JMenu menu_file = JMenuBuilder.addMenu(menuBar, Localization.getString("rife.menu.file"),
			Localization.getChar("rife.menu.file.mnemonic"));
		JMenuBuilder.addMenuItem(menu_file, new ExitAction());

		// build the view menu
		JMenu menu_view = JMenuBuilder.addMenu(menuBar, Localization.getString("rife.menu.view"),
			Localization.getChar("rife.menu.view.mnemonic"));
		JMenu menu_lookandfeel = JMenuBuilder.addMenu(menu_view, Localization.getString("rife.menu.view.lookandfeel"),
			Localization.getChar("rife.menu.view.lookandfeel.mnemonic"));

		// build the look&feel menu
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
					menu_item = JMenuBuilder.addRadioButtonMenuItem(menu_lookandfeel, new JAction(new ChangeLookAndFeel(classname), name));
					if(current_lookandfeel.getName().equals(name))
					{
						menu_item.setSelected(true);
					}
					lookandfeel_group.add(menu_item);
				}
			}
			catch (Throwable e)
			{
				(new JDialogSystemError(MainFrame.this,
					"MainFrame.populateMenuBar() : Error while determining the look & feel to '" + classname + "' : " +
					ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
			}
		}

		// build the tools menu
		JMenu menu_tools = JMenuBuilder.addMenu(menuBar, Localization.getString("rife.menu.tools"),
			Localization.getChar("rife.menu.tools.mnemonic"));
		JMenu menu_beanshell = JMenuBuilder.addMenu(menu_tools, Localization.getString("rife.menu.tools.beanshell"),
			Localization.getChar("rife.menu.tools.beanshell.mnemonic"));
		JMenuBuilder.addMenuItem(menu_beanshell, new OpenBeanshellAction());

		// insert a horizontal glue to push the following menu items to the right
		menuBar.add(Box.createHorizontalGlue());

		// build the help menu
		JMenuBuilder.addMenu(menuBar, Localization.getString("rife.menu.help"),
			Localization.getChar("rife.menu.help.mnemonic"));
/*		JMenuBuilder.addMenuItem(menu_help, Localization.getString("rife.menu.help.context"),
			null, Localization.getChar("rife.menu.help.context.mnemonic"),
			KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false));
		JMenuBuilder.addMenuItem(menu_help, Localization.getString("rife.menu.help.about"),
			null, Localization.getChar("rife.menu.help.about.mnemonic"));*/
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
}

