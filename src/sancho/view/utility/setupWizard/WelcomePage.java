/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.setupWizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class WelcomePage extends WizardPage {
  public WelcomePage() {
    super("welcomePage");
    setTitle(SResources.getString("hm.welcome"));
    setMessage(SResources.getString("hm.configureCore"));
  }

  public void createControl(Composite parent) {
    Composite mainComposite = new Composite(parent, SWT.NONE);
    mainComposite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));
    Label x = new Label(mainComposite, SWT.NONE);
    Image i = SResources.getImage("welcome");
    x.setImage(i);
    x.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER));

    CLabel l = new CLabel(mainComposite, SWT.WRAP);
    GridData lGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    lGridData.widthHint = i.getBounds().width;
    l.setLayoutData(lGridData);
    l.setText(SResources.getString("hm.prefFile") + " " + PreferenceLoader.getPrefFile());

    setControl(mainComposite);
  }
}
