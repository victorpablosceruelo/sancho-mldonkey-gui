/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import gnu.regexp.RE;
import gnu.regexp.REMatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.core.Sancho;
import sancho.utility.SwissArmy;
import sancho.utility.VersionInfo;
import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.UniformResourceLocator;

// this is just ugly..
public class LinkRipper extends Dialog implements Runnable, IMenuListener {
  static RE endSlashRE;
  static RE frameRE;

  static RE snRE;
  MainWindow mainWindow;
  MenuManager popupMenu;
  boolean ripping;
  Thread thread;
  Group urlGroup;

  List urlList;
  Text urlText;
  String URLtoRip;

  public LinkRipper(Shell shell, MainWindow mainWindow) {
    super(shell);
    this.mainWindow = mainWindow;
  }

  private void activateDropTarget(final Text linkEntryText) {
    DropTarget dropTarget = new DropTarget(linkEntryText, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_LINK);
    final UniformResourceLocator uRL = UniformResourceLocator.getInstance();
    final TextTransfer textTransfer = TextTransfer.getInstance();
    dropTarget.setTransfer(new Transfer[]{uRL, textTransfer});
    dropTarget.addDropListener(new DropTargetAdapter() {
      public void dragEnter(DropTargetEvent event) {
        event.detail = DND.DROP_COPY;

        for (int i = 0; i < event.dataTypes.length; i++) {
          if (uRL.isSupportedType(event.dataTypes[i])) {
            event.detail = DND.DROP_LINK;
            break;
          }
        }
      }

      public void drop(DropTargetEvent event) {
        if (event.data == null)
          return;

        linkEntryText.append((String) event.data);
      }
    });
  }

  public void addMenuItem(Menu menu, String resString, String imageString, SelectionAdapter selectionAdapter) {
    MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
    menuItem.setText(SResources.getString(resString));
    menuItem.setImage(SResources.getImage(imageString));
    menuItem.addSelectionListener(selectionAdapter);
  }

  public void addToClipBoard(String string) {
    MainWindow.copyToClipboard(string);
  }

  public boolean close() {
    PreferenceStore p = PreferenceLoader.getPreferenceStore();
    PreferenceConverter.setValue(p, "linkRipperWindowBounds", getShell().getBounds());

    mainWindow.closeLinkRipper();
    return super.close();
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(SResources.getImage("web-link"));
    newShell.setText(VersionInfo.getName() + " " + SResources.getString("l.linkRipper"));
  }

  protected void constrainShellSize() {
    super.constrainShellSize();
    Shell shell = getShell();

    if (PreferenceLoader.contains("linkRipperWindowBounds")) {
      shell.setBounds(PreferenceLoader.loadRectangle("linkRipperWindowBounds"));
    } else {
      shell.setSize(500, 300);
      Point loc = shell.getLocation();
      getShell().setLocation(loc.x - 200, loc.y);
    }

  }

  protected Control createButtonBar(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 5, false));
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Button launch = new Button(composite, SWT.NONE);
    launch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    launch.setText(SResources.getString("b.downloadAll"));
    launch.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        downloadAll();
      }
    });

    Button close = new Button(composite, SWT.NONE);
    close.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    close.setText(SResources.getString("b.close"));
    close.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        close();
      }
    });

    return composite;
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);

    Composite inputComposite = new Composite(composite, SWT.NONE);
    inputComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    inputComposite.setLayout(WidgetFactory.createGridLayout(3, 0, 0, 5, 5, false));

    Label l = new Label(inputComposite, SWT.NONE);
    l.setText(SResources.getString("rip.url"));
    l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

    urlText = new Text(inputComposite, SWT.SINGLE | SWT.BORDER);

    if (SWT.getPlatform().equals("win32") && PreferenceLoader.loadBoolean("dragAndDrop"))
      activateDropTarget(urlText);

    urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Button urlGet = new Button(inputComposite, SWT.NONE);
    urlGet.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    urlGet.setText(SResources.getString("rip.rip"));

    urlGroup = new Group(composite, SWT.NONE);
    urlGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    urlGroup.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));
    urlGroup.setText(SResources.getString("rip.waiting"));
    urlList = new List(urlGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

    createMenu();

    urlList.addListener(SWT.MouseDown, new Listener() {
      public void handleEvent(Event e) {
        if (e.button == 3) {
          Menu menu = popupMenu.createContextMenu(urlList);
          menu.setLocation(urlList.getDisplay().getCursorLocation());
          menu.setVisible(true);
        }
      }
    });

    urlList.setLayoutData(new GridData(GridData.FILL_BOTH));
    urlList.addMouseListener(new MouseAdapter() {
      public void mouseDoubleClick(MouseEvent e) {
        downloadSelected();
      }
    });

    urlGet.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        ripLinks();
      }
    });

    urlText.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.character == SWT.CR) {
          ripLinks();
        }
      }
    });
    return composite;
  }

  private void createMenu() {
    popupMenu = new MenuManager(SResources.S_ES);
    popupMenu.setRemoveAllWhenShown(true);
    popupMenu.addMenuListener(this);
  }

  public void downloadAll() {
    for (int i = 0; i < urlList.getItems().length; i++)
      SwissArmy.sendLink(Sancho.getCore(), urlList.getItems()[i]);
    mainWindow.getStatusline().setText(SResources.getString("sl.linksSent") + urlList.getItemCount());
  }

  public void downloadSelected() {
    if (urlList.getSelectionCount() > 0) {
      String[] selections = urlList.getSelection();
      for (int i = 0; i < selections.length; i++)
        SwissArmy.sendLink(Sancho.getCore(), selections[i]);
      mainWindow.getStatusline().setText(SResources.getString("sl.linksSent") + selections.length);
    }
  }

  protected String getRawPage(String urlString) {

    URL url;

    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      Sancho.pDebug("LinkRipper: " + e);
      return null;
    }
    try {
      BufferedReader b = new BufferedReader(new InputStreamReader(url.openStream()));

      StringBuffer sb = new StringBuffer();
      String string;
      while ((string = b.readLine()) != null)
        sb.append(string);

      b.close();

      return sb.toString();

    } catch (IOException e) {
      Sancho.pDebug("LinkRipper: " + e);
      return null;
    }
  }

  protected int getShellStyle() {
    return SWT.DIALOG_TRIM | SWT.RESIZE | (SWT.getPlatform().equals("fox") ? SWT.NONE : SWT.MODELESS);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    menuManager.add(new DownloadSelectedAction());
    menuManager.add(new DownloadAllAction());
    menuManager.add(new Separator());
    menuManager.add(new CopyAction());
    menuManager.add(new CopyAllAction());
    menuManager.add(new Separator());
    menuManager.add(new ToggleShowAllAction());
  }

  public void ripLinks() {
    if (ripping)
      return;
    ripping = true;

    urlList.removeAll();
    URLtoRip = urlText.getText();
    if (URLtoRip.equals(SResources.S_ES)) {
      ripping = false;
      return;
    }

    urlGroup.setText(SResources.getString("rip.ripping"));
    thread = new Thread(this);
    thread.start();
  }

  public void run() {

    boolean suprnova = false;

    if (!URLtoRip.toLowerCase().startsWith("http"))
      URLtoRip = "http://" + URLtoRip;

    if (endSlashRE.getMatch(URLtoRip) == null) {
      URLtoRip = URLtoRip + "/";
    }

    String page = getRawPage(URLtoRip);

    if (snRE.getMatch(URLtoRip) != null) {
      REMatch match = frameRE.getMatch(page);
      if (match != null) {
        String url = page.substring(match.getStartIndex(1), match.getEndIndex(1));
        url += "list_news.html";
        page = getRawPage(url);
        suprnova = true;
      }
    }

    if (page == null) {
      if (urlGroup == null || urlGroup.isDisposed())
        return;

      urlGroup.getDisplay().syncExec(new Runnable() { // must be sync
            public void run() {
              urlGroup.setText(SResources.getString("rip.error"));
            }
          });
      ripping = false;
      return;
    }

    final String[] urlArray = SwissArmy.parseLinks(page);

    if (urlGroup == null || urlGroup.isDisposed())
      return;
    urlGroup.getDisplay().syncExec(new Runnable() { // must be sync
          public void run() {
            urlGroup.setText("Found links(" + urlArray.length + "):");
            for (int i = 0; i < urlArray.length; i++)
              urlList.add(urlArray[i]);
          }
        });

    ripping = false;
  }

  public void setCurrentLinks(String[] stringArray) {
    urlList.removeAll();

    if (stringArray == null)
      return;

    String[] urlArray = SwissArmy.parseLinks(stringArray);
    urlGroup.setText(SResources.getString("rip.found") + "(" + urlArray.length + "):");
    for (int i = 0; i < urlArray.length; i++)
      urlList.add(urlArray[i]);
  }

  public void setFocus() {
    getShell().setFocus();
  }

  public void setInputURL(String string) {
    if (string == null)
      return;
    urlText.setText(string);
  }

  static {
    try {
      snRE = new RE("http://.+?suprnova.org");
      frameRE = new RE("src=\'(.+?)\'");
      endSlashRE = new RE("http://.+/");
    } catch (Exception e) {
    }
  }

  private class CopyAction extends Action {
    public CopyAction() {
      super(SResources.getString("mi.copy"));
      setImageDescriptor(SResources.getImageDescriptor("copy"));
    }

    public void run() {
      String string = SResources.S_ES;
      for (int i = 0; i < urlList.getSelection().length; i++) {
        string += urlList.getSelection()[i] + "\n";
      }
      if (!string.equals(SResources.S_ES))
        addToClipBoard(string);
    }
  }

  private class CopyAllAction extends Action {
    public CopyAllAction() {
      super(SResources.getString("mi.copyAll"));
      setImageDescriptor(SResources.getImageDescriptor("plus"));
    }

    public void run() {
      String string = SResources.S_ES;
      for (int i = 0; i < urlList.getItems().length; i++)
        string += urlList.getItems()[i] + "\n";
      if (!string.equals(SResources.S_ES))
        addToClipBoard(string);
    }
  }

  private class DownloadAllAction extends Action {
    public DownloadAllAction() {
      super(SResources.getString("mi.downloadAll"));
      setImageDescriptor(SResources.getImageDescriptor("down_arrow_green"));
    }

    public void run() {
      downloadAll();
    }
  }

  private class DownloadSelectedAction extends Action {
    public DownloadSelectedAction() {
      super(SResources.getString("mi.downloadSelected"));
      setImageDescriptor(SResources.getImageDescriptor("down_arrow_yellow"));
    }

    public void run() {
      downloadSelected();
    }
  }

  private static class ToggleShowAllAction extends Action {
    public ToggleShowAllAction() {
      super(SResources.getString("mi.showAll"), Action.AS_CHECK_BOX);
    }

    public boolean isChecked() {
      return PreferenceLoader.loadBoolean("linkRipperShowAll");
    }

    public void run() {
      PreferenceLoader.getPreferenceStore().setValue("linkRipperShowAll", !isChecked());
    }
  }

}
