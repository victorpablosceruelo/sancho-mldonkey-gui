/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.tableTree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.CustomTableTreeViewer;
import sancho.view.viewer.GView;
import sancho.view.viewer.table.GTableContentProvider;
import sancho.view.viewer.table.GTableMenuListener;

public abstract class GTableTreeView extends GView {
  protected GTableTreeContentProvider tableTreeContentProvider;
  protected GTableMenuListener tableTreeMenuListener;

  public GTableTreeView(ViewFrame viewFrame) {
    this.viewFrame = viewFrame;
  }

  protected void createContents(Composite parent) {
    sViewer = new CustomTableTreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);
    super.createContents();
  }

  public Table getTable() {
    return ((CustomTableTreeViewer) sViewer).getTableTree().getTable();
  }

  public GTableContentProvider getTableContentProvider() {
    return tableTreeContentProvider;
  }

  public GTableMenuListener getTableMenuListener() {
    return tableTreeMenuListener;
  }

  protected CustomTableTreeViewer getTableTreeViewer() {
    return (CustomTableTreeViewer) sViewer;
  }
}