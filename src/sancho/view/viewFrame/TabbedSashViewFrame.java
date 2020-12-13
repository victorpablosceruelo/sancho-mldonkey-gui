/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.AbstractTab;
import sancho.view.utility.MaximizeSashMouseAdapter;

public class TabbedSashViewFrame extends TabbedViewFrame  {

  protected SashForm parentSashForm;

  public TabbedSashViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab, String tabPrefString) {
    this(parentSashForm, prefString, prefImageString, aTab, tabPrefString, false);
  }

  public TabbedSashViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab, String tabPrefString, boolean forceFlat) {
    super(parentSashForm, prefString, prefImageString, aTab, tabPrefString, forceFlat);
    this.parentSashForm = parentSashForm;
  }

  public void createViewListener(TabbedSashViewListener sashViewFrameListener) {
    setupViewListener(sashViewFrameListener);
    cLabel.addMouseListener(new MaximizeSashMouseAdapter(cLabel, menuManager, getParentSashForm(),
        getControl()));
  }

  public SashForm getParentSashForm() {
    return parentSashForm;
  }

}

