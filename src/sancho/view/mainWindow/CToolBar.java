/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.mainWindow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;

import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.ToolButton;
import sancho.view.utility.WidgetFactory;

public class CToolBar implements DisposeListener {
  private Composite composite;
  private CoolBar coolbar;
  private boolean coolbarLocked;
  private List mainToolButtons;
  private ToolBar mainTools;
  private MainWindow mainWindow;
  private boolean toolbarSmallButtons;

  public CToolBar(MainWindow mainWindow, boolean size) {
    this.toolbarSmallButtons = size;
    this.coolbarLocked = true;
    this.mainWindow = mainWindow;
    this.mainToolButtons = new ArrayList();
    this.createContent(mainWindow.getMainComposite());
  }

  private void createContent(Composite parent) {
    composite = new Composite(parent, SWT.NONE);
    GridLayout gridLayout = WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false);
    composite.setLayout(gridLayout);
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    createCoolBar();
    createToolBars();
    createCoolItems();
  }

  private void createCoolBar() {
    coolbar = new CoolBar(this.composite, SWT.FLAT);
    coolbar.addDisposeListener(this);
    coolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  public void createCoolItems() {
    for (int i = 0; i < coolbar.getItems().length; i++)
      coolbar.getItems()[i].dispose();

    for (int i = 0; i < 1; i++)
      new CoolItem(coolbar, SWT.NONE);

    CoolItem[] items = coolbar.getItems();
    CoolItem mainCoolItem = items[0];
    mainCoolItem.setControl(mainTools);
  }

  public MenuItem createMenuItem(Menu menu, int style, boolean checked, String resString,
      SelectionAdapter selectionAdapter) {
    MenuItem menuItem = new MenuItem(menu, style);
    menuItem.setText(SResources.getString(resString));
    if (checked)
      menuItem.setSelection(checked);
    menuItem.addSelectionListener(selectionAdapter);
    return menuItem;
  }

  private Menu createToolBarRMMenu() {
    Menu menu = new Menu(mainWindow.getShell(), SWT.POP_UP);

    createMenuItem(menu, SWT.CHECK, toolbarSmallButtons, "mi.cb.small", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        MenuItem menuItem = (MenuItem) e.widget;
        toggleSmallButtons();
      }
    });

    createMenuItem(menu, SWT.NONE, false, "mi.cb.tabSelector", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        mainWindow.configureTabs();
      }
    });

    return menu;
  }

  public void createToolBars() {
    if ((mainTools != null) && !mainTools.isDisposed()) {
      mainTools.getMenu().dispose();
      mainTools.dispose();
    }

    mainTools = new ToolBar(coolbar, (toolbarSmallButtons ? SWT.RIGHT : SWT.NONE) | SWT.FLAT);
    mainTools.setMenu(createToolBarRMMenu());
  }

  public List getMainToolButtons() {
    return mainToolButtons;
  }

  public ToolBar getToolBar() {
    return mainTools;
  }

  public boolean isToolbarSmallButtons() {
    return toolbarSmallButtons;
  }

  public void layoutCoolBar() {
    for (int j = 0; j < coolbar.getItemCount(); j++) {
      CoolItem tempCoolItem = coolbar.getItem(j);
      ToolBar tempToolBar = (ToolBar) tempCoolItem.getControl();
      Point point = tempToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
      point = tempCoolItem.computeSize(point.x, point.y);
      tempCoolItem.setSize(point);
      tempCoolItem.setMinimumSize(point);
    }

    coolbar.getParent().layout();
    coolbar.setLocked(coolbarLocked);
  }

  public void reset() {
    mainToolButtons.clear();
    createToolBars();
    createCoolItems();
  }

  public void savePreferences() {
    PreferenceStore p = PreferenceLoader.getPreferenceStore();
    p.setValue("toolbarSmallButtons", isToolbarSmallButtons());
  }

  public void setToolbarSmallButtons(boolean b) {
    toolbarSmallButtons = b;
  }

  private void toggleSmallButtons() {
    toolbarSmallButtons = !toolbarSmallButtons;
    coolbar.dispose();

    createCoolBar();
    createToolBars();
    createCoolItems();

    for (Iterator i = mainToolButtons.iterator(); i.hasNext();) {
      ToolButton toolButton = (ToolButton) i.next();
      toolButton.useSmallButtons(toolbarSmallButtons);
      toolButton.resetItem(mainTools);
    }
    layoutCoolBar();
    composite.getParent().layout();
  }

  public void widgetDisposed(DisposeEvent e) {
    savePreferences();
  }

}