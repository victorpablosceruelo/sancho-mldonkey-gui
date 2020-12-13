/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.utility;

import org.eclipse.swt.SWT;

import sancho.view.preferences.PreferenceLoader;

public class VersionInfo {
  
  private static final String VERSION = "0.9.4-23";

  public static String getBugPage() {
    return "http://sourceforge.net/tracker/?group_id=98050&atid=619889";
  }

  public static String getBruceHomePage() {
    return "http://www.fliptopbox.com/";
  }

  public static String getDownloadLogFile() {
    return getHomeDirectory() + getName() + ".dls";
  }

  public static String getFAQPage() {
    return getHomePage() + "/faq.phtml";
  }

  public static String getFeaturePage() {
    return "http://sourceforge.net/tracker/?group_id=98050&atid=619892";
  }

  public static String getUserHomeDirectory() {
    String homeDirectory;
    if ((homeDirectory = PreferenceLoader.getHomeDirectory()) != null)
      return homeDirectory;

    return System.getProperty("user.home");

  }

  public static String getHomeDirectory() {
    String homeDirectory;
    if ((homeDirectory = PreferenceLoader.getHomeDirectory()) != null)
      return homeDirectory;

    return System.getProperty("user.home") + System.getProperty("file.separator") + "." + getName()
        + System.getProperty("file.separator");
  }

  public static String getHomePage() {
    return "http://sancho-gui.sourceforge.net";
  }

  public static String getName() {
    return "sancho";
  }

  public static String getOSPlatform() {
    if (System.getProperty("os.name").startsWith("Windows"))
      return "Windows";
    return System.getProperty("os.name");
  }

  public static String getShortHomePage() {
    return "http://sancho-gui.sf.net";
  }

  public static boolean isGNU() {
    return System.getProperty("java.vm.name").startsWith("GNU");
  }

  public static String getSWTPlatform() {
    String platform;

    if ((platform = SWT.getPlatform()).equals("fox")) {
      if ((System.getProperty("os.name").length() > 7) && System.getProperty("os.name").startsWith("Windows"))
        return "win32-fox";
      else
        return "fox";
    } else
      return platform;
  }

  public static String getVersion() {
    return VERSION;
  }

}