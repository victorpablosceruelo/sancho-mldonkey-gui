/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Table;

import sancho.model.mldonkey.Client;
import sancho.view.utility.SResources;
import sancho.view.viewer.CustomTableViewer;
import sancho.view.viewer.GView;
import sancho.view.viewer.ICustomViewer;
import sancho.view.viewer.actions.WebServicesAction;

public abstract class GTableMenuListener implements IMenuListener, ISelectionChangedListener {
  protected GView gView;
  protected List<Object> selectedObjects = Collections.synchronizedList(new ArrayList());
  protected CustomTableViewer tableViewer;

  public GTableMenuListener(GView gView) {
    this.gView = gView;
  }

  protected void addWebServicesMenu(IMenuManager menuManager, String md4, String ed2k, long fileSize) {
    MenuManager webServicesMenu = new MenuManager(SResources.getString("mi.webServices"));

    webServicesMenu.add(new WebServicesAction(WebServicesAction.BITZI, md4));
    webServicesMenu.add(new WebServicesAction(WebServicesAction.FILEDONKEY, md4));
    // webServicesMenu.add(new WebServicesAction(WebServicesAction.JIGLE,
    // fileSize + ":" + md4));
    // webServicesMenu.add(new
    // WebServicesAction(WebServicesAction.SHAREREACTOR, ed2k));
    webServicesMenu.add(new WebServicesAction(WebServicesAction.DONKEY_FAKES, ed2k));

    menuManager.add(webServicesMenu);
  }

  protected void collectSelections(SelectionChangedEvent event, Class clazz) {
    IStructuredSelection sSel = (IStructuredSelection) event.getSelection();
    selectedObjects.clear();
    Object object;
    for (Iterator i = sSel.iterator(); i.hasNext();) {
      object = i.next();
      if (clazz.isInstance(object))
        selectedObjects.add(object);
    }
  }

  public void deselectAll() {
    Table table = gView.getTable();
    if (table != null && !table.isDisposed())
      table.deselectAll();
    selectedObjects.clear();
  }

  public void initialize() {
    if (gView instanceof GTableView)
      tableViewer = ((GTableView) gView).getTableViewer();

    gView.getTable().addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.stateMask == SWT.CTRL && e.character == 0x01)
          gView.getTable().selectAll();
            ICustomViewer v = (ICustomViewer) gView.getViewer();
            v.updateSelection(gView.getViewer().getSelection());
      }
    });
  }

  public abstract void menuAboutToShow(IMenuManager menuManager);

  public abstract void selectionChanged(SelectionChangedEvent event);
}