/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.actions.AddTabAction;
import sancho.view.viewFrame.actions.RemoveTabAction;
import sancho.view.viewFrame.actions.TabsOnTopAction;

public class TabbedViewFrame extends ViewFrame {
  protected Composite cTabChildComposite;
  protected CTabFolder cTabFolder;
  protected int defTabHeight = -1;
  protected CTabItem oldSelectionItem;
  protected MenuManager popupMenu;
  protected String tabPrefString;

  public TabbedViewFrame(Composite composite, String prefString, String prefImageString, AbstractTab aTab,
      String tabPrefString) {
    this(composite, prefString, prefImageString, aTab, tabPrefString, false);
  }

  public TabbedViewFrame(Composite composite, String prefString, String prefImageString, AbstractTab aTab,
      String tabPrefString, boolean forceFlat) {
    super(composite, prefString, prefImageString, aTab, forceFlat);
    this.tabPrefString = tabPrefString;

    createPopupMenu();
    cTabFolder = WidgetFactory.createCTabFolder(childComposite, PreferenceLoader.loadBoolean(tabPrefString
        + "TabsOnTop") ? SWT.TOP : SWT.BOTTOM);
    cTabFolder.setBorderVisible(false);

    int numTabs = PreferenceLoader.loadInt(tabPrefString + "Tabs");

    CTabItem cTabItem;
    for (int i = 0; i < numTabs; i++) {
      cTabItem = new CTabItem(cTabFolder, SWT.NONE);
      cTabItem.setText(PreferenceLoader.loadString(tabPrefString + "Tab_" + i + "_Name"));
      cTabItem.setData("filterString", PreferenceLoader.loadString(tabPrefString + "Tab_" + i + "_Filters"));
      onCTabDispose(cTabItem);
    }

    if (SWT.getPlatform().equals("fox"))
      defTabHeight = cTabFolder.getTabHeight();

    if (!PreferenceLoader.loadBoolean(tabPrefString + "ShowTab"))
      cTabFolder.setTabHeight(0);

    cTabChildComposite = new Composite(cTabFolder, SWT.NONE);
    cTabChildComposite.setLayout(new FillLayout());

    cTabFolder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        CTabItem cTabItem = (CTabItem) e.item;
        oldSelectionItem.setData("filterString", gView.filtersToString());
        oldSelectionItem.setControl(null);
        switchToTab(cTabItem, false);
      }
    });

    int event = SWT.getPlatform().equals("fox") ? SWT.MouseDown : SWT.MenuDetect;

    cTabFolder.addListener(event, new Listener() {
      public void handleEvent(Event e) {

        if (SWT.getPlatform().equals("fox") && e.button != 3)
          return;

        Point p = cTabFolder.getDisplay().getCursorLocation();
        p = cTabFolder.toControl(p);
        CTabItem item = cTabFolder.getItem(p);
        if (item == null)
          return;
        Menu m = popupMenu.createContextMenu(cTabFolder);
        m.setVisible(true);
      }
    });

    Listener listener = new Listener() {
      boolean drag = false;
      CTabItem dragItem;
      boolean exitDrag = false;

      public void handleEvent(Event e) {
        Point p = cTabFolder.toControl(cTabFolder.getDisplay().getCursorLocation()); //see bug 43251

        switch (e.type) {
          case SWT.DragDetect : {
            CTabItem item = cTabFolder.getItem(p);
            CTabItem selItem = cTabFolder.getSelection();
            if (item == null || item != selItem)
              return;
            drag = true;
            exitDrag = false;
            dragItem = item;
            break;
          }
          case SWT.MouseEnter :
            if (exitDrag) {
              exitDrag = false;
              drag = e.button != 0;
            }
            break;
          case SWT.MouseExit :
            if (drag) {
              cTabFolder.setInsertMark(null, false);
              exitDrag = true;
              drag = false;
            }
            break;
          case SWT.MouseUp : {
            if (!drag)
              return;
            cTabFolder.setInsertMark(null, false);
            CTabItem item = cTabFolder.getItem(p);
            if (item == dragItem)
              return;
            if (item != null) {
              int index = cTabFolder.indexOf(item);
              CTabItem newItem = new CTabItem(cTabFolder, SWT.NONE, index);
              newItem.setText(dragItem.getText());
              newItem.setData("filterString", dragItem.getData("filterString"));
              onCTabDispose(newItem);
              Control c = dragItem.getControl();
              dragItem.setControl(null);
              dragItem.dispose();
              switchToTab(newItem);
            }
            drag = false;
            exitDrag = false;
            dragItem = null;
            break;
          }
          case SWT.MouseMove : {
            if (!drag)
              return;
            CTabItem item = cTabFolder.getItem(p);

            if (item == null) {
              cTabFolder.setInsertMark(null, false);
              return;
            }

            cTabFolder.setInsertMark(item, false);
            break;
          }
        }
      }
    };
    cTabFolder.addListener(SWT.DragDetect, listener);
    cTabFolder.addListener(SWT.MouseUp, listener);
    cTabFolder.addListener(SWT.MouseMove, listener);
    cTabFolder.addListener(SWT.MouseExit, listener);
    cTabFolder.addListener(SWT.MouseEnter, listener);

  }

  public void createItem(String name) {
    CTabItem cTabItem;

    cTabItem = new CTabItem(cTabFolder, SWT.NONE);
    cTabItem.setText(name);
    cTabItem.setData("filterString", SResources.S_ES);
    cTabFolder.setSelection(cTabItem);
    onCTabDispose(cTabItem);
    switchToTab(cTabItem);
  }

  public void createPopupMenu() {
    popupMenu = new MenuManager();
    popupMenu.setRemoveAllWhenShown(true);
    popupMenu.addMenuListener(new TabMenuListener(this, tabPrefString));
  }

  public Composite getChildComposite() {
    return cTabChildComposite;
  }

  public CTabFolder getCTabFolder() {
    return cTabFolder;
  }

  public void onCTabDispose(CTabItem cTabItem) {
    cTabItem.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        CTabItem cTabItem = (CTabItem) e.widget;
        PreferenceStore p = PreferenceLoader.getPreferenceStore();
        p.setValue(tabPrefString + "Tabs", cTabFolder.getItemCount());
        int i = cTabFolder.indexOf(cTabItem);

        p.setValue(tabPrefString + "Tab_" + i + "_Name", cTabItem.getText());
        if (cTabFolder.getSelection() == cTabItem) {
          p.setValue(tabPrefString + "Tab_" + i + "_Filters", gView.filtersToString());
        } else
          p.setValue(tabPrefString + "Tab_" + i + "_Filters", (String) cTabItem.getData("filterString"));
      }
    });

  }

  public void switchToTab(CTabItem cTabItem) {
    switchToTab(cTabItem, true);
  }

  public void switchToTab(CTabItem cTabItem, boolean b) {
    if (b)
      cTabFolder.setSelection(cTabItem);

    gView.swapFilters((String) cTabItem.getData("filterString"));
    cTabItem.setControl(getChildComposite());
    oldSelectionItem = cTabItem;
  }

  public void toggleTabs() {
    cTabFolder.setTabHeight(cTabFolder.getTabHeight() == 0 ? defTabHeight : 0);
    PreferenceLoader.getPreferenceStore().setValue(tabPrefString + "ShowTab", cTabFolder.getTabHeight() != 0);
  }

  public void toggleTabPosition() {
    if ((cTabFolder.getStyle() & SWT.BOTTOM) != 0)
      cTabFolder.setTabPosition(SWT.TOP);
    else
      cTabFolder.setTabPosition(SWT.BOTTOM);
  }

  static class TabMenuListener implements IMenuListener {
    String tabPrefString;

    TabbedViewFrame viewFrame;

    public TabMenuListener(TabbedViewFrame viewFrame, String tabPrefString) {
      this.viewFrame = viewFrame;
      this.tabPrefString = tabPrefString;
    }

    public void menuAboutToShow(IMenuManager manager) {
      manager.add(new AddTabAction(viewFrame));
      if (viewFrame.getCTabFolder().getItemCount() > 1)
        manager.add(new RemoveTabAction(viewFrame));
      manager.add(new Separator());
      manager.add(new TabsOnTopAction(tabPrefString, viewFrame));

    }
  }
}