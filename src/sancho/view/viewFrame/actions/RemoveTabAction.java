/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import sancho.view.utility.SResources;
import sancho.view.viewFrame.TabbedViewFrame;

public class RemoveTabAction extends Action {

  TabbedViewFrame viewFrame;

  public RemoveTabAction(TabbedViewFrame viewFrame) {
    super(SResources.getString("mi.d.removeTab"));
    setImageDescriptor(SResources.getImageDescriptor("minus"));
    this.viewFrame = viewFrame;
  }

  public void run() {
    CTabFolder f = viewFrame.getCTabFolder();
    if (f.getItemCount() > 1) {

      CTabItem c = f.getSelection();
      int index = f.indexOf(c) - 1;
      if (c != null && !c.isDisposed())
        c.dispose();

      if (index < 0)
        index = 0;

      c = f.getItems()[index];

      viewFrame.switchToTab(c);
    }
  }

}