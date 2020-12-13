/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.setupWizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import sancho.core.Sancho;
import sancho.view.utility.SResources;

public class SetupWizardDialog extends WizardDialog {

  int hm_num;

  public SetupWizardDialog(Shell parentShell, IWizard newWizard) {
    super(parentShell, newWizard);
  }

  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    if (!Sancho.hasLoaded()) {
      Button finish = getButton(IDialogConstants.FINISH_ID);
      finish.setText(SResources.getString("b.connect"));

      Button cancel = getButton(IDialogConstants.CANCEL_ID);
      cancel.setText(SResources.getString("b.quit"));
    }
  }

  public void setNum(int i) {
    hm_num = i;
  }

  public int getNum() {
    return hm_num;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(SResources.getImage("ProgramIcon"));
  }
}
