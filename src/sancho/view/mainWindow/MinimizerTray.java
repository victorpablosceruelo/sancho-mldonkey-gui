/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.mainWindow;

import java.util.Observable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import sancho.core.Sancho;
import sancho.model.mldonkey.ClientStats;
import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.statusline.actions.DNDBoxAction;
import sancho.view.statusline.actions.PreferencesAction;
import sancho.view.statusline.actions.RateBandwidthDialogAction;
import sancho.view.utility.SResources;

public class MinimizerTray extends Minimizer implements DisposeListener, IMenuListener {

  protected static final String S_DL = "DL: ";
  protected static final String S_UL = ", UL: ";

  private TrayItem trayItem;
  private MenuManager popupMenu;
  private boolean closeMe = false;
  private MainWindow mainWindow;

  public MinimizerTray(MainWindow mainWindow, String titleBarText) {
    super(mainWindow, titleBarText);
    this.mainWindow = mainWindow;

    createTrayIcon();
    createMenu();
    shell.addDisposeListener(this);
    setConnected(true);
  }

  public void setConnected(boolean b) {
    if (b && Sancho.hasCollectionFactory())
      Sancho.getCore().getClientStats().addObserver(this);
  }

  private void createTrayIcon() {
    trayItem = new TrayItem(mainWindow.getShell().getDisplay().getSystemTray(), SWT.NONE);

    if (trayItem == null) {
      MessageBox b = new MessageBox(new Shell(), SWT.OK);
      b.setMessage("trayItem is null");
      b.open();
      return;
    }

    String s = SWT.getPlatform().equals("win32") ? "tray-16" : "tray-22";
    trayItem.setImage(SResources.getImage(s));
    trayItem.setToolTipText(titleBarText);

    trayItem.addListener(SWT.MenuDetect, new Listener() {
      public void handleEvent(Event e) {
        Menu menu = popupMenu.createContextMenu(shell);
        menu.setVisible(true);
      }
    });

    trayItem.addSelectionListener(new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e) {
        if (shell.isVisible()) {
          hide();
        } else {
          restore();

          if (shell.getMinimized())
            shell.setMinimized(false);
        }
      }
    });
  }

  private void createMenu() {
    popupMenu = new MenuManager(SResources.S_ES);
    popupMenu.setRemoveAllWhenShown(true);
    popupMenu.addMenuListener(this);
  }

  public boolean close() {
    if (closeMe || !PreferenceLoader.loadBoolean("minimizeOnClose")) {
      return true;
    } else {
      hide();
      return false;
    }
  }

  public void hide() {
    shell.setMinimized(true);
    shell.setVisible(false);

    if (Sancho.getCoreConsole() != null)
      Sancho.getCoreConsole().getShell().setVisible(false);
  }

  public void forceClose() {
    closeMe = true;
  }

  public boolean minimize() {
    if (PreferenceLoader.loadBoolean("systrayOnMinimize")) {
      hide();
      return false;
    } else {
      return true;
    }
  }

  public void restore() {
    shell.setVisible(true);
    shell.forceActive();
    setTitleBarText();
    trayItem.setToolTipText(titleBarText);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    menuManager.add(new DNDBoxAction(mainWindow));
    menuManager.add(new Separator());
    menuManager.add(new PreferencesAction(mainWindow));
    menuManager.add(new RateBandwidthDialogAction(shell));
    menuManager.add(new Separator());
    menuManager.add(new HideRestoreAction());
    menuManager.add(new CloseAction());
  }

  public void update(Observable o, Object obj) {
    final ClientStats clientStats = (ClientStats) o;

    if (clientStats == null || shell == null || shell.isDisposed())
      return;

    shell.getDisplay().asyncExec(new Runnable() {
      public void run() {
        if (shell == null || shell.isDisposed() || trayItem == null || trayItem.isDisposed())
          return;

        if (shell.isVisible() && shell.getMinimized())
          setTitleBar(clientStats);

        setTrayToolTip(clientStats);
      }
    });
  }

  public void setTrayToolTip(ClientStats clientStats) {
    stringBuffer.setLength(0);
    stringBuffer.append(titleBarText);
    stringBuffer.append(SResources.S_NL);
    stringBuffer.append(S_DL);
    stringBuffer.append(clientStats.getTcpDownRateString());
    stringBuffer.append(S_UL);
    stringBuffer.append(clientStats.getTcpUpRateString());
    trayItem.setToolTipText(stringBuffer.toString());
  }

  public void widgetDisposed(DisposeEvent e) {
    if (Sancho.hasCollectionFactory())
      Sancho.getCore().getClientStats().deleteObserver(this);
  }

  private class HideRestoreAction extends Action {
    public HideRestoreAction() {
      super(SResources.getString(shell.isVisible() ? "mi.hide" : "mi.restore"));
      setImageDescriptor(SResources.getImageDescriptor(shell.isVisible() ? "minus" : "plus"));
    }

    public void run() {
      if (shell.isVisible())
        hide();
      else {
        restore();
        if (shell.getMinimized())
          shell.setMinimized(false);
      }
    }
  }

  private class CloseAction extends Action {
    public CloseAction() {
      super(SResources.getString("mi.close"));
      setImageDescriptor(SResources.getImageDescriptor("x"));
    }

    public void run() {
      closeMe = true;
      shell.close();
    }
  }
}