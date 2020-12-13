/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

import sancho.view.utility.SResources;

public class GCJDirectoryFieldEditor extends DirectoryFieldEditor {
  public GCJDirectoryFieldEditor(String name, String labelText, Composite parent) {
    super(name, labelText, parent);
  }

  protected String changePressed() {
    String input = getTextControl().getText();

    if (input.equals(SResources.S_ES) && SWT.getPlatform().equals("win32"))
      input = ".";

    File f = new File(input);

    if (!f.exists())
      f = null;

    File d = getDirectory(f);

    if (d == null)
      return null;

    return d.getAbsolutePath();
  }

  private File getDirectory(File startingDirectory) {

    DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN);
    if (startingDirectory != null)
      fileDialog.setFilterPath(startingDirectory.getPath());
    String dir = fileDialog.open();
    if (dir != null) {
      dir = dir.trim();
      if (dir.length() > 0)
        return new File(dir);
    }

    return null;
  }

}
