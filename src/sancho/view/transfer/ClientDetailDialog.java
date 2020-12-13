/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import sancho.core.ICore;
import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.enums.EnumClientType;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class ClientDetailDialog extends AbstractDetailDialog {
  private ICore core;
  private File file;
  private Client client;
  private CLabel clName;
  private CLabel clRating;
  private CLabel clActivity;
  private CLabel clKind;
  private CLabel clNetwork;
  private CLabel clSockAddr;
  private CLabel clUploaded;
  private CLabel clDownloaded;
  private CLabel clSoftware;
  private CLabel clHash;
  private CLabel clPort;
  private CLabel clHasFiles;

  public ClientDetailDialog(Shell parentShell, File file, Client client, ICore core) {
    super(parentShell);
    this.file = file;
    this.client = client;
    this.core = core;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(SResources.getString("l.client") + " " + client.getId() + " "
        + SResources.getString("l.details").toLowerCase());
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 0, 5, false));

    createGeneralGroup(composite);

    if (file != null) {
      createChunkGroup(composite, "dd.c.localChunks", null);
      createChunkGroup(composite, "dd.c.clientChunks", client);
    }

    updateLabels();

    if (file != null)
      file.addObserver(this);

    client.addObserver(this);

    return composite;
  }

  public void createGeneralGroup(Composite parent) {
    Group clientGeneral = new Group(parent, SWT.SHADOW_ETCHED_OUT);
    clientGeneral.setText(SResources.getString("dd.c.clientInformation"));
    clientGeneral.setLayout(WidgetFactory.createGridLayout(4, 5, 2, 0, 0, false));
    clientGeneral.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    clName = createLine(clientGeneral, "dd.c.name", true);
    clNetwork = createLine(clientGeneral, "dd.c.network", false);
    clRating = createLine(clientGeneral, "dd.c.rating", false);
    clActivity = createLine(clientGeneral, "dd.c.activity", false);
    clKind = createLine(clientGeneral, "dd.c.kind", false);
    clSoftware = createLine(clientGeneral, "dd.c.software", false);
    clHash = createLine(clientGeneral, "dd.c.hash", false);
    clSockAddr = createLine(clientGeneral, "dd.c.address", false);
    clPort = createLine(clientGeneral, "dd.c.port", false);
    clUploaded = createLine(clientGeneral, "dd.c.uploaded", false);
    clDownloaded = createLine(clientGeneral, "dd.c.downloaded", false);
    clHasFiles = createLine(clientGeneral, "dd.c.hasFiles", false);
  }

  protected void createChunkGroup(Composite parent, String resString, Client client) {
    ChunkCanvas chunkCanvas = super.createChunkGroup(parent, SResources.getString(resString), client, file,
        null);
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

    return parent;
  }

  public void updateLabels() {
    updateLabel(clName, client.getName());
    updateLabel(clRating, String.valueOf(client.getRating()));
    updateLabel(clActivity, client.getClientActivity());
    updateLabel(clKind, client.getModeString());
    updateLabel(clNetwork, client.getEnumNetwork().getName());
    updateLabel(clSockAddr, client.getAddr().toString());
    updateLabel(clPort, String.valueOf(client.getPort()));
    updateLabel(clHash, client.getHash());
    updateLabel(clSoftware, client.getSoftware());
    updateLabel(clUploaded, client.getUploadedString());
    updateLabel(clDownloaded, client.getDownloadedString());
    updateLabel(clHasFiles, String.valueOf(client.hasFiles()));
  }

  public boolean close() {
    client.deleteObserver(this);
    if (file != null)
      file.deleteObserver(this);

    return super.close();
  }
}