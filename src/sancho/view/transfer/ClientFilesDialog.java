/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.enums.EnumClientType;
import sancho.view.friends.clientDirectories.ClientDirectoriesTableView;
import sancho.view.friends.clientDirectories.ClientDirectoriesViewFrame;
import sancho.view.friends.clientFiles.ClientFilesTableView;
import sancho.view.friends.clientFiles.ClientFilesViewFrame;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class ClientFilesDialog extends Dialog {

  protected Client client;

  public ClientFilesDialog(Shell parentShell, Client client) {
    super(parentShell);
    this.client = client;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(SResources.getImage("ProgramIcon"));
    newShell.setText(client.getId() + ": " + client.getName());
  }

  protected int getShellStyle() {
    return super.getShellStyle() | SWT.RESIZE;
  };

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new FillLayout());

    String sashPrefString = "clientFilesSash";
    SashForm sashForm = WidgetFactory.createSashForm(composite, sashPrefString);

    ClientDirectoriesViewFrame cd = new ClientDirectoriesViewFrame(sashForm, "l.clientDirectories",
        "tab.friends.buttonSmall", null);

    ClientFilesViewFrame cf = new ClientFilesViewFrame(sashForm, "l.clientFiles",
        "tab.friends.buttonSmall", null);

    ((ClientDirectoriesTableView) cd.getGView()).setFilesView((ClientFilesTableView) cf.getGView());
    ((ClientDirectoriesTableView) cd.getGView()).setInput(client);
    WidgetFactory.loadSashForm(sashForm, sashPrefString);

    return composite;
  }

  protected Control createButtonBar(Composite parent) {
    Composite buttonComposite = new Composite(parent, SWT.NONE);
    buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    buttonComposite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 0, false));

    final Button addFriendButton = new Button(buttonComposite, SWT.NONE);
    addFriendButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
    addFriendButton.setText(SResources.getString("dd.c.addFriend"));

    if (client.getEnumClientType() == EnumClientType.FRIEND)
      addFriendButton.setEnabled(false);

    addFriendButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        client.addAsFriend();
        addFriendButton.setText(SResources.getString("b.ok"));
        addFriendButton.setEnabled(false);
      }
    });

    Button closeButton = new Button(buttonComposite, SWT.NONE);
    closeButton.setFocus();
    closeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    closeButton.setText(SResources.getString("b.close"));
    closeButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        close();
      }
    });

    return buttonComposite;
  }

}
