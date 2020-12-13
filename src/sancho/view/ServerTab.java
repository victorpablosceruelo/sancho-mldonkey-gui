/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

import sancho.view.server.ServerTableView;
import sancho.view.server.ServerViewFrame;
import sancho.view.server.users.ServerUsersTableView;
import sancho.view.server.users.ServerUsersViewFrame;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.WidgetFactory;

public class ServerTab extends AbstractTab {
  public ServerTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
  }

  protected void createContents(Composite parent) {
    String sashPrefString = "serversSash";
    SashForm sashForm = WidgetFactory.createSashForm(parent, sashPrefString);
    ServerViewFrame s = new ServerViewFrame(sashForm, "tab.servers", "tab.servers.buttonSmall", this);
    ServerUsersViewFrame u = new ServerUsersViewFrame(sashForm, "l.serverUsers", "tab.servers.buttonSmall",
        this);
    ((ServerTableView) s.getGView()).setServerUsersTableView((ServerUsersTableView) u.getGView());

    addViewFrame(s);
    addViewFrame(u);
    WidgetFactory.loadSashForm(sashForm, sashPrefString);
  }
}
