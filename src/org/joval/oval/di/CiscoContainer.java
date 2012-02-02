// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.oval.di;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import org.joval.intf.plugin.IPlugin;
import org.joval.os.embedded.system.TechSupport;
import org.joval.plugin.CiscoPlugin;
import org.joval.util.JOVALMsg;
import org.joval.util.JOVALSystem;

/**
 * Jovaldi continer for the CiscoPlugin.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class CiscoContainer implements IPluginContainer {
    private static PropertyResourceBundle resources;
    static {
	try {
	    ClassLoader cl = CiscoContainer.class.getClassLoader();
	    Locale locale = Locale.getDefault();
	    URL url = cl.getResource("plugin.resources_" + locale.toString() + ".properties");
	    if (url == null) {
		url = cl.getResource("plugin.resources_" + locale.getLanguage() + ".properties");
	    }
	    if (url == null) {
		url = cl.getResource("plugin.resources.properties");
	    }
	    resources = new PropertyResourceBundle(url.openStream());
	} catch (IOException e) {
	    JOVALSystem.getLogger().warn(JOVALSystem.getMessage(JOVALMsg.ERROR_EXCEPTION), e);
	}
    }

    private IPlugin plugin;

    public CiscoContainer() {
    }

    // Implement IPluginContainer

    public void setDataDirectory(File dir) {}

    public void configure(Properties props) throws Exception {
	TechSupport tech = null;
	String str = props.getProperty("tech.url");
	Exception ex = null;
	URL url = CiscoPlugin.toURL(props.getProperty("tech.url"));
	plugin = new CiscoPlugin(new TechSupport(url.openStream()));
    }

    public String getProperty(String key) {
	return resources.getString(key);
    }

    public IPlugin getPlugin() {
	return plugin;
    }
}