/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableColumn;

import sancho.view.viewer.GView;

public class SetDynamicColumnAction extends Action {
  GView gView;
  int column;

  public SetDynamicColumnAction(GView gView, int column) {
    super(((TableColumn) gView.getTable().getColumn(column)).getText(), Action.AS_CHECK_BOX);
    this.gView = gView;
    this.column = column;
  }

  public void run() {
    gView.setDynamicColumn(column);
  }

  public boolean isChecked() {
    return (gView.getDynamicColumn() == column);
  }
}