/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Text;

import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.SashViewFrame;

public class InfoViewFrame extends SashViewFrame {
  Text myText;

  public InfoViewFrame(SashForm sashForm, String prefString, String prefImageString, AbstractTab aTab) {
    super(sashForm, prefString, prefImageString, aTab);

    myText = new Text(getChildComposite(), SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
    createViewListener(new InfoViewListener(this));
  }

  public Text getText() {
    return myText;
  }

}
