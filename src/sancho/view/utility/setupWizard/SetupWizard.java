/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.setupWizard;

import org.eclipse.jface.wizard.Wizard;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;

public class SetupWizard extends Wizard {
  private HostPage hostPage;
  private CoreBinaryPage coreBinaryPage;

  public SetupWizard() {
    super();
    setWindowTitle(SResources.getString("hm.setupTitle"));
    hostPage = new SSHHostPage();
    coreBinaryPage = new CoreBinaryPage();
  }

  public boolean performFinish() {
    hostPage.saveData();
    coreBinaryPage.saveData();
    ((SetupWizardDialog) getContainer()).setNum(hostPage.getNum());
    return true;
  }

  public void addPages() {
    if (!PreferenceLoader.loadBoolean("initialized")) {
      addPage(new WelcomePage());
      addPage(coreBinaryPage);
    }
    addPage(hostPage);
  }

}