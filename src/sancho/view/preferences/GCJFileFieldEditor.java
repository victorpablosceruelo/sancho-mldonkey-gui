/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import sancho.view.utility.SResources;

public class GCJFileFieldEditor extends FileFieldEditor {
  private String[] extensions = null;
  private boolean alwaysValid = false;

  public GCJFileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent) {
    super(name, labelText, enforceAbsolute, parent);
  }

  public GCJFileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent,
      boolean alwaysValid) {
    this(name, labelText, enforceAbsolute, parent);
    this.alwaysValid = alwaysValid;
  }

  protected String changePressed() {
    String input = getTextControl().getText();

    if (input.equals(SResources.S_ES) && SWT.getPlatform().equals("win32"))
      input = ".";

    File f = new File(input);

    if (!f.exists())
      f = null;

    File d = getFile(f);

    if (d == null)
      return null;

    return d.getAbsolutePath();
  }

  private File getFile(File startingDirectory) {
    FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);

    if (startingDirectory != null)
      dialog.setFileName(startingDirectory.getPath());

    if (extensions != null)
      dialog.setFilterExtensions(extensions);

    String file = dialog.open();

    if (file != null) {
      file = file.trim();

      if (file.length() > 0)
        return new File(file);
    }

    return null;
  }

  public void setFileExtensions(String[] extensions) {
    this.extensions = extensions;
  }

  public boolean isValid() {
    if (alwaysValid)
      return true;
    else
      return super.isValid();
  }

  protected boolean checkState() {
    if (alwaysValid)
      return true;
    else
      return super.checkState();
  }
}
