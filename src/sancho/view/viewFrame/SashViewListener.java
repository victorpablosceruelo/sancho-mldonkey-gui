/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.custom.SashForm;

import sancho.view.viewer.actions.FlipSashAction;
import sancho.view.viewer.actions.MaximizeAction;

public abstract class SashViewListener extends ViewListener {
  protected SashForm sashForm;

  public SashViewListener(SashViewFrame sashViewFrame) {
    super(sashViewFrame);
    this.sashForm = sashViewFrame.getParentSashForm();
  }

  public void createSashActions(IMenuManager menuManager, String string) {
    menuManager.add(new Separator());
    menuManager.add(new FlipSashAction(this.sashForm));
    menuManager.add(new MaximizeAction(this.sashForm, this.control, string));
  }
}

