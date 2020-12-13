/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms;

import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.SashViewFrame;

public class RoomsViewFrame extends SashViewFrame {
  public RoomsViewFrame(SashForm parentSashForm, String prefString, String prefImageString, AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab);

    gView = new RoomsTableView(this);
    createViewListener(new RoomsViewListener(this));
  }
}
