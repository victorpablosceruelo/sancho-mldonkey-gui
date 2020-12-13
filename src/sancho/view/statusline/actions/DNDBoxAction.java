/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline.actions;

import org.eclipse.jface.action.Action;

import sancho.view.MainWindow;
import sancho.view.utility.SResources;

public class DNDBoxAction extends Action {

  MainWindow mainWindow;

  public DNDBoxAction(MainWindow mainWindow) {
    super(SResources.getString("l.toggleDNDBox"));
    setImageDescriptor(SResources.getImageDescriptor("rotate"));
    this.mainWindow = mainWindow;
  }

  public void run() {
    mainWindow.toggleDNDBox();
  }

}
