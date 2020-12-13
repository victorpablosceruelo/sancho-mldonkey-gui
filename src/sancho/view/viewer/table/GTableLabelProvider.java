/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.table;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import sancho.view.viewer.GView;
import sancho.view.viewer.ICustomViewer;

public abstract class GTableLabelProvider implements ITableLabelProvider {
  protected ICustomViewer cViewer;
  protected GView gView;

  public GTableLabelProvider(GView gView) {
    this.gView = gView;
    updateDisplay();
  }

  public void addListener(ILabelProviderListener listener) {
  }

  public void dispose() {
  }

  public Image getColumnImage(Object element, int columnIndex) {
    return null;
  }

  public abstract String getColumnText(Object element, int columnIndex);

  public void initialize() {
    cViewer = (ICustomViewer) gView.getViewer();
  }

  public boolean isLabelProperty(Object element, String property) {
    return true;
  }

  public void removeListener(ILabelProviderListener listener) {
  }

  public void updateDisplay() {
  }
}
