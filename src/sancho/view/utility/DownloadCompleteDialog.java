/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.model.mldonkey.File;
import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;

public class DownloadCompleteDialog extends Dialog {

  MainWindow mainWindow;
  Text textInfo;

  public DownloadCompleteDialog(Shell shell, MainWindow mainWindow) {
    super(shell);
    this.mainWindow = mainWindow;
    setBlockOnOpen(false);
  }

  public int getShellStyle() {
    return super.getShellStyle() | SWT.RESIZE | SWT.ON_TOP;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(SResources.getString("l.downloadComplete"));
    newShell.setImage(SResources.getImage("ProgramIcon"));
  }

  protected void constrainShellSize() {
    super.constrainShellSize();
    getShell().setBounds(PreferenceLoader.loadRectangle("downloadCompleteWindowBounds"));
  }

  public void addFile(File file) {
    textInfo.append("[" + file.getId() + "] " + file.getName() + " - " + file.getED2K()
        + textInfo.getLineDelimiter());

    getShell().setText(SResources.getString("l.downloadComplete") + " (" + (textInfo.getLineCount() - 1) + ")");
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);

    textInfo = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    textInfo.setLayoutData(new GridData(GridData.FILL_BOTH));

    return composite;
  }

  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  public boolean close() {
    mainWindow.closeDownloadCompleteDialog();
    PreferenceConverter.setValue(PreferenceLoader.getPreferenceStore(), "downloadCompleteWindowBounds",
        getShell().getBounds());
    PreferenceLoader.saveStore();
    return super.close();
  }
}