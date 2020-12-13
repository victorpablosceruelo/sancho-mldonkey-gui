/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;

import sancho.view.utility.SResources;
import sancho.view.viewFrame.TabbedViewFrame;

public class AddTabAction extends Action {

  TabbedViewFrame viewFrame;

  public AddTabAction(TabbedViewFrame viewFrame) {
    super(SResources.getString("mi.d.addTab"));
    setImageDescriptor(SResources.getImageDescriptor("plus"));
    this.viewFrame = viewFrame;
  }

  public void run() {
    InputDialog dialog = new InputDialog(viewFrame.getCTabFolder().getShell(), SResources
        .getString("ti.d.tabName"), SResources.getString("ti.d.tabName"), SResources.getString("My new tab!"),
        null);

    if (dialog.open() == InputDialog.CANCEL)
      return;

    String result = dialog.getValue();
    viewFrame.createItem(result);
  }

}