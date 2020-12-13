/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline.actions;

import org.eclipse.jface.action.Action;

import sancho.view.MainWindow;
import sancho.view.utility.SResources;

public class PreferencesAction extends Action {

  MainWindow mainWindow;

  public PreferencesAction(MainWindow mainWindow) {
    super(SResources.getString("menu.tools.preferences"));
    setImageDescriptor(SResources.getImageDescriptor("preferences"));
    this.mainWindow = mainWindow;
  }

  public void run() {
    mainWindow.openPreferences();
  }
}