/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.SResources;

public class FlipSashAction extends AbstractSashAction {
  public FlipSashAction(SashForm sashForm) {
    super(sashForm);
    setText(SResources.getString("mi.flipSash"));
    setImageDescriptor(SResources.getImageDescriptor("rotate"));
  }

  public void run() {
    sashForm.setOrientation(sashForm.getOrientation() == SWT.HORIZONTAL ? SWT.VERTICAL : SWT.HORIZONTAL);
  }

}