/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import org.eclipse.swt.widgets.Composite;

import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.ViewFrame;

public class DownloadCompleteViewFrame extends ViewFrame {
  public DownloadCompleteViewFrame(Composite composite, String prefString, String prefImageString,
      AbstractTab aTab) {
    super(composite, prefString, prefImageString, aTab);

    gView = new DownloadCompleteTableView(this);
    createViewListener(new DownloadCompleteViewListener(this));
    createViewToolBar();
  }

  public void createViewToolBar() {
    super.createViewToolBar();

    addRefine();
  }

}