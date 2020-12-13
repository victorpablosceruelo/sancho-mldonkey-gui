/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import sancho.model.mldonkey.enums.EnumHostState;
import sancho.utility.SwissArmy;
import sancho.utility.VersionInfo;
import sancho.view.friends.clientDirectories.ClientDirectoriesTableView;
import sancho.view.server.ServerTableView;
import sancho.view.transfer.downloads.DownloadTableTreeView;
import sancho.view.utility.IDSelector;

public class PreferenceLoader {
  private static List colorArray = new ArrayList();
  private static Map colorMap = new Hashtable();
  private static List fontArray = new ArrayList();
  private static Map fontMap = new Hashtable();
  private static PreferenceStore preferenceStore = null;
  public static boolean customPrefFile; // winreg
  public static boolean customHomeDir; // winreg
  private static String prefFileName;
  private static String localeString;
  private static String homeDirectory;
  private static String ENTRY_SEPARATOR = ";";

  private PreferenceLoader() {
  }

  public static void cleanUp() {
    for (int i = 0; i < fontArray.size(); i++) {
      Font font = (Font) fontArray.get(i);
      if (font != null && !font.isDisposed())
        font.dispose();
    }
    for (int i = 0; i < colorArray.size(); i++) {
      Color color = (Color) colorArray.get(i);
      if (color != null && !color.isDisposed())
        color.dispose();
    }
  }

  public static boolean contains(String preferenceString) {
    return preferenceStore.contains(preferenceString);
  }

  public static PreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public static String getLocaleString() {
    return localeString;
  }

  public static String getPrefFile() {
    return prefFileName;
  }

  public static void initialize() throws IOException {

    if (preferenceStore == null) {
      prefFileName = VersionInfo.getName() + ".pref";

      if (new File(prefFileName).exists())
        preferenceStore = new PreferenceStore(prefFileName);
      else {
        prefFileName = VersionInfo.getHomeDirectory() + prefFileName;

        File prefFile = new File(prefFileName);
        if (!prefFile.exists()) {
          File parentDir = new File(prefFile.getParent());
          parentDir.mkdirs();
        }
        preferenceStore = new PreferenceStore(prefFileName);
      }
    }

    try {
      preferenceStore.load();

    } catch (IOException e) {
      preferenceStore.save();
    }

    // preferenceStore = (PreferenceStore) setDefaults(preferenceStore);
  }

  public static void initialize2() {
    preferenceStore = (PreferenceStore) setDefaults(preferenceStore);
  }

  public static boolean loadBoolean(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return preferenceStore.getBoolean(preferenceString);

    return false;
  }

  public static Color loadColor(String preferenceString) {
    if (preferenceStore.contains(preferenceString)) {
      Color newColor = new Color(null, PreferenceConverter.getColor(preferenceStore, preferenceString));
      if (colorMap.containsKey(preferenceString) && !((Color) colorMap.get(preferenceString)).isDisposed()) {
        if (newColor.getRGB().equals(((Color) colorMap.get(preferenceString)).getRGB()))
          newColor.dispose();
        else {
          colorArray.add(newColor);
          colorMap.put(preferenceString, newColor);
        }
      } else {
        colorArray.add(newColor);
        colorMap.put(preferenceString, newColor);
      }
      return (Color) colorMap.get(preferenceString);
    }
    return null;
  }

  public static Font loadFont(String preferenceString) {
    if (preferenceStore.contains(preferenceString)) {
      Font newFont = new Font(null, PreferenceConverter.getFontDataArray(preferenceStore, preferenceString));
      if (fontMap.containsKey(preferenceString) && !((Font) fontMap.get(preferenceString)).isDisposed()) {
        if (newFont.getFontData()[0].equals(((Font) fontMap.get(preferenceString)).getFontData()[0]))
          newFont.dispose();
        else {
          fontArray.add(newFont);
          fontMap.put(preferenceString, newFont);
        }
      } else {
        fontArray.add(newFont);
        fontMap.put(preferenceString, newFont);
      }
      return (Font) fontMap.get(preferenceString);
    }
    return null;
  }

  public static int loadInt(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return preferenceStore.getInt(preferenceString);
    return 0;
  }

  public static int loadOrientation(String preferenceString) {
    if (preferenceStore.contains(preferenceString)) {
      int orientation = preferenceStore.getInt(preferenceString);
      if ((orientation == SWT.VERTICAL) || (orientation == SWT.HORIZONTAL))
        return orientation;
    }
    return SWT.HORIZONTAL;
  }

  public static Rectangle loadRectangle(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return PreferenceConverter.getRectangle(preferenceStore, preferenceString);
    return null;
  }

  public static String loadString(String preferenceString) {
    if (preferenceStore.contains(preferenceString))
      return preferenceStore.getString(preferenceString).intern();
    return "";
  }

  public static String[] loadStringArray(String preferenceString) {
    StringTokenizer tokenizer = new StringTokenizer(loadString(preferenceString), ENTRY_SEPARATOR);
    int numTokens = tokenizer.countTokens();
    String[] stringArray = new String[numTokens];
    for (int i = 0; i < numTokens; i++) {
      stringArray[i] = tokenizer.nextToken();
    }
    return stringArray;
  }

  public static void setValue(String preferenceString, String[] stringArray) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < stringArray.length; i++) {
      buffer.append(stringArray[i]);
      buffer.append(ENTRY_SEPARATOR);
    }

    preferenceStore.setValue(preferenceString, buffer.toString().intern());
  }

  public static void setValue(String preferenceString, String[] stringArray, int maxLength) {
    if (stringArray.length > maxLength) {
      String[] newArray = new String[maxLength];
      for (int i = 0; i < maxLength; i++)
        newArray[i] = stringArray[i];
      stringArray = newArray;
    }
    setValue(preferenceString, stringArray);
  }

  public static void saveStore() {
    try {
      preferenceStore.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void setDColor(IPreferenceStore pS, Display d, String string, int colorInt) {
    PreferenceConverter.setDefault(pS, string, d.getSystemColor(colorInt).getRGB());
  }

  static IPreferenceStore setDefaults(IPreferenceStore pS) {
    Display d = Display.getDefault();
    FontData[] dFontData = JFaceResources.getDefaultFont().getFontData();

    pS.setDefault("initialized", false);
    pS.setDefault("windowMaximized", false);
    pS.setDefault("coolbarLocked", true);
    pS.setDefault("toolbarSmallButtons", true);
    pS.setDefault("flatInterface", false);
    pS.setDefault("useGraident", true);
    pS.setDefault("splashScreen", true);
    pS.setDefault("killCoreOnExit", false);
    pS.setDefault("killSpawnedCoreOnExit", true);
    pS.setDefault("hostManagerOnStart", false);
    pS.setDefault("downloadCompleteDialog", false);
    pS.setDefault("downloadCompleteLog", true);
    pS.setDefault("explorerExecutable", SWT.getPlatform().equals("win32") ? "explorer" : "");
    pS.setDefault("explorerOpenFolder", "");
    pS.setDefault("linkRipperShowAll", false);

    pS.setDefault("dndBox", false);

    pS.setDefault("ircAutoConnect", false);
    pS.setDefault("ircServer", "irc.freenode.net");
    pS.setDefault("ircNickname", SwissArmy.getRandomString(7));
    pS.setDefault("ircChannel", "#mldonkey");
    pS.setDefault("consoleMaxLines", 300);

    pS.setDefault("mldonkey.InterestedInSources", true);

    pS.setDefault("tableAlternateBGColors", false);
    setDColor(pS, d, "tableAlternateBGColor", SWT.COLOR_INFO_BACKGROUND);
    setDColor(pS, d, "tablesBackgroundColor", SWT.COLOR_LIST_BACKGROUND);

    pS.setDefault("tableHilightSorted", false);
    setDColor(pS, d, "tableSortedColumnBGColor", SWT.COLOR_INFO_BACKGROUND);

    PreferenceConverter.setDefault(pS, "downloadCompleteWindowBounds", new Rectangle(0, 0, 320, 300));
    PreferenceConverter.setDefault(pS, "windowBounds", new Rectangle(40, 40, 580, 420));
    PreferenceConverter.setDefault(pS, "graphHistoryWindowBounds", new Rectangle(40, 40, 580, 420));
    PreferenceConverter.setDefault(pS, "dndBoxWindowBounds", new Rectangle(-1, 0, 0, 0));
    PreferenceConverter.setDefault(pS, "ircWindowBounds", new Rectangle(40, 40, 580, 420));

    setDColor(pS, d, "dndBackgroundColor", SWT.COLOR_WIDGET_BACKGROUND);
    setDColor(pS, d, "dndForegroundColor", SWT.COLOR_BLACK);
    PreferenceConverter.setDefault(pS, "dndFontData", dFontData);
    pS.setDefault("dndWidth", 15);

    setDColor(pS, d, "consoleBackground", SWT.COLOR_BLACK);
    setDColor(pS, d, "consoleForeground", SWT.COLOR_GREEN);
    setDColor(pS, d, "consoleHighlight", SWT.COLOR_WHITE);
    setDColor(pS, d, "consoleInputBackground", SWT.COLOR_BLACK);
    setDColor(pS, d, "consoleInputForeground", SWT.COLOR_GREEN);
    PreferenceConverter.setDefault(pS, "consoleFontData", JFaceResources.getTextFont().getFontData());

    setDColor(pS, d, "ircConsoleBackground", SWT.COLOR_LIST_BACKGROUND);
    setDColor(pS, d, "ircConsoleForeground", SWT.COLOR_LIST_FOREGROUND);
    setDColor(pS, d, "ircConsoleHighlight", SWT.COLOR_LIST_SELECTION);
    setDColor(pS, d, "ircConsoleInputBackground", SWT.COLOR_LIST_BACKGROUND);
    setDColor(pS, d, "ircConsoleInputForeground", SWT.COLOR_LIST_FOREGROUND);
    PreferenceConverter.setDefault(pS, "ircConsoleFontData", JFaceResources.getTextFont().getFontData());

    setDColor(pS, d, "clientsDisconnectedColor", SWT.COLOR_BLACK);
    setDColor(pS, d, "clientsHasFilesColor", SWT.COLOR_DARK_GREEN);
    setDColor(pS, d, "clientsConnectedColor", SWT.COLOR_DARK_YELLOW);
    setDColor(pS, d, "downloadsBackgroundColor", SWT.COLOR_LIST_BACKGROUND);
    setDColor(pS, d, "downloadsAvailableFileColor", SWT.COLOR_BLACK);
    PreferenceConverter.setDefault(pS, "downloadsUnAvailableFileColor", new RGB(128, 128, 128));
    setDColor(pS, d, "downloadsDownloadedFileColor", SWT.COLOR_BLUE);
    setDColor(pS, d, "downloadsQueuedFileColor", SWT.COLOR_GRAY);
    setDColor(pS, d, "downloadsPausedFileColor", SWT.COLOR_DARK_RED);

    PreferenceConverter.setDefault(pS, "downloadsRateAbove20FileColor", new RGB(0, 160, 0));
    PreferenceConverter.setDefault(pS, "downloadsRateAbove10FileColor", new RGB(0, 140, 0));
    PreferenceConverter.setDefault(pS, "downloadsRateAbove0FileColor", new RGB(0, 110, 0));

    PreferenceConverter.setDefault(pS, "downloadsRateAbove20FontData", dFontData);
    PreferenceConverter.setDefault(pS, "downloadsRateAbove10FontData", dFontData);
    PreferenceConverter.setDefault(pS, "downloadsRateAbove0FontData", dFontData);

    PreferenceConverter.setDefault(pS, "downloadsPausedFontData", dFontData);
    PreferenceConverter.setDefault(pS, "downloadsQueuedFontData", dFontData);
    PreferenceConverter.setDefault(pS, "downloadsDownloadedFontData", dFontData);

    pS.setDefault("graphUploadsType", 0);
    pS.setDefault("graphDownloadsType", 0);
    setDColor(pS, d, "graphUploadsColor1", SWT.COLOR_RED);
    PreferenceConverter.setDefault(pS, "graphUploadsColor2", new RGB(125, 0, 0));
    setDColor(pS, d, "graphDownloadsColor1", SWT.COLOR_GREEN);
    PreferenceConverter.setDefault(pS, "graphDownloadsColor2", new RGB(0, 125, 0));
    setDColor(pS, d, "graphBackgroundColor", SWT.COLOR_BLACK);
    PreferenceConverter.setDefault(pS, "graphGridColor", new RGB(0, 128, 64));
    setDColor(pS, d, "graphTextColor", SWT.COLOR_WHITE);
    setDColor(pS, d, "graphLabelBackgroundColor", SWT.COLOR_WHITE);
    setDColor(pS, d, "graphLabelLineColor", SWT.COLOR_YELLOW);
    setDColor(pS, d, "graphLabelTextColor", SWT.COLOR_BLACK);

    setDColor(pS, d, "serverDisconnectedColor", SWT.COLOR_BLACK);
    setDColor(pS, d, "serverConnectingColor", SWT.COLOR_DARK_YELLOW);
    setDColor(pS, d, "serverConnectedColor", SWT.COLOR_DARK_GREEN);

    setDColor(pS, d, "resultDefaultColor", SWT.COLOR_BLACK);
    setDColor(pS, d, "resultAlreadyDownloadedColor", SWT.COLOR_DARK_GREEN);
    setDColor(pS, d, "resultFakeColor", SWT.COLOR_DARK_RED);

    setDColor(pS, d, "ircInNickColor", SWT.COLOR_DARK_BLUE);
    setDColor(pS, d, "ircOutNickColor", SWT.COLOR_DARK_MAGENTA);
    setDColor(pS, d, "ircJoinColor", SWT.COLOR_DARK_GREEN);
    setDColor(pS, d, "ircPartColor", SWT.COLOR_DARK_RED);
    setDColor(pS, d, "ircModeColor", SWT.COLOR_DARK_YELLOW);

    pS.setDefault("defaultWebBrowser", "");

    pS.setDefault("consoleToolItems", 4);
    pS.setDefault("consoleToolItem1", "cs");
    pS.setDefault("consoleToolItem2", "version");
    pS.setDefault("consoleToolItem3", "??");
    pS.setDefault("consoleToolItem4", "log");

    pS.setDefault("webBrowserTabsOnTop", false);
    pS.setDefault("webBrowserToolItems", 3);
    pS.setDefault("webBrowserToolItem1", "http://www.suprnova.org");
    pS.setDefault("webBrowserToolItem2", "http://www.filedonkey.com/");
    pS.setDefault("webBrowserToolItem3", "http://isohunt.com/");
    pS.setDefault("maxFavoriteLength", 50);

    pS.setDefault("hm_0_hostname", "localhost");
    pS.setDefault("hm_0_username", "admin");
    pS.setDefault("hm_0_password", "");
    pS.setDefault("hm_0_port", 4001);
    pS.setDefault("hm_0_protocol", 0);

    pS.setDefault("hm_0_ssh_host", "192.168.0.1");
    pS.setDefault("hm_0_ssh_rhost", "127.0.0.1");
    pS.setDefault("hm_0_ssh_port", 22);
    pS.setDefault("hm_0_ssh_rport", 4001);
    pS.setDefault("hm_0_ssh_lport", 4001);

    pS.setDefault("refineFilterNegation", false);
    pS.setDefault("refineFilterAlternates", false);

    pS.setDefault("searchForceDownload", false);
    pS.setDefault("searchTooltips", true);

    pS.setDefault("searchFilterPornography", false);
    pS.setDefault("searchFilterProfanity", false);
    pS.setDefault("maintainSortOrder", false);
    pS.setDefault("updateDelay", 2);
    pS.setDefault("graphUpdateDelay", 1);

    pS.setDefault("useGradient", true);
    pS.setDefault("displayNodes", false);
    pS.setDefault("displayChunkGraphs", false);
    pS.setDefault("displayGridLines", true);
    pS.setDefault("tableCellEditors", false);
    pS.setDefault("displayTableColors", true);
    pS.setDefault("coreExecutable", "");
    pS.setDefault("useCombo", false);
    pS.setDefault("minimizeOnClose", false);
    pS.setDefault("systrayOnMinimize", false);
    pS.setDefault("dragAndDrop", true);

    pS.setDefault("pollUpStats", true);
    pS.setDefault("pollUploaders", true);
    pS.setDefault("pollPending", false);
    pS.setDefault("pollDelay", 5);
    pS.setDefault("requestFileInfoDelay", 300);

    pS.setDefault("resultsCTabFolderTabsOnTop", true);
    pS.setDefault("roomsCTabFolderTabsOnTop", true);
    pS.setDefault("messagesCTabFolderTabsOnTop", true);
    pS.setDefault("webBrowserCTabFolderTabsOnTop", false);

    // Tabs
    pS.setDefault("downloadsShowTabs", false);
    pS.setDefault("downloadsTabs", 1);
    pS.setDefault("downloadsTab_0_Name", "All");
    pS.setDefault("downloadsTab_0_Filters", "");

    pS.setDefault("clientsShowTabs", false);
    pS.setDefault("clientsTabs", 1);
    pS.setDefault("clientsTab_0_Name", "All");
    pS.setDefault("clientsTab_0_Filters", "");

    pS.setDefault("uploadsShowTabs", false);
    pS.setDefault("uploadsTabs", 1);
    pS.setDefault("uploadsTab_0_Name", "All");
    pS.setDefault("uploadsTab_0_Filters", "");

    pS.setDefault("serverShowTabs", false);
    pS.setDefault("serverTabs", 1);
    pS.setDefault("serverTab_0_Name", "Connected");
    pS.setDefault("serverTab_0_Filters", "1,22,");
    // End tabs

    pS.setDefault("autoReconnect", false);
    pS.setDefault("autoReconnectDelay", 30);

    pS.setDefault("useLastFile", false);
    pS.setDefault("allowMultipleInstances", false);
    pS.setDefault("downloadsFilterQueued", false);
    pS.setDefault("downloadsFilterPaused", false);
    pS.setDefault("autoCloseRooms", true);
    pS.setDefault("autoOpenRooms", false);
    pS.setDefault("previewExecutable", "");
    pS.setDefault("previewWorkingDirectory", "");
    pS.setDefault("previewUseHttp", false);
    pS.setDefault("versionCheck", false);

    pS.setDefault("bwPreset1_download", 100);
    pS.setDefault("bwPreset1_upload", 25);
    pS.setDefault("bwPreset1_slots", 15);
    pS.setDefault("bwPreset1_concurrent", 60);

    pS.setDefault("bwPreset2_download", 40);
    pS.setDefault("bwPreset2_upload", 20);
    pS.setDefault("bwPreset2_slots", 10);
    pS.setDefault("bwPreset2_concurrent", 25);

    pS.setDefault("bwPreset3_download", 25);
    pS.setDefault("bwPreset3_upload", 10);
    pS.setDefault("bwPreset3_slots", 5);
    pS.setDefault("bwPreset3_concurrent", 10);

    PreferenceConverter.setDefault(pS, "tableFontData", dFontData);
    PreferenceConverter.setDefault(pS, "headerFontData", dFontData);

    pS.setDefault("statisticsSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("statisticsSashMaximized", -1);

    pS.setDefault("graphSashOrientation", SWT.VERTICAL);
    pS.setDefault("graphSashMaximized", -1);

    pS.setDefault("clientSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("clientSashMaximized", 0);
    pS.setDefault("transferSashOrientation", SWT.VERTICAL);
    pS.setDefault("transferSashMaximized", -1);
    pS.setDefault("uploadsSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("uploadsSashMaximized", 0);
    pS.setDefault("locale", "");

    pS.setDefault("searchSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("searchSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "searchSashChild0", new Rectangle(0, 0, 2, 0));
    PreferenceConverter.setDefault(pS, "searchSashChild1", new Rectangle(0, 0, 5, 0));

    pS.setDefault("serversSashOrientation", SWT.VERTICAL);
    pS.setDefault("serversSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "serversSashChild0", new Rectangle(0, 0, 4, 0));
    PreferenceConverter.setDefault(pS, "serversSashChild1", new Rectangle(0, 0, 1, 0));

    pS.setDefault("filesMessagesSashOrientation", SWT.VERTICAL);
    pS.setDefault("filesMessagesSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "filesMessagesSashChild0", new Rectangle(0, 0, 2, 0));
    PreferenceConverter.setDefault(pS, "filesMessagesSashChild1", new Rectangle(0, 0, 5, 0));

    pS.setDefault("directoriesFilesSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("directoriesFilesSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "directoriesFilesSashChild0", new Rectangle(0, 0, 1, 0));
    PreferenceConverter.setDefault(pS, "directoriesFilesSashChild1", new Rectangle(0, 0, 5, 0));

    pS.setDefault("clientFilesSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("clientFilesSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "clientFilesSashChild0", new Rectangle(0, 0, 1, 0));
    PreferenceConverter.setDefault(pS, "clientFilesSashChild1", new Rectangle(0, 0, 5, 0));

    pS.setDefault("messagesSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("messagesSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "messagesSashChild0", new Rectangle(0, 0, 2, 0));
    PreferenceConverter.setDefault(pS, "messagesSashChild1", new Rectangle(0, 0, 5, 0));

    pS.setDefault("ircSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("ircSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "ircSashChild0", new Rectangle(0, 0, 5, 0));
    PreferenceConverter.setDefault(pS, "ircSashChild1", new Rectangle(0, 0, 1, 0));

    pS.setDefault("roomsSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("roomsSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "roomsSashChild0", new Rectangle(0, 0, 2, 0));
    PreferenceConverter.setDefault(pS, "roomsSashChild1", new Rectangle(0, 0, 5, 0));

    pS.setDefault("roomSashOrientation", SWT.HORIZONTAL);
    pS.setDefault("roomSashMaximized", -1);
    PreferenceConverter.setDefault(pS, "roomSashChild0", new Rectangle(0, 0, 2, 0));
    PreferenceConverter.setDefault(pS, "roomSashChild1", new Rectangle(0, 0, 5, 0));

    pS.setDefault("downloadDynamicColumn", IDSelector.getID(DownloadTableTreeView.NAME));
    //preferenceStore.setDefault("uploadDynamicColumn",
    // IDSelector.getID(UploadTableView.NAME));

    //preferenceStore.setDefault("uploadersDynamicColumn",
    // IDSelector.getID(UploadersTableView.NAME));
    //preferenceStore.setDefault("clientDynamicColumn",
    // IDSelector.getID(ClientTableView.NAME));

    pS.setDefault("serverDynamicColumn", IDSelector.getID(ServerTableView.NAME));

    //kind of annoying
    //pS.setDefault("resultDynamicColumn", IDSelector.getID(ResultTableView.NAME));

    pS.setDefault("clientDirectoriesDynamicColumn", IDSelector.getID(ClientDirectoriesTableView.DIRECTORY));
    //  preferenceStore.setDefault("downloadExclusionStateFilters",
    // EnumFileState.QUEUED.getValue());

    pS.setDefault("serverStateFilters", EnumHostState.CONNECTING.getValue()
        | EnumHostState.CONNECTED_INITIATING.getValue() | EnumHostState.CONNECTED.getValue());

    return pS;
  }

  public static void setLocaleString(String string) {
    localeString = string;
  }

  public static String getHomeDirectory() {
    return homeDirectory;
  }

  public static void setHomeDirectory(String string) {
    customHomeDir = true;
    homeDirectory = string;
    String sep = System.getProperty("file.separator");
    if (!string.endsWith(sep))
      homeDirectory += sep;
  }

  public static void setPrefFile(String file) {
    customPrefFile = true;
    prefFileName = file;
    preferenceStore = new PreferenceStore(file);
  }

}