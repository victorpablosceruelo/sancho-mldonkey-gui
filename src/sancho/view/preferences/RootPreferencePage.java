/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import sancho.utility.VersionInfo;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class RootPreferencePage extends CPreferencePage {
  public RootPreferencePage(String title) {
    super(title);
  }

  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));

    TabFolder tabFolder = new TabFolder(composite, SWT.TOP);
    tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

    createGeneralTab(tabFolder);
    createDownloadsTab(tabFolder);
    createSearchTab(tabFolder);
    createServersTab(tabFolder);
    createConsoleTab(tabFolder);
    createGraphTab(tabFolder);
    createRoomsTab(tabFolder);
    createIRCTab(tabFolder);
    createWebBrowserTab(tabFolder);

    return composite;
  }

  protected void createGeneralTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "p.general");

    String[] extensions;

    if (VersionInfo.getOSPlatform().equals("Windows"))
      extensions = new String[]{"*.exe;*.bat"};
    else
      extensions = new String[]{"*"};

    createInformationLabel(composite, "p.coreExecutableInfo");
    setupFileEditor("coreExecutable", "p.r.general.coreExecutable", extensions, composite);
    setupBooleanEditor("killSpawnedCoreOnExit", "p.r.general.killSpawnedCoreOnExit", composite);

    if (!VersionInfo.getOSPlatform().equals("Windows")) {
      createSeparator(composite);

      createInformationLabel(composite, "p.webBrowserInfo");
      setupFileEditor("defaultWebBrowser", "p.r.general.defaultBrowser", extensions, composite);
    }

    createSeparator(composite);
    createInformationLabel(composite, "p.localeInfo");

    Label l = new Label(composite, SWT.NONE);
    l.setText(SResources.getString("p.r.general.locale"));
    final Combo c = new Combo(composite, SWT.READ_ONLY);

    String currentValue = PreferenceLoader.loadString("locale");

    c.add(SResources.S_ES);
    String[] s = getLocales();
    for (int i = 0; i < s.length; i++) {
      c.add(s[i]);
      if (s[i].equals(currentValue)) {
        c.select(i + 1);
      }
    }

    c.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        PreferenceLoader.getPreferenceStore().setValue("locale", c.getItem(c.getSelectionIndex()));
      }
    });

    createSeparator(composite);
    setupBooleanEditor("autoReconnect", "p.r.general.autoReconnect", composite);
    setupIntegerEditor("autoReconnectDelay", "p.r.general.autoReconnectDelay", 1, 10000, composite);
    createSeparator(composite);

    if (SWT.getPlatform().equals("win32") || SWT.getPlatform().equals("gtk")) {
      setupBooleanEditor("minimizeOnClose", "p.r.general.systrayOnClose", composite);
      setupBooleanEditor("systrayOnMinimize", "p.r.general.systrayOnMinimize", composite);
    }
    setupBooleanEditor("allowMultipleInstances", "p.r.general.multipleInstances", composite);
    setupBooleanEditor("hostManagerOnStart", "p.r.general.hostManagerOnStart", composite);
    setupBooleanEditor("useLastFile", "p.r.general.useLastFile", composite);
    setupBooleanEditor("killCoreOnExit", "p.r.general.killCoreOnExit", composite);
    setupBooleanEditor("versionCheck", "p.r.general.versionCheck", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createDownloadsTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.transfers");
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    TabFolder mainTabFolder = new TabFolder(composite, SWT.BOTTOM);
    mainTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

    createDownloadsGeneral(mainTabFolder);
    createDownloadsPreview(mainTabFolder);
    createDownloadsExplorer(mainTabFolder);
  }

  protected void createDownloadsGeneral(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "p.general");

    createInformationLabel(composite, "p.delayInfo");
    setupIntegerEditor("updateDelay", "p.r.downloads.updateDelay", 0, 600, composite);

    createSeparator(composite);

    setupBooleanEditor("pollUpStats", "p.r.downloads.pollUpstats", composite);
    setupIntegerEditor("pollDelay", "p.r.downloads.pollDelay", 1, 600, composite);

    createSeparator(composite);
    setupIntegerEditor("requestFileInfoDelay", "p.r.downloads.requestFileInfoDelay", 0, 99999, composite);
    createSeparator(composite);
    
    setupBooleanEditor("displayChunkGraphs", "p.r.downloads.displayChunkGraphs", composite);
    setupBooleanEditor("tableCellEditors", "p.r.downloads.tableCellEditors", composite);
    setupBooleanEditor("dragAndDrop", "p.r.downloads.dragAndDrop", composite);
    setupBooleanEditor("maintainSortOrder", "p.r.downloads.maintainSortOrder", composite);
    setupBooleanEditor("downloadCompleteDialog", "p.r.downloads.downloadCompleteDialog", composite);
    setupBooleanEditor("downloadCompleteLog", "p.r.downloads.downloadCompleteLog", composite);
    setupBooleanEditor("mldonkey.InterestedInSources", "p.r.downloads.interestedInSources", composite);
    
    // setupBooleanEditor("downloadsTabsOnBottom", "p.r.downloads.downloadTabsOnBottom", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createDownloadsPreview(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "m.d.preview");

    String[] extensions;

    if (VersionInfo.getOSPlatform().equals("Windows"))
      extensions = new String[]{"*.exe;*.bat"};
    else
      extensions = new String[]{"*"};

    createInformationLabel(composite, "p.previewInfo");
    setupFileEditor("previewExecutable", "p.r.downloads.previewExecutable", extensions, composite);
    setupDirectoryEditor("previewWorkingDirectory", "p.r.downloads.previewWorkingDirectory", composite);

    createSeparator(composite);

    // real ugly
    // -----------------------------------------------------------------

    final ArrayList extList = new ArrayList();
    final ArrayList progList = new ArrayList();

    GridData gd;
    Composite pComposite = new Composite(composite, SWT.NONE);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 3;
    pComposite.setLayoutData(gd);
    pComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 0, 0, false));

    Composite pSubComp = new Composite(pComposite, SWT.NONE);
    pSubComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    pSubComp.setLayout(WidgetFactory.createGridLayout(5, 0, 0, 0, 5, false));

    Label info = new Label(pSubComp, SWT.NONE);
    info.setText(SResources.getString("p.previewExtensionsInfo"));
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 5;
    info.setLayoutData(gd);

    Label l = new Label(pSubComp, SWT.NONE);
    l.setText(SResources.getString("l.ext"));

    final Text extText = new Text(pSubComp, SWT.SINGLE | SWT.BORDER);
    extText.setText("mp3");

    new Label(pSubComp, SWT.NONE).setText("=");

    final Text progText = new Text(pSubComp, SWT.SINGLE | SWT.BORDER);
    progText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Button browseButton = new Button(pSubComp, SWT.NONE);
    browseButton.setText(SResources.getString("b.browse"));
    browseButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    browseButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        FileDialog fileDialog = new FileDialog(progText.getShell(), SWT.SINGLE);

        if (fileDialog.open() != null) {
          String path = fileDialog.getFilterPath() + System.getProperty("file.separator");
          path += fileDialog.getFileName();
          progText.setText(path);
        }
      }
    });

    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 5;
    Composite butComp = new Composite(pSubComp, SWT.NONE);
    butComp.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 0, 0, false));
    butComp.setLayoutData(gd);

    Button addButton = new Button(butComp, SWT.NONE);
    addButton.setText(SResources.getString("b.add"));
    addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Button remButton = new Button(butComp, SWT.NONE);
    remButton.setText(SResources.getString("b.remove"));
    remButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final List list = new List(pComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    gd = new GridData();
    gd.widthHint = 200;
    gd.heightHint = 75;
    list.setLayoutData(gd);

    addButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (!extText.getText().equals(SResources.S_ES) && !progText.getText().equals(SResources.S_ES)) {
          String newExt = extText.getText();
          String newProg = progText.getText();

          int foundNum = -1;
          for (int i = 0; i < extList.size(); i++) {
            String ext = (String) extList.get(i);
            if (ext.equalsIgnoreCase(newExt)) {
              foundNum = i;
            }
          }
         /* if (foundNum != -1) {
            extList.remove(foundNum);
            progList.remove(foundNum);
          } */
          extList.add(newExt);
          progList.add(newProg);

          refreshList(list, extList, progList);
        }
      }
    });

    remButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (list.getSelectionIndex() != -1) {
          int num = list.getSelectionIndex();
          extList.remove(num);
          progList.remove(num);
          refreshList(list, extList, progList);
        }
      }
    });

    loadList(list, extList, progList);

    // ---------------------------------------------------

    createSeparator(composite);
    createInformationLabel(composite, "p.previewHttpInfo");
    setupBooleanEditor("previewUseHttp", "p.r.downloads.previewUseHttp", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createDownloadsExplorer(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "p.explorer");

    String[] extensions;

    if (VersionInfo.getOSPlatform().equals("Windows"))
      extensions = new String[]{"*.exe;*.bat"};
    else
      extensions = new String[]{"*"};

    createInformationLabel(composite, "p.explorerInfo");
    setupFileEditor("explorerExecutable", "p.r.downloads.explorerExecutable", extensions, composite);
    setupDirectoryEditor("explorerOpenFolder", "p.r.downloads.explorerOpenFolder", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createSearchTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.search");
    setupBooleanEditor("searchForceDownload", "p.r.search.forceDownload", composite);
    setupBooleanEditor("searchFilterPornography", "p.r.search.filterPornography", composite);
    setupBooleanEditor("searchFilterProfanity", "p.r.search.filterProfanity", composite);
    setupBooleanEditor("searchTooltips", "p.r.search.tooltips", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createConsoleTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.console");
    setupIntegerEditor("consoleMaxLines", "p.r.console.maxLines", 25, 10000, composite);
    createSeparator(composite);
    setupIntegerEditor("consoleToolItems", "p.r.console.toolItems", 0, 9, composite);
    createSeparator(composite);
    for (int i = 1; i < 10; i++) {
      setupStringEditor("consoleToolItem" + i, String.valueOf(i) + " ", "p.r.console.toolItem", '0',
          composite);
    }
    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));

  }

  protected void createServersTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.servers");

    setupBooleanEditor("displayNodes", "p.r.servers.displayNodes", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createGraphTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.statistics");
    createInformationLabel(composite, "p.delayInfo");
    setupIntegerEditor("graphUpdateDelay", "p.r.graphs.updateDelay", 0, 600, composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createRoomsTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.rooms");
    setupBooleanEditor("autoCloseRooms", "p.r.rooms.autoClose", composite);
    setupBooleanEditor("autoOpenRooms", "p.r.rooms.autoOpen", composite);
    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createIRCTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.IRC");

    setupStringEditor("ircServer", "p.r.irc.ircServer", '0', composite);
    setupStringEditor("ircChannel", "p.r.irc.ircChannel", '0', composite);
    setupStringEditor("ircNickname", "p.r.irc.ircNickname", '0', composite);
    setupBooleanEditor("ircAutoConnect", "p.r.irc.autoConnect", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createWebBrowserTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.webbrowser");
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    TabFolder webTabFolder = new TabFolder(composite, SWT.BOTTOM);
    webTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

    // createWebBrowserGeneral(webTabFolder);
    if (SWT.getPlatform().equals("win32"))
      createWebBrowserFavorites(webTabFolder);
    createWebBrowserToolItems(webTabFolder);
  }

  protected void createWebBrowserToolItems(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.toolItems");
    setupIntegerEditor("webBrowserToolItems", "p.r.webbrowser.toolItems", 0, 9, composite);
    createSeparator(composite);
    for (int i = 1; i < 10; i++) {
      setupStringEditor("webBrowserToolItem" + i, String.valueOf(i) + " ", "p.r.webbrowser.toolItem", '0',
          composite);
    }
    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  //  protected void createWebBrowserGeneral(TabFolder tabFolder) {
  //    Composite composite = createNewTab(tabFolder, "p.general");
  //    setupBooleanEditor("webBrowserTabsOnTop", "p.r.webbrowser.tabsOnTop", composite);
  //    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  //  }

  protected void createWebBrowserFavorites(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.favorites");
    createInformationLabel(composite, "p.favoritesDirectoryInfo");
    setupDirectoryEditor("favoritesDirectory", "p.r.webbrowser.favoritesDirectory", composite);
    setupIntegerEditor("maxFavoriteLength", "p.r.webbrowser.maxFavoriteLength", 0, 500, composite);
    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected String[] getLocales() {
    String userDIR = VersionInfo.getHomeDirectory();

    File f = new File(userDIR);

    File[] fileArray = f.listFiles(new PropertiesFilter());
    ArrayList stringList = new ArrayList();

    for (int i = 0; i < fileArray.length; i++) {
      String name = fileArray[i].getName();
      if (name.length() >= 18) {
        String ls = name.substring(7, name.length() - 11);
        stringList.add(ls);
      }
    }

    String[] resultArray = new String[stringList.size()];
    stringList.toArray(resultArray);

    return resultArray;
  }

  static class PropertiesFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
      String lower = name.toLowerCase();
      return name.startsWith(VersionInfo.getName()) && name.endsWith(".properties");
    }
  }

  public void refreshList(List list, ArrayList extList, ArrayList progList) {
    list.removeAll();
    String[] sArray = new String[extList.size()];
    for (int i = 0; i < extList.size(); i++) {
      sArray[i] = extList.get(i) + " = " + progList.get(i);
    }
    list.setItems(sArray);
    saveList(extList, progList);
  }

  public void saveList(ArrayList extList, ArrayList progList) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < extList.size(); i++) {
      sb.append(extList.get(i));
      sb.append(";");
      sb.append(progList.get(i));
      sb.append(";");
    }
    PreferenceLoader.getPreferenceStore().setValue("previewExtensions", sb.toString());
  }

  public void loadList(List list, ArrayList extList, ArrayList progList) {

    String previewExtensions = PreferenceLoader.loadString("previewExtensions");

    if (!previewExtensions.equals(SResources.S_ES)) {
      StringTokenizer st = new StringTokenizer(previewExtensions, ";");
      int ct = st.countTokens();

      String ext = SResources.S_ES;
      String prog = SResources.S_ES;

      while (st.hasMoreTokens()) {
        ext = st.nextToken();
        if (st.hasMoreTokens()) {
          prog = st.nextToken();
          list.add(ext + " = " + prog);
          extList.add(ext);
          progList.add(prog);
        }
      }
    }
  }

}