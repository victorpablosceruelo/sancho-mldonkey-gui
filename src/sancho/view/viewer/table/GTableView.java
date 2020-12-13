/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.CustomTableViewer;
import sancho.view.viewer.GView;

public abstract class GTableView extends GView {
  protected GTableContentProvider tableContentProvider;
  protected GTableMenuListener tableMenuListener;

  public GTableView(ViewFrame viewFrame) {
    this.viewFrame = viewFrame;
  }

  protected void createContents(Composite parent) {
    sViewer = new CustomTableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);
    super.createContents();
  }

  public Table getTable() {
    return ((CustomTableViewer) sViewer).getTable();
  }

  public GTableContentProvider getTableContentProvider() {
    return tableContentProvider;
  }

  public GTableMenuListener getTableMenuListener() {
    return tableMenuListener;
  }

  protected CustomTableViewer getTableViewer() {
    return (CustomTableViewer) sViewer;
  }
}