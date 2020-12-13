/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.swt.custom.CTabFolder;

public abstract class CTabFolderViewListener extends SashViewListener {
  protected CTabFolder cTabFolder;

  public CTabFolderViewListener(CTabFolderViewFrame cTabFolderViewFrame) {
    super(cTabFolderViewFrame);
    this.cTabFolder = cTabFolderViewFrame.getCTabFolder();
  }
}
