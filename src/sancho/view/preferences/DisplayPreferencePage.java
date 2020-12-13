/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

import sancho.view.utility.WidgetFactory;

public class DisplayPreferencePage extends CPreferencePage {
  public DisplayPreferencePage(String title) {
    super(title);
  }

  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));

    TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
    tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

    createGeneralTab(tabFolder);
    createDownloadsTab(tabFolder);
    createConsoleTab(tabFolder);
    createServerTab(tabFolder);
    createSearchTab(tabFolder);
    createGraphsTab(tabFolder);
    createClientsTab(tabFolder);
    createIRCTab(tabFolder);

    return composite;
  }

  protected void createGeneralTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "p.general");

    setupFontEditor("headerFontData", "p.d.general.headerFont", composite);
    setupFontEditor("tableFontData", "p.d.general.tableFont", composite);
    setupColorEditor("tablesBackgroundColor", "p.d.general.tablesBackgroundColor", composite);

    createSeparator(composite);
    setupBooleanEditor("tableAlternateBGColors", "p.d.general.tableAlternateBGColors", composite);
    setupColorEditor("tableAlternateBGColor", "p.d.general.tableAlternateBGColor", composite);
    createSeparator(composite);

    setupBooleanEditor("tableHilightSorted", "p.d.general.tableHilightSorted", composite);
    setupColorEditor("tableSortedColumnBGColor", "p.d.general.tableSortedColumnBGColor", composite);

    createSeparator(composite);

    setupColorEditor("dndBackgroundColor", "p.d.general.dndBackgroundColor", composite);
    setupColorEditor("dndForegroundColor", "p.d.general.dndForegroundColor", composite);
    setupFontEditor("dndFontData", "p.d.general.dndFont", composite);
    setupIntegerEditor("dndWidth", "p.d.general.dndWidth", 5, 1000, composite);
    createSeparator(composite);
    setupBooleanEditor("splashScreen", "p.d.general.splashScreen", composite);
    setupBooleanEditor("flatInterface", "p.d.general.flatInterface", composite);
    setupBooleanEditor("useGradient", "p.d.general.useGradient", composite);
    setupBooleanEditor("displayTableColors", "p.d.general.displayTableColors", composite);
    setupBooleanEditor("displayGridLines", "p.d.general.displayGridLines", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createDownloadsTab(TabFolder tabFolder) {

    Composite composite = createNewTab(tabFolder, "tab.transfers");
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    TabFolder transfersTabFolder = new TabFolder(composite, SWT.BOTTOM);
    transfersTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

    createDownloadsColorsTab(transfersTabFolder);
    createDownloadsFontsTab(transfersTabFolder);

  }

  protected void createDownloadsColorsTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.colors");
    setupColorEditor("downloadsAvailableFileColor", "p.d.downloads.available", composite);
    setupColorEditor("downloadsUnAvailableFileColor", "p.d.downloads.unavailable", composite);
    setupColorEditor("downloadsPausedFileColor", "p.d.downloads.paused", composite);
    setupColorEditor("downloadsQueuedFileColor", "p.d.downloads.queued", composite);
    setupColorEditor("downloadsDownloadedFileColor", "p.d.downloads.downloaded", composite);
    setupColorEditor("downloadsRateAbove20FileColor", "p.d.downloads.rateAbove20", composite);
    setupColorEditor("downloadsRateAbove10FileColor", "p.d.downloads.rateAbove10", composite);
    setupColorEditor("downloadsRateAbove0FileColor", "p.d.downloads.rateAbove0", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createDownloadsFontsTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.fonts");

    setupFontEditor("downloadsPausedFontData", "p.d.downloads.pausedFontData", composite);
    setupFontEditor("downloadsQueuedFontData", "p.d.downloads.queuedFontData", composite);
    setupFontEditor("downloadsDownloadedFontData", "p.d.downloads.downloadedFontData", composite);

    setupFontEditor("downloadsRateAbove20FontData", "p.d.downloads.rateAbove20FontData", composite);
    setupFontEditor("downloadsRateAbove10FontData", "p.d.downloads.rateAbove10FontData", composite);
    setupFontEditor("downloadsRateAbove0FontData", "p.d.downloads.rateAbove0FontData", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createConsoleTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.console");

    setupFontEditor("consoleFontData", "p.d.console.font", composite);

    createSeparator(composite);

    setupColorEditor("consoleBackground", "p.d.console.background", composite);
    setupColorEditor("consoleForeground", "p.d.console.foreground", composite);
    setupColorEditor("consoleHighlight", "p.d.console.highlight", composite);
    setupColorEditor("consoleInputBackground", "p.d.console.inputBackground", composite);
    setupColorEditor("consoleInputForeground", "p.d.console.inputForeground", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createServerTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.servers");

    setupColorEditor("serverConnectedColor", "p.d.server.connected", composite);
    setupColorEditor("serverConnectingColor", "p.d.server.connecting", composite);
    setupColorEditor("serverDisconnectedColor", "p.d.server.disconnected", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createSearchTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.search");

    setupColorEditor("resultAlreadyDownloadedColor", "p.d.search.alreadyDownloaded", composite);
    setupColorEditor("resultFakeColor", "p.d.search.fake", composite);
    setupColorEditor("resultDefaultColor", "p.d.search.default", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createGraphsTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "tab.statistics");

    setupColorEditor("graphBackgroundColor", "p.d.graphs.background", composite);
    setupColorEditor("graphGridColor", "p.d.graphs.grid", composite);
    setupColorEditor("graphTextColor", "p.d.graphs.text", composite);
    setupColorEditor("graphUploadsColor1", "p.d.graphs.uploads1", composite);
    setupColorEditor("graphUploadsColor2", "p.d.graphs.uploads2", composite);
    setupColorEditor("graphDownloadsColor1", "p.d.graphs.downloads1", composite);
    setupColorEditor("graphDownloadsColor2", "p.d.graphs.downloads2", composite);
    setupColorEditor("graphLabelBackgroundColor", "p.d.graphs.labelBackground", composite);
    setupColorEditor("graphLabelTextColor", "p.d.graphs.labelText", composite);
    setupColorEditor("graphLabelLineColor", "p.d.graphs.labelLine", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createClientsTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.clients");

    setupColorEditor("clientsDisconnectedColor", "p.d.clients.disconnectedColor", composite);
    setupColorEditor("clientsConnectedColor", "p.d.clients.connectedColor", composite);
    setupColorEditor("clientsHasFilesColor", "p.d.clients.hasFilesColor", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

  protected void createIRCTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.IRC");

    setupFontEditor("ircConsoleFontData", "p.d.console.font", composite);

    createSeparator(composite);

    setupColorEditor("ircConsoleBackground", "p.d.console.background", composite);
    setupColorEditor("ircConsoleForeground", "p.d.console.foreground", composite);
    setupColorEditor("ircConsoleHighlight", "p.d.console.highlight", composite);
    setupColorEditor("ircConsoleInputBackground", "p.d.console.inputBackground", composite);
    setupColorEditor("ircConsoleInputForeground", "p.d.console.inputForeground", composite);

    setupColorEditor("ircInNickColor", "p.d.irc.inNickColor", composite);
    setupColorEditor("ircOutNickColor", "p.d.irc.outNickColor", composite);
    setupColorEditor("ircJoinColor", "p.d.irc.joinColor", composite);
    setupColorEditor("ircPartColor", "p.d.irc.partColor", composite);
    setupColorEditor("ircModeColor", "p.d.irc.modeColor", composite);

    composite.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
  }

}
