/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.tableTree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sancho.view.viewer.CustomTableTreeViewer;
import sancho.view.viewer.GView;
import sancho.view.viewer.table.GTableContentProvider;

public abstract class GTableTreeContentProvider extends GTableContentProvider implements ITreeContentProvider {
  protected CustomTableTreeViewer tableTreeViewer;

  public GTableTreeContentProvider(GView gView) {
    super(gView);
  }

  public Object[] getChildren(Object parent) {
    return EMPTY_ARRAY;
  }

  public Object getParent(Object child) {
    return null;
  }

  public boolean hasChildren(Object parent) {
    return false;
  }

  public void initialize() {
    tableTreeViewer = ((GTableTreeView) gView).getTableTreeViewer();
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    if (viewer instanceof CustomTableTreeViewer) {
      this.tableTreeViewer = (CustomTableTreeViewer) viewer;
    }
  }
}
