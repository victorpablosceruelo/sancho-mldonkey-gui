/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class AddFriendDialog extends Dialog {
  private ICore core;
  private Text host;
  private Text port;

  public AddFriendDialog(Shell parentShell, ICore core) {
    super(parentShell);
    this.core = core;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(SResources.getImage("ProgramIcon"));
    newShell.setText(SResources.getString("t.f.addByIP"));
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 10, 5, false));

    Label hostLabel = new Label(composite, SWT.NONE);
    hostLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    hostLabel.setText(SResources.getString("t.f.host"));

    host = new Text(composite, SWT.BORDER);
    host.setText("192.168.1.1");
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 120;
    host.setLayoutData(gridData);

    Label portLabel = new Label(composite, SWT.NONE);
    portLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    portLabel.setText(SResources.getString("t.f.port"));

    port = new Text(composite, SWT.BORDER);
    port.setText("4662");
    port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    return composite;
  }

  protected Control createButtonBar(Composite parent) {
    Composite buttonComposite = new Composite(parent, SWT.NONE);
    buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    buttonComposite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 0, false));

    Button okButton = new Button(buttonComposite, SWT.NONE);
    okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    okButton.setText(SResources.getString("b.ok"));
    okButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        String string = "afr " + host.getText() + " " + port.getText();
        Sancho.send(OpCodes.S_CONSOLE_MESSAGE, string);
        close();
      }
    });

    Button cancelButton = new Button(buttonComposite, SWT.NONE);
    cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cancelButton.setText(SResources.getString("b.cancel"));
    cancelButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        close();
      }
    });

    return buttonComposite;
  }
}