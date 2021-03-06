/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends;

import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.SashViewFrame;

public class FriendsViewFrame extends SashViewFrame {
  public FriendsViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab);

    gView = new FriendsTableView(this);
    createViewListener(new FriendsViewListener(this));
  }
}
