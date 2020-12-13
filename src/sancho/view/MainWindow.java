/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import sancho.core.CoreFactory;
import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.VersionInfo;
import sancho.view.mainWindow.CToolBar;
import sancho.view.mainWindow.MenuBar;
import sancho.view.mainWindow.Minimizer;
import sancho.view.mainWindow.MinimizerTray;
import sancho.view.preferences.CPreferenceManager;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.DNDBox;
import sancho.view.utility.DownloadCompleteDialog;
import sancho.view.utility.IDSelector;
import sancho.view.utility.LinkRipper;
import sancho.view.utility.SResources;
import sancho.view.utility.Splash;
import sancho.view.utility.WidgetFactory;
import sancho.view.utility.dialogs.BugDialog;

public class MainWindow implements ShellListener, Observer, DisposeListener {
  private static final int TRANSFER = 0;
  private static final int SEARCH = 1;
  private static final int SERVER = 2;
  private static final int SHARES = 3;
  private static final int CONSOLE = 4;
  private static final int STATISTICS = 5;
  private static final int MESSAGES = 6;
  private static final int ROOMS = 7;
  private static final int WEBBROWSER = 8;
  private static final String[] ALL_TAB_IDS = new String[]{"tab.transfers", "tab.search", "tab.servers",
      "tab.shares", "tab.console", "tab.statistics", "tab.friends", "tab.rooms", "tab.webbrowser"};
  private static final String prefString = "mainWindow";
  private String titleBarText = VersionInfo.getName() + " " + VersionInfo.getVersion();
  private CToolBar coolBar;
  private StatusLine statusLine;
  private StackLayout stackLayout;
  private Minimizer minimizer;
  private Shell shell;
  private Composite mainComposite;
  private Composite pageContainer;
  private List registeredTabs;
  private AbstractTab activeTab;
  private LinkRipper linkRipper = null;
  private static Clipboard clipboard;
  private static List ircShellList;
  private DownloadCompleteDialog downloadCompleteDialog;
  private DNDBox dndBox;
  private boolean closing;

  public MainWindow(Display display) {
    Sancho.hasLoaded = true;

    if (Sancho.monitorMode) {
      Splash.dispose();
      this.shell = new Shell(display, SWT.NO_TRIM | SWT.NO_BACKGROUND);
      this.shell.addDisposeListener(this);
      this.registeredTabs = new ArrayList();
      Sancho.getCoreFactory().addObserver(this);
      dndBox = new DNDBox(this);
    } else {
      clipboard = new Clipboard(display);
      this.shell = new Shell(display);
      this.shell.setImage(SResources.getImage("ProgramIcon"));
      this.shell.setLayout(new FillLayout());
      this.shell.addShellListener(this);

      boolean hasTray = SWT.getPlatform().equals("win32") || SWT.getPlatform().equals("fox")
          || SWT.getPlatform().equals("gtk");

      this.minimizer = hasTray ? new MinimizerTray(this, titleBarText) : new Minimizer(this, titleBarText);
      this.minimizer.setTitleBarText();

      Splash.updateText("splash.creatingGUI");
      this.registeredTabs = new ArrayList();
      createContents(shell);
      this.shell.pack();

      Sancho.getCoreFactory().addObserver(this);
      Splash.dispose();

      // must come after pack()
      restoreWindowBounds(shell);
      this.shell.open();

      this.shell.addDisposeListener(this);

      if (PreferenceLoader.loadBoolean("dndBox"))
        toggleDNDBox();

    }

    System.out.print(SResources.S_ES);

    try {
      while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) {
          display.sleep();
        }
      }
    } catch (SWTError serr) {
      if (Sancho.debug) {
        serr.printStackTrace();
        if (serr.throwable != null)
          serr.throwable.printStackTrace();
      } else {
        StringWriter sw = new StringWriter();
        if (activeTab != null)
          sw.write(activeTab.toString() + "\n");
        serr.printStackTrace(new PrintWriter(sw, true));
        if (serr.throwable != null)
          serr.throwable.printStackTrace(new PrintWriter(sw, true));
        new BugDialog(new Shell(display), sw.toString()).open();
      }
    } catch (SWTException se) {
      if (Sancho.debug) {
        se.printStackTrace();
        if (se.throwable != null)
          se.throwable.printStackTrace();
      } else {
        StringWriter sw = new StringWriter();
        if (activeTab != null)
          sw.write(activeTab.toString() + "\n");
        se.printStackTrace(new PrintWriter(sw, true));
        if (se.throwable != null)
          se.throwable.printStackTrace(new PrintWriter(sw, true));
        new BugDialog(new Shell(display), sw.toString()).open();
      }
    } catch (Exception e) {
      if (Sancho.debug)
        e.printStackTrace();
      else {
        StringWriter sw = new StringWriter();
        if (activeTab != null)
          sw.write(activeTab.toString() + "\n");
        e.printStackTrace(new PrintWriter(sw, true));
        new BugDialog(new Shell(display), sw.toString()).open();
      }
    }
  }

  private void createContents(Shell parent) {
    this.mainComposite = new Composite(parent, SWT.NONE);
    this.mainComposite.setLayout(WidgetFactory.createGridLayout(1, 1, 1, 0, 1, false));

    new MenuBar(this);

    new Label(mainComposite, SWT.HORIZONTAL | SWT.SEPARATOR).setLayoutData(new GridData(
        GridData.FILL_HORIZONTAL));

    this.coolBar = new CToolBar(this, PreferenceLoader.loadBoolean("toolbarSmallButtons"));

    this.pageContainer = new Composite(mainComposite, SWT.NONE);
    this.pageContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
    this.stackLayout = new StackLayout();
    this.pageContainer.setLayout(stackLayout);

    this.addTabs();
    this.coolBar.layoutCoolBar();
    this.statusLine = new StatusLine(this, true);
  }

  private void addTab(int i) {

    if (i == WEBBROWSER && Sancho.noBrowser)
      return;

    Splash.updateText("splash.creatingTab", SResources.getString(ALL_TAB_IDS[i]), i + 1);

    switch (i) {
      case TRANSFER :
        registeredTabs.add(new TransferTab(this, ALL_TAB_IDS[i]));
        break;
      case SEARCH :
        registeredTabs.add(new SearchTab(this, ALL_TAB_IDS[i]));
        break;
      case SERVER :
        registeredTabs.add(new ServerTab(this, ALL_TAB_IDS[i]));
        break;
      case SHARES :
        registeredTabs.add(new SharesTab(this, ALL_TAB_IDS[i]));
        break;
      case CONSOLE :
        registeredTabs.add(new ConsoleTab(this, ALL_TAB_IDS[i]));
        break;
      case STATISTICS :
        registeredTabs.add(new StatisticTab(this, ALL_TAB_IDS[i]));
        break;
      case MESSAGES :
        registeredTabs.add(new FriendsTab(this, ALL_TAB_IDS[i]));
        break;
      case ROOMS :
        registeredTabs.add(new RoomsTab(this, ALL_TAB_IDS[i]));
        break;
    
      default :
        break;
    }
  }

  private void addTabs() {
    String prefTabs = IDSelector.loadIDs(prefString + "Tabs", getAllTabIDs());

    int arrayItem;
    for (int i = 0; i < prefTabs.length(); i++) {
      arrayItem = prefTabs.charAt(i) - IDSelector.MAGIC_NUMBER;
      addTab(arrayItem);
    }
    setVisible(true);

    AbstractTab aTab = (AbstractTab) registeredTabs.get(0);
    aTab.setActive();
  }

  public AbstractTab getActiveTab() {
    return activeTab;
  }

  public LinkRipper openLinkRipper() {
    linkRipper = new LinkRipper(new Shell(), this);
    linkRipper.create();
    return linkRipper;
  }

  public LinkRipper getLinkRipper() {
    return linkRipper;
  }

  public void closeLinkRipper() {
    linkRipper = null;
  }

  private void removeAllTabs() {
    for (int i = 0; i < registeredTabs.size(); i++) {
      AbstractTab aTab = (AbstractTab) registeredTabs.get(i);
      aTab.setInActive();
      aTab.dispose();
    }
    registeredTabs.clear();

    ((StackLayout) pageContainer.getLayout()).topControl = null;
    pageContainer.layout();
    activeTab = null;
  }

  public void resetTabs() {
    removeAllTabs();
    getCoolBar().reset();
    addTabs();
    getCoolBar().layoutCoolBar();
    System.gc();
  }

  public void setActive(AbstractTab activatedTab) {
    if (activeTab != null)
      activeTab.setInActive();
    stackLayout.topControl = activatedTab.getContent();
    pageContainer.layout();
    activeTab = activatedTab;
  }

  public void openPreferences() {
    CPreferenceManager myprefs = new CPreferenceManager(PreferenceLoader.getPreferenceStore());
    if (myprefs.open(new Shell()) == PreferenceDialog.OK) {
      for (Iterator i = registeredTabs.iterator(); i.hasNext();)
        ((AbstractTab) i.next()).updateDisplay();
      if (getCore() != null)
        getCore().updatePreferences();
    }
  }

  public void restoreWindowBounds(Shell shell) {
    if (PreferenceLoader.loadBoolean("windowMaximized"))
      shell.setMaximized(true);
    else
      shell.setBounds(PreferenceLoader.loadRectangle("windowBounds"));
  }

  public void saveWindowBounds(Shell shell) {
    PreferenceStore p = PreferenceLoader.getPreferenceStore();

    if (shell.getMaximized())
      p.setValue("windowMaximized", shell.getMaximized());
    else {
      PreferenceConverter.setValue(p, "windowBounds", shell.getBounds());
      p.setValue("windowMaximized", shell.getMaximized());
    }
  }

  public void update(Observable o, final Object obj) {
    if (shell == null || shell.isDisposed() || closing)
      return;

    if (o instanceof CoreFactory) {

      shell.getDisplay().syncExec(new Runnable() {
        public void run() {
          if (shell == null || shell.isDisposed())
            return;

          if (obj instanceof File) {
            if (PreferenceLoader.loadBoolean("downloadCompleteDialog")) {
              if (downloadCompleteDialog == null) {
                downloadCompleteDialog = new DownloadCompleteDialog(new Shell(), MainWindow.this);
                downloadCompleteDialog.open();
              }
              downloadCompleteDialog.addFile((File) obj);
            }
          } else if (obj instanceof Boolean) {
            boolean b = obj == Boolean.TRUE ? true : false;

            for (Iterator i = registeredTabs.iterator(); i.hasNext();) {
              AbstractTab aTab = (AbstractTab) i.next();

              if (b)
                aTab.onConnect();
              else
                aTab.onDisconnect();
            }

            if (statusLine != null)
              statusLine.setConnected(b);
            if (minimizer != null)
              minimizer.setConnected(b);
            if (dndBox != null)
              dndBox.setConnected(b);
          } else if (obj instanceof String) {
            if (statusLine != null)
              statusLine.setText((String) obj);
          }
        }
      });
    }
  }

  public void configureTabs() {
    IDSelector idSelector = new IDSelector(shell, getTabLegend(), getPreferenceString(), "Tabs");
    if (idSelector.open() == IDSelector.OK) {
      idSelector.savePrefs();
      resetTabs();
    }
  }

  public void toggleDNDBox() {
    if (dndBox == null) {
      dndBox = new DNDBox(this);
      PreferenceLoader.getPreferenceStore().setValue("dndBox", true);
    } else {
      dndBox.close();
      dndBox = null;
      PreferenceLoader.getPreferenceStore().setValue("dndBox", false);
    }
  }

  public void closeDownloadCompleteDialog() {
    downloadCompleteDialog = null;
  }

  public Composite getMainComposite() {
    return mainComposite;
  }

  public StatusLine getStatusline() {
    return statusLine;
  }

  public List getTabs() {
    return this.registeredTabs;
  }

  public String getPreferenceString() {
    return prefString;
  }

  public String getAllTabIDs() {
    return IDSelector.createIDString(ALL_TAB_IDS);
  }

  public String[] getTabLegend() {
    return ALL_TAB_IDS;
  }

  public Composite getPageContainer() {
    return pageContainer;
  }

  public ICore getCore() {
    return Sancho.getCoreFactory().getCore();
  }

  public Shell getShell() {
    return shell;
  }

  public CToolBar getCoolBar() {
    return coolBar;
  }

  public Minimizer getMinimizer() {
    return minimizer;
  }

  public void setVisible(boolean b) {
    for (int i = 0; i < registeredTabs.size(); i++)
      ((AbstractTab) registeredTabs.get(i)).setVisible(b);
  }

  public void shellActivated(ShellEvent e) {
  }

  public void shellClosed(ShellEvent e) {
    e.doit = minimizer.close();
    setVisible(false);
  }

  public void shellDeactivated(ShellEvent e) {
  }

  public void shellDeiconified(ShellEvent e) {
    minimizer.restore();
    setVisible(true);
  }

  public void shellIconified(ShellEvent e) {
    e.doit = minimizer.minimize();
    setVisible(false);
  }

  public static void copyToClipboard(String string) {
    if (string == null || clipboard == null || clipboard.isDisposed())
      return;

    clipboard.setContents(new String[]{string}, new Transfer[]{TextTransfer.getInstance()});
  }

  public void widgetDisposed(DisposeEvent e) {
    saveWindowBounds(shell);

    if (getLinkRipper() != null)
      getLinkRipper().close();

    if (downloadCompleteDialog != null)
      downloadCompleteDialog.close();

    if (dndBox != null)
      dndBox.close();

    //    for (Iterator i = registeredTabs.iterator(); i.hasNext();)
    //      ((AbstractTab) i.next()).dispose();

    if (PreferenceLoader.loadBoolean("killSpawnedCoreOnExit")
        && (Sancho.getCoreConsole() != null || Sancho.spawnAborted)) {
      Sancho.send(OpCodes.S_KILL_CORE);
      if (Sancho.getCoreConsole() != null)
        Sancho.getCoreConsole().dispose();
    } else if (PreferenceLoader.loadBoolean("killCoreOnExit") && Sancho.getCoreFactory().isConnected())
      Sancho.send(OpCodes.S_KILL_CORE);

    if (Sancho.getCore() != null) {
      closing = true;
      Sancho.getCoreFactory().deleteObservers();
      Sancho.getCoreFactory().disconnect();
    }

    PreferenceLoader.saveStore();
    PreferenceLoader.cleanUp();

    if (clipboard != null && !clipboard.isDisposed())
      clipboard.dispose();
  }

}