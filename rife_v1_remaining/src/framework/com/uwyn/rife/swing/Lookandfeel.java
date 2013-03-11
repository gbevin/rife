/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Lookandfeel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import com.uwyn.rife.tools.SortListComparables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

public abstract class Lookandfeel
{
    // possible JDK Look & Feels
    private static final String		LAF_GTK_CLASS					= "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    private static final String 	LAF_MAC_CLASS					= "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    private static final String 	LAF_METAL_CLASS					= "javax.swing.plaf.metal.MetalLookAndFeel";
    private static final String 	LAF_MOTIF_CLASS					= "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private static final String 	LAF_WINDOWS_CLASS				= "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    
	private static final String 	LAF_KUNSTSTOFF_CLASS			= "com.incors.plaf.kunststoff.KunststoffLookAndFeel";
	private static final String 	LAF_ALLOY_CLASS					= "com.incors.plaf.alloy.AlloyLookAndFeel";
	private static final String 	LAF_COMPIERE_CLASS				= "org.compiere.plaf.CompiereLookAndFeel";
	
	private static final String 	LAF_JGOODIES_WINDOWS_CLASS		= "com.jgoodies.plaf.windows.ExtWindowsLookAndFeel";
    private static final String 	LAF_JGOODIES_PLASTIC_CLASS		= "com.jgoodies.plaf.plastic.PlasticLookAndFeel";
    private static final String 	LAF_JGOODIES_PLASTIC3D_CLASS	= "com.jgoodies.plaf.plastic.Plastic3DLookAndFeel";
    private static final String 	LAF_JGOODIES_PLASTICXP_CLASS	= "com.jgoodies.plaf.plastic.PlasticXPLookAndFeel";
	
	private static final String 	LAF_JGOODIESV2_WINDOWS_CLASS	= "com.jgoodies.looks.windows.WindowsLookAndFeel";
    private static final String 	LAF_JGOODIESV2_PLASTIC_CLASS	= "com.jgoodies.looks.plastic.PlasticLookAndFeel";
    private static final String 	LAF_JGOODIESV2_PLASTIC3D_CLASS	= "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";
    private static final String 	LAF_JGOODIESV2_PLASTICXP_CLASS	= "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
    
	private static final String 	LAF_METOUIA_CLASS				= "net.sourceforge.mlf.metouia.MetouiaLookAndFeel";
	private static final String 	LAF_OYOAHA_CLASS				= "com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel";
	private static final String 	LAF_SLAF_CLASS					= "com.memoire.slaf.SlafLookAndFeel";
	
	private static final String[]	JDK_LAFS = new String[] {
		LAF_GTK_CLASS, LAF_MAC_CLASS, LAF_METAL_CLASS, LAF_MOTIF_CLASS, LAF_WINDOWS_CLASS,
		LAF_KUNSTSTOFF_CLASS, LAF_ALLOY_CLASS, LAF_COMPIERE_CLASS, LAF_JGOODIES_WINDOWS_CLASS,
		LAF_JGOODIES_PLASTIC_CLASS, LAF_JGOODIES_PLASTIC3D_CLASS, LAF_JGOODIES_PLASTICXP_CLASS,
		LAF_JGOODIESV2_WINDOWS_CLASS, LAF_JGOODIESV2_PLASTIC_CLASS, LAF_JGOODIESV2_PLASTIC3D_CLASS,
		LAF_JGOODIESV2_PLASTICXP_CLASS, LAF_METOUIA_CLASS, LAF_OYOAHA_CLASS, LAF_SLAF_CLASS};
		
	public static Map<String, String> getAvailableLookAndFeels()
	{
		// get the possible look & feel classnames
		HashSet<String> classnames = new HashSet<String>();
			
		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		{
			classnames.add(info.getClassName());
		}
		
		for (String classname : JDK_LAFS)
		{
			classnames.add(classname);
		}
		
		// get the list of supported look & feels
		ArrayList<String>		supported_names = new ArrayList<String>();
		HashMap<String, String>	supported_mappings = new HashMap<String, String>();
		Class<LookAndFeel>		laf_class = null;
		String					laf_name = null;
		LookAndFeel				lookandfeel = null;
		for (String classname : classnames)
		{
			try
			{
				laf_class = (Class<LookAndFeel>)Class.forName(classname);
				lookandfeel = laf_class.newInstance();
				laf_name = lookandfeel.getName();
				
				if (lookandfeel.isSupportedLookAndFeel())
				{
					supported_names.add(laf_name);
					supported_mappings.put(laf_name, classname);
				}
			}
			catch (Throwable exception)
			{
				// If anything weird happens, just continue and ignore it
			}
		}
		
		// sort them
		SortListComparables sort = new SortListComparables();
		sort.sort(supported_names);
		
		// put them in a linked hashmap
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (String name : supported_names)
		{
			result.put(name, supported_mappings.get(name));
		}
		
		return result;
	}
}


