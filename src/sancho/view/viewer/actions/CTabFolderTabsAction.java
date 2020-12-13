/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;

public class CTabFolderTabsAction extends Action {

  String prefString;
  CTabFolder cTabFolder;

  public CTabFolderTabsAction(CTabFolder cTabFolder, String prefString) {
    super(SResources.getString("mi.tabsOnTop"), Action.AS_CHECK_BOX);
    this.cTabFolder = cTabFolder;
    this.prefString = prefString;
  }

  public boolean isChecked() {
    return PreferenceLoader.loadBoolean(prefString + "TabsOnTop");
  }

  public void run() {
    PreferenceLoader.getPreferenceStore().setValue(prefString + "TabsOnTop", !isChecked());
    cTabFolder.setTabPosition((cTabFolder.getStyle() & SWT.BOTTOM) != 0 ? SWT.TOP : SWT.BOTTOM);
  }
}