/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JDateSelector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import java.awt.*;
import javax.swing.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.tools.Localization;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class JDateSelector extends JComponent implements ActionListener, DaySelectionListener
{
	private static final long serialVersionUID = -6302136916436828393L;

	private ArrayList<JDateSelectorListener>	mJDateSelectorListeners = new ArrayList<JDateSelectorListener>();

	private	JPopupMenu 			mPopup = null;
	private Calendar				mCalendar = null;
	private Font					mFont = null;
	private SimpleDateFormat		mSimpleDateFormat = null;
	private JComponent			mParent = null;

	private GridBagConstraints	mConstraints = null;
	private Insets				mInsets = null;

	private JButton				mPreviousYearButton = null;
	private JButton				mPreviousMonthButton = null;
	private JButton				mSelectedDateButton = null;
	private JButton				mNextMonthButton = null;
	private JButton				mNextYearButton = null;
	
	private JPanel				mHeaderPanel = null;

	private JPanel				mDaysPanel = null;
	
	private String[]				mWeekDayNames = null;

	public JDateSelector()
	{
		this(Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale()));
	}
	
	public JDateSelector(Calendar calendar)
	{
		super();
		
		mSimpleDateFormat = new SimpleDateFormat();
		mSimpleDateFormat.applyPattern("dd MMMM yyyy");

		setDate(calendar);
	}
	
	public void setFont(Font font)
	{
		super.setFont(font);
		
		mFont = font;
		
		if (null != mCalendar)
		{
			setDate(mCalendar);
		}
	}
	
	public void setWeekDayNames(String[] weekDayNames)
	{
		mWeekDayNames = weekDayNames;
		DateFormatSymbols symbols = mSimpleDateFormat.getDateFormatSymbols();
		symbols.setShortWeekdays(mWeekDayNames);
		mSimpleDateFormat.setDateFormatSymbols(symbols);
	}
	
	public void setMonthNames(String[] monthNames)
	{
		DateFormatSymbols symbols = mSimpleDateFormat.getDateFormatSymbols();
		symbols.setMonths(monthNames);
		mSimpleDateFormat.setDateFormatSymbols(symbols);
	}
	
	public void setDateFormatPattern(String pattern)
	{
		mSimpleDateFormat.applyPattern(pattern);
		updateLabel();
	}
	
	public void setDate(Calendar calendar)
	{
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		mCalendar = calendar;
		mCalendar.setLenient(true);
		
		mConstraints = new GridBagConstraints();
		mInsets = new Insets(2, 0, 2, 0);
		
		mPreviousYearButton = new JButton("<<");
		if (null != mFont) mPreviousYearButton.setFont(mFont);
		mPreviousYearButton.addActionListener(this);
		mPreviousMonthButton = new JButton("<");
		if (null != mFont) mPreviousMonthButton.setFont(mFont);
		mPreviousMonthButton.addActionListener(this);
		mSelectedDateButton = new JButton("");
		if (null != mFont) mSelectedDateButton.setFont(mFont);
		mSelectedDateButton.addActionListener(this);
		mNextMonthButton = new JButton(">");
		if (null != mFont) mNextMonthButton.setFont(mFont);
		mNextMonthButton.addActionListener(this);
		mNextYearButton = new JButton(">>");
		if (null != mFont) mNextYearButton.setFont(mFont);
		mNextYearButton.addActionListener(this);
		
		if (null != mHeaderPanel)
		{
			remove(mHeaderPanel);
		}
		mHeaderPanel = new JPanel();
		mHeaderPanel.setLayout(new GridBagLayout());
		
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;
        mConstraints.gridwidth = 1;
        mConstraints.gridheight = 1;
        mConstraints.weightx = 0;
        mConstraints.weighty = 0;
        mConstraints.anchor = GridBagConstraints.CENTER;
        mConstraints.fill = GridBagConstraints.BOTH;
        mConstraints.insets = mInsets;
		mHeaderPanel.add(mPreviousYearButton, mConstraints);
        mConstraints.gridx = 1;
		mHeaderPanel.add(mPreviousMonthButton, mConstraints);
        mConstraints.gridx = 2;
        mConstraints.weightx = 1;
		mHeaderPanel.add(mSelectedDateButton, mConstraints);
        mConstraints.gridx = 3;
        mConstraints.weightx = 0;
		mHeaderPanel.add(mNextMonthButton, mConstraints);
        mConstraints.gridx = 4;
		mHeaderPanel.add(mNextYearButton, mConstraints);
		
		setLayout(new BorderLayout());
		add("North", mHeaderPanel);

		createDaysOfTheMonth(mCalendar);
	}
	
	public Calendar getSelectedDate()
	{
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);

		return mCalendar;
	}
	
	public void popup(JComponent parent)
	{
		mPopup = new JPopupMenu();
		mPopup.add(this);
		mParent = parent;
		mPopup.show(mParent, 0, mParent.getHeight());
	}
	
	public JComponent getParentComponent()
	{
		return mParent;
	}
	
	public void popdown()
	{
		if (null != mPopup)
		{
			mPopup.setVisible(false);
		}
	}
	
	public boolean isPoppedUp()
	{
		if (null == mPopup)
		{
			return false;
		}
		else
		{
			return mPopup.isVisible();
		}
	}
	
	private void createDaysOfTheMonth(Calendar calendar)
	{
		if (null != mDaysPanel)
		{
			remove(mDaysPanel);
		}
		
		mDaysPanel = new JPanel(new GridLayout(7, 7));
		
		Calendar temp_calendar = null;
		
		temp_calendar = (Calendar)calendar.clone();
		int last_layout_position = 0;
		if (null == mWeekDayNames)
		{
			mWeekDayNames = mSimpleDateFormat.getDateFormatSymbols().getShortWeekdays();
		}
		for (int i = 1; i < 8; i++)
		{
			JLabel label = new JLabel(mWeekDayNames[i]);
			if (null != mFont) label.setFont(mFont);
			mDaysPanel.add(label);
			
			temp_calendar.roll(Calendar.DAY_OF_WEEK, true);
			last_layout_position++;
		}
		
		temp_calendar = (Calendar)calendar.clone();
		
		temp_calendar.set(Calendar.DAY_OF_MONTH, 1);
		int first_visible_day = temp_calendar.get(Calendar.DAY_OF_WEEK);
		temp_calendar.roll(Calendar.DATE, false);
		int last_day_of_month = temp_calendar.get(Calendar.DAY_OF_MONTH);
		temp_calendar.set(Calendar.DAY_OF_MONTH, 1);
		temp_calendar.roll(Calendar.MONTH, false);
		temp_calendar.roll(Calendar.DATE, false);
		temp_calendar.set(Calendar.DAY_OF_MONTH, temp_calendar.get(Calendar.DAY_OF_MONTH) - (first_visible_day - 2));
		for (int i = 0; i < (first_visible_day - 1); i++)
		{
			DayButton button = new DayButton(temp_calendar.get(Calendar.DAY_OF_MONTH)+i);
			if (null != mFont) button.setFont(mFont);
			button.setEnabled(false);
			mDaysPanel.add(button);
			last_layout_position++;
		}
		
		temp_calendar = (Calendar)calendar.clone();
		temp_calendar.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i < last_day_of_month; i++)
		{
			DayButton button = new DayButton(temp_calendar.get(Calendar.DAY_OF_MONTH));
			if (null != mFont) button.setFont(mFont);
			button.addDaySelectionListener(this);
			mDaysPanel.add(button);
			temp_calendar.roll(Calendar.DAY_OF_MONTH, true);
			
			last_layout_position++;
		}
		
		for (int i = last_layout_position; i < 49; i++)
		{
			DayButton button = new DayButton(temp_calendar.get(Calendar.DAY_OF_MONTH));
			if (null != mFont) button.setFont(mFont);
			button.setEnabled(false);
			mDaysPanel.add(button);
			temp_calendar.roll(Calendar.DAY_OF_MONTH, true);
		}
		
		add("Center", mDaysPanel);
		revalidate();
		
		if (null != mPopup)
		{
			mPopup.pack();
		}
		add("Center", mDaysPanel);
		updateLabel();
	}
	
	private void updateLabel()
	{
		mSelectedDateButton.setText(mSimpleDateFormat.format(mCalendar.getTime()));
	}
	
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		
		if (mPreviousYearButton == source)
		{
			mCalendar.roll(Calendar.YEAR, false);
			createDaysOfTheMonth(mCalendar);
		}
		else if (mPreviousMonthButton == source)
		{
			mCalendar.roll(Calendar.MONTH, false);
			createDaysOfTheMonth(mCalendar);
		}
		else if (mNextMonthButton == source)
		{
			mCalendar.roll(Calendar.MONTH, true);
			createDaysOfTheMonth(mCalendar);
		}
		else if (mNextYearButton == source)
		{
			mCalendar.roll(Calendar.YEAR, true);
			createDaysOfTheMonth(mCalendar);
		}
		else if (mSelectedDateButton == source)
		{
			daySelected(mCalendar.get(Calendar.DAY_OF_MONTH));
		}
	}
	
	public void daySelected(int day)
	{
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		updateLabel();
		if (null != mPopup)
		{
			mPopup.setVisible(false);
		}
		fireDateSelected(mCalendar.getTime());
	}
	
	public void addJDateSelectorListener(JDateSelectorListener jDateSelectorListeners)
	{
		mJDateSelectorListeners.add(jDateSelectorListeners);
	}
	
	public void removeJDateSelectorListener(JDateSelectorListener jDateSelectorListeners)
	{
		mJDateSelectorListeners.remove(jDateSelectorListeners);
	}

	private void fireDateSelected(Date date)
	{
		for (JDateSelectorListener listener : mJDateSelectorListeners)
		{
			listener.dateSelected(date);
		}
	}
}
	
class DayButton extends JButton implements ActionListener
{
	private static final long serialVersionUID = 9212576380626603405L;
	
	private ArrayList<DaySelectionListener>	mDaySelectionListeners = null;
	private int	        					mDay = 0;
	
	public DayButton(int day)
	{
		super((new Integer(day)).toString());
		mDaySelectionListeners = new ArrayList<DaySelectionListener>();
		mDay = day;
		addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if (mDaySelectionListeners.size() > 0)
		{
			for (DaySelectionListener listener : mDaySelectionListeners)
			{
				listener.daySelected(mDay);
			}
		}
	}
	
	public void addDaySelectionListener(DaySelectionListener daySelectionListeners)
	{
		mDaySelectionListeners.add(daySelectionListeners);
	}
	
	public void removeDaySelectionListener(DaySelectionListener daySelectionListeners)
	{
		mDaySelectionListeners.remove(daySelectionListeners);
	}
}

interface DaySelectionListener
{
	public void daySelected(int day);
}
