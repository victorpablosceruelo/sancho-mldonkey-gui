/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.table;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sancho.view.viewer.CustomTableViewer;
import sancho.view.viewer.GView;

public abstract class GTableContentProvider implements IStructuredContentProvider, Observer {
  protected final static Object[] EMPTY_ARRAY = new Object[0];
  protected GView gView;
  protected boolean needsRefresh;
  protected CustomTableViewer tableViewer;

  public GTableContentProvider(GView gView) {
    this.gView = gView;
  }

  public void dispose() {
  }

  public Object[] getElements(Object inputElement) {
    return EMPTY_ARRAY;
  }

  public void initialize() {
    tableViewer = ((GTableView) gView).getTableViewer();
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    if (viewer instanceof CustomTableViewer)
      this.tableViewer = (CustomTableViewer) viewer;
  }

  public void setActive(boolean b) {
    if (b && needsRefresh) {
      gView.refresh();
      needsRefresh = false;
    }
  }

  public void setVisible(boolean b) {
    if (b && needsRefresh) {
      gView.refresh();
      needsRefresh = false;
    }
  }

  public void update(Observable o, Object obj) {
  }

  public void updateDisplay() {
  }
}