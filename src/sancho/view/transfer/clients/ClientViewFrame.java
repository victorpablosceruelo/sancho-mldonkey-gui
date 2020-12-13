/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.clients;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.ToolItem;

import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.TabbedSashViewFrame;
import sancho.view.viewer.GView;

public class ClientViewFrame extends TabbedSashViewFrame {
  public ClientViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab, final GView downloadGView) {
    super(parentSashForm, prefString, prefImageString, aTab, "clients");

    gView = new ClientTableView(this);
    createViewListener(new ClientViewListener(this));
    createViewToolBar();

    switchToTab(cTabFolder.getItems()[0]);
  }

  public void createViewToolBar() {
    super.createViewToolBar();
    new ToolItem(toolBar, SWT.SEPARATOR);
    addRefine();
  }

}