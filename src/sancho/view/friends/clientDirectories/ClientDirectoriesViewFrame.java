/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientDirectories;

import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.SashViewFrame;

public class ClientDirectoriesViewFrame extends SashViewFrame {

  public ClientDirectoriesViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab);
    gView = gView = new ClientDirectoriesTableView(this);
    createViewListener(new ClientDirectoriesViewListener(this));
  }

}
