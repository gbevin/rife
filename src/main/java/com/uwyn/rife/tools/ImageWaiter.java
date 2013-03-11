/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import java.awt.*;
import java.awt.image.ImageObserver;

public class ImageWaiter
{
    private static Canvas waitComponent = null;

    public static void wait(Image image)
    {
        if (waitComponent == null)
        {
            waitComponent = new Canvas();
        }
        wait(image, waitComponent);
    }

    public static void wait(Image image, Component component)
    {
        MediaTracker tracker = new MediaTracker(component);
        tracker.addImage(image, 0);
        try
        {
            tracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            // do nothing
        }
    }

    public static void wait(final Image image, final Component component, final ImageObserver imageobserver)
    {
        final MediaTracker tracker = new MediaTracker(component);

        new Thread(new Runnable()
        {
            public void run()
            {
                tracker.addImage(image, 0);
                try
                {
                    tracker.waitForAll();
                }
                catch (InterruptedException e)
                {
                    // do nothing
                }

                imageobserver.imageUpdate(image, 0, 0, 0, image.getWidth(null), image.getHeight(null));
            }
        }).start();
    }

}

