/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import sancho.core.Sancho;
import sancho.model.mldonkey.ClientStats;
import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.statusline.actions.DNDBoxAction;
import sancho.view.statusline.actions.PreferencesAction;
import sancho.view.statusline.actions.RateBandwidthDialogAction;

public class DNDBox implements Observer, PaintListener {

  static int JUMP_MARGIN = 10;
  MainWindow mainWindow;
  Point mouseDownPoint;
  boolean multiMonitors;
  MenuManager popupMenu;
  Rectangle screenBounds;
  Shell shell;
  Rectangle shellBounds;
  Point shellLocation;
  Shell dummyShell;

  StringBuffer upString = new StringBuffer();
  StringBuffer downString = new StringBuffer();

  int cWidth;
  int cHeight;

  Font textFont;
  Color bColor;
  Color fColor;

  int shellWidth;
  int shellHeight;

  public DNDBox(MainWindow mainWindow) {
    this.mainWindow = mainWindow;

    dummyShell = new Shell();
    dummyShell.setVisible(false);

    this.shell = new Shell(dummyShell, SWT.ON_TOP | SWT.NO_TRIM | SWT.NO_BACKGROUND);
    this.shell.setLayout(new FillLayout());

    bColor = PreferenceLoader.loadColor("dndBackgroundColor");
    fColor = PreferenceLoader.loadColor("dndForegroundColor");
    textFont = PreferenceLoader.loadFont("dndFontData");

    GC gc = new GC(shell);
    gc.setFont(textFont);

    cWidth = gc.getFontMetrics().getAverageCharWidth();
    cHeight = gc.getFontMetrics().getHeight();

    gc.dispose();

    int numChars = PreferenceLoader.loadInt("dndWidth");

    shellHeight = cHeight * 2 + 6;
    shellWidth = numChars * cWidth;

    shell.setBounds(0, 0, shellWidth, shellHeight);

    this.shell.addPaintListener(this);
    this.shellBounds = shell.getBounds();

    popupMenu = new MenuManager();
    popupMenu.addMenuListener(new DNDBoxMenuListener());
    popupMenu.setRemoveAllWhenShown(true);

    this.shell.setMenu(popupMenu.createContextMenu(shell));
    this.screenBounds = this.shell.getDisplay().getBounds();

    Rectangle saved = PreferenceLoader.loadRectangle("dndBoxWindowBounds");
    if (saved.x == -1)
      this.shell.setLocation(screenBounds.width - shellBounds.width, screenBounds.height - shellBounds.height
          - 40);
    else
      this.shell.setLocation(saved.x, saved.y);

    this.shell.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PreferenceConverter.setValue(PreferenceLoader.getPreferenceStore(), "dndBoxWindowBounds", shell
            .getBounds());

        if (Sancho.hasCollectionFactory())
          Sancho.getCore().getClientStats().deleteObserver(DNDBox.this);

      }
    });

    this.shell.open();

    WidgetFactory.createLinkDropTarget(this.shell);

    // fox?
    if (this.shell.getDisplay().getMonitors().length > 1)
      multiMonitors = true;

    Listener shellListener = new Listener() {
      public void handleEvent(Event e) {
        switch (e.type) {
          case SWT.MouseDown :
            onMouseDown(e);
            break;
          case SWT.MouseUp :
            onMouseUp(e);
            break;
          case SWT.MouseMove :
            onMouseMove(e);
            break;
        }
      }
    };
    int[] shellEvents = new int[]{SWT.MouseDown, SWT.MouseUp, SWT.MouseMove};

    for (int i = 0; i < shellEvents.length; i++)
      this.shell.addListener(shellEvents[i], shellListener);

    setConnected(true);
  }

  public void close() {
    if (this.shell != null && !this.shell.isDisposed())
      this.shell.close();
    if (this.dummyShell != null && !this.dummyShell.isDisposed())
      this.dummyShell.close();
    if (this.shell != null && !this.shell.isDisposed())
      this.shell.dispose();
  }

  public void onMouseDown(Event e) {
    mouseDownPoint = new Point(e.x, e.y);
  }

  public void setConnected(boolean b) {
    if (Sancho.hasCollectionFactory()) {
      Sancho.getCore().getClientStats().addObserver(this);
    }
  }

  public void onMouseMove(Event e) {
    if (mouseDownPoint != null) {
      this.shellLocation = shell.getLocation();

      int x = shellLocation.x - (mouseDownPoint.x - e.x);
      int y = shellLocation.y - (mouseDownPoint.y - e.y);

      if (!multiMonitors) {
        x = x < JUMP_MARGIN ? 0 : x;
        y = y < JUMP_MARGIN ? 0 : y;

        if (x > screenBounds.width - (shellBounds.width + JUMP_MARGIN))
          x = screenBounds.width - shellBounds.width;
        if (y > screenBounds.height - (shellBounds.height + JUMP_MARGIN))
          y = screenBounds.height - shellBounds.height;
      }

      this.shell.setLocation(x, y);
    }
  }

  public void onMouseUp(Event e) {
    mouseDownPoint = null;
  }

  public void redrawImage(ClientStats clientStats) {
    if (shell == null || clientStats == null || shell.isDisposed())
      return;

    upString.setLength(0);
    downString.setLength(0);

    upString.append("U:" + clientStats.getTcpUpRateString());
    downString.append("D:" + clientStats.getTcpDownRateString());

    this.shell.redraw();
  }

  public void paintControl(PaintEvent e) {

    int x = shell.getBounds().x;
    int y = shell.getBounds().y;

    e.gc.setBackground(bColor);
    e.gc.fillRectangle(0, 0, shellWidth - 1, shellHeight - 1);

    e.gc.setForeground(fColor);
    e.gc.setFont(textFont);

    e.gc.drawText(upString.toString(), 3, 2, true);
    e.gc.drawText(downString.toString(), 3, cHeight + 4, true);

    e.gc.drawRectangle(0, 0, shellWidth - 1, shellHeight - 1);

    // e.gc.drawImage(image, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
  }

  public void update(final Observable o, Object obj) {
    if (o instanceof ClientStats) {
      if (this.shell != null && !this.shell.isDisposed()) {
        this.shell.getDisplay().asyncExec(new Runnable() {
          public void run() {
            redrawImage((ClientStats) o);
          }
        });
      }
    }
  }

  class DNDBoxMenuListener implements IMenuListener {
    public void menuAboutToShow(IMenuManager menuManager) {
      if (Sancho.monitorMode) {
        menuManager.add(new ExitAction(mainWindow.getShell()));
      } else {
        menuManager.add(new HideRestoreAction(mainWindow.getShell()));
        if (mainWindow.getShell().isVisible())
          menuManager.add(new DNDBoxAction(mainWindow));
        menuManager.add(new Separator());
        menuManager.add(new PreferencesAction(mainWindow));
        menuManager.add(new RateBandwidthDialogAction(shell));
      }
    }
  }

  static class ExitAction extends Action {

    Shell shell;

    public ExitAction(Shell shell) {
      super(SResources.getString("menu.file.exit"));
      this.shell = shell;
    }

    public void run() {
      shell.close();
    }
  }

  static class HideRestoreAction extends Action {

    Shell shell;

    public HideRestoreAction(Shell shell) {
      super(SResources.getString(shell.isVisible() ? "mi.hide" : "mi.restore"));
      setImageDescriptor(SResources.getImageDescriptor(shell.isVisible() ? "minus" : "plus"));
      this.shell = shell;
    }

    public void run() {
      shell.setVisible(!shell.isVisible());
    }
  }
}