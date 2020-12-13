/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientFiles;

import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.SashViewFrame;

public class ClientFilesViewFrame extends SashViewFrame {

  public ClientFilesViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab);
    gView = gView = new ClientFilesTableView(this);
    createViewListener(new ClientFilesViewListener(this));
    createViewToolBar();
  }

  public void createViewToolBar() {
    super.createViewToolBar();
    addRefine();
  }
}
