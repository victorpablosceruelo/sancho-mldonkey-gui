/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.utility.SResources;
import sancho.view.viewer.GView;

public class RefreshUploadsAction extends Action {
  private GView gView;

  public RefreshUploadsAction(GView gView) {
    super(SResources.getString("mi.refresh"));
    this.gView = gView;
  }

  public void run() {
    Sancho.send(OpCodes.S_REFRESH_UPLOAD_STATS);
  }
}