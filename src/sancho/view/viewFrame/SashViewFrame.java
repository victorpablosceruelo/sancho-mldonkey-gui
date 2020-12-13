/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.AbstractTab;
import sancho.view.utility.MaximizeSashMouseAdapter;

public class SashViewFrame extends ViewFrame  {
  protected SashForm parentSashForm;

  public SashViewFrame(SashForm parentSashForm, String prefString, String prefImageString, AbstractTab aTab) {
    this(parentSashForm, prefString, prefImageString, aTab, false);
  }

  public SashViewFrame(SashForm parentSashForm, String prefString, String prefImageString, AbstractTab aTab,
      boolean forceFlat) {
    super(parentSashForm, prefString, prefImageString, aTab, forceFlat);
    this.parentSashForm = parentSashForm;
  }

  public void createViewListener(SashViewListener sashViewFrameListener) {
    setupViewListener(sashViewFrameListener);
    cLabel.addMouseListener(new MaximizeSashMouseAdapter(cLabel, menuManager, getParentSashForm(),
        getControl()));
  }

  public SashForm getParentSashForm() {
    return parentSashForm;
  }

}