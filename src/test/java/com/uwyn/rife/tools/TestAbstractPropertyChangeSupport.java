/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.tools.exceptions.SourceBeanRequiredException;
import junit.framework.TestCase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestAbstractPropertyChangeSupport extends TestCase
{
    public TestAbstractPropertyChangeSupport(String name)
    {
        super(name);
    }

    public void testNoInitialListeners()
    {
        PropertyChangeTest test = new PropertyChangeTest();

        assertEquals(0, test.getPropertyChangeListeners().length);

        assertEquals(0, test.getPropertyChangeListeners("string").length);
        assertEquals(0, test.getPropertyChangeListeners("int").length);
        assertEquals(0, test.getPropertyChangeListeners("boolean").length);

        assertFalse(test.hasListeners("string"));
        assertFalse(test.hasListeners("int"));
        assertFalse(test.hasListeners("boolean"));
    }

    public void testNoSource()
    {
        PropertyChangeTest test = new PropertyChangeTest(false);
        PropertyChangeHandler handler = new PropertyChangeHandler();
        try
        {
            test.addPropertyChangeListener(handler);
            fail();
        }
        catch (SourceBeanRequiredException e)
        {
            assertSame(e.getBeanClass(), PropertyChangeTest.class);
        }
    }

    public void testListeners()
    {
        PropertyChangeTest test = new PropertyChangeTest();

        PropertyChangeHandler handler1 = new PropertyChangeHandler();
        PropertyChangeHandler handler2 = new PropertyChangeHandler();
        PropertyChangeHandler handler3 = new PropertyChangeHandler();
        PropertyChangeHandler handler4 = new PropertyChangeHandler();
        PropertyChangeHandler handler5 = new PropertyChangeHandler();
        PropertyChangeHandler handler6 = new PropertyChangeHandler();
        PropertyChangeHandler handler7 = new PropertyChangeHandler();

        test.addPropertyChangeListener(handler1);
        test.addPropertyChangeListener("string", handler2);
        test.addPropertyChangeListener("int", handler3);
        test.addPropertyChangeListener("int", handler4);
        test.addPropertyChangeListener("boolean", handler5);
        test.addPropertyChangeListener("boolean", handler6);
        test.addPropertyChangeListener("boolean", handler7);

        assertEquals(7, test.getPropertyChangeListeners().length);
        assertEquals(1, test.getPropertyChangeListeners("string").length);
        assertEquals(2, test.getPropertyChangeListeners("int").length);
        assertEquals(3, test.getPropertyChangeListeners("boolean").length);

        assertTrue(test.hasListeners("string"));
        assertTrue(test.hasListeners("int"));
        assertTrue(test.hasListeners("boolean"));

        test.removePropertyChangeListener(handler1);
        test.removePropertyChangeListener("string", handler2);
        test.removePropertyChangeListener("int", handler3);
        test.removePropertyChangeListener("int", handler4);
        test.removePropertyChangeListener("boolean", handler5);
        test.removePropertyChangeListener("boolean", handler6);
        test.removePropertyChangeListener("boolean", handler7);

        assertEquals(0, test.getPropertyChangeListeners().length);

        assertEquals(0, test.getPropertyChangeListeners("string").length);
        assertEquals(0, test.getPropertyChangeListeners("int").length);
        assertEquals(0, test.getPropertyChangeListeners("boolean").length);

        assertFalse(test.hasListeners("string"));
        assertFalse(test.hasListeners("int"));
        assertFalse(test.hasListeners("boolean"));
    }

    public void testFirePropertyChange()
    {
        PropertyChangeTest test = new PropertyChangeTest();

        PropertyChangeHandler handler1 = new PropertyChangeHandler();
        PropertyChangeHandler handler2 = new PropertyChangeHandler();
        PropertyChangeHandler handler3 = new PropertyChangeHandler();
        PropertyChangeHandler handler4 = new PropertyChangeHandler();

        test.addPropertyChangeListener(handler1);
        test.addPropertyChangeListener("string", handler2);
        test.addPropertyChangeListener("int", handler3);
        test.addPropertyChangeListener("boolean", handler4);

        assertNull(handler1.getEvent());
        assertNull(handler2.getEvent());
        assertNull(handler3.getEvent());
        assertNull(handler4.getEvent());

        PropertyChangeEvent handler1_event;
        PropertyChangeEvent handler2_event;
        PropertyChangeEvent handler3_event;
        PropertyChangeEvent handler4_event;

        test.setString("one");

        assertNotNull(handler1.getEvent());
        handler1_event = handler1.getEvent();
        assertEquals("string", handler1_event.getPropertyName());
        assertNull(handler1_event.getOldValue());
        assertEquals("one", handler1_event.getNewValue());

        assertNotNull(handler2.getEvent());
        handler2_event = handler2.getEvent();
        assertEquals("string", handler2_event.getPropertyName());
        assertNull(handler2_event.getOldValue());
        assertEquals("one", handler2_event.getNewValue());

        test.setInt(2);

        assertNotNull(handler1.getEvent());
        assertNotSame(handler1_event, handler1.getEvent());
        handler1_event = handler1.getEvent();
        assertEquals("int", handler1_event.getPropertyName());
        assertEquals(-1, handler1_event.getOldValue());
        assertEquals(2, handler1_event.getNewValue());

        assertSame(handler2_event, handler2.getEvent());

        assertNotNull(handler3.getEvent());
        handler3_event = handler3.getEvent();
        assertEquals("int", handler3_event.getPropertyName());
        assertEquals(-1, handler3_event.getOldValue());
        assertEquals(2, handler3_event.getNewValue());

        test.setBoolean(true);

        assertNotNull(handler1.getEvent());
        assertNotSame(handler1_event, handler1.getEvent());
        handler1_event = handler1.getEvent();
        assertEquals("boolean", handler1_event.getPropertyName());
        assertEquals(false, handler1_event.getOldValue());
        assertEquals(true, handler1_event.getNewValue());

        assertSame(handler2_event, handler2.getEvent());
        assertSame(handler3_event, handler3.getEvent());

        assertNotNull(handler4.getEvent());
        handler4_event = handler4.getEvent();
        assertEquals("boolean", handler4_event.getPropertyName());
        assertEquals(false, handler4_event.getOldValue());
        assertEquals(true, handler4_event.getNewValue());

        test.customEvent();

        assertNotNull(handler1.getEvent());
        assertNotSame(handler1_event, handler1.getEvent());
        handler1_event = handler1.getEvent();
        assertEquals("blah", handler1_event.getPropertyName());
        assertEquals("first", handler1_event.getOldValue());
        assertEquals("second", handler1_event.getNewValue());

        assertSame(handler2_event, handler2.getEvent());
        assertSame(handler3_event, handler3.getEvent());
        assertSame(handler4_event, handler4.getEvent());
    }

    public void testClone()
    {
        PropertyChangeTest test1 = new PropertyChangeTest();
        test1.setInt(1);

        PropertyChangeHandler handler = new PropertyChangeHandler();
        test1.addPropertyChangeListener(handler);
        assertEquals(1, test1.getPropertyChangeListeners().length);

        PropertyChangeTest test2 = (PropertyChangeTest)test1.clone();
        assertEquals(0, test2.getPropertyChangeListeners().length);
    }
}

class PropertyChangeTest extends AbstractPropertyChangeSupport
{
    private String mString = null;
    private int mInt = -1;
    private boolean mBoolean = false;

    public PropertyChangeTest()
    {
        this(true);
    }

    public PropertyChangeTest(boolean registerSource)
    {
        if (registerSource)
        {
            setSource(this);
        }
    }

    public void customEvent()
    {
        firePropertyChange(new PropertyChangeEvent(this, "blah", "first", "second"));
    }

    public String getString()
    {
        return mString;
    }

    public void setString(String value)
    {
        String old = mString;

        mString = value;

        firePropertyChange("string", old, value);
    }

    public int getInt()
    {
        return mInt;
    }

    public void setInt(int value)
    {
        int old = mInt;

        mInt = value;

        firePropertyChange("int", old, value);
    }

    public boolean isBoolean()
    {
        return mBoolean;
    }

    public void setBoolean(boolean value)
    {
        boolean old = mBoolean;

        mBoolean = value;

        firePropertyChange("boolean", old, value);
    }
}

class PropertyChangeHandler implements PropertyChangeListener
{
    private PropertyChangeEvent mEvent = null;

    public void propertyChange(PropertyChangeEvent event)
    {
        mEvent = event;
    }

    public PropertyChangeEvent getEvent()
    {
        return mEvent;
    }
}
