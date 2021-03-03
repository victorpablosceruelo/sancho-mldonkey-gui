/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import sancho.utility.VersionInfo;
import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;

public class SResources {
  private static ImageRegistry imageRegistry;
  private static Hashtable stringRegistry;
  private static final String epDirectory = "e/";
  private static final String miscDirectory = "m/";
  private static final String imagesDirectory = "img/";
  private static final String networksDirectory = "n/";

  public static final String S_ES = "";
  public static final String S_PERCENT = "%";
  public static final String S_SPACE = " ";
  public static final String S_SLASH2 = " / ";
  public static final String S_SLASH = "/";
  public static final String S_COLON = ": ";
  public static final String S_COMMA = ", ";
  public static final String S_OBS = " (";
  public static final String S_OB = "(";
  public static final String S_CB = ")";
  public static final String S_DASH = "-";
  public static final String S_NL = "\n";
  public static final String S_DOT = ".";
  public static final String[] SA_Z = {"z"};
  public static final String S_0 = "0";
  public static final String S_00 = "00";

  public static final String S_GIF = ".gif";

  static {

    String localeString = PreferenceLoader.loadString("locale");

    if (PreferenceLoader.getLocaleString() != null)
      localeString = PreferenceLoader.getLocaleString();

    String userDIR = VersionInfo.getHomeDirectory();

    File file = new File(userDIR + VersionInfo.getName() + "_" + localeString + ".properties");

    ResourceBundle bundle;

    if (!file.exists()) {
    	String baseName = VersionInfo.getName();
    	bundle = ResourceBundle.getBundle(baseName);
    } else {
      try {
        String language = "";
        String country = "";
        String variant = "";
        StringTokenizer st = new StringTokenizer(localeString, "_");

        if (st.countTokens() > 0)
          language = st.nextToken();
        if (st.countTokens() > 0)
          country = st.nextToken();
        if (st.countTokens() > 0)
          variant = st.nextToken();

        Locale l = new Locale(language, country, variant);

        URL[] urlArray = new URL[]{new URL("file:///" + userDIR)};
        URLClassLoader urlClassLoader = new URLClassLoader(urlArray);
        bundle = ResourceBundle.getBundle(VersionInfo.getName(), l, urlClassLoader);
      } catch (Exception e) {
        bundle = ResourceBundle.getBundle(VersionInfo.getName());
      }

    }

    stringRegistry = new Hashtable();

    String key;
    String value = null;
    for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
      key = (String) e.nextElement();
      value = bundle.getString(key);
      stringRegistry.put(key.intern(), value.intern());
    }

  }

  private SResources() {
  }

  public static void initialize() {
    createImageRegistry();
  }

  /**
   * Get resource image
   * 
   * @param key
   * @return Image
   */
  public static synchronized Image getImage(String key) {
    return getImageRegistry().get(key);
  }

  /**
   * Get resource imageDescriptor
   * 
   * @param key
   * @return
   */
  public static synchronized ImageDescriptor getImageDescriptor(String key) {
    return getImageRegistry().getDescriptor(key);
  }

  /**
   * Get ImageRegistry
   * 
   * @return ImageRegistry
   */
  private static ImageRegistry getImageRegistry() {
    if (imageRegistry == null)
      imageRegistry = new ImageRegistry();

    return imageRegistry;
  }

  public static synchronized void putImage(String key, Image image) {
    try {
      getImageRegistry().put(key, image);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  public static synchronized void putImage(String key, ImageDescriptor imageDescriptor) {
    try {
      getImageRegistry().put(key, imageDescriptor);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get resource string
   * 
   * @param key
   * @return String
   */
  public static String getString(String key) {
    String result = (String) stringRegistry.get(key);
    if (result == null)
      return key;

    return result.intern();
  }

  /**
   * Create the image registry
   */
  private static void createImageRegistry() {
    ImageRegistry reg = SResources.getImageRegistry();

    reg.put("splashScreen", createRawImage("splash.png"));
    reg.put("splashHighlight", createRawImage("splash-hl.png"));
    reg.put("ProgramIcon", createRawImage("icon.gif"));
    reg.put("ProgramIcon-12", createRawImage("icon-12.gif"));
    reg.put("tray-16", createRawImage("tray-16.gif"));
    reg.put("tray-22", createRawImage("tray-22.gif"));
    reg.put("about", createRawImage("about.png"));
    reg.put("welcome", createRawImage("welcome.png"));

    String[] buttonFiles = {"statistics", "console", "transfers", "search", "servers", "friends", "shares",
        "rooms", "webbrowser"};

    for (int i = 0; i < buttonFiles.length; i++) {
      String buttonString = "tab." + buttonFiles[i];

      reg.put(buttonString + ".button", createRawImage(buttonFiles[i] + ".gif"));
      ImageDescriptor imgDescriptor = reg.getDescriptor(buttonString + ".button");
      reg.put(buttonString + ".buttonActive", createActiveImage(imgDescriptor));
      reg.put(buttonString + ".buttonSmall", createRawImage(buttonFiles[i] + "-16.gif"));
      reg.put(buttonString + ".buttonSmallActive", createActiveImage(reg.getDescriptor("tab."
          + buttonFiles[i] + ".buttonSmall")));
    }

    reg.put("FriendsButtonSmallBW", createRawImage("friends-16-bw.gif"));
    reg.put("FriendsButtonSmallBWPlus", createRawImage("friends-16-bw-plus.gif"));
    reg.put("FriendsButtonSmallPlus", createRawImage("friends-16-plus.gif"));

    reg.put("rateDownArrow", createRawImage("down.gif"));
    reg.put("rateUpArrow", createRawImage("up.gif"));
    reg.put("RedCrossSmall", createRawImage("red_cross-12.gif"));

    createNetworksIcons(reg);
    createMiscIcons(reg);
    createEPIcons(reg);
  }

  public static void createMiscIcons(ImageRegistry reg) {
    String[] mIcons = {"ep_unknown", "ep_transferring", "ep_noneeded", "ep_connecting", "ep_asking",
        "search_small", "search_complete", "up_arrow_blue", "up_arrow_green", "down_arrow_green",
        "down_arrow_yellow", "x", "x-light", "toggle", "heart", "irc", "page-forward", "page-back",
        "page-refresh", "page-stop", "jigle", "bitzi", "sharereactor", "info", "cancel", "resume", "pause",
        "preview", "verify", "commit", "commit_question", "edonkey", "globe", "rotate", "collapseall",
        "expandall", "new-message", "plus", "forward", "back", "plus-globe", "minus", "maximize", "restore",
        "table", "split-table", "copy", "home", "clear", "clear-12", "graph", "dropdown", "menu-disconnect",
        "menu-connect", "nuke", "cabinet", "preferences", "web-link", "web-link-12", "gun", "refine",
        "defprog", "http-add", "folder-12", "file-explorer", "brothers", "azureus", "abc", "bittornado",
        "g3", "torrentstorm", "bitcomet"};

    for (int i = 0; i < mIcons.length; i++) 
      reg.put(mIcons[i], createID_M(mIcons[i]));
    
    for (int i = 1; i < 10; i++)
      reg.put(String.valueOf(i), createID_M(String.valueOf(i)));
  }

  /**
   * @param reg
   */
  public static void createEPIcons(ImageRegistry reg) {
    reg.put("epRatingPoor", createID_E("ep_rating_poor"));
    reg.put("epRatingFair", createID_E("ep_rating_fair"));
    reg.put("epRatingGood", createID_E("ep_rating_good"));
    reg.put("epRatingExcellent", createID_E("ep_rating_excellent"));
    reg.put("epRatingFake", createID_E("ep_rating_fake"));

    for (int i = 0; i < 9; i++)
      reg.put("epClientType" + i, createID_E("client_type_" + i));
  }

  /**
   * @param reg
   */
  public static void createNetworksIcons(ImageRegistry reg) {
    String[] fileNames = {"directconnect", "donkey", "gnutella", "gnutella2", "fasttrack", "soulseek",
        "opennap", "unknown"};
    String resName;

    for (int i = 0; i < fileNames.length; i++) {
      resName = "e.network." + fileNames[i];
      reg.put(resName + ".connected", createID_N(fileNames[i] + "_connected"));
      reg.put(resName + ".disconnected", createID_N(fileNames[i] + "_disconnected"));
      reg.put(resName + ".disabled", createID_N(fileNames[i] + "_disabled"));
      reg.put(resName + ".badconnect", createID_N(fileNames[i] + "_badconnect"));
    }

    fileNames = new String[]{"bittorrent", "multinet", "filetp"};
    for (int i = 0; i < fileNames.length; i++) {
      resName = "e.network." + fileNames[i];
      reg.put(resName + ".connected", createID_N(fileNames[i] + "_connected"));
      reg.put(resName + ".disabled", createID_N(fileNames[i] + "_disabled"));
    }
  }

  /**
   * @param imageDescriptor
   * @return Image
   */
  private static Image createActiveImage(ImageDescriptor imageDescriptor) {
    ImageData imageData = imageDescriptor.getImageData();
    Image result = new Image(null, imageData);

    GC gc = new GC(result);
    for (int w = 0; w < imageData.width; w++) {
      for (int h = 0; h < imageData.height; h++) {
        int pixel = imageData.getPixel(w, h);
        PaletteData paletteData = imageData.palette;
        RGB oldRGB = paletteData.getRGB(pixel);

        if (pixel != imageData.transparentPixel) {
          Color foregroundColor = WidgetFactory.changeColor(oldRGB, 20, 255);
          gc.setForeground(foregroundColor);
          gc.drawPoint(w, h);
          foregroundColor.dispose();
        }
      }
    }
    gc.dispose();
    return result;
  }

  private static ImageDescriptor createRawImage(String filename) {
    return ImageDescriptor.createFromFile(MainWindow.class, imagesDirectory + filename);
  }

  private static ImageDescriptor createID_M(String filename) {
    return createRawImage(miscDirectory + filename + S_GIF);
  }

  private static ImageDescriptor createID_N(String filename) {
    return createRawImage(networksDirectory + filename + S_GIF);
  }

  private static ImageDescriptor createID_E(String filename) {
    return createRawImage(epDirectory + filename + S_GIF);
  }
}
