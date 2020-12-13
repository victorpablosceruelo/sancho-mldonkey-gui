/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import sancho.utility.VersionInfo;
import sancho.view.utility.SResources;

public class DownloadCompleteShell extends Dialog {

  public DownloadCompleteShell(Shell parentShell) {
    super(parentShell);
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(SResources.getImage("ProgramIcon"));
    newShell.setText(SResources.getString("l.downloadCompleteTitle"));
  }

  public int getShellStyle() {
    return super.getShellStyle() | SWT.RESIZE;
  }

  protected void createButtonsForButtonBar(Composite parent) {

    ((GridLayout) parent.getLayout()).numColumns++;
    final Button b = new Button(parent, SWT.NONE);
    b.setText(SResources.getString("b.deleteLogFile"));
    b.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        File f = new File(VersionInfo.getDownloadLogFile());
        f.delete();
        b.setEnabled(false);
      }
    });

    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  protected Control createDialogArea(Composite parent) {

    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new FillLayout());

    new DownloadCompleteViewFrame(composite, "l.downloadCompleteTitle", "tab.transfers.buttonSmall", null);

    return composite;
  }

  protected void buttonPressed(int buttonId) {
    super.buttonPressed(buttonId);
  }

}