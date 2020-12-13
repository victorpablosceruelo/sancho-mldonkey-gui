/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import sancho.view.shares.UploadViewFrame;
import sancho.view.utility.AbstractTab;

import org.eclipse.swt.widgets.Composite;

public class SharesTab extends AbstractTab {

  public SharesTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
  }

  protected void createContents(Composite parent) {
    addViewFrame(new UploadViewFrame(parent, "l.uploads", "up_arrow_blue", this));
  }
}
