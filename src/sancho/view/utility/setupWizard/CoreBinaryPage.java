/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.setupWizard;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class CoreBinaryPage extends WizardPage {

  Text text;

  public CoreBinaryPage() {
    super("hostPage");
    setTitle(SResources.getString("hm.coreSettings"));
    setMessage(SResources.getString("hm.info"));
  }

  public void createControl(Composite parent) {

    Composite mainComposite = new Composite(parent, SWT.NONE);
    mainComposite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));
    mainComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Label x = new Label(mainComposite, SWT.NONE);
    x.setText(SResources.getString("p.coreExecutableInfo"));
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    x.setLayoutData(gd);

    Composite subComposite = new Composite(mainComposite, SWT.NONE);
    subComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 5, 5, false));
    subComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    text = new Text(subComposite, SWT.SINGLE | SWT.BORDER);
    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    Button b = new Button(subComposite, SWT.NULL);
    b.setText(SResources.getString("b.browse"));
    b.setLayoutData(new GridData());
    b.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.NULL);
        String result;
        if ((result = fileDialog.open()) != null) {
          text.setText(result);
        }
      }
    });

    setControl(mainComposite);

  }

  public void saveData() {

    if (text != null && !text.isDisposed() && !text.getText().equals(SResources.S_ES)) {
      PreferenceStore p = PreferenceLoader.getPreferenceStore();
      p.setValue("coreExecutable", text.getText());
      PreferenceLoader.saveStore();
    }
  }

}